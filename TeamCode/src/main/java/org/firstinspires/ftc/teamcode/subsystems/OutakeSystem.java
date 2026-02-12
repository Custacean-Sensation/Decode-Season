package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

public class OutakeSystem {
    private final DcMotorEx launcher;
    private final CRServo leftFeeder;
    private final CRServo rightFeeder;

    private enum LaunchState {
        IDLE,
        SPIN_UP,
        LAUNCH,
        LAUNCHING,
    }

    private LaunchState launchState = LaunchState.IDLE;

    private final ElapsedTime feederTimer = new ElapsedTime();

    private static final double FEED_TIME_SECONDS = 0.25;
    private static final double STOP_SPEED = 0.0;
    private static final double FULL_SPEED = 1.0;

    // Keep your constants
    private static final double LAUNCHER_TARGET_VELOCITY = 1125 * 1.5;
    private static final double LAUNCHER_MIN_VELOCITY    = 1075 * 1.5;

    // --- Shot queue ---
    private int pendingShots = 0;
    private boolean launching = false;

    // --- Manual feed pulse mode (prevents fighting the state machine) ---
    private boolean manualFeeding = false;

    public OutakeSystem(HardwareMap hardwareMap,
                        String launcherName,
                        String leftFeederName,
                        String rightFeederName) {

        launcher = hardwareMap.get(DcMotorEx.class, launcherName);
        leftFeeder = hardwareMap.get(CRServo.class, leftFeederName);
        rightFeeder = hardwareMap.get(CRServo.class, rightFeederName);

        launcher.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        launcher.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER,
                new PIDFCoefficients(300, 0, 0, 10));

        // SAFETY: hard stop feeders immediately
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);

        leftFeeder.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFeeder.setDirection(DcMotorSimple.Direction.REVERSE);

        // SAFETY: ensure we start fully reset (prevents startup feeding)
        reset();
    }

    // -------------------------
    // Safety reset (call in OpMode init too)
    // -------------------------
    public void reset() {
        pendingShots = 0;
        launching = false;
        manualFeeding = false;
        launchState = LaunchState.IDLE;

        stopFeeders();

        try { launcher.setVelocity(0); } catch (Exception ignored) {}
        launcher.setPower(0);

        feederTimer.reset();
    }

    // -------------------------
    // Helpful velocity getters
    // -------------------------
    public double getLauncherTargetVelocity() {
        return LAUNCHER_TARGET_VELOCITY;
    }

    public double getLauncherMinVelocity() {
        return LAUNCHER_MIN_VELOCITY;
    }

    // Legacy name kept for compatibility
    public double launchVelocity() {
        return LAUNCHER_MIN_VELOCITY;
    }

    public void setLauncherVelocity(double velocity) {
        launcher.setVelocity(velocity);
    }

    public double getLauncherVelocity() {
        return launcher.getVelocity();
    }

    // -------------------------
    // Shot requests (queued)
    // -------------------------
    public void requestShot() {
        requestShots(1);
    }

    public void requestShots(int count) {
        if (count <= 0) return;

        pendingShots += count;
        launching = true;

        // Only enter the shot state machine when we actually have shots queued
        if (launchState == LaunchState.IDLE) {
            launchState = LaunchState.SPIN_UP;
        }
    }

    /**
     * Spin up launcher ONLY. This should never arm feeding by itself.
     * (Feeding happens ONLY when pendingShots > 0 via requestShots)
     */
    public void spinUpLauncher() {
        try {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } catch (Exception ignored) {
            launcher.setPower(FULL_SPEED);
        }

        // CRITICAL FIX:
        // Do NOT change launchState here. Otherwise you "arm" the feeder logic at startup.
        // We only enter SPIN_UP through requestShots().
    }

    public void manualFeedPulse() {
        manualFeeding = true;
        feederTimer.reset();
        leftFeeder.setPower(FULL_SPEED);
        rightFeeder.setPower(FULL_SPEED);
    }

    public void reverseFeedPulse() {
        manualFeeding = true;
        feederTimer.reset();
        leftFeeder.setPower(-FULL_SPEED);
        rightFeeder.setPower(-FULL_SPEED);
    }

    public void stopFeeders() {
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);
    }

    public void stopLauncher() {
        // Hard stop everything + clear queue + clear flags
        pendingShots = 0;
        launching = false;
        manualFeeding = false;

        try { launcher.setVelocity(0); } catch (Exception ignored) {}
        launcher.setPower(0);

        stopFeeders();

        feederTimer.reset();
        launchState = LaunchState.IDLE;
    }

    public boolean isLaunching() {
        return launching;
    }

    public void update() {
        // Manual feed pulses: run them and do NOT let the state machine fight them
        if (manualFeeding) {
            if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                stopFeeders();
                manualFeeding = false;
            }
            return;
        }

        switch (launchState) {
            case IDLE:
                // Extra safety: make sure feeders stay off in IDLE
                stopFeeders();

                // If somehow shots are queued while IDLE, enter SPIN_UP
                if (pendingShots > 0) {
                    launching = true;
                    launchState = LaunchState.SPIN_UP;
                }
                break;

            case SPIN_UP:
                // Maintain target speed
                try {
                    launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                } catch (Exception ignored) {
                    launcher.setPower(FULL_SPEED);
                }

                // Only advance to feeding if we actually have shots queued
                if (pendingShots > 0 && launcher.getVelocity() >= LAUNCHER_MIN_VELOCITY) {
                    launchState = LaunchState.LAUNCH;
                }
                break;

            case LAUNCH:
                // Feed exactly one shot
                leftFeeder.setPower(FULL_SPEED);
                rightFeeder.setPower(FULL_SPEED);
                feederTimer.reset();
                launchState = LaunchState.LAUNCHING;
                break;

            case LAUNCHING:
                if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                    stopFeeders();

                    // Consume exactly one queued shot
                    pendingShots = Math.max(0, pendingShots - 1);

                    if (pendingShots > 0) {
                        // Recover speed between shots
                        launchState = LaunchState.SPIN_UP;
                    } else {
                        launching = false;
                        launchState = LaunchState.IDLE;
                    }
                }
                break;
        }
    }

    /**
     * Periodic update method that should be called every loop iteration.
     * Delegates to update() to avoid duplicate state machines.
     */
    public void periodic() {
        update();
    }
}

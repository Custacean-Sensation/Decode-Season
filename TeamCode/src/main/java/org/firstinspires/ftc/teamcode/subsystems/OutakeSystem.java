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

    private static final double FEED_TIME_SECONDS = 0.20;
    private static final double STOP_SPEED = 0.0;
    private static final double FULL_SPEED = 1.0;

    // Keep your constants
    private static final double LAUNCHER_TARGET_VELOCITY = 1125 * 1.5;
    private static final double LAUNCHER_MIN_VELOCITY    = 1075 * 1.45;

    // --- New: shot queue ---
    private int pendingShots = 0;

    // "launching" now means: we are currently servicing one or more requested shots
    private boolean launching = false;

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

        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);
        leftFeeder.setDirection(DcMotorSimple.Direction.FORWARD);
        rightFeeder.setDirection(DcMotorSimple.Direction.REVERSE);
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

    // Keep your existing name for compatibility (this returns MIN like before)
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

        // Make sure we are in the state machine and spinning up
        if (launchState == LaunchState.IDLE) {
            launchState = LaunchState.SPIN_UP;
        }
    }

    /**
     * Spin up launcher but do NOT feed unless shots are requested.
     */
    public void spinUpLauncher() {
        try {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } catch (Exception ignored) {
            launcher.setPower(FULL_SPEED);
        }

        // Stay in SPIN_UP so velocity is maintained; feeding only happens if pendingShots > 0
        if (launchState == LaunchState.IDLE) {
            launchState = LaunchState.SPIN_UP;
        }
    }

    public void manualFeedPulse() {
        feederTimer.reset();
        leftFeeder.setPower(FULL_SPEED);
        rightFeeder.setPower(FULL_SPEED);
    }

    public void reverseFeedPulse() {
        feederTimer.reset();
        leftFeeder.setPower(-FULL_SPEED);
        rightFeeder.setPower(-FULL_SPEED);
    }

    public void stopLauncher() {
        // Hard stop everything + clear queue + clear launching flag
        pendingShots = 0;
        launching = false;

        try { launcher.setVelocity(0); } catch (Exception ignored) {}
        launcher.setPower(0);

        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);

        feederTimer.reset();
        launchState = LaunchState.IDLE;
    }

    public boolean isLaunching() {
        return launching;
    }

    public void update() {
        switch (launchState) {
            case IDLE:
                // nothing
                break;

            case SPIN_UP:
                // Maintain target speed
                try {
                    launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                } catch (Exception ignored) {
                    launcher.setPower(FULL_SPEED);
                }

                // Only advance to feeding if we actually have shots queued
                if (pendingShots > 0 && launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
                    launchState = LaunchState.LAUNCH;
                }
                break;

            case LAUNCH:
                leftFeeder.setPower(FULL_SPEED);
                rightFeeder.setPower(FULL_SPEED);
                feederTimer.reset();
                launchState = LaunchState.LAUNCHING;
                break;

            case LAUNCHING:
                if (feederTimer.seconds() > FEED_TIME_SECONDS) {
                    leftFeeder.setPower(STOP_SPEED);
                    rightFeeder.setPower(STOP_SPEED);

                    // Consume exactly one queued shot
                    pendingShots = Math.max(0, pendingShots - 1);

                    if (pendingShots > 0) {
                        // Go back to SPIN_UP for the next shot (gives time to recover velocity)
                        launchState = LaunchState.SPIN_UP;
                    } else {
                        launching = false;
                        launchState = LaunchState.IDLE;
                    }
                }
                break;
        }

        // stop manual pulse after FEED_TIME_SECONDS even if not in state machine
        if (launchState == LaunchState.IDLE && feederTimer.seconds() > FEED_TIME_SECONDS) {
            leftFeeder.setPower(STOP_SPEED);
            rightFeeder.setPower(STOP_SPEED);
        }
    }
}

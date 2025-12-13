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
    private static final double LAUNCHER_TARGET_VELOCITY = 1125 * 1.5;
    private static final double LAUNCHER_MIN_VELOCITY = 1075;

    // When true, reaching the target velocity will automatically advance feeders.
    // When false, the launcher will spin up but will not advance balls until a
    // manual requestShot() is issued.
    private boolean autoLaunchEnabled = true;
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

    public void setLauncherVelocity(double velocity) {
        launcher.setVelocity(velocity);
    }

    public double getLauncherVelocity() {
        return launcher.getVelocity();
    }

    public void requestShot() {
        // When explicitly requesting a shot we enable auto-launch so the state
        // machine will progress from SPIN_UP -> LAUNCH automatically once up to speed.
        autoLaunchEnabled = true;
        launching = true;
        if (launchState == LaunchState.IDLE) {
            launchState = LaunchState.SPIN_UP;
        }
    }

    public void manualFeedPulse() {
        feederTimer.reset();
        leftFeeder.setPower(FULL_SPEED);
        rightFeeder.setPower(FULL_SPEED);
    }

    /**
     * Begin spinning the launcher back up to the configured target velocity and enter the
     * SPIN_UP state so the state machine can progress to a shot when ready.
     *
     * By default this method disables auto-launch so the launcher will spin up
     * but will NOT advance the feeders when it reaches speed. Call
     * requestShot() (or otherwise enable autoLaunchEnabled) to allow feeding.
     */
    public void spinUpLauncher() {
        // Spin up but prevent auto-advancing feeders until an explicit shot is requested
        autoLaunchEnabled = false;
        try {
            launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
        } catch (Exception ignored) {
            launcher.setPower(FULL_SPEED);
        }
        launchState = LaunchState.SPIN_UP;
    }

    /**
     * Pulse the feeder servos in reverse for FEED_TIME_SECONDS (same duration as forward feed).
     * This is useful for clearing jams or retrieving a stuck game element.
     */
    public void reverseFeedPulse() {
        feederTimer.reset();
        leftFeeder.setPower(-FULL_SPEED);
        rightFeeder.setPower(-FULL_SPEED);
    }

    /**
     * Immediately stop the launcher motor and feeders, and return the state machine to IDLE.
     * This sets both velocity and power to zero as a defensive measure.
     */
    public void stopLauncher() {
        // Stop launcher motor
        try {
            launcher.setVelocity(0);
        } catch (Exception ignored) {
            // setVelocity should be supported, but fall back to setPower if necessary
        }
        launcher.setPower(0);

        // Stop feeders and reset timers/state
        leftFeeder.setPower(STOP_SPEED);
        rightFeeder.setPower(STOP_SPEED);
        feederTimer.reset();
        launchState = LaunchState.IDLE;
    }

    public boolean isLaunching(){
        return launching;
    }

    public void update() {
        switch (launchState) {
            case IDLE:
                // nothing
                break;
            case SPIN_UP:
                // Maintain spin-up target. Only advance to LAUNCH if autoLaunchEnabled
                // is true (i.e. a shot was explicitly requested).
                launcher.setVelocity(LAUNCHER_TARGET_VELOCITY);
                if (autoLaunchEnabled && launcher.getVelocity() > LAUNCHER_MIN_VELOCITY) {
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
                    launching = false;
                    launchState = LaunchState.IDLE;
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

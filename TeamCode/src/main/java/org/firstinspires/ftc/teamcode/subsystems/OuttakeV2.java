package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants;

public class OuttakeV2 {
    /*declare the hardware components
       1 motorex for fly wheel
       2 cr servos for feeder
       1 intake object to control intake system
     */

    private final DcMotorEx flywheel;
    private final CRServo rFeeder;
    private final CRServo lFeeder;
    private final Intake intake;
    private final DigitalChannel beamBreak;

    private final ElapsedTime feederTimer = new ElapsedTime();

    private static final double FEED_TIME_SECONDS = 0.20;
    private static final double STOP_SPEED = 0.0;
    private static final double FULL_SPEED = 1.0;
    private static final double INTAKE_POWER = 0.2;

    public OuttakeV2 (HardwareMap hm, String flywheelName, String rFeederName, String lFeederName,String intakeName, String beamBreakName) {
        flywheel = hm.get(DcMotorEx.class, flywheelName);
        rFeeder = hm.get(CRServo.class, rFeederName);
        lFeeder = hm.get(CRServo.class, lFeederName);
        beamBreak = hm.get(DigitalChannel.class, beamBreakName);


        intake = new Intake(hm, intakeName);

        //set directions
        rFeeder.setDirection(DcMotorEx.Direction.REVERSE);
        lFeeder.setDirection(DcMotorEx.Direction.FORWARD);

        //set zero power behavior
        flywheel.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        flywheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
        flywheel.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, new PIDFCoefficients(Constants.flyWheel.kP, Constants.flyWheel.kI,
                Constants.flyWheel.kD, Constants.flyWheel.kF));
    }

    /*
    Launch cycle:
    1. Spin up flywheel to target velocity
    2. Start intake to load ring into feeder
    3. Run feeders to launch balll.
    4. Stop intake and feeders
     */

    //we gonna use a states machine here
    //declare the cases
    public enum LaunchState {
        IDLE, //do nothing but keep flywhell at target velocity or 1/2 speed
        SPIN_UP, //spin up flywheel to target velocity || should be able to skip this state once we initially spin up
        LAUNCH, //start intake at low power
        LAUNCHING, //run feeders to launch ball stop once beam go break
    }

    public LaunchState launchState = LaunchState.IDLE;

    /**
     * Request a launch sequence. Advances from IDLE -> SPIN_UP -> LAUNCH -> LAUNCHING.
     * The state machine will proceed automatically once flywheel reaches target velocity.
     */
    public void requestShot() {
        if (launchState == LaunchState.IDLE) {
            launchState = LaunchState.SPIN_UP;
        }
    }

    /**
     * Begin spinning the flywheel to the target velocity without advancing the launch sequence.
     * Useful for prepping the launcher without feeding.
     */
    public void spinUpFlywheel() {
        if (launchState == LaunchState.IDLE) {
            launchState = LaunchState.SPIN_UP;
        }
    }

    /**
     * Stop the launch sequence and return to IDLE state.
     * Stops flywheel, intake, and feeders.
     */
    public void stopLauncher() {
        flywheel.setPower(0);
        rFeeder.setPower(STOP_SPEED);
        lFeeder.setPower(STOP_SPEED);
        intake.stop();
        feederTimer.reset();
        launchState = LaunchState.IDLE;
    }

    /**
     * Manually pulse the feeders forward for FEED_TIME_SECONDS.
     */
    public void manualFeedPulse() {
        feederTimer.reset();
        rFeeder.setPower(FULL_SPEED);
        lFeeder.setPower(FULL_SPEED);
    }

    /**
     * Manually pulse the feeders in reverse for FEED_TIME_SECONDS (clear jams).
     */
    public void reverseFeedPulse() {
        feederTimer.reset();
        rFeeder.setPower(-FULL_SPEED);
        lFeeder.setPower(-FULL_SPEED);
    }

    /**
     * Get the current flywheel velocity.
     */
    public double getFlyWheelVelocity() {
        return flywheel.getVelocity();
    }

    /**
     * Main update method implementing the launch state machine.
     * Should be called every loop iteration to advance state transitions and manage timers.
     */
    public void update() {
        switch (launchState) {
            case IDLE:
                // Keep flywheel at idle speed (half target) for quick spin-up
                flywheel.setVelocity(Constants.flyWheel.TARGET_VELOCITY / 2.0);
                rFeeder.setPower(STOP_SPEED);
                lFeeder.setPower(STOP_SPEED);
                intake.stop();
                break;
            case SPIN_UP:
                // Spin up to target velocity
                flywheel.setVelocity(Constants.flyWheel.TARGET_VELOCITY);
                // Check if we've reached minimum velocity to proceed to LAUNCH
                if (flywheel.getVelocity() > Constants.flyWheel.TARGET_VELOCITY * 0.95) {
                    launchState = LaunchState.LAUNCH;
                }
                break;
            case LAUNCH:
                // Start intake at reduced power to load ring into feeder
                intake.setIntakePower(INTAKE_POWER);
                // Brief delay before starting feeder
                if (feederTimer.seconds() < 0.1) {
                    feederTimer.reset();
                } else {
                    launchState = LaunchState.LAUNCHING;
                }
                break;
            case LAUNCHING:
                // Run feeders to advance ball through launcher
                rFeeder.setPower(FULL_SPEED);
                lFeeder.setPower(FULL_SPEED);
                // Check beam break sensor or use timed feed
                if (!beamBreak.getState() || feederTimer.seconds() > FEED_TIME_SECONDS) {
                    rFeeder.setPower(STOP_SPEED);
                    lFeeder.setPower(STOP_SPEED);
                    intake.stop();
                    launchState = LaunchState.IDLE;
                }
                break;
        }

        // Stop manual pulse after FEED_TIME_SECONDS even if not in state machine
        if (launchState == LaunchState.IDLE && feederTimer.seconds() > FEED_TIME_SECONDS) {
            rFeeder.setPower(STOP_SPEED);
            lFeeder.setPower(STOP_SPEED);
        }
    }


}

package org.firstinspires.ftc.teamcode.subsystems;

import com.bylazar.configurables.annotations.Configurable;
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
    private final ElapsedTime beamBreakTimer = new ElapsedTime();

    private static final double FEED_TIME_SECONDS = 5;
    private static final double STOP_SPEED = 0.0;
    private static final double FULL_SPEED = 1.0;
    private static final double INTAKE_POWER = 0.6;
    private static final double BEAM_BREAK_DEBOUNCE_SECONDS = 0.1;
    private static final double LAUNCHING_TIMEOUT_SECONDS = 0.7;

    private boolean sawBeamBreak = false;

    private boolean intakeRequested = false;
    private double intakeRequestedPower = 0.0;

    private int artifactsLaunched = 0;

    private void resetArtifactsLaunched() {
        artifactsLaunched = 0;
    }

    public int getArtifactsLaunched() {
        return artifactsLaunched;
    }

    public OuttakeV2 (HardwareMap hm, String flywheelName, String rFeederName, String lFeederName,Intake intakeSuragate, String beamBreakName) {
        flywheel = hm.get(DcMotorEx.class, flywheelName);
        rFeeder = hm.get(CRServo.class, rFeederName);
        lFeeder = hm.get(CRServo.class, lFeederName);
        beamBreak = hm.get(DigitalChannel.class, beamBreakName);


        intake = intakeSuragate;

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
        IDLE, //do nothing but keep  flywhell at target velocity or 1/2 speed
        SPIN_UP, //spin up flywheel to target velocity || should be able to skip this state once we initially spin up
        LAUNCH, //start intake at low power
        LAUNCHING, //run feeders to launch ball stop once beam go break
        EMERGENCY,
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
        flywheel.setVelocity(Constants.flyWheel.TARGET_VELOCITY);
        launchState = LaunchState.IDLE;
    }

    /**
     * Stop the launch sequence and return to IDLE state.
     * Stops flywheel and feeders.
     */
    public void stopLauncher() {
        flywheel.setPower(0);
        rFeeder.setPower(STOP_SPEED);
        lFeeder.setPower(STOP_SPEED);
        feederTimer.reset();
        clearIntakeRequest();
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

    public boolean breaked(){
        return beamBreak.getState();
    }

    private boolean isBeamBroken() {
        // If your sensor is active-low, invert this return value.
        return beamBreak.getState();
    }

    /**
     * Check if beam break has been triggered with debounce.
     * Requires beam to be broken continuously for BEAM_BREAK_DEBOUNCE_SECONDS
     * to prevent false triggers from holes in the balls.
     * @return true if beam has been broken for the debounce duration
     */
    private boolean debouncedBeamBreak() {
        boolean broken = isBeamBroken();

        if (broken) {
            beamBreakTimer.reset();
            sawBeamBreak = true;
        }

        return sawBeamBreak && beamBreakTimer.seconds() <= BEAM_BREAK_DEBOUNCE_SECONDS;
    }

    public boolean hasIntakeRequest() {
        return intakeRequested;
    }

    public double getIntakeRequestPower() {
        return intakeRequestedPower;
    }

    private void requestIntake(double power) {
        intakeRequested = true;
        intakeRequestedPower = power;
    }

    private void clearIntakeRequest() {
        intakeRequested = false;
        intakeRequestedPower = 0.0;
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
                clearIntakeRequest();
                break;
            case SPIN_UP:
                // Spin up to target velocity
                flywheel.setVelocity(Constants.flyWheel.TARGET_VELOCITY);
                clearIntakeRequest();
                // Check if we've reached minimum velocity to proceed to LAUNCH
                if (flywheel.getVelocity() > Constants.flyWheel.TARGET_VELOCITY * 0.95) {
                    feederTimer.reset();
                    launchState = LaunchState.LAUNCH;
                }
                break;
            case LAUNCH:
                // Start intake at reduced power to load ring into feeder
                requestIntake(INTAKE_POWER);
                // Brief delay before starting feeder
                if (feederTimer.seconds() > 0.1) {
                    launchState = LaunchState.LAUNCHING;
                    feederTimer.reset();
                    beamBreakTimer.reset();
                    sawBeamBreak = false;
                    break;
                }


            case LAUNCHING:
                // Run feeders to advance ball through launcher
                rFeeder.setPower(FULL_SPEED);
                lFeeder.setPower(FULL_SPEED);
                requestIntake(INTAKE_POWER);
                // Check beam break sensor with debounce to prevent false triggers
                if (debouncedBeamBreak()) {
                    artifactsLaunched++;
                    rFeeder.setPower(STOP_SPEED);
                    lFeeder.setPower(STOP_SPEED);
                    clearIntakeRequest();
                    launchState = LaunchState.IDLE;
                    break;
                }
                // Fallback timeout if the beam break is missed
                if (feederTimer.seconds() > LAUNCHING_TIMEOUT_SECONDS) {
                    rFeeder.setPower(STOP_SPEED);
                    lFeeder.setPower(STOP_SPEED);
                    clearIntakeRequest();
                    launchState = LaunchState.IDLE;
                }
                break;

            case EMERGENCY:
                //ahhhhhhh
                flywheel.setPower(0);
                rFeeder.setPower(STOP_SPEED);
                lFeeder.setPower(STOP_SPEED);
                intake.stop();
        }

        // Stop manual pulse after FEED_TIME_SECONDS even if not in state machine
        if (launchState == LaunchState.IDLE && feederTimer.seconds() > FEED_TIME_SECONDS) {
            rFeeder.setPower(STOP_SPEED);
            lFeeder.setPower(STOP_SPEED);
        }
    }

    /**
     * Autonomous shooting method - blocks until all shots are complete or timeout.
     * This method leverages the existing state machine by repeatedly calling update().
     * @param shots Number of balls to shoot
     * @param timeoutSeconds Maximum time to wait for all shots
     * @return true if all shots were launched, false if timed out
     */
    public boolean autoShoot(int shots, double timeoutSeconds) {
        ElapsedTime timeout = new ElapsedTime();
        int startingArtifacts = artifactsLaunched;
        int targetArtifacts = startingArtifacts + shots;
        ElapsedTime delayBetweenShots = new ElapsedTime();
        boolean waitingForNextShot = false;

        while (artifactsLaunched < targetArtifacts) {
            // Check timeout
            if (timeout.seconds() > timeoutSeconds) {
                stopLauncher();
                return false;
            }

            // Handle intake requests from the state machine
            if (hasIntakeRequest()) {
                intake.setIntakePower(getIntakeRequestPower());
                intake.start();
            } else {
                intake.stop();
            }

            // If we just completed a shot, wait briefly before requesting the next one
            if (waitingForNextShot) {
                if (delayBetweenShots.seconds() > 0.3) {
                    waitingForNextShot = false;
                }
            } else if (launchState == LaunchState.IDLE && artifactsLaunched < targetArtifacts) {
                // Request the next shot
                requestShot();
                delayBetweenShots.reset();
                waitingForNextShot = true;
            }

            // Run the state machine
            update();
        }

        // Return to IDLE (already there, but ensure clean state)
        launchState = LaunchState.IDLE;
        update();
        intake.stop();

        return true;
    }

    /**
     * Convenience overload with default 10 second timeout.
     * @param shots Number of balls to shoot
     * @return true if all shots were launched, false if timed out
     */
    public boolean autoShoot(int shots) {
        return autoShoot(shots, 10.0);
    }



}

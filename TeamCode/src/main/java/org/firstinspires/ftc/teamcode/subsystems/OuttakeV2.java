package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

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

    public OuttakeV2 (HardwareMap hm, String flywheelName, String rFeederName, String lFeederName, String beamBreakName) {
        flywheel = hm.get(DcMotorEx.class, flywheelName);
        rFeeder = hm.get(CRServo.class, rFeederName);
        lFeeder = hm.get(CRServo.class, lFeederName);
        beamBreak = hm.get(DigitalChannel.class, beamBreakName);


        intake = new Intake(hm, "intakeMotor");

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

    public void launchMechanism() {
        switch (launchState) {
            case IDLE:
                flywheel.setVelocity(Constants.flyWheel.TARGET_VELOCITY / 2);
                rFeeder.setPower(0);
                lFeeder.setPower(0);
                intake.stop();
                break;
            case SPIN_UP:
                flywheel.setVelocity(Constants.flyWheel.TARGET_VELOCITY); //target velocity
                break;
            case LAUNCH:
                intake.setIntakePower(0.2);
                break;
            case LAUNCHING:
                rFeeder.setPower(1.0);
                lFeeder.setPower(1.0);
                if(beamBreak.getState() == false) { //beam break is triggered
                    rFeeder.setPower(0);
                    lFeeder.setPower(0);
                    intake.stop();
                    launchState = LaunchState.IDLE; //reset to idle
                }
        }
    }


}

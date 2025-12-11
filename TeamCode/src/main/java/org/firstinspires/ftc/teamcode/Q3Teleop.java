package org.firstinspires.ftc.teamcode;

import java.util.Arrays;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.OutakeSystem;

@TeleOp(name = "Q3 Teleop")
public class Q3Teleop extends OpMode {
    GoBildaPinpointDriver pinpoint;
    ExampleDrivetrain dt;
    Intake intake;

    OutakeSystem outake;

    //limelight stuff
    private LimelightClient limelight;

    //turning control for limelight
    private static final double KP_TURN = 0.03;
    private static final double MAX_TURN = 0.35; //cap turn power
    private static final double TX_DEADBAND = 0.5; //degrees for the good enought window
    @Override
    public void init() {
        //set up drivetrain and motors and servos
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        dt = new ExampleDrivetrain(hardwareMap, "frontLeft", "frontRight", "backLeft", "backRight", "pinpoint");
        // Intake API changed: constructor now only needs the intake motor name
        intake = new Intake(hardwareMap, "intakeMotor");
        outake = new OutakeSystem(hardwareMap, "launcher", "leftFeeder", "rightFeeder");

        //limelight (fixed URL typo)
        limelight = new LimelightClient("http://limelight.local:5807");
        telemetry.addLine("Limelight connection complete");
        telemetry.update();

    }

    @Override
    public void start() {
        pinpoint.resetPosAndIMU();
    }

    @Override
    public void loop() {
        dt.fieldCentricDrive(gamepad1.left_stick_y, gamepad1.left_stick_x, gamepad1.right_stick_x);

        if (gamepad1.left_stick_button && gamepad1.right_stick_button) {
            pinpoint.resetPosAndIMU();   
        }
        //intake control
        if (gamepad1.right_trigger > 0.1) {
            intake.start();
        } else if (gamepad1.left_trigger > 0.1) {
            intake.reverse();
        } else {
            intake.stop();
        }

        //outake control
        if (gamepad1.right_bumper){
            outake.requestShot();
        }
        if (gamepad1.dpad_down){
            outake.stopLauncher();
        }

        if (gamepad1.dpad_up) {
            // Spin up the launcher but do NOT advance feeders automatically.
            outake.spinUpLauncher();
        }
        if (gamepad1.dpad_left){
            outake.reverseFeedPulse();
        }
        if (gamepad1.dpad_right) {
            outake.manualFeedPulse();
        }

        outake.update();

        //allign to tag
        limelight.update();
        boolean hasTarget = limelight.hasTarget();
        double tx = limelight.getTx();
        double ty = limelight.getTy();
        double[] botpose = limelight.getBotpose(); //[x, y, z, roll, pitch, yaw]

        // Telemetry to make use of limelight values and aid debugging
        telemetry.addData("limelight/hasTarget", hasTarget);
        telemetry.addData("limelight/tx", tx);
        telemetry.addData("limelight/ty", ty);
        if (botpose != null) telemetry.addData("limelight/botpose", Arrays.toString(botpose));

        // Advance the outake state machine every loop so spin-up and timed feeds work
        outake.update();
        telemetry.addData("launcherVel", outake.getLauncherVelocity());

        double turnCmd = 0.0;
        boolean aligning = gamepad1.left_bumper; //hold left_bumper to align
        if (aligning && hasTarget) {
            double error = tx; // degrees righ tpositve per limelight
            if(Math.abs(error) > TX_DEADBAND) {
                turnCmd = Range.clip(error * KP_TURN, -MAX_TURN, MAX_TURN);
            }else {
                turnCmd = 0.0;
            }
            dt.mecanumDrive(0.0, 0.0, -turnCmd);
        }
        //end allign to tag

        telemetry.update();

    }


}

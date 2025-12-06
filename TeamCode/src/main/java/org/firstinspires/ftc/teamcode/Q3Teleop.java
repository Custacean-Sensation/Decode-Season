package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;

@TeleOp(name = "Q3 Teleop")
public class Q3Teleop extends OpMode {
    GoBildaPinpointDriver pinpoint;
    ExampleDrivetrain dt;
    Intake intake;

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
        intake = new Intake(hardwareMap, "intakeMotor", "leftFeed", "rightFeed");

        //limelight
        limelight = new LimelightClient("http//limeligh.local:5807");
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
        if (gamepad1.a) {
            intake.start();
        } 
        if (gamepad1.b) {
            intake.reverse();
        } 
        if (gamepad1.x) {
            intake.stop();
        }

        //allign to tag
        limelight.update();
        boolean hasTarget = limelight.hasTarget();
        double tx = limelight.getTx();
        double ty = limelight.getTy();
        double[] botpose = limelight.getBotpose(); //[x, y, z, roll, pitch, yaw]

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


    }



}

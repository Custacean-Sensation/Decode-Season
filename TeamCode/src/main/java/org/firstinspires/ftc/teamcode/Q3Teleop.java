package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
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
    private Limelight3A limelight;



    @Override
    public void init() {
        //set up drivetrain and motors and servos
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        dt = new ExampleDrivetrain(hardwareMap, "frontLeft", "frontRight", "backLeft", "backRight", "pinpoint");
        // Intake API changed: constructor now only needs the intake motor name
        intake = new Intake(hardwareMap, "intake");
        outake = new OutakeSystem(hardwareMap, "launcher", "leftFeeder", "rightFeeder");

        // Initialize and start the Limelight3A sensor if present
        try {
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limelight.pipelineSwitch(0);
            limelight.start();
            telemetry.addLine("Limelight started");
        } catch (Exception e) {
            limelight = null;
            telemetry.addLine("Limelight not found: " + e.getMessage());
        }
        telemetry.update();

    }

    @Override
    public void start() {
        pinpoint.resetPosAndIMU();
    }

    @Override
    public void loop() {
        dt.fieldCentricDrive(gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);

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

        // Align to tag using Limelight3A when left bumper held
        if (limelight != null) {
            LLResult result = limelight.getLatestResult();
            boolean hasTarget = result != null && result.isValid();
            double tx = hasTarget ? result.getTx() : 0.0;
            double ty = hasTarget ? result.getTy() : 0.0;
            Pose3D botpose = (result != null) ? result.getBotpose() : null; // Pose3D [x,y,z,roll,pitch,yaw]

            // Telemetry to make use of limelight values and aid debugging
            telemetry.addData("limelight/hasTarget", hasTarget);
            telemetry.addData("limelight/tx", tx);
            telemetry.addData("limelight/ty", ty);
            if (botpose != null) telemetry.addData("limelight/botpose", botpose.toString());

            double turnCmd = 0.0;
            boolean aligning = gamepad1.left_bumper; //hold left_bumper to align
            if (aligning && hasTarget) {
                double error = tx; // degrees right positive per limelight
                if(Math.abs(error) > RobotConstants.LIMELIGHT_TX_DEADBAND_Q3) {
                    turnCmd = Range.clip(error * RobotConstants.LIMELIGHT_KP_TURN, -RobotConstants.LIMELIGHT_MAX_TURN, RobotConstants.LIMELIGHT_MAX_TURN);
                }else {
                    turnCmd = 0.0;
                }
                // use mecanumDrive to apply rotation command
                dt.mecanumDrive(0.0, 0.0, -turnCmd);
            }
        }

        // Advance the outake state machine every loop so spin-up and timed feeds work
        outake.update();
        telemetry.addData("launcherVel", outake.getLauncherVelocity());

        telemetry.update();

    }


}

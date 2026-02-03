package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.OuttakeV2;

@TeleOp(name = "State Teleop")
public class StateTeleop extends OpMode {
    GoBildaPinpointDriver pinpoint;
    ExampleDrivetrain dt;
    Intake intake;

    OuttakeV2 outtake;


    @Override
    public void init() {
        //set up drivetrain and motors and servos
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        dt = new ExampleDrivetrain(hardwareMap, "frontLeft", "frontRight", "backLeft", "backRight", "pinpoint");
        intake = new Intake(hardwareMap, "intake");
        outtake = new OuttakeV2(hardwareMap, "flywheel", "rightFeeder", "leftFeeder", "beamBreak");

        telemetry.addLine("StateTeleop initialized");
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

        //outtake control
        if (gamepad1.right_bumper){
            outtake.requestShot(); // Request a shot
        }
        if (gamepad1.dpad_down){
            outtake.stopLauncher(); // Emergency stop
        }
        if (gamepad1.dpad_up) {
            // Spin up the launcher but do NOT advance feeders automatically.
            outtake.spinUpFlywheel();
        }
        if (gamepad1.dpad_left){
            outtake.reverseFeedPulse();
        }
        if (gamepad1.dpad_right) {
            outtake.manualFeedPulse();
        }

        // Advance the outtake state machine every loop
        outtake.update();

        telemetry.addData("Flywheel Velocity", outtake.getFlyWheelVelocity());
        telemetry.addData("Launch State", outtake.launchState);
        telemetry.update();
    }
}

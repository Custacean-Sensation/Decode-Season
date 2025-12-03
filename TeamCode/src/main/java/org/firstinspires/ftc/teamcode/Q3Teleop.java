package org.firstinspires.ftc.teamcode;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;

@TeleOp(name = "Q3 Teleop")
public class Q3Teleop extends OpMode {
    GoBildaPinpointDriver pinpoint;
    ExampleDrivetrain dt;
    Intake intake;
    @Override
    public void init() {
        //set up drivetrain and motors and servos
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        dt = new ExampleDrivetrain(hardwareMap, "frontLeft", "frontRight", "backLeft", "backRight", "pinpoint");
        intake = new Intake(hardwareMap, "intakeMotor", "leftFeed", "rightFeed");

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
    }



}

package org.firstinspires.ftc.teamcode.StarterBot;


import static com.qualcomm.robotcore.hardware.DcMotor.ZeroPowerBehavior.BRAKE;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;
import org.firstinspires.ftc.teamcode.subsystems.OutakeSystem;

@TeleOp(name = "StarterBotTeleop")
public class TeleOpHEHE extends OpMode {

    private ExampleDrivetrain dt = null;
    private OutakeSystem outakeSystem = null;

    @Override
    public void init() {
        outakeSystem = new OutakeSystem(hardwareMap, "launcher", "leftFeeder", "rightFeeder");

        dt = new ExampleDrivetrain(hardwareMap, "frontLeft", "frontRight", "backLeft", "backRight");

        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void init_loop() {
    }

    @Override
    public void start() {
    }

    @Override
    public void loop() {
        mecanumDrive(gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x);

        if (gamepad1.y) {
            outakeSystem.setLauncherVelocity(1125 * 1.25);
        } else if (gamepad1.b) {
            outakeSystem.setLauncherVelocity(0.0);
        }

        if (gamepad1.leftBumperWasPressed()) {
            outakeSystem.manualFeedPulse();
        }

        outakeSystem.requestShot();
        if (gamepad1.rightBumperWasPressed()) {
            outakeSystem.requestShot();
        }

        outakeSystem.update();

        telemetry.addData("motorSpeed", outakeSystem.getLauncherVelocity());
    }

    @Override
    public void stop() {
    }

    void mecanumDrive(double forward, double rotate, double yaw) {
        double foreLeftPower = (forward + rotate) + yaw;
        double foreRightPower = (forward - rotate) - yaw;
        double backLeftPower = (forward - rotate) + yaw;
        double backRightPower = (forward + rotate) - yaw;

        dt.setPowers(foreLeftPower, foreRightPower, backLeftPower, backRightPower);
    }
}

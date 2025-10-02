package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
@TeleOp(name = "BasicStrafer")
public class BasicStrafer extends LinearOpMode
{
    private final ExampleDrivetrain dt = new ExampleDrivetrain(hardwareMap, "LeftFront", "RightFront", "LeftBack", "RightBack");
    public void runOpMode()
    {
        //create vars for how I want to move the robot
        float axial;
        float yaw;
        float lateral;
        double maxPower;
        double foreLeftPower;
        double foreRightPower;
        double backLeftPower;
        double backRightPower;

        dt.stop();

        waitForStart();
        
        while(opModeIsActive())
        {
            // Motor control
            axial = -gamepad1.left_stick_x;
            lateral = -gamepad1.left_stick_y;
            yaw = gamepad1.right_stick_x;

            foreLeftPower = (axial - lateral) - yaw;
            foreRightPower = axial + lateral + yaw;
            backLeftPower = (axial + lateral) - yaw;
            backRightPower = (axial - lateral) + yaw;

            dt.setPowers(foreLeftPower, foreRightPower, backLeftPower, backRightPower);

            maxPower = Math.max(Math.abs(foreLeftPower), Math.max(Math.abs(foreRightPower), Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));
            if (maxPower > 1) {
                foreLeftPower /= maxPower;
                foreRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            dt.setPowers(foreLeftPower, foreRightPower, backLeftPower, backRightPower);
        }
    }
}

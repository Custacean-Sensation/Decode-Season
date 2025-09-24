package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "BasicStrafer")
public class BasicStrafer extends LinearOpMode
{
    //Declare all of my DRIVE motors
    public DcMotor foreLeftDrive;
    public DcMotor foreRightDrive;
    public DcMotor backLeftDrive;
    public DcMotor backRightDrive;

    //Declare Power value
    double foreLeftPower;
    double foreRightPower;
    double backLeftPower;
    double backRightPower;
    public void runOpMode()
    {
        //craete vars for how I want to move the robot
        float axial;
        float yaw;
        float lateral;
        double maxPower;

        //give motor assignments
        foreLeftDrive = hardwareMap.get(DcMotor.class, "LeftFront");
        foreRightDrive = hardwareMap.get(DcMotor.class, "RightFront");
        backLeftDrive = hardwareMap.get(DcMotor.class, "LeftBack");
        backRightDrive = hardwareMap.get(DcMotor.class, "RightBack");

        //set motor directions
        foreLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        foreRightDrive.setDirection(DcMotor.Direction.FORWARD);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.FORWARD);
        
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

            maxPower = Math.max(Math.abs(foreLeftPower), Math.max(Math.abs(foreRightPower), Math.max(Math.abs(backLeftPower), Math.abs(backRightPower))));
            if (maxPower > 1) {
                foreLeftPower /= maxPower;
                foreRightPower /= maxPower;
                backLeftPower /= maxPower;
                backRightPower /= maxPower;
            }

            foreLeftDrive.setPower(foreLeftPower);
            foreRightDrive.setPower(foreRightPower);
            backLeftDrive.setPower(backLeftPower);
            backRightDrive.setPower(backRightPower);
        }
    }
}

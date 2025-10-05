package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class ExampleDrivetrain
{
    private final DcMotor foreLeft;
    private final DcMotor foreRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;

    //constructor
    public ExampleDrivetrain(HardwareMap hw, String fl, String fr, String bl, String br)
    {
        foreLeft = hw.get(DcMotor.class, fl);
        foreRight = hw.get(DcMotor.class, fr);
        backLeft = hw.get(DcMotor.class, bl);
        backRight = hw.get(DcMotor.class, br);

        foreLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        foreRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        foreLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        foreRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    //raw power inputs
    public void setPowers(double fl, double fr, double bl, double br)
    {
        foreLeft.setPower(fl <= 1 || fl >= -1 ? 0 : fl);
        foreRight.setPower(fr <= 1 || fr >= -1 ? 0 : fr);
        backLeft.setPower(bl <= 1 || bl >= -1 ? 0 : bl);
        backRight.setPower(br <= 1 || br >= -1 ? 0 : br);
    }

    //STOP
    public void stop()
    {
        foreLeft.setPower(0);
        foreRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);
    }

    //if you want you could implement simple movements below ill just do move forward
    public void forward(float power)
    {
        setPowers(power, power, power, power);
    }
}

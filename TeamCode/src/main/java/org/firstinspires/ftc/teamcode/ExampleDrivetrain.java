package org.firstinspires.ftc.teamcode;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class ExampleDrivetrain
{
    private final DcMotor foreLeft;
    private final DcMotor foreRight;
    private final DcMotor backLeft;
    private final DcMotor backRight;
    private final double DRIVE_SPEED = 0.5;
    private final double ROTATE_SPEED = 0.2;
    private final double WHEEL_DIAMETER_MM = 96;
    private final double ENCODER_TICKS_PER_REV = 537.7;
    private final double TICKS_PER_MM = (ENCODER_TICKS_PER_REV / (WHEEL_DIAMETER_MM * Math.PI));

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

    public void resetEncoders(){
        foreLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        foreRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode((DcMotor.RunMode.STOP_AND_RESET_ENCODER));
    }

    public boolean tankDrive(double speed, double distance, DistanceUnit distanceUnit){

        final double TOLERANCE_MM = 10;

        double targetPosition = (distanceUnit.toMm(distance) * TICKS_PER_MM);

        foreLeft.setTargetPosition((int) targetPosition);
        foreRight.setTargetPosition((int) targetPosition);
        backLeft.setTargetPosition((int) targetPosition);
        backRight.setTargetPosition((int) targetPosition);

        foreLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        foreRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        foreLeft.setPower(speed);
        foreRight.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }

    public boolean tankRotate(double speed, double angle, AngleUnit angleUnit) {
        final double TOLERANCE_MM = 10;

        double targetMm = angleUnit.toRadians(angle);

        double leftTargetPosition = -(targetMm*TICKS_PER_MM);
        double rightTargetPosition = targetMm*TICKS_PER_MM;

        //set left side motors
        foreLeft.setTargetPosition((int) leftTargetPosition);
        backLeft.setTargetPosition((int) leftTargetPosition);

        //set right side motor
        foreRight.setTargetPosition((int) rightTargetPosition);
        backRight.setTargetPosition((int) rightTargetPosition);

        //drive motors
        foreLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        foreRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        foreRight.setPower(speed);
        foreLeft.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(speed);

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return true;
    }
}

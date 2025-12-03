package org.firstinspires.ftc.teamcode.subsystems;

import static java.lang.Thread.sleep;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.GoBildaPinpointDriver;

public class ExampleDrivetrain
{
    private DcMotor foreLeft;
    private DcMotor foreRight;
    private DcMotor backLeft;
    private DcMotor backRight;
    private GoBildaPinpointDriver pinpoint;
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

        foreLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        foreRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        foreLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        foreRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        foreLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        foreRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public ExampleDrivetrain(HardwareMap hw, String fl, String fr, String bl, String br, String pp)
    {
        foreLeft = hw.get(DcMotor.class, fl);
        foreRight = hw.get(DcMotor.class, fr);
        backLeft = hw.get(DcMotor.class, bl);
        backRight = hw.get(DcMotor.class, br);

        foreLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        foreRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        foreLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        foreRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        foreLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        foreRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        //set pinpoint values and data
        pinpoint = hw.get(GoBildaPinpointDriver.class, "pinpoint");

        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );

        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD, //x pod
                GoBildaPinpointDriver.EncoderDirection.FORWARD //y pod
        );

        //offsets
        pinpoint.setOffsets(
                0.0, // x offset
                0.0 //y offset
        );

        pinpoint.resetPosAndIMU();
    }


    //raw power inputs
    public void setPowers(double fl, double fr, double bl, double br)
    {
        foreLeft.setPower(fl);
        foreRight.setPower(fr);
        backLeft.setPower(bl);
        backRight.setPower(br);
    }

    //STOP
    public boolean stop()
    {
        foreLeft.setPower(0);
        foreRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        foreLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        foreRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        return true;

    }

    //if you want you could implement simple movements below ill just do move forward
    public void forward(float power)
    {
        setPowers(power, power, power, power);
    }

    public void resetEncoders(){
        foreLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        foreRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        foreLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        foreRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        setPowers(0,0,0,0);
    }

    public boolean tankDrive(double speed, double distance, DistanceUnit distanceUnit){

        final double TOLERANCE_MM = 10;

        double targetPosition = (distanceUnit.toMm(distance) * TICKS_PER_MM);

        resetEncoders();

        foreLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        foreRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        foreLeft.setTargetPosition((int) targetPosition);
        foreRight.setTargetPosition((int) targetPosition);
        backLeft.setTargetPosition((int) targetPosition);
        backRight.setTargetPosition((int) targetPosition);

        foreRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        foreLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        foreLeft.setPower(speed);
        foreRight.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);

        while(foreLeft.isBusy() || foreRight.isBusy() || backLeft.isBusy() || backRight.isBusy()){}
        return true;
    }

    public boolean tankRotate(double speed, double angle, AngleUnit angleUnit) {
        final double TOLERANCE_MM = 10;

        double targetMm = angleUnit.toRadians(angle);

        double leftTargetPosition = -(targetMm*TICKS_PER_MM);
        double rightTargetPosition = targetMm*TICKS_PER_MM;

        //reset encoders
        resetEncoders();

        //set left side motors
        foreLeft.setTargetPosition((int) leftTargetPosition);
        backLeft.setTargetPosition((int) leftTargetPosition);

        //set right side motor
        foreRight.setTargetPosition((int) rightTargetPosition);
        backRight.setTargetPosition((int) rightTargetPosition);

        foreLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        foreRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        foreRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        foreLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        foreLeft.setPower(speed);
        foreRight.setPower(speed);
        backRight.setPower(speed);
        backLeft.setPower(speed);

        while(foreLeft.isBusy() || foreRight.isBusy() || backLeft.isBusy() || backRight.isBusy()){}

        return true;
    }

    public boolean dtIsBusy(){
        if(foreLeft.isBusy() || foreRight.isBusy() || backRight.isBusy() || backLeft.isBusy()){
            return true;
        }
        else{
            return false;
        }
    }

    int[] thinkgs = new int[4];
    public int postitionsBL() {
        return backLeft.getCurrentPosition();
    }
    public int postitionsBR() {
        return backRight.getCurrentPosition();
    }

    public double getBLPower(){
        return backLeft.getPower();
    }

    public void mecanumDrive(double lateral, double axial, double yaw) {
        yaw = -yaw;
        lateral = -lateral;
        axial = -axial;
        double foreLeftPower = (lateral + axial) + yaw;
        double foreRightPower = (lateral - axial) - yaw;
        double backLeftPower = (lateral - axial) + yaw;
        double backRightPower = (lateral + axial) - yaw;

        setPowers(foreLeftPower, foreRightPower, backLeftPower, backRightPower);
    }

    public void fieldCentricDrive(double y, double x, double turn) {
        double deadzone = 0.03;
        if (Math.abs(x) < deadzone) x = 0;
        if (Math.abs(y) < deadzone) y = 0;
        if (Math.abs(turn) < deadzone) turn = 0;

        pinpoint.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);
        double heading = pinpoint.getHeading(AngleUnit.RADIANS);

        //field centric maths
        double cosH = Math.cos(-heading);
        double sinH = Math.sin(-heading);

        double rotX = x * cosH - y * sinH;
        double rotY = x * sinH + y * cosH;

        //mecanum drive math stuff
        double flPower = rotY - rotX - turn;
        double frPower = rotY + rotX + turn;
        double blPower = rotY + rotX - turn;
        double brPower = rotY - rotX + turn;

        //normalize powers
        double max = Math.max(
                1, Math.max(
                        Math.abs(flPower),
                        Math.max(Math.abs(frPower),
                                Math.max(Math.abs(blPower), Math.abs(brPower)))
                )
        );

        foreLeft.setPower(flPower / max);
        foreRight.setPower(frPower / max);
        backLeft.setPower(blPower / max);
        backRight.setPower(brPower / max);
    }
}
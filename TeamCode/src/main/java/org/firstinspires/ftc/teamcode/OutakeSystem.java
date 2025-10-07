package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

public class OutakeSystem {
    private DcMotor outakeMotorL;
    private DcMotor outakeMotorR;
    private boolean outakeOn = false;
    private boolean servoOn = false;
    private Servo outakeServo;
    

    // Constructor
    public OutakeSystem(HardwareMap hardwareMap, String outakeMotorName, String beltMotorName) {
        this.outakeMotorL = hardwareMap.get(DcMotor.class, outakeMotorName);
        this.outakeMotorR = hardwareMap.get(DcMotor.class, beltMotorName);

        // Set motor directions (assuming forward for both)
        this.outakeMotorL.setDirection(DcMotor.Direction.FORWARD);
        this.outakeMotorR.setDirection(DcMotor.Direction.FORWARD);

        // Set motor modes
        this.outakeMotorL.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.outakeMotorR.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void startOutake() {
        outakeMotorL.setPower(0.5);
        outakeMotorR.setPower(-0.5);
        outakeOn = true;
    }

    public void stopOutake() {
        outakeMotorL.setPower(0);
        outakeMotorR.setPower(0);
        outakeOn = false;
    }

    public void startServo() {
        outakeServo.setPosition(1.0);
        servoOn = true;
    }

    public void ReverseServo() {
        outakeServo.setPosition(0.0);
        servoOn = false;
    }

    public boolean isOutakeOn() {
        return outakeOn;
    }

    public boolean isServoOn() {
        return servoOn;
    }

    public void setOutakePower(double power) {
        outakeMotorL.setPower(power);
        outakeOn = power != 0;
    }

    public void setOutakeRPower(double power) {
        outakeMotorR.setPower(power);
        outakeOn = power != 0;
    }
}
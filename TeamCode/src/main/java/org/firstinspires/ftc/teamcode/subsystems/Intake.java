package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private DcMotor intakeMotor;
    private CRServo leftFeed;
    private CRServo rightFreed;

    private double intakePower = 1.0; 
    private double feedPower = 1.0;   

    // Constructor
    public Intake(HardwareMap hardwareMap, String intakeMotorName, String leftFeedName, String rightFeedName) {
        intakeMotor = hardwareMap.get(DcMotor.class, intakeMotorName); 
        leftFeed = hardwareMap.get(CRServo.class, leftFeedName);
        righFeed = hardwareMap.get(CRServo.class, rightFeedName) 

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        leftFeed.setDirection(CRServo.Direction.FORWARD);
        rightFeed.setDirection(CRServo.Direction.FORWARD);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftFeed.setZeroPowerBehavior(CRServo.ZeroPowerBehavior.BRAKE);
        rightFeed.setZeroPowerBehavior(CRServo.ZeroPowerBehavior.BRAKE);
    }

    // Start both intake and belt
    public void start() {
        intakeMotor.setPower(intakePower);
        leftFeed.setPower(feedPower);
        righFeed.setPower(feedPower);
    }

    // Spit out artifact(if possible)
    public void reverse() {
        intakeMotor.setPower(-intakePower);
        leftFeed.setPower(-feedPower)
        righFeed.setPower(-feedPower);
    }

    // Stop both intake and belt
    public void stop() {
        intakeMotor.setPower(0);
        leftFeed.setPower(0);
        righFeed.setPower(0);
    }
}

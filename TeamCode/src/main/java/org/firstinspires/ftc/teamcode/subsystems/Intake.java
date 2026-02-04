package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class  Intake {

    private DcMotor intakeMotor;

    private double intakePower = 1.0;

    // Constructor
    public Intake(HardwareMap hardwareMap, String intakeMotorName) {
        intakeMotor = hardwareMap.get(DcMotor.class, intakeMotorName);

        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // Start both intake and belt
    public void start() {
        intakeMotor.setPower(intakePower);
    }

    // Spit out artifact(if possible)
    public void reverse() {
        intakeMotor.setPower(-intakePower);
    }

    // Stop both intake and belt
    public void stop() {
        intakeMotor.setPower(0);
    }

    public void setIntakePower(double power) {
        intakePower = power;
    }
}

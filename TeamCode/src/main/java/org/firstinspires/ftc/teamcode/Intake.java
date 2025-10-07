package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {

    private DcMotor intakeMotor;
    private DcMotor beltMotor;

    private double intakePower = 1.0; 
    private double beltPower = 1.0;   

    // Constructor
    public Intake(HardwareMap hardwareMap, String intakeMotorName, String beltMotorName) {
        intakeMotor = hardwareMap.get(DcMotor.class, intakeMotorName); 
        beltMotor = hardwareMap.get(DcMotor.class, beltMotorName); 

        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
        beltMotor.setDirection(DcMotor.Direction.FORWARD);

        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        beltMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // Start both intake and belt
    public void start() {
        intakeMotor.setPower(intakePower);
        beltMotor.setPower(beltPower);
    }

    // Stop both intake and belt
    public void stop() {
        intakeMotor.setPower(0);
        beltMotor.setPower(0);
    }
}
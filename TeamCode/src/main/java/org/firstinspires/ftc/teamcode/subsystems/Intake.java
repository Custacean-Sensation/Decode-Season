package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.teamcode.RobotConstants;

public class  Intake {

    private DcMotor intakeMotor;

    // Constructor
    public Intake(HardwareMap hardwareMap, String intakeMotorName) {
        intakeMotor = hardwareMap.get(DcMotor.class, intakeMotorName);

        intakeMotor.setDirection(DcMotor.Direction.REVERSE);
        intakeMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // Start both intake and belt
    public void start() {
        intakeMotor.setPower(RobotConstants.INTAKE_POWER);
    }

    // Spit out artifact(if possible)
    public void reverse() {
        intakeMotor.setPower(-RobotConstants.INTAKE_POWER);
    }

    // Stop both intake and belt
    public void stop() {
        intakeMotor.setPower(0);
    }
}

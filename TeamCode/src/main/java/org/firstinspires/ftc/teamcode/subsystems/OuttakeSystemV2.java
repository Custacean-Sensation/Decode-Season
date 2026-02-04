package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class OuttakeSystemV2 {
    //two crservos for feedres and one motor for launcher
    //launcher needs velocity control
    private DcMotorEx launcher;
    private CRServo leftFeeder;
    private CRServo rightFeeder;

    public OuttakeSystemV2(String launcherName, String leftFeederName, String rightFeederName, HardwareMap hardwareMap) {
        launcher = hardwareMap.get(DcMotorEx.class, launcherName);
        leftFeeder = hardwareMap.get(CRServo.class, leftFeederName);
        rightFeeder = hardwareMap.get(CRServo.class, rightFeederName);
    }



}

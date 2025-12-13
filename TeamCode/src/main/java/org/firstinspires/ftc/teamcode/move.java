package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;

@Autonomous(name="move")
public class move extends OpMode {
    private ExampleDrivetrain dt;

    @Override
    public void init(){
        dt = new ExampleDrivetrain(hardwareMap, "frontLeft", "frontRight", "backLeft", "backRight");
    }
    @Override
    public void start(){}

    @Override
    public void loop() {
        if(dt.tankDrive(1, 10, DistanceUnit.INCH)){
            dt.stop();
        }
    }
}

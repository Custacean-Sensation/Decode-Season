package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;

/**
 * AlignToTag
 * Rotates in place to drive Limelight's tx -> 0 (centered on the AprilTag).
 * Assumes 4 mecanum/tank motors named: "lf","rf","lb","rb".
 * You control when to align with gamepad1.a (hold to align).
 * If Limelight has no target, motors stop (no blind spinning).
 */
public class Limelight{
    private ExampleDrivetrain Drivetrain;
    private Limelight3A limelight;
    private LLResult result;

    private boolean hasTarget;
    double tx;
    double ty;
    double ta;
    Pose3D botpose;
    double turnCommand;
    double forwardCommand;

    public limelight(ExampleDrivetrain dt, HardwareMap hardwareMap, String limelightName){
        this.Drivetrain = dt;
        limelight = hardwareMap.get(DcMotor.class, limelightName);
        limelight.pipelineSwitch(0);
        limelight.start();
    }

    public void align(){
        result = limelight.getLatestResult();
        hasTarget = (result != null && result.isValid());
        tx = 0.0;
        ty = 0.0;
        ta = 0.0;
        botpose = null;
        if (hasTarget) {
            tx = result.getTx();
            ty = result.getTy();
            ta = result.getTa();
            botpose = result.getBotpose();
        }

        double turnCmd = 0.0;
        double forwardCmd = 0.0;


        if (hasTarget) {
            double error = tx; // degrees right positive per Limelight convention
            if (Math.abs(error) >= TX_DEADBAND) {
                turnCmd = Range.clip(error * KP_TURN, -MAX_TURN, MAX_TURN);
            } else {
                turnCmd = 0.0; // inside deadband, stop rotating
            }
        } else {
            // When not aligning, you can add your own driver control here if you want.
            turnCmd = 0.0;
        }

        if (hasTarget && Math.abs(ta) < TA_DEADBAND) {
            double error = TA_GOAL - ta; // distance error to Goal
            if (Math.abs(error) > TA_DEADBAND) {
                forwardCmd = Range.clip(error * KP_FORWARD, -MAX_FWD, MAX_FWD);
            } else {
                forwardCmd = 0.0;
            }
        } else {
            forwardCmd = 0.0;
        }

        dt.mecanumDrive(forwardCmd, 0.0, turnCmd);
    }


}
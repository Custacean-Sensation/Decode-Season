package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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
@TeleOp(name="AlignToTag", group="drive")
public class AlignToTag extends OpMode {

    // Drivetrain subsystem
    private ExampleDrivetrain dt;

    // Limelight client (Limelight3A)
    private Limelight3A limelight;

    // Turning control
    private static final double KP_TURN = 0.03;     // Proportional gain for tx (tune this)
    private static final double MAX_TURN = 0.35;    // Cap turn power so it doesn’t whip
    private static final double KP_FORWARD = 0.04;
    private static final double MAX_FWD = 0.4;
    private static final double TX_DEADBAND = 2.5;  // Degrees. Inside this => "good enough"
    private static final double TA_DEADBAND = 2.5; // Distance from Limelight
    private static final double TA_GOAL = 35.0; // Goal for % size of tag

    @Override
    public void init() {
        // Initialize drivetrain subsystem using configured motor names
        dt = new ExampleDrivetrain(hardwareMap, "frontLeft", "frontRight", "backLeft", "backRight");

        //Use Limelight3a and hardware map
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        telemetry.addLine("AlignToTag: init complete");
        limelight.pipelineSwitch(0);
        limelight.start();
        telemetry.update();
    }

    @Override
    public void loop() {
        // Pull latest measurements using the LLResult pattern
        LLResult result = limelight.getLatestResult();
        boolean hasTarget = (result != null && result.isValid());
        double tx = 0.0;
        double ty = 0.0;
        double ta = 0.0;
        Pose3D botpose = null;
        if (hasTarget) {
            tx = result.getTx();
            ty = result.getTy();
            ta = result.getTa();
            botpose = result.getBotpose();
        }

        double turnCmd = 0.0;
        double forwardCmd = 0.0;
        boolean aligning = gamepad1.a; // hold A to align
        boolean upDown = gamepad1.b; // hold B to align forward/back

        if (aligning && hasTarget) {
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

        if (upDown && hasTarget && Math.abs(ta) < TA_DEADBAND) {
            double error = TA_GOAL - ta; // distance error to Goal
            if (Math.abs(error) > TA_DEADBAND) {
                forwardCmd = Range.clip(error * KP_FORWARD, -MAX_FWD, MAX_FWD);
            } else {
                forwardCmd = 0.0;
            }
        } else {
            forwardCmd = 0.0;
        }

        // Rotate/move via drivetrain subsystem. ExampleDrivetrain.mecanumDrive
        // expects (lateral, axial, yaw). To produce left=+turn, right=-turn we pass
        dt.mecanumDrive(forwardCmd, 0.0, turnCmd);

        // Telemetry — no fluff, just what matters
        telemetry.addData("Aligning (hold A)", aligning);
        telemetry.addData("Aligning (hold B)", upDown);
        telemetry.addData("Has Target", hasTarget);
        telemetry.addData("tx (deg)", "%.2f", tx);
        telemetry.addData("ty (deg)", "%.2f", ty);
        telemetry.addData("ta (%)", "%.2f", ta);
        telemetry.addData("turnCmd", "%.3f", turnCmd);
        telemetry.addData("forwardCmd", "%.3f", forwardCmd);
        if (botpose != null) {
            telemetry.addData("botpose", botpose.toString());
        }
        telemetry.update();
    }

    // drive control handled by ExampleDrivetrain
}

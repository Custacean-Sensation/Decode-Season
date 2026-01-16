package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.teamcode.subsystems.ExampleDrivetrain;
import java.util.List;
import com.qualcomm.hardware.limelightvision.FiducialResult;

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
    private static final double KP_STRAFE = 0.8;    // meters → power
    private static final double KP_FORWARD_AUTO = 0.6;

    private static final double STRAFE_DEADBAND = 0.05;   // meters (~2 inches)
    private static final double FORWARD_DEADBAND = 0.10;  // meters (~4 inches)

    private static final double TX_DEADBAND = 2.5;   // degrees

    
    private int fiducialId = -1;
    private double forwardMeters = 0.0;
    private double strafeMeters = 0.0;
    private double distanceMeters = 0.0;

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
        LLResult result = limelight.getLatestResult();
        boolean hasTarget = (result != null && result.isValid());
        Pose3D botpose = null;
        double tx = 0.0;

        if (hasTarget) {
            tx = result.getTx();
            botpose = result.getBotpose();

            List<FiducialResult> fiducials = result.getFiducialResults();
            if (!fiducials.isEmpty()) {
                FiducialResult fiducial = fiducials.get(0);

                fiducialId = fiducial.getFiducialId();
                forwardMeters = fiducial.getRobotPoseTargetSpace().getX();
                strafeMeters  = fiducial.getRobotPoseTargetSpace().getY();
                distanceMeters = Math.hypot(forwardMeters, strafeMeters);
            }else {
                fiducialId = -1;
                forwardMeters = 0.0;
                strafeMeters = 0.0;
                distanceMeters = 0.0;
            }
        }

        double strafeCmd = 0.0;
        double forwardCmd = 0.0;
        double turnCmd = 0.0;

        if (hasTarget && fiducialId > 0 && gamepad1.a) {
            // ----- ROTATION (keep tag centered) -----
            if (Math.abs(tx) > TX_DEADBAND) {
                turnCmd = Range.clip(tx * KP_TURN, -MAX_TURN, MAX_TURN);
            }

            // Strafe to center on tag
            if (Math.abs(strafeMeters) > STRAFE_DEADBAND) {
                strafeCmd = Range.clip(-strafeMeters * KP_STRAFE, -MAX_FWD, MAX_FWD);
            }

            // Drive to correct distance
            if (Math.abs(forwardMeters) > FORWARD_DEADBAND) {
                forwardCmd = Range.clip(forwardMeters * KP_FORWARD_AUTO, -MAX_FWD, MAX_FWD);
            }

            if (Math.abs(tx) > TX_DEADBAND) forwardCmd = 0.0;
        }

        dt.mecanumDrive(strafeCmd, forwardCmd, turnCmd);

        telemetry.addData("Has Target", hasTarget);
        telemetry.addData("Tag ID", fiducialId);
        telemetry.addData("Forward (m)", "%.2f", forwardMeters);
        telemetry.addData("Strafe (m)", "%.2f", strafeMeters);
        telemetry.addData("Distance (m)", "%.2f", distanceMeters);
        telemetry.addData("tx (deg)", "%.2f", tx);
        telemetry.addData("Strafe Cmd", "%.2f", strafeCmd);
        telemetry.addData("Turn Cmd", "%.2f", turnCmd);
        telemetry.addData("Turn Cmd", "%.2f", forwardCmd);

        if (botpose != null) {
            telemetry.addData("botpose", botpose.toString());
        }

        telemetry.update();
    }

    // drive control handled by ExampleDrivetrain
}
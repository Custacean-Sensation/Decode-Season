package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
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

    // Limelight client (HTTP JSON). Change host/IP to your camera.
    private LimelightClient limelight;

    // Turning control
    private static final double KP_TURN = 0.03;     // Proportional gain for tx (tune this)
    private static final double MAX_TURN = 0.35;    // Cap turn power so it doesn’t whip
    private static final double KP_FORWARD = 0.04;
    private static final double MAX_FWD = 0.4;
    private static final double TX_DEADBAND = 0.5;  // Degrees. Inside this => "good enough"
    private static final double TY_DEADBAND = 0.5; // Distance from LimeLight
    private static final double TY_SETPOINT = -5.0; // Camera offset to find correct positioning

    @Override
    public void init() {
        // Initialize drivetrain subsystem using configured motor names
        dt = new ExampleDrivetrain(hardwareMap, "lf", "rf", "lb", "rb");

        // Point this at your Limelight. If mDNS isn’t reliable, use the IP.
        limelight = new LimelightClient("http://limelight.local:5807");

        telemetry.addLine("AlignToTag: init complete");
        telemetry.update();
    }

    @Override
    public void loop() {
        // Pull latest measurements
        limelight.update();

        boolean hasTarget = limelight.hasTarget();
        double tx = limelight.getTx();
        double ty = limelight.getTy(); // not used for rotation, but we show it
        double[] botpose = limelight.getBotpose(); // [x,y,z, roll, pitch, yaw] if available

        double turnCmd = 0.0;
        double forwardCmd = 0.0;
        boolean aligning = gamepad1.a; // hold A to align
        boolean upDown = gamepad1.b; // hold B to align

        if (aligning && hasTarget) {
            double error = tx; // degrees right positive per Limelight convention
            if (Math.abs(error) > TX_DEADBAND) {
                turnCmd = Range.clip(error * KP_TURN, -MAX_TURN, MAX_TURN);
            } else {
                turnCmd = 0.0; // inside deadband, stop rotating
            }
        } else {
            // When not aligning, you can add your own driver control here if you want.
            turnCmd = 0.0;
        }

        if (upDown && hasTarget){
            double error = ty - TYSETPOINT; // we use it now lol
            if(Math.abs(error) > TY_DEADBAND){
                forwardCmd = Range.clip(ty * KP_FORWARD, -MAX_FWD, MAX_FWD);
            } else {
                forwardCmd = 0.0;
            }
        } else {
            forwardCmd = 0.0;
        }

        // Rotate in place via drivetrain subsystem. ExampleDrivetrain.mecanumDrive
        // expects (lateral, axial, yaw). To produce left=+turn, right=-turn we pass
        // yaw = -turnCmd (the method negates yaw internally), so use -turnCmd here.
        dt.mecanumDrive(0.0, forwardCmd, -turnCmd);

        // Telemetry — no fluff, just what matters
        telemetry.addData("Aligning (hold A)", aligning);
        telemetry.addData("Has Target", hasTarget);
        telemetry.addData("tx (deg)", "%.2f", tx);
        telemetry.addData("ty (deg)", "%.2f", ty - TYSETPOINT);
        telemetry.addData("turnCmd", "%.3f", turnCmd);
        telemetry.addData("forwardCmd", "%.3f" forwardCmd);
        if (botpose != null && botpose.length >= 6) {
            telemetry.addData("botpose xy (m)", "%.2f, %.2f", botpose[0], botpose[1]);
            telemetry.addData("botpose yaw (deg)", "%.1f", botpose[5]);
        }
        telemetry.update();
    }

    // drive control handled by ExampleDrivetrain
}

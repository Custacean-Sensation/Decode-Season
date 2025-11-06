package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;

/**
 * AlignToTag
 * Rotates in place to drive Limelight's tx -> 0 (centered on the AprilTag).
 * Assumes 4 mecanum/tank motors named: "lf","rf","lb","rb".
 * You control when to align with gamepad1.a (hold to align).
 * If Limelight has no target, motors stop (no blind spinning).
 */
@TeleOp(name="AlignToTag", group="drive")
public class AlignToTag extends OpMode {

    // Motors
    private DcMotorEx lf, rf, lb, rb;

    // Limelight client (HTTP JSON). Change host/IP to your camera.
    private LimelightClient limelight;

    // Turning control
    private static final double KP_TURN = 0.03;     // Proportional gain for tx (tune this)
    private static final double MAX_TURN = 0.35;    // Cap turn power so it doesn’t whip
    private static final double TX_DEADBAND = 0.5;  // Degrees. Inside this => "good enough"

    @Override
    public void init() {
        lf = hardwareMap.get(DcMotorEx.class, "lf");
        rf = hardwareMap.get(DcMotorEx.class, "rf");
        lb = hardwareMap.get(DcMotorEx.class, "lb");
        rb = hardwareMap.get(DcMotorEx.class, "rb");

        // Typical directions for mecanum; flip if your robot spins the wrong way.
        lf.setDirection(DcMotorSimple.Direction.REVERSE);
        lb.setDirection(DcMotorSimple.Direction.REVERSE);
        rf.setDirection(DcMotorSimple.Direction.FORWARD);
        rb.setDirection(DcMotorSimple.Direction.FORWARD);

        // Coast is fine here; use BRAKE if you want it to stop harder.
        lf.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

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
        boolean aligning = gamepad1.a; // hold A to align

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

        // Rotate in place: left +turn, right -turn
        setDrivePower(+turnCmd, -turnCmd, +turnCmd, -turnCmd);

        // Telemetry — no fluff, just what matters
        telemetry.addData("Aligning (hold A)", aligning);
        telemetry.addData("Has Target", hasTarget);
        telemetry.addData("tx (deg)", "%.2f", tx);
        telemetry.addData("ty (deg)", "%.2f", ty);
        telemetry.addData("turnCmd", "%.3f", turnCmd);
        if (botpose != null && botpose.length >= 6) {
            telemetry.addData("botpose xy (m)", "%.2f, %.2f", botpose[0], botpose[1]);
            telemetry.addData("botpose yaw (deg)", "%.1f", botpose[5]);
        }
        telemetry.update();
    }

    private void setDrivePower(double lfp, double rfp, double lbp, double rbp) {
        lf.setPower(lfp);
        rf.setPower(rfp);
        lb.setPower(lbp);
        rb.setPower(rbp);
    }
}

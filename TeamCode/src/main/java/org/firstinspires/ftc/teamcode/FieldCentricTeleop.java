package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

@TeleOp(name = "FieldCentricTeleOp_Pinpoint")
public class FieldCentricTeleop extends OpMode {

    private DcMotor fl, fr, bl, br;
    private GoBildaPinpointDriver pinpoint;

    @Override
    public void init() {
        // Drive motors
        fl = hardwareMap.get(DcMotor.class, "frontLeft");
        fr = hardwareMap.get(DcMotor.class, "frontRight");
        bl = hardwareMap.get(DcMotor.class, "backLeft");
        br = hardwareMap.get(DcMotor.class, "backRight");

        // Adjust directions to match your robot
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        br.setDirection(DcMotorSimple.Direction.FORWARD);

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Pinpoint
        pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");

        // ---- PINPOINT CONFIG – YOU MUST FIX THESE FOR YOUR ROBOT ----

        // Use built-in pod constants if you're using goBILDA pods
        pinpoint.setEncoderResolution(
                GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_SWINGARM_POD
                // or GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD
        );

        // Make sure encoder directions match your pod wiring + physical direction
        pinpoint.setEncoderDirections(
                GoBildaPinpointDriver.EncoderDirection.FORWARD,  // X pod (forward)
                GoBildaPinpointDriver.EncoderDirection.FORWARD   // Y pod (strafe)
        );

        // Pod offsets from robot center in **mm** (measure this properly)
        // xOffset: + left of center, - right of center
        // yOffset: + forward of center, - backward of center
        pinpoint.setOffsets(
                0.0, // xOffset mm
                0.0,  // yOffset mm
                DistanceUnit.MM
        );

        // Reset pose + IMU once at init (robot must be still)
        pinpoint.resetPosAndIMU();

        telemetry.addLine("Initialized – Pinpoint configured");
        telemetry.update();
    }

    @Override
    public void start() {
        // Extra safety: reset again at start of TeleOp if robot is stationary
        pinpoint.resetPosAndIMU();
    }

    @Override
    public void loop() {
        // ---- DRIVER INPUTS ----
        double y = -gamepad1.left_stick_y;   // forward/back
        double x = gamepad1.left_stick_x;    // strafe
        double turn = gamepad1.right_stick_x; // rotation

        // Optional: small deadzone
        double deadzone = 0.03;
        if (Math.abs(x) < deadzone) x = 0;
        if (Math.abs(y) < deadzone) y = 0;
        if (Math.abs(turn) < deadzone) turn = 0;

        // ---- UPDATE PINPOINT (HEADING ONLY FOR SPEED) ----
        pinpoint.update(GoBildaPinpointDriver.ReadData.ONLY_UPDATE_HEADING);

        // Unnormalized heading in **radians**
        double heading = pinpoint.getHeading(AngleUnit.RADIANS);

        // ---- FIELD-CENTRIC TRANSFORM ----
        // Rotate joystick vector by -heading to convert field frame -> robot frame
        double cosH = Math.cos(-heading);
        double sinH = Math.sin(-heading);

        double rotX = x * cosH - y * sinH;
        double rotY = x * sinH + y * cosH;

        // ---- MECANUM DRIVE MATH ----
        double flPower = rotY + rotX + turn;
        double frPower = rotY - rotX - turn;
        double blPower = rotY - rotX + turn;
        double brPower = rotY + rotX - turn;

        // Normalize so max magnitude = 1
        double max = Math.max(
                1.0,
                Math.max(
                        Math.abs(flPower),
                        Math.max(Math.abs(frPower),
                                Math.max(Math.abs(blPower), Math.abs(brPower)))
                )
        );

        fl.setPower(flPower / max);
        fr.setPower(frPower / max);
        bl.setPower(blPower / max);
        br.setPower(brPower / max);

        // ---- TELEMETRY ----
        double headingDeg = heading * 180.0 / Math.PI;
        telemetry.addData("Heading (rad)", heading);
        telemetry.addData("Heading (deg, unnormalized)", headingDeg);
        telemetry.addData("rotX", rotX);
        telemetry.addData("rotY", rotY);
        telemetry.update();
    }
}

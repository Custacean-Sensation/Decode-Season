package org.firstinspires.ftc.teamcode.pedroPathing;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.FilteredPIDFCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.pedropathing.paths.PathConstraints;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {

    // -------------------------------------------------
    // FOLLOWER (global motion) TUNING
    // -------------------------------------------------
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(16.0)
            .forwardZeroPowerAcceleration(-26.0)
            .lateralZeroPowerAcceleration(-65.0)

            .translationalPIDFCoefficients(new PIDFCoefficients(
                    0.035, 0.000, 0.000, 0.015
            ))
            .translationalPIDFSwitch(4.0)
            .secondaryTranslationalPIDFCoefficients(new PIDFCoefficients(
                    0.40, 0.000, 0.005, 0.0006
            ))

            .headingPIDFCoefficients(new PIDFCoefficients(
                    0.85, 0.00, 0.00, 0.010
            ))
            .secondaryHeadingPIDFCoefficients(new PIDFCoefficients(
                    2.2, 0.00, 0.10, 0.0005
            ))

            .drivePIDFCoefficients(new FilteredPIDFCoefficients(
                    0.10, 0.00, 0.00035, 0.60, 0.015
            ))
            .secondaryDrivePIDFCoefficients(new FilteredPIDFCoefficients(
                    0.020, 0.000, 0.000005, 0.60, 0.010
            ))
            .drivePIDFSwitch(15.0)

            .centripetalScaling(0.0005);

    // -------------------------------------------------
    // MECANUM DRIVETRAIN DEFINITIONS
    // -------------------------------------------------
    public static MecanumConstants driveConstants = new MecanumConstants()
            .leftFrontMotorName("motor_lf")
            .leftRearMotorName("motor_lb")
            .rightFrontMotorName("motor_rf")
            .rightRearMotorName("motor_rb")

            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)

            .xVelocity(78.0)   // measured fwd/back max speed (in/s)
            .yVelocity(60.0);  // measured strafe max speed (in/s)

    // -------------------------------------------------
    // PINPOINT LOCALIZER OFFSETS
    // -------------------------------------------------
    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(0.75)
            .strafePodX(-6.6)
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.REVERSED);

    // -------------------------------------------------
    // PATH CONSTRAINTS
    // -------------------------------------------------
    public static PathConstraints pathConstraints = new PathConstraints(
            0.995,  // tValueConstraint (leave ~1.0)
            0.10,   // velocityConstraint: % of max speed
            0.10,   // translationalConstraint: accel cap
            0.009,  // headingConstraint: angular speed cap
            50.0,   // timeoutConstraint (s)
            1.25,   // brakingStrength
            10.0,   // BEZIER_CURVE_SEARCH_LIMIT (leave at 10)
            1.0     // brakingStart (in)
    );

    // -------------------------------------------------
    // FACTORY
    // -------------------------------------------------
    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .mecanumDrivetrain(driveConstants)
                .pinpointLocalizer(localizerConstants)
                .pathConstraints(pathConstraints)
                .build();
    }

    // -------------------------------------------------
    // TUNING ORDER (FOLLOW THIS STEP-BY-STEP)
    // -------------------------------------------------
    /*
     1. Motor directions & names:
        - Verify config names match your RobotConfig.
        - Reverse whichever motors spin the wrong way.
     2. Localizer directions & offsets:
        - Push robot by hand: X forward, Y left. Adjust directions if flipped.
        - Measure offsets with a ruler. Wrong values = drift.
     3. Measured velocities:
        - Time a 6–10 ft sprint. Set xVelocity and yVelocity to true max speeds.
        - Don’t guess high, it breaks feedforward math.
     4. Translational / Heading PID:
        - Tune P first (both primary and secondary).
        - Adjust secondary for crisp final lock-in.
     5. Drive PIDF:
        - Adjust carefully. Small changes only. Goal = responsive but stable.
     6. Path constraints:
        - Adjust per auto routine to limit speed, accel, or turn rate.
        - Use brakingStrength/brakingStart to stop overshooting endpoints.
    */
}

package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

/**
 * Centralized robot constants with support for runtime tuning via:
 * - Bylazar Configurables (@Configurable annotation)
 * - FTC Dashboard (@Config annotation)
 *
 * To tune these values at runtime:
 * 1. For Bylazar Panels: Connect to http://192.168.43.1:8080/panels
 *    The @Configurable annotation auto-registers this class with the Panels web interface.
 * 2. For FTC Dashboard: Use the Dashboard app with @Config classes
 *
 * All public static fields in this class are automatically exposed for tuning.
 *
 * Reference: https://panels.bylazar.com/docs/com.bylazar.configurables/
 */
@Configurable
@Config
public class RobotConstants {

    // =============================================
    // DRIVETRAIN CONSTANTS
    // =============================================
    public static double DRIVE_SPEED = 0.5;
    public static double ROTATE_SPEED = 0.2;
    public static double WHEEL_DIAMETER_MM = 96;
    public static double ENCODER_TICKS_PER_REV = 537.7;
    // Note: TICKS_PER_MM is calculated, not directly tunable
    public static double TICKS_PER_MM = (ENCODER_TICKS_PER_REV / (WHEEL_DIAMETER_MM * Math.PI));

    // =============================================
    // LIMELIGHT & VISION ALIGNMENT CONSTANTS
    // =============================================
    // Proportional gain for turning to target (tx alignment)
    public static double LIMELIGHT_KP_TURN = 0.03;
    // Maximum turn power cap
    public static double LIMELIGHT_MAX_TURN = 0.35;
    // Proportional gain for forward/backward alignment
    public static double LIMELIGHT_KP_FORWARD = 0.04;
    // Maximum forward power cap
    public static double LIMELIGHT_MAX_FWD = 0.4;
    // Deadband for tx (rotation) in degrees - AlignToTag variant
    public static double LIMELIGHT_TX_DEADBAND_ALIGN = 2.5;
    // Deadband for tx (rotation) in degrees - Q3Teleop variant
    public static double LIMELIGHT_TX_DEADBAND_Q3 = 5.0;
    // Deadband for ta (area) in percent
    public static double LIMELIGHT_TA_DEADBAND = 2.5;
    // Goal target area in percent
    public static double LIMELIGHT_TA_GOAL = 35.0;

    // =============================================
    // OUTAKE SYSTEM CONSTANTS
    // =============================================
    // Time for feeder pulse in seconds
    public static double OUTAKE_FEED_TIME_SECONDS = 0.20;
    // Stop speed for servos
    public static double OUTAKE_STOP_SPEED = 0.0;
    // Full speed for servos
    public static double OUTAKE_FULL_SPEED = 1.0;
    // Launcher target velocity in ticks/second
    public static double OUTAKE_LAUNCHER_TARGET_VELOCITY = 1125 * 1.5;
    // Launcher minimum velocity threshold in ticks/second
    public static double OUTAKE_LAUNCHER_MIN_VELOCITY = 1075 * 1.45;

    // =============================================
    // INTAKE SYSTEM CONSTANTS
    // =============================================
    public static double INTAKE_POWER = 1.0;

    // =============================================
    // PINPOINT ODOMETRY CONSTANTS
    // =============================================
    // goBILDA Swingarm Pod ticks-per-mm conversion factor
    public static float GOBILDA_SWINGARM_POD_TICKS_PER_MM = 13.26291192f;
    // goBILDA 4-Bar Pod ticks-per-mm conversion factor
    public static float GOBILDA_4_BAR_POD_TICKS_PER_MM = 19.89436789f;
    // Default I2C address for goBILDA Pinpoint driver
    public static byte PINPOINT_DEFAULT_ADDRESS = 0x31;

    // =============================================
    // FIELD CENTRIC DRIVE CONSTANTS
    // =============================================
    // Deadzone for gamepad inputs
    public static double DRIVE_DEADZONE = 0.03;

    /**
     * Recalculates derived constants that depend on other configurable values.
     * Call this after updating WHEEL_DIAMETER_MM or ENCODER_TICKS_PER_REV.
     */
    public static void recalculateDerivedConstants() {
        TICKS_PER_MM = (ENCODER_TICKS_PER_REV / (WHEEL_DIAMETER_MM * Math.PI));
    }

}

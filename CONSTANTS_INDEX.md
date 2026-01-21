# Robot Constants Index - Decode Season

## Summary
This document indexes all constant variables found in the TeamCode src directory and their current status. All constants have been centralized in `RobotConstants.java`.

## Files Scanned
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/RobotConstants.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/AlignToTag.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/BasicLimelightCalls.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/BasicStrafer.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/FieldCentricTeleop.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Q3Teleop.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/move.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/GoBildaPinpointDriver.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/ExampleDrivetrain.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/Intake.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/OutakeSystem.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/subsystems/OuttakeSystemV2.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedroPathing/Constants.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/pedroPathing/Tuning.java
- ✓ TeamCode/src/main/java/org/firstinspires/ftc/teamcode/Paths/*.java

## Centralized Constants in RobotConstants.java

### Drivetrain Constants
- `DRIVE_SPEED = 0.5` (from ExampleDrivetrain.java)
- `ROTATE_SPEED = 0.2` (from ExampleDrivetrain.java)
- `WHEEL_DIAMETER_MM = 96` (from ExampleDrivetrain.java)
- `ENCODER_TICKS_PER_REV = 537.7` (from ExampleDrivetrain.java)
- `TICKS_PER_MM` (calculated from above)

### Limelight & Vision Alignment Constants
- `LIMELIGHT_KP_TURN = 0.03` (from AlignToTag.java, Q3Teleop.java)
- `LIMELIGHT_MAX_TURN = 0.35` (from AlignToTag.java, Q3Teleop.java)
- `LIMELIGHT_KP_FORWARD = 0.04` (from AlignToTag.java)
- `LIMELIGHT_MAX_FWD = 0.4` (from AlignToTag.java)
- `LIMELIGHT_TX_DEADBAND_ALIGN = 2.5` (from AlignToTag.java)
- `LIMELIGHT_TX_DEADBAND_Q3 = 5.0` (from Q3Teleop.java)
- `LIMELIGHT_TA_DEADBAND = 2.5` (from AlignToTag.java)
- `LIMELIGHT_TA_GOAL = 35.0` (from AlignToTag.java)

### Outake System Constants
- `OUTAKE_FEED_TIME_SECONDS = 0.20` (from OutakeSystem.java)
- `OUTAKE_STOP_SPEED = 0.0` (from OutakeSystem.java)
- `OUTAKE_FULL_SPEED = 1.0` (from OutakeSystem.java)
- `OUTAKE_LAUNCHER_TARGET_VELOCITY = 1125 * 1.5` (from OutakeSystem.java)
- `OUTAKE_LAUNCHER_MIN_VELOCITY = 1075 * 1.45` (from OutakeSystem.java)

### Intake System Constants
- `INTAKE_POWER = 1.0` (from Intake.java)

### Pinpoint Odometry Constants
- `GOBILDA_SWINGARM_POD_TICKS_PER_MM = 13.26291192f` (from GoBildaPinpointDriver.java)
- `GOBILDA_4_BAR_POD_TICKS_PER_MM = 19.89436789f` (from GoBildaPinpointDriver.java)
- `PINPOINT_DEFAULT_ADDRESS = 0x31` (from GoBildaPinpointDriver.java)

### Field Centric Drive Constants
- `DRIVE_DEADZONE = 0.03` (from ExampleDrivetrain.java, FieldCentricTeleop.java)

## Next Steps
To fully implement this centralization, update the following files to use `RobotConstants` imports:
1. AlignToTag.java - Replace local constants with RobotConstants
2. Q3Teleop.java - Replace local constants with RobotConstants
3. OutakeSystem.java - Replace local constants with RobotConstants
4. ExampleDrivetrain.java - Replace local constants with RobotConstants
5. Intake.java - Replace hardcoded value with RobotConstants
6. FieldCentricTeleop.java - Replace hardcoded deadzone with RobotConstants

## Benefits
- ✓ Single source of truth for all robot configuration constants
- ✓ Easier to adjust robot behavior without touching multiple files
- ✓ Better maintainability and consistency across the codebase
- ✓ Integration with @Configurable annotation for runtime tuning

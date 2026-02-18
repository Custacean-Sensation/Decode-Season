package org.firstinspires.ftc.teamcode.Paths;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.OuttakeV2;

@Autonomous(name = "Backside-Right Red", group = "Autonomous")
@Configurable
public class BacksideRightRed extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;

    private int pathState;
    private Paths paths;

    private Intake intake;
    private OuttakeV2 outake;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);

        intake = new Intake(hardwareMap, "intakeMotor");
        outake = new OuttakeV2(hardwareMap, "flywheel", "rightFeeder", "leftFeeder", intake, "beamBreak");

        // Build poses + paths
        paths = new Paths(follower);
        follower.setStartingPose(paths.START);

        pathState = 0;

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        follower.update();
        outake.update();
        pathState = autonomousPathUpdate();

        panelsTelemetry.debug("Path State", pathState);
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());
        panelsTelemetry.debug("Launch State", outake.launchState.toString());
        panelsTelemetry.update(telemetry);
    }

    // Paths inner class
    public static class Paths {
        private final Follower follower;

        // Pose variables
        public final Pose START;
        public final Pose LAUNCH_ZONE;

        // Path variables
        public PathChain gotoLaunchzone;
        public PathChain moveToArtifactsRow1;
        public PathChain pickupArtifactsRow1;
        public PathChain backToLaunchzone1;
        public PathChain downToArtifacts2;
        public PathChain pickupArtifacts2;
        public PathChain upToLaunchzone;
    
        public Paths(Follower follower) {
            this.follower = follower;

            // Define poses from the .pp file
            START = new Pose(121.25674091441971, 125.70222743259085, Math.toRadians(37));
            LAUNCH_ZONE = new Pose(94.5, 93.5, Math.toRadians(45));

            buildPaths();
        }

        public void buildPaths() {
            gotoLaunchzone = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(START, LAUNCH_ZONE)
                    ))
                    .setLinearHeadingInterpolation(START.getHeading(), LAUNCH_ZONE.getHeading())
                    .build();

            moveToArtifactsRow1 = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(LAUNCH_ZONE, new Pose(94.5, 84.5, Math.toRadians(180)))
                    ))
                    .setLinearHeadingInterpolation(LAUNCH_ZONE.getHeading(), Math.toRadians(180))
                    .build();

            pickupArtifactsRow1 = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(new Pose(94.5, 84.5, Math.toRadians(180)), new Pose(116.8, 84.5, Math.toRadians(180)))
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            backToLaunchzone1 = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(new Pose(116.8, 84.5, Math.toRadians(180)), LAUNCH_ZONE)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), LAUNCH_ZONE.getHeading())
                    .build();

            downToArtifacts2 = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(LAUNCH_ZONE, new Pose(94.5, 60, Math.toRadians(180)))
                    ))
                    .setLinearHeadingInterpolation(LAUNCH_ZONE.getHeading(), Math.toRadians(180))
                    .build();

            pickupArtifacts2 = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(new Pose(94.5, 60, Math.toRadians(180)), new Pose(116.8, 60, Math.toRadians(180)))
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .build();

            upToLaunchzone = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(new Pose(116.8, 60, Math.toRadians(180)), LAUNCH_ZONE)
                    ))
                    .setLinearHeadingInterpolation(Math.toRadians(180), LAUNCH_ZONE.getHeading())
                    .build();
        }
    }

    private void launchAtZone() {
        outake.autoShoot(2);
    }

    public int autonomousPathUpdate() {
        switch (pathState) {
            case 0:
                follower.followPath(paths.gotoLaunchzone);
                pathState++;
                break;

            case 1:
                if (!follower.isBusy()) {
                    launchAtZone();
                    pathState++;
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(paths.moveToArtifactsRow1);
                    pathState++;
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    follower.followPath(paths.pickupArtifactsRow1);
                    pathState++;
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    follower.followPath(paths.backToLaunchzone1);
                    pathState++;
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    launchAtZone();
                    pathState++;
                }
                break;

            case 6:
                if (!follower.isBusy()) {
                    follower.followPath(paths.downToArtifacts2);
                    pathState++;
                }
                break;

            case 7:
                if (!follower.isBusy()) {
                    follower.followPath(paths.pickupArtifacts2);
                    pathState++;
                }
                break;

            case 8:
                if (!follower.isBusy()) {
                    follower.followPath(paths.upToLaunchzone);
                    pathState++;
                }
                break;

            case 9:
                if (!follower.isBusy()) {
                    launchAtZone();
                    pathState++;
                }
                break;

            case 10:
                if (!follower.isBusy()) {
                    outake.stopLauncher();
                    pathState = -1;
                }
                break;
        }

        return pathState;
    }
}

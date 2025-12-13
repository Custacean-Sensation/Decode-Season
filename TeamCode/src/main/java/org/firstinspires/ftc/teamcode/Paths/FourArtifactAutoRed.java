package org.firstinspires.ftc.teamcode.Paths;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.OutakeSystem;

@Autonomous(name = "FourArtifactAutoRed", group = "Autonomous")
@Configurable
public class FourArtifactAutoRed extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;

    private int pathState;
    private Paths paths;

    private Intake intake;
    private OutakeSystem outake;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);

        intake = new Intake(hardwareMap, "intake");
        outake = new OutakeSystem(hardwareMap, "launcher", "leftFeeder", "rightFeeder");

        // Build poses + paths Pedro-example style
        paths = new Paths(follower);
        follower.setStartingPose(paths.START); // uses saved pose variable

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
        panelsTelemetry.debug("Launching?", outake.isLaunching());
        panelsTelemetry.update(telemetry);
    }

    // ------------------------------------------------------------
    // Pedro-example style: all poses are variables + buildPaths()
    // ------------------------------------------------------------
    public static class Paths {
        private final Follower follower;

        // ---- Pose variables ----
        public final Pose START;
        public final Pose LAUNCH;

        public final Pose PREP_GRAB_P1;
        public final Pose PREP_GRAB_END;

        public final Pose GRAB_END;

        public final Pose PREP_LAUNCH_END;

        // ---- Each segment is its own path object (PathChain of 1 path) ----
        public PathChain firstLaunch;
        public PathChain prepForGrab;
        public PathChain grab;
        public PathChain prepForLaunch;
        public PathChain secondLaunch;

        public Paths(Follower follower) {
            this.follower = follower;

            // Define poses once (no magic numbers scattered around)
            START = new Pose(126.189, 126.491, Math.toRadians(35));
            LAUNCH = new Pose(96, 96, Math.toRadians(50));

            PREP_GRAB_P1 = new Pose(94.64, 84.37, Math.toRadians(45));
            PREP_GRAB_END = new Pose(96, 83.62, Math.toRadians(180));

            GRAB_END = new Pose(120, 84, Math.toRadians(180));

            PREP_LAUNCH_END = new Pose(103.291, 83.241, Math.toRadians(180));


            buildPaths();
        }

        // public void buildPaths() and each segment is separate.
        public void buildPaths() {

            firstLaunch = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(START, LAUNCH)
                            )
                    )
                    .setLinearHeadingInterpolation(START.getHeading(), LAUNCH.getHeading())
                    .build();

            prepForGrab = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(LAUNCH, PREP_GRAB_END)
                            )
                    )
                    .setLinearHeadingInterpolation(LAUNCH.getHeading(), PREP_GRAB_END.getHeading())
                    .build();

            grab = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(PREP_GRAB_END, GRAB_END)
                    ))
                    .setConstantHeadingInterpolation(GRAB_END.getHeading())
                    .build();

            prepForLaunch = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(GRAB_END, LAUNCH)
                    ))
                    .setLinearHeadingInterpolation(GRAB_END.getHeading(), LAUNCH.getHeading())
                    .build();

        }
    }

    public int autonomousPathUpdate() {
        switch (pathState) {

            case 0:
                follower.followPath(paths.firstLaunch);
                pathState++;
                break;

            case 1:
                if (!follower.isBusy()) {
                    outake.spinUpLauncher();
                    while(outake.getLauncherVelocity() < outake.launchVelocity() - 100){outake.update();};
                    intake.start();
                    outake.requestShot();
                    while(outake.isLaunching()){outake.update(); panelsTelemetry.addData("launching?", outake.isLaunching()); panelsTelemetry.update();}
                    outake.requestShot();
                    while(outake.isLaunching()){outake.update(); panelsTelemetry.addData("launching?", outake.isLaunching()); panelsTelemetry.update();}
                    follower.followPath(paths.prepForGrab);
                    pathState++;
                }
                break;

            case 2:
                if (!follower.isBusy()) {
                    follower.followPath(paths.grab);
                    pathState++;
                }
                break;

            case 3:
                if (!follower.isBusy()) {
                    intake.stop();
                    follower.followPath(paths.prepForLaunch);
                    pathState++;
                }
                break;

            case 4:
                if (!follower.isBusy()) {
                    intake.start();
                    outake.requestShot();
                    while(outake.isLaunching()){outake.update();}
                    outake.requestShot();
                    while(outake.isLaunching()){outake.update();}
                    pathState++;
                }
                break;

            case 5:
                if (!follower.isBusy()) {
                    intake.stop();
                    pathState = -1;
                }
                break;
        }

        return pathState;
    }
}

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

@Autonomous(name = "FourArtifactAutoBlue", group = "Autonomous")
@Configurable
public class FourArtifactAutoBlue extends OpMode {

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
        panelsTelemetry.update(telemetry);
    }

    // ------------------------------------------------------------
    // Pedro-example style: all poses are variables + buildPaths()
    // ------------------------------------------------------------
    public static class Paths {
        private final Follower follower;

        // ---- Pose variables ----
        public Pose START, LAUNCH, PREP_GRAB_P1, PREP_GRAB, GRAB, RETURN_P1;

        // ---- Each segment is its own path object (PathChain of 1 path) ----
        public PathChain firstLaunch;
        public PathChain prepForGrab;
        public PathChain grab;
        public PathChain returnToLaunch;

        public Paths(Follower follower) {
            this.follower = follower;

            // Define poses once (no magic numbers scattered around)
            START = new Pose(20.981, 122.415, Math.toRadians(145));
            LAUNCH = new Pose(56.604, 87.245, Math.toRadians(135));

            PREP_GRAB_P1 = new Pose(53.736, 81.057);
            PREP_GRAB = new Pose(41.811, 83.472, Math.toRadians(0));

            GRAB = new Pose(24.453, 83.472, Math.toRadians(0));

            RETURN_P1 = new Pose(51.623, 63.849);

            buildPaths();
        }

        // public void buildPaths() and each segment is separate.
        public void buildPaths() {

            firstLaunch = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(START, LAUNCH)
                    ))
                    .setLinearHeadingInterpolation(START.getHeading(), LAUNCH.getHeading())
                    .build();

            prepForGrab = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierCurve(
                                    LAUNCH,
                                    PREP_GRAB_P1,
                                    PREP_GRAB
                            )
                    ))
                    .setLinearHeadingInterpolation(LAUNCH.getHeading(), PREP_GRAB.getHeading())
                    .build();

            grab = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(
                                    PREP_GRAB,
                                    GRAB
                            )
                    ))
                    .setLinearHeadingInterpolation(PREP_GRAB.getHeading(), GRAB.getHeading())
                    .build();

            returnToLaunch = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierCurve(
                                    GRAB,
                                    RETURN_P1,
                                    LAUNCH
                            )
                    ))
                    .setLinearHeadingInterpolation(GRAB.getHeading(), LAUNCH.getHeading())
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
                    intake.start();
                    outake.spinUpLauncher();
                    while(outake.getLauncherVelocity() < outake.launchVelocity()){}
                    outake.requestShot();
                    while(outake.isLaunching()){
                        outake.update();
                    }
                    outake.requestShot();
                    while(outake.isLaunching()){outake.update();}
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
                    follower.followPath(paths.returnToLaunch);
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
                    outake.stopLauncher();
                    pathState = -1;
                }
                break;
        }

        return pathState;
    }
}

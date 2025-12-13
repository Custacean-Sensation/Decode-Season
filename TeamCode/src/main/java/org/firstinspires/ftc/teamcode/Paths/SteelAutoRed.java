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
import org.firstinspires.ftc.teamcode.subsystems.OutakeSystem;

@Autonomous(name = "steelAutoRed", group = "Autonomous")
@Configurable
public class SteelAutoRed extends OpMode {

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
        public final Pose END;

        // ---- Each segment is its own path object (PathChain of 1 path) ----
        public PathChain firstLaunch;
        public PathChain prepForGrab;
        public PathChain grab;
        public PathChain prepForLaunch;
        public PathChain secondLaunch;

        public Paths(Follower follower) {
            this.follower = follower;

            // Define poses once (no magic numbers scattered around)
            START = new Pose(83, 12, Math.toRadians(90));
            LAUNCH = new Pose(85.5, 89, Math.toRadians(45));
            END = new Pose(83, 20, Math.toRadians(180));

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

            grab = follower.pathBuilder()
                    .addPath(new Path(
                            new BezierLine(LAUNCH, END)
                    ))
                    .setLinearHeadingInterpolation(LAUNCH.getHeading(), END.getHeading())
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
                    while(outake.getLauncherVelocity() < outake.launchVelocity() - 50){outake.update();};
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
                    intake.stop();
                    outake.stopLauncher();
                    pathState = 3;
                }
                break;
            case 3:
                if(!follower.isBusy()){
                    follower.followPath(paths.grab);
                    pathState = 4;
                }
            case 4:
                if(!follower.isBusy()){
                    pathState = -1;
                }

        }

        return pathState;
    }
}

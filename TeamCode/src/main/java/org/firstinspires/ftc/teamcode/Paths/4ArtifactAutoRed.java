package org.firstinspires.ftc.teamcode;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.OutakeSystem;

@Autonomous(name = "Pedro Pathing Autonomous", group = "Autonomous")
@Configurable // Panels
public class PedroAutonomous extends OpMode {

  private TelemetryManager panelsTelemetry; // Panels Telemetry instance
  public Follower follower; // Pedro Pathing follower instance
  private int pathState; // Current autonomous path state (state machine)
  private Paths paths; // Paths defined in the Paths class
  Intake intake;
  OutakeSystem outake;

  @Override
  public void init() {
    panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    follower = Constants.createFollower(hardwareMap);
    follower.setStartingPose(new Pose(72, 8, Math.toRadians(90)));

    paths = new Paths(follower); // Build paths

    panelsTelemetry.debug("Status", "Initialized");
    panelsTelemetry.update(telemetry);

    intake = new Intake(hardwareMap, "intake");
    outake = new OutakeSystem(hardwareMap, "launcher", "leftFeeder", "rightFeeder");
  }

  @Override
  public void loop() {
    follower.update(); // Update Pedro Pathing
    pathState = autonomousPathUpdate(); // Update autonomous state machine

    // Log values to Panels and Driver Station
    panelsTelemetry.debug("Path State", pathState);
    panelsTelemetry.debug("X", follower.getPose().getX());
    panelsTelemetry.debug("Y", follower.getPose().getY());
    panelsTelemetry.debug("Heading", follower.getPose().getHeading());
    panelsTelemetry.update(telemetry);
  }

  public static class Paths {

    public PathChain FirstLaunch;
    public PathChain PrepforGrab;
    public PathChain Grab;
    public PathChain PrepforLaunch;
    public PathChain SecondLaunch;

    public Paths(Follower follower) {
      FirstLaunch = follower
        .pathBuilder()
        .addPath(
          new BezierLine(new Pose(121.924, 123.139), new Pose(103.291, 102.278))
        )
        .setConstantHeadingInterpolation(Math.toRadians(45))
        .build();
      intake.start();
      outake.requestShot();
      outake.requestShot();
      outake.stopLauncher();

      PrepforGrab = follower
        .pathBuilder()
        .addPath(
          new BezierCurve(
            new Pose(103.291, 102.278),
            new Pose(103.291, 91.747),
            new Pose(103.291, 83.241)
          )
        )
        .setLinearHeadingInterpolation(Math.toRadians(45), Math.toRadians(180))
        .build();

      Grab = follower
        .pathBuilder()
        .addPath(
          new BezierLine(new Pose(103.291, 83.241), new Pose(124.962, 83.443))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180))
        .build();
      intake.stop();

      PrepforLaunch = follower
        .pathBuilder()
        .addPath(
          new BezierLine(new Pose(124.962, 83.443), new Pose(103.291, 83.241))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180))
        .build();

      SecondLaunch = follower
        .pathBuilder()
        .addPath(
          new BezierLine(new Pose(103.291, 83.241), new Pose(103.291, 102.278))
        )
        .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(45))
        .build();
      intake.start();
      outake.requestShot();
      outake.requestShot();
      intake.stop();
      outake.stopLauncher();
    }
  }

  public int autonomousPathUpdate() {
    // Add your state machine Here
    // Access paths with paths.pathName
    // Refer to the Pedro Pathing Docs (Auto Example) for an example state machine
    return pathState;
  }
}

package org.firstinspires.ftc.teamcode.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
public class Paths {

  public PathChain Path1;
  public PathChain Path2;
  public PathChain Path3;
  public PathChain Path4;

  public Paths(Follower follower) {
    Path1 = follower
      .pathBuilder()
      .addPath(new BezierLine(new Pose(56.453, 5.434), new Pose(15.849, 5.736)))
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
      .setReversed()
      .build();

    Path2 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(15.849, 5.736), new Pose(14.943, 24.604))
      )
      .setTangentHeadingInterpolation()
      .build();

    Path3 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(14.943, 24.604), new Pose(24.000, 34.717))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(0))
      .build();

    Path4 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(24.000, 34.717), new Pose(62.189, 11.019))
      )
      .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(65))
      .build();
  }
}
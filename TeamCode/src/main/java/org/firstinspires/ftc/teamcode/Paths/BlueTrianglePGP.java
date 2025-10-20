package org.firstinspires.ftc.teamcode.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
public class Paths {

  public PathChain Path1;
  public PathChain Path2;
  public PathChain Path4;
  public PathChain Path3;

  public Paths(Follower follower) {
    Path1 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(81.057, 8.453), new Pose(107.774, 58.566))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(0))
      .build();

    Path2 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(107.774, 58.566), new Pose(125.434, 59.321))
      )
      .setTangentHeadingInterpolation()
      .build();

    Path4 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(125.434, 59.321), new Pose(107.321, 58.717))
      )
      .setTangentHeadingInterpolation()
      .setReversed()
      .build();

    Path3 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(107.321, 58.717), new Pose(80.906, 8.151))
      )
      .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(115))
      .build();
  }
}
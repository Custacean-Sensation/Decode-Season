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
      .addPath(
        new BezierLine(new Pose(85.887, 4.226), new Pose(130.264, 6.038))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
      .setReversed()
      .build();

    Path2 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(130.264, 6.038), new Pose(129.962, 26.566))
      )
      .setTangentHeadingInterpolation()
      .build();

    Path3 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(129.962, 26.566), new Pose(120.302, 33.057))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
      .build();

    Path4 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(120.302, 33.057), new Pose(81.660, 9.358))
      )
      .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(115))
      .build();
  }
}
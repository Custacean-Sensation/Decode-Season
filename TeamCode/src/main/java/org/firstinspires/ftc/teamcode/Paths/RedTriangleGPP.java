import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
public class Paths {

  public PathChain Path1;
  public PathChain Path2;
  public PathChain Path3;

  public Paths(Follower follower) {
    Path1 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(55.849, 7.698), new Pose(56.000, 36.000))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
      .setReversed()
      .build();

    Path2 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(56.000, 36.000), new Pose(18.415, 35.925))
      )
      .setTangentHeadingInterpolation()
      .build();

    Path3 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(18.415, 35.925), new Pose(63.396, 6.792))
      )
      .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(60))
      .build();
  }
}
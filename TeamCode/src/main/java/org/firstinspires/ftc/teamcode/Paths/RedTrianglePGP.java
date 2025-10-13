public static class Paths {

  public PathChain Path1;
  public PathChain Path2;
  public PathChain Path3;

  public Paths(Follower follower) {
    Path1 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(55.849, 7.698), new Pose(57.208, 59.321))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(180))
      .setReversed(true)
      .build();

    Path2 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(57.208, 59.321), new Pose(12.528, 60.075))
      )
      .setTangentHeadingInterpolation()
      .build();

    Path3 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(12.528, 60.075), new Pose(70.792, 82.113))
      )
      .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(50))
      .build();
  }
}
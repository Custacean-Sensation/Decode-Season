public static class Paths {

  public PathChain Path1;
  public PathChain Path2;
  public PathChain Path3;

  public Paths(Follower follower) {
    Path1 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(83.774, 7.698), new Pose(107.623, 35.321))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(0))
      .build();

    Path2 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(107.623, 35.321), new Pose(126.943, 35.472))
      )
      .setTangentHeadingInterpolation()
      .build();

    Path3 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(126.943, 35.472), new Pose(83.925, 7.698))
      )
      .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(115))
      .build();
  }
}
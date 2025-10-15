public static class Paths {

  public PathChain Path5;

  public Paths(Follower follower) {
    Path5 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(87.111, 9.244), new Pose(72.000, 85.000))
      )
      .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(90))
      .build();
  }
}
public static class Paths {

  public PathChain Path5;

  public Paths(Follower follower) {
    Path5 = follower
      .pathBuilder()
      .addPath(
        new BezierLine(new Pose(22.400, 120.711), new Pose(72.000, 85.000))
      )
      .setLinearHeadingInterpolation(Math.toRadians(0), Math.toRadians(90))
      .build();
  }
}
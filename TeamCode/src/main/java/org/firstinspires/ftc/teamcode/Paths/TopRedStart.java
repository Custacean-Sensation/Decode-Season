package org.firstinspires.ftc.teamcode.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class TopRedStart {

    public PathChain Path1;
    public TopRedStart(Follower follower) {
        Path1 = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(new Pose(123.321, 122.868), new Pose(91.019, 90.415))
                )
                .setLinearHeadingInterpolation(Math.toRadians(37), Math.toRadians(100))
                .build();
    }
}

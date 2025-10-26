package org.firstinspires.ftc.teamcode.Paths;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;

public class TopBlueStart {

    public PathChain Path1;

    public TopBlueStart(Follower follower) {
        Path1 = follower
                .pathBuilder()
                .addPath(
                        new BezierLine(new Pose(19.472, 125.283), new Pose(48.000, 95.000))
                )
                .setLinearHeadingInterpolation(Math.toRadians(-44), Math.toRadians(75))
                .build();
    }

}

package org.firstinspires.ftc.teamcode.Paths;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.subsystems.Intake;
import org.firstinspires.ftc.teamcode.subsystems.OutakeSystem;

@Autonomous(name = "steelAutoBlue", group = "Autonomous")
@Configurable
public class SteelAutoBlue extends OpMode {

    private TelemetryManager panelsTelemetry;
    public Follower follower;

    private Paths paths;

    private Intake intake;
    private OutakeSystem outake;

    // -------------------------
    // Autonomous state machine
    // -------------------------
    private enum AutoState {
        DRIVE_TO_LAUNCH,
        SPINUP_AND_QUEUE_SHOTS,
        WAIT_SHOTS_DONE,
        DRIVE_TO_END,
        DONE
    }

    private AutoState state = AutoState.DRIVE_TO_LAUNCH;
    private AutoState lastState = null;

    private final ElapsedTime stateTimer = new ElapsedTime();

    // Reasonable fail-safes (prevents “stuck forever”)
    private static final double DRIVE_TIMEOUT_SEC = 6.0;
    private static final double SPINUP_TIMEOUT_SEC = 3.0;
    private static final double SHOOT_TIMEOUT_SEC  = 4.0;

    // Guard so we only call followPath() once per drive state
    private boolean pathStarted = false;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);

        intake = new Intake(hardwareMap, "intake");
        outake = new OutakeSystem(hardwareMap, "launcher", "leftFeeder", "rightFeeder");

        paths = new Paths(follower);
        follower.setStartingPose(paths.START);

        setState(AutoState.DRIVE_TO_LAUNCH);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        // Always keep these running
        follower.update();
        outake.update();

        runAuto();

        // Telemetry
        panelsTelemetry.debug("State", state.toString());
        panelsTelemetry.debug("TimeInState", stateTimer.seconds());
        panelsTelemetry.debug("Busy", follower.isBusy());
        panelsTelemetry.debug("X", follower.getPose().getX());
        panelsTelemetry.debug("Y", follower.getPose().getY());
        panelsTelemetry.debug("Heading", follower.getPose().getHeading());

        panelsTelemetry.debug("LauncherVel", outake.getLauncherVelocity());
        panelsTelemetry.debug("LauncherMin", outake.getLauncherMinVelocity());
        panelsTelemetry.debug("Launching?", outake.isLaunching());

        panelsTelemetry.update(telemetry);
    }

    private void runAuto() {
        // State entry hook
        if (state != lastState) {
            stateTimer.reset();
            pathStarted = false;
            lastState = state;
        }

        switch (state) {
            case DRIVE_TO_LAUNCH: {
                outake.spinUpLauncher();
                if (!pathStarted) {
                    follower.followPath(paths.toLaunch);
                    pathStarted = true;
                }

                // done condition
                if (!follower.isBusy()) {
                    setState(AutoState.SPINUP_AND_QUEUE_SHOTS);
                    break;
                }

                // timeout fallback
                if (stateTimer.seconds() > DRIVE_TIMEOUT_SEC) {
                    setState(AutoState.SPINUP_AND_QUEUE_SHOTS);
                }
                break;
            }

            case SPINUP_AND_QUEUE_SHOTS: {
                // Keep flywheel commanded on every loop in case your implementation expects it
                outake.spinUpLauncher();

                // Stage rings only while shooting
                intake.start();

                boolean ready =
                        outake.getLauncherVelocity() >= outake.getLauncherMinVelocity();

                // Queue shots once we’re at speed (or if we time out)
                if (ready || stateTimer.seconds() > SPINUP_TIMEOUT_SEC) {
                    outake.requestShots(2); // <- requires the queued-shot OutakeSystem fix
                    setState(AutoState.WAIT_SHOTS_DONE);
                }
                break;
            }

            case WAIT_SHOTS_DONE: {
                // Wait until the outake reports it finished all queued shots
                if (!outake.isLaunching()) {
                    // Stop everything cleanly
                    intake.stop();
                    outake.stopLauncher();

                    setState(AutoState.DRIVE_TO_END);
                    break;
                }

                // fail-safe: don’t hang forever
                if (stateTimer.seconds() > SHOOT_TIMEOUT_SEC) {
                    intake.stop();
                    outake.stopLauncher();
                    setState(AutoState.DRIVE_TO_END);
                }
                break;
            }

            case DRIVE_TO_END: {
                if (!pathStarted) {
                    follower.followPath(paths.toEnd);
                    pathStarted = true;
                }

                if (!follower.isBusy()) {
                    setState(AutoState.DONE);
                    break;
                }

                if (stateTimer.seconds() > DRIVE_TIMEOUT_SEC) {
                    setState(AutoState.DONE);
                }
                break;
            }

            case DONE: {
                // Do nothing. Robot will sit still.
                intake.stop();
                // Do not keep calling stopLauncher every loop unless you want it:
                // outake.stopLauncher();
                break;
            }
        }
    }

    private void setState(AutoState newState) {
        state = newState;
        // lastState check is handled in runAuto()
    }

    // -------------------------
    // Paths (Pedro example style)
    // -------------------------
    public static class Paths {
        private final Follower follower;

        public final Pose START;
        public final Pose LAUNCH;
        public final Pose END;

        public final PathChain toLaunch;
        public final PathChain toEnd;

        public Paths(Follower follower) {
            this.follower = follower;

            START  = new Pose(61, 8,  Math.toRadians(90));
            LAUNCH = new Pose(62, 87, Math.toRadians(135));
            END    = new Pose(62, 20, Math.toRadians(180));

            toLaunch = follower.pathBuilder()
                    .addPath(new Path(new BezierLine(START, LAUNCH)))
                    .setLinearHeadingInterpolation(START.getHeading(), LAUNCH.getHeading())
                    .build();

            toEnd = follower.pathBuilder()
                    .addPath(new Path(new BezierLine(LAUNCH, END)))
                    .setLinearHeadingInterpolation(LAUNCH.getHeading(), END.getHeading())
                    .build();
        }
    }
}

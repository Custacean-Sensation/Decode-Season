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
        SPINUP_AND_STAGE,
        QUEUE_SHOTS,
        WAIT_SHOTS_DONE,
        DRIVE_TO_END,
        DONE
    }

    private AutoState state = AutoState.DRIVE_TO_LAUNCH;
    private AutoState lastState = null;

    private final ElapsedTime stateTimer = new ElapsedTime();

    // ---- Timeouts / fail-safes (do NOT "shoot anyway") ----
    private static final double DRIVE_TIMEOUT_SEC  = 6.0;
    private static final double SPINUP_TIMEOUT_SEC = 4.5;  // if not ready by then, skip shooting
    private static final double SHOOT_TIMEOUT_SEC  = 5.0;  // if jammed/hung, cancel and move on

    // ---- Intake staging + velocity stability gates ----
    private static final double INTAKE_PREROLL_SEC = 0.25; // ensure intake is running before shot sequence
    private static final double VEL_TOLERANCE      = 60.0; // velocity must be within this of target
    private static final double VEL_STABLE_SEC     = 0.25; // must stay in tolerance this long

    private final ElapsedTime intakeTimer = new ElapsedTime();
    private final ElapsedTime velStableTimer = new ElapsedTime();

    // Guard so we only call followPath() once per drive state
    private boolean pathStarted = false;

    // Guard so we only queue shots once
    private boolean shotsQueued = false;

    @Override
    public void init() {
        panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

        follower = Constants.createFollower(hardwareMap);

        intake = new Intake(hardwareMap, "intake");
        outake = new OutakeSystem(hardwareMap, "launcher", "leftFeeder", "rightFeeder");

        paths = new Paths(follower);
        follower.setStartingPose(paths.START);
        outake.reverseFeedPulse();
        outake.stopFeeders(); // method that sets both to 0


        setState(AutoState.DRIVE_TO_LAUNCH);

        panelsTelemetry.debug("Status", "Initialized");
        panelsTelemetry.update(telemetry);
    }

    @Override
    public void loop() {
        runAuto();

        // Telemetry
        panelsTelemetry.debug("State", state.toString());
        panelsTelemetry.debug("TimeInState", stateTimer.seconds());
        panelsTelemetry.debug("Busy", follower.isBusy());

        Pose p = follower.getPose();
        panelsTelemetry.debug("X", p.getX());
        panelsTelemetry.debug("Y", p.getY());
        panelsTelemetry.debug("Heading", p.getHeading());

        panelsTelemetry.debug("LauncherVel", outake.getLauncherVelocity());
        panelsTelemetry.debug("LauncherMin", outake.getLauncherMinVelocity());
        // If you added getLauncherTargetVelocity() in OutakeSystem, this will show real target:
        // panelsTelemetry.debug("LauncherTarget", outake.getLauncherTargetVelocity());

        panelsTelemetry.debug("VelStableT", velStableTimer.seconds());
        panelsTelemetry.debug("IntakePreT", intakeTimer.seconds());
        panelsTelemetry.debug("Launching?", outake.isLaunching());
        panelsTelemetry.debug("ShotsQueued", shotsQueued);

        panelsTelemetry.update(telemetry);

        // Always keep these running (never block)
        follower.update();
        outake.update();

    }

    private void runAuto() {
        // State entry hook
        if (state != lastState) {
            stateTimer.reset();
            pathStarted = false;
            lastState = state;

            // Reset per-state helpers
            if (state == AutoState.SPINUP_AND_STAGE) {
                intakeTimer.reset();
                velStableTimer.reset();
                shotsQueued = false;
            }
            if (state == AutoState.QUEUE_SHOTS) {
                // keep current timers; just ensure we only queue once
                shotsQueued = false;
            }
        }

        switch (state) {
            case DRIVE_TO_LAUNCH: {
                // Spin up early while driving to save time
                outake.spinUpLauncher();

                if (!pathStarted) {
                    follower.followPath(paths.toLaunch);
                    pathStarted = true;
                }

                if (!follower.isBusy()) {
                    setState(AutoState.SPINUP_AND_STAGE);
                    break;
                }

                if (stateTimer.seconds() > DRIVE_TIMEOUT_SEC) {
                    // If drive is taking too long, proceed anyway (but still won't shoot unless ready)
                    setState(AutoState.SPINUP_AND_STAGE);
                }
                break;
            }

            case SPINUP_AND_STAGE: {
                outake.spinUpLauncher();

                // Intake ON to make sure a ring is staged before both shots
                intake.start();

                // ---- Velocity stability gate ----
                double vel = outake.getLauncherVelocity();

                // Prefer a real TARGET if you add getLauncherTargetVelocity(). Otherwise, use min as a fallback.
                // double target = outake.getLauncherTargetVelocity();
                double target = outake.getLauncherMinVelocity(); // fallback if you didn't add target getter

                // If you only have MIN available, tighten the logic: require vel comfortably above MIN.
                // This prevents feeding right at the edge.
                boolean aboveMinWithMargin = vel >= (outake.getLauncherMinVelocity() + 100.0);

                boolean inTol = Math.abs(vel - target) <= VEL_TOLERANCE;
                if (!inTol) {
                    velStableTimer.reset();
                }
                boolean velStable = velStableTimer.seconds() >= VEL_STABLE_SEC;

                boolean intakePreRolled = intakeTimer.seconds() >= INTAKE_PREROLL_SEC;

                // If you don't have target getter, use aboveMinWithMargin + stability time as the readiness check.
                boolean readyToShoot = intakePreRolled && (aboveMinWithMargin || velStable);

                if (readyToShoot) {
                    setState(AutoState.QUEUE_SHOTS);
                    break;
                }

                // IMPORTANT: timeout means "skip shooting", NOT "shoot early"
                if (stateTimer.seconds() > SPINUP_TIMEOUT_SEC) {
                    intake.stop();
                    outake.stopLauncher();
                    setState(AutoState.DRIVE_TO_END);
                }
                break;
            }

            case QUEUE_SHOTS: {
                // Keep flywheel commanded during shooting
                outake.spinUpLauncher();

                // Keep intake running until shots are fully complete
                intake.start();

                if (!shotsQueued) {
                    outake.requestShots(2); // requires OutakeSystem queue support
                    shotsQueued = true;
                }

                setState(AutoState.WAIT_SHOTS_DONE);
                break;
            }

            case WAIT_SHOTS_DONE: {
                // Explicitly keep intake running until BOTH shots complete
                intake.start();
                outake.spinUpLauncher();

                if (!outake.isLaunching()) {
                    intake.stop();
                    outake.stopLauncher();
                    setState(AutoState.DRIVE_TO_END);
                    break;
                }

                if (stateTimer.seconds() > SHOOT_TIMEOUT_SEC) {
                    // Jam / hung: cancel and move on
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
                // Park: keep mechanisms safe/off
                intake.stop();
                // outake.stopLauncher(); // optional; if you want hard-off every loop
                break;
            }
        }
    }

    private void setState(AutoState newState) {
        state = newState;
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

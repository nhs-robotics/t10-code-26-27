package opmodes.auto;

import static launchpad.Constants.DRIVE_MOTOR_CONFIG;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import launchpad.Constants;
import launchpad.actions.MoveToAction;
import launchpad.actions.SequentialAction;
import launchpad.actions.SimultaneousAction;
import launchpad.geometry.FieldPosition;
import launchpad.hardware.Motor;
import launchpad.hardware.MotorConfig;
import launchpad.hardware.PinpointModule;
import launchpad.movement.mecanum.MecanumDriver;
import launchpad.pathing.PinpointLocalizer;
import launchpad.telemetry_viewer.websocket.TelemetryData;
import launchpad.telemetry_viewer.websocket.TelemetryObject;

@Autonomous(name="MoveAuto")
public class MoveAuto extends OpMode {

    @TelemetryData
    protected SequentialAction actionQueue;

    @TelemetryObject
    private MecanumDriver driver;

    @TelemetryObject
    private Motor fl, fr, bl, br;

    private Motor intakeMotor;
    private Motor transferMotor;

    private Motor outtakeMotor;

    private Servo launchServo;

    private PinpointLocalizer localizer;

    @Override
    public void init() {
        fl = new Motor(hardwareMap.get(DcMotorEx.class, "fl"), DRIVE_MOTOR_CONFIG);
        fr = new Motor(hardwareMap.get(DcMotorEx.class, "fr"), DRIVE_MOTOR_CONFIG);
        bl = new Motor(hardwareMap.get(DcMotorEx.class, "bl"), DRIVE_MOTOR_CONFIG);
        br = new Motor(hardwareMap.get(DcMotorEx.class, "br"), DRIVE_MOTOR_CONFIG);

        driver = new MecanumDriver(fl, fr, bl, br, Constants.MECANUM_COEFFICIENT_MATRIX, Constants.MAX_WHEEL_VELOCITY);

        intakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "intake"), new MotorConfig(Constants.MotorConstants.GOBILDA_435RPM_5203_2402_0014_TICKS_PER_ROTATION, 2.5));
        transferMotor = new Motor(hardwareMap.get(DcMotorEx.class, "transfer"), new MotorConfig(Constants.MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION, 5));

        outtakeMotor = new Motor(hardwareMap.get(DcMotorEx.class, "outtake"), new MotorConfig(Constants.MotorConstants.GOBILDA_6000RPM_5203_2402_0001_TICKS_PER_ROTATION, 3.7795));

        launchServo = hardwareMap.get(Servo.class, "launchServo");
        launchServo.setDirection(Servo.Direction.REVERSE);
        launchServo.setPosition(-1);

        localizer = new PinpointLocalizer(hardwareMap.get(PinpointModule.class, "pinpoint"), Constants.PINPOINT_X_OFFSET, PinpointModule.EncoderDirection.FORWARD, Constants.PINPOINT_Y_OFFSET, PinpointModule.EncoderDirection.FORWARD, PinpointModule.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        localizer.setCurrentFieldPosition(new FieldPosition(0, 0, 0));

        actionQueue = new SequentialAction(
                new MoveToAction(driver, localizer, new FieldPosition(10, 0, 0), 1, 1, 1, Math.toRadians(5))
        );
    }

    @Override
    public void loop() {
        localizer.loop();
        actionQueue.loop();
    }
}

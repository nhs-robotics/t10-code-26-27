package opmodes.teleop;

import static launchpad.Constants.DRIVE_MOTOR_CONFIG;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;

import launchpad.Constants;
import launchpad.TeleopBase;
import launchpad.actions.CustomAction;
import launchpad.actions.SequentialAction;
import launchpad.actions.SleepAction;
import launchpad.geometry.MovementVector;
import launchpad.hardware.Motor;
import launchpad.hardware.MotorConfig;
import launchpad.movement.mecanum.MecanumDriver;
import launchpad.subsystems.Outtake;
import launchpad.telemetry_viewer.websocket.TelemetryObject;

@TeleOp(name="Manual Teleop")
public class ManualTeleop extends TeleopBase {

    @TelemetryObject
    private final Outtake outtake = new Outtake();

    @TelemetryObject
    private MecanumDriver driver;

    @TelemetryObject
    private Motor fl, fr, bl, br;

    private Motor intakeMotor;
    private Motor transferMotor;

    private Motor outtakeMotor;

    private Servo launchServo;

    private float launchSpeed = 600;

    @Override
    protected void registerSubsystems() {
//        robot.register(outtake);
    }

    @Override
    public void init() {
        super.init();

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

        mainGamepad.leftTrigger.onToggle((Boolean on) -> {
            intakeMotor.setPower(on ? 0.1 : 0);
            transferMotor.setPower(on ? -0.1 : 0);
        });

        mainGamepad.rightTrigger.onPress(() -> {
           runningActions.addAndInitialize(new SequentialAction(
                   new CustomAction(() -> {
                       launchServo.setPosition(1);
                   }),
                   new SleepAction(500),
                   new CustomAction(() -> {
                       launchServo.setPosition(-1);
                   })
           ), true);
        });

        secondaryGamepad.yButton.onPress(() -> {
           launchSpeed += 100;
        });
        secondaryGamepad.aButton.onPress(() -> {
            launchSpeed -= 100;
        });
    }

    @Override
    public void loop() {
        super.loop();

        outtakeMotor.setVelocity(mainGamepad.xButton.isToggled() ? launchSpeed : 0);
//        outtakeHeadingServo.setPower(secondaryGamepad.rightJoystick.getX());

//        outtake.setTargetHeading(Math.atan2(secondaryGamepad.leftJoystick.getY(), secondaryGamepad.leftJoystick.getX()));
        driver.setRelativeVelocity(new MovementVector(mainGamepad.leftJoystick.getY() * 28, mainGamepad.leftJoystick.getX() * 28, mainGamepad.rightJoystick.getX() * 28));
    }
}

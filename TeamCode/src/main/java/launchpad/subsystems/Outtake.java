package launchpad.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import launchpad.Constants;
import launchpad.controllers.PIDController;
import launchpad.geometry.Angles;
import launchpad.hardware.Motor;
import launchpad.hardware.MotorConfig;

public class Outtake extends Subsystem {
    private Motor outtakeHeadingMotor;
    private double targetHeadingRadians = 0;
    private PIDController headingController;
    private static final PIDCoefficients headingPIDCoefficients = new PIDCoefficients(1, 0, 0);

    @Override
    public void init(HardwareMap hardwareMap) {
        outtakeHeadingMotor = new Motor(hardwareMap.get(DcMotorEx.class, "outtakeRotator"), new MotorConfig(Constants.MotorConstants.GOBILDA_312RPM_5203_2402_0019_TICKS_PER_ROTATION, 6, 176.0/64.0));
        headingController = new PIDController(headingPIDCoefficients, () -> Angles.angleDifference(outtakeHeadingMotor.getMotorEncoder().getPosition(), targetHeadingRadians));
    }

    @Override
    public void loop() {
        outtakeHeadingMotor.setAngularVelocity(headingController.getPower());
    }

    public void setTargetHeading(double targetHeadingRadians) {
        this.targetHeadingRadians = targetHeadingRadians;
    }
}

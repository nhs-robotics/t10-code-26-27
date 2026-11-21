package launchpad.subsystems;

import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDCoefficients;

import launchpad.hardware.CRServoPositionController;
import launchpad.telemetry_viewer.websocket.TelemetryObject;

public class Outtake extends Subsystem {
    @TelemetryObject
    private CRServoPositionController outtakeHeadingServo;

    private static final PIDCoefficients headingPIDCoefficients = new PIDCoefficients(1, 0, 0);

    private static final double HEADING_GEAR_RATIO = 176.0 / 64.0;

    @Override
    public void init(HardwareMap hardwareMap) {
        outtakeHeadingServo = new CRServoPositionController(hardwareMap.get(CRServo.class, "outtakeHeadingServo"), hardwareMap.get(AnalogInput.class, "outtakeRotatorServoPosition"), headingPIDCoefficients, HEADING_GEAR_RATIO, -Math.PI/2, Math.PI/2, "outtakeHeadingServo");
    }

    @Override
    public void loop() {
        outtakeHeadingServo.loop();
    }

    public void setTargetHeading(double targetHeadingRadians) {
        this.outtakeHeadingServo.setTargetAngleRadians(targetHeadingRadians);
    }
}

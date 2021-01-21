package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

// simple autonomous program that drives bot forward 2 seconds then ends.



// below is the Annotation that registers this OpMode with the FtcRobotController app.
// @Autonomous classifies the OpMode as autonomous, name is the OpMode title and the
// optional group places the OpMode into the Exercises group.
// uncomment the @Disable annotation to remove the OpMode from the OpMode list.

@Autonomous(name="AutonamousFirst", group="Exercises")
public class AutonamousFirst extends LinearOpMode {
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackeDrive = null;
    private DcMotor rightBackeDrive = null;
    private DcMotor intake = null;
    private DcMotor outtake = null;

    // called when init button is  pressed.

    @Override
    public void runOpMode() throws InterruptedException
    {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFront");  // motor 2
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront"); // motor 3
        leftBackeDrive = hardwareMap.get(DcMotor.class, "leftBack"); // motor 0
        rightBackeDrive = hardwareMap.get(DcMotor.class, "rightBack"); // motor 1
        intake = hardwareMap.get(DcMotor.class, "intake"); // motor 1
        outtake = hardwareMap.get(DcMotor.class, "outtake"); // motor 1

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackeDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackeDrive.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotor.Direction.REVERSE);

        telemetry.addData("Mode", "waiting");
        telemetry.update();

        // wait for start button.

        waitForStart();

        telemetry.addData("Mode", "running");
        telemetry.update();

        // set both motors to 25% power.

        driveit(-.75, .75,
                -.75, .75, 2000);


//        driveit(1.0, 1.0,
//                1.0, 1.0, 2000);
//        driveit(-1.0, -1.0,
//                1.0, 1.0, 2000);


    }

    private void driveit(double leftfrontpower, double leftbackpower,
                         double rightfrontpower, double rightbackpower, long sleepTime) {
        leftFrontDrive.setPower(leftfrontpower);
        leftBackeDrive.setPower(leftbackpower);
        rightFrontDrive.setPower(rightfrontpower);
        rightBackeDrive.setPower(rightbackpower);

        sleep(2000);        // wait for 2 seconds.

        // set motor power to zero to stop motors.

        leftFrontDrive.setPower(0.0);
        leftBackeDrive.setPower(0.0);
        rightFrontDrive.setPower(0.0);
        rightBackeDrive.setPower(0.0);
    }
}
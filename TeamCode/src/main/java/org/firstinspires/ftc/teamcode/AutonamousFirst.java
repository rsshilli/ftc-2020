package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import java.util.Date;
import java.util.List;
// simple autonomous program that drives bot forward 2 seconds then ends.


// below is the Annotation that registers this OpMode with the FtcRobotController app.
// @Autonomous classifies the OpMode as autonomous, name is the OpMode title and the
// optional group places the OpMode into the Exercises group.
// uncomment the @Disable annotation to remove the OpMode from the OpMode list.

@Autonomous(name = "AutonamousFirst", group = "Exercises")
public class AutonamousFirst extends LinearOpMode {
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackeDrive = null;
    private DcMotor rightBackeDrive = null;
    private DcMotor intake = null;
    private DcMotor outtake = null;
    private DcMotor armMotor = null;
    private Servo gripServo = null;
    private int numberOfRings;
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";
    double gripPosition;
    double MIN_GRIP = 0.5, MAX_GRIP = 1.0;
    private int previousTicks = 0;
    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * com.vuforia.Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */
    private static final String VUFORIA_KEY =
            "AWz6j8L/////AAABmRKkIryTuktLi2mFvZqqaeBDZF67S0ewoDJGMGD7nMiS/el/YAB4BDMhHU9CLQpwfj9cEEkSYB9pZgtsyWTg9q+koX/OUS9w1fDUD2O/ZgUHqvquZ3DgZe+HpsRa3ZcFslOjrqxWO/A7tEYFSJi0OZYLKVD9duT6zYq2OUiT4NJbESkRJvEk0HKmOzIwW395Ujv1uVVxgfaEdIDp4RdMhdI7Fl+ZZ+yKbnoDSnVw/UZHKSg6S/2ZclKQTPZpBmR7wJJp0y4CoSjZZhaukcNSCvsUB6Glr6WajtHP5qDooeWVjmsGi6RRol4h/QlV2sFrLv4ueJS6DPAnOn7oZ9CCeWYavv9cLTYvi6tDB6MuTOsm";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the TensorFlow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;
    // called when init button is  pressed.

    @Override
    public void runOpMode() throws InterruptedException {
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFront");  // motor 2
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront"); // motor 3
        leftBackeDrive = hardwareMap.get(DcMotor.class, "leftBack"); // motor 0
        rightBackeDrive = hardwareMap.get(DcMotor.class, "rightBack"); // motor 1
        intake = hardwareMap.get(DcMotor.class, "intake"); // motor 1
        outtake = hardwareMap.get(DcMotor.class, "outtake"); // motor 2
        armMotor = hardwareMap.get(DcMotor.class, "arm_motor"); // motor 3
        gripServo = hardwareMap.servo.get("grip_servo"); // servo 5

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackeDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackeDrive.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotor.Direction.REVERSE);
        armMotor.setDirection(DcMotor.Direction.FORWARD);
        armMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        initVuforia();
        initTfod();
        stopAndResetEncoder();
        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            //- to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.5, 16.0 / 9.0);
        }

        telemetry.addData("Mode", "waiting");
        telemetry.update();

        // wait for start button.
        gripServo.setPosition(Range.clip(.5, MIN_GRIP, MAX_GRIP));
        //close grip
        runToTicks(.8, 200);
        //move arm up
        waitForStart();

        telemetry.addData("Mode", "running");
        telemetry.update();


        // set both motors to 25% power
        numberOfRings = lookToFindRings();
        sleep(500);
        numberOfRings = 4;
        //wait
        driveit(.5, .5,
                .5, .5, 1000);
        //drive forward
        //look to find rings
        if (numberOfRings == 0 || numberOfRings == 4) {
            if (numberOfRings == 0) {
                runToTicks(.8, 200);
                driveit(-1, 1, 1, -1, 1200);
                //strafe to wall
                runToTicks(.8, 200);
                driveit(1, 1,
                        1, 1, 1400);
                //move forward
                sleep(500);
                //wait
                runToTicks(.08, 500);
                //move arm into position000
                sleep(1000);
                //release grip
                moveServo(1);
//                //wait
                driveit(0, 0, 0, 0, 1000);
//                //wait
                runToTicks(.03, 300);
//                //move arm up
                driveit(.4, .4,
                        .4, .4, 1000);
                //wait
                driveit(.5, -.5,
                        -.5, .5, 1900);
//                //strafe away from woblle zone
                driveit(-1, -1, -1, -1, 300);
                //move to line
                runToTicks(.6, 50);
                moveServo(.6);

            } else {
                runToTicks(.8, 200);
                driveit(-0.8, 0.8, 0.8, -0.8, 1200);
                //strafe to wall
                runToTicks(.8, 200);
                driveit(1.0, 1.0,
                        1.0, 1.0, 2400);
                //move forward
                sleep(500);
                //wait
                sleep(1000);
                driveit(-1.0, -1.0,
                        1.0, 1.0, 500);
                sleep(1000);
                //release grip
                moveServo(1);
//                //wait
                sleep(1000);
//
                driveit(1.0, 1.0,
                        -1.0, -1.0, 500);
                sleep(500);
                //wait
                driveit(-1.0, -1.0,
                        -1.0, -1.0, 600);
//                //strafe back from woblle zone
                //move to line
                runToTicks(.6, 50);
                moveServo(.6);

            }
        } else {
            runToTicks(.8, 200);
            driveit(.99, .99,
                    .99, .99, 1600);
            runToTicks(.8, 100);
            sleep(500);
            moveServo(1);
            sleep(500);
            driveit(-1, -1, -1, -1, 200);
//            moveServo(.25,.5);
            runToTicks(.6, 50);
            moveServo(.6);
//            sleep(50);
//            moveServo(.25,.08);
//            moveServo(.3,.08);
//            driveit(1,1,1,1,1000);
//            moveServo(.3,.5);


        }


//        driveit(1.0, 1.0,
//                1.0, 1.0, 2000);
//        driveit(-1.0, -1.0,
//                1.0, 1.0, 2000);
        driveit(0, 0, 0, 0, 500);

    }

    private int lookToFindRings() {
        int numberOfRings = 0;
        if (tfod != null) {
            long endTime = new Date().getTime() + 3000;

            while (numberOfRings == 0 && new Date().getTime() < endTime) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                if (updatedRecognitions != null) {
                    telemetry.addData("# Object Detected", updatedRecognitions.size());

                    // step through the list of recognitions and display boundary info.
                    int i = 0;
                    for (Recognition recognition : updatedRecognitions) {
                        String label = recognition.getLabel();
                        if (label.equals("Quad")) {
                            numberOfRings = 4;
                        } else if (label.equals("Single")) {
                            numberOfRings = 1;
                        }
                        telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                        telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                recognition.getLeft(), recognition.getTop());
                        telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                recognition.getRight(), recognition.getBottom());
                    }
                    telemetry.update();
                }
            }
        }
        return numberOfRings;
    }


    private void runToTicks(double speed, int ticks) {
        armMotor.setTargetPosition(ticks);
        armMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        armMotor.setPower(Math.abs(speed));
        while (opModeIsActive() && armMotor.isBusy()) {
            telemetry.addData("LFT, RFT", "Running to %7d", ticks);
            telemetry.addData("LFP, RFP", "Running at %7d",
                    armMotor.getCurrentPosition()
            );
            telemetry.update();
        }

    }


    private void stopAndResetEncoder() {
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
    }

    private void runUsingEncoder() {
        armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    private void moveServo(double gripPosition) {
        if (opModeIsActive()) {
            gripServo.setPosition(Range.clip(gripPosition, MIN_GRIP, MAX_GRIP));
        }
    }

    private void driveit(double leftfrontpower, double leftbackpower,
                         double rightfrontpower, double rightbackpower, long sleepTime) {
        if (opModeIsActive()) {

            leftFrontDrive.setPower(leftfrontpower);
            leftBackeDrive.setPower(leftbackpower);
            rightFrontDrive.setPower(rightfrontpower);
            rightBackeDrive.setPower(rightbackpower);

            telemetry.addData(String.format("found %d rings Driving for %d ", numberOfRings, sleepTime), " (%.03f , %.03f, %.03f , %.03f)",
                    leftfrontpower, rightfrontpower, leftbackpower, rightbackpower);
            telemetry.update();

            sleep(sleepTime);        // wait for 2 seconds.

            // set motor power to zero to stop motors.

            leftFrontDrive.setPower(0.0);
            leftBackeDrive.setPower(0.0);
            rightFrontDrive.setPower(0.0);
            rightBackeDrive.setPower(0.0);
            armMotor.setPower(0.0);
        }

    }

    /**
     * Initialize the Vuforia localizati0on engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        // Loading trackables is not necessary for the TensorFlow Object Detection engine.
    }

    /**
     * Initialize the TensorFlow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfodParameters.minResultConfidence = 0.8f;
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_FIRST_ELEMENT, LABEL_SECOND_ELEMENT);
    }
}
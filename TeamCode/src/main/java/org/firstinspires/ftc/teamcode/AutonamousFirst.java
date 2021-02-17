package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer.CameraDirection;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

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
    private int numberOfRings;
    private static final String TFOD_MODEL_ASSET = "UltimateGoal.tflite";
    private static final String LABEL_FIRST_ELEMENT = "Quad";
    private static final String LABEL_SECOND_ELEMENT = "Single";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-manager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
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
        outtake = hardwareMap.get(DcMotor.class, "outtake"); // motor 1

        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackeDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackeDrive.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotor.Direction.REVERSE);


        initVuforia();
        initTfod();

        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(2.5, 16.0 / 9.0);
        }

        telemetry.addData("Mode", "waiting");
        telemetry.update();

        // wait for start button.

        waitForStart();

        telemetry.addData("Mode", "running");
        telemetry.update();


        // set both motors to 25% power
         numberOfRings = lookToFindRings();
        driveit(.5, .5,
                .5, .5, 1400);

        //look to find rings
        if (numberOfRings == 0 || numberOfRings == 4) {
            if (numberOfRings == 0) {
                driveit(1.0, .0,
                        1.0, 1.0, 800);
                //realeasre thew wobwle here
                        driveit(.01,.01,
                                .01,.01,1000);
                driveit(-1.0, 1.0,
                        -1.0, 1.0, 1000);
                //raiss arm so we dont hit tyhe woble
                driveit(.01,.01,
                        .01,.01,1000);
                driveit(-1.,-1.,
                        -1.,-1.,1900);
                //pick up the woblle
                driveit(.01,.01,
                        .01,.01,1000);
                driveit(1.,-1,
                        1, -1.,1400);
            } else {

                driveit(1.0, 1.0,
                        1.0, 1.0, 1850);
                driveit(-1.0, -1.0,
                        -1.0, -1.0, 700);


            }
        } else {
            driveit(.175, -.175,
                    .175, -.175, 4050);
            driveit(.99, .99,
                     .99, .99, 1250);

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

    private void driveit(double leftfrontpower, double leftbackpower,
                         double rightfrontpower, double rightbackpower, long sleepTime) {
        if (opModeIsActive()) {

            leftFrontDrive.setPower(leftfrontpower);
            leftBackeDrive.setPower(leftbackpower);
            rightFrontDrive.setPower(rightfrontpower);
            rightBackeDrive.setPower(rightbackpower);

            telemetry.addData(String.format( "found %d rings Driving for %d ",numberOfRings, sleepTime), " (%.03f , %.03f, %.03f , %.03f)",
                    leftfrontpower, rightfrontpower, leftbackpower, rightbackpower);
            telemetry.update();

            sleep(sleepTime);        // wait for 2 seconds.

            // set motor power to zero to stop motors.

//        leftFrontDrive.setPower(0.0);
//        leftBackeDrive.setPower(0.0);
//        rightFrontDrive.setPower(0.0);
//        rightBackeDrive.setPower(0.0);
        }
    }

    /**
     * Initialize the Vuforia localization engine.
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
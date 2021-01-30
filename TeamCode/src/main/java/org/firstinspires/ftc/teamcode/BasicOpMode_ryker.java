/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autosnomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 * <p>
 * This particular OpMode just executes a basic Tank Drive Teleop for a two wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "Basic: Linear potato", group = "Linear Opmode")
public class BasicOpMode_ryker extends LinearOpMode {

    // Declare OpMode members.
    ;
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor leftFrontDrive = null;
    private DcMotor rightFrontDrive = null;
    private DcMotor leftBackeDrive = null;
    private DcMotor rightBackeDrive = null;
    private DcMotor intake = null;
    private DcMotor outtake = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        leftFrontDrive = hardwareMap.get(DcMotor.class, "leftFront");  // motor 2
        rightFrontDrive = hardwareMap.get(DcMotor.class, "rightFront"); // motor 3
        leftBackeDrive = hardwareMap.get(DcMotor.class, "leftBack"); // motor 0
        rightBackeDrive = hardwareMap.get(DcMotor.class, "rightBack"); // motor 1
        intake = hardwareMap.get(DcMotor.class, "intake"); // motor 1
        outtake = hardwareMap.get(DcMotor.class, "outtake"); // motor 1
        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackeDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackeDrive.setDirection(DcMotor.Direction.FORWARD);
        intake.setDirection(DcMotor.Direction.FORWARD);
        outtake.setDirection(DcMotor.Direction.REVERSE);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double leftFrontPower;
            double rightBackPower;
            double leftBackPower;
            double rightFrontPower;
            double intakePower;
            double outtakePower;
            // helloMyNameIsBob
            double strafe = gamepad1.left_stick_x;

            // The left stick Is for forward, backward,  and to turn the robot's front.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = -gamepad1.left_stick_y;
            double turn = gamepad1.right_stick_x;
            leftFrontPower = Range.clip(drive + turn - strafe, -1.0, 1.0);            //
            leftBackPower = Range.clip(drive + turn + strafe, -1.0, 1.0);
            rightFrontPower = Range.clip(drive - turn + strafe, -1.0, 1.0);
            rightBackPower = Range.clip(drive - turn - strafe, -1.0, 1.0);
            // someVariable = 1 < 0 ? answerIfTrue : answerIfFalse;
            double leftBumperPower = gamepad1.left_bumper ? 1 : 0;
            double rightBumperPower = gamepad1.right_bumper ? 1 : 0;
            intakePower = Range.clip(gamepad1.left_trigger - leftBumperPower, -1.0, 1.0);
            outtakePower = Range.clip(gamepad1.right_trigger - rightBumperPower, -1.0, 1.0);

            
            // Send calculated power to wheels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackeDrive.setPower(leftBackPower);
            rightBackeDrive.setPower(rightBackPower);

//                // intake
            intake.setPower(intakePower);
            outtake.setPower(outtakePower);
//                    leftFrontDrive.setPower(1);
//                    rightFrontDrive.setPower(-1);
//                    leftBackeDrive.setPower(1);
//                    rightBackeDrive.setPower(-1);
//                }
//            }
            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString() + "RightY :" + gamepad1.right_stick_y + "RightX :" + gamepad1.right_stick_x);
            telemetry.addData("Motors", "left (%.2f), right (%.2f), leftt (%.2f) outtake (%.2f)", leftFrontPower, rightFrontPower, gamepad1.left_trigger, outtakePower);
            telemetry.update();
        }
    }
}

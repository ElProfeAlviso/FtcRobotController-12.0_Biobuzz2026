package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class HelloWorld extends OpMode {

    @Override
    public void init() {
        telemetry.addData("Hello", "Soy luz desde mi branch");
    }

    @Override
    public void loop() {

    }
}

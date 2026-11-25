package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BHI260IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;


@Autonomous(name="Avanza y Estaciona Hive Bajo", group="Autos",preselectTeleOp="TitaniumRamsRegional")

//Clase principal del programa (IMU utilizado modelo:
public class AvanzaEstaciona extends LinearOpMode {

    // Declarar los objetos de motores
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    private DcMotor intake;
    private DcMotor turret;
    private DcMotorEx shooterLeft;
    private DcMotorEx shooterRight;
    private DcMotor indexer;

    private Servo leftPivot;
    private Servo rightPivot;

    // Posiciones para los servomotores Pivot
    private static final double PIVOT_ACTIVE_POS = 0.65;
    private static final double PIVOT_INACTIVE_POS = 0.0;

    private final double intakePower = 0.9;
    private final double indexerPower = 0.95;

    private final double indexerLowPower = 0.6;
    private final double turretPower = 0.8;
    private final double shooterPower = 1450;


    private BHI260IMU imu;
    private Orientation angulos;


    // Definir el objetivo en pulgadas
    // Definir la circunferencia del rueda en pulgadas
    private static final double WHEEL_CIRCUMFERENCE = 3.54331* Math.PI; // Diametro de las ruedas 3.54331 pulgadas

    // Define el encoder ticks por revolution (TPR) del motor
    private static final int TICKS_PER_REV =  420;//yMark NeveRest 40

    // Arm and Wrist target positions for each state
    //Posiciones de Arm en diferentes configuraciones.






    @Override
    public void runOpMode() {
        // inicializa las variables de hardware, los strings son los nombres de parametros en la driver station.
        // La funcion get obtiene el mapeo de la configuracion de hardware en el driver station.
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Set motors to use encoders
        leftDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        //Se establece el modo de freno al estar en neutral.
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Configuración del motor Intake
        intake = hardwareMap.get(DcMotor.class, "intake");
        intake.setDirection(DcMotorSimple.Direction.FORWARD);
        intake.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        intake.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Configuración del motor Indexer (Invertido)
        indexer = hardwareMap.get(DcMotor.class, "indexer");
        indexer.setDirection(DcMotorSimple.Direction.REVERSE);
        indexer.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        indexer.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Configuración de los motores del Shooter (shooterleft y shooterright)
        shooterLeft = hardwareMap.get(DcMotorEx.class, "shooterleft");
        shooterRight = hardwareMap.get(DcMotorEx.class, "shooterright");

        // Al estar montados opuestos en el lanzador, invertimos la dirección de uno de ellos
        shooterLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        shooterRight.setDirection(DcMotorSimple.Direction.REVERSE);

        shooterLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        shooterRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

        shooterLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        shooterRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Configuración de Servos Pivot (leftpivot y rightpivot)
        leftPivot = hardwareMap.get(Servo.class, "leftPivot");
        rightPivot = hardwareMap.get(Servo.class, "rightPivot");

        leftPivot.setDirection(Servo.Direction.REVERSE);

        // Posición inicial de los servos
        leftPivot.setPosition(PIVOT_INACTIVE_POS);
        rightPivot.setPosition(PIVOT_INACTIVE_POS);




        // 2. Inicialización del IMU BHI260AP

        // Los parámetros son limitados en BHI260AP,
        // generalmente no se necesitan configuraciones adicionales.



        imu = hardwareMap.get(BHI260IMU.class, "imu");
        BHI260IMU.Parameters parametrosIMU = new BHI260IMU.Parameters(
                new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                        RevHubOrientationOnRobot.UsbFacingDirection.LEFT));


        imu.initialize(parametrosIMU);


        // Esperar a que el IMU esté listo (puede no ser necesario para BHI260AP)
        sleep(100); // Pausa corta para asegurar la inicialización

        telemetry.addData("IMU", "Inicializado");
        telemetry.update();

        // La funcion waitForStart() espera a que el boton de play sea presionado.

        waitForStart();





        //===================SECUENCIA DE COMANDOS AUTONOMOS====================================

        Adelante(30, 0.7, 1000);
       girarDerecha(70,0.7,1000);
        Adelante(60, 0.7, 1000);





        }



        //================================FUNCIONES DE INTAKE=================================










        //================================FUNCIONES DE MOVIMIENTOS=================================

      private void Adelante(double TARGET_DISTANCE ,double Power, long SLEEPTIME) {
        // Reset encoders
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Calculate target encoder ticks
        int targetTicks = (int) ((TARGET_DISTANCE / WHEEL_CIRCUMFERENCE) * TICKS_PER_REV);

        // Set target position and run to position
        leftDrive.setTargetPosition(targetTicks);
        rightDrive.setTargetPosition(targetTicks);

        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Start motors
        leftDrive.setPower(Power);
        rightDrive.setPower(Power);

        // Wait for motors to reach target position
        while (opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy())) {
            // Display telemetry data (optional)
            telemetry.addData("Target Ticks", targetTicks);
            telemetry.addData("Left Position", leftDrive.getCurrentPosition());
            telemetry.addData("Right Position", rightDrive.getCurrentPosition());
            telemetry.update();
        }

        // Stop motors
        leftDrive.setPower(0);
        rightDrive.setPower(0);

        sleep(SLEEPTIME);
    }


    private void Atras(double TARGET_DISTANCE ,double Power, long SLEEPTIME) {
        TARGET_DISTANCE = -TARGET_DISTANCE;
        // Reset encoders
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Calculate target encoder ticks
        int targetTicks = (int) ((TARGET_DISTANCE / WHEEL_CIRCUMFERENCE) * TICKS_PER_REV);

        // Set target position and run to position
        leftDrive.setTargetPosition(targetTicks);
        rightDrive.setTargetPosition(targetTicks);

        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Start motors
        leftDrive.setPower(Power);
        rightDrive.setPower(Power);

        // Wait for motors to reach target position
        while (opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy())) {
            // Display telemetry data (optional)
            telemetry.addData("Target Ticks", targetTicks);
            telemetry.addData("Left Position", leftDrive.getCurrentPosition());
            telemetry.addData("Right Position", rightDrive.getCurrentPosition());
            telemetry.update();
        }

        // Stop motors
        leftDrive.setPower(0);
        rightDrive.setPower(0);

        sleep(SLEEPTIME);
    }


    // Función para girar a la izquierda (grados positivos)
    private void girarIzquierda(double grados , double Power, long SLEEPTIME) { girar((grados - 11),Power,SLEEPTIME);}

    // Función para girar a la derecha (grados negativos)
    private void girarDerecha(double grados , double Power, long SLEEPTIME) { girar(-(grados -11) ,Power,SLEEPTIME); }

    // Función principal para realizar el giro
    private void girar(double grados, double Power, long SLEEPTIME) {

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        imu.resetYaw();
        angulos = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
        double anguloInicial = angulos.firstAngle;
        double anguloObjetivo = anguloInicial + grados;

        // Ajustar el ángulo objetivo para que esté entre -180 y 180 grados
        anguloObjetivo = anguloObjetivo % 360;
        if (anguloObjetivo > 180) {
            anguloObjetivo -= 360;
        } else if (anguloObjetivo < -180) {
            anguloObjetivo += 360;
        }

        while (opModeIsActive() && Math.abs(angulos.firstAngle - anguloObjetivo) > 2) {
            angulos = imu.getRobotOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);

            double potencia = (grados > 0 ? Power : -Power); // Ajustar la potencia según la dirección

            leftDrive.setPower(potencia);
            rightDrive.setPower(-potencia);

            telemetry.addLine("==Angulo del IMU==");
            telemetry.addData("Ángulo actual", angulos.firstAngle);
            telemetry.addData("Ángulo objetivo", anguloObjetivo);
            telemetry.addLine("**Potencias del Drive**");
            telemetry.addData("LeftDrive", leftDrive.getPower());
            telemetry.addData("RightDrive", rightDrive.getPower());
            telemetry.update();
        }


        leftDrive.setPower(0);
        rightDrive.setPower(0);

        sleep(SLEEPTIME);
    }

    private void indexerOn(long sleep){

        indexer.setPower(-indexerPower);


        sleep(sleep);

    }

    private void indexerOff(long sleep) {

        indexer.setPower(0.0);


        sleep(sleep);
    }

    private void indexerBack(long sleep){

        indexer.setPower(indexerPower);


        sleep(sleep);

    }

    private void intakeOn (long sleep){

        intake.setPower(-intakePower);

        sleep(sleep);


    }
    private void intakeOff (long sleep){

        intake.setPower(0.0);

        sleep(sleep);

    }

    private void shooterON (long sleep){

        shooterLeft.setVelocity(shooterPower);
        shooterRight.setVelocity(shooterPower);

        sleep(sleep);


    }

    private void shooterOff (long sleep){

        shooterLeft.setVelocity(0.0);
        shooterRight.setVelocity(0.0);

        sleep(sleep);




    }


}





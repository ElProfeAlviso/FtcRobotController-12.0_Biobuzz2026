package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;

@Disabled
@Autonomous(name="Auto Derecha", group="Autos", preselectTeleOp="biobuzz2026")
public class AutoDemo extends LinearOpMode {

    // Declarar los objetos de motores
    private DcMotor leftDrive = null;
    private DcMotor rightDrive = null;

    // Objeto estándar para la IMU
    private IMU imu;

    // --- CONFIGURACIÓN DE CHASSIS Y ENCODERS ---
    // Diámetro de la rueda en pulgadas (3.54331 pulg = 90mm)
    private static final double WHEEL_DIAMETER_INCHES = 3.54331;
    private static final double WHEEL_CIRCUMFERENCE = WHEEL_DIAMETER_INCHES * Math.PI;

    // Tics de encoder por revolución del motor del chasis (ej. 537.7 para GoBILDA 312 RPM)
    private static final double TICKS_PER_REV = 537.7;

    @Override
    public void runOpMode() {
        // Inicializar motores desde el hardwareMap
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        // Direcciones coincidentes con TeleOp (biobuzz2026)
        leftDrive.setDirection(DcMotor.Direction.REVERSE);
        rightDrive.setDirection(DcMotor.Direction.FORWARD);

        // Modo de freno al tener potencia cero
        leftDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rightDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reiniciar encoders al inicio
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Inicialización de la IMU estándar
        imu = hardwareMap.get(IMU.class, "imu");
        IMU.Parameters imuParameters = new IMU.Parameters(
                new RevHubOrientationOnRobot(
                        RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                        RevHubOrientationOnRobot.UsbFacingDirection.RIGHT
                )
        );
        imu.initialize(imuParameters);
        imu.resetYaw();

        telemetry.addData("Status", "IMU y Motores Inicializados");
        telemetry.update();

        // Esperar el botón de PLAY
        waitForStart();

        if (opModeIsActive()) {
            // =================== SECUENCIA DE COMANDOS AUTÓNOMOS ===================

            Adelante(10, 0.8, 100);        // Avanzar 10 pulgadas

        }
    }

    // ================================= FUNCIONES DE MOVIMIENTO =================================

    /**
     * Desplaza el chasis hacia adelante una distancia determinada en pulgadas.
     */
    private void Adelante(double targetDistanceInches, double power, long sleepTime) {
        moveDistance(targetDistanceInches, power, sleepTime);
    }

    /**
     * Desplaza el chasis hacia atrás una distancia determinada en pulgadas.
     */
    private void Atras(double targetDistanceInches, double power, long sleepTime) {
        moveDistance(-targetDistanceInches, power, sleepTime);
    }

    /**
     * Función general de desplazamiento por posición de encoder.
     */
    private void moveDistance(double targetDistanceInches, double power, long sleepTime) {
        if (!opModeIsActive()) return;

        // Reiniciar encoders
        leftDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        // Calcular tics objetivo
        int targetTicks = (int) Math.round((targetDistanceInches / WHEEL_CIRCUMFERENCE) * TICKS_PER_REV);

        leftDrive.setTargetPosition(targetTicks);
        rightDrive.setTargetPosition(targetTicks);

        leftDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        rightDrive.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        // Aplicar potencia absoluta
        double absPower = Math.abs(power);
        leftDrive.setPower(absPower);
        rightDrive.setPower(absPower);

        // Esperar a llegar a la posición
        while (opModeIsActive() && (leftDrive.isBusy() || rightDrive.isBusy())) {
            telemetry.addData("Objetivo Inches", targetDistanceInches);
            telemetry.addData("Objetivo Tics", targetTicks);
            telemetry.addData("Posición Izq Tics", leftDrive.getCurrentPosition());
            telemetry.addData("Posición Der Tics", rightDrive.getCurrentPosition());
            telemetry.update();
        }

        // Detener motores
        leftDrive.setPower(0);
        rightDrive.setPower(0);

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        sleep(sleepTime);
    }

    /**
     * Gira a la derecha el número de grados especificado.
     */
    private void girarDerecha(double grados, double power, long sleepTime) {
        girar(grados, power, sleepTime);
    }

    /**
     * Gira a la izquierda el número de grados especificado.
     */
    private void girarIzquierda(double grados, double power, long sleepTime) {
        girar(-grados, power, sleepTime);
    }

    /**
     * Giro relativo sencillo guiado por IMU (sin control PID).
     * @param grados Grados a girar (positivo = derecha, negativo = izquierda)
     */
    private void girar(double grados, double power, long sleepTime) {
        if (!opModeIsActive()) return;

        leftDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightDrive.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Reiniciar la referencia del ángulo inicial a 0°
        imu.resetYaw();

        // Bucle sencillo: gira con potencia fija hasta alcanzar la cantidad de grados deseada
        while (opModeIsActive() && Math.abs(getHeading()) < Math.abs(grados)) {
            if (grados > 0) {
                // Girar a la derecha
                leftDrive.setPower(power);
                rightDrive.setPower(-power);
            } else {
                // Girar a la izquierda
                leftDrive.setPower(-power);
                rightDrive.setPower(power);
            }

            telemetry.addData("Ángulo Actual", "%.1f°", getHeading());
            telemetry.addData("Ángulo Objetivo", "%.1f°", grados);
            telemetry.update();
        }

        // Frenar motores al alcanzar el ángulo
        leftDrive.setPower(0);
        rightDrive.setPower(0);

        sleep(sleepTime);
    }

    /**
     * Obtiene la orientación actual Yaw del robot en grados (-180° a +180°).
     */
    private double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }
}

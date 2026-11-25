package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name = "Teleop biobuzz2026", group = "Iterative Opmode")
public class biobuzz2026 extends OpMode {

    // Configuración para torreta de 360° con motor REV NEO (550)
    // El motor REV NEO 550 genera 42 tics por revolución del motor
    static final double NEO_MOTOR_TICKS_PER_REV = 42.0;
    // Relación de reducción total (Caja UltraPlanetary * Engranes de la Torreta)
    // Modificar según tu mecanismo (ejemplo: 20:1)
    static final double TURRET_GEAR_RATIO = 20.0;
    // Tics de encoder por cada grado de la torreta
    static final double TICKS_PER_DEGREE = (NEO_MOTOR_TICKS_PER_REV * TURRET_GEAR_RATIO) / 360.0;

    // Posiciones para los servomotores Pivot
    private static final double PIVOT_ACTIVE_POS = 0.65;
    private static final double PIVOT_INACTIVE_POS = 0.0;

    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DcMotor intake;
    private DcMotor turret;
    private DcMotorEx shooterLeft;
    private DcMotorEx shooterRight;
    private DcMotor indexer;

    private Servo leftPivot;
    private Servo rightPivot;

    private final double intakePower = 0.8;
    private final double indexerPower = 0.95;

    private final double indexerLowPower = 0.6;
    private final double turretPower = 0.8;
    private final double shooterPower = 1400;

    // Estado para alternar (toggle) los servos pivot con el botón triángulo
    private boolean pivotActive = false;
    private boolean trianglePressedLast = false;

    @Override
    public void init() {
        // Configuración de motores del chasis (Tanque)
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        // En un chasis tanque, los motores están montados opuestos.
        // Invertimos la dirección de uno para que ambos avancen en el mismo sentido.
        leftDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        rightDrive.setDirection(DcMotorSimple.Direction.FORWARD);

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

        // Configuración del motor Turret (NEO 550 - 360°)
        turret = hardwareMap.get(DcMotor.class, "turret");
        turret.setDirection(DcMotorSimple.Direction.FORWARD);
        turret.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        turret.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        turret.setTargetPosition(0);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);

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


        telemetry.addData("Status", "Initialized");
    }

    @Override
    public void loop() {
        // Control Tanque / Arcade:
        double leftPowerDrive = -gamepad1.left_stick_y;
        double rightPowerTurn = -gamepad1.right_stick_x;
        double leftPower = Range.clip(leftPowerDrive + rightPowerTurn, -1.0, 1.0);
        double rightPower = Range.clip(leftPowerDrive - rightPowerTurn, -1.0, 1.0);

        leftDrive.setPower(leftPower);
        rightDrive.setPower(rightPower);

        // Control del motor Intake e Indexer con el botón X (cross) del gamepad
        if (gamepad1.left_trigger_pressed) {
            intake.setPower(-intakePower);
            indexer.setPower(-indexerLowPower);
        } else if (gamepad1.right_bumper) {
            intake.setPower(intakePower);
            indexer.setPower(indexerPower);
        }else if (gamepad1.right_trigger_pressed) {
            intake.setPower(-intakePower);
            indexer.setPower(-indexerPower);
        }
        else {
            intake.setPower(0.0);
            indexer.setPower(0.0);
        }



        // Control de la Torreta 360° con el D-pad (Ángulos en Grados)
        // dpad_up: 0° (Frente), dpad_right: 90° (Derecha), dpad_down: 180° (Atrás), dpad_left: 270° (Izquierda)
        if (gamepad1.dpad_up) {
            setTurretAngleDegrees(0.0);
        } else if (gamepad1.dpad_right) {
            setTurretAngleDegrees(-80.0);
        } else if (gamepad1.dpad_down) {
            setTurretAngleDegrees(0);
        } else if (gamepad1.dpad_left) {
            setTurretAngleDegrees(80.0);
        }

        // Control del Shooter: cuadrado para encender, círculo para apagar
        if (gamepad1.triangle) {
            shooterLeft.setVelocity(shooterPower);
            shooterRight.setVelocity(shooterPower);
        } else if (gamepad1.ps) {
            shooterLeft.setPower(-shooterPower);
            shooterRight.setPower(-shooterPower);
        }else if (gamepad1.circle){
            shooterLeft.setPower(0.0);
            shooterRight.setPower(0.0);
        }

        // Control de alternancia (toggle) para los servos Pivot con el botón Cuadrado (Square)
        boolean trianglePressedCurrent = gamepad1.square;
        if (trianglePressedCurrent && !trianglePressedLast) {
            pivotActive = !pivotActive; // Cambia de activo a inactivo y viceversa
        }
        trianglePressedLast = trianglePressedCurrent;

        // Asignar posición a los servos según el estado activo/inactivo
        if (pivotActive) {
            leftPivot.setPosition(PIVOT_ACTIVE_POS);
            rightPivot.setPosition(PIVOT_ACTIVE_POS);
        } else {
            leftPivot.setPosition(PIVOT_INACTIVE_POS);
            rightPivot.setPosition(PIVOT_INACTIVE_POS);
        }



        // Telemetría en la Driver Station
        telemetry.addData("Left Drive Power", leftPower);
        telemetry.addData("Right Drive Power", rightPower);
        telemetry.addData("Intake Power", intake.getPower());
        telemetry.addData("Indexer Power", indexer.getPower());
        telemetry.addData("Turret Angle (deg)", "%.1f°", turret.getCurrentPosition() / TICKS_PER_DEGREE);
        telemetry.addData("Turret Target Tics", turret.getTargetPosition());
        telemetry.addData("Shooter Left Velocity", shooterLeft.getVelocity());
        telemetry.addData("Shooter Right Velocity", shooterRight.getVelocity());
        telemetry.addData("Pivot Active", pivotActive);
        telemetry.addData("Left Pivot Pos", leftPivot.getPosition());
        telemetry.addData("Right Pivot Pos", rightPivot.getPosition());
    }

    /**
     * Establece el ángulo objetivo de la torreta en grados (0° a 360°)
     */
    private void setTurretAngleDegrees(double degrees) {
        int targetTicks = (int) Math.round(degrees * TICKS_PER_DEGREE);
        turret.setTargetPosition(targetTicks);
        turret.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        turret.setPower(turretPower);
    }
}


package rtandroid.ballsort.blocks;

import rtandroid.ballsort.hardware.Stepper;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;
import rtandroid.ballsort.util.Utils;

public class SlingshotMotor extends AStateBlock
{
    private static final int[] ROTATE_PATTERN = { Stepper.WHILE_OPENED, Stepper.WHILE_CLOSED, 35 };
    private final Stepper mStepper;

    public SlingshotMotor()
    {
        super("SlingshotMotor", Constants.THREAD_BLOCK_PRIORITY);
        int stepperEnablePinID = Constants.SLINGSHOT_MOTOR_PIN_ENABLE;
        int stepperStepPinID = Constants.SLINGSHOT_MOTOR_PIN_STEP;
        int stepperDelay = SettingsManager.getSettings().SlingshotStepperPwmDelay;
        int stepperReferencePinID = Constants.SLINGSHOT_MOTOR_PIN_REF;
        mStepper = new Stepper("SlingshotMotorStepper", stepperEnablePinID, stepperStepPinID, stepperDelay, stepperReferencePinID);
    }

    public void allow()
    {
        mStepper.emergencyWait(false);
    }

    public void forbid()
    {
        mStepper.emergencyWait(true);
    }

    @Override
    protected void cancel()
    {
        super.cancel();
        mStepper.cancel();
    }

    @Override
    protected void cleanup()
    {
        super.cleanup();
        mStepper.cleanup();
    }

    @Override
    protected void handleState()
    {
        Settings settings = SettingsManager.getSettings();
        DataState data = SettingsManager.getData();

        if (mStepper.isEmergencyWaiting())
        {
            data.SlingshotMotorState = "STOPPED";
        }
        else
        {
            data.SlingshotMotorState = "ROTATING";
            mStepper.doSteps(ROTATE_PATTERN);
            data.SlingshotMotorState = "WAITING";
            Utils.delayMs(settings.SlingshotDelayAfterRotate);
        }
    }
}

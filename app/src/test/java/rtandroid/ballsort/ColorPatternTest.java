package rtandroid.ballsort;

import android.graphics.Color;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;

import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.hardware.MockOutputPin;
import rtandroid.ballsort.blocks.color.ColorPattern;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

import static org.junit.Assert.*;

public class ColorPatternTest extends ColorPattern
{
    @Test
    public void testGetState()
    {
        // this should work for all possible states
        for (PatternState state : PatternState.values())
        {
            mState = state;
            assertEquals(getPatternState(), state);
        }
    }

    /**
     * When the filling is empty, it should not be reported as full.
     */
    @Test
    public void isFullWhenEmpty()
    {
        mFillings = new int[Constants.PATTERN_COLUMNS_COUNT];
        assertFalse(isFull());
    }

    /**
     * Fill the Pattern and test if reported as full
     */
    @Test
    public void isFullWhenFull()
    {
        Settings settings = SettingsManager.getSettings();
        mFillings = new int[Constants.PATTERN_COLUMNS_COUNT];

        // Create MAGENTA dummy Pattern
        for (int[] row : settings.Pattern){ Arrays.fill(row, Color.BLUE); }
        assertFalse(isFull());

        // Force BUILDING State
        mState = PatternState.BUILDING;

        for (int i = 0; i < Constants.PATTERN_COLUMNS_COUNT * Constants.PATTERN_COLUMNS_SIZE; i++)
        {
            assertThat(getNextColumn(ColorData.MAGENTA), not(-1));
        }
        assertTrue(isFull());
    }

    /**
     * Test if ignoring works
     */
    @Test
    public void getNextRowWhenIgnoring()
    {
        Settings settings = SettingsManager.getSettings();
        mFillings = new int[Constants.PATTERN_COLUMNS_COUNT];

        // whole array is SKIP
        for (int[] row : settings.Pattern){Arrays.fill(row, 1);}

        assertFalse(isFull());
    }

    @Test
    public void stateResetting()
    {
        mBottomPins = new MockOutputPin();

        // Simulate a filled Pattern
        Arrays.fill(mFillings, Constants.PATTERN_COLUMNS_SIZE);
        assertTrue(isFull());

        SettingsManager.getSettings().FallDownDelay = 0;
        mState = PatternState.RESETTING;
        handleState();

        //Filling should be empty after calling, pins went down
        assertThat(mFillings, is(new int[Constants.PATTERN_COLUMNS_COUNT]));
        assertTrue(((MockOutputPin)mBottomPins).mWasClosed);
        assertFalse(((MockOutputPin)mBottomPins).mWasOpened);

        // We should now be in the IGNORING state
        assertEquals(mState, PatternState.IGNORING);
    }

    @Test
    public void stateIgnoring()
    {
        mBottomPins = new MockOutputPin();

        mState = PatternState.IGNORING;
        // Ignore Balls
        while(mState == PatternState.IGNORING) {handleState();}

        assertTrue(((MockOutputPin)mBottomPins).mWasOpened);
        assertFalse(((MockOutputPin)mBottomPins).mWasClosed);

    }

    @Test
    public void stateBuilding()
    {
        Settings settings = SettingsManager.getSettings();
        mFillings = new int[Constants.PATTERN_COLUMNS_COUNT];

        //Create MAGENTA dummy Pattern
        for (int[] row : settings.Pattern){ Arrays.fill(row, Color.BLUE); }
        assertFalse(isFull());

        //Force BUILDING State
        settings.StateDelay = 0;
        mState = PatternState.BUILDING;
    }

    @Test
    public void stateFull()
    {
        mState = PatternState.FULL;
        handleState();

        // no change should have occured
        assertEquals(mState, PatternState.FULL);

        // external call when full
        resetPattern();
        assertEquals(mState, PatternState.RESETTING);
    }
}

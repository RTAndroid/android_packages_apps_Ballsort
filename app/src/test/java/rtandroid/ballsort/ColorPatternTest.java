package rtandroid.ballsort;

import android.graphics.Color;

import org.junit.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;

import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.blocks.color.ColorPattern;
import rtandroid.ballsort.settings.Constants;
import rtandroid.ballsort.settings.Settings;
import rtandroid.ballsort.settings.SettingsManager;

import static org.junit.Assert.*;

public class ColorPatternTest extends ColorPattern
{
    /**
     * When the filling is empty, it should not be reported as full.
     */
    @Test
    public void isFullWhenEmpty()
    {
        mFillings = new int[Constants.PATTERN_COLUMN_COUNT];
        assertFalse(isFull());
    }

    /**
     * Fill the Pattern and test if reported as full
     */
    @Test
    public void isFullWhenFull()
    {
        Settings settings = SettingsManager.getSettings();
        mFillings = new int[Constants.PATTERN_COLUMN_COUNT];

        // Create dummy pattern
        for (ColorType[] row : settings.Pattern){ Arrays.fill(row, Color.BLUE); }
        assertFalse(isFull());

        for (int i = 0; i < Constants.PATTERN_COLUMN_COUNT * Constants.PATTERN_COLUMN_CAPACITY; i++)
        {
            assertThat(getNextColumn(ColorType.BLACK), not(ColorType.EMPTY));
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
        mFillings = new int[Constants.PATTERN_COLUMN_COUNT];

        // whole array is SKIP
        for (ColorType[] row : settings.Pattern){Arrays.fill(row, ColorType.EMPTY);}

        assertFalse(isFull());
    }
}

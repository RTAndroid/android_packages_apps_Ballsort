package rtandroid.ballsort.blocks.color;

import android.graphics.Color;

import rtandroid.ballsort.blocks.color.space.ColorHSV;
import rtandroid.ballsort.blocks.color.space.ColorLAB;
import rtandroid.ballsort.blocks.color.space.ColorRGB;

/**
 * Converting between two non-absolute color spaces (RGB -> CMYK) or between absolute and non-absolute color spaces (RGB -> L*a*b*) can be meaningless.
 * It should still be evaluated for producing better detection results.
 */
public class ColorConverter
{
    public static ColorHSV rgb2hsv(ColorRGB color)
    {
        float[] hsv = new float[3];
        Color.RGBToHSV(color.R, color.G, color.B, hsv);

        return new ColorHSV((int) hsv[0], hsv[1], hsv[2]);
    }

    private static double normalizeLab(double v)
    {
        if (v > 0.008856) { v = Math.pow(v, 1.0 / 3.0); }
                     else { v = (7.787 * v) + (16.0 / 116.0); }

        return v;
    }

    public static ColorLAB rgb2lab(ColorRGB color)
    {
        double[] whitePointChromaD65 = {0.3127, 0.3290, 100.0};

        double x = color.R / whitePointChromaD65[0];
        double y = color.G / whitePointChromaD65[1];
        double z = color.B / whitePointChromaD65[2];

        normalizeLab(x);
        normalizeLab(y);
        normalizeLab(z);

        double l = (116.0 * y) - 16.0;
        double a = 500.0 * (x - y);
        double b = 200.0 * (y - z);

        return new ColorLAB(l, a, b);
    }
}

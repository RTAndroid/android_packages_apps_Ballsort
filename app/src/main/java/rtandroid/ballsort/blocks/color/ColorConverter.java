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
        Color.RGBToHSV(color.R / 256, color.G / 256, color.B / 256, hsv);
        return new ColorHSV((int) hsv[0], hsv[1], hsv[2]);
    }

    public static ColorLAB rgb2lab(ColorRGB color)
    {
        double R = color.R / 256 / 255.0;
        double G = color.G / 256 / 255.0;
        double B = color.B / 256 / 255.0;

        double x = 0.4124564 * R + 0.3575761 * G + 0.1804375 * B;
        double y = 0.2126729 * R + 0.7151522 * G + 0.0721750 * B;
        double z = 0.0193339 * R + 0.1191920 * G + 0.9503041 * B;

        double l = 0 + y;
        double a = x - y;
        double b = y - z;

        return new ColorLAB(l, a, b);
    }
}

package rtandroid.ballsort.blocks.color.classifier;

import rtandroid.ballsort.blocks.color.ColorRGB;
import rtandroid.ballsort.blocks.color.ColorType;

public interface IColorClassifier
{
    String getName();
    ColorType classify(ColorRGB color);
}

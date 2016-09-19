package rtandroid.ballsort.blocks.color.classifier;

import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.space.ColorRGB;

public interface IColorClassifier
{
    String getName();
    ColorData classify(ColorRGB color);
}

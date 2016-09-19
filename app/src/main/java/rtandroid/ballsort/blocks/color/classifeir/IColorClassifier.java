package rtandroid.ballsort.blocks.color.classifeir;

import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.space.ColorRGB;

public interface IColorClassifier
{
    String getName();
    ColorData classify(ColorRGB color);
}

package rtandroid.ballsort.blocks.color.classifier;

import rtandroid.ballsort.blocks.color.ColorObject;
import rtandroid.ballsort.blocks.color.ColorType;

public interface IColorClassifier
{
    String getName();
    ColorType classify(ColorObject color);
}

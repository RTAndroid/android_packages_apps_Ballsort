package rtandroid.ballsort.blocks.color.Classifier;


import android.util.Log;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.ColorRGB;

public class ClassifierManager
{
    private enum Classifier
    {
        TREE,
        MEAN;
    }

    public static Classifier mCurrent = Classifier.MEAN;

    public static ColorData classify(ColorRGB color)
    {
        Log.d(MainActivity.TAG, "Classifing using: "+mCurrent.name());
        switch (mCurrent)
        {
            case TREE:
                return ColorTreeClassifier.classify(color);

            case MEAN:
                return ColorMeanClassifier.classify(color);
        }
        return null;
    }

}

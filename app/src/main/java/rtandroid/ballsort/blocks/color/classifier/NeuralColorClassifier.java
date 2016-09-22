/*
 * Copyright (C) 2016 RTAndroid Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package rtandroid.ballsort.blocks.color.classifier;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorType;

public class NeuralColorClassifier implements IColorClassifier
{
    private static final String NETWORK_FILENAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/colors.nnet";
    private static final int INPUT_COUNT = (2 + 2 + 2) + (1 + 1 + 1);          // RGB + HSV;
    private static final int OUTPUT_COUNT = ColorType.values().length - 1;     // don't detect EMPTY
    private static final int HIDDEN_NEURONS = INPUT_COUNT * OUTPUT_COUNT * 10; // random guess

    private static final double[][] BLACK_DATA = new double[][]
    {
            {0.00392156862745098,0.9647058823529412,0.00392156862745098,0.8745098039215686,0.00392156862745098,0.6,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.9686274509803922,0.00392156862745098,0.9019607843137255,0.00392156862745098,0.6,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7764705882352941,0.00392156862745098,0.6901960784313725,0.00392156862745098,0.4470588235294118,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7529411764705882,0.00392156862745098,0.6666666666666666,0.00392156862745098,0.43137254901960786,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.8196078431372549,0.00392156862745098,0.7725490196078432,0.00392156862745098,0.5058823529411764,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7372549019607844,0.00392156862745098,0.6627450980392157,0.00392156862745098,0.4196078431372549,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7843137254901961,0.00392156862745098,0.7254901960784313,0.00392156862745098,0.4666666666666667,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7137254901960784,0.00392156862745098,0.6666666666666666,0.00392156862745098,0.4235294117647059,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7137254901960784,0.00392156862745098,0.6549019607843137,0.00392156862745098,0.4117647058823529,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.6862745098039216,0.00392156862745098,0.6313725490196078,0.00392156862745098,0.396078431372549,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.611764705882353, 0.00392156862745098,0.6274509803921569,0.00392156862745098,0.4196078431372549,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.4980392156862745,0.00392156862745098,0.4980392156862745,0.00392156862745098,0.29411764705882354,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.796078431372549, 0.00392156862745098,0.7019607843137254,0.00392156862745098,0.45098039215686275,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7725490196078432,0.00392156862745098,0.6745098039215687,0.00392156862745098,0.43137254901960786,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7686274509803922,0.00392156862745098,0.6784313725490196,0.00392156862745098,0.43529411764705883,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7490196078431373,0.00392156862745098,0.6627450980392157,0.00392156862745098,0.4235294117647059,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7568627450980392,0.00392156862745098,0.6705882352941176,0.00392156862745098,0.43137254901960786,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.803921568627451, 0.00392156862745098,0.7294117647058823,0.00392156862745098,0.47058823529411764,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7450980392156863,0.00392156862745098,0.6745098039215687,0.00392156862745098,0.43137254901960786,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7529411764705882,0.00392156862745098,0.6862745098039216,0.00392156862745098,0.44313725490196076,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.7764705882352941,0.00392156862745098,0.7215686274509804,0.00392156862745098,0.4666666666666667,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.39215686274509803,0.00392156862745098,0.4117647058823529,0.00392156862745098,0.16862745098039217,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.25882352941176473,0.00392156862745098,0.25098039215686274,0.00392156862745098,0.050980392156862744,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.43137254901960786,0.00392156862745098,0.4196078431372549,0.00392156862745098,0.2,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.3254901960784314,0.00392156862745098,0.32941176470588235,0.00392156862745098,0.12156862745098039,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.4196078431372549,0.00392156862745098,0.2784313725490196,0.00392156862745098,0.06666666666666667,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.403921568627451,0.00392156862745098,0.29411764705882354,0.00392156862745098,0.09019607843137255,0.0,0.0,0.003921568859368563,},
            {0.00392156862745098,0.6235294117647059,0.00392156862745098,0.4823529411764706,0.00392156862745098,0.21568627450980393,0.0,0.0,0.003921568859368563,},
    };

    private static final double[][] BLUE_DATA = new double[][]
    {
    };

    private static final double[][] GREEN_DATA = new double[][]
    {
            {0.00784313725490196,0.9882352941176471,0.011764705882352941,0.5882352941176471,0.00784313725490196,0.44313725490196076,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.6588235294117647,0.011764705882352941,0.11372549019607843,0.00784313725490196,0.13725490196078433,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.9215686274509803,0.011764705882352941,0.5490196078431373,0.00784313725490196,0.3764705882352941,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.7450980392156863,0.011764705882352941,0.24313725490196078,0.00784313725490196,0.19607843137254902,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.8431372549019608,0.011764705882352941,0.4666666666666667,0.00784313725490196,0.3411764705882353,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.48627450980392156,0.00784313725490196,0.9411764705882353,0.00784313725490196,0.03137254901960784,0.0,0.0,0.007843137718737125,},
            {0.00784313725490196,0.5333333333333333,0.00784313725490196,0.9882352941176471,0.00784313725490196,0.054901960784313725,0.0,0.0,0.007843137718737125,},
            {0.00784313725490196,0.5176470588235295,0.00784313725490196,0.996078431372549,0.00784313725490196,0.07450980392156863,0.0,0.0,0.007843137718737125,},
            {0.00784313725490196,0.4823529411764706,0.00784313725490196,0.9568627450980393,0.00784313725490196,0.07058823529411765,0.0,0.0,0.007843137718737125,},
            {0.00784313725490196,0.5137254901960784,0.00784313725490196,0.9921568627450981,0.00784313725490196,0.07450980392156863,0.0,0.0,0.007843137718737125,},
            {0.00784313725490196,0.7725490196078432,0.011764705882352941,0.23137254901960785,0.00784313725490196,0.2,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.7450980392156863,0.011764705882352941,0.20392156862745098,0.00784313725490196,0.16470588235294117,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.807843137254902,0.011764705882352941,0.27450980392156865,0.00784313725490196,0.20784313725490197,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.6235294117647059,0.011764705882352941,0.09411764705882353,0.00784313725490196,0.12549019607843137,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.6274509803921569,0.011764705882352941,0.11764705882352941,0.00784313725490196,0.1450980392156863,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.5843137254901961,0.011764705882352941,0.03137254901960784,0.00784313725490196,0.09803921568627451,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.6980392156862745,0.011764705882352941,0.1568627450980392,0.00784313725490196,0.1450980392156863,0.3333333333333333,0.3333333432674408,0.0117647061124444,},
            {0.00784313725490196,0.5215686274509804,0.00784313725490196,0.9882352941176471,0.00784313725490196,0.06666666666666667,0.0,0.0,0.007843137718737125,},
            {0.00784313725490196,0.023529411764705882,0.00784313725490196,0.4392156862745098,0.00392156862745098,0.6627450980392157,0.16666666666666666,0.5,0.007843137718737125,},
            {0.00784313725490196,0.10588235294117647,0.00784313725490196,0.5411764705882353,0.00392156862745098,0.7254901960784313,0.16666666666666666,0.5,0.007843137718737125,},
            {0.00784313725490196,0.26666666666666666,0.00784313725490196,0.5803921568627451,0.00392156862745098,0.7450980392156863,0.16666666666666666,0.5,0.007843137718737125,},
            {0.00784313725490196,0.2784313725490196,0.00784313725490196,0.596078431372549,0.00392156862745098,0.7450980392156863,0.16666666666666666,0.5,0.007843137718737125,},
            {0.0392156862745098,0.43529411764705883,0.043137254901960784,0.403921568627451,0.03137254901960784,0.03137254901960784,0.22222220102945964,0.27272728085517883,0.04313725605607033,},
            {0.00784313725490196,0.27450980392156865,0.00784313725490196,0.6,0.00392156862745098,0.7450980392156863,0.16666666666666666,0.5,0.007843137718737125,},
    };

    private static final double[][] RED_DATA = new double[][]
    {
            {0.01568627450980392,0.8313725490196079,0.00784313725490196,0.058823529411764705,0.00392156862745098,0.7490196078431373,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.6901960784313725,0.00784313725490196,0.0392156862745098,0.00392156862745098,0.7333333333333333,0.05555555555555555,0.75,0.01568627543747425,},
            {0.0196078431372549,0.1568627450980392,0.00784313725490196,0.19215686274509805,0.00392156862745098,0.8392156862745098,0.041666666666666664,0.800000011920929,0.019607843831181526,},
            {0.01568627450980392,0.5882352941176471,0.00784313725490196,0.027450980392156862,0.00392156862745098,0.7254901960784313,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.7372549019607844,0.00784313725490196,0.0784313725490196,0.00392156862745098,0.7490196078431373,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.6509803921568628,0.00784313725490196,0.03137254901960784,0.00392156862745098,0.7294117647058823,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.5411764705882353,0.00784313725490196,0.03137254901960784,0.00392156862745098,0.7176470588235294,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.4627450980392157,0.00784313725490196,0.0196078431372549,0.00392156862745098,0.7058823529411765,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.6392156862745098,0.00784313725490196,0.10196078431372549,0.00392156862745098,0.7490196078431373,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.9176470588235294,0.00784313725490196,0.13333333333333333,0.00392156862745098,0.8235294117647058,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.611764705882353,0.00784313725490196,0.06274509803921569,0.00392156862745098,0.7411764705882353,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.44313725490196076,0.00784313725490196,0.011764705882352941,0.00392156862745098,0.7058823529411765,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.7450980392156863,0.00784313725490196,0.17647058823529413,0.00392156862745098,0.7607843137254902,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.30980392156862746,0.00784313725490196,0.00784313725490196,0.00392156862745098,0.6980392156862745,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.2549019607843137,0.00392156862745098,0.996078431372549,0.00392156862745098,0.6862745098039216,0.0,0.75,0.01568627543747425,},
            {0.01568627450980392,0.45098039215686275,0.00784313725490196,0.06666666666666667,0.00392156862745098,0.7450980392156863,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.2784313725490196,0.00784313725490196,0.01568627450980392,0.00392156862745098,0.6941176470588235,0.05555555555555555,0.75,0.01568627543747425,},
            {0.01568627450980392,0.047058823529411764,0.00392156862745098,0.9411764705882353,0.00392156862745098,0.6470588235294118,0.0,0.75,0.01568627543747425,},
            {0.01568627450980392,0.17254901960784313,0.00392156862745098,0.984313725490196,0.00392156862745098,0.6705882352941176,0.0,0.75,0.01568627543747425,},
            {0.0196078431372549,0.15294117647058825,0.00784313725490196,0.20784313725490197,0.00392156862745098,0.8117647058823529,0.041666666666666664,0.800000011920929,0.019607843831181526,},
            {0.011764705882352941,0.4549019607843137,0.00392156862745098,0.6784313725490196,0.00392156862745098,0.4,0.0,0.6666666865348816,0.0117647061124444,},
            {0.0196078431372549,0.403921568627451,0.00784313725490196,0.054901960784313725,0.00392156862745098,0.7294117647058823,0.041666666666666664,0.800000011920929,0.019607843831181526,},
            {0.01568627450980392,0.8509803921568627,0.00392156862745098,0.8784313725490196,0.00392156862745098,0.5725490196078431,0.0,0.75,0.01568627543747425,},
            {0.03137254901960784,0.9254901960784314,0.03137254901960784,0.6705882352941176,0.011764705882352941,0.9215686274509803,0.16666666666666666,0.625,0.0313725508749485,},
            {0.01568627450980392,0.20784313725490197,0.00392156862745098,0.6627450980392157,0.00392156862745098,0.4,0.0,0.75,0.01568627543747425,},
    };

    private static final double[][] YELLOW_DATA = new double[][]
    {
            {0.023529411764705882,0.9411764705882353,0.027450980392156862,0.12941176470588237,0.011764705882352941,0.403921568627451,0.20833333333333334,0.5714285969734192,0.027450980618596077,},
            {0.023529411764705882,0.8509803921568627,0.027450980392156862,0.07058823529411765,0.011764705882352941,0.4,0.20833333333333334,0.5714285969734192,0.027450980618596077,},
            {0.023529411764705882,0.7294117647058823,0.023529411764705882,0.9294117647058824,0.011764705882352941,0.34509803921568627,0.16666666666666666,0.5,0.0235294122248888,},
            {0.023529411764705882,0.403921568627451,0.023529411764705882,0.6431372549019608,0.011764705882352941,0.22745098039215686,0.16666666666666666,0.5,0.0235294122248888,},
            {0.027450980392156862,0.0784313725490196,0.027450980392156862,0.30196078431372547,0.011764705882352941,0.4745098039215686,0.16666666666666666,0.5714285969734192,0.027450980618596077,},
            {0.023529411764705882,0.7803921568627451,0.027450980392156862,0.00784313725490196,0.011764705882352941,0.37254901960784315,0.20833333333333334,0.5714285969734192,0.027450980618596077,},
            {0.023529411764705882,0.7647058823529411,0.023529411764705882,1.0,0.011764705882352941,0.33725490196078434,0.16666666666666666,0.5,0.0235294122248888,},
            {0.023529411764705882,0.7843137254901961,0.027450980392156862,0.08235294117647059,0.011764705882352941,0.4117647058823529,0.20833333333333334,0.5714285969734192,0.027450980618596077,},
            {0.027450980392156862,0.43529411764705883,0.027450980392156862,0.9019607843137255,0.011764705882352941,0.7137254901960784,0.16666666666666666,0.5714285969734192,0.027450980618596077,},
            {0.027450980392156862,0.8862745098039215,0.03137254901960784,0.06274509803921569,0.011764705882352941,0.7686274509803922,0.2,0.625,0.0313725508749485,},
            {0.0196078431372549,0.8549019607843137,0.023529411764705882,0.24705882352941178,0.011764705882352941,0.0196078431372549,0.22222220102945964,0.5,0.0235294122248888,},
            {0.0196078431372549,0.03529411764705882,0.0196078431372549,0.2901960784313726,0.00784313725490196,0.6509803921568628,0.16666666666666666,0.6000000238418579,0.019607843831181526,},
            {0.01568627450980392,0.9254901960784314,0.0196078431372549,0.1568627450980392,0.00784313725490196,0.5882352941176471,0.22222220102945964,0.6000000238418579,0.019607843831181526,},
            {0.01568627450980392,0.9803921568627451,0.0196078431372549,0.19215686274509805,0.00784313725490196,0.5725490196078431,0.22222220102945964,0.6000000238418579,0.019607843831181526,},
            {0.01568627450980392,0.9568627450980393,0.0196078431372549,0.19607843137254902,0.00784313725490196,0.5882352941176471,0.22222220102945964,0.6000000238418579,0.019607843831181526,},
            {0.01568627450980392,0.6941176470588235,0.01568627450980392,0.9568627450980393,0.00784313725490196,0.4980392156862745,0.16666666666666666,0.5,0.01568627543747425,},
            {0.01568627450980392,0.6274509803921569,0.01568627450980392,0.8862745098039215,0.00784313725490196,0.4666666666666667,0.16666666666666666,0.5,0.01568627543747425,},
            {0.01568627450980392,0.7294117647058823,0.01568627450980392,0.9803921568627451,0.00784313725490196,0.5058823529411764,0.16666666666666666,0.5,0.01568627543747425,},
            {0.01568627450980392,0.4745098039215686,0.01568627450980392,0.7450980392156863,0.00784313725490196,0.403921568627451,0.16666666666666666,0.5,0.01568627543747425,},
            {0.0196078431372549,0.3137254901960784,0.0196078431372549,0.5843137254901961,0.00784313725490196,0.6627450980392157,0.16666666666666666,0.6000000238418579,0.019607843831181526,},
            {0.0196078431372549,0.12156862745098039,0.0196078431372549,0.3843137254901961,0.00784313725490196,0.596078431372549,0.16666666666666666,0.6000000238418579,0.019607843831181526,},
            {0.023529411764705882,0.9176470588235294,0.023529411764705882,0.615686274509804,0.011764705882352941,0.10196078431372549,0.16666666666666666,0.5,0.0235294122248888,},
            {0.03137254901960784,0.4588235294117647,0.03137254901960784,0.18823529411764706,0.011764705882352941,0.7411764705882353,0.16666666666666666,0.625,0.0313725508749485,},
            {0.00392156862745098,0.5803921568627451,0.00392156862745098,0.45098039215686275,0.00392156862745098,0.19215686274509805,0.0,0.0,0.003921568859368563,},
            {0.023529411764705882,0.8862745098039215,0.023529411764705882,0.592156862745098,0.011764705882352941,0.0784313725490196,0.16666666666666666,0.5,0.0235294122248888,},
    };

    private static final double[][] WHITE_DATA = new double[][]
    {
            {0.0196078431372549,0.6980392156862745,0.023529411764705882,0.792156862745098,0.01568627450980392,0.8784313725490196,0.25,0.3333333432674408,0.0235294122248888,},
            {0.03137254901960784,0.30980392156862746,0.03529411764705882,0.6078431372549019,0.023529411764705882,0.807843137254902,0.22222220102945964,0.3333333432674408,0.03529411926865578,},
            {0.03137254901960784,0.7176470588235294,0.0392156862745098,0.043137254901960784,0.027450980392156862,0.03529411764705882,0.2777777777777778,0.30000001192092896,0.03921568766236305,},
            {0.03137254901960784,0.803921568627451,0.0392156862745098,0.3137254901960784,0.027450980392156862,0.2235294117647059,0.2777777777777778,0.30000001192092896,0.03921568766236305,},
            {0.03529411764705882,0.9019607843137255,0.043137254901960784,0.7294117647058823,0.03137254901960784,0.23137254901960785,0.2777777777777778,0.27272728085517883,0.04313725605607033,},
            {0.03137254901960784,0.6,0.03529411764705882,0.807843137254902,0.023529411764705882,0.9568627450980393,0.22222220102945964,0.3333333432674408,0.03529411926865578,},
            {0.03137254901960784,0.23921568627450981,0.03529411764705882,0.5215686274509804,0.023529411764705882,0.6588235294117647,0.22222220102945964,0.3333333432674408,0.03529411926865578,},
            {0.03137254901960784,0.8352941176470589,0.0392156862745098,0.2627450980392157,0.027450980392156862,0.21568627450980393,0.2777777777777778,0.30000001192092896,0.03921568766236305,},
            {0.03137254901960784,0.0196078431372549,0.03529411764705882,0.3058823529411765,0.023529411764705882,0.6392156862745098,0.22222220102945964,0.3333333432674408,0.03529411926865578,},
            {0.03137254901960784,0.45098039215686275,0.03529411764705882,0.8470588235294118,0.023529411764705882,0.9098039215686274,0.22222220102945964,0.3333333432674408,0.03529411926865578,},
            {0.03529411764705882,0.18823529411764706,0.0392156862745098,0.8352941176470589,0.027450980392156862,0.5529411764705883,0.22222220102945964,0.30000001192092896,0.03921568766236305,},
            {0.0392156862745098,0.6313725490196078,0.047058823529411764,0.16862745098039217,0.03137254901960784,0.5764705882352941,0.25,0.3333333432674408,0.0470588244497776,},
            {0.03529411764705882,0.6352941176470588,0.0392156862745098,0.8901960784313725,0.027450980392156862,0.6627450980392157,0.22222220102945964,0.30000001192092896,0.03921568766236305,},
            {0.03529411764705882,0.47843137254901963,0.0392156862745098,0.6666666666666666,0.027450980392156862,0.5098039215686274,0.22222220102945964,0.30000001192092896,0.03921568766236305,},
            {0.03529411764705882,0.8235294117647058,0.043137254901960784,0.11372549019607843,0.027450980392156862,0.8470588235294118,0.25,0.3636363744735718,0.04313725605607033,},
            {0.03529411764705882,0.6941176470588235,0.043137254901960784,0.011764705882352941,0.027450980392156862,0.7725490196078432,0.25,0.3636363744735718,0.04313725605607033,},
            {0.03529411764705882,0.43137254901960786,0.0392156862745098,0.7647058823529411,0.027450980392156862,0.6666666666666666,0.22222220102945964,0.30000001192092896,0.03921568766236305,},
            {0.03529411764705882,0.6392156862745098,0.0392156862745098,0.9607843137254902,0.027450980392156862,0.7333333333333333,0.22222220102945964,0.30000001192092896,0.03921568766236305,},
            {0.03529411764705882,0.34901960784313724,0.0392156862745098,0.6313725490196078,0.027450980392156862,0.5333333333333333,0.22222220102945964,0.30000001192092896,0.03921568766236305,},
            {0.023529411764705882,0.9176470588235294,0.03137254901960784,0.050980392156862744,0.0196078431372549,0.6901960784313725,0.2777777777777778,0.375,0.0313725508749485,},
            {0.027450980392156862,0.36470588235294116,0.03137254901960784,0.8156862745098039,0.023529411764705882,0.21176470588235294,0.25,0.25,0.0313725508749485,},
            {0.01568627450980392,0.5490196078431373,0.0196078431372549,0.34509803921568627,0.011764705882352941,0.8470588235294118,0.25,0.4000000059604645,0.019607843831181526,},
            {0.01568627450980392,0.5647058823529412,0.0196078431372549,0.43137254901960786,0.011764705882352941,0.9176470588235294,0.25,0.4000000059604645,0.019607843831181526,},
            {0.03137254901960784,0.592156862745098,0.03529411764705882,0.2196078431372549,0.023529411764705882,0.5803921568627451,0.22222220102945964,0.3333333432674408,0.03529411926865578,},
            {0.0392156862745098,0.37254901960784315,0.043137254901960784,0.35294117647058826,0.03137254901960784,0.1450980392156863,0.22222220102945964,0.27272728085517883,0.04313725605607033,},
            {0.01568627450980392,0.1843137254901961,0.00392156862745098,0.6196078431372549,0.00392156862745098,0.3803921568627451,0.0,0.75,0.01568627543747425,},
            {0.00784313725490196,0.20784313725490197,0.00784313725490196,0.5098039215686274,0.00392156862745098,0.6705882352941176,0.16666666666666666,0.5,0.007843137718737125,},
    };

    private static final HashMap<ColorType, double[][]> sTrainingData = new HashMap<>();
    static
    {
        sTrainingData.put(ColorType.BLACK,  BLACK_DATA);
        sTrainingData.put(ColorType.BLUE,   BLUE_DATA);
        sTrainingData.put(ColorType.GREEN,  GREEN_DATA);
        sTrainingData.put(ColorType.RED,    RED_DATA);
        sTrainingData.put(ColorType.YELLOW, YELLOW_DATA);
        sTrainingData.put(ColorType.WHITE,  WHITE_DATA);
    }

    private static double[] createInputs(int r, int g, int b)
    {
        double[] result = new double[INPUT_COUNT];

        // fill the normalized RGB data
        result[0] = ((r >> 8) & 0xFF) / 255.0;
        result[1] = (r & 0xFF) / 255.0;
        result[2] = ((g >> 8) & 0xFF) / 255.0;
        result[3] = (g & 0xFF) / 255.0;
        result[4] = ((b >> 8) & 0xFF) / 255.0;
        result[5] = (b & 0xFF) / 255.0;

        // calculate HSV data
        float[] hsv = new float[3];
        Color.RGBToHSV(r / 256, g / 256, b / 256, hsv);

        // fill the normalized HSV data
        result[6] = hsv[0] / 360.0;
        result[7] = hsv[1];
        result[8] = hsv[2];

        StringBuilder builder = new StringBuilder();
        for (double v : result) { builder.append(v).append(","); }
        Log.d(MainActivity.TAG, "{" + builder.toString() + "},");

        return result;
    }

    /**
     * The actual neural network instance. Created in the constructor.
     */
    private NeuralNetwork mNetwork = null;

    /**
     * Constructor takes care of network creation and initialization.
     */
    public NeuralColorClassifier()
    {
        // discard old stuff
        mNetwork = null;
        boolean match = true;

        // try to load the network from file
        File networkFile = new File(NETWORK_FILENAME);
        if (networkFile.exists())
        {
            Log.i(MainActivity.TAG, "Loading neural network from: " + NETWORK_FILENAME);
            mNetwork = MultiLayerPerceptron.createFromFile(NETWORK_FILENAME);

            // plausibility check
            match &= mNetwork.getInputsCount() == INPUT_COUNT;
            match &= mNetwork.getOutputsCount() == OUTPUT_COUNT;
        }

        // we can't use it, if it doesn't match
        if (!match)
        {
            Log.e(MainActivity.TAG, "Bad network format detected. The old network will be overwritten");
            mNetwork = null;
        }

        // load wasn't successful, create a new one
        if (mNetwork == null)
        {
            Log.i(MainActivity.TAG, "Creating new neural network...");
            mNetwork = new MultiLayerPerceptron(INPUT_COUNT, HIDDEN_NEURONS, OUTPUT_COUNT);
            mNetwork.randomizeWeights();
        }

        // generate an csv-sheet for a deeper analysis
        if (!true)
        {
            Runnable export = this::export;
            Thread exporter = new Thread(export, "NeuralNet Export Thread");
            exporter.start();
        }

        // sometimes it might be a good idea to continue training
        if (!true)
        {
            Runnable learn = this::learn;
            Thread learner = new Thread(learn, "NeuralNet Learning Thread");
            learner.start();
        }
    }

    private void export()
    {
        try
        {
            String filename = NETWORK_FILENAME.replace(".nnet", ".csv");
            FileOutputStream fis = new FileOutputStream(filename);
            DataOutputStream dis = new DataOutputStream(fis);

            String header = "Color;R1;R2;G1;G2;B1;B2;H;S;V\r";
            dis.writeUTF(header);

            for (Map.Entry<ColorType,double[][]> entry : sTrainingData.entrySet())
            {
                ColorType color = entry.getKey();
                double[][] trainingData = entry.getValue();

                for (double[] inputs : trainingData)
                {
                    StringBuilder builder = new StringBuilder();
                    builder.append(color.name()).append(";");
                    for (double value : inputs) { builder.append(value).append(";"); }
                    builder.append("\n");

                    dis.writeUTF(builder.toString());
                }

                dis.flush();
                fis.flush();
            }

            dis.close();
            fis.close();

            Log.i(MainActivity.TAG, "CSV data was exported to " + filename);
        }
        catch (Exception e) { Log.e(MainActivity.TAG, "Failed to export color data: " + e.getMessage()); }
    }

    private void learn()
    {
        // set learning settings
        BackPropagation learningRule = ((MultiLayerPerceptron) mNetwork).getLearningRule();
        learningRule.setLearningRate(0.3);
        learningRule.setMaxError(1E-30);
        learningRule.setMaxIterations(1000);

        // show learning progress
        learningRule.addListener(event ->
        {
            BackPropagation bp = (BackPropagation) event.getSource();
            if (event.getEventType().equals(LearningEvent.Type.LEARNING_STOPPED))
            {
                Log.i(MainActivity.TAG, "Training completed in " + bp.getCurrentIteration() + " iterations");
                Log.i(MainActivity.TAG, "With total error " + bp.getTotalNetworkError());

                mNetwork.save(NETWORK_FILENAME);
                Log.i(MainActivity.TAG, "The network was saved to " + NETWORK_FILENAME);
            }
            else
            {
                Log.i(MainActivity.TAG, "Iteration: " + bp.getCurrentIteration() + " | Error: " + bp.getTotalNetworkError());
            }
        });

        // create a dataset for learning
        DataSet trainingSet = new DataSet(INPUT_COUNT, OUTPUT_COUNT);
        for (Map.Entry<ColorType,double[][]> entry : sTrainingData.entrySet())
        {
            ColorType color = entry.getKey();
            double[][] trainingData = entry.getValue();

            double[] outputs = new double[OUTPUT_COUNT];
            outputs[color.ordinal()] = 1.0;

            for (double[] inputs : trainingData)
            {
                DataSetRow dataRow = new DataSetRow(inputs, outputs);
                trainingSet.addRow(dataRow);
            }
        }
        Log.i(MainActivity.TAG, "Size of the training set: " + trainingSet.size());

        // crete a set of names
        LinkedList<String> names = new LinkedList<>();
        for (ColorType c : ColorType.values()) { names.add(c.name()); }
        String[] labels = names.toArray(new String[names.size()]);

        // set labels for output columns
        mNetwork.setOutputLabels(labels);
        for (int c = 0; c < trainingSet.getOutputSize(); c++) { trainingSet.setColumnName(INPUT_COUNT + c, labels[c]); }

        // we can finally start learning
        mNetwork.learn(trainingSet);
    }

    private ColorType recognize(double[] inputs)
    {
        double maxValue = 0.1;
        String maxName = ColorType.EMPTY.name();

        // calculate the result
        mNetwork.setInput(inputs);
        mNetwork.calculate();

        // find the best match
        for (Neuron neuron : mNetwork.getOutputNeurons())
        {
            // get the value
            String label = neuron.getLabel();
            double output = neuron.getOutput();

            // do some debugging
            Log.d(MainActivity.TAG, " - " + label + ": " + output);

            // the neural net may be somehow broken
            if (label == null)
            {
                Log.e(MainActivity.TAG, "Neural net doesn't know output names!");
                continue;
            }

            // compare to our max
            if (output > maxValue)
            {
                maxValue = output;
                maxName = label;
            }
        }

        // convert it back to the enum value
        Log.i(MainActivity.TAG, "Detected " + maxName + " with probablity of " + maxValue);
        return ColorType.valueOf(maxName);
    }

    @Override
    public String getName()
    {
        return NeuralColorClassifier.class.getSimpleName();
    }

    @Override
    public ColorType classify(int r, int g, int b)
    {
        double[] inputs = createInputs(r, g, b);
        return recognize(inputs);
    }
}

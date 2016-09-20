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

import android.util.Log;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import java.io.File;
import java.util.LinkedList;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorConverter;
import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.space.ColorHSV;
import rtandroid.ballsort.blocks.color.space.ColorLAB;
import rtandroid.ballsort.blocks.color.space.ColorRGB;
import rtandroid.ballsort.settings.Constants;

public class NeuronalColorClassifier implements IColorClassifier
{
    private static final String NETWORK_NAME = "colors.nnet";
    private static final int INPUT_COUNT = (2 + 2 + 2) + (2 + 1 + 1) + (1 + 1 + 1); // RGB + HSV + LAB;
    private static final int HIDDEN_NEURONS = Constants.PATTERN_COLUMNS_COUNT;
    private static final int OUTPUT_COUNT = ColorData.values().length;

    private static NeuralNetwork sNetwork = new MultiLayerPerceptron(INPUT_COUNT, HIDDEN_NEURONS, OUTPUT_COUNT);
    private static DataSetRow[] sTrainingSet = new DataSetRow[]
    {
            new DataSetRow(new double[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, new double[] {0, 0, 0, 0, 0, 0, 0})
    };

    public static void startLearning()
    {
        Log.i(MainActivity.TAG, "Creating new neural sNetwork...");
        sNetwork.randomizeWeights();

        File networkFile = new File(NETWORK_NAME);
        if (networkFile.exists())
        {
            Log.i(MainActivity.TAG, "Loading neural sNetwork from file...");
            sNetwork = MultiLayerPerceptron.createFromFile(NETWORK_NAME);
        }

        // set learning settings
        BackPropagation learningRule = ((MultiLayerPerceptron) sNetwork).getLearningRule();
        learningRule.setLearningRate(0.3);
        learningRule.setMaxError(1E-10);
        learningRule.setMaxIterations(500);

        // show learning progress
        learningRule.addListener(new LearningEventListener()
        {
            @Override
            public void handleLearningEvent(LearningEvent event)
            {
                BackPropagation bp = (BackPropagation) event.getSource();
                if (event.getEventType().equals(LearningEvent.Type.LEARNING_STOPPED))
                {
                    Log.i(MainActivity.TAG, "Training completed in " + bp.getCurrentIteration() + " iterations");
                    Log.i(MainActivity.TAG, "With total error " + bp.getTotalNetworkError());
                }
                else
                {
                    Log.i(MainActivity.TAG, "Iteration: " + bp.getCurrentIteration() + " | Error: " + bp.getTotalNetworkError());
                }
            }
        });

        // create a dataset for learning
        DataSet trainingSet = new DataSet(INPUT_COUNT, OUTPUT_COUNT);
        for (DataSetRow dataRow : sTrainingSet) { trainingSet.addRow(dataRow); }
        Log.i(MainActivity.TAG, "Size of the training set: " + trainingSet.size());

        // crete a set of names
        LinkedList<String> names = new LinkedList<>();
        for (ColorData c : ColorData.values()) { names.add(c.name().toLowerCase()); }
        String[] labels = names.toArray(new String[names.size()]);

        // set labels for output columns
        for (int c = 0; c < trainingSet.getOutputSize(); c++) { trainingSet.setColumnName(INPUT_COUNT + c, labels[c]); }

        // we can finally start learning
        sNetwork.learn(trainingSet);
        sNetwork.setOutputLabels(labels);
        sNetwork.save(NETWORK_NAME);
    }

    private static double[] createInputs(ColorRGB colorRGB)
    {
        ColorHSV colorHSV = ColorConverter.rgb2hsv(colorRGB);
        ColorLAB colorLAB = ColorConverter.rgb2lab(colorRGB);

        double[] result = new double[INPUT_COUNT];
        result[0] = (colorRGB.R >> 8) & 0xF;
        result[1] = colorRGB.R & 0xF;
        result[2] = (colorRGB.G >> 8) & 0xF;
        result[3] = colorRGB.G & 0xF;
        result[4] = (colorRGB.B >> 8) & 0xF;
        result[5] = colorRGB.B & 0xF;
        result[6] = (colorHSV.H >> 8) & 0xF;
        result[7] = colorHSV.H & 0xF;
        result[8] = colorHSV.S;
        result[9] = colorHSV.V;
        result[10] = colorLAB.L;
        result[11] = colorLAB.A;
        result[12] = colorLAB.B;

        StringBuilder builder = new StringBuilder();
        builder.append("new DataSetRow(new double[] {");
        for (double v : result) { builder.append(v).append(","); }
        builder.append("}, new double[] {0, 0, 0, 0, 0, 0, 0})");
        Log.d(MainActivity.TAG, builder.toString());

        return result;
    }

    private static ColorData recognize(double[] inputs)
    {
        double minDistance = Integer.MAX_VALUE;
        ColorData minName = ColorData.EMPTY;

        // calculate the result
        sNetwork.setInput(inputs);
        sNetwork.calculate();

        // find the detected element
        /*
        String maxName = TRColor.TR_UNKNOWN.name().toLowerCase();
        double maxValue = 0.2;
        for (Neuron neuron : sNetwork.getOutputNeurons())
        {
            // get the value
            String label = neuron.getLabel();
            double output = neuron.getOutput();

            // compare to our max
            if (output > maxValue)
            {
                maxValue = output;
                maxName = label;
            }
        }

        // convert it to the enum value
        TRColor value = TRColor.valueOf(maxName.toUpperCase());
        return new Pair<>(value, maxValue);
        */

        return minName;
    }

    @Override
    public String getName()
    {
        return NeuronalColorClassifier.class.getSimpleName();
    }

    @Override
    public ColorData classify(ColorRGB color)
    {
        double[] inputs = createInputs(color);
        return recognize(inputs);
    }
}

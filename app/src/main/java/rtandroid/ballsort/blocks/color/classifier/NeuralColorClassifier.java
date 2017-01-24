/*
 * Copyright (C) 2017 RTAndroid Project
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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorRGB;
import rtandroid.ballsort.blocks.color.ColorType;
import rtandroid.ballsort.settings.DataState;
import rtandroid.ballsort.settings.SettingsManager;

import static rtandroid.ballsort.blocks.color.ColorType.*;

public class NeuralColorClassifier implements IColorClassifier {

    private static NeuralColorClassifier sInstance = null;
    public static NeuralColorClassifier getInstance()
    {
        if (sInstance == null) { sInstance = new NeuralColorClassifier(); }
        return sInstance;
    }

    private static final int INPUT_COUNT = (2 + 2 + 2) + (1 + 1 + 1);       // RGB + HSV;
    private static final int OUTPUT_COUNT = values().length - 1;            // don't detect EMPTY
    private static final int HIDDEN_NEURONS = INPUT_COUNT * OUTPUT_COUNT;   // random guess

    private static final String NETWORK_FILENAME = Environment.getExternalStorageDirectory().getAbsolutePath() +
            "/colors" +
            "_" + INPUT_COUNT +
            "_" + HIDDEN_NEURONS +
            "_" + OUTPUT_COUNT +
            ".nnet";

    private static final List<double[]> BLACK_DATA = new ArrayList<>();
    private static final List<double[]> BLUE_DATA = new ArrayList<>();
    private static final List<double[]> GREEN_DATA = new ArrayList<>();
    private static final List<double[]> RED_DATA = new ArrayList<>();
    private static final List<double[]> YELLOW_DATA = new ArrayList<>();
    private static final List<double[]> WHITE_DATA = new ArrayList<>();

    /**
     * The actual neural network instance. Created in the constructor.
     */
    private NeuralNetwork mNetwork = null;
    private HashMap<ColorType, List<double[]>> sTrainingData = null;

    private void resetTrainingsData()
    {
        mNetwork = new MultiLayerPerceptron(INPUT_COUNT, HIDDEN_NEURONS, OUTPUT_COUNT);
        mNetwork.randomizeWeights();

        sTrainingData = new HashMap<>();
        sTrainingData.put(BLACK, BLACK_DATA);
        sTrainingData.put(BLUE, BLUE_DATA);
        sTrainingData.put(GREEN, GREEN_DATA);
        sTrainingData.put(RED,  RED_DATA);
        sTrainingData.put(YELLOW, YELLOW_DATA);
        sTrainingData.put(WHITE,  WHITE_DATA);

        Iterator it = sTrainingData.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            List<double[]> list = (List<double[]>) pair.getValue();
            System.out.println(pair.getKey() + " = " + list.size());
            it.next();
        }
    }

    public void addTrainingsEntry(ColorType type, ColorRGB color)
    {
        double[] entry = createInputs(color.r, color.g, color.b);
        switch (type)
        {
            case BLACK:
                BLACK_DATA.add(entry);
                break;
            case BLUE:
                BLUE_DATA.add(entry);
                break;
            case GREEN:
                GREEN_DATA.add(entry);
                break;
            case RED:
                RED_DATA.add(entry);
                break;
            case YELLOW:
                YELLOW_DATA.add(entry);
                break;
            case WHITE:
                WHITE_DATA.add(entry);
                break;
            default:
                Log.e(MainActivity.TAG, "Unknown color: " + type);
                break;
        }

        StringBuilder builder = new StringBuilder();
        for (double v : entry) { builder.append(v).append(","); }
        Log.d(MainActivity.TAG, "{" + builder.toString() + "},");
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

        // do some debugging
        if (result.length < 0)
        {
            StringBuilder builder = new StringBuilder();
            for (double v : result) { builder.append(v).append(","); }
            Log.d(MainActivity.TAG, "{" + builder.toString() + "},");
        }

        return result;
    }


    /**
     * Constructor takes care of network creation and initialization.
     */
    private NeuralColorClassifier()
    {
        // discard old stuff
        mNetwork = null;
        boolean match = true;

        // try to prepare the network from file
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

        // prepare wasn't successful, create a new one
        if (mNetwork == null)
        {
            Log.i(MainActivity.TAG, "Creating new neural network...");
            mNetwork = new MultiLayerPerceptron(INPUT_COUNT, HIDDEN_NEURONS, OUTPUT_COUNT);
            mNetwork.randomizeWeights();
        }
    }

    public void startLearning()
    {
        resetTrainingsData();
        Runnable learn = this::learn;
        Thread learner = new Thread(learn, "NeuralNet Learning Thread");
        learner.start();
    }

    private void learn()
    {
            final double learningRate = 0.4;
            final double learningGoal = 1E-50;
            final int learningIterations = 200000;

            // apply learning settings
            final long learningStart = System.nanoTime();
            BackPropagation learningRule = ((MultiLayerPerceptron) mNetwork).getLearningRule();
            learningRule.setLearningRate(learningRate);
            learningRule.setMaxIterations(learningIterations);
            learningRule.setMaxError(learningGoal);

            // show learning progress
            learningRule.addListener(event ->
            {
                BackPropagation bp = (BackPropagation) event.getSource();
                if (event.getEventType().equals(LearningEvent.Type.LEARNING_STOPPED))
                {
                    Log.i(MainActivity.TAG, "Training completed in " + bp.getCurrentIteration() + " iterations");

                    long totalTime = (System.nanoTime() - learningStart) / 1000 / 1000 / 1000 + 1;
                    long totalSpeed = (learningIterations * 3600) / totalTime;
                    Log.i(MainActivity.TAG, "With learning speed of " + totalSpeed + " iterations / hour");

                    mNetwork.save(NETWORK_FILENAME);
                    Log.i(MainActivity.TAG, "The network was saved to " + NETWORK_FILENAME);
                }
                else
                {
                    DataState data = SettingsManager.getData();
                    data.mLearningError =  bp.getTotalNetworkError();
                    Log.i(MainActivity.TAG, "Iteration: " + bp.getCurrentIteration() + " | Error: " + bp.getTotalNetworkError());
                }
            });

            // create a dataset for learning
            DataSet trainingSet = new DataSet(INPUT_COUNT, OUTPUT_COUNT);
            for (Map.Entry<ColorType, List<double[]>> entry : sTrainingData.entrySet())
            {
                ColorType color = entry.getKey();
                List<double[]> trainingData = entry.getValue();

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
            for (ColorType c : values())
            {
                names.add(c.name());
            }
            String[] labels = names.toArray(new String[names.size()]);

            // set labels for output columns
            mNetwork.setOutputLabels(labels);
            for (int c = 0; c < trainingSet.getOutputSize(); c++)
            {
                trainingSet.setColumnName(INPUT_COUNT + c, labels[c]);
            }

            // we can finally start learning
            mNetwork.learn(trainingSet);
    }

    private ColorType recognize(double[] inputs)
    {
        double maxValue = 0.1;
        String maxName = EMPTY.name();

        // calculate the result
        mNetwork.setInput(inputs);
        mNetwork.calculate();

        // find the best match
        for (Neuron neuron : mNetwork.getOutputNeurons())
        {
            // get the value
            String label = neuron.getLabel();
            double output = neuron.getOutput();

            // show which color was the most probable
            // Log.d(MainActivity.TAG, " - " + label + ": " + output);

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
        return valueOf(maxName);
    }

    @Override
    public String getName()
    {
        return NeuralColorClassifier.class.getSimpleName();
    }

    @Override
    public ColorType classify(ColorRGB color)
    {
        double[] inputs = createInputs(color.r, color.g, color.b);
        return recognize(inputs);
    }
}

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
import org.neuroph.nnet.MultiLayerPerceptron;

import java.io.File;

import rtandroid.ballsort.MainActivity;
import rtandroid.ballsort.blocks.color.ColorConverter;
import rtandroid.ballsort.blocks.color.ColorData;
import rtandroid.ballsort.blocks.color.space.ColorHSV;
import rtandroid.ballsort.blocks.color.space.ColorLAP;
import rtandroid.ballsort.blocks.color.space.ColorRGB;
import rtandroid.ballsort.settings.Constants;

public class NeuronalColorClassifier implements IColorClassifier
{
    private static final String NETWORK_NAME = "colors.nnet";
    private static final int INPUT_COUNT = 3 * 2 + 3 + 3; // RGB + HSV + LAP;
    private static final int HIDDEN_NEURONS = Constants.PATTERN_COLUMNS_COUNT;
    private static final int OUTPUT_COUNT = ColorData.values().length;

    public NeuronalColorClassifier()
    {
        Log.i(MainActivity.TAG, "Creating new neural network...");
        NeuralNetwork network = new MultiLayerPerceptron(INPUT_COUNT, HIDDEN_NEURONS, OUTPUT_COUNT);
        network.randomizeWeights();

        File networkFile = new File(NETWORK_NAME);
        if (networkFile.exists())
        {
            Log.i(MainActivity.TAG, "Loading neural network from file...");
            network = MultiLayerPerceptron.createFromFile(NETWORK_NAME);
        }
    }

    @Override
    public String getName()
    {
        return NeuronalColorClassifier.class.getSimpleName();
    }

    @Override
    public ColorData classify(ColorRGB color)
    {
        double minDistance = Long.MAX_VALUE;
        ColorData minName = ColorData.EMPTY;

        ColorHSV colorHSV = ColorConverter.rgb2hsv(color);
        ColorLAP colorLAB = ColorConverter.rgb2lab(color);
        Log.d(MainActivity.TAG, color.toString() + " - " + colorHSV.toString() + "-" + colorLAB.toString());

        return minName;
    }

    /*
    public static Pair<TRColor, Double> recognize(BufferedImage img)
    {
        // create fraction data for inputs
        Image image = new ImageJ2SE(img);
        FractionRgbData rgbData = new FractionRgbData(image);
        double[] input = rgbData.getFlattenedRgbValues();

        // calculate the result
        sNetwork.setInput(input);
        sNetwork.calculate();

        // find the detected element
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
    }

    public static void main(String[] args)
    {
        // set learning settings
        BackPropagation learningRule = ((MultiLayerPerceptron) sNetwork).getLearningRule();
        learningRule.setLearningRate(0.3);
        learningRule.setMaxError(1E-10);
        learningRule.setMaxIterations(500);

        // show learning progress
        learningRule.addListener(event ->
        {
            BackPropagation bp = (BackPropagation) event.getSource();
            if (event.getEventType().equals(LearningEvent.Type.LEARNING_STOPPED))
            {
                System.out.println();
                System.out.println("Training completed in " + bp.getCurrentIteration() + " iterations");
                System.out.println("With total error " + bp.getTotalNetworkError() + '\n');
            }
            else
            {
                System.out.println("Iteration: " + bp.getCurrentIteration() + " | Error: " + bp.getTotalNetworkError());
            }
        });

        // load all the images for learning
        Map<String, FractionRgbData> rgbDataMap = new HashMap<>();
        File dir = new File("res\\data\\neuralnet");
        File[] files = dir.listFiles();
        if (files == null) { files = new File[0]; }
        for (File f : files)
        {
            if (!f.isFile()) { continue; }
            if (!f.getName().startsWith("tr_")) { continue; }

            Image image = ImageFactory.getImage(f);
            FractionRgbData rgbData = new FractionRgbData(image);
            rgbDataMap.put(f.getName(), rgbData);
        }

        // crete a set of names
        LinkedList<String> names = new LinkedList<>();
        for (TRColor c : TRColor.values()) { names.add(c.name().toLowerCase()); }
        String[] labels = names.toArray(new String[1]);

        // create a set for learning
        DataSet trainingSet = ImageRecognitionHelper.createRGBTrainingSet(names, rgbDataMap);
        System.out.println("Size of the training set: " + trainingSet.size());

        // we can finally start learning
        sNetwork.learn(trainingSet);
        sNetwork.setOutputLabels(labels);
        sNetwork.save(NETWORK_NAME);
    }
    */
}

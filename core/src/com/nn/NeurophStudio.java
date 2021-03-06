package com.nn;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Weight;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;
import java.util.Locale;

public class NeurophStudio {
    private String FileDataSet;
    private String FileNetwork;
    private String PathDataSet;
    private String PathNetwork;
    public NeuralNetwork nnetwork;

    public NeurophStudio(String pathDataSet, String pathNetwork, String pathDataSet1, String pathNetwork1) {
        FileDataSet = pathDataSet;
        FileNetwork = pathNetwork;
        PathDataSet = pathDataSet1;
        PathNetwork = pathNetwork1;
        //nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
    }

    public double Perceptron(TransferFunctionType transferFunctionType, double[] inputs, double[] outputs){
        //DataSet trainingSet = new DataSet(inputs.length, outputs.length);
        //trainingSet.addRow(new DataSetRow(inputs, outputs));

        Perceptron myPerceptron = new Perceptron(inputs.length, outputs.length, transferFunctionType);
        //myPerceptron.learn(trainingSet);
        myPerceptron.setInput(inputs);
        myPerceptron.calculate();
        //myPerceptron.save(PathNetwork + FileNetwork);

        //System.out.print("Weigths1: ");
        //for(int i=0; i<weight.length; i++)
        //    System.out.printf(Locale.US, " %012.8f", myPerceptron.getWeights()[i]);
        //System.out.println();

        //NeuralNetwork loadedPerceptron = NeuralNetwork.load("NeurophProject_BangBang/Neural Networks/mySamplePerceptron.nnet");
        //loadedPerceptron.setInput(inputs);
        //loadedPerceptron.setWeights(weight);
        //loadedPerceptron.calculate();

        //System.out.printf(Locale.US, "Sigmoid2: %012.8f%n", loadedPerceptron.getOutput()[0]);

        //System.out.print("Weigths2: ");
        //for(int i=0; i<weight.length; i++)
        //    System.out.printf(Locale.US, "%012.8f ", loadedPerceptron.getWeights()[i]);
        //System.out.println();

        return myPerceptron.getOutput()[0];
    }

    public double PerceptronML(TransferFunctionType transferFunctionType,
                               double[] inputs, double[] outputs, int[] neuronsInLayers){

        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(transferFunctionType,
                neuronsInLayers);
        myMlPerceptron.setInput(inputs);
        myMlPerceptron.calculate();

        return myMlPerceptron.getOutput()[0];
    }

    public void CreateMLP(int[] neuronsInLayers, String FileNetwork){
        //nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, neuronsInLayers);
        myMlPerceptron.setLabel(FileNetwork);
        myMlPerceptron.save(PathNetwork + FileNetwork);

        // Load NN
        nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
    }

    public double[] MLP(DataSetRow dataRow, int[] neuronsInLayers, float maxError, float learnRate, String FileNetwork){

        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, neuronsInLayers);
        myMlPerceptron.setLabel(FileNetwork);

        BackPropagation lr = new BackPropagation();
        lr.setLearningRate(learnRate);
        lr.setMaxError(maxError);
        lr.setNeuralNetwork(myMlPerceptron);

        myMlPerceptron.setLearningRule(lr);
        myMlPerceptron.save(PathNetwork + FileNetwork);

        NeuralNetwork loadedPerceptron = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        loadedPerceptron.setInput(dataRow.getInput());
        loadedPerceptron.calculate();

        return loadedPerceptron.getOutput();
    }

    public double[] MLPGetweights(DataSetRow dataRow, int[] neuronsInLayers, float maxError, float learnRate, String FileNetwork){

        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, neuronsInLayers);
        myMlPerceptron.setLabel(FileNetwork);

        BackPropagation lr = new BackPropagation();
        lr.setLearningRate(learnRate);
        lr.setMaxError(maxError);
        lr.setNeuralNetwork(myMlPerceptron);

        myMlPerceptron.setLearningRule(lr);
        myMlPerceptron.save(PathNetwork + FileNetwork);

        NeuralNetwork loadedPerceptron = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        loadedPerceptron.setInput(dataRow.getInput());
        loadedPerceptron.calculate();

        System.out.println(Arrays.toString(loadedPerceptron.getOutput()));

        int total = myMlPerceptron.getWeights().length;
        int k= 0;
        double[] w = new double[total];

        for(int h=1; h<myMlPerceptron.getLayers().size(); h++) {
            for (int i = 0; i < myMlPerceptron.getLayerAt(h).getNeurons().size(); i++) {
                for (int j = 0; j < myMlPerceptron.getLayerAt(h).getNeurons().get(i).getWeights().length; j++) {
                    Object o = myMlPerceptron.getLayerAt(h).getNeurons().get(i).getWeights()[j];
                    w[k] = (double) ((Weight) o).getValue();
                    k++;
                }
            }
        }

        return w;
    }

    public int PerceptronMLSave(TransferFunctionType transferFunctionType, DataSet trainingSet,
                                int[] neuronsInLayers, String FileDataSet, String FileNetwork,
                                float maxError, float learnRate, float momentum, int maxIteration,
                                String[] labelInput,
                                String[] labelOutput){

        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(transferFunctionType, neuronsInLayers);
        myMlPerceptron.setLabel(FileNetwork);

        int iterations = 0;

        trainingSet.setLabel(FileDataSet);
        trainingSet.save(PathDataSet + FileDataSet);

        for(int i=0; i<neuronsInLayers[0]; i++) {
            myMlPerceptron.getInputNeurons().get(i).setLabel(labelInput[i]);
        }

        for(int i=0; i<neuronsInLayers[neuronsInLayers.length-1]; i++) {
            myMlPerceptron.getOutputNeurons().get(i).setLabel(labelOutput[i]);
        }

        if(momentum>0) {
            MomentumBackpropagation lr = (MomentumBackpropagation) myMlPerceptron.getLearningRule();
            lr.setMomentum(momentum);
            lr.setLearningRate(learnRate);
            lr.setMaxError(maxError);
            lr.setMaxIterations(maxIteration);
            lr.setNeuralNetwork(myMlPerceptron);
            iterations = lr.getCurrentIteration();
        }else{
            BackPropagation lr = new BackPropagation();
            lr.setLearningRate(learnRate);
            lr.setMaxError(maxError);
            lr.setNeuralNetwork(myMlPerceptron);
            myMlPerceptron.setLearningRule(lr);
            iterations = lr.getCurrentIteration();
        }

        myMlPerceptron.learn(trainingSet);
        myMlPerceptron.save(PathNetwork + FileNetwork);

        //System.out.println("Error: " + lr.getTotalNetworkError());

        return iterations;
    }

    public double[] TestX(double[] input){
        nnetwork.setInput(input);
        nnetwork.calculate();
        //nnetwork.save(PathNetwork + FileNetwork);

        double[] networkOutput = new double[input.length];
        networkOutput = nnetwork.getOutput();
        //System.out.println("Save on: " + PathNetwork + FileNetwork);

        return networkOutput;
    }

    public double[] Test(String FileNetwork, double[] input){
        NeuralNetwork loadedPerceptron = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        loadedPerceptron.setInput(input);
        loadedPerceptron.calculate();
        loadedPerceptron.save(PathNetwork + FileNetwork);

        double[] networkOutput = new double[input.length];
        networkOutput = loadedPerceptron.getOutput();
        //System.out.println("Save on: " + PathNetwork + FileNetwork);

        return networkOutput;
    }

    public double[] TestNetworkMl(String FileNetwork, DataSet trainingSet){
        NeuralNetwork loadedPerceptron = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);

        double[] networkOutput = new double[trainingSet.getRows().size()];

        for(int i=0; i<(trainingSet.getRows().get(0).getInput().length + trainingSet.getRows().get(0).getDesiredOutput().length + trainingSet.getOutputSize()); i++)
            System.out.print("+--------------");

        System.out.println("+");

        for(int i=0; i<trainingSet.getRows().get(0).getInput().length; i++)
            System.out.printf(Locale.US,"|    Input %d   ", (i + 1) );

        for(int i=0; i<trainingSet.getRows().get(0).getDesiredOutput().length; i++)
            System.out.printf(Locale.US,"|  Desired %d   ", (i + 1) );

        for(int i=0; i<trainingSet.getOutputSize(); i++)
            System.out.printf(Locale.US,"|  Output  %d   ", (i + 1) );

        System.out.print("|\n");

        for(int i=0; i<(trainingSet.getRows().get(0).getInput().length + trainingSet.getRows().get(0).getDesiredOutput().length + trainingSet.getOutputSize()); i++)
            System.out.print("+--------------");

        System.out.println("+");

        for(DataSetRow dataRow : trainingSet.getRows()) {
            loadedPerceptron.setInput(dataRow.getInput());
            loadedPerceptron.calculate();
            networkOutput = loadedPerceptron.getOutput();

            //System.out.printf(
            //        Locale.US,
            //        "Input: %s, Output: %s, Desired: %s%n",
            //        Arrays.toString(dataRow.getInput()),
            //        Arrays.toString(networkOutput),
            //        Arrays.toString(dataRow.getDesiredOutput())
            //);

            //System.out.print("|");

            for(int i=0; i<dataRow.getInput().length; i++){
                System.out.printf(Locale.US, "| %012.8f ", dataRow.getInput()[i]);

                //if(i<dataRow.getInput().length-1)
                //    System.out.print("|");
            }

            for(int i=0; i<dataRow.getDesiredOutput().length; i++) {
                System.out.printf(Locale.US, "| %012.8f ", dataRow.getDesiredOutput()[i]);

                //if (i < dataRow.getDesiredOutput().length - 1)
                //    System.out.print("|");
            }

            for(int i=0; i<networkOutput.length; i++) {
                System.out.printf(Locale.US, "| %012.8f ", networkOutput[i]);

                //if (i < dataRow.getDesiredOutput().length - 1)
                //    System.out.print("|");
            }

            System.out.print("|\n");
        }

        for(int i=0; i<(trainingSet.getRows().get(0).getInput().length + trainingSet.getRows().get(0).getDesiredOutput().length + trainingSet.getOutputSize()); i++)
            System.out.print("+--------------");

        System.out.println("+");
        System.out.println("Total: " + trainingSet.getRows().size() );

        return networkOutput;
    }

    public double[] getWeights(String FileNetwork){

        NeuralNetwork nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);

        int total = nnetwork.getWeights().length;
        int k= 0;
        double[] w = new double[total];

        for(int h=1; h<nnetwork.getLayers().size(); h++) {
            for (int i = 0; i < nnetwork.getLayerAt(h).getNeurons().size(); i++) {
                //System.out.println("L:" + h + " N:" + i + " [ " + nnetwork.getLayerAt(h).getNeurons().get(i).getOutput() + " ] ");
                for (int j = 0; j < nnetwork.getLayerAt(h).getNeurons().get(i).getWeights().length; j++) {
                    //System.out.print(nnetwork.getLayerAt(h).getNeurons().get(i).getWeights()[j] + " ");
                    Object o = nnetwork.getLayerAt(h).getNeurons().get(i).getWeights()[j];
                    w[k] = (double) ((Weight) o).getValue();
                    k++;
                }
                //System.out.println();
            }
        }

        return w;
    }

    public double[] getWeights2(String FileNetwork, double[] inputs){
        NeuralNetwork nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        nnetwork.setInput(inputs);
        //nnetwork.randomizeWeights();
        nnetwork.calculate();

        int total = nnetwork.getWeights().length;
        int k= 0;
        double[] w = new double[total];

        for(int h=1; h<nnetwork.getLayers().size(); h++) {
            for (int i = 0; i < nnetwork.getLayerAt(h).getNeurons().size(); i++) {
                //System.out.println("L:" + h + " N:" + i + " [ " + nnetwork.getLayerAt(h).getNeurons().get(i).getOutput() + " ] ");
                for (int j = 0; j < nnetwork.getLayerAt(h).getNeurons().get(i).getWeights().length; j++) {
                    //System.out.print(nnetwork.getLayerAt(h).getNeurons().get(i).getWeights()[j] + " ");
                    Object o = nnetwork.getLayerAt(h).getNeurons().get(i).getWeights()[j];
                    w[k] = (double) ((Weight) o).getValue();
                    k++;
                }
                //System.out.println();
            }
        }

        return w;
    }

    public void setWeights(String FileNetwork, double[] weights){
        NeuralNetwork nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        nnetwork.setWeights(weights);
        nnetwork.save(PathNetwork + "NewNeuralNetwork.nnet");
    }

    public void setWeightsX(double[] weights){
        //NeuralNetwork nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        //nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        nnetwork.setWeights(weights);
        //nnetwork.save(PathNetwork + "NewNeuralNetwork.nnet");
    }

    public void setOutputs(String FileNetwork, double[] outputs){

        int total = 0;
        int k= 0;

        NeuralNetwork nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        nnetwork.randomizeWeights();

        for(int h=0; h<nnetwork.getLayers().size(); h++)
            for (int i = 0; i < nnetwork.getLayerAt(h).getNeurons().size(); i++)
                total++;

        for(int h=0; h<nnetwork.getLayers().size(); h++) {
            for (int i = 1; i < nnetwork.getLayerAt(h).getNeurons().size()-1; i++) {
                nnetwork.getLayerAt(h).getNeurons().get(i).setOutput(outputs[k]);
                k++;
            }
        }

        nnetwork.calculate();
        nnetwork.save(PathNetwork + FileNetwork);
    }

    public double[] getOutputs(String FileNetwork, double[] inputs){
        int total = 0;
        int k= 0;

        NeuralNetwork nnetwork = NeuralNetwork.createFromFile(PathNetwork + FileNetwork);
        nnetwork.setInput(inputs);
        nnetwork.calculate();

        for(int i=0; i<nnetwork.getLayers().size(); i++)
            for (int j = 0; j < nnetwork.getLayerAt(i).getNeurons().size(); j++)
                total++;

        double[] out = new double[total];

        for(int i=0; i<nnetwork.getLayers().size(); i++) {
            for (int j = 0; j < nnetwork.getLayerAt(i).getNeurons().size(); j++) {
                out[k] = nnetwork.getLayerAt(i).getNeurons().get(j).getOutput();
                k++;
            }
        }

        return out;
    }

    public DataSetRow MinValue(DataSet trainingSet){
        DataSetRow row = new DataSetRow();
        double min = 99999999;

        for(int i=0; i<trainingSet.getRows().size(); i++){
            double col = trainingSet.getRows().get(i).getInput()[0];
            if(col < min){
                min = col;
                row = trainingSet.getRows().get(i);
            }
        }

        return row;
    }

    public DataSetRow MinValueSpace(DataSet trainingSet, double limit){
        DataSetRow row = new DataSetRow();
        double min = 99999999;

        for(int i=0; i<trainingSet.getRows().size(); i++){
            double col = Math.abs(trainingSet.getRows().get(i).getInput()[0]-limit);
            if(col < min){
                min = col;
                row = trainingSet.getRows().get(i);
            }
        }

        return row;
    }

    public DataSetRow MaxValue(DataSet trainingSet, double limit){
        DataSetRow row = new DataSetRow();
        row.setInput(new double[]{0});
        double max = 0;

        for(int i=0; i<trainingSet.getRows().size(); i++){
            double col = trainingSet.getRows().get(i).getInput()[0];
            if(col > max && col <= limit){
                max = col;
                row = trainingSet.getRows().get(i);
            }
        }
        return row;
    }

    public DataSet Order(DataSet trainingSet) {
        DataSet trainingSetTMP = new DataSet(trainingSet.getInputSize(), trainingSet.getOutputSize());
        int count = 0;
        int total = trainingSet.getRows().size();

        while(count < total) {
            double row = MinValue(trainingSet).getInput()[0];
            trainingSetTMP.addRow(MinValue(trainingSet));

            for (int i = 0; i < trainingSet.getRows().size(); i++) {
                double value = trainingSet.getRows().get(i).getInput()[0];

                if (row == value) {
                    trainingSet.remove(i);
                    break;
                }
            }
            count++;
        }
        return trainingSetTMP;
    }

    public DataSet Best(DataSet trainingSet, int max, double limit) {
        DataSet trainingSetTMP = new DataSet(trainingSet.getInputSize(), trainingSet.getOutputSize());
        DataSet trainingSetBKP = new DataSet(trainingSet.getInputSize(), trainingSet.getOutputSize());

        for(int i=0; i<trainingSet.getRows().size(); i++)
            trainingSetTMP.addRow(trainingSet.getRows().get(i));

        for (int i=0; i<max; i++) {
            double val = 0;
            for (int j=0; j<trainingSetTMP.getRows().size(); j++) {
                double value = trainingSetTMP.getRows().get(j).getInput()[0];
                if (value>val && value<=limit+0.10f) {
                    val = value;
                    trainingSetBKP.addRow(trainingSetTMP.getRows().get(j));
                    trainingSetTMP.remove(j);
                }
            }
        }

        return trainingSetBKP;
    }
}

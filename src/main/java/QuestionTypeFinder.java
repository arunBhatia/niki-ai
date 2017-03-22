package main.java;

import libsvm.svm_model;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Class which tries to learn from the features in the training dataset and predicts type of the sentence
 * in testing dataset.
 *
 * Type of the sentence can be:
 * 1. Who
 * 2. What
 * 3. When
 * 4. Affirmative
 * 5. Unknown - in case, nothing gets found
 *
 * Created by Arun bhatia on 22/3/17.
 */
public class QuestionTypeFinder {

    public static void main(String... args) throws IOException {

        Map<Integer, HashMap<Integer, Integer>> featuresTraining;
        Map<Integer, Integer> labelTraining;
        Map<Integer, String> trainingData;

        Map<Integer, HashMap<Integer, Integer>> featuresTesting;
        Map<Integer, Integer> labelTesting;
        Map<Integer, String> testingData;

        // Read training data
        FeatureConvertor training = new FeatureConvertor("training_Data");
        training.extractFeatures();
        featuresTraining = training.getFeatureVectorMap();
        labelTraining = training.getLabelMap();
        trainingData = training.getDataMap();

        // Read test data
        FeatureConvertor testing = new FeatureConvertor("testing_Data");
        testing.extractFeatures();
        featuresTesting = testing.getFeatureVectorMap();
        labelTesting = testing.getLabelMap();
        testingData = testing.getDataMap();

        //printTrainingFeatures(trainingData, featuresTraining);

        // Train the SVM model
        Svm_Classifier classifier = new Svm_Classifier();
        svm_model model = classifier.trainClassifier(featuresTraining, labelTraining);

        // Test the model
        Map<Integer, Integer> resultLabel = classifier.testModel(model, featuresTesting);

        getResults(resultLabel, labelTesting, testingData);

    }

    /**
     * Prints results
     * @param result
     * @param actual
     * @param testingData
     */
    private static void getResults(Map<Integer, Integer> result, Map<Integer, Integer> actual,
            Map<Integer, String> testingData){

        Map<Integer, String> labelMap = Constants.getIntLabelMap();
        int totalClasses = labelMap.size();
        if (result.size() != actual.size()){
            System.out.println("Size mismatch!!!!");
        }

        else {
            int size = result.size();
            int [][] confusionMatrix = new int[totalClasses][totalClasses];
            int correct = 0;

            for (int i  = 0; i < size; i++){
                if (result.get(i).equals(actual.get(i))){
                    correct++;
                }
//                else {
//                    String[] tokens = testingData.get(i).split(Constants.SEPARATOR);
//                    System.out.println(tokens[0]);
//                    System.out.println("actual label: " + tokens[1]);
//                    System.out.println("predicted label: " + labelMap.get(result.get(i)));
//                }
                confusionMatrix[actual.get(i)-1][result.get(i)-1]++;


            }
            DecimalFormat df = new DecimalFormat("#.##");
            Double accuracy = (correct * 1.0) / size;
            Double precision1 = getPrecision(0, totalClasses, confusionMatrix);
            Double precision2 = getPrecision(1, totalClasses, confusionMatrix);
            Double precision3 = getPrecision(2, totalClasses, confusionMatrix);
            Double precision4 = getPrecision(3, totalClasses, confusionMatrix);
            Double precision5 = getPrecision(4, totalClasses, confusionMatrix);

            Double recall1 = getRecall(0, totalClasses, confusionMatrix);
            Double recall2 = getRecall(1, totalClasses, confusionMatrix);
            Double recall3 = getRecall(2, totalClasses, confusionMatrix);
            Double recall4 = getRecall(3, totalClasses, confusionMatrix);
            Double recall5 = getRecall(4, totalClasses, confusionMatrix);

            System.out.println("\n************  Results  ****************************");
            System.out.println("Total test examples: " + size);
            System.out.println("Accuracy: " + df.format(accuracy));
            System.out.println("Precision for class - Who(1): " + df.format(precision1));
            System.out.println("Recall for class - Who(1): " + df.format(recall1) + "\n");
            System.out.println("Precision for class - What(2): " + df.format(precision2));
            System.out.println("Recall for class - What(2): " + df.format(recall2) + "\n");
            System.out.println("Precision for class - When(3): " + df.format(precision3));
            System.out.println("Recall for class - When(3): " + df.format(recall3) + "\n");
            System.out.println("Precision for class - Affirmation(4): " + df.format(precision4));
            System.out.println("Recall for class - Affirmation(4): " + df.format(recall4) + "\n");
            System.out.println("Precision for class - Unknown(5): " + df.format(precision5));
            System.out.println("Recall for class - Unknown(5): " + df.format(recall5));
        }
    }

    /**
     * Returns recall for class represented by classIndex - R = TP / FN + TP
     * @param classIndex
     * @param size
     * @param confusionMatrix
     * @return
     */
    private static Double getRecall(int classIndex, int size, int[][] confusionMatrix){

        int falseNegatives = 0;
        int truePositives = confusionMatrix[classIndex][classIndex];
        for (int i = 0; i < size; i++){
            if (i != classIndex){
                falseNegatives += confusionMatrix[classIndex][i];
            }
        }
        return truePositives == 0 && falseNegatives == 0 ? 0.0 : (truePositives * 1.0)/(truePositives + falseNegatives);
    }

    /**
     * returns Precision for class represented by classIndex - P = TP/ TP+FP
     * @param classIndex
     * @param size
     * @param confusionMatrix
     * @return
     */
    private static Double getPrecision(int classIndex, int size, int[][] confusionMatrix){

        int falsePositives = 0;
        int truePositives = confusionMatrix[classIndex][classIndex];
        for (int i = 0; i < size; i++){
            if ( i != classIndex){
                falsePositives += confusionMatrix[i][classIndex];
            }
        }

        return truePositives == 0 && falsePositives == 0 ? 0.0 : (truePositives * 1.0) / (truePositives + falsePositives);
    }

    /**
     * Temp method for testing
     * @param trainingData
     * @param featuresTraining
     */
    private static void printTrainingFeatures(Map<Integer, String> trainingData,
            Map<Integer, HashMap<Integer, Integer>> featuresTraining){

        int size = trainingData.size();
        for (int i = 0; i< size; i++){
            String[] tokens = trainingData.get(i).split(Constants.SEPARATOR);
            System.out.println(tokens[0]);
            System.out.println("actual label: " + tokens[1]);
            HashMap<Integer, Integer> features = featuresTraining.get(i);
            features.keySet().forEach(k -> System.out.print(k + ":"  + features.get(k) + ","));
            System.out.println("\n");
        }
    }

}



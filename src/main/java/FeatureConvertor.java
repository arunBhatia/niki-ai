package main.java;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to convert data into feature vector
 *
 * Created by Arun bhatia on 22/3/17.
 */
public class FeatureConvertor {

    private String fileName;

    private Map<Integer, HashMap<Integer, Integer>> featureVectorMap = new HashMap<Integer, HashMap<Integer,
            Integer>>();
    private Map<Integer, Integer> labelMap = new HashMap<Integer, Integer>();
    private Map<Integer, String> dataMap = new HashMap<Integer, String>();

    /**
     * Constructor which takes file name as input
     * @param fileName
     */
    public FeatureConvertor(String fileName){
        this.fileName = fileName;
    }

    /**
     * Call this method to convert sentences into features.
     * Each Feature vector is of length 4
     */
    public void extractFeatures(){

        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader("src/main/resources/" + this.fileName + ".txt"));
            String line = null;
            int lineNum = 0;
            while((line = reader.readLine())!=null){
                featureVectorMap.put(lineNum, new HashMap<Integer,Integer>());
                String[] tokens = line.split(Constants.SEPARATOR);
                int label = getLabel(tokens[1]);
                labelMap.put(lineNum, label);
                dataMap.put(lineNum, line);
                featureVectorMap.get(lineNum).putAll(getFeatures(tokens[0]));
                lineNum++;
            }
            reader.close();
        }catch (Exception e){

        }
    }

    /**
     * Populates and returns featureMap. Each feature vector is of length of 4
     * Idea is to look for specific words in the sentence and based on the presence of those words, assign weight to
     * that index in the vector. For e.g. <1,0,0,0> means sentence has one "who" word
     * Each index in the vector represents the class type.
     * @param sentence
     * @return
     */
    private static Map<Integer, Integer> getFeatures(String sentence){

        Map<Integer, Integer> featureMap = new HashMap<Integer, Integer>();
        List<String> tokens = Arrays.asList(sentence.toLowerCase().trim().split(" "));

        if (tokens.get(0).equalsIgnoreCase("is") ||
                tokens.get(0).equalsIgnoreCase("are") ||
                tokens.get(0).equalsIgnoreCase("do") ||
                tokens.get(0).equalsIgnoreCase("does") ||
                tokens.get(0).equalsIgnoreCase("did") ||
                tokens.get(0).equalsIgnoreCase("can") ||
                tokens.get(0).equalsIgnoreCase("could") ||
                tokens.get(0).equalsIgnoreCase("would") ||
                tokens.get(0).equalsIgnoreCase("will") ||
                tokens.get(0).equalsIgnoreCase("has")){
            featureMap.put(4, 1);
        }
        else if (tokens.contains("who")){
            featureMap.put(1, 1);
        }
        else if (tokens.contains("when") || (tokens.contains("what") && tokens.contains("time") && tokens.indexOf
                ("what") < tokens.indexOf("time"))){
            featureMap.put(3, 1);
        }
        else if (tokens.contains("what")){
            featureMap.put(2, 1);
        }

        // For class unknown
        if (featureMap.size() == 0){
            featureMap.put(1, -1);
            featureMap.put(2, -1);
            featureMap.put(3, -1);
            featureMap.put(4, -1);
        }

        return featureMap;
    }


    /**
     * Returns integer map of the actual label
     * 1 - Who
     * 2 - What
     * 3 - When
     * 4 - Affirmation
     * 5 - Unknown
     * @param label
     * @return
     */
    private static int getLabel(String label){

        if (label.equalsIgnoreCase("who")){
            return 1;
        }

        else if (label.equalsIgnoreCase("what")){
            return 2;
        }

        else if (label.equalsIgnoreCase("when")){
            return 3;
        }

        else if(label.equalsIgnoreCase("affirmation")){
            return 4;
        }
        else return 5;
    }


    public Map<Integer, HashMap<Integer, Integer>> getFeatureVectorMap() {
        return featureVectorMap;
    }

    public Map<Integer, Integer> getLabelMap() {
        return labelMap;
    }

    public Map<Integer, String> getDataMap() {
        return dataMap;
    }

}

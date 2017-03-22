package main.java;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;
import libsvm.svm_parameter;
import libsvm.svm_problem;

import java.util.HashMap;
import java.util.Map;

import static libsvm.svm_parameter.C_SVC;
import static libsvm.svm_parameter.RBF;

/**
 * Class which handles all the learning and prediction process via SVM classifier
 *
 * Created by Arun bhatia on 22/3/17.
 */
public class Svm_Classifier {

    /**
     * Returns the svm model after learning from the feature vectors of training dataset
     * @param featuresTraining
     * @param labelTraining
     * @return
     */
    public svm_model trainClassifier(Map<Integer, HashMap<Integer, Integer>> featuresTraining, Map<Integer, Integer> labelTraining){

        // Preparing the SVM param
        svm_parameter param = new svm_parameter();
        param.svm_type = C_SVC;
        param.kernel_type = RBF;
        param.gamma = 0.5;
        param.nu = 0.5;
        param.cache_size = 20000;
        param.C = 1;
        param.eps = 0.001;
        param.p = 0.1;
        svm_problem prob = new svm_problem();
        int numTrainingInstances = featuresTraining.keySet().size();
        prob.l = numTrainingInstances;
        prob.y = new double[prob.l];
        prob.x = new svm_node[prob.l][];

        for(int i = 0;i < numTrainingInstances;i++){
            HashMap<Integer,Integer> featureVector = featuresTraining.get(i);
            prob.x[i]=new svm_node[featureVector.keySet().size()];
            int indx = 0;
            for(Integer id : featureVector.keySet()){
                svm_node node = new svm_node();
                node.index = id;
                node.value = featureVector.get(id);
                prob.x[i][indx] = node;
                indx++;
            }

            prob.y[i] = labelTraining.get(i);
        }

        return svm.svm_train(prob,param);
    }

    /**
     * Given a model and feature vectors from testing data set, predicts type for each sentence
     * @param model
     * @param featuresTesting
     * @return
     */
    public Map<Integer, Integer> testModel(svm_model model, Map<Integer, HashMap<Integer, Integer>> featuresTesting){

        Map<Integer, Integer> resultLabel = new HashMap<Integer, Integer>();

        for(Integer testInstance:featuresTesting.keySet()){
            HashMap<Integer, Integer> featureVector = featuresTesting.get(testInstance);
            int numFeatures = featureVector.keySet().size();
            svm_node[] x = new svm_node[numFeatures];
            int featureIndx=0;
            for(Integer feature : featureVector.keySet()){
                x[featureIndx] = new svm_node();
                x[featureIndx].index = feature;
                x[featureIndx].value = featureVector.get(feature);
                featureIndx++;
            }

            double d = svm.svm_predict(model, x);
            resultLabel.put(testInstance, (int) d);
            // System.out.println(testInstance + "\t" + d);
        }
        return resultLabel;
    }

}

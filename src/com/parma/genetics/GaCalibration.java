package com.parma.genetics;

import java.util.Random;
import java.util.TreeSet;
import java.util.List;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import com.parma.genetics.fitness.FitnessEval;
import com.parma.genetics.settings.GaSettings;

public class GaCalibration {

  private String tablePath;

  private Population population;

  private int safeboxSize;

  private TreeSet<ParamIndividual> safebox;

  private GaSettings settings;

  private Hashtable<String, Float> params;

  private final Random random = new Random();

  public GaCalibration(GaSettings settings) {
    this.settings = settings;
    this.population = new Population(settings);
    this.population.initializePopulation(settings.getMaxIndividuals());
    this.safeboxSize = Math.max(1, (int) ((float) settings.getMaxIndividuals() * 0.2));
    this.safebox = new TreeSet<ParamIndividual>();
    this.settings.setSelectionThreshold((float) 0.6);
    this.params = loadStoredParams();
    this.tablePath = settings.getLocation()+ "t.tmp";
    header();
  }

  private Hashtable<String, Float> loadStoredParams() {

    try {
      FileInputStream fis = new FileInputStream(tablePath);
      ObjectInputStream ois = new ObjectInputStream(fis);
      Object tableObject = ois.readObject();
      if (tableObject instanceof java.util.Hashtable<?, ?>) {
        Hashtable<String, Float> table = (Hashtable<String, Float>) tableObject;
        return table;
      } else {
        return new Hashtable<String, Float>();
      }

    } catch (Exception e) {
      return new Hashtable<String, Float>();
    }
  }

  private void storeParams() {

    try {
      FileOutputStream fos = new FileOutputStream(tablePath);
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      oos.writeObject(params);
      oos.close();

    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  public void runCalibration() {


    ParamIndividual bestIndividual = new ParamIndividual();
    CrossoverOperator crossover = new CrossoverOperator(settings.getCrossoverType());

    // run GA for the number of generations specified in settings
    for (int gen = 0; gen < settings.getMaxGenerations(); gen++) {

      /* fitness function step */

      calculatePopulationFitness(gen);
      population.sortByFitness();

      bestIndividual = population.getIndividual(0);

      log(gen, getAverageFitness(), bestIndividual.getFitness(), bestIndividual.getW(),
          bestIndividual.getW_n(), bestIndividual.getSigma_r(), bestIndividual.getLambda());

      safebox.add(bestIndividual);

      if (safebox.size() > this.safeboxSize) {
        safebox.remove(safebox.last());
      }

      normalizePopulationFitness();

      List<ParamIndividual> selectionIndividuals = getSelectionIndividuals();

      List<ParamIndividual> offspring =
          crossover.cross(selectionIndividuals, (int) (settings.getMaxIndividuals() / 2));

      population.update(offspring);

      applyMutation();

      storeParams();

    }

    bestIndividual = safebox.first();

  }


  public Population getPopulation() {
    return population;
  }


  public void setPopulation(Population population) {
    this.population = population;
  }


  private float getAverageFitness() {
    float averageFitness = 0;
    for (int ind = 0; ind < settings.getMaxIndividuals(); ind++) {
      averageFitness += population.getIndividual(ind).getFitness();
    }
    averageFitness = averageFitness / settings.getMaxIndividuals();
    return averageFitness;
  }


  private void calculatePopulationFitness(int gen) {

    for (int ind = 0; ind < settings.getMaxIndividuals(); ind++) {

      ParamIndividual p = population.getIndividual(ind);
      FitnessEval fitEval =
          new FitnessEval(settings.getFitnessFunction(), settings.getSegmentationTechnique());

      String key = p.toString();
      Float value = params.get(key);
      float score = 0;

      if (value != null) {
        // if the value is in the table, don't calculate it again
        score = value;

      } else {
        // calculate fitness score for every (image, ground_truth) pair provided
        for (int index = 0; index < settings.getSampleCount(); index++) {
          score += fitEval.evaluate(p, settings.getOriginalImage(index),
              settings.getGroundtruthImage(index));
        }
        // calculate the mean score for the samples
        score = score / (float) settings.getSampleCount();

        // save it to the params set
        params.put(key, score);
      }

      // LOG this individual's score
      System.out
          .println("Key: " + String.format("%1$-" + 7 + "s", key) + " | Score: " + score + ";");

      population.getIndividual(ind).setFitness(score);
    }
  }


  private void normalizePopulationFitness() {
    float accumulatedFitness = getAccumulatedFitness();
    for (int ind = 0; ind < settings.getMaxIndividuals(); ind++) {
      ParamIndividual p = population.getIndividual(ind);
      float normFitness = (float) (p.getFitness() / accumulatedFitness);
      p.setFitness(normFitness);
    }
  }


  private List<ParamIndividual> getSelectionIndividuals() {
    float individualAccumulatedFitness = 1;
    List<ParamIndividual> selectedIndividuals = new ArrayList<ParamIndividual>();
    float threshold = settings.getSelectionThreshold();

    for (int index = 0; individualAccumulatedFitness >= threshold
        && index < population.getSize(); index++) {

      ParamIndividual p = population.getIndividual(index);
      selectedIndividuals.add(p);
      individualAccumulatedFitness -= p.getFitness();
    }

    return selectedIndividuals;
  }


  private void applyMutation() {
    Mutator mutator = new Mutator(settings.getMutationType());
    float mutationFactor = random.nextFloat();
    for (int index = 0; index < settings.getMaxIndividuals(); index++) {
      if (mutationFactor <= settings.getMutationPerc()) {
        ParamIndividual p = population.getIndividual(index);
        mutator.mutate(p);
      }
      mutationFactor = random.nextFloat();
    }
  }


  private float getAccumulatedFitness() {
    float accumulatedFitness = 0;
    for (int ind = 0; ind < settings.getMaxIndividuals(); ind++) {
      ParamIndividual p = population.getIndividual(ind);
      accumulatedFitness += p.getFitness();
    }
    return accumulatedFitness;
  }

  // simple console reports

  public void header() {
    System.out.println("generation,average_fitness,best_fitness,best_w,best_w_n,best_s_r,best_lambda");
  }

  public void log(int gen, float avgf, float bestf, int bestw, int bestwn, int bestsr, float bestlambda) {
    System.out.println(gen + "," + avgf + "," + bestf + "," + bestw + "," + bestwn + "," + bestsr + "," + bestlambda);
  }

}

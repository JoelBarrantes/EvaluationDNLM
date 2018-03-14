package com.parma.genetics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.parma.genetics.settings.GaSettings;

public class Population {

  private List<ParamIndividual> population;

  // safeguard the best individuals encountered
  private List<ParamIndividual> safebox;

  private final Random random = new Random();

  private final GaSettings settings;

  public Population() {
    this.settings = new GaSettings();
  }
  public Population(GaSettings settings) {
    this.population = new ArrayList<ParamIndividual>();
    this.safebox = new ArrayList<ParamIndividual>();
    this.settings = settings;
  }

  public void initializePopulation(int maxInd) {
    for (int i = 0; i < maxInd; i++) {
      ParamIndividual p = new ParamIndividual();

      p.setW(getRandomIntegerBetween(settings.getLowerW(), settings.getUpperW()));

      p.setW_n(getRandomIntegerBetween(settings.getLowerWn(), settings.getUpperWn()));

      p.setSigma_r(getRandomIntegerBetween(settings.getLowerSigmaR(), settings.getUpperSigmaR()));
      
     p.setLambda(getRandomFloatBetween(settings.getLowerLambda(), settings.getUpperLambda()));

      population.add(p);
    }

  }
  
  
  public void sortByFitness() {
    Collections.sort(population);
  }
  
  public ParamIndividual getIndividual(int index) {
    return this.population.get(index);
  }

  public void addBestToSafebox() {    
    safebox.add(population.get(0));
  }

  public void removeWorstFromSafebox() {   
    safebox.remove(safebox.size() - 1);
  }

  public int getSize() {
    return this.population.size();
  }

  private int getRandomIntegerBetween(int lower, int upper) {
    return random.nextInt(upper - lower + 1) + lower;
  }
  
  public float getRandomFloatBetween(float lower, float upper) {
    return lower+(upper-lower)*random.nextFloat();
  }

  public void update(List<ParamIndividual> offspring) {
	population.subList(getSize()- offspring.size(), getSize()).clear();
	population.addAll(offspring);
  }

}

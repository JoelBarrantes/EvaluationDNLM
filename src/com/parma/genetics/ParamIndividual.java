package com.parma.genetics;

/**
 * 
 * @author Joel Schuster
 *
 */

public class ParamIndividual implements Comparable<ParamIndividual> {

  private int w;
  private int w_n;
  private int sigma_r;
  private float fitness;
  private float lambda;

  public ParamIndividual() {
    this.setFitness(0);
  }

  public int getW() {
    return w;
  }

  public void setW(int w) {
    this.w = w;
  }

  public int getW_n() {
    return w_n;
  }

  public void setW_n(int w_n) {
    this.w_n = w_n;
  }

  public int getSigma_r() {
    return sigma_r;
  }

  public void setSigma_r(int sigma_r) {
    this.sigma_r = sigma_r;
  }

  public float getFitness() {
    return fitness;
  }

  public void setFitness(float fitness) {
    this.fitness = fitness;
  }

  /**
   * Implement Comparator. Individuals with a higher fitness will be ordered higher.
   */

  @Override
  public int compareTo(ParamIndividual other) {
    return Double.compare(other.fitness, this.fitness);
  }

  @Override
  public String toString() {
    return String.valueOf(this.getW()) + "," + String.valueOf(this.getW_n()) + ","
        + String.valueOf(this.getSigma_r()) + "," + String.valueOf(this.getLambda());

  }

  public float getLambda() {
    return (float) Math.round((lambda ) * 100) / 100;
  }

  public void setLambda(float lambda) {
    this.lambda = lambda;
  }
}

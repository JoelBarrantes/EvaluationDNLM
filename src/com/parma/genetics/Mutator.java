package com.parma.genetics;

import java.util.Random;
import com.parma.genetics.settings.Mutation;

public class Mutator {

  private Mutation type;
  private final Random random = new Random();

  public Mutator(Mutation mutationType) {
    setType(mutationType);
  }

  public Mutation getType() {
    return type;
  }

  public void setType(Mutation type) {
    this.type = type;
  }


  public void mutate(ParamIndividual p) {

    if (type == Mutation.RANDOM_BIT) {
      int bitPlace = random.nextInt(2);
      int param = random.nextInt();
      if (param == 0) {
        int newW = p.getW() ^ (1 << bitPlace);
        p.setW(newW);
      }
      if (param == 1) {
        int newW_n = p.getW_n() ^ (1 << bitPlace);
        p.setW_n(newW_n);
      }
      if(param == 2){
        float lambda=p.getLambda();
        if (bitPlace == 0) {
          lambda+=0.5;
        } else {
          lambda-=0.5;
        }
        p.setLambda(lambda);
      } else {
        int newSigma_r = p.getSigma_r() ^ (1 << bitPlace);
        p.setSigma_r(newSigma_r);
      }
    }
    
  }

}

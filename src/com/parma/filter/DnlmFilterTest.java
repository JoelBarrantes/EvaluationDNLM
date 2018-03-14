package com.parma.filter;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.opencv.core.Mat;
import com.parma.genetics.scoring.Dice;

import com.parma.genetics.settings.Fitness;
import com.parma.genetics.settings.Segmentation;
import com.parma.images.ImageHandler;
import com.parma.segmentation.Thresholder;
import com.parma.genetics.ParamIndividual;
import com.parma.genetics.fitness.FitnessEval;


public class DnlmFilterTest {

  @Test
  public void testFilter() {

    String absdir = "C:\\Users\\Eliot\\Documents\\Github\\GeneticAlgorithmDNLM\\GeneticAlgorithmDNLM\\images\\";
    ImageHandler imageHandler = new ImageHandler();
    Mat imagengd1 = imageHandler.leerImagenGrises(absdir + "GT\\1.png");
    Mat imagen1 = imageHandler.leerImagenGrises(absdir + "RECORTADAS\\1.tif");

    ParamIndividual p = new ParamIndividual();
    p.setW(17);
    p.setW_n(3);
    p.setSigma_r(277);
    p.setLambda(Float.valueOf("1.6"));
    
    FitnessEval fit = new FitnessEval(Fitness.DICE, Segmentation.OTSU);
    double dice = fit.evaluate(p, imagen1, imagengd1);
    
    
    System.out.println(dice);
    


  }

}

package com.parma.genetics.scoring;

import static org.junit.Assert.*;
import org.junit.Test;
import org.opencv.core.Mat;
import com.parma.genetics.ParamIndividual;
import com.parma.genetics.fitness.FitnessEval;
import com.parma.genetics.settings.Fitness;
import com.parma.genetics.settings.Segmentation;
import com.parma.images.ImageHandler;

public class DiceScoreTest {

  @Test
  public void test() {
    
    String absdir = "";     
    
    // cargar las imágenes con sus groundtruths
    ImageHandler imageHandler = new ImageHandler();
    Mat imagen1 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/1.png");
    Mat imagengd1 = imageHandler.leerImagenGrises(absdir + "images/GT/1.png");   

    Mat imagen2 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/2.png");
    Mat imagengd2 = imageHandler.leerImagenGrises(absdir + "images/GT/2.png");    

    Mat imagen3 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/3.png");
    Mat imagengd3 = imageHandler.leerImagenGrises(absdir + "images/GT/3.png");
    
    Mat imagen4 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/4.png");
    Mat imagengd4 = imageHandler.leerImagenGrises(absdir + "images/GT/4.png");       
    
    // el individuo con valores de parámetros
    ParamIndividual p = new ParamIndividual();
    p.setW(17);
    p.setW_n(3);
    p.setSigma_r(277);
    p.setLambda(1.6f);
    
    // evaluación por score de Sorensen-Dice segmentado por Otsu 
    FitnessEval fitEval = new FitnessEval(Fitness.DICE, Segmentation.OTSU);
       
    double dice1 = fitEval.evaluate(p, imagen1, imagengd1);       
    System.out.println("Dice 1: " + dice1);
    
    double dice2 = fitEval.evaluate(p, imagen2, imagengd2);
    System.out.println("Dice 2: " + dice2);
    
    double dice3 = fitEval.evaluate(p, imagen3, imagengd3);
    System.out.println("Dice 3: " + dice3);
    
    double dice4 = fitEval.evaluate(p, imagen4, imagengd4);
    System.out.println("Dice 4: " + dice4);
    
    // Promedio de los 4 resultados
    System.out.println("Dice avg: " + (dice1 + dice2 + dice3 + dice4)/4.0f);
    
  }
  

}

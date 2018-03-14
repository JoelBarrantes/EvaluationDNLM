package com.parma;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.opencv.core.Mat;
import com.parma.genetics.GaCalibration;
import com.parma.genetics.settings.Crossover;
import com.parma.genetics.settings.Fitness;
import com.parma.genetics.settings.GaSettings;
import com.parma.genetics.settings.Mutation;
import com.parma.genetics.settings.Segmentation;
import com.parma.images.ImageHandler;

public class Main {

  public static void main(String args[]) {
    String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
    System.out.println(timeStamp);
    Crossover crossover = Crossover.SIMPLE;
    int mIndividuos = 10;
    float mutPerc = 0.05f;
    int mGenerations = 25;
    
    if (args.length == 4) {
      
      switch (args[0]) {
        case "c":
          crossover = Crossover.CLUSTER;
          break;
        case "b":
          crossover = Crossover.BITWISE;
          break;
        default:
          break;
      }
      mIndividuos = Integer.parseInt(args[1]);
      mutPerc = (float) Integer.parseInt(args[2]) / 100.0f;
      mGenerations = Integer.parseInt(args[3]);
      
    }
    
    try {
      GaSettings settings = new GaSettings();

      settings.setCrossoverType(crossover);//ARG
      settings.setFitnessFunction(Fitness.DICE);
      settings.setLowerW(1);
      settings.setUpperW(21);
      settings.setLowerWn(3);
      settings.setUpperWn(3);
      settings.setLowerSigmaR(1);
      settings.setUpperSigmaR(500);
      settings.setLowerLambda(1);
      settings.setUpperLambda(30);
      settings.setMaxGenerations(mGenerations); 
      settings.setMaxIndividuals(mIndividuos);//ARG
      settings.setMutationPerc((float) mutPerc);//ARG
      settings.setMutationType(Mutation.RANDOM_BIT);
      settings.setSegmentationTechnique(Segmentation.OTSU); 
      settings.setSelectionThreshold((float) 0.6);

      String absdir = "";
      if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
        absdir = "/home/jdnlm/DnlmTests/GeneticAlgorithmDNLM/";
      }

      settings.setLocation(absdir);

      ImageHandler imageHandler = new ImageHandler();
      Mat imagen1 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/1.png");
      Mat imagengd1 = imageHandler.leerImagenGrises(absdir + "images/GT/1.png");

      settings.addToOriginalImages(imagen1);
      settings.addToGroundtruthImages(imagengd1);

      Mat imagen2 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/2.png");
      Mat imagengd2 = imageHandler.leerImagenGrises(absdir + "images/GT/2.png");

      settings.addToOriginalImages(imagen2);
      settings.addToGroundtruthImages(imagengd2);

      Mat imagen3 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/3.png");
      Mat imagengd3 = imageHandler.leerImagenGrises(absdir + "images/GT/3.png");

      settings.addToOriginalImages(imagen3);
      settings.addToGroundtruthImages(imagengd3);
      
      Mat imagen4 = imageHandler.leerImagenGrises(absdir + "images/Recortadas/4.png");
      Mat imagengd4 = imageHandler.leerImagenGrises(absdir + "images/GT/4.png");

      settings.addToOriginalImages(imagen4);
      settings.addToGroundtruthImages(imagengd4);

      GaCalibration calibration = new GaCalibration(settings);

      calibration.runCalibration();
      
      String timeStamp2 = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
      System.out.println(timeStamp2);
      
    } catch (Exception ex) {
      System.err.println(ex.getCause() + " - " + ex.getMessage() + " - " + ex.getStackTrace());
    }

  }

}

package com.parma.filter;
import com.parma.genetics.ParamIndividual;
import com.parma.genetics.fitness.FitnessEval;
import com.parma.genetics.settings.Fitness;
import com.parma.genetics.settings.Segmentation;
import com.parma.images.ImageHandler;
import org.opencv.core.Mat;

public class FilterImage {

  public static void main(String[] args) {
    
    ImageHandler ih = new ImageHandler(); 
    
    String absdir = "";
    if (!System.getProperty("os.name").toLowerCase().contains("windows")) {
      absdir = "/home/jdnlm/DnlmTests/EvaluationDNLM/";
    }
    
    
    String imagePath = args[0];
    
    Mat image = ih.leerImagenGrises(absdir+"CLAHE/" + imagePath +".tif");
    Mat groundtruth = ih.leerImagenGrises(absdir+"GT/" + imagePath +".png");
    
    ParamIndividual p = new ParamIndividual();
    p.setW(Integer.valueOf(args[1]));
    p.setW_n(Integer.valueOf(args[2]));
    p.setSigma_r(Integer.valueOf(args[3]));
    p.setLambda(Float.valueOf(args[4]));

    FitnessEval f = new FitnessEval(Fitness.DICE, Segmentation.OTSU);
    
    float dice = f.evaluate(p, image, groundtruth, imagePath);
    System.out.println(String.valueOf(dice));
  }

}

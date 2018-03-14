package com.parma.segmentation;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Clase experta en umbralización de las imágenes
 * 
 * @author Joel Barrantes
 */
public class Thresholder {

  /**
   * 
   * @param image
   */
  public static void applyOtsuThreshold(Mat image) {  
    image.convertTo(image, CvType.CV_8UC1);
    Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);
  }

  /**
   * Umbraliza la image con OpenCV de forma binaria
   * 
   * @param image matriz OpenCV de la image
   * @param t valor de umbral para definir blancos y negros
   */
  public static void applyThreshold(Mat image, int t) {
    Imgproc.threshold(image, image, t, 255, Imgproc.THRESH_BINARY);
  }

  /**
   * Umbraliza la image con OpenCV de forma binaria e inversa
   * 
   * @param image matriz OpenCV de la image
   * @param t valor de umbral para definir blancos y negros
   */
  public static void applyInverseThreshold(Mat image, int t) {
    Imgproc.threshold(image, image, t, 255, Imgproc.THRESH_BINARY_INV);
  }


}

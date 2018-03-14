package com.parma.images;

import org.opencv.core.Core;

import org.opencv.core.Mat;

import org.opencv.imgcodecs.Imgcodecs;

public class ImageHandler {

  /**
   * Carga una imagen en 3 canales desde ubicación en disco.
   * 
   * @param ruta ubicación de la imagen en disco
   * @return imagen matriz de OpenCV en RGB (a 3 canales)
   */
  
  public Mat leerImagenColor(String ruta) {
    try {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      Mat imagen = Imgcodecs.imread(ruta, Imgcodecs.IMREAD_ANYCOLOR);
      return imagen;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Carga una imagen desde ubicación en disco considerando solo la
   * información en escala de grises (8 bits).
   * 
   * @param ruta ubicación de la imagen en disco
   * @return imagen matriz de OpenCV de 8 bits en escala de grises
   */
  public Mat leerImagenGrises(String ruta) {
    try {
      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
      Mat imagen = Imgcodecs.imread(ruta, Imgcodecs.IMREAD_GRAYSCALE);
      return imagen;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Guarda una imagen en la ruta especificada
   * 
   * @param ruta ruta donde almacenar imagen en disco
   * @param nombreImagen nombre para asignar a imagen guardada
   * @param formato formato de imagen (sin puntos, ej. 'bmp', 'jpg')
   * @param imagen matriz de OpenCV con datos de imagen
   */
  public void guardarImagen(String ruta, String nombreImagen, String formato, Mat imagen) {
    try {
      Imgcodecs.imwrite(ruta + "/" + nombreImagen + "." + formato, imagen);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Sobreescribe una imagen en la ruta especificada.
   * 
   * @param ruta ruta donde almacenar imagen en disco 
   * @param imagen matriz de OpenCV con datos de imagen
   */
  public void sobreescribirImagen(String ruta, Mat imagen) {
    try {
      Imgcodecs.imwrite(ruta, imagen);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}

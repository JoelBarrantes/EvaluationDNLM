package com.parma.filter;

import java.util.ArrayList;
import java.util.stream.IntStream;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class DnlmFilter {

  public Mat filter(Mat I, double w, double w_n, double sigma_r, double lambda) {

    Mat G = new Mat(I.size(), CvType.CV_64FC1);
    I.copyTo(G);
    G.convertTo(G, CvType.CV_64FC1);

    int size_x = G.rows();
    int size_y = G.cols();

    Mat R = Mat.zeros(size_x, size_y, CvType.CV_64FC1);

    Mat II2 = Mat.zeros(new Size(size_x, size_y), CvType.CV_64FC1);
    Imgproc.integral(G.mul(G), II2, CvType.CV_64FC1);

    double sigma_s = w / 1.5;
    Mat X = new Mat(), Y = new Mat();
    Range r = new Range(new double[] {-w, w});
    meshgrid(r, r, X, Y);

    X = X.mul(X);
    Y = Y.mul(Y);
    Mat S = new Mat(X.size(), CvType.CV_64FC1);
    Core.add(X, Y, S);
    Core.multiply(S, new Scalar(-1.0 / (2.0 * sigma_s * sigma_s)), S);

    Mat GaussW = new Mat(S.size(), CvType.CV_64FC1);
    S.copyTo(GaussW);
    Core.exp(GaussW, GaussW);

    Mat U = NoAdaptativeUSM(G, lambda, 17, 0.005);

    IntStream iterations = IntStream.range(0, size_x);

    iterations.parallel().forEach(i -> {

      for (int j = 0; j < size_y; j++) {

        if ((i > w + w_n) && (j > w + w_n) && (i < size_x - w - w_n) && (j < size_y - w - w_n)) {
          // extract local
          int iMin = (int) Math.max(i - w - w_n, 0);
          int iMax = (int) Math.min(i + w + w_n, size_x - 1);
          int jMin = (int) Math.max(j - w - w_n, 0);
          int jMax = (int) Math.min(j + w + w_n, size_y - 1);

          // get current window
          Mat I_t = G.submat(iMin - 1, iMax, jMin - 1, jMax);

          int sizeW_x = I_t.width();
          int sizeW_y = I_t.height();

          // create output matrix
          Mat O = Mat.zeros((int) (sizeW_x - 2 * w_n), (int) (sizeW_y - 2 * w_n), CvType.CV_64FC1);

          // extract pixel neighborhood P local region
          int mMin_p = (int) (i - w_n - 1);
          int mMax_p = (int) (i + w_n - 1);
          int nMin_p = (int) (j - w_n - 1);
          int nMax_p = (int) (j + w_n - 1);

          // get sum of squad neighborhood P
          double sum_p = II2.get(mMin_p, nMin_p)[0] + II2.get(mMax_p + 1, nMax_p + 1)[0]
              - II2.get(mMin_p, nMax_p + 1)[0] - II2.get(mMax_p + 1, nMin_p)[0];

          // get current neighborhood p
          Mat neighbor_p = G.submat(mMin_p, mMax_p + 1, nMin_p, nMax_p + 1);
          neighbor_p.convertTo(neighbor_p, CvType.CV_64FC1);

          int sizeP_x = neighbor_p.width();
          int sizeP_y = neighbor_p.height();

          // perform correlation
          // output size
          int mm = sizeW_x + sizeP_x - 1;
          int nn = sizeW_y + sizeP_y - 1;

          // pad, multiply and transform back
          // C_a fft2
          Mat padded = Mat.zeros(new Size(mm, nn), CvType.CV_64FC1);
          I_t.copyTo(padded);          
          
          Core.copyMakeBorder(padded, padded, 0, nn - sizeW_y, 0, nn - sizeW_x,
              Core.BORDER_CONSTANT, Scalar.all(0));
          ArrayList<Mat> planes = new ArrayList<Mat>();
          planes.add(padded.clone());
          planes.add(Mat.zeros(padded.size(), CvType.CV_64F));
          Mat complexI_a = new Mat(padded.size(), CvType.CV_64FC2);
          Core.merge(planes, complexI_a);
          Core.dft(complexI_a, complexI_a);
          planes.clear();

          // C_b fft2 + flip
          padded = Mat.zeros(new Size(mm, nn), CvType.CV_64FC1);
          neighbor_p.copyTo(padded);                    
          
          Core.flip(padded, padded, -1); // rot90 2 times
          Core.copyMakeBorder(padded, padded, 0, nn - sizeP_y, 0, nn - sizeP_x,
              Core.BORDER_CONSTANT, Scalar.all(0));
          planes.add(padded);
                             
          planes.add(Mat.zeros(padded.size(), CvType.CV_64F));
          Mat complexI_b = new Mat(padded.size(), CvType.CV_64FC2);
          Core.merge(planes, complexI_b);
          Core.dft(complexI_b, complexI_b);
          planes.clear();

          // complex C
          Mat C = new Mat(complexI_b.size(), CvType.CV_64FC2);
          Core.mulSpectrums(complexI_a, complexI_b, C, Core.DFT_ROWS);          
          
          Core.idft(C, C, Core.DFT_COMPLEX_OUTPUT + Core.DFT_SCALE, 0);
          Core.extractChannel(C, C, 0); // extract REAL channel

          // padding constants (for output of size == size(A))
          int padC_m = (int) Math.ceil((sizeP_x - 1) / 2.0);
          int padC_n = (int) Math.ceil((sizeP_y - 1) / 2.0);
          // convolution result
          Mat correlation = C.submat(padC_m, sizeW_x + padC_m, padC_n, sizeW_y + padC_n);          

          for (int m = (int) w_n; m < sizeW_x - w_n; m++) {
            int mMin_w = (int) (iMin + m - 1 - w_n);
            int mMax_w = (int) (iMin + m - 1 + w_n);

            for (int n = (int) w_n; n < sizeW_y - w_n; n++) {
              int nMin_w = (int) (jMin + n - 1 - w_n);
              int nMax_w = (int) (jMin + n - 1 + w_n);
              double sum_w = II2.get(mMin_w, nMin_w)[0] + II2.get(mMax_w + 1, nMax_w + 1)[0]
                  - II2.get(mMin_w, nMax_w + 1)[0] - II2.get(mMax_w + 1, nMin_w)[0];
              O.put((int) (m - w_n), (int) (n - w_n), sum_p + sum_w - 2 * correlation.get(m, n)[0]);
            }
          }                   

          Core.multiply(O, new Scalar(1.0 / (-2.0 * sigma_r * sigma_r)), O);
          Core.exp(O, O);
          O = O.mul(GaussW);

          double norm_factor = Core.sumElems(O).val[0];

          Mat OxU_sub = U.submat((int) (iMin + w_n - 1), (int) (iMax - w_n), (int) (jMin + w_n - 1),
              (int) (jMax - w_n));

          OxU_sub = OxU_sub.mul(O);
                    
          R.put(i - 1, j - 1, Core.sumElems(OxU_sub).val[0] / norm_factor);
          
          I_t.release();
          O.release();
          neighbor_p.release();
          padded.release();
          complexI_a.release();
          complexI_b.release();
          C.release();
          correlation.release();
          OxU_sub.release();
         
        }
      }
    });
    
    G.release();
    II2.release();
    X.release();
    Y.release();
    S.release();
    GaussW.release();
    U.release();
    
    return R;

  } // end DNLM-IFFT Filter

  /**
   * No adaptative laplacian. CURRENT VERSION HAS HARDCODED KERNEL
   */
  private Mat NoAdaptativeUSM(Mat SrcImage, double lambda, int kernelSize,
      double kernelSigma) {
    Mat kernel = new Mat(kernelSize, kernelSize, CvType.CV_64FC1);
    for (int i = 0; i < kernelSize; i++) {
      for (int j = 0; j < kernelSize; j++) {
        double value = (i == 8 && j == 8) ? 79723.18339100346 : -276.8166089965398;
        kernel.put(i, j, value);
      }
    }
    Mat Z = new Mat(SrcImage.size(), CvType.CV_64FC1);
    Imgproc.filter2D(SrcImage, Z, -1, kernel, new Point(-1, -1), 0, Core.BORDER_CONSTANT);

    double maxZ = 0, maxSrc = 0;
    // get max (abs) of Z
    for (int i = 0; i < Z.width(); i++) {
      for (int j = 0; j < Z.height(); j++) {
        double currVal = Math.abs(Z.get(j, i)[0]);
        if (currVal > maxZ)
          maxZ = currVal;
      }
    }
    // get max of SrcImage
    for (int i = 0; i < SrcImage.width(); i++) {
      for (int j = 0; j < SrcImage.height(); j++) {
        double currVal = SrcImage.get(j, i)[0];
        if (currVal > maxSrc)
          maxSrc = currVal;
      }
    }
    Core.multiply(Z, new Scalar(lambda * maxSrc / maxZ), Z, 1.0, CvType.CV_64FC1);

    Mat U = new Mat(SrcImage.size(), CvType.CV_64FC1);
    Core.add(SrcImage, Z, U, Mat.ones(SrcImage.size(), CvType.CV_8UC1), CvType.CV_64FC1);

    return U;
  } // end NoAdaptativeUSM

  /**
   * Meshgrid generator
   */
  private void meshgrid(Range xgv, Range ygv, Mat X, Mat Y) {
    double[] t_x = new double[xgv.size() + 1];
    double[] t_y = new double[ygv.size() + 1];
    for (int i = xgv.start; i <= xgv.end; i++) {
      int index = i - xgv.start;
      t_x[index] = i;
    }
    for (int i = ygv.start; i <= ygv.end; i++) {
      int index = i - ygv.start;
      t_y[index] = i;
    }
    Mat xgvM = new Mat(new Size(1, t_x.length), CvType.CV_64FC1);
    xgvM.put(0, 0, t_x);
    Mat ygvM = new Mat(new Size(1, t_y.length), CvType.CV_64FC1);
    ygvM.put(0, 0, t_y);
    Core.repeat(xgvM.reshape(1, 1), (int) ygvM.total(), 1, X);
    Core.repeat(ygvM.reshape(1, 1).t(), 1, (int) xgvM.total(), Y);
  } // end Meshgrid

} /* end DnlmFilter */


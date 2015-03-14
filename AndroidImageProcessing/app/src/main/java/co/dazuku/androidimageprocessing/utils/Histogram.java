package co.dazuku.androidimageprocessing.utils;

import android.util.Log;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by dazuku on 3/14/15.
 */
public class Histogram {
    private int R[];
    private int G[];
    private int B[];

    private float Rn[];
    private float Gn[];
    private float Bn[];

    public float[] getRedNormalized() {
        return Rn;
    }

    public float[] getGreenNormalized() {
        return Gn;
    }

    public float[] getBlueNormalized() {
        return Bn;
    }

    public int[] getRed() {
        return R;
    }

    public void setRed(int[] r) {
        R = r;
    }

    public int[] getGreen() {
        return G;
    }

    public void setGreen(int[] g) {
        G = g;
    }

    public int[] getBlue() {
        return B;
    }

    public void setBlue(int[] b) {
        B = b;
    }

    public Histogram() {
        R = new int[256];
        G = new int[256];
        B = new int[256];

        Arrays.fill(R, 0);
        Arrays.fill(G, 0);
        Arrays.fill(B, 0);
    }

    public Histogram(int[] r, int[] g, int[] b) {
        R = r;
        G = g;
        B = b;
    }

    public void normalize() {
        normalize(1.0f);
    }

    public static int findMax(int[] array) {
        int indexOfMax = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > array[indexOfMax]) {
                indexOfMax = i;
            }
        }
        return array[indexOfMax];
    }

    public void normalize(float num) {
        Rn = new float[R.length];
        Gn = new float[G.length];
        Bn = new float[B.length];

        float maxR = findMax(R);
        float maxG = findMax(G);
        float maxB = findMax(B);

        for(int i = 0; i < R.length; i++) {
            Rn[i] = (float) R[i] / maxR * num;
            Gn[i] = (float) G[i] / maxG * num;
            Bn[i] = (float) B[i] / maxB * num;
        }

        Log.e("ImageProcessing", "Rn: " + Arrays.toString(Rn));
    }
}

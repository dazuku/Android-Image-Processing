package co.dazuku.androidimageprocessing.utils;

import java.util.ArrayList;
import java.util.List;

public  class CurveComposition {
    private List<KeyPoint> red;
    private List<KeyPoint> green;
    private List<KeyPoint> blue;
    private List<KeyPoint> composition;

    public CurveComposition() {
        this.red = new ArrayList<>();
        this.green = new ArrayList<>();
        this.blue = new ArrayList<>();
        this.composition = new ArrayList<>();
    }

    public void addRedKeyPoint(KeyPoint keyPoint) {
        this.red.add(keyPoint);
    }

    public void addGreenKeyPoint(KeyPoint keyPoint) {
        this.green.add(keyPoint);
    }

    public void addBlueKeyPoint(KeyPoint keyPoint) {
        this.blue.add(keyPoint);
    }

    public void addCompositionKeyPoint(KeyPoint keyPoint) {
        this.composition.add(keyPoint);
    }

    public void setRed(List<KeyPoint> red) {
        this.red = red;
    }

    public void setGreen(List<KeyPoint> green) {
        this.green = green;
    }

    public void setBlue(List<KeyPoint> blue) {
        this.blue = blue;
    }

    public void setComposition(List<KeyPoint> composition) {
        this.composition = composition;
    }

    public List<KeyPoint> getRed() {
        return red;
    }

    public List<KeyPoint> getGreen() {
        return green;
    }

    public List<KeyPoint> getBlue() {
        return blue;
    }

    public List<KeyPoint> getComposition() {
        return composition;
    }
}
   
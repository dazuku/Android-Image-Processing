package co.dazuku.androidimageprocessing.utils;

public class KeyPoint {
    private float x;
    private float y;

    public KeyPoint() {
        this.x = 0.0f;
        this.y = 0.0f;
    }

    public KeyPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
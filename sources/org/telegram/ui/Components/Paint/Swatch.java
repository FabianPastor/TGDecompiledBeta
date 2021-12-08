package org.telegram.ui.Components.Paint;

public class Swatch {
    public float brushWeight;
    public int color;
    public float colorLocation;

    public Swatch(int color2, float colorLocation2, float brushWeight2) {
        this.color = color2;
        this.colorLocation = colorLocation2;
        this.brushWeight = brushWeight2;
    }
}

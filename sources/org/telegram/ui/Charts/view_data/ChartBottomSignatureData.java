package org.telegram.ui.Charts.view_data;

public class ChartBottomSignatureData {
    public int alpha;
    public int fixedAlpha = 255;
    public final int step;
    public final int stepMax;
    public final int stepMin;

    public ChartBottomSignatureData(int step2, int stepMax2, int stepMin2) {
        this.step = step2;
        this.stepMax = stepMax2;
        this.stepMin = stepMin2;
    }
}

package org.telegram.ui.Charts.view_data;

import org.telegram.messenger.AndroidUtilities;

public class ChartHorizontalLinesData {
    public int alpha;
    public int fixedAlpha;
    public int[] values;
    public String[] valuesStr;
    public String[] valuesStr2;

    public ChartHorizontalLinesData(int newMaxHeight, int newMinHeight, boolean useMinHeight) {
        this(newMaxHeight, newMinHeight, useMinHeight, 0.0f);
    }

    public ChartHorizontalLinesData(int newMaxHeight, int newMinHeight, boolean useMinHeight, float k) {
        float step;
        int n;
        int n2;
        this.fixedAlpha = 255;
        boolean skipFloatValues = false;
        if (!useMinHeight) {
            int v = newMaxHeight > 100 ? round(newMaxHeight) : newMaxHeight;
            int step2 = Math.max(1, (int) Math.ceil((double) (((float) v) / 5.0f)));
            if (v < 6) {
                n2 = Math.max(2, v + 1);
            } else if (v / 2 < 6) {
                n2 = (v / 2) + 1;
                if (v % 2 != 0) {
                    n2++;
                }
            } else {
                n2 = 6;
            }
            this.values = new int[n2];
            this.valuesStr = new String[n2];
            for (int i = 1; i < n2; i++) {
                int[] iArr = this.values;
                iArr[i] = i * step2;
                this.valuesStr[i] = AndroidUtilities.formatWholeNumber(iArr[i], 0);
            }
            return;
        }
        int dif = newMaxHeight - newMinHeight;
        if (dif == 0) {
            newMinHeight--;
            n = 3;
            step = 1.0f;
        } else if (dif < 6) {
            n = Math.max(2, dif + 1);
            step = 1.0f;
        } else if (dif / 2 < 6) {
            n = (dif / 2) + (dif % 2) + 1;
            step = 2.0f;
        } else {
            step = ((float) (newMaxHeight - newMinHeight)) / 5.0f;
            if (step <= 0.0f) {
                step = 1.0f;
                n = Math.max(2, (newMaxHeight - newMinHeight) + 1);
            } else {
                n = 6;
            }
        }
        this.values = new int[n];
        this.valuesStr = new String[n];
        if (k > 0.0f) {
            this.valuesStr2 = new String[n];
        }
        skipFloatValues = step / k < 1.0f ? true : skipFloatValues;
        for (int i2 = 0; i2 < n; i2++) {
            int[] iArr2 = this.values;
            iArr2[i2] = ((int) (((float) i2) * step)) + newMinHeight;
            this.valuesStr[i2] = AndroidUtilities.formatWholeNumber(iArr2[i2], dif);
            if (k > 0.0f) {
                float v2 = ((float) this.values[i2]) / k;
                if (!skipFloatValues) {
                    this.valuesStr2[i2] = AndroidUtilities.formatWholeNumber((int) v2, (int) (((float) dif) / k));
                } else if (v2 - ((float) ((int) v2)) < 0.01f) {
                    this.valuesStr2[i2] = AndroidUtilities.formatWholeNumber((int) v2, (int) (((float) dif) / k));
                } else {
                    this.valuesStr2[i2] = "";
                }
            }
        }
    }

    public static int lookupHeight(int maxValue) {
        int v = maxValue;
        if (maxValue > 100) {
            v = round(maxValue);
        }
        return ((int) Math.ceil((double) (((float) v) / 5.0f))) * 5;
    }

    private static int round(int maxValue) {
        if (((float) (maxValue / 5)) % 10.0f == 0.0f) {
            return maxValue;
        }
        return ((maxValue / 10) + 1) * 10;
    }
}

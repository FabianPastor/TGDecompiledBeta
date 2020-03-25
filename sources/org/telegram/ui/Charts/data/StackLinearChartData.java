package org.telegram.ui.Charts.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.SegmentTree;
import org.telegram.ui.Charts.data.ChartData;

public class StackLinearChartData extends ChartData {
    public int simplifiedSize;
    public int[][] simplifiedY;
    int[] ySum;

    public StackLinearChartData(JSONObject jSONObject) throws JSONException {
        super(jSONObject);
        int length = this.lines.get(0).y.length;
        int size = this.lines.size();
        this.ySum = new int[length];
        for (int i = 0; i < length; i++) {
            this.ySum[i] = 0;
            for (int i2 = 0; i2 < size; i2++) {
                int[] iArr = this.ySum;
                iArr[i] = iArr[i] + this.lines.get(i2).y[i];
            }
        }
        new SegmentTree(this.ySum);
    }

    public StackLinearChartData(ChartData chartData, long j) {
        int binarySearch = Arrays.binarySearch(chartData.x, j);
        int i = binarySearch - 4;
        int i2 = binarySearch + 4;
        if (i < 0) {
            i2 += -i;
            i = 0;
        }
        long[] jArr = chartData.x;
        if (i2 > jArr.length - 1) {
            i -= i2 - jArr.length;
            i2 = jArr.length - 1;
        }
        i = i < 0 ? 0 : i;
        int i3 = (i2 - i) + 1;
        this.x = new long[i3];
        this.xPercentage = new float[i3];
        this.lines = new ArrayList<>();
        for (int i4 = 0; i4 < chartData.lines.size(); i4++) {
            ChartData.Line line = new ChartData.Line(this);
            line.y = new int[i3];
            line.id = chartData.lines.get(i4).id;
            line.name = chartData.lines.get(i4).name;
            line.colorKey = chartData.lines.get(i4).colorKey;
            line.color = chartData.lines.get(i4).color;
            line.colorDark = chartData.lines.get(i4).colorDark;
            this.lines.add(line);
        }
        int i5 = 0;
        while (i <= i2) {
            this.x[i5] = chartData.x[i];
            for (int i6 = 0; i6 < this.lines.size(); i6++) {
                this.lines.get(i6).y[i5] = chartData.lines.get(i6).y[i];
            }
            i5++;
            i++;
        }
        this.timeStep = 86400000;
        measure();
    }

    /* access modifiers changed from: protected */
    public void measure() {
        super.measure();
        this.simplifiedSize = 0;
        int length = this.xPercentage.length;
        int size = this.lines.size();
        int max = Math.max(1, Math.round(((float) length) / 140.0f));
        int i = length / max;
        int[] iArr = new int[2];
        iArr[1] = i;
        iArr[0] = size;
        this.simplifiedY = (int[][]) Array.newInstance(int.class, iArr);
        int[] iArr2 = new int[size];
        for (int i2 = 0; i2 < length; i2++) {
            for (int i3 = 0; i3 < size; i3++) {
                int[] iArr3 = this.lines.get(i3).y;
                if (iArr3[i2] > iArr2[i3]) {
                    iArr2[i3] = iArr3[i2];
                }
            }
            if (i2 % max == 0) {
                for (int i4 = 0; i4 < size; i4++) {
                    this.simplifiedY[i4][this.simplifiedSize] = iArr2[i4];
                    iArr2[i4] = 0;
                }
                int i5 = this.simplifiedSize + 1;
                this.simplifiedSize = i5;
                if (i5 >= i) {
                    return;
                }
            }
        }
    }
}

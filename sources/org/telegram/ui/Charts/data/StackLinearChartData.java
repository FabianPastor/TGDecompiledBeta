package org.telegram.ui.Charts.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.SegmentTree;
import org.telegram.ui.Charts.data.ChartData;

public class StackLinearChartData extends ChartData {
    public int simplifiedSize;
    public int[][] simplifiedY;
    int[] ySum;
    SegmentTree ySumSegmentTree;

    public StackLinearChartData(JSONObject jsonObject, boolean isLanguages) throws JSONException {
        super(jsonObject);
        if (isLanguages) {
            long[] totalCount = new long[this.lines.size()];
            int[] emptyCount = new int[this.lines.size()];
            long total = 0;
            for (int k = 0; k < this.lines.size(); k++) {
                int n = this.x.length;
                for (int i = 0; i < n; i++) {
                    int v = ((ChartData.Line) this.lines.get(k)).y[i];
                    totalCount[k] = totalCount[k] + ((long) v);
                    if (v == 0) {
                        emptyCount[k] = emptyCount[k] + 1;
                    }
                }
                total += totalCount[k];
            }
            ArrayList<ChartData.Line> removed = new ArrayList<>();
            for (int k2 = 0; k2 < this.lines.size(); k2++) {
                double d = (double) totalCount[k2];
                double d2 = (double) total;
                Double.isNaN(d);
                Double.isNaN(d2);
                if (d / d2 < 0.01d && ((float) emptyCount[k2]) > ((float) this.x.length) / 2.0f) {
                    removed.add((ChartData.Line) this.lines.get(k2));
                }
            }
            Iterator<ChartData.Line> it = removed.iterator();
            while (it.hasNext()) {
                this.lines.remove(it.next());
            }
        }
        int n2 = ((ChartData.Line) this.lines.get(0)).y.length;
        int k3 = this.lines.size();
        this.ySum = new int[n2];
        for (int i2 = 0; i2 < n2; i2++) {
            this.ySum[i2] = 0;
            for (int j = 0; j < k3; j++) {
                int[] iArr = this.ySum;
                iArr[i2] = iArr[i2] + ((ChartData.Line) this.lines.get(j)).y[i2];
            }
        }
        this.ySumSegmentTree = new SegmentTree(this.ySum);
    }

    public StackLinearChartData(ChartData data, long d) {
        int index = Arrays.binarySearch(data.x, d);
        int startIndex = index - 4;
        int endIndex = index + 4;
        if (startIndex < 0) {
            endIndex += -startIndex;
            startIndex = 0;
        }
        if (endIndex > data.x.length - 1) {
            startIndex -= endIndex - data.x.length;
            endIndex = data.x.length - 1;
        }
        startIndex = startIndex < 0 ? 0 : startIndex;
        int n = (endIndex - startIndex) + 1;
        this.x = new long[n];
        this.xPercentage = new float[n];
        this.lines = new ArrayList();
        for (int i = 0; i < data.lines.size(); i++) {
            ChartData.Line line = new ChartData.Line();
            line.y = new int[n];
            line.id = data.lines.get(i).id;
            line.name = data.lines.get(i).name;
            line.colorKey = data.lines.get(i).colorKey;
            line.color = data.lines.get(i).color;
            line.colorDark = data.lines.get(i).colorDark;
            this.lines.add(line);
        }
        int i2 = 0;
        for (int j = startIndex; j <= endIndex; j++) {
            this.x[i2] = data.x[j];
            for (int k = 0; k < this.lines.size(); k++) {
                ((ChartData.Line) this.lines.get(k)).y[i2] = data.lines.get(k).y[j];
            }
            i2++;
        }
        this.timeStep = 86400000;
        measure();
    }

    /* access modifiers changed from: protected */
    public void measure() {
        super.measure();
        this.simplifiedSize = 0;
        int n = this.xPercentage.length;
        int nl = this.lines.size();
        int step = Math.max(1, Math.round(((float) n) / 140.0f));
        int maxSize = n / step;
        int[] iArr = new int[2];
        iArr[1] = maxSize;
        iArr[0] = nl;
        this.simplifiedY = (int[][]) Array.newInstance(int.class, iArr);
        int[] max = new int[nl];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < nl; k++) {
                ChartData.Line line = (ChartData.Line) this.lines.get(k);
                if (line.y[i] > max[k]) {
                    max[k] = line.y[i];
                }
            }
            if (i % step == 0) {
                for (int k2 = 0; k2 < nl; k2++) {
                    this.simplifiedY[k2][this.simplifiedSize] = max[k2];
                    max[k2] = 0;
                }
                int i2 = this.simplifiedSize + 1;
                this.simplifiedSize = i2;
                if (i2 >= maxSize) {
                    return;
                }
            }
        }
    }
}

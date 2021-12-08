package org.telegram.ui.Charts.data;

import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.ui.Charts.data.ChartData;

public class DoubleLinearChartData extends ChartData {
    public float[] linesK;

    public DoubleLinearChartData(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }

    /* access modifiers changed from: protected */
    public void measure() {
        super.measure();
        int n = this.lines.size();
        int max = 0;
        for (int i = 0; i < n; i++) {
            int m = ((ChartData.Line) this.lines.get(i)).maxValue;
            if (m > max) {
                max = m;
            }
        }
        this.linesK = new float[n];
        for (int i2 = 0; i2 < n; i2++) {
            int m2 = ((ChartData.Line) this.lines.get(i2)).maxValue;
            if (max == m2) {
                this.linesK[i2] = 1.0f;
            } else {
                this.linesK[i2] = (float) (max / m2);
            }
        }
    }
}

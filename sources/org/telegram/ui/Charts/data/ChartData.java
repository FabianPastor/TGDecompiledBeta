package org.telegram.ui.Charts.data;

import android.graphics.Color;
import android.text.TextUtils;
import androidx.core.graphics.ColorUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.SegmentTree;

public class ChartData {
    public String[] daysLookup;
    public ArrayList<Line> lines = new ArrayList<>();
    public int maxValue = 0;
    public int minValue = Integer.MAX_VALUE;
    public float oneDayPercentage = 0.0f;
    protected long timeStep;
    public long[] x;
    public float[] xPercentage;

    protected ChartData() {
    }

    public ChartData(JSONObject jsonObject) throws JSONException {
        JSONArray columns = jsonObject.getJSONArray("columns");
        int length = columns.length();
        for (int i = 0; i < columns.length(); i++) {
            JSONArray a = columns.getJSONArray(i);
            if (a.getString(0).equals("x")) {
                int len = a.length() - 1;
                this.x = new long[len];
                for (int j = 0; j < len; j++) {
                    this.x[j] = a.getLong(j + 1);
                }
            } else {
                Line l = new Line();
                this.lines.add(l);
                int len2 = a.length() - 1;
                l.id = a.getString(0);
                l.y = new int[len2];
                for (int j2 = 0; j2 < len2; j2++) {
                    l.y[j2] = a.getInt(j2 + 1);
                    if (l.y[j2] > l.maxValue) {
                        l.maxValue = l.y[j2];
                    }
                    if (l.y[j2] < l.minValue) {
                        l.minValue = l.y[j2];
                    }
                }
            }
            long[] jArr = this.x;
            if (jArr.length > 1) {
                this.timeStep = jArr[1] - jArr[0];
            } else {
                this.timeStep = 86400000;
            }
            measure();
        }
        JSONObject colors = jsonObject.optJSONObject("colors");
        JSONObject names = jsonObject.optJSONObject("names");
        Pattern colorPattern = Pattern.compile("(.*)(#.*)");
        for (int i2 = 0; i2 < this.lines.size(); i2++) {
            Line line = this.lines.get(i2);
            if (colors != null) {
                Matcher matcher = colorPattern.matcher(colors.getString(line.id));
                if (matcher.matches()) {
                    if (!TextUtils.isEmpty(matcher.group(1))) {
                        line.colorKey = "statisticChartLine_" + matcher.group(1).toLowerCase();
                    }
                    line.color = Color.parseColor(matcher.group(2));
                    line.colorDark = ColorUtils.blendARGB(-1, line.color, 0.85f);
                }
            }
            if (names != null) {
                line.name = names.getString(line.id);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void measure() {
        SimpleDateFormat formatter;
        long[] jArr = this.x;
        int n = jArr.length;
        if (n != 0) {
            long start = jArr[0];
            long end = jArr[n - 1];
            float[] fArr = new float[n];
            this.xPercentage = fArr;
            if (n == 1) {
                fArr[0] = 1.0f;
            } else {
                for (int i = 0; i < n; i++) {
                    this.xPercentage[i] = ((float) (this.x[i] - start)) / ((float) (end - start));
                }
            }
            for (int i2 = 0; i2 < this.lines.size(); i2++) {
                if (this.lines.get(i2).maxValue > this.maxValue) {
                    this.maxValue = this.lines.get(i2).maxValue;
                }
                if (this.lines.get(i2).minValue < this.minValue) {
                    this.minValue = this.lines.get(i2).minValue;
                }
                this.lines.get(i2).segmentTree = new SegmentTree(this.lines.get(i2).y);
            }
            long j = this.timeStep;
            this.daysLookup = new String[(((int) ((end - start) / j)) + 10)];
            long j2 = 1;
            if (j == 1) {
                formatter = null;
            } else if (j < 86400000) {
                formatter = new SimpleDateFormat("HH:mm");
            } else {
                formatter = new SimpleDateFormat("MMM d");
            }
            int i3 = 0;
            while (true) {
                String[] strArr = this.daysLookup;
                if (i3 < strArr.length) {
                    if (this.timeStep == j2) {
                        strArr[i3] = String.format(Locale.ENGLISH, "%02d:00", new Object[]{Integer.valueOf(i3)});
                    } else {
                        strArr[i3] = formatter.format(new Date((((long) i3) * this.timeStep) + start));
                    }
                    i3++;
                    j2 = 1;
                } else {
                    long[] jArr2 = this.x;
                    this.oneDayPercentage = ((float) this.timeStep) / ((float) (jArr2[jArr2.length - 1] - jArr2[0]));
                    return;
                }
            }
        }
    }

    public String getDayString(int i) {
        String[] strArr = this.daysLookup;
        long[] jArr = this.x;
        return strArr[(int) ((jArr[i] - jArr[0]) / this.timeStep)];
    }

    public int findStartIndex(float v) {
        int n;
        if (v == 0.0f || (n = this.xPercentage.length) < 2) {
            return 0;
        }
        int left = 0;
        int right = n - 1;
        while (left <= right) {
            int middle = (right + left) >> 1;
            float[] fArr = this.xPercentage;
            if ((v < fArr[middle] && (middle == 0 || v > fArr[middle - 1])) || v == fArr[middle]) {
                return middle;
            }
            if (v < fArr[middle]) {
                right = middle - 1;
            } else if (v > fArr[middle]) {
                left = middle + 1;
            }
        }
        return left;
    }

    public int findEndIndex(int left, float v) {
        int n = this.xPercentage.length;
        if (v == 1.0f) {
            return n - 1;
        }
        int right = n - 1;
        while (left <= right) {
            int middle = (right + left) >> 1;
            float[] fArr = this.xPercentage;
            if ((v > fArr[middle] && (middle == n - 1 || v < fArr[middle + 1])) || v == fArr[middle]) {
                return middle;
            }
            if (v < fArr[middle]) {
                right = middle - 1;
            } else if (v > fArr[middle]) {
                left = middle + 1;
            }
        }
        return right;
    }

    public int findIndex(int left, int right, float v) {
        float[] fArr = this.xPercentage;
        int n = fArr.length;
        if (v <= fArr[left]) {
            return left;
        }
        if (v >= fArr[right]) {
            return right;
        }
        while (left <= right) {
            int middle = (right + left) >> 1;
            float[] fArr2 = this.xPercentage;
            if ((v > fArr2[middle] && (middle == n - 1 || v < fArr2[middle + 1])) || v == fArr2[middle]) {
                return middle;
            }
            if (v < fArr2[middle]) {
                right = middle - 1;
            } else if (v > fArr2[middle]) {
                left = middle + 1;
            }
        }
        return right;
    }

    public class Line {
        public int color = -16777216;
        public int colorDark = -1;
        public String colorKey;
        public String id;
        public int maxValue = 0;
        public int minValue = Integer.MAX_VALUE;
        public String name;
        public SegmentTree segmentTree;
        public int[] y;

        public Line() {
        }
    }
}

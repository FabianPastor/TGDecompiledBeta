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
    public ArrayList<Line> lines;
    public int maxValue;
    public int minValue;
    public float oneDayPercentage;
    protected long timeStep;
    public long[] x;
    public float[] xPercentage;

    protected ChartData() {
        this.lines = new ArrayList<>();
        this.maxValue = 0;
        this.minValue = Integer.MAX_VALUE;
        this.oneDayPercentage = 0.0f;
    }

    public ChartData(JSONObject jSONObject) throws JSONException {
        this.lines = new ArrayList<>();
        this.maxValue = 0;
        this.minValue = Integer.MAX_VALUE;
        this.oneDayPercentage = 0.0f;
        JSONArray jSONArray = jSONObject.getJSONArray("columns");
        jSONArray.length();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONArray jSONArray2 = jSONArray.getJSONArray(i);
            if (jSONArray2.getString(0).equals("x")) {
                int length = jSONArray2.length() - 1;
                this.x = new long[length];
                int i2 = 0;
                while (i2 < length) {
                    int i3 = i2 + 1;
                    this.x[i2] = jSONArray2.getLong(i3);
                    i2 = i3;
                }
            } else {
                Line line = new Line(this);
                this.lines.add(line);
                int length2 = jSONArray2.length() - 1;
                line.id = jSONArray2.getString(0);
                line.y = new int[length2];
                int i4 = 0;
                while (i4 < length2) {
                    int i5 = i4 + 1;
                    line.y[i4] = jSONArray2.getInt(i5);
                    int[] iArr = line.y;
                    if (iArr[i4] > line.maxValue) {
                        line.maxValue = iArr[i4];
                    }
                    int[] iArr2 = line.y;
                    if (iArr2[i4] < line.minValue) {
                        line.minValue = iArr2[i4];
                    }
                    i4 = i5;
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
        JSONObject optJSONObject = jSONObject.optJSONObject("colors");
        JSONObject optJSONObject2 = jSONObject.optJSONObject("names");
        Pattern compile = Pattern.compile("(.*)(#.*)");
        for (int i6 = 0; i6 < this.lines.size(); i6++) {
            Line line2 = this.lines.get(i6);
            if (optJSONObject != null) {
                Matcher matcher = compile.matcher(optJSONObject.getString(line2.id));
                if (matcher.matches()) {
                    if (!TextUtils.isEmpty(matcher.group(1))) {
                        line2.colorKey = "statisticChartLine_" + matcher.group(1).toLowerCase();
                    }
                    int parseColor = Color.parseColor(matcher.group(2));
                    line2.color = parseColor;
                    line2.colorDark = ColorUtils.blendARGB(-1, parseColor, 0.85f);
                }
            }
            if (optJSONObject2 != null) {
                line2.name = optJSONObject2.getString(line2.id);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void measure() {
        SimpleDateFormat simpleDateFormat;
        long[] jArr = this.x;
        int length = jArr.length;
        if (length != 0) {
            long j = jArr[0];
            long j2 = jArr[length - 1];
            float[] fArr = new float[length];
            this.xPercentage = fArr;
            if (length == 1) {
                fArr[0] = 1.0f;
            } else {
                for (int i = 0; i < length; i++) {
                    this.xPercentage[i] = ((float) (this.x[i] - j)) / ((float) (j2 - j));
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
            long j3 = this.timeStep;
            this.daysLookup = new String[(((int) ((j2 - j) / j3)) + 10)];
            if (j3 == 1) {
                simpleDateFormat = null;
            } else if (j3 < 86400000) {
                simpleDateFormat = new SimpleDateFormat("HH:mm");
            } else {
                simpleDateFormat = new SimpleDateFormat("MMM d");
            }
            int i3 = 0;
            while (true) {
                String[] strArr = this.daysLookup;
                if (i3 < strArr.length) {
                    if (this.timeStep == 1) {
                        strArr[i3] = String.format(Locale.ENGLISH, "%02d:00", new Object[]{Integer.valueOf(i3)});
                    } else {
                        strArr[i3] = simpleDateFormat.format(new Date((((long) i3) * this.timeStep) + j));
                    }
                    i3++;
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

    public int findStartIndex(float f) {
        int length;
        int i = 0;
        if (f == 0.0f || (length = this.xPercentage.length) < 2) {
            return 0;
        }
        int i2 = length - 1;
        while (i <= i2) {
            int i3 = (i2 + i) >> 1;
            float[] fArr = this.xPercentage;
            if (f < fArr[i3] && (i3 == 0 || f > fArr[i3 - 1])) {
                return i3;
            }
            float[] fArr2 = this.xPercentage;
            if (f == fArr2[i3]) {
                return i3;
            }
            if (f < fArr2[i3]) {
                i2 = i3 - 1;
            } else if (f > fArr2[i3]) {
                i = i3 + 1;
            }
        }
        return i;
    }

    public int findEndIndex(int i, float f) {
        int length = this.xPercentage.length;
        if (f == 1.0f) {
            return length - 1;
        }
        int i2 = length - 1;
        int i3 = i2;
        while (i <= i3) {
            int i4 = (i3 + i) >> 1;
            float[] fArr = this.xPercentage;
            if (f > fArr[i4] && (i4 == i2 || f < fArr[i4 + 1])) {
                return i4;
            }
            float[] fArr2 = this.xPercentage;
            if (f == fArr2[i4]) {
                return i4;
            }
            if (f < fArr2[i4]) {
                i3 = i4 - 1;
            } else if (f > fArr2[i4]) {
                i = i4 + 1;
            }
        }
        return i3;
    }

    public int findIndex(int i, int i2, float f) {
        float[] fArr = this.xPercentage;
        int length = fArr.length;
        if (f <= fArr[i]) {
            return i;
        }
        if (f >= fArr[i2]) {
            return i2;
        }
        while (i <= i2) {
            int i3 = (i2 + i) >> 1;
            float[] fArr2 = this.xPercentage;
            if (f > fArr2[i3] && (i3 == length - 1 || f < fArr2[i3 + 1])) {
                return i3;
            }
            float[] fArr3 = this.xPercentage;
            if (f == fArr3[i3]) {
                return i3;
            }
            if (f < fArr3[i3]) {
                i2 = i3 - 1;
            } else if (f > fArr3[i3]) {
                i = i3 + 1;
            }
        }
        return i2;
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

        public Line(ChartData chartData) {
        }
    }
}

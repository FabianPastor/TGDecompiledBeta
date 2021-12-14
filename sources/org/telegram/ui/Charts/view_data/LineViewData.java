package org.telegram.ui.Charts.view_data;

import android.animation.ValueAnimator;
import android.graphics.Paint;
import android.graphics.Path;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Charts.BaseChartView;
import org.telegram.ui.Charts.data.ChartData;

public class LineViewData {
    public float alpha = 1.0f;
    public ValueAnimator animatorIn;
    public ValueAnimator animatorOut;
    public final Paint bottomLinePaint;
    public final Path bottomLinePath = new Path();
    public final Path chartPath = new Path();
    public final Path chartPathPicker = new Path();
    public boolean enabled = true;
    public final ChartData.Line line;
    public int lineColor;
    public float[] linesPath;
    public float[] linesPathBottom;
    public int linesPathBottomSize;
    public final Paint paint;
    public final Paint selectionPaint;

    public LineViewData(ChartData.Line line2) {
        Paint paint2 = new Paint(1);
        this.bottomLinePaint = paint2;
        Paint paint3 = new Paint(1);
        this.paint = paint3;
        Paint paint4 = new Paint(1);
        this.selectionPaint = paint4;
        this.line = line2;
        paint3.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
        paint3.setStyle(Paint.Style.STROKE);
        if (!BaseChartView.USE_LINES) {
            paint3.setStrokeJoin(Paint.Join.ROUND);
        }
        paint3.setColor(line2.color);
        paint2.setStrokeWidth(AndroidUtilities.dpf2(1.0f));
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setColor(line2.color);
        paint4.setStrokeWidth(AndroidUtilities.dpf2(10.0f));
        paint4.setStyle(Paint.Style.STROKE);
        paint4.setStrokeCap(Paint.Cap.ROUND);
        paint4.setColor(line2.color);
        int[] iArr = line2.y;
        this.linesPath = new float[(iArr.length << 2)];
        this.linesPathBottom = new float[(iArr.length << 2)];
    }

    public void updateColors() {
        String str = this.line.colorKey;
        if (str == null || !Theme.hasThemeKey(str)) {
            this.lineColor = (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite")) > 0.5d ? 1 : (ColorUtils.calculateLuminance(Theme.getColor("windowBackgroundWhite")) == 0.5d ? 0 : -1)) < 0 ? this.line.colorDark : this.line.color;
        } else {
            this.lineColor = Theme.getColor(this.line.colorKey);
        }
        this.paint.setColor(this.lineColor);
        this.bottomLinePaint.setColor(this.lineColor);
        this.selectionPaint.setColor(this.lineColor);
    }
}

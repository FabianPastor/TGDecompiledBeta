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
    public final Paint bottomLinePaint = new Paint(1);
    public final Path bottomLinePath = new Path();
    public final Path chartPath = new Path();
    public final Path chartPathPicker = new Path();
    public boolean enabled = true;
    public final ChartData.Line line;
    public int lineColor;
    public float[] linesPath;
    public float[] linesPathBottom;
    public int linesPathBottomSize;
    public final Paint paint = new Paint(1);
    public final Paint selectionPaint = new Paint(1);

    public LineViewData(ChartData.Line line2) {
        this.line = line2;
        this.paint.setStrokeWidth(AndroidUtilities.dpf2(2.0f));
        this.paint.setStyle(Paint.Style.STROKE);
        if (!BaseChartView.USE_LINES) {
            this.paint.setStrokeJoin(Paint.Join.ROUND);
        }
        this.paint.setColor(line2.color);
        this.bottomLinePaint.setStrokeWidth(AndroidUtilities.dpf2(1.0f));
        this.bottomLinePaint.setStyle(Paint.Style.STROKE);
        this.bottomLinePaint.setColor(line2.color);
        this.selectionPaint.setStrokeWidth(AndroidUtilities.dpf2(10.0f));
        this.selectionPaint.setStyle(Paint.Style.STROKE);
        this.selectionPaint.setStrokeCap(Paint.Cap.ROUND);
        this.selectionPaint.setColor(line2.color);
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

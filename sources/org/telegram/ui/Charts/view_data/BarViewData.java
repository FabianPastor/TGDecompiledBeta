package org.telegram.ui.Charts.view_data;

import android.graphics.Paint;
import org.telegram.ui.Charts.data.ChartData;

public class BarViewData extends LineViewData {
    public int blendColor = 0;
    public final Paint unselectedPaint = new Paint();

    public BarViewData(ChartData.Line line) {
        super(line);
        this.paint.setStyle(Paint.Style.STROKE);
        this.unselectedPaint.setStyle(Paint.Style.STROKE);
        this.paint.setAntiAlias(false);
    }

    public void updateColors() {
        super.updateColors();
    }
}

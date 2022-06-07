package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class DividerCell extends View {
    private boolean forceDarkTheme;
    private Paint paint;
    private Theme.ResourcesProvider resourcesProvider;

    public DividerCell(Context context) {
        this(context, (Theme.ResourcesProvider) null);
    }

    public DividerCell(Context context, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        this.paint = new Paint();
        this.resourcesProvider = resourcesProvider2;
        setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), getPaddingTop() + getPaddingBottom() + 1);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.forceDarkTheme) {
            this.paint.setColor(ColorUtils.blendARGB(-16777216, Theme.getColor("voipgroup_dialogBackground", this.resourcesProvider), 0.2f));
        } else {
            this.paint.setColor(Theme.getColor("divider", this.resourcesProvider));
        }
        canvas.drawLine((float) getPaddingLeft(), (float) getPaddingTop(), (float) (getWidth() - getPaddingRight()), (float) getPaddingTop(), this.paint);
    }

    public void setForceDarkTheme(boolean z) {
        this.forceDarkTheme = z;
    }
}

package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.text.style.ReplacementSpan;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class LoadingSpan extends ReplacementSpan {
    private LoadingDrawable drawable;
    private int size;
    private View view;

    public LoadingSpan(View view2, int i) {
        this.view = view2;
        this.size = i;
        LoadingDrawable loadingDrawable = new LoadingDrawable((Theme.ResourcesProvider) null);
        this.drawable = loadingDrawable;
        loadingDrawable.paint.setPathEffect(new CornerPathEffect((float) AndroidUtilities.dp(4.0f)));
    }

    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        return this.size;
    }

    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        int i6 = (int) f;
        this.drawable.setBounds(i6, i3 + AndroidUtilities.dp(2.0f), this.size + i6, i5 - AndroidUtilities.dp(2.0f));
        this.drawable.draw(canvas);
        View view2 = this.view;
        if (view2 != null) {
            view2.invalidate();
        }
    }
}

package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import org.telegram.ui.ActionBar.Theme;

public class BluredView extends View {
    public final BlurBehindDrawable drawable;

    public BluredView(Context context, View parentView, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        BlurBehindDrawable blurBehindDrawable = new BlurBehindDrawable(parentView, this, 1, resourcesProvider);
        this.drawable = blurBehindDrawable;
        blurBehindDrawable.setAnimateAlpha(false);
        blurBehindDrawable.show(true);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.drawable.draw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.drawable.checkSizes();
    }

    public void update() {
        this.drawable.invalidate();
    }

    public boolean fullyDrawing() {
        return this.drawable.isFullyDrawing() && getVisibility() == 0;
    }
}

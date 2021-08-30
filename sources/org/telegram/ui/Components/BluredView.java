package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class BluredView extends View {
    BlurBehindDrawable drawable;

    public BluredView(Context context, View view) {
        super(context);
        BlurBehindDrawable blurBehindDrawable = new BlurBehindDrawable(view, this);
        this.drawable = blurBehindDrawable;
        blurBehindDrawable.setAnimateAlpha(false);
        this.drawable.show(true);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        this.drawable.draw(canvas);
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.drawable.checkSizes();
    }

    public void update() {
        this.drawable.invalidate();
    }
}

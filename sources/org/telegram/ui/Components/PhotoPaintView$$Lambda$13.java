package org.telegram.ui.Components;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

final /* synthetic */ class PhotoPaintView$$Lambda$13 implements OnTouchListener {
    private final PhotoPaintView arg$1;

    PhotoPaintView$$Lambda$13(PhotoPaintView photoPaintView) {
        this.arg$1 = photoPaintView;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.arg$1.lambda$showPopup$16$PhotoPaintView(view, motionEvent);
    }
}

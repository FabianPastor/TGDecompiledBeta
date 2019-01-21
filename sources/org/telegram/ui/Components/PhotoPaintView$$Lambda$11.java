package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class PhotoPaintView$$Lambda$11 implements OnClickListener {
    private final PhotoPaintView arg$1;
    private final boolean arg$2;

    PhotoPaintView$$Lambda$11(PhotoPaintView photoPaintView, boolean z) {
        this.arg$1 = photoPaintView;
        this.arg$2 = z;
    }

    public void onClick(View view) {
        this.arg$1.lambda$buttonForText$14$PhotoPaintView(this.arg$2, view);
    }
}

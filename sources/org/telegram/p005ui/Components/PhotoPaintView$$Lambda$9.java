package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.Components.PhotoPaintView$$Lambda$9 */
final /* synthetic */ class PhotoPaintView$$Lambda$9 implements OnClickListener {
    private final PhotoPaintView arg$1;
    private final int arg$2;

    PhotoPaintView$$Lambda$9(PhotoPaintView photoPaintView, int i) {
        this.arg$1 = photoPaintView;
        this.arg$2 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$buttonForBrush$12$PhotoPaintView(this.arg$2, view);
    }
}

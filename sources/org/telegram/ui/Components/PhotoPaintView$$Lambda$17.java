package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Components.Paint.Views.EntityView;

final /* synthetic */ class PhotoPaintView$$Lambda$17 implements OnClickListener {
    private final PhotoPaintView arg$1;
    private final EntityView arg$2;

    PhotoPaintView$$Lambda$17(PhotoPaintView photoPaintView, EntityView entityView) {
        this.arg$1 = photoPaintView;
        this.arg$2 = entityView;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$8$PhotoPaintView(this.arg$2, view);
    }
}

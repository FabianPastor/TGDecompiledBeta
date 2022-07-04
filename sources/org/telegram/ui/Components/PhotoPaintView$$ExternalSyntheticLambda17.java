package org.telegram.ui.Components;

import org.telegram.ui.Components.Paint.Views.EntityView;

public final /* synthetic */ class PhotoPaintView$$ExternalSyntheticLambda17 implements Runnable {
    public final /* synthetic */ PhotoPaintView f$0;
    public final /* synthetic */ EntityView f$1;

    public /* synthetic */ PhotoPaintView$$ExternalSyntheticLambda17(PhotoPaintView photoPaintView, EntityView entityView) {
        this.f$0 = photoPaintView;
        this.f$1 = entityView;
    }

    public final void run() {
        this.f$0.lambda$registerRemovalUndo$9(this.f$1);
    }
}

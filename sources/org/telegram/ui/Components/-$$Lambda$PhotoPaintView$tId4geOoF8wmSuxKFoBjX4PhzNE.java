package org.telegram.ui.Components;

import org.telegram.ui.Components.Paint.Views.EntityView;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PhotoPaintView$tId4geOoF8wmSuxKFoBjX4PhzNE implements Runnable {
    private final /* synthetic */ PhotoPaintView f$0;
    private final /* synthetic */ EntityView f$1;

    public /* synthetic */ -$$Lambda$PhotoPaintView$tId4geOoF8wmSuxKFoBjX4PhzNE(PhotoPaintView photoPaintView, EntityView entityView) {
        this.f$0 = photoPaintView;
        this.f$1 = entityView;
    }

    public final void run() {
        this.f$0.lambda$registerRemovalUndo$7$PhotoPaintView(this.f$1);
    }
}

package org.telegram.ui.Components;

import org.telegram.ui.Components.PhotoFilterBlurControl.PhotoFilterLinearBlurControlDelegate;

final /* synthetic */ class PhotoFilterView$$Lambda$0 implements PhotoFilterLinearBlurControlDelegate {
    private final PhotoFilterView arg$1;

    PhotoFilterView$$Lambda$0(PhotoFilterView photoFilterView) {
        this.arg$1 = photoFilterView;
    }

    public void valueChanged(Point point, float f, float f2, float f3) {
        this.arg$1.lambda$new$0$PhotoFilterView(point, f, f2, f3);
    }
}

package org.telegram.ui;

import android.widget.ImageView;
import org.telegram.ui.Components.MediaActionDrawable.MediaActionDrawableDelegate;

final /* synthetic */ class TestActivity$$Lambda$0 implements MediaActionDrawableDelegate {
    private final ImageView arg$1;

    private TestActivity$$Lambda$0(ImageView imageView) {
        this.arg$1 = imageView;
    }

    static MediaActionDrawableDelegate get$Lambda(ImageView imageView) {
        return new TestActivity$$Lambda$0(imageView);
    }

    public void invalidate() {
        this.arg$1.invalidate();
    }
}

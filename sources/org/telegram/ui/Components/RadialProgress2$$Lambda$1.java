package org.telegram.ui.Components;

import android.view.View;
import org.telegram.ui.Components.MediaActionDrawable.MediaActionDrawableDelegate;

final /* synthetic */ class RadialProgress2$$Lambda$1 implements MediaActionDrawableDelegate {
    private final View arg$1;

    private RadialProgress2$$Lambda$1(View view) {
        this.arg$1 = view;
    }

    static MediaActionDrawableDelegate get$Lambda(View view) {
        return new RadialProgress2$$Lambda$1(view);
    }

    public void invalidate() {
        this.arg$1.invalidate();
    }
}

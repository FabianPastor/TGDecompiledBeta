package org.telegram.p005ui.Components;

import android.view.View;
import org.telegram.p005ui.Components.MediaActionDrawable.MediaActionDrawableDelegate;

/* renamed from: org.telegram.ui.Components.RadialProgress2$$Lambda$1 */
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

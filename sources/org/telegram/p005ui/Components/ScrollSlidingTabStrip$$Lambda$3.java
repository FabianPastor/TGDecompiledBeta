package org.telegram.p005ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

/* renamed from: org.telegram.ui.Components.ScrollSlidingTabStrip$$Lambda$3 */
final /* synthetic */ class ScrollSlidingTabStrip$$Lambda$3 implements OnClickListener {
    private final ScrollSlidingTabStrip arg$1;
    private final int arg$2;

    ScrollSlidingTabStrip$$Lambda$3(ScrollSlidingTabStrip scrollSlidingTabStrip, int i) {
        this.arg$1 = scrollSlidingTabStrip;
        this.arg$2 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$addStickerTab$3$ScrollSlidingTabStrip(this.arg$2, view);
    }
}

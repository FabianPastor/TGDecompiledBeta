package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class PagerSlidingTabStrip$$Lambda$0 implements OnClickListener {
    private final PagerSlidingTabStrip arg$1;
    private final int arg$2;

    PagerSlidingTabStrip$$Lambda$0(PagerSlidingTabStrip pagerSlidingTabStrip, int i) {
        this.arg$1 = pagerSlidingTabStrip;
        this.arg$2 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$addIconTab$0$PagerSlidingTabStrip(this.arg$2, view);
    }
}

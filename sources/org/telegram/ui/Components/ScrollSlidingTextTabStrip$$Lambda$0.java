package org.telegram.ui.Components;

import android.view.View;
import android.view.View.OnClickListener;

final /* synthetic */ class ScrollSlidingTextTabStrip$$Lambda$0 implements OnClickListener {
    private final ScrollSlidingTextTabStrip arg$1;
    private final int arg$2;

    ScrollSlidingTextTabStrip$$Lambda$0(ScrollSlidingTextTabStrip scrollSlidingTextTabStrip, int i) {
        this.arg$1 = scrollSlidingTextTabStrip;
        this.arg$2 = i;
    }

    public void onClick(View view) {
        this.arg$1.lambda$addTextTab$0$ScrollSlidingTextTabStrip(this.arg$2, view);
    }
}

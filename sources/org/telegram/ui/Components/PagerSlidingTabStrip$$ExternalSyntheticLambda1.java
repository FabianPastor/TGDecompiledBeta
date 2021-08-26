package org.telegram.ui.Components;

public final /* synthetic */ class PagerSlidingTabStrip$$ExternalSyntheticLambda1 implements Runnable {
    public final /* synthetic */ PagerSlidingTabStrip f$0;

    public /* synthetic */ PagerSlidingTabStrip$$ExternalSyntheticLambda1(PagerSlidingTabStrip pagerSlidingTabStrip) {
        this.f$0 = pagerSlidingTabStrip;
    }

    public final void run() {
        this.f$0.notifyDataSetChanged();
    }
}

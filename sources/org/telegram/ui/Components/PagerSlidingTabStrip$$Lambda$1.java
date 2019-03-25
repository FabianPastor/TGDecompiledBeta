package org.telegram.ui.Components;

final /* synthetic */ class PagerSlidingTabStrip$$Lambda$1 implements Runnable {
    private final PagerSlidingTabStrip arg$1;

    PagerSlidingTabStrip$$Lambda$1(PagerSlidingTabStrip pagerSlidingTabStrip) {
        this.arg$1 = pagerSlidingTabStrip;
    }

    public void run() {
        this.arg$1.notifyDataSetChanged();
    }
}

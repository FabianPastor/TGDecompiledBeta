package org.telegram.ui.ActionBar;

final /* synthetic */ class ActionBarLayout$$Lambda$1 implements Runnable {
    private final ActionBarLayout arg$1;
    private final boolean arg$2;
    private final boolean arg$3;
    private final BaseFragment arg$4;
    private final BaseFragment arg$5;

    ActionBarLayout$$Lambda$1(ActionBarLayout actionBarLayout, boolean z, boolean z2, BaseFragment baseFragment, BaseFragment baseFragment2) {
        this.arg$1 = actionBarLayout;
        this.arg$2 = z;
        this.arg$3 = z2;
        this.arg$4 = baseFragment;
        this.arg$5 = baseFragment2;
    }

    public void run() {
        this.arg$1.lambda$presentFragment$1$ActionBarLayout(this.arg$2, this.arg$3, this.arg$4, this.arg$5);
    }
}

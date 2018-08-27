package org.telegram.ui.ActionBar;

final /* synthetic */ class ActionBarLayout$$Lambda$5 implements Runnable {
    private final ActionBarLayout arg$1;
    private final BaseFragment arg$2;

    ActionBarLayout$$Lambda$5(ActionBarLayout actionBarLayout, BaseFragment baseFragment) {
        this.arg$1 = actionBarLayout;
        this.arg$2 = baseFragment;
    }

    public void run() {
        this.arg$1.lambda$closeLastFragment$5$ActionBarLayout(this.arg$2);
    }
}

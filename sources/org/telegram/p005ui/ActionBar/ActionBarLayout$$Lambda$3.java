package org.telegram.p005ui.ActionBar;

/* renamed from: org.telegram.ui.ActionBar.ActionBarLayout$$Lambda$3 */
final /* synthetic */ class ActionBarLayout$$Lambda$3 implements Runnable {
    private final ActionBarLayout arg$1;
    private final BaseFragment arg$2;
    private final BaseFragment arg$3;

    ActionBarLayout$$Lambda$3(ActionBarLayout actionBarLayout, BaseFragment baseFragment, BaseFragment baseFragment2) {
        this.arg$1 = actionBarLayout;
        this.arg$2 = baseFragment;
        this.arg$3 = baseFragment2;
    }

    public void run() {
        this.arg$1.lambda$closeLastFragment$3$ActionBarLayout(this.arg$2, this.arg$3);
    }
}

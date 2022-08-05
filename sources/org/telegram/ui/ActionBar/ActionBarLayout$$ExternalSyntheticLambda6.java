package org.telegram.ui.ActionBar;

import org.telegram.ui.ActionBar.ActionBarPopupWindow;

public final /* synthetic */ class ActionBarLayout$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ ActionBarLayout f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ ActionBarPopupWindow.ActionBarPopupWindowLayout f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ BaseFragment f$5;

    public /* synthetic */ ActionBarLayout$$ExternalSyntheticLambda6(ActionBarLayout actionBarLayout, boolean z, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, boolean z2, BaseFragment baseFragment, BaseFragment baseFragment2) {
        this.f$0 = actionBarLayout;
        this.f$1 = z;
        this.f$2 = actionBarPopupWindowLayout;
        this.f$3 = z2;
        this.f$4 = baseFragment;
        this.f$5 = baseFragment2;
    }

    public final void run() {
        this.f$0.lambda$presentFragment$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

package org.telegram.ui.ActionBar;

import android.view.View;

public final /* synthetic */ class ActionBarLayout$$ExternalSyntheticLambda4 implements Runnable {
    public final /* synthetic */ ActionBarLayout f$0;
    public final /* synthetic */ boolean f$1;
    public final /* synthetic */ View f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ BaseFragment f$4;
    public final /* synthetic */ BaseFragment f$5;

    public /* synthetic */ ActionBarLayout$$ExternalSyntheticLambda4(ActionBarLayout actionBarLayout, boolean z, View view, boolean z2, BaseFragment baseFragment, BaseFragment baseFragment2) {
        this.f$0 = actionBarLayout;
        this.f$1 = z;
        this.f$2 = view;
        this.f$3 = z2;
        this.f$4 = baseFragment;
        this.f$5 = baseFragment2;
    }

    public final void run() {
        this.f$0.lambda$presentFragment$1(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

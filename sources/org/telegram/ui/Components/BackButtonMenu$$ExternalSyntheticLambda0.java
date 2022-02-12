package org.telegram.ui.Components;

import android.view.View;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BackButtonMenu;

public final /* synthetic */ class BackButtonMenu$$ExternalSyntheticLambda0 implements View.OnClickListener {
    public final /* synthetic */ AtomicReference f$0;
    public final /* synthetic */ BackButtonMenu.PulledDialog f$1;
    public final /* synthetic */ ActionBarLayout f$2;
    public final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ BackButtonMenu$$ExternalSyntheticLambda0(AtomicReference atomicReference, BackButtonMenu.PulledDialog pulledDialog, ActionBarLayout actionBarLayout, BaseFragment baseFragment) {
        this.f$0 = atomicReference;
        this.f$1 = pulledDialog;
        this.f$2 = actionBarLayout;
        this.f$3 = baseFragment;
    }

    public final void onClick(View view) {
        BackButtonMenu.lambda$show$0(this.f$0, this.f$1, this.f$2, this.f$3, view);
    }
}

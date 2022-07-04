package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda56 implements Runnable {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ TLObject f$2;
    public final /* synthetic */ BaseFragment f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ Bundle f$5;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda56(LaunchActivity launchActivity, AlertDialog alertDialog, TLObject tLObject, BaseFragment baseFragment, int i, Bundle bundle) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = tLObject;
        this.f$3 = baseFragment;
        this.f$4 = i;
        this.f$5 = bundle;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$67(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
    }
}

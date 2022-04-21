package org.telegram.ui;

import android.os.Bundle;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda76 implements RequestDelegate {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ Bundle f$4;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda76(LaunchActivity launchActivity, AlertDialog alertDialog, BaseFragment baseFragment, int i, Bundle bundle) {
        this.f$0 = launchActivity;
        this.f$1 = alertDialog;
        this.f$2 = baseFragment;
        this.f$3 = i;
        this.f$4 = bundle;
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.f$0.m2376lambda$runLinkRequest$63$orgtelegramuiLaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, tLObject, tL_error);
    }
}

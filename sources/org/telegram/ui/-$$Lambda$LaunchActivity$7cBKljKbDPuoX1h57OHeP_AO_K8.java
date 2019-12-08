package org.telegram.ui;

import android.os.Bundle;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LaunchActivity$7cBKljKbDPuoX1h57OHeP_AO_K8 implements Runnable {
    private final /* synthetic */ LaunchActivity f$0;
    private final /* synthetic */ Bundle f$1;
    private final /* synthetic */ Integer f$2;
    private final /* synthetic */ int[] f$3;
    private final /* synthetic */ AlertDialog f$4;
    private final /* synthetic */ BaseFragment f$5;
    private final /* synthetic */ int f$6;

    public /* synthetic */ -$$Lambda$LaunchActivity$7cBKljKbDPuoX1h57OHeP_AO_K8(LaunchActivity launchActivity, Bundle bundle, Integer num, int[] iArr, AlertDialog alertDialog, BaseFragment baseFragment, int i) {
        this.f$0 = launchActivity;
        this.f$1 = bundle;
        this.f$2 = num;
        this.f$3 = iArr;
        this.f$4 = alertDialog;
        this.f$5 = baseFragment;
        this.f$6 = i;
    }

    public final void run() {
        this.f$0.lambda$runLinkRequest$36$LaunchActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
    }
}

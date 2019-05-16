package org.telegram.ui.Components;

import android.content.SharedPreferences;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$vTt7GpWddJNaVuBDzHfJsdiKdl4 implements RequestDelegate {
    private final /* synthetic */ SharedPreferences f$0;
    private final /* synthetic */ AlertDialog f$1;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ BaseFragment f$3;

    public /* synthetic */ -$$Lambda$AlertsCreator$vTt7GpWddJNaVuBDzHfJsdiKdl4(SharedPreferences sharedPreferences, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        this.f$0 = sharedPreferences;
        this.f$1 = alertDialog;
        this.f$2 = i;
        this.f$3 = baseFragment;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        AlertsCreator.lambda$performAskAQuestion$8(this.f$0, this.f$1, this.f$2, this.f$3, tLObject, tL_error);
    }
}

package org.telegram.ui.Components;

import android.content.SharedPreferences;
import org.telegram.tgnet.TLRPC.TL_help_support;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$M0pJapDgZt4nkaop5tg0yx7_Iys implements Runnable {
    private final /* synthetic */ SharedPreferences f$0;
    private final /* synthetic */ TL_help_support f$1;
    private final /* synthetic */ AlertDialog f$2;
    private final /* synthetic */ int f$3;
    private final /* synthetic */ BaseFragment f$4;

    public /* synthetic */ -$$Lambda$AlertsCreator$M0pJapDgZt4nkaop5tg0yx7_Iys(SharedPreferences sharedPreferences, TL_help_support tL_help_support, AlertDialog alertDialog, int i, BaseFragment baseFragment) {
        this.f$0 = sharedPreferences;
        this.f$1 = tL_help_support;
        this.f$2 = alertDialog;
        this.f$3 = i;
        this.f$4 = baseFragment;
    }

    public final void run() {
        AlertsCreator.lambda$null$6(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4);
    }
}

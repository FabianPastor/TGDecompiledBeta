package org.telegram.ui.Components.voip;

import android.app.Activity;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda6 implements Runnable {
    public final /* synthetic */ TLRPC.User f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ boolean f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ Activity f$6;
    public final /* synthetic */ BaseFragment f$7;
    public final /* synthetic */ AccountInstance f$8;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda6(TLRPC.User user, TLRPC.Chat chat, String str, boolean z, boolean z2, boolean z3, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        this.f$0 = user;
        this.f$1 = chat;
        this.f$2 = str;
        this.f$3 = z;
        this.f$4 = z2;
        this.f$5 = z3;
        this.f$6 = activity;
        this.f$7 = baseFragment;
        this.f$8 = accountInstance;
    }

    public final void run() {
        VoIPHelper.lambda$initiateCall$2(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
    }
}

package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.DialogInterface;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda15 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TLRPC.User f$0;
    public final /* synthetic */ TLRPC.Chat f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC.InputPeer f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ Activity f$7;
    public final /* synthetic */ BaseFragment f$8;
    public final /* synthetic */ AccountInstance f$9;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda15(TLRPC.User user, TLRPC.Chat chat, String str, TLRPC.InputPeer inputPeer, boolean z, boolean z2, boolean z3, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        this.f$0 = user;
        this.f$1 = chat;
        this.f$2 = str;
        this.f$3 = inputPeer;
        this.f$4 = z;
        this.f$5 = z2;
        this.f$6 = z3;
        this.f$7 = activity;
        this.f$8 = baseFragment;
        this.f$9 = accountInstance;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        VoIPHelper.doInitiateCall(this.f$0, this.f$1, this.f$2, this.f$3, false, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, false, false);
    }
}

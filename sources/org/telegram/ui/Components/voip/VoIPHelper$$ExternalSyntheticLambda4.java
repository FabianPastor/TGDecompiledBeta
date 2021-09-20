package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.DialogInterface;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ TLRPC$User f$0;
    public final /* synthetic */ TLRPC$Chat f$1;
    public final /* synthetic */ String f$2;
    public final /* synthetic */ TLRPC$InputPeer f$3;
    public final /* synthetic */ boolean f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ Activity f$7;
    public final /* synthetic */ BaseFragment f$8;
    public final /* synthetic */ AccountInstance f$9;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda4(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, String str, TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2, boolean z3, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        this.f$0 = tLRPC$User;
        this.f$1 = tLRPC$Chat;
        this.f$2 = str;
        this.f$3 = tLRPC$InputPeer;
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

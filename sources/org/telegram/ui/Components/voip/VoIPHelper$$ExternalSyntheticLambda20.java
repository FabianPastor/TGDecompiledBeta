package org.telegram.ui.Components.voip;

import android.app.Activity;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.JoinCallAlert;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda20 implements JoinCallAlert.JoinCallAlertDelegate {
    public final /* synthetic */ boolean f$0;
    public final /* synthetic */ Activity f$1;
    public final /* synthetic */ AccountInstance f$2;
    public final /* synthetic */ TLRPC$Chat f$3;
    public final /* synthetic */ String f$4;
    public final /* synthetic */ TLRPC$User f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ BaseFragment f$8;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda20(boolean z, Activity activity, AccountInstance accountInstance, TLRPC$Chat tLRPC$Chat, String str, TLRPC$User tLRPC$User, boolean z2, boolean z3, BaseFragment baseFragment) {
        this.f$0 = z;
        this.f$1 = activity;
        this.f$2 = accountInstance;
        this.f$3 = tLRPC$Chat;
        this.f$4 = str;
        this.f$5 = tLRPC$User;
        this.f$6 = z2;
        this.f$7 = z3;
        this.f$8 = baseFragment;
    }

    public final void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2) {
        VoIPHelper.lambda$doInitiateCall$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLRPC$InputPeer, z, z2);
    }
}

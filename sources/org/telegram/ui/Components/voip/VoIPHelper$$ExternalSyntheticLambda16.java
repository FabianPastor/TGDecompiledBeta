package org.telegram.ui.Components.voip;

import android.app.Activity;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda16 implements MessagesStorage.BooleanCallback {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ Activity f$1;
    public final /* synthetic */ TLRPC$Chat f$2;
    public final /* synthetic */ TLRPC$User f$3;
    public final /* synthetic */ TLRPC$InputPeer f$4;
    public final /* synthetic */ boolean f$5;
    public final /* synthetic */ boolean f$6;
    public final /* synthetic */ BaseFragment f$7;
    public final /* synthetic */ AccountInstance f$8;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda16(String str, Activity activity, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2, BaseFragment baseFragment, AccountInstance accountInstance) {
        this.f$0 = str;
        this.f$1 = activity;
        this.f$2 = tLRPC$Chat;
        this.f$3 = tLRPC$User;
        this.f$4 = tLRPC$InputPeer;
        this.f$5 = z;
        this.f$6 = z2;
        this.f$7 = baseFragment;
        this.f$8 = accountInstance;
    }

    public final void run(boolean z) {
        VoIPHelper.lambda$doInitiateCall$4(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, z);
    }
}
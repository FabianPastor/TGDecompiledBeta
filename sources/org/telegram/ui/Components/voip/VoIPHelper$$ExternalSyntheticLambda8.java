package org.telegram.ui.Components.voip;

import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.MessagesStorage;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class VoIPHelper$$ExternalSyntheticLambda8 implements MessagesStorage.BooleanCallback {
    public final /* synthetic */ TLRPC.Chat f$0;
    public final /* synthetic */ TLRPC.InputPeer f$1;
    public final /* synthetic */ BaseFragment f$2;
    public final /* synthetic */ AccountInstance f$3;

    public /* synthetic */ VoIPHelper$$ExternalSyntheticLambda8(TLRPC.Chat chat, TLRPC.InputPeer inputPeer, BaseFragment baseFragment, AccountInstance accountInstance) {
        this.f$0 = chat;
        this.f$1 = inputPeer;
        this.f$2 = baseFragment;
        this.f$3 = accountInstance;
    }

    public final void run(boolean z) {
        VoIPHelper.startCall(this.f$0, this.f$1, (String) null, true, this.f$2.getParentActivity(), this.f$2, this.f$3);
    }
}

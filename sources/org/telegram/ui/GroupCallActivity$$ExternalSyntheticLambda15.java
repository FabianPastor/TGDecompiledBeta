package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda15 implements View.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ NumberPicker f$2;
    public final /* synthetic */ NumberPicker f$3;
    public final /* synthetic */ TLRPC.Chat f$4;
    public final /* synthetic */ AccountInstance f$5;
    public final /* synthetic */ TLRPC.InputPeer f$6;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda15(GroupCallActivity groupCallActivity, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, TLRPC.Chat chat, AccountInstance accountInstance, TLRPC.InputPeer inputPeer) {
        this.f$0 = groupCallActivity;
        this.f$1 = numberPicker;
        this.f$2 = numberPicker2;
        this.f$3 = numberPicker3;
        this.f$4 = chat;
        this.f$5 = accountInstance;
        this.f$6 = inputPeer;
    }

    public final void onClick(View view) {
        this.f$0.m2977lambda$new$29$orgtelegramuiGroupCallActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, view);
    }
}

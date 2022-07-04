package org.telegram.ui;

import android.view.View;
import org.telegram.messenger.AccountInstance;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.ui.Components.NumberPicker;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda25 implements View.OnClickListener {
    public final /* synthetic */ GroupCallActivity f$0;
    public final /* synthetic */ NumberPicker f$1;
    public final /* synthetic */ NumberPicker f$2;
    public final /* synthetic */ NumberPicker f$3;
    public final /* synthetic */ TLRPC$Chat f$4;
    public final /* synthetic */ AccountInstance f$5;
    public final /* synthetic */ TLRPC$InputPeer f$6;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda25(GroupCallActivity groupCallActivity, NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, TLRPC$Chat tLRPC$Chat, AccountInstance accountInstance, TLRPC$InputPeer tLRPC$InputPeer) {
        this.f$0 = groupCallActivity;
        this.f$1 = numberPicker;
        this.f$2 = numberPicker2;
        this.f$3 = numberPicker3;
        this.f$4 = tLRPC$Chat;
        this.f$5 = accountInstance;
        this.f$6 = tLRPC$InputPeer;
    }

    public final void onClick(View view) {
        this.f$0.lambda$new$29(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, view);
    }
}

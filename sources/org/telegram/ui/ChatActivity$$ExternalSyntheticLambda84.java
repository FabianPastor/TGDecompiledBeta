package org.telegram.ui;

import android.view.View;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.ui.Components.TranslateAlert;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda84 implements View.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ ArrayList f$2;
    public final /* synthetic */ TLRPC$InputPeer f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ String f$5;
    public final /* synthetic */ CharSequence f$6;
    public final /* synthetic */ boolean f$7;
    public final /* synthetic */ TranslateAlert.OnLinkPress f$8;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda84(ChatActivity chatActivity, int i, ArrayList arrayList, TLRPC$InputPeer tLRPC$InputPeer, int i2, String str, CharSequence charSequence, boolean z, TranslateAlert.OnLinkPress onLinkPress) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = arrayList;
        this.f$3 = tLRPC$InputPeer;
        this.f$4 = i2;
        this.f$5 = str;
        this.f$6 = charSequence;
        this.f$7 = z;
        this.f$8 = onLinkPress;
    }

    public final void onClick(View view) {
        this.f$0.lambda$createMenu$181(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, view);
    }
}

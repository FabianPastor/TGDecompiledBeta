package org.telegram.ui.Components;

import java.util.ArrayList;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class ChatActivityEnterView$$ExternalSyntheticLambda48 implements DialogsActivity.DialogsActivityDelegate {
    public final /* synthetic */ ChatActivityEnterView f$0;
    public final /* synthetic */ MessageObject f$1;
    public final /* synthetic */ TLRPC.KeyboardButton f$2;

    public /* synthetic */ ChatActivityEnterView$$ExternalSyntheticLambda48(ChatActivityEnterView chatActivityEnterView, MessageObject messageObject, TLRPC.KeyboardButton keyboardButton) {
        this.f$0 = chatActivityEnterView;
        this.f$1 = messageObject;
        this.f$2 = keyboardButton;
    }

    public final void didSelectDialogs(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z) {
        this.f$0.m2039x4CLASSNAMECLASSNAMEe(this.f$1, this.f$2, dialogsActivity, arrayList, charSequence, z);
    }
}

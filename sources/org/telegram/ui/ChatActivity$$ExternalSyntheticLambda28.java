package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda28 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatActivity f$0;
    public final /* synthetic */ MessagesController f$1;
    public final /* synthetic */ CharSequence f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda28(ChatActivity chatActivity, MessagesController messagesController, CharSequence charSequence, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = messagesController;
        this.f$2 = charSequence;
        this.f$3 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$searchLinks$80(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}

package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$-bRCkaPqqWDtcnmPMbtsYn_oHx0 implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ MessagesController f$1;
    private final /* synthetic */ CharSequence f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$ChatActivity$-bRCkaPqqWDtcnmPMbtsYn_oHx0(ChatActivity chatActivity, MessagesController messagesController, CharSequence charSequence, boolean z) {
        this.f$0 = chatActivity;
        this.f$1 = messagesController;
        this.f$2 = charSequence;
        this.f$3 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$49$ChatActivity(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}

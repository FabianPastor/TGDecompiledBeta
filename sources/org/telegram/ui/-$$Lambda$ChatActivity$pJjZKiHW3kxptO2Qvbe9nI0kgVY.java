package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$pJjZKiHW3kxptO2Qvbe9nI0kgVY implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ MessageObject f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$pJjZKiHW3kxptO2Qvbe9nI0kgVY(ChatActivity chatActivity, int i, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = messageObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$shareMyContact$43$ChatActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}

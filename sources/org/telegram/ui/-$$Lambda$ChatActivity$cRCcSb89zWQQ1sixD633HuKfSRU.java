package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$cRCcSb89zWQQ1sixD633HuKfSRU implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ MessageObject f$2;

    public /* synthetic */ -$$Lambda$ChatActivity$cRCcSb89zWQQ1sixD633HuKfSRU(ChatActivity chatActivity, int i, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = i;
        this.f$2 = messageObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$shareMyContact$47$ChatActivity(this.f$1, this.f$2, dialogInterface, i);
    }
}

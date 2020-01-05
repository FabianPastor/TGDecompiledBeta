package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$R3CU66b2ysVCnM9ui_5weMBHQBs implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ MessageObject f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$R3CU66b2ysVCnM9ui_5weMBHQBs(ChatActivity chatActivity, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processSelectedOption$94$ChatActivity(this.f$1, dialogInterface, i);
    }
}

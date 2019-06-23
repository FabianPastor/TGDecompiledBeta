package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessageObject;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$DIMPYRfOO1arM3MSK4qcHTTOkHM implements OnClickListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ MessageObject f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$DIMPYRfOO1arM3MSK4qcHTTOkHM(ChatActivity chatActivity, MessageObject messageObject) {
        this.f$0 = chatActivity;
        this.f$1 = messageObject;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$processSelectedOption$83$ChatActivity(this.f$1, dialogInterface, i);
    }
}

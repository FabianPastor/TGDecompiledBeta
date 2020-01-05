package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$DM27ZSnxeaKJxwrx58Ru-5vw92w implements OnCancelListener {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$DM27ZSnxeaKJxwrx58Ru-5vw92w(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$scrollToMessageId$64$ChatActivity(dialogInterface);
    }
}

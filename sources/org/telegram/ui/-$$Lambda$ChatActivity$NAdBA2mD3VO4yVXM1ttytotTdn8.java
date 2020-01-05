package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$NAdBA2mD3VO4yVXM1ttytotTdn8 implements OnCancelListener {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$NAdBA2mD3VO4yVXM1ttytotTdn8(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$scrollToLastMessage$62$ChatActivity(dialogInterface);
    }
}

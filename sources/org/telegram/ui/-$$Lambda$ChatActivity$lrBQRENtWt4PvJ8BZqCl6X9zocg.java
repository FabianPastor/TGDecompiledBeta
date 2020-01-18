package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$lrBQRENtWt4PvJ8BZqCl6X9zocg implements OnCancelListener {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$lrBQRENtWt4PvJ8BZqCl6X9zocg(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$scrollToLastMessage$63$ChatActivity(dialogInterface);
    }
}

package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$-E_ibZFz5mtlzQ9GVZLTSlVLx18 implements OnCancelListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$-E_ibZFz5mtlzQ9GVZLTSlVLx18(ChatActivity chatActivity, int i) {
        this.f$0 = chatActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$null$76$ChatActivity(this.f$1, dialogInterface);
    }
}
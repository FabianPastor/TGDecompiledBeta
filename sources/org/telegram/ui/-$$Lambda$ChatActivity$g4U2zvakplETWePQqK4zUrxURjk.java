package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$g4U2zvakplETWePQqK4zUrxURjk implements OnCancelListener {
    private final /* synthetic */ ChatActivity f$0;
    private final /* synthetic */ int f$1;

    public /* synthetic */ -$$Lambda$ChatActivity$g4U2zvakplETWePQqK4zUrxURjk(ChatActivity chatActivity, int i) {
        this.f$0 = chatActivity;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$null$92$ChatActivity(this.f$1, dialogInterface);
    }
}

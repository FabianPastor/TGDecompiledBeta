package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$ie4nUnVCnNn8pOZLFn7U9EOSypo implements OnCancelListener {
    private final /* synthetic */ ChatActivity f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$ie4nUnVCnNn8pOZLFn7U9EOSypo(ChatActivity chatActivity) {
        this.f$0 = chatActivity;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$scrollToMessageId$65$ChatActivity(dialogInterface);
    }
}

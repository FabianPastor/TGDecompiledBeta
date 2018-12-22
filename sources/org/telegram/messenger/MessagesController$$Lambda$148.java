package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.p005ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$148 implements OnCancelListener {
    private final MessagesController arg$1;
    private final int arg$2;
    private final BaseFragment arg$3;

    MessagesController$$Lambda$148(MessagesController messagesController, int i, BaseFragment baseFragment) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = baseFragment;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$checkCanOpenChat$244$MessagesController(this.arg$2, this.arg$3, dialogInterface);
    }
}

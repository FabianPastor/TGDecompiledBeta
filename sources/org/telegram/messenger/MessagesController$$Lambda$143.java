package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.ActionBar.BaseFragment;

final /* synthetic */ class MessagesController$$Lambda$143 implements OnClickListener {
    private final MessagesController arg$1;
    private final int arg$2;
    private final BaseFragment arg$3;

    MessagesController$$Lambda$143(MessagesController messagesController, int i, BaseFragment baseFragment) {
        this.arg$1 = messagesController;
        this.arg$2 = i;
        this.arg$3 = baseFragment;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$checkCanOpenChat$236$MessagesController(this.arg$2, this.arg$3, dialogInterface, i);
    }
}

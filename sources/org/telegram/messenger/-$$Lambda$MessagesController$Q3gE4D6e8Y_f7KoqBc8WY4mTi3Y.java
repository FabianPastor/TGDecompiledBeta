package org.telegram.messenger;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.ui.ActionBar.BaseFragment;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$Q3gE4D6e8Y_f7KoqBc8WY4mTi3Y implements OnCancelListener {
    private final /* synthetic */ MessagesController f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ -$$Lambda$MessagesController$Q3gE4D6e8Y_f7KoqBc8WY4mTi3Y(MessagesController messagesController, int i, BaseFragment baseFragment) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = baseFragment;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$checkCanOpenChat$276$MessagesController(this.f$1, this.f$2, dialogInterface);
    }
}
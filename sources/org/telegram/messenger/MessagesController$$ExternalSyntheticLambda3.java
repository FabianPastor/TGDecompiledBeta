package org.telegram.messenger;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.BaseFragment;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda3 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ BaseFragment f$2;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda3(MessagesController messagesController, int i, BaseFragment baseFragment) {
        this.f$0 = messagesController;
        this.f$1 = i;
        this.f$2 = baseFragment;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$checkCanOpenChat$326(this.f$1, this.f$2, dialogInterface);
    }
}

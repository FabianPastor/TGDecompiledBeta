package org.telegram.messenger;

import android.content.DialogInterface;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda0 implements DialogInterface.OnCancelListener {
    public final /* synthetic */ MessagesController f$0;
    public final /* synthetic */ int f$1;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda0(MessagesController messagesController, int i) {
        this.f$0 = messagesController;
        this.f$1 = i;
    }

    public final void onCancel(DialogInterface dialogInterface) {
        this.f$0.lambda$convertToGigaGroup$211(this.f$1, dialogInterface);
    }
}

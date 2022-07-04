package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$6$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ DialogsActivity.AnonymousClass6 f$0;
    public final /* synthetic */ MessagesController.DialogFilter f$1;

    public /* synthetic */ DialogsActivity$6$$ExternalSyntheticLambda0(DialogsActivity.AnonymousClass6 r1, MessagesController.DialogFilter dialogFilter) {
        this.f$0 = r1;
        this.f$1 = dialogFilter;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3433lambda$showDeleteAlert$2$orgtelegramuiDialogsActivity$6(this.f$1, dialogInterface, i);
    }
}

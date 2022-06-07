package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.MessagesController;

public final /* synthetic */ class LogoutActivity$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int f$0;

    public /* synthetic */ LogoutActivity$$ExternalSyntheticLambda0(int i) {
        this.f$0 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getInstance(this.f$0).performLogout(1);
    }
}

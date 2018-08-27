package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.HashMap;
import org.telegram.messenger.ContactsController;

final /* synthetic */ class LaunchActivity$$Lambda$24 implements OnClickListener {
    private final int arg$1;
    private final HashMap arg$2;
    private final boolean arg$3;
    private final boolean arg$4;

    LaunchActivity$$Lambda$24(int i, HashMap hashMap, boolean z, boolean z2) {
        this.arg$1 = i;
        this.arg$2 = hashMap;
        this.arg$3 = z;
        this.arg$4 = z2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        ContactsController.getInstance(this.arg$1).syncPhoneBookByAlert(this.arg$2, this.arg$3, this.arg$4, true);
    }
}

package org.telegram.ui;

import android.content.DialogInterface;
import java.util.HashMap;
import org.telegram.messenger.ContactsController;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda55 implements DialogInterface.OnClickListener {
    public final /* synthetic */ int f$0;
    public final /* synthetic */ HashMap f$1;
    public final /* synthetic */ boolean f$2;
    public final /* synthetic */ boolean f$3;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda55(int i, HashMap hashMap, boolean z, boolean z2) {
        this.f$0 = i;
        this.f$1 = hashMap;
        this.f$2 = z;
        this.f$3 = z2;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        ContactsController.getInstance(this.f$0).syncPhoneBookByAlert(this.f$1, this.f$2, this.f$3, true);
    }
}

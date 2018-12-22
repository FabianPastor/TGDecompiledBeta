package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.p005ui.ProfileActivity.CLASSNAME;

/* renamed from: org.telegram.ui.ProfileActivity$3$$Lambda$4 */
final /* synthetic */ class ProfileActivity$3$$Lambda$4 implements OnCancelListener {
    private final CLASSNAME arg$1;
    private final int arg$2;

    ProfileActivity$3$$Lambda$4(CLASSNAME CLASSNAME, int i) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$onItemClick$5$ProfileActivity$3(this.arg$2, dialogInterface);
    }
}

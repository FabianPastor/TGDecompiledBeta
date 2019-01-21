package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import org.telegram.ui.ProfileActivity.AnonymousClass3;

final /* synthetic */ class ProfileActivity$3$$Lambda$4 implements OnCancelListener {
    private final AnonymousClass3 arg$1;
    private final int arg$2;

    ProfileActivity$3$$Lambda$4(AnonymousClass3 anonymousClass3, int i) {
        this.arg$1 = anonymousClass3;
        this.arg$2 = i;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.arg$1.lambda$onItemClick$5$ProfileActivity$3(this.arg$2, dialogInterface);
    }
}

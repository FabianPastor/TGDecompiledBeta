package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$izrGH6tzz_c5ZefftonTyrpURyU implements OnClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ boolean[] f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$izrGH6tzz_c5ZefftonTyrpURyU(PassportActivity passportActivity, boolean[] zArr) {
        this.f$0 = passportActivity;
        this.f$1 = zArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$createDocumentDeleteAlert$38$PassportActivity(this.f$1, dialogInterface, i);
    }
}

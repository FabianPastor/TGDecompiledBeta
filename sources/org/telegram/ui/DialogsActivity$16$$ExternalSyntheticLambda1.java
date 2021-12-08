package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.DialogsActivity;

public final /* synthetic */ class DialogsActivity$16$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ DialogsActivity.AnonymousClass16 f$0;
    public final /* synthetic */ long f$1;

    public /* synthetic */ DialogsActivity$16$$ExternalSyntheticLambda1(DialogsActivity.AnonymousClass16 r1, long j) {
        this.f$0 = r1;
        this.f$1 = j;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$needRemoveHint$0(this.f$1, dialogInterface, i);
    }
}

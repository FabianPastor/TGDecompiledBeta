package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public final /* synthetic */ class SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda2 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ SelectAnimatedEmojiDialog.SelectStatusDurationDialog f$0;
    public final /* synthetic */ boolean[] f$1;

    public /* synthetic */ SelectAnimatedEmojiDialog$SelectStatusDurationDialog$$ExternalSyntheticLambda2(SelectAnimatedEmojiDialog.SelectStatusDurationDialog selectStatusDurationDialog, boolean[] zArr) {
        this.f$0 = selectStatusDurationDialog;
        this.f$1 = zArr;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        this.f$0.lambda$new$5(this.f$1, dialogInterface);
    }
}

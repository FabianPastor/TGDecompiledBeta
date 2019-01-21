package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.SettingsActivity.AnonymousClass2;

final /* synthetic */ class SettingsActivity$2$$Lambda$0 implements OnClickListener {
    private final AnonymousClass2 arg$1;

    SettingsActivity$2$$Lambda$0(AnonymousClass2 anonymousClass2) {
        this.arg$1 = anonymousClass2;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$SettingsActivity$2(dialogInterface, i);
    }
}

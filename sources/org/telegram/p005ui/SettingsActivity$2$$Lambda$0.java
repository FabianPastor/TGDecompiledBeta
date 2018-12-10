package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.SettingsActivity.CLASSNAME;

/* renamed from: org.telegram.ui.SettingsActivity$2$$Lambda$0 */
final /* synthetic */ class SettingsActivity$2$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;

    SettingsActivity$2$$Lambda$0(CLASSNAME CLASSNAME) {
        this.arg$1 = CLASSNAME;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$SettingsActivity$2(dialogInterface, i);
    }
}

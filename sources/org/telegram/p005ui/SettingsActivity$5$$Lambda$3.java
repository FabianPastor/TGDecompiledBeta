package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.SettingsActivity.CLASSNAME;

/* renamed from: org.telegram.ui.SettingsActivity$5$$Lambda$3 */
final /* synthetic */ class SettingsActivity$5$$Lambda$3 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final int arg$2;

    SettingsActivity$5$$Lambda$3(CLASSNAME CLASSNAME, int i) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$3$SettingsActivity$5(this.arg$2, dialogInterface, i);
    }
}

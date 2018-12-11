package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.Components.NumberPicker;
import org.telegram.p005ui.SettingsActivity.CLASSNAME;

/* renamed from: org.telegram.ui.SettingsActivity$5$$Lambda$0 */
final /* synthetic */ class SettingsActivity$5$$Lambda$0 implements OnClickListener {
    private final CLASSNAME arg$1;
    private final NumberPicker arg$2;
    private final int arg$3;

    SettingsActivity$5$$Lambda$0(CLASSNAME CLASSNAME, NumberPicker numberPicker, int i) {
        this.arg$1 = CLASSNAME;
        this.arg$2 = numberPicker;
        this.arg$3 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$SettingsActivity$5(this.arg$2, this.arg$3, dialogInterface, i);
    }
}

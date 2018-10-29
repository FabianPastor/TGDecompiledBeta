package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.SettingsActivity.C17245;

final /* synthetic */ class SettingsActivity$5$$Lambda$0 implements OnClickListener {
    private final C17245 arg$1;
    private final NumberPicker arg$2;
    private final int arg$3;

    SettingsActivity$5$$Lambda$0(C17245 c17245, NumberPicker numberPicker, int i) {
        this.arg$1 = c17245;
        this.arg$2 = numberPicker;
        this.arg$3 = i;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onItemClick$0$SettingsActivity$5(this.arg$2, this.arg$3, dialogInterface, i);
    }
}

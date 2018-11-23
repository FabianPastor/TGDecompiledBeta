package org.telegram.messenger;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class AndroidUtilities$$Lambda$1 implements OnClickListener {
    private final Activity arg$1;

    AndroidUtilities$$Lambda$1(Activity activity) {
        this.arg$1 = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        AndroidUtilities.lambda$openForView$1$AndroidUtilities(this.arg$1, dialogInterface, i);
    }
}

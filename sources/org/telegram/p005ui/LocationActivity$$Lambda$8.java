package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/* renamed from: org.telegram.ui.LocationActivity$$Lambda$8 */
final /* synthetic */ class LocationActivity$$Lambda$8 implements OnClickListener {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$8(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$showPermissionAlert$11$LocationActivity(dialogInterface, i);
    }
}

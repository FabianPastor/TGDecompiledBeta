package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class LocationActivity$$Lambda$7 implements OnClickListener {
    private final LocationActivity arg$1;

    LocationActivity$$Lambda$7(LocationActivity locationActivity) {
        this.arg$1 = locationActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$onMapInit$10$LocationActivity(dialogInterface, i);
    }
}
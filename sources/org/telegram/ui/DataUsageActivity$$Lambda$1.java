package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final /* synthetic */ class DataUsageActivity$$Lambda$1 implements OnClickListener {
    private final DataUsageActivity arg$1;
    private final ListAdapter arg$2;

    DataUsageActivity$$Lambda$1(DataUsageActivity dataUsageActivity, ListAdapter listAdapter) {
        this.arg$1 = dataUsageActivity;
        this.arg$2 = listAdapter;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$1$DataUsageActivity(this.arg$2, dialogInterface, i);
    }
}

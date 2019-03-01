package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;

final /* synthetic */ class DataAutoDownloadActivity$$Lambda$5 implements OnClickListener {
    private final DataAutoDownloadActivity arg$1;
    private final Builder arg$10;
    private final View arg$11;
    private final TextCheckBoxCell[] arg$2;
    private final int arg$3;
    private final MaxFileSizeCell[] arg$4;
    private final int arg$5;
    private final TextCheckCell[] arg$6;
    private final int arg$7;
    private final String arg$8;
    private final String arg$9;

    DataAutoDownloadActivity$$Lambda$5(DataAutoDownloadActivity dataAutoDownloadActivity, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, int i2, TextCheckCell[] textCheckCellArr, int i3, String str, String str2, Builder builder, View view) {
        this.arg$1 = dataAutoDownloadActivity;
        this.arg$2 = textCheckBoxCellArr;
        this.arg$3 = i;
        this.arg$4 = maxFileSizeCellArr;
        this.arg$5 = i2;
        this.arg$6 = textCheckCellArr;
        this.arg$7 = i3;
        this.arg$8 = str;
        this.arg$9 = str2;
        this.arg$10 = builder;
        this.arg$11 = view;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$3$DataAutoDownloadActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, this.arg$8, this.arg$9, this.arg$10, this.arg$11, view);
    }
}

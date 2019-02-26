package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.TextCheckCell;

final /* synthetic */ class DataAutoDownloadActivity$$Lambda$3 implements OnClickListener {
    private final TextCheckCell[] arg$1;

    DataAutoDownloadActivity$$Lambda$3(TextCheckCell[] textCheckCellArr) {
        this.arg$1 = textCheckCellArr;
    }

    public void onClick(View view) {
        DataAutoDownloadActivity.lambda$null$1$DataAutoDownloadActivity(this.arg$1, view);
    }
}

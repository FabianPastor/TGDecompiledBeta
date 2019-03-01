package org.telegram.ui;

import android.animation.AnimatorSet;
import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;

final /* synthetic */ class DataAutoDownloadActivity$$Lambda$2 implements OnClickListener {
    private final DataAutoDownloadActivity arg$1;
    private final TextCheckBoxCell arg$2;
    private final TextCheckBoxCell[] arg$3;
    private final int arg$4;
    private final MaxFileSizeCell[] arg$5;
    private final TextCheckCell[] arg$6;
    private final AnimatorSet[] arg$7;

    DataAutoDownloadActivity$$Lambda$2(DataAutoDownloadActivity dataAutoDownloadActivity, TextCheckBoxCell textCheckBoxCell, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, TextCheckCell[] textCheckCellArr, AnimatorSet[] animatorSetArr) {
        this.arg$1 = dataAutoDownloadActivity;
        this.arg$2 = textCheckBoxCell;
        this.arg$3 = textCheckBoxCellArr;
        this.arg$4 = i;
        this.arg$5 = maxFileSizeCellArr;
        this.arg$6 = textCheckCellArr;
        this.arg$7 = animatorSetArr;
    }

    public void onClick(View view) {
        this.arg$1.lambda$null$0$DataAutoDownloadActivity(this.arg$2, this.arg$3, this.arg$4, this.arg$5, this.arg$6, this.arg$7, view);
    }
}

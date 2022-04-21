package org.telegram.ui;

import android.animation.AnimatorSet;
import android.view.View;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;

public final /* synthetic */ class DataAutoDownloadActivity$$ExternalSyntheticLambda1 implements View.OnClickListener {
    public final /* synthetic */ DataAutoDownloadActivity f$0;
    public final /* synthetic */ TextCheckBoxCell f$1;
    public final /* synthetic */ TextCheckBoxCell[] f$2;
    public final /* synthetic */ int f$3;
    public final /* synthetic */ MaxFileSizeCell[] f$4;
    public final /* synthetic */ TextCheckCell[] f$5;
    public final /* synthetic */ AnimatorSet[] f$6;

    public /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda1(DataAutoDownloadActivity dataAutoDownloadActivity, TextCheckBoxCell textCheckBoxCell, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, TextCheckCell[] textCheckCellArr, AnimatorSet[] animatorSetArr) {
        this.f$0 = dataAutoDownloadActivity;
        this.f$1 = textCheckBoxCell;
        this.f$2 = textCheckBoxCellArr;
        this.f$3 = i;
        this.f$4 = maxFileSizeCellArr;
        this.f$5 = textCheckCellArr;
        this.f$6 = animatorSetArr;
    }

    public final void onClick(View view) {
        this.f$0.m2052lambda$createView$0$orgtelegramuiDataAutoDownloadActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, view);
    }
}

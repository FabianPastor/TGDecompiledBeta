package org.telegram.ui;

import android.view.View;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;

public final /* synthetic */ class DataAutoDownloadActivity$$ExternalSyntheticLambda2 implements View.OnClickListener {
    public final /* synthetic */ DataAutoDownloadActivity f$0;
    public final /* synthetic */ TextCheckBoxCell[] f$1;
    public final /* synthetic */ View f$10;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ MaxFileSizeCell[] f$3;
    public final /* synthetic */ int f$4;
    public final /* synthetic */ TextCheckCell[] f$5;
    public final /* synthetic */ int f$6;
    public final /* synthetic */ String f$7;
    public final /* synthetic */ String f$8;
    public final /* synthetic */ BottomSheet.Builder f$9;

    public /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda2(DataAutoDownloadActivity dataAutoDownloadActivity, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, int i2, TextCheckCell[] textCheckCellArr, int i3, String str, String str2, BottomSheet.Builder builder, View view) {
        this.f$0 = dataAutoDownloadActivity;
        this.f$1 = textCheckBoxCellArr;
        this.f$2 = i;
        this.f$3 = maxFileSizeCellArr;
        this.f$4 = i2;
        this.f$5 = textCheckCellArr;
        this.f$6 = i3;
        this.f$7 = str;
        this.f$8 = str2;
        this.f$9 = builder;
        this.f$10 = view;
    }

    public final void onClick(View view) {
        this.f$0.m2804lambda$createView$3$orgtelegramuiDataAutoDownloadActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, view);
    }
}

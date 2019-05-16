package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.Cells.MaxFileSizeCell;
import org.telegram.ui.Cells.TextCheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataAutoDownloadActivity$bStK8LWlLSXpGpzkzlpaQV4c9zo implements OnClickListener {
    private final /* synthetic */ DataAutoDownloadActivity f$0;
    private final /* synthetic */ TextCheckBoxCell[] f$1;
    private final /* synthetic */ View f$10;
    private final /* synthetic */ int f$2;
    private final /* synthetic */ MaxFileSizeCell[] f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ TextCheckCell[] f$5;
    private final /* synthetic */ int f$6;
    private final /* synthetic */ String f$7;
    private final /* synthetic */ String f$8;
    private final /* synthetic */ Builder f$9;

    public /* synthetic */ -$$Lambda$DataAutoDownloadActivity$bStK8LWlLSXpGpzkzlpaQV4c9zo(DataAutoDownloadActivity dataAutoDownloadActivity, TextCheckBoxCell[] textCheckBoxCellArr, int i, MaxFileSizeCell[] maxFileSizeCellArr, int i2, TextCheckCell[] textCheckCellArr, int i3, String str, String str2, Builder builder, View view) {
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
        this.f$0.lambda$null$3$DataAutoDownloadActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, view);
    }
}

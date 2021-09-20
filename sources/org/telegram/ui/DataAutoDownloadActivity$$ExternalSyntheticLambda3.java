package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Cells.TextCheckCell;

public final /* synthetic */ class DataAutoDownloadActivity$$ExternalSyntheticLambda3 implements View.OnClickListener {
    public final /* synthetic */ TextCheckCell[] f$0;

    public /* synthetic */ DataAutoDownloadActivity$$ExternalSyntheticLambda3(TextCheckCell[] textCheckCellArr) {
        this.f$0 = textCheckCellArr;
    }

    public final void onClick(View view) {
        this.f$0[0].setChecked(!this.f$0[0].isChecked());
    }
}

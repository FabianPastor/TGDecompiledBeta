package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$GroupCreateActivity$3XN987OICBmldOkyTJTx1cRwAQA implements OnClickListener {
    private final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ -$$Lambda$GroupCreateActivity$3XN987OICBmldOkyTJTx1cRwAQA(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        this.f$0[0].setChecked(this.f$0[0].isChecked() ^ 1, true);
    }
}

package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$fyJkH2woSuhQkdGYMOHIrosl40Q implements OnClickListener {
    private final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$fyJkH2woSuhQkdGYMOHIrosl40Q(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        ChatActivity.lambda$showRequestUrlAlert$96(this.f$0, view);
    }
}

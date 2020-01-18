package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$VQFvSbjHYDddE7UuiKqyRPBBg5o implements OnClickListener {
    private final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$VQFvSbjHYDddE7UuiKqyRPBBg5o(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        ChatActivity.lambda$showRequestUrlAlert$103(this.f$0, view);
    }
}

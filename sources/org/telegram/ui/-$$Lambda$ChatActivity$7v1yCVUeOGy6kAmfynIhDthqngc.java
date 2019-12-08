package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import org.telegram.ui.Cells.CheckBoxCell;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ChatActivity$7v1yCVUeOGy6kAmfynIhDthqngc implements OnClickListener {
    private final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ -$$Lambda$ChatActivity$7v1yCVUeOGy6kAmfynIhDthqngc(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        ChatActivity.lambda$showRequestUrlAlert$98(this.f$0, view);
    }
}

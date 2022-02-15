package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda82 implements View.OnClickListener {
    public final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda82(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        ChatActivity.lambda$showRequestUrlAlert$204(this.f$0, view);
    }
}

package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class ChatActivity$$ExternalSyntheticLambda249 implements View.OnClickListener {
    public final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ ChatActivity$$ExternalSyntheticLambda249(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        ChatActivity.lambda$showRequestUrlAlert$229(this.f$0, view);
    }
}

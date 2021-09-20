package org.telegram.ui;

import android.view.View;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class GroupCreateActivity$$ExternalSyntheticLambda4 implements View.OnClickListener {
    public final /* synthetic */ CheckBoxCell[] f$0;

    public /* synthetic */ GroupCreateActivity$$ExternalSyntheticLambda4(CheckBoxCell[] checkBoxCellArr) {
        this.f$0 = checkBoxCellArr;
    }

    public final void onClick(View view) {
        this.f$0[0].setChecked(!this.f$0[0].isChecked(), true);
    }
}

package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.messenger.ChatObject;
import org.telegram.ui.Cells.CheckBoxCell;

public final /* synthetic */ class GroupCallActivity$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ ChatObject.Call f$0;
    public final /* synthetic */ CheckBoxCell[] f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ Runnable f$3;

    public /* synthetic */ GroupCallActivity$$ExternalSyntheticLambda4(ChatObject.Call call, CheckBoxCell[] checkBoxCellArr, int i, Runnable runnable) {
        this.f$0 = call;
        this.f$1 = checkBoxCellArr;
        this.f$2 = i;
        this.f$3 = runnable;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        GroupCallActivity.processOnLeave(this.f$0, this.f$1[0].isChecked(), this.f$2, this.f$3);
    }
}

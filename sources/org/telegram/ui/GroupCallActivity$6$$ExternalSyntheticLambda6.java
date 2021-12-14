package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda6 implements DialogInterface.OnShowListener {
    public final /* synthetic */ GroupCallActivity.AnonymousClass6 f$0;
    public final /* synthetic */ AlertDialog f$1;
    public final /* synthetic */ EditTextBoldCursor f$2;

    public /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda6(GroupCallActivity.AnonymousClass6 r1, AlertDialog alertDialog, EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = r1;
        this.f$1 = alertDialog;
        this.f$2 = editTextBoldCursor;
    }

    public final void onShow(DialogInterface dialogInterface) {
        this.f$0.lambda$onItemClick$6(this.f$1, this.f$2, dialogInterface);
    }
}

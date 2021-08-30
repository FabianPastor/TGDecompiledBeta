package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$$ExternalSyntheticLambda4 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCallActivity.AnonymousClass6 f$0;
    public final /* synthetic */ EditTextBoldCursor f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ GroupCallActivity$6$$ExternalSyntheticLambda4(GroupCallActivity.AnonymousClass6 r1, EditTextBoldCursor editTextBoldCursor, int i) {
        this.f$0 = r1;
        this.f$1 = editTextBoldCursor;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onItemClick$6(this.f$1, this.f$2, dialogInterface, i);
    }
}

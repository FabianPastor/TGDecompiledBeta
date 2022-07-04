package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.GroupCallActivity;

public final /* synthetic */ class GroupCallActivity$6$1$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ GroupCallActivity.AnonymousClass6.AnonymousClass1 f$0;
    public final /* synthetic */ EditTextBoldCursor f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ GroupCallActivity$6$1$$ExternalSyntheticLambda1(GroupCallActivity.AnonymousClass6.AnonymousClass1 r1, EditTextBoldCursor editTextBoldCursor, int i) {
        this.f$0 = r1;
        this.f$1 = editTextBoldCursor;
        this.f$2 = i;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m3563lambda$onStartRecord$3$orgtelegramuiGroupCallActivity$6$1(this.f$1, this.f$2, dialogInterface, i);
    }
}

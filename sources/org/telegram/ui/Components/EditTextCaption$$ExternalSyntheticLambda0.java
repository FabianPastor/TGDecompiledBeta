package org.telegram.ui.Components;

import android.content.DialogInterface;

public final /* synthetic */ class EditTextCaption$$ExternalSyntheticLambda0 implements DialogInterface.OnClickListener {
    public final /* synthetic */ EditTextCaption f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ int f$2;
    public final /* synthetic */ EditTextBoldCursor f$3;

    public /* synthetic */ EditTextCaption$$ExternalSyntheticLambda0(EditTextCaption editTextCaption, int i, int i2, EditTextBoldCursor editTextBoldCursor) {
        this.f$0 = editTextCaption;
        this.f$1 = i;
        this.f$2 = i2;
        this.f$3 = editTextBoldCursor;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$makeSelectedUrl$0(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}

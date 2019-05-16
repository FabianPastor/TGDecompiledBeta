package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$-OkER2xuwjfE0-6Y_fzH2uLkAhM implements OnClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ ArrayList f$1;

    public /* synthetic */ -$$Lambda$PassportActivity$-OkER2xuwjfE0-6Y_fzH2uLkAhM(PassportActivity passportActivity, ArrayList arrayList) {
        this.f$0 = passportActivity;
        this.f$1 = arrayList;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$openAddDocumentAlert$23$PassportActivity(this.f$1, dialogInterface, i);
    }
}

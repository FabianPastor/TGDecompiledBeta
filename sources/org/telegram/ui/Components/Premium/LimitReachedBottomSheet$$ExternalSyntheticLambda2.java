package org.telegram.ui.Components.Premium;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class LimitReachedBottomSheet$$ExternalSyntheticLambda2 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LimitReachedBottomSheet f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC.User f$2;

    public /* synthetic */ LimitReachedBottomSheet$$ExternalSyntheticLambda2(LimitReachedBottomSheet limitReachedBottomSheet, ArrayList arrayList, TLRPC.User user) {
        this.f$0 = limitReachedBottomSheet;
        this.f$1 = arrayList;
        this.f$2 = user;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.m1239xe2652fc7(this.f$1, this.f$2, dialogInterface, i);
    }
}

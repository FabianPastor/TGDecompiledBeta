package org.telegram.ui.Components.Premium;

import android.content.DialogInterface;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$User;

public final /* synthetic */ class LimitReachedBottomSheet$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ LimitReachedBottomSheet f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ TLRPC$User f$2;

    public /* synthetic */ LimitReachedBottomSheet$$ExternalSyntheticLambda1(LimitReachedBottomSheet limitReachedBottomSheet, ArrayList arrayList, TLRPC$User tLRPC$User) {
        this.f$0 = limitReachedBottomSheet;
        this.f$1 = arrayList;
        this.f$2 = tLRPC$User;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$leaveFromSelectedGroups$4(this.f$1, this.f$2, dialogInterface, i);
    }
}
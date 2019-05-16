package org.telegram.ui.Components;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.messenger.MessagesController;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$AlertsCreator$e12ECB5TMizqnTDhs10iwsjTFck implements OnClickListener {
    private final /* synthetic */ int[] f$0;

    public /* synthetic */ -$$Lambda$AlertsCreator$e12ECB5TMizqnTDhs10iwsjTFck(int[] iArr) {
        this.f$0 = iArr;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        MessagesController.getGlobalMainSettings().edit().putInt("keep_media", this.f$0[0]).commit();
    }
}

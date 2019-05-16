package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$StickersActivity$ListAdapter$4oHDK9bhePlEa0rCLASSNAMEWtPkp-42Q implements OnClickListener {
    private final /* synthetic */ ListAdapter f$0;
    private final /* synthetic */ int[] f$1;
    private final /* synthetic */ TL_messages_stickerSet f$2;

    public /* synthetic */ -$$Lambda$StickersActivity$ListAdapter$4oHDK9bhePlEa0rCLASSNAMEWtPkp-42Q(ListAdapter listAdapter, int[] iArr, TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = listAdapter;
        this.f$1 = iArr;
        this.f$2 = tL_messages_stickerSet;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$0$StickersActivity$ListAdapter(this.f$1, this.f$2, dialogInterface, i);
    }
}

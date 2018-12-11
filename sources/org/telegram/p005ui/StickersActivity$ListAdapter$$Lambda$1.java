package org.telegram.p005ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import org.telegram.p005ui.StickersActivity.ListAdapter;
import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

/* renamed from: org.telegram.ui.StickersActivity$ListAdapter$$Lambda$1 */
final /* synthetic */ class StickersActivity$ListAdapter$$Lambda$1 implements OnClickListener {
    private final ListAdapter arg$1;
    private final int[] arg$2;
    private final TL_messages_stickerSet arg$3;

    StickersActivity$ListAdapter$$Lambda$1(ListAdapter listAdapter, int[] iArr, TL_messages_stickerSet tL_messages_stickerSet) {
        this.arg$1 = listAdapter;
        this.arg$2 = iArr;
        this.arg$3 = tL_messages_stickerSet;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.arg$1.lambda$null$0$StickersActivity$ListAdapter(this.arg$2, this.arg$3, dialogInterface, i);
    }
}

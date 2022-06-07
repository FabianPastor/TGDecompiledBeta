package org.telegram.ui;

import android.content.DialogInterface;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.StickersActivity;

public final /* synthetic */ class StickersActivity$ListAdapter$$ExternalSyntheticLambda1 implements DialogInterface.OnClickListener {
    public final /* synthetic */ StickersActivity.ListAdapter f$0;
    public final /* synthetic */ int[] f$1;
    public final /* synthetic */ TLRPC$TL_messages_stickerSet f$2;

    public /* synthetic */ StickersActivity$ListAdapter$$ExternalSyntheticLambda1(StickersActivity.ListAdapter listAdapter, int[] iArr, TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet) {
        this.f$0 = listAdapter;
        this.f$1 = iArr;
        this.f$2 = tLRPC$TL_messages_stickerSet;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$onCreateViewHolder$3(this.f$1, this.f$2, dialogInterface, i);
    }
}

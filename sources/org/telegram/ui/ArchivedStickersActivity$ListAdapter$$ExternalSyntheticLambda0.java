package org.telegram.ui;

import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ArchivedStickersActivity;
import org.telegram.ui.Cells.ArchivedStickerSetCell;

public final /* synthetic */ class ArchivedStickersActivity$ListAdapter$$ExternalSyntheticLambda0 implements ArchivedStickerSetCell.OnCheckedChangeListener {
    public final /* synthetic */ ArchivedStickersActivity.ListAdapter f$0;
    public final /* synthetic */ TLRPC.StickerSetCovered f$1;

    public /* synthetic */ ArchivedStickersActivity$ListAdapter$$ExternalSyntheticLambda0(ArchivedStickersActivity.ListAdapter listAdapter, TLRPC.StickerSetCovered stickerSetCovered) {
        this.f$0 = listAdapter;
        this.f$1 = stickerSetCovered;
    }

    public final void onCheckedChanged(ArchivedStickerSetCell archivedStickerSetCell, boolean z) {
        this.f$0.m1370x7ffa3458(this.f$1, archivedStickerSetCell, z);
    }
}

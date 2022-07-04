package org.telegram.ui;

import org.telegram.tgnet.TLRPC$StickerSetCovered;
import org.telegram.ui.ArchivedStickersActivity;
import org.telegram.ui.Cells.ArchivedStickerSetCell;

public final /* synthetic */ class ArchivedStickersActivity$ListAdapter$$ExternalSyntheticLambda0 implements ArchivedStickerSetCell.OnCheckedChangeListener {
    public final /* synthetic */ ArchivedStickersActivity.ListAdapter f$0;
    public final /* synthetic */ TLRPC$StickerSetCovered f$1;

    public /* synthetic */ ArchivedStickersActivity$ListAdapter$$ExternalSyntheticLambda0(ArchivedStickersActivity.ListAdapter listAdapter, TLRPC$StickerSetCovered tLRPC$StickerSetCovered) {
        this.f$0 = listAdapter;
        this.f$1 = tLRPC$StickerSetCovered;
    }

    public final void onCheckedChanged(ArchivedStickerSetCell archivedStickerSetCell, boolean z) {
        this.f$0.lambda$onBindViewHolder$0(this.f$1, archivedStickerSetCell, z);
    }
}

package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_messages_stickerSet;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DataQuery$Gs_0kL3OC_eV-RiFuvATRDxcFsE implements Runnable {
    private final /* synthetic */ DataQuery f$0;
    private final /* synthetic */ TL_messages_stickerSet f$1;

    public /* synthetic */ -$$Lambda$DataQuery$Gs_0kL3OC_eV-RiFuvATRDxcFsE(DataQuery dataQuery, TL_messages_stickerSet tL_messages_stickerSet) {
        this.f$0 = dataQuery;
        this.f$1 = tL_messages_stickerSet;
    }

    public final void run() {
        this.f$0.lambda$putSetToCache$9$DataQuery(this.f$1);
    }
}

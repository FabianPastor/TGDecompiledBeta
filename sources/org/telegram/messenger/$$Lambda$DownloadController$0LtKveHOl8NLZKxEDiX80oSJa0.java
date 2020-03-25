package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$DownloadController$0LtKveHOl8NLZKx-EDiX80oSJa0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$DownloadController$0LtKveHOl8NLZKxEDiX80oSJa0 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$DownloadController$0LtKveHOl8NLZKxEDiX80oSJa0 INSTANCE = new $$Lambda$DownloadController$0LtKveHOl8NLZKxEDiX80oSJa0();

    private /* synthetic */ $$Lambda$DownloadController$0LtKveHOl8NLZKxEDiX80oSJa0() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        DownloadController.lambda$savePresetToServer$3(tLObject, tLRPC$TL_error);
    }
}

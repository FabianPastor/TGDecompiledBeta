package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8 INSTANCE = new $$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8();

    private /* synthetic */ $$Lambda$FileRefController$9vHgXK5LhN8UmJHe149yk5CGvC8() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        FileRefController.lambda$onUpdateObjectReference$25(tLObject, tLRPC$TL_error);
    }
}

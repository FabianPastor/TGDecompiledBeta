package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$JU6Vn2XI6a6J78k6RJwedOdT0ek  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$JU6Vn2XI6a6J78k6RJwedOdT0ek implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$JU6Vn2XI6a6J78k6RJwedOdT0ek INSTANCE = new $$Lambda$MessagesController$JU6Vn2XI6a6J78k6RJwedOdT0ek();

    private /* synthetic */ $$Lambda$MessagesController$JU6Vn2XI6a6J78k6RJwedOdT0ek() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$installTheme$71(tLObject, tL_error);
    }
}

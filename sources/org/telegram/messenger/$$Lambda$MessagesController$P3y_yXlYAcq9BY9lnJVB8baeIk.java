package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$P3-y_yXlYAcq9BY9lnJVB8baeIk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$P3y_yXlYAcq9BY9lnJVB8baeIk implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$P3y_yXlYAcq9BY9lnJVB8baeIk INSTANCE = new $$Lambda$MessagesController$P3y_yXlYAcq9BY9lnJVB8baeIk();

    private /* synthetic */ $$Lambda$MessagesController$P3y_yXlYAcq9BY9lnJVB8baeIk() {
    }

    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
        MessagesController.lambda$markMessageContentAsRead$156(tLObject, tL_error);
    }
}

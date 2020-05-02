package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$QF-RU4ME6kAk-ft14OvPTmc9or4  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$QFRU4ME6kAkft14OvPTmc9or4 implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$QFRU4ME6kAkft14OvPTmc9or4 INSTANCE = new $$Lambda$MessagesController$QFRU4ME6kAkft14OvPTmc9or4();

    private /* synthetic */ $$Lambda$MessagesController$QFRU4ME6kAkft14OvPTmc9or4() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$markMentionsAsRead$176(tLObject, tLRPC$TL_error);
    }
}

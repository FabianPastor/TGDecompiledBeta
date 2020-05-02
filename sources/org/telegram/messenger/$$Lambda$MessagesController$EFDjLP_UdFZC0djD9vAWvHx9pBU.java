package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$EFDjLP_UdFZC0djD9vAWvHx9pBU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$EFDjLP_UdFZC0djD9vAWvHx9pBU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$EFDjLP_UdFZC0djD9vAWvHx9pBU INSTANCE = new $$Lambda$MessagesController$EFDjLP_UdFZC0djD9vAWvHx9pBU();

    private /* synthetic */ $$Lambda$MessagesController$EFDjLP_UdFZC0djD9vAWvHx9pBU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$completeReadTask$174(tLObject, tLRPC$TL_error);
    }
}

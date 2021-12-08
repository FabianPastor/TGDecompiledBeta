package org.telegram.messenger;

import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class FileRefController$$ExternalSyntheticLambda28 implements Runnable {
    public final /* synthetic */ FileRefController f$0;
    public final /* synthetic */ TLRPC.TL_messages_sendMultiMedia f$1;
    public final /* synthetic */ Object[] f$2;

    public /* synthetic */ FileRefController$$ExternalSyntheticLambda28(FileRefController fileRefController, TLRPC.TL_messages_sendMultiMedia tL_messages_sendMultiMedia, Object[] objArr) {
        this.f$0 = fileRefController;
        this.f$1 = tL_messages_sendMultiMedia;
        this.f$2 = objArr;
    }

    public final void run() {
        this.f$0.m648xcb28ae7e(this.f$1, this.f$2);
    }
}

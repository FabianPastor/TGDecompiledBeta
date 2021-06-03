package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$ISXtXUr9hgrloUJK0I3_P8VKzCU  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$ISXtXUr9hgrloUJK0I3_P8VKzCU implements RequestDelegate {
    public static final /* synthetic */ $$Lambda$MessagesController$ISXtXUr9hgrloUJK0I3_P8VKzCU INSTANCE = new $$Lambda$MessagesController$ISXtXUr9hgrloUJK0I3_P8VKzCU();

    private /* synthetic */ $$Lambda$MessagesController$ISXtXUr9hgrloUJK0I3_P8VKzCU() {
    }

    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        MessagesController.lambda$blockPeer$62(tLObject, tLRPC$TL_error);
    }
}

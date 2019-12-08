package org.telegram.messenger;

import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$0hPcYvKhm1VkgzSScmSOznrQVcc implements RequestDelegate {
    public static final /* synthetic */ -$$Lambda$MessagesController$0hPcYvKhm1VkgzSScmSOznrQVcc INSTANCE = new -$$Lambda$MessagesController$0hPcYvKhm1VkgzSScmSOznrQVcc();

    private /* synthetic */ -$$Lambda$MessagesController$0hPcYvKhm1VkgzSScmSOznrQVcc() {
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        MessagesController.lambda$markMentionMessageAsRead$144(tLObject, tL_error);
    }
}

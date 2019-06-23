package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Update;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$ebZej4dhcIpF5Mmy5QOrl8QRmY0 implements Comparator {
    private final /* synthetic */ MessagesController f$0;

    public /* synthetic */ -$$Lambda$MessagesController$ebZej4dhcIpF5Mmy5QOrl8QRmY0(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$new$2$MessagesController((Update) obj, (Update) obj2);
    }
}

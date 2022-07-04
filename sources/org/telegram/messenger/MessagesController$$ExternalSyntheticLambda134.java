package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda134 implements Comparator {
    public final /* synthetic */ MessagesController f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda134(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.m302lambda$new$6$orgtelegrammessengerMessagesController((TLRPC.Dialog) obj, (TLRPC.Dialog) obj2);
    }
}

package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda136 implements Comparator {
    public final /* synthetic */ MessagesController f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda136(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.m304lambda$new$8$orgtelegrammessengerMessagesController((TLRPC.Update) obj, (TLRPC.Update) obj2);
    }
}

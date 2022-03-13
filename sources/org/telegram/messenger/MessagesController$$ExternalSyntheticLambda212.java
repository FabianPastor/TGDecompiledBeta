package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Dialog;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda212 implements Comparator {
    public final /* synthetic */ MessagesController f$0;

    public /* synthetic */ MessagesController$$ExternalSyntheticLambda212(MessagesController messagesController) {
        this.f$0 = messagesController;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.lambda$new$6((TLRPC$Dialog) obj, (TLRPC$Dialog) obj2);
    }
}

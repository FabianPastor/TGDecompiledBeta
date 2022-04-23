package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda220 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda220 INSTANCE = new MessagesController$$ExternalSyntheticLambda220();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda220() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

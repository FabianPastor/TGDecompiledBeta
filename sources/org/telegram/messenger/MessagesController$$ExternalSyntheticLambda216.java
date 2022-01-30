package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda216 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda216 INSTANCE = new MessagesController$$ExternalSyntheticLambda216();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda216() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

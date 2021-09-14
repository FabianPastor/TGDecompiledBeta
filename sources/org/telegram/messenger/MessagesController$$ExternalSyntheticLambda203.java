package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda203 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda203 INSTANCE = new MessagesController$$ExternalSyntheticLambda203();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda203() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

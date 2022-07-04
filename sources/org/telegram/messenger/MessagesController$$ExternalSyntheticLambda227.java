package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

public final /* synthetic */ class MessagesController$$ExternalSyntheticLambda227 implements Comparator {
    public static final /* synthetic */ MessagesController$$ExternalSyntheticLambda227 INSTANCE = new MessagesController$$ExternalSyntheticLambda227();

    private /* synthetic */ MessagesController$$ExternalSyntheticLambda227() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

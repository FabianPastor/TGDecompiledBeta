package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class MessagesController$$Lambda$119 implements Comparator {
    static final Comparator $instance = new MessagesController$$Lambda$119();

    private MessagesController$$Lambda$119() {
    }

    public int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

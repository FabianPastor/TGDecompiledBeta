package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

final /* synthetic */ class MessagesController$$Lambda$120 implements Comparator {
    static final Comparator $instance = new MessagesController$$Lambda$120();

    private MessagesController$$Lambda$120() {
    }

    public int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

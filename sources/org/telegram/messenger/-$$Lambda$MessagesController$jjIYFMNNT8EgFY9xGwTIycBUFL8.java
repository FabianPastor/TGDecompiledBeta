package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$jjIYFMNNT8EgFY9xGwTIycBUFL8 implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$jjIYFMNNT8EgFY9xGwTIycBUFL8 INSTANCE = new -$$Lambda$MessagesController$jjIYFMNNT8EgFY9xGwTIycBUFL8();

    private /* synthetic */ -$$Lambda$MessagesController$jjIYFMNNT8EgFY9xGwTIycBUFL8() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

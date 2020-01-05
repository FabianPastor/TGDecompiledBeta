package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$r6viuXq_dpBF5FJFu8L7BQA0K6U implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$r6viuXq_dpBF5FJFu8L7BQA0K6U INSTANCE = new -$$Lambda$MessagesController$r6viuXq_dpBF5FJFu8L7BQA0K6U();

    private /* synthetic */ -$$Lambda$MessagesController$r6viuXq_dpBF5FJFu8L7BQA0K6U() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

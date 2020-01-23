package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$rhbsDIk1nTSWfFYZoXkT_x6A-ZA implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$rhbsDIk1nTSWfFYZoXkT_x6A-ZA INSTANCE = new -$$Lambda$MessagesController$rhbsDIk1nTSWfFYZoXkT_x6A-ZA();

    private /* synthetic */ -$$Lambda$MessagesController$rhbsDIk1nTSWfFYZoXkT_x6A-ZA() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

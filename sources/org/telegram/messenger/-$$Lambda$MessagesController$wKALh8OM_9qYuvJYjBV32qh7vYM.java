package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$wKALh8OM_9qYuvJYjBV32qh7vYM implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$wKALh8OM_9qYuvJYjBV32qh7vYM INSTANCE = new -$$Lambda$MessagesController$wKALh8OM_9qYuvJYjBV32qh7vYM();

    private /* synthetic */ -$$Lambda$MessagesController$wKALh8OM_9qYuvJYjBV32qh7vYM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

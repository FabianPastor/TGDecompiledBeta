package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$AUzdmo52knMl_KOgEuWErrviEZM implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$AUzdmo52knMl_KOgEuWErrviEZM INSTANCE = new -$$Lambda$MessagesController$AUzdmo52knMl_KOgEuWErrviEZM();

    private /* synthetic */ -$$Lambda$MessagesController$AUzdmo52knMl_KOgEuWErrviEZM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

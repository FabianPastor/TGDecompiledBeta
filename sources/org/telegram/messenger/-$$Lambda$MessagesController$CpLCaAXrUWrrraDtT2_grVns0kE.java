package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$CpLCaAXrUWrrraDtT2_grVns0kE implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$CpLCaAXrUWrrraDtT2_grVns0kE INSTANCE = new -$$Lambda$MessagesController$CpLCaAXrUWrrraDtT2_grVns0kE();

    private /* synthetic */ -$$Lambda$MessagesController$CpLCaAXrUWrrraDtT2_grVns0kE() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

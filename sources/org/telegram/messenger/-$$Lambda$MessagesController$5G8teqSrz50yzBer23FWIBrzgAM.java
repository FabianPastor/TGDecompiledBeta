package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$5G8teqSrz50yzBer23FWIBrzgAM implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$5G8teqSrz50yzBer23FWIBrzgAM INSTANCE = new -$$Lambda$MessagesController$5G8teqSrz50yzBer23FWIBrzgAM();

    private /* synthetic */ -$$Lambda$MessagesController$5G8teqSrz50yzBer23FWIBrzgAM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

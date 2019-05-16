package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$JGJclbw8cDS2wcbI-Tj2zlng-g0 implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$JGJclbw8cDS2wcbI-Tj2zlng-g0 INSTANCE = new -$$Lambda$MessagesController$JGJclbw8cDS2wcbI-Tj2zlng-g0();

    private /* synthetic */ -$$Lambda$MessagesController$JGJclbw8cDS2wcbI-Tj2zlng-g0() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

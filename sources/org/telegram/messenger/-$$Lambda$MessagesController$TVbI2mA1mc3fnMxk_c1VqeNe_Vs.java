package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$TVbI2mA1mc3fnMxk_c1VqeNe_Vs implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$TVbI2mA1mc3fnMxk_c1VqeNe_Vs INSTANCE = new -$$Lambda$MessagesController$TVbI2mA1mc3fnMxk_c1VqeNe_Vs();

    private /* synthetic */ -$$Lambda$MessagesController$TVbI2mA1mc3fnMxk_c1VqeNe_Vs() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

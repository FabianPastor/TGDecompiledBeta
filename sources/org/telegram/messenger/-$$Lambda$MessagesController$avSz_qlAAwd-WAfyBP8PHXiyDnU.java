package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$avSz_qlAAwd-WAfyBP8PHXiyDnU implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$avSz_qlAAwd-WAfyBP8PHXiyDnU INSTANCE = new -$$Lambda$MessagesController$avSz_qlAAwd-WAfyBP8PHXiyDnU();

    private /* synthetic */ -$$Lambda$MessagesController$avSz_qlAAwd-WAfyBP8PHXiyDnU() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

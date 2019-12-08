package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$3834nBPF4EOwUWsRWpTHDeDyk94 implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$3834nBPF4EOwUWsRWpTHDeDyk94 INSTANCE = new -$$Lambda$MessagesController$3834nBPF4EOwUWsRWpTHDeDyk94();

    private /* synthetic */ -$$Lambda$MessagesController$3834nBPF4EOwUWsRWpTHDeDyk94() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

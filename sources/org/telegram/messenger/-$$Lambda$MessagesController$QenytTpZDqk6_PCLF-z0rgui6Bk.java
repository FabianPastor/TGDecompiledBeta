package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$QenytTpZDqk6_PCLF-z0rgui6Bk implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$QenytTpZDqk6_PCLF-z0rgui6Bk INSTANCE = new -$$Lambda$MessagesController$QenytTpZDqk6_PCLF-z0rgui6Bk();

    private /* synthetic */ -$$Lambda$MessagesController$QenytTpZDqk6_PCLF-z0rgui6Bk() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$tJ3bWEuWlp4A6o4NctxCTApO7Bo implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$tJ3bWEuWlp4A6o4NctxCTApO7Bo INSTANCE = new -$$Lambda$MessagesController$tJ3bWEuWlp4A6o4NctxCTApO7Bo();

    private /* synthetic */ -$$Lambda$MessagesController$tJ3bWEuWlp4A6o4NctxCTApO7Bo() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

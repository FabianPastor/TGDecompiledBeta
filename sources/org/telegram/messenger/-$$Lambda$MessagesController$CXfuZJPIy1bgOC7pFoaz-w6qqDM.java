package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$CXfuZJPIy1bgOC7pFoaz-w6qqDM implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$CXfuZJPIy1bgOC7pFoaz-w6qqDM INSTANCE = new -$$Lambda$MessagesController$CXfuZJPIy1bgOC7pFoaz-w6qqDM();

    private /* synthetic */ -$$Lambda$MessagesController$CXfuZJPIy1bgOC7pFoaz-w6qqDM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$PUyUVZnZ70f6Cr73GU0SUNuqJHo  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$PUyUVZnZ70f6Cr73GU0SUNuqJHo implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$PUyUVZnZ70f6Cr73GU0SUNuqJHo INSTANCE = new $$Lambda$MessagesController$PUyUVZnZ70f6Cr73GU0SUNuqJHo();

    private /* synthetic */ $$Lambda$MessagesController$PUyUVZnZ70f6Cr73GU0SUNuqJHo() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

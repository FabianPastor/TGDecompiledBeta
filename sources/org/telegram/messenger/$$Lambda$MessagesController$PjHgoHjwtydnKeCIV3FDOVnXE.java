package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$PjHgoHjwtydnKeCI--V3FDOVnXE  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$PjHgoHjwtydnKeCIV3FDOVnXE implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$PjHgoHjwtydnKeCIV3FDOVnXE INSTANCE = new $$Lambda$MessagesController$PjHgoHjwtydnKeCIV3FDOVnXE();

    private /* synthetic */ $$Lambda$MessagesController$PjHgoHjwtydnKeCIV3FDOVnXE() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

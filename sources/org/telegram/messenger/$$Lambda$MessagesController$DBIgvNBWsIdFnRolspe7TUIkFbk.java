package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$DBIgvNBWsIdFnRolspe7TUIkFbk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$DBIgvNBWsIdFnRolspe7TUIkFbk implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$DBIgvNBWsIdFnRolspe7TUIkFbk INSTANCE = new $$Lambda$MessagesController$DBIgvNBWsIdFnRolspe7TUIkFbk();

    private /* synthetic */ $$Lambda$MessagesController$DBIgvNBWsIdFnRolspe7TUIkFbk() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

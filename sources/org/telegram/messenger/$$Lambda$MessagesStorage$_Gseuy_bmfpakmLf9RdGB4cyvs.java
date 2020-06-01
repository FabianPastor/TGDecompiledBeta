package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Message;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesStorage$-_Gseuy_bmfpakmLf9RdGB4cyvs  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesStorage$_Gseuy_bmfpakmLf9RdGB4cyvs implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesStorage$_Gseuy_bmfpakmLf9RdGB4cyvs INSTANCE = new $$Lambda$MessagesStorage$_Gseuy_bmfpakmLf9RdGB4cyvs();

    private /* synthetic */ $$Lambda$MessagesStorage$_Gseuy_bmfpakmLf9RdGB4cyvs() {
    }

    public final int compare(Object obj, Object obj2) {
        return MessagesStorage.lambda$getMessagesInternal$106((TLRPC$Message) obj, (TLRPC$Message) obj2);
    }
}

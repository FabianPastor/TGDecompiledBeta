package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$zxGAFs-qAd9PZfsdBDBBTftIDsM  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$zxGAFsqAd9PZfsdBDBBTftIDsM implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$zxGAFsqAd9PZfsdBDBBTftIDsM INSTANCE = new $$Lambda$MessagesController$zxGAFsqAd9PZfsdBDBBTftIDsM();

    private /* synthetic */ $$Lambda$MessagesController$zxGAFsqAd9PZfsdBDBBTftIDsM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$2HvU-5o5E0s3P1HUISMsHon5vDk  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$2HvU5o5E0s3P1HUISMsHon5vDk implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$2HvU5o5E0s3P1HUISMsHon5vDk INSTANCE = new $$Lambda$MessagesController$2HvU5o5E0s3P1HUISMsHon5vDk();

    private /* synthetic */ $$Lambda$MessagesController$2HvU5o5E0s3P1HUISMsHon5vDk() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

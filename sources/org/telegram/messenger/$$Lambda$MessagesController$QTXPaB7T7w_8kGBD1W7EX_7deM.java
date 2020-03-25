package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$QTXPaB7T7w_8kGBD1W7EX_7de-M  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$QTXPaB7T7w_8kGBD1W7EX_7deM implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$QTXPaB7T7w_8kGBD1W7EX_7deM INSTANCE = new $$Lambda$MessagesController$QTXPaB7T7w_8kGBD1W7EX_7deM();

    private /* synthetic */ $$Lambda$MessagesController$QTXPaB7T7w_8kGBD1W7EX_7deM() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$4dAGN8BiAM3IliNp_60LPeygpS0  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$4dAGN8BiAM3IliNp_60LPeygpS0 implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$4dAGN8BiAM3IliNp_60LPeygpS0 INSTANCE = new $$Lambda$MessagesController$4dAGN8BiAM3IliNp_60LPeygpS0();

    private /* synthetic */ $$Lambda$MessagesController$4dAGN8BiAM3IliNp_60LPeygpS0() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

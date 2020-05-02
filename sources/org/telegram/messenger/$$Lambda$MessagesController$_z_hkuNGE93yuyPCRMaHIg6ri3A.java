package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC$Updates;

/* renamed from: org.telegram.messenger.-$$Lambda$MessagesController$_z_hkuNGE93yuyPCRMaHIg6ri3A  reason: invalid class name */
/* compiled from: lambda */
public final /* synthetic */ class $$Lambda$MessagesController$_z_hkuNGE93yuyPCRMaHIg6ri3A implements Comparator {
    public static final /* synthetic */ $$Lambda$MessagesController$_z_hkuNGE93yuyPCRMaHIg6ri3A INSTANCE = new $$Lambda$MessagesController$_z_hkuNGE93yuyPCRMaHIg6ri3A();

    private /* synthetic */ $$Lambda$MessagesController$_z_hkuNGE93yuyPCRMaHIg6ri3A() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((TLRPC$Updates) obj).pts, ((TLRPC$Updates) obj2).pts);
    }
}

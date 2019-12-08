package org.telegram.messenger;

import java.util.Comparator;
import org.telegram.tgnet.TLRPC.Updates;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$MessagesController$hGUHFGlJkXICR41QitU5GeMfKbg implements Comparator {
    public static final /* synthetic */ -$$Lambda$MessagesController$hGUHFGlJkXICR41QitU5GeMfKbg INSTANCE = new -$$Lambda$MessagesController$hGUHFGlJkXICR41QitU5GeMfKbg();

    private /* synthetic */ -$$Lambda$MessagesController$hGUHFGlJkXICR41QitU5GeMfKbg() {
    }

    public final int compare(Object obj, Object obj2) {
        return AndroidUtilities.compare(((Updates) obj).pts, ((Updates) obj2).pts);
    }
}

package org.telegram.ui.ActionBar;

import android.view.MenuItem;
import java.util.Comparator;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$FloatingToolbar$id3tSGGj4IAqzijkydDavDmoZuo implements Comparator {
    public static final /* synthetic */ -$$Lambda$FloatingToolbar$id3tSGGj4IAqzijkydDavDmoZuo INSTANCE = new -$$Lambda$FloatingToolbar$id3tSGGj4IAqzijkydDavDmoZuo();

    private /* synthetic */ -$$Lambda$FloatingToolbar$id3tSGGj4IAqzijkydDavDmoZuo() {
    }

    public final int compare(Object obj, Object obj2) {
        return (((MenuItem) obj).getOrder() - ((MenuItem) obj2).getOrder());
    }
}

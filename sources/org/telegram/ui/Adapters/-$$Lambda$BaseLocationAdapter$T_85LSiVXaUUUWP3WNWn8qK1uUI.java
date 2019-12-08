package org.telegram.ui.Adapters;

import android.location.Location;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BaseLocationAdapter$T_85LSiVXaUUUWP3WNWn8qK1uUI implements Runnable {
    private final /* synthetic */ BaseLocationAdapter f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ Location f$2;

    public /* synthetic */ -$$Lambda$BaseLocationAdapter$T_85LSiVXaUUUWP3WNWn8qK1uUI(BaseLocationAdapter baseLocationAdapter, String str, Location location) {
        this.f$0 = baseLocationAdapter;
        this.f$1 = str;
        this.f$2 = location;
    }

    public final void run() {
        this.f$0.lambda$searchDelayed$1$BaseLocationAdapter(this.f$1, this.f$2);
    }
}

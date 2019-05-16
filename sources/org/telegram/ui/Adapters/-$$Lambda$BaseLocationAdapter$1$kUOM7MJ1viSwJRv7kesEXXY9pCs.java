package org.telegram.ui.Adapters;

import android.location.Location;
import org.telegram.ui.Adapters.BaseLocationAdapter.AnonymousClass1;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$BaseLocationAdapter$1$kUOM7MJ1viSwJRv7kesEXXY9pCs implements Runnable {
    private final /* synthetic */ AnonymousClass1 f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ Location f$2;

    public /* synthetic */ -$$Lambda$BaseLocationAdapter$1$kUOM7MJ1viSwJRv7kesEXXY9pCs(AnonymousClass1 anonymousClass1, String str, Location location) {
        this.f$0 = anonymousClass1;
        this.f$1 = str;
        this.f$2 = location;
    }

    public final void run() {
        this.f$0.lambda$run$0$BaseLocationAdapter$1(this.f$1, this.f$2);
    }
}

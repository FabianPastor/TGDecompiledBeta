package org.telegram.messenger;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$2$Fz7og9tmQ141WKm_AnGeoOkHPJ8 implements Runnable {
    private final /* synthetic */ int f$0;
    private final /* synthetic */ String f$1;
    private final /* synthetic */ float f$2;

    public /* synthetic */ -$$Lambda$ImageLoader$2$Fz7og9tmQ141WKm_AnGeoOkHPJ8(int i, String str, float f) {
        this.f$0 = i;
        this.f$1 = str;
        this.f$2 = f;
    }

    public final void run() {
        NotificationCenter.getInstance(this.f$0).postNotificationName(NotificationCenter.FileLoadProgressChanged, this.f$1, Float.valueOf(this.f$2));
    }
}

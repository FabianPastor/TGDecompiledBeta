package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.Document;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ImageLoader$bsD7o_FB_o0LApsZQkahjvu_ZzU implements Runnable {
    private final /* synthetic */ ImageLoader f$0;
    private final /* synthetic */ int f$1;
    private final /* synthetic */ Object f$10;
    private final /* synthetic */ Document f$11;
    private final /* synthetic */ boolean f$12;
    private final /* synthetic */ boolean f$13;
    private final /* synthetic */ int f$14;
    private final /* synthetic */ int f$15;
    private final /* synthetic */ String f$16;
    private final /* synthetic */ int f$17;
    private final /* synthetic */ String f$2;
    private final /* synthetic */ String f$3;
    private final /* synthetic */ int f$4;
    private final /* synthetic */ ImageReceiver f$5;
    private final /* synthetic */ String f$6;
    private final /* synthetic */ int f$7;
    private final /* synthetic */ ImageLocation f$8;
    private final /* synthetic */ boolean f$9;

    public /* synthetic */ -$$Lambda$ImageLoader$bsD7o_FB_o0LApsZQkahjvu_ZzU(ImageLoader imageLoader, int i, String str, String str2, int i2, ImageReceiver imageReceiver, String str3, int i3, ImageLocation imageLocation, boolean z, Object obj, Document document, boolean z2, boolean z3, int i4, int i5, String str4, int i6) {
        this.f$0 = imageLoader;
        this.f$1 = i;
        this.f$2 = str;
        this.f$3 = str2;
        this.f$4 = i2;
        this.f$5 = imageReceiver;
        this.f$6 = str3;
        this.f$7 = i3;
        this.f$8 = imageLocation;
        this.f$9 = z;
        this.f$10 = obj;
        this.f$11 = document;
        this.f$12 = z2;
        this.f$13 = z3;
        this.f$14 = i4;
        this.f$15 = i5;
        this.f$16 = str4;
        this.f$17 = i6;
    }

    public final void run() {
        ImageLoader imageLoader = this.f$0;
        ImageLoader imageLoader2 = imageLoader;
        imageLoader2.lambda$createLoadOperationForImageReceiver$5$ImageLoader(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, this.f$16, this.f$17);
    }
}

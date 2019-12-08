package org.telegram.ui;

import java.util.ArrayList;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$wh4XBcIYYsYXASYVxRv0U0OZ3bo implements RequestDelegate {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ ErrorRunnable f$1;
    private final /* synthetic */ boolean f$2;
    private final /* synthetic */ TL_secureRequiredType f$3;
    private final /* synthetic */ TL_secureRequiredType f$4;
    private final /* synthetic */ boolean f$5;
    private final /* synthetic */ ArrayList f$6;
    private final /* synthetic */ Runnable f$7;

    public /* synthetic */ -$$Lambda$PassportActivity$wh4XBcIYYsYXASYVxRv0U0OZ3bo(PassportActivity passportActivity, ErrorRunnable errorRunnable, boolean z, TL_secureRequiredType tL_secureRequiredType, TL_secureRequiredType tL_secureRequiredType2, boolean z2, ArrayList arrayList, Runnable runnable) {
        this.f$0 = passportActivity;
        this.f$1 = errorRunnable;
        this.f$2 = z;
        this.f$3 = tL_secureRequiredType;
        this.f$4 = tL_secureRequiredType2;
        this.f$5 = z2;
        this.f$6 = arrayList;
        this.f$7 = runnable;
    }

    public final void run(TLObject tLObject, TL_error tL_error) {
        this.f$0.lambda$deleteValueInternal$60$PassportActivity(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, tLObject, tL_error);
    }
}

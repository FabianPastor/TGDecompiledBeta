package org.telegram.ui;

import android.view.View;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$5Sry1zhVbDTBEYl5VpIUjvovUNY implements OnClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ ArrayList f$1;
    private final /* synthetic */ TL_secureRequiredType f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$PassportActivity$5Sry1zhVbDTBEYl5VpIUjvovUNY(PassportActivity passportActivity, ArrayList arrayList, TL_secureRequiredType tL_secureRequiredType, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = arrayList;
        this.f$2 = tL_secureRequiredType;
        this.f$3 = z;
    }

    public final void onClick(View view) {
        this.f$0.lambda$addField$65$PassportActivity(this.f$1, this.f$2, this.f$3, view);
    }
}

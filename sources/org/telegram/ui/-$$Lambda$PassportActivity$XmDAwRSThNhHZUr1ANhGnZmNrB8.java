package org.telegram.ui;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC.TL_secureRequiredType;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$PassportActivity$XmDAwRSThNhHZUr1ANhGnZmNrB8 implements OnClickListener {
    private final /* synthetic */ PassportActivity f$0;
    private final /* synthetic */ TL_secureRequiredType f$1;
    private final /* synthetic */ ArrayList f$2;
    private final /* synthetic */ boolean f$3;

    public /* synthetic */ -$$Lambda$PassportActivity$XmDAwRSThNhHZUr1ANhGnZmNrB8(PassportActivity passportActivity, TL_secureRequiredType tL_secureRequiredType, ArrayList arrayList, boolean z) {
        this.f$0 = passportActivity;
        this.f$1 = tL_secureRequiredType;
        this.f$2 = arrayList;
        this.f$3 = z;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        this.f$0.lambda$null$61$PassportActivity(this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}

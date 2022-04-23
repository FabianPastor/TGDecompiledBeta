package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import androidx.core.util.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda32 implements DialogInterface.OnClickListener {
    public final /* synthetic */ boolean f$0;
    public final /* synthetic */ Context f$1;
    public final /* synthetic */ AtomicBoolean f$2;
    public final /* synthetic */ Consumer f$3;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda32(boolean z, Context context, AtomicBoolean atomicBoolean, Consumer consumer) {
        this.f$0 = z;
        this.f$1 = context;
        this.f$2 = atomicBoolean;
        this.f$3 = consumer;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createWebViewPermissionsRequestDialog$2(this.f$0, this.f$1, this.f$2, this.f$3, dialogInterface, i);
    }
}

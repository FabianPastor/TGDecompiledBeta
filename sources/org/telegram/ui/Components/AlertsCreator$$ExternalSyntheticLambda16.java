package org.telegram.ui.Components;

import android.content.DialogInterface;
import androidx.core.util.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda16 implements DialogInterface.OnClickListener {
    public final /* synthetic */ AtomicBoolean f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda16(AtomicBoolean atomicBoolean, Consumer consumer) {
        this.f$0 = atomicBoolean;
        this.f$1 = consumer;
    }

    public final void onClick(DialogInterface dialogInterface, int i) {
        AlertsCreator.lambda$createWebViewPermissionsRequestDialog$3(this.f$0, this.f$1, dialogInterface, i);
    }
}

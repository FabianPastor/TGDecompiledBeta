package org.telegram.ui.Components;

import android.content.DialogInterface;
import androidx.core.util.Consumer;
import java.util.concurrent.atomic.AtomicBoolean;

public final /* synthetic */ class AlertsCreator$$ExternalSyntheticLambda68 implements DialogInterface.OnDismissListener {
    public final /* synthetic */ AtomicBoolean f$0;
    public final /* synthetic */ Consumer f$1;

    public /* synthetic */ AlertsCreator$$ExternalSyntheticLambda68(AtomicBoolean atomicBoolean, Consumer consumer) {
        this.f$0 = atomicBoolean;
        this.f$1 = consumer;
    }

    public final void onDismiss(DialogInterface dialogInterface) {
        AlertsCreator.lambda$createWebViewPermissionsRequestDialog$4(this.f$0, this.f$1, dialogInterface);
    }
}

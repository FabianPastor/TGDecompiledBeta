package org.telegram.ui;

import android.content.Intent;
import org.telegram.messenger.ContactsLoadingObserver;

public final /* synthetic */ class LaunchActivity$$ExternalSyntheticLambda66 implements ContactsLoadingObserver.Callback {
    public final /* synthetic */ LaunchActivity f$0;
    public final /* synthetic */ Intent f$1;

    public /* synthetic */ LaunchActivity$$ExternalSyntheticLambda66(LaunchActivity launchActivity, Intent intent) {
        this.f$0 = launchActivity;
        this.f$1 = intent;
    }

    public final void onResult(boolean z) {
        this.f$0.lambda$handleIntent$12(this.f$1, z);
    }
}

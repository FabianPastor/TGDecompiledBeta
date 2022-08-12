package org.telegram.messenger;

import androidx.core.util.Consumer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public final /* synthetic */ class GoogleLocationProvider$$ExternalSyntheticLambda2 implements OnCompleteListener {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ GoogleLocationProvider$$ExternalSyntheticLambda2(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void onComplete(Task task) {
        GoogleLocationProvider.lambda$checkLocationSettings$1(this.f$0, task);
    }
}

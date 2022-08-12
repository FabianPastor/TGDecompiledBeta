package org.telegram.messenger;

import android.location.Location;
import androidx.core.util.Consumer;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public final /* synthetic */ class GoogleLocationProvider$$ExternalSyntheticLambda1 implements OnCompleteListener {
    public final /* synthetic */ Consumer f$0;

    public /* synthetic */ GoogleLocationProvider$$ExternalSyntheticLambda1(Consumer consumer) {
        this.f$0 = consumer;
    }

    public final void onComplete(Task task) {
        this.f$0.accept((Location) task.getResult());
    }
}

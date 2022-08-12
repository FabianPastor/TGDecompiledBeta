package org.telegram.messenger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.telegram.messenger.PushListenerController;

public final /* synthetic */ class PushListenerController$GooglePushListenerServiceProvider$$ExternalSyntheticLambda0 implements OnCompleteListener {
    public final /* synthetic */ PushListenerController.GooglePushListenerServiceProvider f$0;

    public /* synthetic */ PushListenerController$GooglePushListenerServiceProvider$$ExternalSyntheticLambda0(PushListenerController.GooglePushListenerServiceProvider googlePushListenerServiceProvider) {
        this.f$0 = googlePushListenerServiceProvider;
    }

    public final void onComplete(Task task) {
        this.f$0.lambda$onRequestPushToken$0(task);
    }
}

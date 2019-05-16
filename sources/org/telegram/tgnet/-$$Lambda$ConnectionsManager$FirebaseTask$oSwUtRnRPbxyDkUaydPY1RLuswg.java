package org.telegram.tgnet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$ConnectionsManager$FirebaseTask$oSwUtRnRPbxyDkUaydPY1RLuswg implements OnCompleteListener {
    private final /* synthetic */ FirebaseTask f$0;

    public /* synthetic */ -$$Lambda$ConnectionsManager$FirebaseTask$oSwUtRnRPbxyDkUaydPY1RLuswg(FirebaseTask firebaseTask) {
        this.f$0 = firebaseTask;
    }

    public final void onComplete(Task task) {
        this.f$0.lambda$doInBackground$1$ConnectionsManager$FirebaseTask(task);
    }
}

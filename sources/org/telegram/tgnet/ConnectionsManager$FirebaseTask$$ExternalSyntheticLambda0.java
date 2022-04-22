package org.telegram.tgnet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import org.telegram.tgnet.ConnectionsManager;

public final /* synthetic */ class ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda0 implements OnCompleteListener {
    public final /* synthetic */ ConnectionsManager.FirebaseTask f$0;

    public /* synthetic */ ConnectionsManager$FirebaseTask$$ExternalSyntheticLambda0(ConnectionsManager.FirebaseTask firebaseTask) {
        this.f$0 = firebaseTask;
    }

    public final void onComplete(Task task) {
        this.f$0.lambda$doInBackground$0(task);
    }
}

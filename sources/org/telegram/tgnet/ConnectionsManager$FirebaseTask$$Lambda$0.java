package org.telegram.tgnet;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

final /* synthetic */ class ConnectionsManager$FirebaseTask$$Lambda$0 implements OnCompleteListener {
    private final FirebaseTask arg$1;

    ConnectionsManager$FirebaseTask$$Lambda$0(FirebaseTask firebaseTask) {
        this.arg$1 = firebaseTask;
    }

    public void onComplete(Task task) {
        this.arg$1.lambda$doInBackground$1$ConnectionsManager$FirebaseTask(task);
    }
}

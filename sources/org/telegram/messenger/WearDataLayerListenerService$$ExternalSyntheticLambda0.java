package org.telegram.messenger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public final /* synthetic */ class WearDataLayerListenerService$$ExternalSyntheticLambda0 implements OnCompleteListener {
    public final /* synthetic */ String f$0;
    public final /* synthetic */ byte[] f$1;

    public /* synthetic */ WearDataLayerListenerService$$ExternalSyntheticLambda0(String str, byte[] bArr) {
        this.f$0 = str;
        this.f$1 = bArr;
    }

    public final void onComplete(Task task) {
        WearDataLayerListenerService.lambda$sendMessageToWatch$7(this.f$0, this.f$1, task);
    }
}

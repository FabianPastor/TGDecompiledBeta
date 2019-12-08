package org.telegram.messenger;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$WearDataLayerListenerService$RaGAAXsdKhxe-vpoR0F8ujrQJFI implements OnCompleteListener {
    private final /* synthetic */ String f$0;
    private final /* synthetic */ byte[] f$1;

    public /* synthetic */ -$$Lambda$WearDataLayerListenerService$RaGAAXsdKhxe-vpoR0F8ujrQJFI(String str, byte[] bArr) {
        this.f$0 = str;
        this.f$1 = bArr;
    }

    public final void onComplete(Task task) {
        WearDataLayerListenerService.lambda$sendMessageToWatch$7(this.f$0, this.f$1, task);
    }
}

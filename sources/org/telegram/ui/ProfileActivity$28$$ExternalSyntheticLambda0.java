package org.telegram.ui;

import androidx.collection.LongSparseArray;
import org.telegram.ui.ProfileActivity;

public final /* synthetic */ class ProfileActivity$28$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ProfileActivity.AnonymousClass28 f$0;
    public final /* synthetic */ LongSparseArray f$1;
    public final /* synthetic */ int f$2;

    public /* synthetic */ ProfileActivity$28$$ExternalSyntheticLambda0(ProfileActivity.AnonymousClass28 r1, LongSparseArray longSparseArray, int i) {
        this.f$0 = r1;
        this.f$1 = longSparseArray;
        this.f$2 = i;
    }

    public final void run() {
        this.f$0.lambda$onSend$0(this.f$1, this.f$2);
    }
}

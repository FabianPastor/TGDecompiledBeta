package org.telegram.ui;

import android.content.Context;
import java.util.ArrayList;
import org.telegram.ui.ChatUsersActivity;

public final /* synthetic */ class ChatUsersActivity$9$$ExternalSyntheticLambda0 implements Runnable {
    public final /* synthetic */ ArrayList f$0;
    public final /* synthetic */ int f$1;
    public final /* synthetic */ Context f$2;

    public /* synthetic */ ChatUsersActivity$9$$ExternalSyntheticLambda0(ArrayList arrayList, int i, Context context) {
        this.f$0 = arrayList;
        this.f$1 = i;
        this.f$2 = context;
    }

    public final void run() {
        ChatUsersActivity.AnonymousClass9.lambda$didSelectUsers$0(this.f$0, this.f$1, this.f$2);
    }
}

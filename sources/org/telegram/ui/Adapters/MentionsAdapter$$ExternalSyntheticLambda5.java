package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;

public final /* synthetic */ class MentionsAdapter$$ExternalSyntheticLambda5 implements Runnable {
    public final /* synthetic */ MentionsAdapter f$0;
    public final /* synthetic */ ArrayList f$1;
    public final /* synthetic */ SparseArray f$2;

    public /* synthetic */ MentionsAdapter$$ExternalSyntheticLambda5(MentionsAdapter mentionsAdapter, ArrayList arrayList, SparseArray sparseArray) {
        this.f$0 = mentionsAdapter;
        this.f$1 = arrayList;
        this.f$2 = sparseArray;
    }

    public final void run() {
        this.f$0.lambda$searchUsernameOrHashtag$7(this.f$1, this.f$2);
    }
}

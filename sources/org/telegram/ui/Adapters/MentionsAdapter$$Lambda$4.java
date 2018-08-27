package org.telegram.ui.Adapters;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Comparator;
import org.telegram.tgnet.TLRPC.User;

final /* synthetic */ class MentionsAdapter$$Lambda$4 implements Comparator {
    private final SparseArray arg$1;
    private final ArrayList arg$2;

    MentionsAdapter$$Lambda$4(SparseArray sparseArray, ArrayList arrayList) {
        this.arg$1 = sparseArray;
        this.arg$2 = arrayList;
    }

    public int compare(Object obj, Object obj2) {
        return MentionsAdapter.lambda$searchUsernameOrHashtag$5$MentionsAdapter(this.arg$1, this.arg$2, (User) obj, (User) obj2);
    }
}

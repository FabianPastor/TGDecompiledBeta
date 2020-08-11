package j$.util;

import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;

class M extends i0 {
    final /* synthetic */ SortedSet f;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    M(SortedSet this$0, Collection collection, int characteristics) {
        super(collection, characteristics);
        this.f = this$0;
    }

    public Comparator getComparator() {
        return this.f.comparator();
    }
}

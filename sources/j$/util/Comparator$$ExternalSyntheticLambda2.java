package j$.util;

import j$.util.function.Function;
import java.io.Serializable;
import java.util.Comparator;

public final /* synthetic */ class Comparator$$ExternalSyntheticLambda2 implements Comparator, Serializable {
    public final /* synthetic */ Function f$0;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda2(Function function) {
        this.f$0 = function;
    }

    public final int compare(Object obj, Object obj2) {
        return ((Comparable) this.f$0.apply(obj)).compareTo(this.f$0.apply(obj2));
    }
}

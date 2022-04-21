package j$.util;

import j$.util.function.ToDoubleFunction;
import java.io.Serializable;
import java.util.Comparator;

public final /* synthetic */ class Comparator$$ExternalSyntheticLambda3 implements Comparator, Serializable {
    public final /* synthetic */ ToDoubleFunction f$0;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda3(ToDoubleFunction toDoubleFunction) {
        this.f$0 = toDoubleFunction;
    }

    public final int compare(Object obj, Object obj2) {
        return Double.compare(this.f$0.applyAsDouble(obj), this.f$0.applyAsDouble(obj2));
    }
}

package j$.util;

import j$.time.LocalTime$$ExternalSyntheticBackport0;
import j$.util.function.ToIntFunction;
import java.io.Serializable;
import java.util.Comparator;

public final /* synthetic */ class Comparator$$ExternalSyntheticLambda4 implements Comparator, Serializable {
    public final /* synthetic */ ToIntFunction f$0;

    public /* synthetic */ Comparator$$ExternalSyntheticLambda4(ToIntFunction toIntFunction) {
        this.f$0 = toIntFunction;
    }

    public final int compare(Object obj, Object obj2) {
        return LocalTime$$ExternalSyntheticBackport0.m(this.f$0.applyAsInt(obj), this.f$0.applyAsInt(obj2));
    }
}

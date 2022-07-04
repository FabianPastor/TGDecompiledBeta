package j$.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public final /* synthetic */ class Map$Entry$$ExternalSyntheticLambda1 implements Comparator, Serializable {
    public final /* synthetic */ Comparator f$0;

    public /* synthetic */ Map$Entry$$ExternalSyntheticLambda1(Comparator comparator) {
        this.f$0 = comparator;
    }

    public final int compare(Object obj, Object obj2) {
        return this.f$0.compare(((Map.Entry) obj).getValue(), ((Map.Entry) obj2).getValue());
    }
}

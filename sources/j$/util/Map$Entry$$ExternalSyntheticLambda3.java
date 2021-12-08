package j$.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public final /* synthetic */ class Map$Entry$$ExternalSyntheticLambda3 implements Comparator, Serializable {
    public static final /* synthetic */ Map$Entry$$ExternalSyntheticLambda3 INSTANCE = new Map$Entry$$ExternalSyntheticLambda3();

    private /* synthetic */ Map$Entry$$ExternalSyntheticLambda3() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((Comparable) ((Map.Entry) obj).getValue()).compareTo(((Map.Entry) obj2).getValue());
    }
}

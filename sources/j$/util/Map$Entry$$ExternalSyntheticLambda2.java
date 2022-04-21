package j$.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public final /* synthetic */ class Map$Entry$$ExternalSyntheticLambda2 implements Comparator, Serializable {
    public static final /* synthetic */ Map$Entry$$ExternalSyntheticLambda2 INSTANCE = new Map$Entry$$ExternalSyntheticLambda2();

    private /* synthetic */ Map$Entry$$ExternalSyntheticLambda2() {
    }

    public final int compare(Object obj, Object obj2) {
        return ((Comparable) ((Map.Entry) obj).getKey()).compareTo(((Map.Entry) obj2).getKey());
    }
}

package j$.time.format;

import java.util.Comparator;
import java.util.Map;
/* loaded from: classes2.dex */
class d implements Comparator {
    public d(int i) {
    }

    @Override // java.util.Comparator
    public int compare(Object obj, Object obj2) {
        return ((String) ((Map.Entry) obj2).getKey()).length() - ((String) ((Map.Entry) obj).getKey()).length();
    }
}

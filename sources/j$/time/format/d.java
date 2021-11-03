package j$.time.format;

import java.util.Comparator;
import java.util.Map;

class d implements Comparator {
    public d(int i) {
    }

    public int compare(Object obj, Object obj2) {
        return ((String) ((Map.Entry) obj2).getKey()).length() - ((String) ((Map.Entry) obj).getKey()).length();
    }
}

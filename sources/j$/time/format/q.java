package j$.time.format;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class q {
    q(Map map) {
        HashMap hashMap = new HashMap();
        ArrayList arrayList = new ArrayList();
        for (Map.Entry entry : map.entrySet()) {
            HashMap hashMap2 = new HashMap();
            for (Map.Entry entry2 : ((Map) entry.getValue()).entrySet()) {
                int i = c.b;
                hashMap2.put((String) entry2.getValue(), new AbstractMap.SimpleImmutableEntry((String) entry2.getValue(), (Long) entry2.getKey()));
            }
            ArrayList arrayList2 = new ArrayList(hashMap2.values());
            Collections.sort(arrayList2, c.a);
            hashMap.put((t) entry.getKey(), arrayList2);
            arrayList.addAll(arrayList2);
            hashMap.put((Object) null, arrayList);
        }
        Collections.sort(arrayList, c.a);
    }
}

package j$.time.v;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TimeZone;

final class f extends g {
    private final Set c;

    f() {
        LinkedHashSet<String> availableIds = new LinkedHashSet<>();
        for (String id : TimeZone.getAvailableIDs()) {
            availableIds.add(id);
        }
        this.c = Collections.unmodifiableSet(availableIds);
    }

    /* access modifiers changed from: protected */
    public Set e() {
        return this.c;
    }

    /* access modifiers changed from: protected */
    public c d(String zoneId, boolean forCaching) {
        if (this.c.contains(zoneId)) {
            return new c(TimeZone.getTimeZone(zoneId));
        }
        throw new d("Not a built-in time zone: " + zoneId);
    }
}

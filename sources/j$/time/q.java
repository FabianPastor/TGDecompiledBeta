package j$.time;

import j$.CLASSNAMEp;
import j$.time.v.c;
import j$.time.v.d;
import j$.time.v.g;
import java.io.Serializable;

final class q extends o implements Serializable {
    private final String b;
    private final transient c c;

    static q T(String zoneId, boolean checkAvailable) {
        CLASSNAMEp.a(zoneId, "zoneId");
        S(zoneId);
        c rules = null;
        try {
            rules = g.c(zoneId, true);
        } catch (d ex) {
            if (checkAvailable) {
                throw ex;
            }
        }
        return new q(zoneId, rules);
    }

    private static void S(String zoneId) {
        int n = zoneId.length();
        if (n >= 2) {
            for (int i = 0; i < n; i++) {
                char c2 = zoneId.charAt(i);
                if ((c2 < 'a' || c2 > 'z') && ((c2 < 'A' || c2 > 'Z') && ((c2 != '/' || i == 0) && ((c2 < '0' || c2 > '9' || i == 0) && ((c2 != '~' || i == 0) && ((c2 != '.' || i == 0) && ((c2 != '_' || i == 0) && ((c2 != '+' || i == 0) && (c2 != '-' || i == 0))))))))) {
                    throw new f("Invalid ID for region-based ZoneId, invalid format: " + zoneId);
                }
            }
            return;
        }
        throw new f("Invalid ID for region-based ZoneId, invalid format: " + zoneId);
    }

    q(String id, c rules) {
        this.b = id;
        this.c = rules;
    }

    public String getId() {
        return this.b;
    }

    public c A() {
        c cVar = this.c;
        return cVar != null ? cVar : g.c(this.b, false);
    }
}

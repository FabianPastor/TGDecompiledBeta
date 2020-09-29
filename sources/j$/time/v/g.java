package j$.time.v;

import j$.CLASSNAMEp;
import j$.util.concurrent.ConcurrentHashMap;
import java.security.AccessController;
import java.time.zone.ZoneRulesProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class g {
    private static final CopyOnWriteArrayList a = new CopyOnWriteArrayList();
    private static final ConcurrentMap b = new ConcurrentHashMap(512, 0.75f, 2);

    /* access modifiers changed from: protected */
    public abstract c d(String str, boolean z);

    /* access modifiers changed from: protected */
    public abstract Set e();

    static {
        List<ZoneRulesProvider> loaded = new ArrayList<>();
        AccessController.doPrivileged(new e(loaded));
        a.addAll(loaded);
    }

    public static Set a() {
        return new HashSet(b.keySet());
    }

    public static c c(String zoneId, boolean forCaching) {
        CLASSNAMEp.a(zoneId, "zoneId");
        return b(zoneId).d(zoneId, forCaching);
    }

    private static g b(String zoneId) {
        g provider = (g) b.get(zoneId);
        if (provider != null) {
            return provider;
        }
        if (b.isEmpty()) {
            throw new d("No time-zone data files registered");
        }
        throw new d("Unknown time-zone ID: " + zoneId);
    }

    public static void f(g provider) {
        CLASSNAMEp.a(provider, "provider");
        g(provider);
        a.add(provider);
    }

    private static void g(g provider) {
        for (String zoneId : provider.e()) {
            CLASSNAMEp.a(zoneId, "zoneId");
            if (((g) b.putIfAbsent(zoneId, provider)) != null) {
                throw new d("Unable to register zone as one already registered with that ID: " + zoneId + ", currently loading from provider: " + provider);
            }
        }
    }

    protected g() {
    }
}

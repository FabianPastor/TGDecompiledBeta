package j$.time.zone;

import j$.util.Objects;
import j$.util.concurrent.ConcurrentHashMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.time.zone.ZoneRules;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class ZoneRulesProvider {
    private static final CopyOnWriteArrayList<ZoneRulesProvider> PROVIDERS;
    private static final ConcurrentMap<String, ZoneRulesProvider> ZONES = new ConcurrentHashMap(512, 0.75f, 2);

    /* access modifiers changed from: protected */
    public abstract ZoneRules provideRules(String str, boolean z);

    /* access modifiers changed from: protected */
    public abstract NavigableMap<String, ZoneRules> provideVersions(String str);

    /* access modifiers changed from: protected */
    public abstract Set<String> provideZoneIds();

    static {
        CopyOnWriteArrayList<ZoneRulesProvider> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        PROVIDERS = copyOnWriteArrayList;
        final List<java.time.zone.ZoneRulesProvider> loaded = new ArrayList<>();
        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            public Object run() {
                String prop = System.getProperty("java.time.zone.DefaultZoneRulesProvider");
                if (prop != null) {
                    try {
                        ZoneRulesProvider provider = ZoneRulesProvider.class.cast(Class.forName(prop, true, ZoneRulesProvider.class.getClassLoader()).newInstance());
                        ZoneRulesProvider.registerProvider(provider);
                        loaded.add(provider);
                        return null;
                    } catch (Exception x) {
                        throw new Error(x);
                    }
                } else {
                    ZoneRulesProvider.registerProvider(new TimeZoneRulesProvider());
                    return null;
                }
            }
        });
        copyOnWriteArrayList.addAll(loaded);
    }

    public static Set<String> getAvailableZoneIds() {
        return new HashSet(ZONES.keySet());
    }

    public static ZoneRules getRules(String zoneId, boolean forCaching) {
        Objects.requireNonNull(zoneId, "zoneId");
        return getProvider(zoneId).provideRules(zoneId, forCaching);
    }

    public static NavigableMap<String, ZoneRules> getVersions(String zoneId) {
        Objects.requireNonNull(zoneId, "zoneId");
        return getProvider(zoneId).provideVersions(zoneId);
    }

    private static ZoneRulesProvider getProvider(String zoneId) {
        ConcurrentMap<String, ZoneRulesProvider> concurrentMap = ZONES;
        ZoneRulesProvider provider = (ZoneRulesProvider) concurrentMap.get(zoneId);
        if (provider != null) {
            return provider;
        }
        if (concurrentMap.isEmpty()) {
            throw new ZoneRulesException("No time-zone data files registered");
        }
        throw new ZoneRulesException("Unknown time-zone ID: " + zoneId);
    }

    public static void registerProvider(ZoneRulesProvider provider) {
        Objects.requireNonNull(provider, "provider");
        registerProvider0(provider);
        PROVIDERS.add(provider);
    }

    private static void registerProvider0(ZoneRulesProvider provider) {
        for (String zoneId : provider.provideZoneIds()) {
            Objects.requireNonNull(zoneId, "zoneId");
            if (ZONES.putIfAbsent(zoneId, provider) != null) {
                throw new ZoneRulesException("Unable to register zone as one already registered with that ID: " + zoneId + ", currently loading from provider: " + provider);
            }
        }
    }

    public static boolean refresh() {
        boolean changed = false;
        Iterator<ZoneRulesProvider> it = PROVIDERS.iterator();
        while (it.hasNext()) {
            changed |= it.next().provideRefresh();
        }
        return changed;
    }

    protected ZoneRulesProvider() {
    }

    /* access modifiers changed from: protected */
    public boolean provideRefresh() {
        return false;
    }

    private static final class TimeZoneRulesProvider extends ZoneRulesProvider {
        private final Set<String> zoneIds;

        TimeZoneRulesProvider() {
            LinkedHashSet<String> availableIds = new LinkedHashSet<>();
            for (String id : TimeZone.getAvailableIDs()) {
                availableIds.add(id);
            }
            this.zoneIds = Collections.unmodifiableSet(availableIds);
        }

        /* access modifiers changed from: protected */
        public Set<String> provideZoneIds() {
            return this.zoneIds;
        }

        /* access modifiers changed from: protected */
        public ZoneRules provideRules(String zoneId, boolean forCaching) {
            if (this.zoneIds.contains(zoneId)) {
                return new ZoneRules(TimeZone.getTimeZone(zoneId));
            }
            throw new ZoneRulesException("Not a built-in time zone: " + zoneId);
        }

        /* access modifiers changed from: protected */
        public NavigableMap<String, ZoneRules> provideVersions(String zoneId) {
            ZoneRules rules = provideRules(zoneId, false);
            TreeMap<String, ZoneRules> versionMap = new TreeMap<>();
            versionMap.put("builtin", rules);
            return versionMap;
        }
    }
}

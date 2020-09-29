package j$.time.v;

import java.security.PrivilegedAction;
import java.util.List;

class e implements PrivilegedAction {
    final /* synthetic */ List a;

    e(List list) {
        this.a = list;
    }

    public Object run() {
        Class<g> cls = g.class;
        String prop = System.getProperty("java.time.zone.DefaultZoneRulesProvider");
        if (prop != null) {
            try {
                g provider = cls.cast(Class.forName(prop, true, cls.getClassLoader()).newInstance());
                g.f(provider);
                this.a.add(provider);
                return null;
            } catch (Exception x) {
                throw new Error(x);
            }
        } else {
            g.f(new f());
            return null;
        }
    }
}

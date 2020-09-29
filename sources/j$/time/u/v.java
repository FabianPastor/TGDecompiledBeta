package j$.time.u;

import j$.CLASSNAMEp;
import j$.time.f;

public final /* synthetic */ class v {
    public static G c(w _this, B field) {
        if (!(field instanceof j)) {
            CLASSNAMEp.a(field, "field");
            return field.M(_this);
        } else if (_this.h(field)) {
            return field.p();
        } else {
            throw new F("Unsupported field: " + field);
        }
    }

    public static int a(w _this, B field) {
        G range = _this.p(field);
        if (range.g()) {
            long value = _this.f(field);
            if (range.i(value)) {
                return (int) value;
            }
            throw new f("Invalid value for " + field + " (valid values " + range + "): " + value);
        }
        throw new F("Invalid field " + field + " for get() method, use getLong() instead");
    }

    public static Object b(w _this, D d) {
        if (d == C.n() || d == C.a() || d == C.l()) {
            return null;
        }
        return d.a(_this);
    }
}

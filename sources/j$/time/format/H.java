package j$.time.format;

import j$.CLASSNAMEe;
import j$.CLASSNAMEf;
import j$.CLASSNAMEi;
import j$.CLASSNAMEk;
import j$.CLASSNAMEp;
import j$.time.LocalDate;
import j$.time.LocalTime;
import j$.time.m;
import j$.time.o;
import j$.time.p;
import j$.time.t.f;
import j$.time.t.i;
import j$.time.t.q;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.F;
import j$.time.u.G;
import j$.time.u.j;
import j$.time.u.v;
import j$.time.u.w;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

final class H implements w {
    final Map a = new HashMap();
    o b;
    q c;
    boolean d;
    private I e;
    private f f;
    private LocalTime g;
    m h = m.d;

    public /* synthetic */ int i(B b2) {
        return v.a(this, b2);
    }

    public /* synthetic */ G p(B b2) {
        return v.c(this, b2);
    }

    H() {
    }

    /* access modifiers changed from: package-private */
    public H j() {
        H cloned = new H();
        cloned.a.putAll(this.a);
        cloned.b = this.b;
        cloned.c = this.c;
        cloned.d = this.d;
        return cloned;
    }

    public boolean h(B field) {
        f fVar;
        LocalTime localTime;
        if (this.a.containsKey(field) || (((fVar = this.f) != null && fVar.h(field)) || ((localTime = this.g) != null && localTime.h(field)))) {
            return true;
        }
        if (field == null || (field instanceof j) || !field.K(this)) {
            return false;
        }
        return true;
    }

    public long f(B field) {
        CLASSNAMEp.a(field, "field");
        Long value = (Long) this.a.get(field);
        if (value != null) {
            return value.longValue();
        }
        f fVar = this.f;
        if (fVar != null && fVar.h(field)) {
            return this.f.f(field);
        }
        LocalTime localTime = this.g;
        if (localTime != null && localTime.h(field)) {
            return this.g.f(field);
        }
        if (!(field instanceof j)) {
            return field.A(this);
        }
        throw new F("Unsupported field: " + field);
    }

    public Object r(D d2) {
        if (d2 == C.n()) {
            return this.b;
        }
        if (d2 == C.a()) {
            return this.c;
        }
        if (d2 == C.i()) {
            f fVar = this.f;
            if (fVar != null) {
                return LocalDate.M(fVar);
            }
            return null;
        } else if (d2 == C.j()) {
            return this.g;
        } else {
            if (d2 == C.m() || d2 == C.k()) {
                return d2.a(this);
            }
            if (d2 == C.l()) {
                return null;
            }
            return d2.a(this);
        }
    }

    /* access modifiers changed from: package-private */
    public w o(I resolverStyle, Set resolverFields) {
        if (resolverFields != null) {
            this.a.keySet().retainAll(resolverFields);
        }
        this.e = resolverStyle;
        v();
        K();
        k();
        F();
        x();
        A();
        return this;
    }

    private void v() {
        C();
        t();
        H();
        if (this.a.size() > 0) {
            int changedCount = 0;
            loop0:
            while (changedCount < 50) {
                for (Map.Entry<TemporalField, Long> entry : this.a.entrySet()) {
                    B targetField = (B) entry.getKey();
                    w resolvedObject = targetField.x(this.a, this, this.e);
                    if (resolvedObject != null) {
                        boolean z = resolvedObject instanceof j$.time.t.m;
                        w resolvedObject2 = resolvedObject;
                        if (z) {
                            j$.time.t.m mVar = (j$.time.t.m) resolvedObject;
                            o oVar = this.b;
                            if (oVar == null) {
                                this.b = mVar.s();
                            } else if (!oVar.equals(mVar.s())) {
                                throw new j$.time.f("ChronoZonedDateTime must use the effective parsed zone: " + this.b);
                            }
                            resolvedObject2 = mVar.B();
                        }
                        if (resolvedObject2 instanceof i) {
                            i iVar = (i) resolvedObject2;
                            L(iVar.d(), m.d);
                            M(iVar.e());
                            changedCount++;
                        } else if (resolvedObject2 instanceof f) {
                            M((f) resolvedObject2);
                            changedCount++;
                        } else if (resolvedObject2 instanceof LocalTime) {
                            L((LocalTime) resolvedObject2, m.d);
                            changedCount++;
                        } else {
                            throw new j$.time.f("Method resolve() can only return ChronoZonedDateTime, ChronoLocalDateTime, ChronoLocalDate or LocalTime");
                        }
                    } else if (!this.a.containsKey(targetField)) {
                        changedCount++;
                    }
                }
            }
            if (changedCount == 50) {
                throw new j$.time.f("One of the parsed fields has an incorrectly implemented resolve method");
            } else if (changedCount > 0) {
                C();
                t();
                H();
            }
        }
    }

    private void N(B targetField, B changeField, Long changeValue) {
        Long old = (Long) this.a.put(changeField, changeValue);
        if (old != null && old.longValue() != changeValue.longValue()) {
            throw new j$.time.f("Conflict found: " + changeField + " " + old + " differs from " + changeField + " " + changeValue + " while resolving  " + targetField);
        }
    }

    private void C() {
        if (this.a.containsKey(j.INSTANT_SECONDS)) {
            o oVar = this.b;
            if (oVar != null) {
                E(oVar);
                return;
            }
            Long offsetSecs = (Long) this.a.get(j.OFFSET_SECONDS);
            if (offsetSecs != null) {
                E(p.X(offsetSecs.intValue()));
            }
        }
    }

    private void E(o selectedZone) {
        j$.time.t.m H = this.c.H(j$.time.i.R(((Long) this.a.remove(j.INSTANT_SECONDS)).longValue()), selectedZone);
        M(H.e());
        N(j.INSTANT_SECONDS, j.SECOND_OF_DAY, Long.valueOf((long) H.d().Z()));
    }

    private void t() {
        M(this.c.E(this.a, this.e));
    }

    private void M(f cld) {
        f fVar = this.f;
        if (fVar != null) {
            if (cld != null && !fVar.equals(cld)) {
                throw new j$.time.f("Conflict found: Fields resolved to two different dates: " + this.f + " " + cld);
            }
        } else if (cld == null) {
        } else {
            if (this.c.equals(cld.b())) {
                this.f = cld;
                return;
            }
            throw new j$.time.f("ChronoLocalDate must use the effective parsed chronology: " + this.c);
        }
    }

    private void H() {
        long j = 0;
        if (this.a.containsKey(j.CLOCK_HOUR_OF_DAY)) {
            long ch = ((Long) this.a.remove(j.CLOCK_HOUR_OF_DAY)).longValue();
            I i = this.e;
            if (i == I.STRICT || (i == I.SMART && ch != 0)) {
                j.CLOCK_HOUR_OF_DAY.P(ch);
            }
            N(j.CLOCK_HOUR_OF_DAY, j.HOUR_OF_DAY, Long.valueOf(ch == 24 ? 0 : ch));
        }
        if (this.a.containsKey(j.CLOCK_HOUR_OF_AMPM)) {
            long ch2 = ((Long) this.a.remove(j.CLOCK_HOUR_OF_AMPM)).longValue();
            I i2 = this.e;
            if (i2 == I.STRICT || (i2 == I.SMART && ch2 != 0)) {
                j.CLOCK_HOUR_OF_AMPM.P(ch2);
            }
            j jVar = j.CLOCK_HOUR_OF_AMPM;
            j jVar2 = j.HOUR_OF_AMPM;
            if (ch2 != 12) {
                j = ch2;
            }
            N(jVar, jVar2, Long.valueOf(j));
        }
        if (this.a.containsKey(j.AMPM_OF_DAY) && this.a.containsKey(j.HOUR_OF_AMPM)) {
            long ap = ((Long) this.a.remove(j.AMPM_OF_DAY)).longValue();
            long hap = ((Long) this.a.remove(j.HOUR_OF_AMPM)).longValue();
            if (this.e == I.LENIENT) {
                N(j.AMPM_OF_DAY, j.HOUR_OF_DAY, Long.valueOf(CLASSNAMEe.a(CLASSNAMEk.a(ap, (long) 12), hap)));
            } else {
                j.AMPM_OF_DAY.P(ap);
                j.HOUR_OF_AMPM.P(ap);
                N(j.AMPM_OF_DAY, j.HOUR_OF_DAY, Long.valueOf((12 * ap) + hap));
            }
        }
        if (this.a.containsKey(j.NANO_OF_DAY)) {
            long nod = ((Long) this.a.remove(j.NANO_OF_DAY)).longValue();
            if (this.e != I.LENIENT) {
                j.NANO_OF_DAY.P(nod);
            }
            N(j.NANO_OF_DAY, j.HOUR_OF_DAY, Long.valueOf(nod / 3600000000000L));
            N(j.NANO_OF_DAY, j.MINUTE_OF_HOUR, Long.valueOf((nod / 60000000000L) % 60));
            N(j.NANO_OF_DAY, j.SECOND_OF_MINUTE, Long.valueOf((nod / NUM) % 60));
            N(j.NANO_OF_DAY, j.NANO_OF_SECOND, Long.valueOf(nod % NUM));
        }
        if (this.a.containsKey(j.MICRO_OF_DAY)) {
            long cod = ((Long) this.a.remove(j.MICRO_OF_DAY)).longValue();
            if (this.e != I.LENIENT) {
                j.MICRO_OF_DAY.P(cod);
            }
            N(j.MICRO_OF_DAY, j.SECOND_OF_DAY, Long.valueOf(cod / 1000000));
            N(j.MICRO_OF_DAY, j.MICRO_OF_SECOND, Long.valueOf(cod % 1000000));
        }
        if (this.a.containsKey(j.MILLI_OF_DAY)) {
            long lod = ((Long) this.a.remove(j.MILLI_OF_DAY)).longValue();
            if (this.e != I.LENIENT) {
                j.MILLI_OF_DAY.P(lod);
            }
            N(j.MILLI_OF_DAY, j.SECOND_OF_DAY, Long.valueOf(lod / 1000));
            N(j.MILLI_OF_DAY, j.MILLI_OF_SECOND, Long.valueOf(lod % 1000));
        }
        if (this.a.containsKey(j.SECOND_OF_DAY)) {
            long sod = ((Long) this.a.remove(j.SECOND_OF_DAY)).longValue();
            if (this.e != I.LENIENT) {
                j.SECOND_OF_DAY.P(sod);
            }
            N(j.SECOND_OF_DAY, j.HOUR_OF_DAY, Long.valueOf(sod / 3600));
            N(j.SECOND_OF_DAY, j.MINUTE_OF_HOUR, Long.valueOf((sod / 60) % 60));
            N(j.SECOND_OF_DAY, j.SECOND_OF_MINUTE, Long.valueOf(sod % 60));
        }
        if (this.a.containsKey(j.MINUTE_OF_DAY)) {
            long mod = ((Long) this.a.remove(j.MINUTE_OF_DAY)).longValue();
            if (this.e != I.LENIENT) {
                j.MINUTE_OF_DAY.P(mod);
            }
            N(j.MINUTE_OF_DAY, j.HOUR_OF_DAY, Long.valueOf(mod / 60));
            N(j.MINUTE_OF_DAY, j.MINUTE_OF_HOUR, Long.valueOf(mod % 60));
        }
        if (this.a.containsKey(j.NANO_OF_SECOND)) {
            long nos = ((Long) this.a.get(j.NANO_OF_SECOND)).longValue();
            if (this.e != I.LENIENT) {
                j.NANO_OF_SECOND.P(nos);
            }
            if (this.a.containsKey(j.MICRO_OF_SECOND)) {
                long cos = ((Long) this.a.remove(j.MICRO_OF_SECOND)).longValue();
                if (this.e != I.LENIENT) {
                    j.MICRO_OF_SECOND.P(cos);
                }
                nos = (cos * 1000) + (nos % 1000);
                N(j.MICRO_OF_SECOND, j.NANO_OF_SECOND, Long.valueOf(nos));
            }
            if (this.a.containsKey(j.MILLI_OF_SECOND)) {
                long los = ((Long) this.a.remove(j.MILLI_OF_SECOND)).longValue();
                if (this.e != I.LENIENT) {
                    j.MILLI_OF_SECOND.P(los);
                }
                N(j.MILLI_OF_SECOND, j.NANO_OF_SECOND, Long.valueOf((los * 1000000) + (nos % 1000000)));
            }
        }
        if (this.a.containsKey(j.HOUR_OF_DAY) && this.a.containsKey(j.MINUTE_OF_HOUR) && this.a.containsKey(j.SECOND_OF_MINUTE) && this.a.containsKey(j.NANO_OF_SECOND)) {
            G(((Long) this.a.remove(j.HOUR_OF_DAY)).longValue(), ((Long) this.a.remove(j.MINUTE_OF_HOUR)).longValue(), ((Long) this.a.remove(j.SECOND_OF_MINUTE)).longValue(), ((Long) this.a.remove(j.NANO_OF_SECOND)).longValue());
        }
    }

    private void K() {
        if (this.g == null) {
            if (this.a.containsKey(j.MILLI_OF_SECOND)) {
                long los = ((Long) this.a.remove(j.MILLI_OF_SECOND)).longValue();
                if (this.a.containsKey(j.MICRO_OF_SECOND)) {
                    long cos = (los * 1000) + (((Long) this.a.get(j.MICRO_OF_SECOND)).longValue() % 1000);
                    N(j.MILLI_OF_SECOND, j.MICRO_OF_SECOND, Long.valueOf(cos));
                    this.a.remove(j.MICRO_OF_SECOND);
                    this.a.put(j.NANO_OF_SECOND, Long.valueOf(1000 * cos));
                } else {
                    this.a.put(j.NANO_OF_SECOND, Long.valueOf(1000000 * los));
                }
            } else if (this.a.containsKey(j.MICRO_OF_SECOND)) {
                this.a.put(j.NANO_OF_SECOND, Long.valueOf(1000 * ((Long) this.a.remove(j.MICRO_OF_SECOND)).longValue()));
            }
            Long hod = (Long) this.a.get(j.HOUR_OF_DAY);
            if (hod != null) {
                Long moh = (Long) this.a.get(j.MINUTE_OF_HOUR);
                Long som = (Long) this.a.get(j.SECOND_OF_MINUTE);
                Long nos = (Long) this.a.get(j.NANO_OF_SECOND);
                if (moh == null && (som != null || nos != null)) {
                    return;
                }
                if (moh == null || som != null || nos == null) {
                    long nosVal = 0;
                    long mohVal = moh != null ? moh.longValue() : 0;
                    long somVal = som != null ? som.longValue() : 0;
                    if (nos != null) {
                        nosVal = nos.longValue();
                    }
                    G(hod.longValue(), mohVal, somVal, nosVal);
                    this.a.remove(j.HOUR_OF_DAY);
                    this.a.remove(j.MINUTE_OF_HOUR);
                    this.a.remove(j.SECOND_OF_MINUTE);
                    this.a.remove(j.NANO_OF_SECOND);
                } else {
                    return;
                }
            }
        }
        if (this.e != I.LENIENT && this.a.size() > 0) {
            for (Map.Entry<TemporalField, Long> entry : this.a.entrySet()) {
                B field = (B) entry.getKey();
                if ((field instanceof j) && field.r()) {
                    ((j) field).P(entry.getValue().longValue());
                }
            }
        }
    }

    private void G(long hod, long moh, long som, long nos) {
        if (this.e == I.LENIENT) {
            long totalNanos = CLASSNAMEe.a(CLASSNAMEe.a(CLASSNAMEe.a(CLASSNAMEk.a(hod, 3600000000000L), CLASSNAMEk.a(moh, 60000000000L)), CLASSNAMEk.a(som, NUM)), nos);
            L(LocalTime.S(CLASSNAMEi.a(totalNanos, 86400000000000L)), m.d((int) CLASSNAMEf.a(totalNanos, 86400000000000L)));
            return;
        }
        int mohVal = j.MINUTE_OF_HOUR.O(moh);
        int nosVal = j.NANO_OF_SECOND.O(nos);
        if (this.e == I.SMART && hod == 24 && mohVal == 0 && som == 0 && nosVal == 0) {
            L(LocalTime.g, m.d(1));
        } else {
            L(LocalTime.R(j.HOUR_OF_DAY.O(hod), mohVal, j.SECOND_OF_MINUTE.O(som), nosVal), m.d);
        }
    }

    private void F() {
        if (this.f != null && this.g != null && !this.h.c()) {
            this.f = this.f.D(this.h);
            this.h = m.d;
        }
    }

    private void x() {
        if (this.g != null) {
            return;
        }
        if (!this.a.containsKey(j.INSTANT_SECONDS) && !this.a.containsKey(j.SECOND_OF_DAY) && !this.a.containsKey(j.SECOND_OF_MINUTE)) {
            return;
        }
        if (this.a.containsKey(j.NANO_OF_SECOND)) {
            long nos = ((Long) this.a.get(j.NANO_OF_SECOND)).longValue();
            this.a.put(j.MICRO_OF_SECOND, Long.valueOf(nos / 1000));
            this.a.put(j.MILLI_OF_SECOND, Long.valueOf(nos / 1000000));
            return;
        }
        this.a.put(j.NANO_OF_SECOND, 0L);
        this.a.put(j.MICRO_OF_SECOND, 0L);
        this.a.put(j.MILLI_OF_SECOND, 0L);
    }

    private void A() {
        LocalTime localTime;
        f fVar = this.f;
        if (fVar != null && (localTime = this.g) != null) {
            if (this.b != null) {
                this.a.put(j.INSTANT_SECONDS, Long.valueOf(fVar.u(localTime).n(this.b).f(j.INSTANT_SECONDS)));
                return;
            }
            Long offsetSecs = (Long) this.a.get(j.OFFSET_SECONDS);
            if (offsetSecs != null) {
                this.a.put(j.INSTANT_SECONDS, Long.valueOf(this.f.u(this.g).n(p.X(offsetSecs.intValue())).f(j.INSTANT_SECONDS)));
            }
        }
    }

    private void L(LocalTime timeToSet, m periodToSet) {
        LocalTime localTime = this.g;
        if (localTime == null) {
            this.g = timeToSet;
            this.h = periodToSet;
        } else if (!localTime.equals(timeToSet)) {
            throw new j$.time.f("Conflict found: Fields resolved to different times: " + this.g + " " + timeToSet);
        } else if (this.h.c() || periodToSet.c() || this.h.equals(periodToSet)) {
            this.h = periodToSet;
        } else {
            throw new j$.time.f("Conflict found: Fields resolved to different excess periods: " + this.h + " " + periodToSet);
        }
    }

    private void k() {
        f fVar = this.f;
        if (fVar != null) {
            m(fVar);
        }
        LocalTime localTime = this.g;
        if (localTime != null) {
            m(localTime);
            if (this.f != null && this.a.size() > 0) {
                m(this.f.u(this.g));
            }
        }
    }

    private void m(w target) {
        Iterator<Map.Entry<TemporalField, Long>> it = this.a.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<TemporalField, Long> entry = it.next();
            B field = (B) entry.getKey();
            if (target.h(field)) {
                try {
                    long val1 = target.f(field);
                    long val2 = entry.getValue().longValue();
                    if (val1 == val2) {
                        it.remove();
                    } else {
                        throw new j$.time.f("Conflict found: Field " + field + " " + val1 + " differs from " + field + " " + val2 + " derived from " + target);
                    }
                } catch (RuntimeException e2) {
                }
            }
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(64);
        buf.append(this.a);
        buf.append(',');
        buf.append(this.c);
        if (this.b != null) {
            buf.append(',');
            buf.append(this.b);
        }
        if (!(this.f == null && this.g == null)) {
            buf.append(" resolved to ");
            f fVar = this.f;
            if (fVar != null) {
                buf.append(fVar);
                if (this.g != null) {
                    buf.append('T');
                    buf.append(this.g);
                }
            } else {
                buf.append(this.g);
            }
        }
        return buf.toString();
    }
}

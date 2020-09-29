package j$.time.format;

import j$.CLASSNAMEp;
import j$.time.f;
import j$.time.m;
import j$.time.o;
import j$.time.t.q;
import j$.time.t.t;
import j$.time.u.D;
import j$.time.u.j;
import j$.time.u.s;
import j$.time.u.w;
import java.io.IOException;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class DateTimeFormatter {
    public static final DateTimeFormatter h;
    public static final DateTimeFormatter i;
    public static final DateTimeFormatter j;
    public static final DateTimeFormatter k;
    public static final DateTimeFormatter l;
    private final CLASSNAMEi a;
    private final Locale b;
    private final G c;
    private final I d;
    private final Set e;
    private final q f;
    private final o g;

    public static DateTimeFormatter ofPattern(String pattern, Locale locale) {
        z zVar = new z();
        zVar.j(pattern);
        return zVar.G(locale);
    }

    static {
        z p = new z().p(j.YEAR, 4, 10, J.EXCEEDS_PAD);
        p.e('-');
        p.o(j.MONTH_OF_YEAR, 2);
        p.e('-');
        p.o(j.DAY_OF_MONTH, 2);
        h = p.F(I.STRICT, t.a);
        z zVar = new z();
        zVar.z();
        zVar.a(h);
        zVar.i();
        zVar.F(I.STRICT, t.a);
        z zVar2 = new z();
        zVar2.z();
        zVar2.a(h);
        zVar2.w();
        zVar2.i();
        zVar2.F(I.STRICT, t.a);
        z zVar3 = new z();
        zVar3.o(j.HOUR_OF_DAY, 2);
        zVar3.e(':');
        zVar3.o(j.MINUTE_OF_HOUR, 2);
        zVar3.w();
        zVar3.e(':');
        zVar3.o(j.SECOND_OF_MINUTE, 2);
        zVar3.w();
        zVar3.b(j.NANO_OF_SECOND, 0, 9, true);
        i = zVar3.F(I.STRICT, (q) null);
        z zVar4 = new z();
        zVar4.z();
        zVar4.a(i);
        zVar4.i();
        zVar4.F(I.STRICT, (q) null);
        z zVar5 = new z();
        zVar5.z();
        zVar5.a(i);
        zVar5.w();
        zVar5.i();
        zVar5.F(I.STRICT, (q) null);
        z zVar6 = new z();
        zVar6.z();
        zVar6.a(h);
        zVar6.e('T');
        zVar6.a(i);
        j = zVar6.F(I.STRICT, t.a);
        z zVar7 = new z();
        zVar7.z();
        zVar7.a(j);
        zVar7.i();
        k = zVar7.F(I.STRICT, t.a);
        z zVar8 = new z();
        zVar8.a(k);
        zVar8.w();
        zVar8.e('[');
        zVar8.A();
        zVar8.s();
        zVar8.e(']');
        zVar8.F(I.STRICT, t.a);
        z zVar9 = new z();
        zVar9.a(j);
        zVar9.w();
        zVar9.i();
        zVar9.w();
        zVar9.e('[');
        zVar9.A();
        zVar9.s();
        zVar9.e(']');
        zVar9.F(I.STRICT, t.a);
        z zVar10 = new z();
        zVar10.z();
        z p2 = zVar10.p(j.YEAR, 4, 10, J.EXCEEDS_PAD);
        p2.e('-');
        p2.o(j.DAY_OF_YEAR, 3);
        p2.w();
        p2.i();
        p2.F(I.STRICT, t.a);
        z zVar11 = new z();
        zVar11.z();
        z p3 = zVar11.p(s.c, 4, 10, J.EXCEEDS_PAD);
        p3.f("-W");
        p3.o(s.b, 2);
        p3.e('-');
        p3.o(j.DAY_OF_WEEK, 1);
        p3.w();
        p3.i();
        p3.F(I.STRICT, t.a);
        z zVar12 = new z();
        zVar12.z();
        zVar12.c();
        l = zVar12.F(I.STRICT, (q) null);
        z zVar13 = new z();
        zVar13.z();
        zVar13.o(j.YEAR, 4);
        zVar13.o(j.MONTH_OF_YEAR, 2);
        zVar13.o(j.DAY_OF_MONTH, 2);
        zVar13.w();
        zVar13.h("+HHMMss", "Z");
        zVar13.F(I.STRICT, t.a);
        Map<Long, String> dow = new HashMap<>();
        dow.put(1L, "Mon");
        dow.put(2L, "Tue");
        dow.put(3L, "Wed");
        dow.put(4L, "Thu");
        dow.put(5L, "Fri");
        dow.put(6L, "Sat");
        dow.put(7L, "Sun");
        Map<Long, String> moy = new HashMap<>();
        moy.put(1L, "Jan");
        moy.put(2L, "Feb");
        moy.put(3L, "Mar");
        moy.put(4L, "Apr");
        moy.put(5L, "May");
        moy.put(6L, "Jun");
        moy.put(7L, "Jul");
        moy.put(8L, "Aug");
        moy.put(9L, "Sep");
        moy.put(10L, "Oct");
        moy.put(11L, "Nov");
        moy.put(12L, "Dec");
        z zVar14 = new z();
        zVar14.z();
        zVar14.C();
        zVar14.w();
        zVar14.l(j.DAY_OF_WEEK, dow);
        zVar14.f(", ");
        zVar14.v();
        z p4 = zVar14.p(j.DAY_OF_MONTH, 1, 2, J.NOT_NEGATIVE);
        p4.e(' ');
        p4.l(j.MONTH_OF_YEAR, moy);
        p4.e(' ');
        p4.o(j.YEAR, 4);
        p4.e(' ');
        p4.o(j.HOUR_OF_DAY, 2);
        p4.e(':');
        p4.o(j.MINUTE_OF_HOUR, 2);
        p4.w();
        p4.e(':');
        p4.o(j.SECOND_OF_MINUTE, 2);
        p4.v();
        p4.e(' ');
        p4.h("+HHMM", "GMT");
        p4.F(I.SMART, t.a);
        CLASSNAMEb bVar = CLASSNAMEb.a;
        CLASSNAMEa aVar = CLASSNAMEa.a;
    }

    static /* synthetic */ m h(w t) {
        if (t instanceof H) {
            return ((H) t).h;
        }
        return m.d;
    }

    static /* synthetic */ Boolean i(w t) {
        if (t instanceof H) {
            return Boolean.valueOf(((H) t).d);
        }
        return Boolean.FALSE;
    }

    DateTimeFormatter(CLASSNAMEi printerParser, Locale locale, G decimalStyle, I resolverStyle, Set resolverFields, q chrono, o zone) {
        CLASSNAMEp.a(printerParser, "printerParser");
        this.a = printerParser;
        this.e = resolverFields;
        CLASSNAMEp.a(locale, "locale");
        this.b = locale;
        CLASSNAMEp.a(decimalStyle, "decimalStyle");
        this.c = decimalStyle;
        CLASSNAMEp.a(resolverStyle, "resolverStyle");
        this.d = resolverStyle;
        this.f = chrono;
        this.g = zone;
    }

    public Locale f() {
        return this.b;
    }

    public G e() {
        return this.c;
    }

    public q d() {
        return this.f;
    }

    public o g() {
        return this.g;
    }

    public String b(w temporal) {
        StringBuilder buf = new StringBuilder(32);
        c(temporal, buf);
        return buf.toString();
    }

    public void c(w temporal, Appendable appendable) {
        CLASSNAMEp.a(temporal, "temporal");
        CLASSNAMEp.a(appendable, "appendable");
        try {
            C context = new C(temporal, this);
            if (appendable instanceof StringBuilder) {
                this.a.i(context, (StringBuilder) appendable);
                return;
            }
            StringBuilder buf = new StringBuilder(32);
            this.a.i(context, buf);
            appendable.append(buf);
        } catch (IOException ex) {
            throw new f(ex.getMessage(), ex);
        }
    }

    public Object j(CharSequence text, D d2) {
        CLASSNAMEp.a(text, "text");
        CLASSNAMEp.a(d2, "query");
        try {
            return ((H) k(text, (ParsePosition) null)).r(d2);
        } catch (DateTimeParseException ex) {
            throw ex;
        } catch (RuntimeException ex2) {
            throw a(text, ex2);
        }
    }

    private DateTimeParseException a(CharSequence text, RuntimeException ex) {
        String abbr;
        if (text.length() > 64) {
            abbr = text.subSequence(0, 64).toString() + "...";
        } else {
            abbr = text.toString();
        }
        return new DateTimeParseException("Text '" + abbr + "' could not be parsed: " + ex.getMessage(), text, 0, ex);
    }

    private w k(CharSequence text, ParsePosition position) {
        String abbr;
        ParsePosition pos = position != null ? position : new ParsePosition(0);
        A context = l(text, pos);
        if (context != null && pos.getErrorIndex() < 0 && (position != null || pos.getIndex() >= text.length())) {
            return context.t(this.d, this.e);
        }
        if (text.length() > 64) {
            abbr = text.subSequence(0, 64).toString() + "...";
        } else {
            abbr = text.toString();
        }
        if (pos.getErrorIndex() >= 0) {
            throw new DateTimeParseException("Text '" + abbr + "' could not be parsed at index " + pos.getErrorIndex(), text, pos.getErrorIndex());
        }
        throw new DateTimeParseException("Text '" + abbr + "' could not be parsed, unparsed text found at index " + pos.getIndex(), text, pos.getIndex());
    }

    private A l(CharSequence text, ParsePosition position) {
        CLASSNAMEp.a(text, "text");
        CLASSNAMEp.a(position, "position");
        A context = new A(this);
        int pos = this.a.p(context, text, position.getIndex());
        if (pos < 0) {
            position.setErrorIndex(pos ^ -1);
            return null;
        }
        position.setIndex(pos);
        return context;
    }

    /* access modifiers changed from: package-private */
    public CLASSNAMEi m(boolean optional) {
        return this.a.a(optional);
    }

    public String toString() {
        String pattern = this.a.toString();
        return pattern.startsWith("[") ? pattern : pattern.substring(1, pattern.length() - 1);
    }
}

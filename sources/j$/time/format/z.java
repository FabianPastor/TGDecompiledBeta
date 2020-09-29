package j$.time.format;

import j$.CLASSNAMEp;
import j$.time.o;
import j$.time.p;
import j$.time.t.f;
import j$.time.t.q;
import j$.time.u.B;
import j$.time.u.C;
import j$.time.u.D;
import j$.time.u.j;
import j$.time.u.s;
import j$.time.u.w;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class z {
    private static final D h = CLASSNAMEc.a;
    private static final Map i;
    private z a;
    private final z b;
    private final List c;
    private final boolean d;
    private int e;
    private char f;
    private int g;

    static {
        HashMap hashMap = new HashMap();
        i = hashMap;
        hashMap.put('G', j.ERA);
        i.put('y', j.YEAR_OF_ERA);
        i.put('u', j.YEAR);
        i.put('Q', s.a);
        i.put('q', s.a);
        i.put('M', j.MONTH_OF_YEAR);
        i.put('L', j.MONTH_OF_YEAR);
        i.put('D', j.DAY_OF_YEAR);
        i.put('d', j.DAY_OF_MONTH);
        i.put('F', j.ALIGNED_DAY_OF_WEEK_IN_MONTH);
        i.put('E', j.DAY_OF_WEEK);
        i.put('c', j.DAY_OF_WEEK);
        i.put('e', j.DAY_OF_WEEK);
        i.put('a', j.AMPM_OF_DAY);
        i.put('H', j.HOUR_OF_DAY);
        i.put('k', j.CLOCK_HOUR_OF_DAY);
        i.put('K', j.HOUR_OF_AMPM);
        i.put('h', j.CLOCK_HOUR_OF_AMPM);
        i.put('m', j.MINUTE_OF_HOUR);
        i.put('s', j.SECOND_OF_MINUTE);
        i.put('S', j.NANO_OF_SECOND);
        i.put('A', j.MILLI_OF_DAY);
        i.put('n', j.NANO_OF_SECOND);
        i.put('N', j.NANO_OF_DAY);
        new CLASSNAMEf();
    }

    static /* synthetic */ o u(w temporal) {
        o zone = (o) temporal.r(C.n());
        if (zone == null || (zone instanceof p)) {
            return null;
        }
        return zone;
    }

    public z() {
        this.a = this;
        this.c = new ArrayList();
        this.g = -1;
        this.b = null;
        this.d = false;
    }

    private z(z parent, boolean optional) {
        this.a = this;
        this.c = new ArrayList();
        this.g = -1;
        this.b = parent;
        this.d = optional;
    }

    public z A() {
        d(t.SENSITIVE);
        return this;
    }

    public z z() {
        d(t.INSENSITIVE);
        return this;
    }

    public z C() {
        d(t.LENIENT);
        return this;
    }

    public z n(B field) {
        CLASSNAMEp.a(field, "field");
        m(new n(field, 1, 19, J.NORMAL));
        return this;
    }

    public z o(B field, int width) {
        CLASSNAMEp.a(field, "field");
        if (width < 1 || width > 19) {
            throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + width);
        }
        m(new n(field, width, width, J.NOT_NEGATIVE));
        return this;
    }

    public z p(B field, int minWidth, int maxWidth, J signStyle) {
        if (minWidth == maxWidth && signStyle == J.NOT_NEGATIVE) {
            o(field, maxWidth);
            return this;
        }
        CLASSNAMEp.a(field, "field");
        CLASSNAMEp.a(signStyle, "signStyle");
        if (minWidth < 1 || minWidth > 19) {
            throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + minWidth);
        } else if (maxWidth < 1 || maxWidth > 19) {
            throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + maxWidth);
        } else if (maxWidth >= minWidth) {
            m(new n(field, minWidth, maxWidth, signStyle));
            return this;
        } else {
            throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
        }
    }

    public z q(B field, int width, int maxWidth, f baseDate) {
        CLASSNAMEp.a(field, "field");
        CLASSNAMEp.a(baseDate, "baseDate");
        m(new s(field, width, maxWidth, 0, baseDate));
        return this;
    }

    private z m(n pp) {
        n basePP;
        z zVar = this.a;
        if (zVar.g >= 0) {
            int activeValueParser = zVar.g;
            n basePP2 = (n) zVar.c.get(activeValueParser);
            if (pp.b == pp.c && pp.d == J.NOT_NEGATIVE) {
                basePP = basePP2.f(pp.c);
                d(pp.e());
                this.a.g = activeValueParser;
            } else {
                basePP = basePP2.e();
                this.a.g = d(pp);
            }
            this.a.c.set(activeValueParser, basePP);
        } else {
            zVar.g = d(pp);
        }
        return this;
    }

    public z b(B field, int minWidth, int maxWidth, boolean decimalPoint) {
        d(new CLASSNAMEk(field, minWidth, maxWidth, decimalPoint));
        return this;
    }

    public z k(B field, K textStyle) {
        CLASSNAMEp.a(field, "field");
        CLASSNAMEp.a(textStyle, "textStyle");
        d(new v(field, textStyle, F.g()));
        return this;
    }

    public z l(B field, Map textLookup) {
        CLASSNAMEp.a(field, "field");
        CLASSNAMEp.a(textLookup, "textLookup");
        d(new v(field, K.FULL, new CLASSNAMEe(this, new E(Collections.singletonMap(K.FULL, new LinkedHashMap<>(textLookup))))));
        return this;
    }

    public z c() {
        d(new CLASSNAMEl(-2));
        return this;
    }

    public z i() {
        d(o.d);
        return this;
    }

    public z h(String pattern, String noOffsetText) {
        d(new o(pattern, noOffsetText));
        return this;
    }

    public z g(K style) {
        CLASSNAMEp.a(style, "style");
        if (style == K.FULL || style == K.SHORT) {
            d(new m(style));
            return this;
        }
        throw new IllegalArgumentException("Style must be either full or short");
    }

    public z r() {
        d(new x(C.n(), "ZoneId()"));
        return this;
    }

    public z s() {
        d(new x(h, "ZoneRegionId()"));
        return this;
    }

    public z t(K textStyle) {
        d(new y(textStyle, (Set) null));
        return this;
    }

    public z e(char literal) {
        d(new CLASSNAMEh(literal));
        return this;
    }

    public z f(String literal) {
        CLASSNAMEp.a(literal, "literal");
        if (literal.length() > 0) {
            if (literal.length() == 1) {
                d(new CLASSNAMEh(literal.charAt(0)));
            } else {
                d(new u(literal));
            }
        }
        return this;
    }

    public z a(DateTimeFormatter formatter) {
        CLASSNAMEp.a(formatter, "formatter");
        d(formatter.m(false));
        return this;
    }

    public z j(String pattern) {
        CLASSNAMEp.a(pattern, "pattern");
        D(pattern);
        return this;
    }

    private void D(String pattern) {
        int pos;
        int start = 0;
        while (start < pattern.length()) {
            char cur = pattern.charAt(start);
            if ((cur >= 'A' && cur <= 'Z') || (cur >= 'a' && cur <= 'z')) {
                int pos2 = start + 1;
                while (pos2 < pattern.length() && pattern.charAt(pos2) == cur) {
                    pos2++;
                }
                int count = pos2 - start;
                if (cur == 'p') {
                    int pad = 0;
                    if (pos2 < pattern.length()) {
                        cur = pattern.charAt(pos2);
                        if ((cur < 'A' || cur > 'Z') && (cur < 'a' || cur > 'z')) {
                            pos = pos2;
                        } else {
                            pad = count;
                            pos = pos2 + 1;
                            int start2 = pos2;
                            while (pos < pattern.length() && pattern.charAt(pos) == cur) {
                                pos++;
                            }
                            count = pos - start2;
                        }
                    } else {
                        pos = pos2;
                    }
                    if (pad != 0) {
                        x(pad);
                    } else {
                        throw new IllegalArgumentException("Pad letter 'p' must be followed by valid pad pattern: " + pattern);
                    }
                } else {
                    pos = pos2;
                }
                B field = (B) i.get(Character.valueOf(cur));
                if (field != null) {
                    B(cur, count, field);
                } else if (cur == 'z') {
                    if (count > 4) {
                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                    } else if (count == 4) {
                        t(K.FULL);
                    } else {
                        t(K.SHORT);
                    }
                } else if (cur != 'V') {
                    String str = "+0000";
                    if (cur == 'Z') {
                        if (count < 4) {
                            h("+HHMM", str);
                        } else if (count == 4) {
                            g(K.FULL);
                        } else if (count == 5) {
                            h("+HH:MM:ss", "Z");
                        } else {
                            throw new IllegalArgumentException("Too many pattern letters: " + cur);
                        }
                    } else if (cur != 'O') {
                        int i2 = 0;
                        if (cur == 'X') {
                            if (count <= 5) {
                                String[] strArr = o.c;
                                if (count != 1) {
                                    i2 = 1;
                                }
                                h(strArr[i2 + count], "Z");
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'x') {
                            if (count <= 5) {
                                if (count == 1) {
                                    str = "+00";
                                } else if (count % 2 != 0) {
                                    str = "+00:00";
                                }
                                String zero = str;
                                String[] strArr2 = o.c;
                                if (count != 1) {
                                    i2 = 1;
                                }
                                h(strArr2[i2 + count], zero);
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'W') {
                            if (count <= 1) {
                                d(new w(cur, count));
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'w') {
                            if (count <= 2) {
                                d(new w(cur, count));
                            } else {
                                throw new IllegalArgumentException("Too many pattern letters: " + cur);
                            }
                        } else if (cur == 'Y') {
                            d(new w(cur, count));
                        } else {
                            throw new IllegalArgumentException("Unknown pattern letter: " + cur);
                        }
                    } else if (count == 1) {
                        g(K.SHORT);
                    } else if (count == 4) {
                        g(K.FULL);
                    } else {
                        throw new IllegalArgumentException("Pattern letter count must be 1 or 4: " + cur);
                    }
                } else if (count == 2) {
                    r();
                } else {
                    throw new IllegalArgumentException("Pattern letter count must be 2: " + cur);
                }
                start = pos - 1;
            } else if (cur == '\'') {
                int pos3 = start + 1;
                while (pos3 < pattern.length()) {
                    if (pattern.charAt(pos3) == '\'') {
                        if (pos3 + 1 >= pattern.length() || pattern.charAt(pos3 + 1) != '\'') {
                            break;
                        }
                        pos3++;
                    }
                    pos3++;
                }
                if (pos3 < pattern.length()) {
                    String str2 = pattern.substring(start + 1, pos3);
                    if (str2.length() == 0) {
                        e('\'');
                    } else {
                        f(str2.replace("''", "'"));
                    }
                    start = pos3;
                } else {
                    throw new IllegalArgumentException("Pattern ends with an incomplete string literal: " + pattern);
                }
            } else if (cur == '[') {
                w();
            } else if (cur == ']') {
                if (this.a.b != null) {
                    v();
                } else {
                    throw new IllegalArgumentException("Pattern invalid as it contains ] without previous [");
                }
            } else if (cur == '{' || cur == '}' || cur == '#') {
                throw new IllegalArgumentException("Pattern includes reserved character: '" + cur + "'");
            } else {
                e(cur);
            }
            start++;
        }
    }

    private void B(char cur, int count, B field) {
        boolean standalone = false;
        if (cur != 'Q') {
            if (cur == 'S') {
                b(j.NANO_OF_SECOND, count, count, false);
                return;
            } else if (cur != 'a') {
                if (!(cur == 'h' || cur == 'k' || cur == 'm')) {
                    if (cur != 'q') {
                        if (cur != 's') {
                            if (cur != 'u' && cur != 'y') {
                                switch (cur) {
                                    case 'D':
                                        if (count == 1) {
                                            n(field);
                                            return;
                                        } else if (count <= 3) {
                                            o(field, count);
                                            return;
                                        } else {
                                            throw new IllegalArgumentException("Too many pattern letters: " + cur);
                                        }
                                    case 'E':
                                        break;
                                    case 'F':
                                        if (count == 1) {
                                            n(field);
                                            return;
                                        }
                                        throw new IllegalArgumentException("Too many pattern letters: " + cur);
                                    case 'G':
                                        if (count == 1 || count == 2 || count == 3) {
                                            k(field, K.SHORT);
                                            return;
                                        } else if (count == 4) {
                                            k(field, K.FULL);
                                            return;
                                        } else if (count == 5) {
                                            k(field, K.NARROW);
                                            return;
                                        } else {
                                            throw new IllegalArgumentException("Too many pattern letters: " + cur);
                                        }
                                    case 'H':
                                        break;
                                    default:
                                        switch (cur) {
                                            case 'K':
                                                break;
                                            case 'L':
                                                break;
                                            case 'M':
                                                break;
                                            default:
                                                switch (cur) {
                                                    case 'c':
                                                        if (count == 2) {
                                                            throw new IllegalArgumentException("Invalid pattern \"cc\"");
                                                        }
                                                        break;
                                                    case 'd':
                                                        break;
                                                    case 'e':
                                                        break;
                                                    default:
                                                        if (count == 1) {
                                                            n(field);
                                                            return;
                                                        } else {
                                                            o(field, count);
                                                            return;
                                                        }
                                                }
                                        }
                                }
                            } else if (count == 2) {
                                q(field, 2, 2, s.i);
                                return;
                            } else if (count < 4) {
                                p(field, count, 19, J.NORMAL);
                                return;
                            } else {
                                p(field, count, 19, J.EXCEEDS_PAD);
                                return;
                            }
                        }
                    }
                    standalone = true;
                }
                if (count == 1) {
                    n(field);
                    return;
                } else if (count == 2) {
                    o(field, count);
                    return;
                } else {
                    throw new IllegalArgumentException("Too many pattern letters: " + cur);
                }
            } else if (count == 1) {
                k(field, K.SHORT);
                return;
            } else {
                throw new IllegalArgumentException("Too many pattern letters: " + cur);
            }
        }
        if (count == 1 || count == 2) {
            if (cur == 'c' || cur == 'e') {
                d(new w(cur, count));
            } else if (cur == 'E') {
                k(field, K.SHORT);
            } else if (count == 1) {
                n(field);
            } else {
                o(field, 2);
            }
        } else if (count == 3) {
            k(field, standalone ? K.SHORT_STANDALONE : K.SHORT);
        } else if (count == 4) {
            k(field, standalone ? K.FULL_STANDALONE : K.FULL);
        } else if (count == 5) {
            k(field, standalone ? K.NARROW_STANDALONE : K.NARROW);
        } else {
            throw new IllegalArgumentException("Too many pattern letters: " + cur);
        }
    }

    public z x(int padWidth) {
        y(padWidth, ' ');
        return this;
    }

    public z y(int padWidth, char padChar) {
        if (padWidth >= 1) {
            z zVar = this.a;
            zVar.e = padWidth;
            zVar.f = padChar;
            zVar.g = -1;
            return this;
        }
        throw new IllegalArgumentException("The pad width must be at least one but was " + padWidth);
    }

    public z w() {
        z zVar = this.a;
        zVar.g = -1;
        this.a = new z(zVar, true);
        return this;
    }

    public z v() {
        z zVar = this.a;
        if (zVar.b != null) {
            if (zVar.c.size() > 0) {
                z zVar2 = this.a;
                CLASSNAMEi cpp = new CLASSNAMEi(zVar2.c, zVar2.d);
                this.a = this.a.b;
                d(cpp);
            } else {
                this.a = this.a.b;
            }
            return this;
        }
        throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
    }

    private int d(CLASSNAMEj pp) {
        CLASSNAMEp.a(pp, "pp");
        z zVar = this.a;
        int i2 = zVar.e;
        if (i2 > 0) {
            pp = new p(pp, i2, zVar.f);
            z zVar2 = this.a;
            zVar2.e = 0;
            zVar2.f = 0;
        }
        this.a.c.add(pp);
        z zVar3 = this.a;
        zVar3.g = -1;
        return zVar3.c.size() - 1;
    }

    public DateTimeFormatter E() {
        return G(Locale.getDefault());
    }

    public DateTimeFormatter G(Locale locale) {
        return H(locale, I.SMART, (q) null);
    }

    /* access modifiers changed from: package-private */
    public DateTimeFormatter F(I resolverStyle, q chrono) {
        return H(Locale.getDefault(), resolverStyle, chrono);
    }

    private DateTimeFormatter H(Locale locale, I resolverStyle, q chrono) {
        CLASSNAMEp.a(locale, "locale");
        while (this.a.b != null) {
            v();
        }
        return new DateTimeFormatter(new CLASSNAMEi(this.c, false), locale, G.e, resolverStyle, (Set) null, chrono, (o) null);
    }
}

package j$.time.format;

import j$.CLASSNAMEd;
import j$.time.u.j;

final class m implements CLASSNAMEj {
    private final K a;

    m(K style) {
        this.a = style;
    }

    private static StringBuilder a(StringBuilder buf, int t) {
        buf.append((char) ((t / 10) + 48));
        buf.append((char) ((t % 10) + 48));
        return buf;
    }

    public boolean i(C context, StringBuilder buf) {
        Long offsetSecs = context.f(j.OFFSET_SECONDS);
        if (offsetSecs == null) {
            return false;
        }
        buf.append("GMT");
        int totalSecs = CLASSNAMEd.a(offsetSecs.longValue());
        if (totalSecs == 0) {
            return true;
        }
        int absHours = Math.abs((totalSecs / 3600) % 100);
        int absMinutes = Math.abs((totalSecs / 60) % 60);
        int absSeconds = Math.abs(totalSecs % 60);
        buf.append(totalSecs < 0 ? "-" : "+");
        if (this.a == K.FULL) {
            a(buf, absHours);
            buf.append(':');
            a(buf, absMinutes);
            if (absSeconds == 0) {
                return true;
            }
            buf.append(':');
            a(buf, absSeconds);
            return true;
        }
        if (absHours >= 10) {
            buf.append((char) ((absHours / 10) + 48));
        }
        buf.append((char) ((absHours % 10) + 48));
        if (absMinutes == 0 && absSeconds == 0) {
            return true;
        }
        buf.append(':');
        a(buf, absMinutes);
        if (absSeconds == 0) {
            return true;
        }
        buf.append(':');
        a(buf, absSeconds);
        return true;
    }

    /* access modifiers changed from: package-private */
    public int b(CharSequence text, int position) {
        char c = text.charAt(position);
        if (c < '0' || c > '9') {
            return -1;
        }
        return c - '0';
    }

    public int p(A context, CharSequence text, int position) {
        int negative;
        int pos;
        int pos2;
        int m1;
        int h;
        CharSequence charSequence = text;
        int pos3 = position;
        int end = pos3 + text.length();
        if (!context.s(text, pos3, "GMT", 0, "GMT".length())) {
            return position ^ -1;
        }
        int pos4 = pos3 + "GMT".length();
        if (pos4 == end) {
            return context.o(j.OFFSET_SECONDS, 0, position, pos4);
        }
        char sign = charSequence.charAt(pos4);
        if (sign == '+') {
            negative = 1;
        } else if (sign == '-') {
            negative = -1;
        } else {
            return context.o(j.OFFSET_SECONDS, 0, position, pos4);
        }
        int pos5 = pos4 + 1;
        int m = 0;
        int s = 0;
        if (this.a == K.FULL) {
            int pos6 = pos5 + 1;
            int h1 = b(charSequence, pos5);
            int pos7 = pos6 + 1;
            int h2 = b(charSequence, pos6);
            if (h1 < 0 || h2 < 0) {
                int m12 = pos7;
            } else {
                int pos8 = pos7 + 1;
                if (charSequence.charAt(pos7) == 58) {
                    int h3 = (h1 * 10) + h2;
                    int h4 = pos8 + 1;
                    int m13 = b(charSequence, pos8);
                    int pos9 = h4 + 1;
                    int m2 = b(charSequence, h4);
                    if (m13 < 0 || m2 < 0) {
                        return position ^ -1;
                    }
                    int m3 = (m13 * 10) + m2;
                    if (pos9 + 2 < end && charSequence.charAt(pos9) == ':') {
                        int s1 = b(charSequence, pos9 + 1);
                        int s2 = b(charSequence, pos9 + 2);
                        if (s1 >= 0 && s2 >= 0) {
                            s = (s1 * 10) + s2;
                            pos9 += 3;
                        }
                    }
                    m1 = s;
                    pos = pos9;
                    pos2 = h3;
                    h = m3;
                }
            }
            return position ^ -1;
        }
        int pos10 = pos5 + 1;
        int h5 = b(charSequence, pos5);
        if (h5 < 0) {
            return position ^ -1;
        }
        if (pos10 < end) {
            int h22 = b(charSequence, pos10);
            if (h22 >= 0) {
                pos10++;
                h5 = (h5 * 10) + h22;
            }
            if (pos10 + 2 < end && charSequence.charAt(pos10) == ':' && pos10 + 2 < end && charSequence.charAt(pos10) == ':') {
                int m14 = b(charSequence, pos10 + 1);
                int m22 = b(charSequence, pos10 + 2);
                if (m14 >= 0 && m22 >= 0) {
                    m = (m14 * 10) + m22;
                    pos10 += 3;
                    if (pos10 + 2 < end && charSequence.charAt(pos10) == ':') {
                        int s12 = b(charSequence, pos10 + 1);
                        int s22 = b(charSequence, pos10 + 2);
                        if (s12 >= 0 && s22 >= 0) {
                            m1 = (s12 * 10) + s22;
                            pos = pos10 + 3;
                            pos2 = h5;
                            h = m;
                        }
                    }
                }
            }
            m1 = 0;
            pos = pos10;
            pos2 = h5;
            h = m;
        } else {
            m1 = 0;
            pos = pos10;
            pos2 = h5;
            h = 0;
        }
        return context.o(j.OFFSET_SECONDS, ((long) negative) * ((((long) pos2) * 3600) + (((long) h) * 60) + ((long) m1)), position, pos);
    }

    public String toString() {
        return "LocalizedOffset(" + this.a + ")";
    }
}

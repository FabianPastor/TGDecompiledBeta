package j$.time.format;

import j$.CLASSNAMEf;
import j$.CLASSNAMEi;
import j$.CLASSNAMEk;
import j$.time.LocalDateTime;
import j$.time.p;
import j$.time.u.j;

/* renamed from: j$.time.format.l  reason: case insensitive filesystem */
final class CLASSNAMEl implements CLASSNAMEj {
    private final int a;

    CLASSNAMEl(int fractionalDigits) {
        this.a = fractionalDigits;
    }

    public boolean i(C context, StringBuilder buf) {
        StringBuilder sb = buf;
        Long inSecs = context.f(j.INSTANT_SECONDS);
        Long inNanos = null;
        if (context.e().h(j.NANO_OF_SECOND)) {
            inNanos = Long.valueOf(context.e().f(j.NANO_OF_SECOND));
        }
        if (inSecs == null) {
            return false;
        }
        long inSec = inSecs.longValue();
        int inNano = j.NANO_OF_SECOND.O(inNanos != null ? inNanos.longValue() : 0);
        if (inSec >= -62167219200L) {
            long zeroSecs = (inSec - 315569520000L) + 62167219200L;
            long hi = CLASSNAMEf.a(zeroSecs, 315569520000L) + 1;
            Long l = inSecs;
            Long l2 = inNanos;
            LocalDateTime ldt = LocalDateTime.W(CLASSNAMEi.a(zeroSecs, 315569520000L) - 62167219200L, 0, p.f);
            if (hi > 0) {
                sb.append('+');
                sb.append(hi);
            }
            sb.append(ldt);
            if (ldt.P() == 0) {
                sb.append(":00");
            }
        } else {
            Long l3 = inNanos;
            long zeroSecs2 = inSec + 62167219200L;
            long hi2 = zeroSecs2 / 315569520000L;
            long lo = zeroSecs2 % 315569520000L;
            LocalDateTime ldt2 = LocalDateTime.W(lo - 62167219200L, 0, p.f);
            int pos = buf.length();
            sb.append(ldt2);
            if (ldt2.P() == 0) {
                sb.append(":00");
            }
            if (hi2 < 0) {
                if (ldt2.Q() == -10000) {
                    sb.replace(pos, pos + 2, Long.toString(hi2 - 1));
                } else if (lo == 0) {
                    sb.insert(pos, hi2);
                } else {
                    sb.insert(pos + 1, Math.abs(hi2));
                }
            }
        }
        if ((this.a < 0 && inNano > 0) || this.a > 0) {
            sb.append('.');
            int div = NUM;
            int i = 0;
            while (true) {
                if ((this.a != -1 || inNano <= 0) && ((this.a != -2 || (inNano <= 0 && i % 3 == 0)) && i >= this.a)) {
                    break;
                }
                int digit = inNano / div;
                sb.append((char) (digit + 48));
                inNano -= digit * div;
                div /= 10;
                i++;
            }
        }
        sb.append('Z');
        return true;
    }

    public int p(A context, CharSequence text, int position) {
        int sec;
        int hour;
        int sec2;
        int i = position;
        int i2 = this.a;
        int nano = 0;
        if (i2 < 0) {
            i2 = 0;
        }
        int minDigits = i2;
        int i3 = this.a;
        if (i3 < 0) {
            i3 = 9;
        }
        int maxDigits = i3;
        z zVar = new z();
        zVar.a(DateTimeFormatter.h);
        zVar.e('T');
        zVar.o(j.HOUR_OF_DAY, 2);
        zVar.e(':');
        zVar.o(j.MINUTE_OF_HOUR, 2);
        zVar.e(':');
        zVar.o(j.SECOND_OF_MINUTE, 2);
        zVar.b(j.NANO_OF_SECOND, minDigits, maxDigits, true);
        zVar.e('Z');
        CLASSNAMEi parser = zVar.E().m(false);
        A newContext = context.d();
        int pos = parser.p(newContext, text, i);
        if (pos < 0) {
            return pos;
        }
        long yearParsed = newContext.j(j.YEAR).longValue();
        int month = newContext.j(j.MONTH_OF_YEAR).intValue();
        int day = newContext.j(j.DAY_OF_MONTH).intValue();
        int hour2 = newContext.j(j.HOUR_OF_DAY).intValue();
        int min = newContext.j(j.MINUTE_OF_HOUR).intValue();
        Long secVal = newContext.j(j.SECOND_OF_MINUTE);
        Long nanoVal = newContext.j(j.NANO_OF_SECOND);
        int sec3 = secVal != null ? secVal.intValue() : 0;
        if (nanoVal != null) {
            nano = nanoVal.intValue();
        }
        if (hour2 == 24 && min == 0 && sec3 == 0 && nano == 0) {
            hour = 0;
            sec = sec3;
            sec2 = 1;
        } else if (hour2 == 23 && min == 59 && sec3 == 60) {
            context.p();
            hour = hour2;
            sec = 59;
            sec2 = 0;
        } else {
            hour = hour2;
            sec = sec3;
            sec2 = 0;
        }
        int year = ((int) yearParsed) % 10000;
        try {
            int nano2 = nano;
            try {
                int i4 = min;
                int year2 = year;
                try {
                    long j = yearParsed;
                    int days = sec2;
                    try {
                        long instantSecs = LocalDateTime.U(year, month, day, hour, min, sec, 0).Y((long) sec2).w(p.f) + CLASSNAMEk.a(yearParsed / 10000, 315569520000L);
                        int i5 = year2;
                        A a2 = context;
                        int i6 = days;
                        int i7 = position;
                        return a2.o(j.NANO_OF_SECOND, (long) nano2, i7, a2.o(j.INSTANT_SECONDS, instantSecs, i7, pos));
                    } catch (RuntimeException e) {
                        int i8 = year2;
                        int year3 = days;
                        return i ^ -1;
                    }
                } catch (RuntimeException e2) {
                    long j2 = yearParsed;
                    int i9 = year2;
                    int year4 = sec2;
                    return i ^ -1;
                }
            } catch (RuntimeException e3) {
                int i10 = sec2;
                long j3 = yearParsed;
                int i11 = min;
                int i12 = year;
                return i ^ -1;
            }
        } catch (RuntimeException e4) {
            int i13 = nano;
            int i14 = sec2;
            long j4 = yearParsed;
            int i15 = min;
            int i16 = year;
            return i ^ -1;
        }
    }

    public String toString() {
        return "Instant()";
    }
}

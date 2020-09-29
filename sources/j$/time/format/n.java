package j$.time.format;

import j$.time.u.B;
import java.math.BigInteger;

class n implements CLASSNAMEj {
    static final long[] f = {0, 10, 100, 1000, 10000, 100000, 1000000, 10000000, NUM, NUM, 10000000000L};
    final B a;
    final int b;
    final int c;
    /* access modifiers changed from: private */
    public final J d;
    final int e;

    n(B field, int minWidth, int maxWidth, J signStyle) {
        this.a = field;
        this.b = minWidth;
        this.c = maxWidth;
        this.d = signStyle;
        this.e = 0;
    }

    protected n(B field, int minWidth, int maxWidth, J signStyle, int subsequentWidth) {
        this.a = field;
        this.b = minWidth;
        this.c = maxWidth;
        this.d = signStyle;
        this.e = subsequentWidth;
    }

    /* access modifiers changed from: package-private */
    public n e() {
        if (this.e == -1) {
            return this;
        }
        return new n(this.a, this.b, this.c, this.d, -1);
    }

    /* access modifiers changed from: package-private */
    public n f(int subsequentWidth) {
        return new n(this.a, this.b, this.c, this.d, this.e + subsequentWidth);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x007a, code lost:
        if (r5 != 4) goto L_0x00a7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean i(j$.time.format.C r12, java.lang.StringBuilder r13) {
        /*
            r11 = this;
            j$.time.u.B r0 = r11.a
            java.lang.Long r0 = r12.f(r0)
            if (r0 != 0) goto L_0x000a
            r1 = 0
            return r1
        L_0x000a:
            long r1 = r0.longValue()
            long r1 = r11.b(r12, r1)
            j$.time.format.G r3 = r12.c()
            r4 = -9223372036854775808
            int r6 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r6 != 0) goto L_0x001f
            java.lang.String r4 = "NUM"
            goto L_0x0027
        L_0x001f:
            long r4 = java.lang.Math.abs(r1)
            java.lang.String r4 = java.lang.Long.toString(r4)
        L_0x0027:
            int r5 = r4.length()
            int r6 = r11.c
            java.lang.String r7 = " cannot be printed as the value "
            java.lang.String r8 = "Field "
            if (r5 > r6) goto L_0x00bf
            java.lang.String r4 = r3.a(r4)
            r5 = 0
            r9 = 1
            int r10 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r10 < 0) goto L_0x006c
            int[] r5 = j$.time.format.CLASSNAMEg.a
            j$.time.format.J r6 = r11.d
            int r6 = r6.ordinal()
            r5 = r5[r6]
            if (r5 == r9) goto L_0x0056
            r6 = 2
            if (r5 == r6) goto L_0x004e
            goto L_0x006b
        L_0x004e:
            char r5 = r3.e()
            r13.append(r5)
            goto L_0x006b
        L_0x0056:
            int r5 = r11.b
            r6 = 19
            if (r5 >= r6) goto L_0x006b
            long[] r6 = f
            r5 = r6[r5]
            int r7 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r7 < 0) goto L_0x006b
            char r5 = r3.e()
            r13.append(r5)
        L_0x006b:
            goto L_0x00a7
        L_0x006c:
            j$.time.format.J r5 = r11.d
            int r5 = r5.ordinal()
            if (r5 == 0) goto L_0x009f
            if (r5 == r9) goto L_0x009f
            r6 = 3
            if (r5 == r6) goto L_0x007d
            r6 = 4
            if (r5 == r6) goto L_0x009f
            goto L_0x00a7
        L_0x007d:
            j$.time.f r5 = new j$.time.f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            j$.time.u.B r8 = r11.a
            r6.append(r8)
            r6.append(r7)
            r6.append(r1)
            java.lang.String r7 = " cannot be negative according to the SignStyle"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            throw r5
        L_0x009f:
            char r5 = r3.d()
            r13.append(r5)
        L_0x00a7:
            r5 = 0
        L_0x00a8:
            int r6 = r11.b
            int r7 = r4.length()
            int r6 = r6 - r7
            if (r5 >= r6) goto L_0x00bb
            char r6 = r3.f()
            r13.append(r6)
            int r5 = r5 + 1
            goto L_0x00a8
        L_0x00bb:
            r13.append(r4)
            return r9
        L_0x00bf:
            j$.time.f r5 = new j$.time.f
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r8)
            j$.time.u.B r8 = r11.a
            r6.append(r8)
            r6.append(r7)
            r6.append(r1)
            java.lang.String r7 = " exceeds the maximum print width of "
            r6.append(r7)
            int r7 = r11.c
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            r5.<init>(r6)
            goto L_0x00e7
        L_0x00e6:
            throw r5
        L_0x00e7:
            goto L_0x00e6
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.n.i(j$.time.format.C, java.lang.StringBuilder):boolean");
    }

    /* access modifiers changed from: package-private */
    public long b(C context, long value) {
        return value;
    }

    /* access modifiers changed from: package-private */
    public boolean c(A context) {
        int i = this.e;
        return i == -1 || (i > 0 && this.b == this.c && this.d == J.NOT_NEGATIVE);
    }

    public int p(A context, CharSequence text, int position) {
        boolean positive;
        boolean negative;
        int position2;
        int pos;
        long total;
        BigInteger totalBig;
        int pos2;
        BigInteger totalBig2;
        long total2;
        int length;
        char sign;
        char sign2;
        int i = position;
        int length2 = text.length();
        if (i == length2) {
            return i ^ -1;
        }
        char sign3 = text.charAt(position);
        int i2 = 1;
        if (sign3 == context.g().e()) {
            if (!this.d.i(true, context.l(), this.b == this.c)) {
                return i ^ -1;
            }
            position2 = i + 1;
            negative = false;
            positive = true;
        } else if (sign3 == context.g().d()) {
            if (!this.d.i(false, context.l(), this.b == this.c)) {
                return i ^ -1;
            }
            position2 = i + 1;
            negative = true;
            positive = false;
        } else if (this.d == J.ALWAYS && context.l()) {
            return i ^ -1;
        } else {
            position2 = i;
            negative = false;
            positive = false;
        }
        if (context.l() || c(context)) {
            i2 = this.b;
        }
        int effMinWidth = i2;
        int minEndPos = position2 + effMinWidth;
        if (minEndPos > length2) {
            return position2 ^ -1;
        }
        long total3 = 0;
        BigInteger totalBig3 = null;
        int pos3 = position2;
        int pass = 0;
        int effMaxWidth = ((context.l() || c(context)) ? this.c : 9) + Math.max(this.e, 0);
        while (true) {
            if (pass >= 2) {
                char c2 = sign3;
                pos = pos3;
                break;
            }
            int maxEndPos = Math.min(pos3 + effMaxWidth, length2);
            while (true) {
                if (pos3 >= maxEndPos) {
                    total2 = total3;
                    length = length2;
                    sign = sign3;
                    pos = pos3;
                    break;
                }
                int pos4 = pos3 + 1;
                length = length2;
                char ch = text.charAt(pos3);
                int maxEndPos2 = maxEndPos;
                int digit = context.g().b(ch);
                if (digit < 0) {
                    pos = pos4 - 1;
                    if (pos < minEndPos) {
                        return position2 ^ -1;
                    }
                    total2 = total3;
                    sign = sign3;
                } else {
                    char c3 = ch;
                    if (pos4 - position2 > 18) {
                        if (totalBig3 == null) {
                            totalBig3 = BigInteger.valueOf(total3);
                        }
                        sign2 = sign3;
                        totalBig3 = totalBig3.multiply(BigInteger.TEN).add(BigInteger.valueOf((long) digit));
                    } else {
                        sign2 = sign3;
                        long j = total3;
                        total3 = ((long) digit) + (10 * total3);
                    }
                    pos3 = pos4;
                    maxEndPos = maxEndPos2;
                    length2 = length;
                    sign3 = sign2;
                }
            }
            int i3 = this.e;
            if (i3 <= 0 || pass != 0) {
                total3 = total2;
            } else {
                effMaxWidth = Math.max(effMinWidth, (pos - position2) - i3);
                pos3 = position2;
                totalBig3 = null;
                pass++;
                total3 = 0;
                length2 = length;
                sign3 = sign;
            }
        }
        if (!negative) {
            if (this.d == J.EXCEEDS_PAD && context.l()) {
                int parseLen = pos - position2;
                if (positive) {
                    if (parseLen <= this.b) {
                        return (position2 - 1) ^ -1;
                    }
                } else if (parseLen > this.b) {
                    return position2 ^ -1;
                }
            }
            total = total3;
            totalBig = totalBig3;
        } else if (totalBig3 != null) {
            if (totalBig3.equals(BigInteger.ZERO) && context.l()) {
                return (position2 - 1) ^ -1;
            }
            total = total3;
            totalBig = totalBig3.negate();
        } else if (total3 == 0 && context.l()) {
            return (position2 - 1) ^ -1;
        } else {
            total = -total3;
            totalBig = totalBig3;
        }
        if (totalBig != null) {
            if (totalBig.bitLength() > 63) {
                pos2 = pos - 1;
                totalBig2 = totalBig.divide(BigInteger.TEN);
            } else {
                pos2 = pos;
                totalBig2 = totalBig;
            }
            return d(context, totalBig2.longValue(), position2, pos2);
        }
        BigInteger bigInteger = totalBig;
        return d(context, total, position2, pos);
    }

    /* access modifiers changed from: package-private */
    public int d(A context, long value, int errorPos, int successPos) {
        return context.o(this.a, value, errorPos, successPos);
    }

    public String toString() {
        if (this.b == 1 && this.c == 19 && this.d == J.NORMAL) {
            return "Value(" + this.a + ")";
        } else if (this.b == this.c && this.d == J.NOT_NEGATIVE) {
            return "Value(" + this.a + "," + this.b + ")";
        } else {
            return "Value(" + this.a + "," + this.b + "," + this.c + "," + this.d + ")";
        }
    }
}

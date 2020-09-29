package j$.time.format;

import j$.CLASSNAMEd;
import j$.CLASSNAMEp;
import j$.time.u.j;

final class o implements CLASSNAMEj {
    static final String[] c = {"+HH", "+HHmm", "+HH:mm", "+HHMM", "+HH:MM", "+HHMMss", "+HH:MM:ss", "+HHMMSS", "+HH:MM:SS"};
    static final o d = new o("+HH:MM:ss", "Z");
    static final o e = new o("+HH:MM:ss", "0");
    private final String a;
    private final int b;

    o(String pattern, String noOffsetText) {
        CLASSNAMEp.a(pattern, "pattern");
        CLASSNAMEp.a(noOffsetText, "noOffsetText");
        this.b = a(pattern);
        this.a = noOffsetText;
    }

    private int a(String pattern) {
        int i = 0;
        while (true) {
            String[] strArr = c;
            if (i >= strArr.length) {
                throw new IllegalArgumentException("Invalid zone offset pattern: " + pattern);
            } else if (strArr[i].equals(pattern)) {
                return i;
            } else {
                i++;
            }
        }
    }

    public boolean i(C context, StringBuilder buf) {
        Long offsetSecs = context.f(j.OFFSET_SECONDS);
        if (offsetSecs == null) {
            return false;
        }
        int totalSecs = CLASSNAMEd.a(offsetSecs.longValue());
        if (totalSecs == 0) {
            buf.append(this.a);
        } else {
            int absHours = Math.abs((totalSecs / 3600) % 100);
            int absMinutes = Math.abs((totalSecs / 60) % 60);
            int absSeconds = Math.abs(totalSecs % 60);
            int bufPos = buf.length();
            int output = absHours;
            buf.append(totalSecs < 0 ? "-" : "+");
            buf.append((char) ((absHours / 10) + 48));
            buf.append((char) ((absHours % 10) + 48));
            int i = this.b;
            if (i >= 3 || (i >= 1 && absMinutes > 0)) {
                String str = ":";
                buf.append(this.b % 2 == 0 ? str : "");
                buf.append((char) ((absMinutes / 10) + 48));
                buf.append((char) ((absMinutes % 10) + 48));
                output += absMinutes;
                int i2 = this.b;
                if (i2 >= 7 || (i2 >= 5 && absSeconds > 0)) {
                    if (this.b % 2 != 0) {
                        str = "";
                    }
                    buf.append(str);
                    buf.append((char) ((absSeconds / 10) + 48));
                    buf.append((char) ((absSeconds % 10) + 48));
                    output += absSeconds;
                }
            }
            if (output == 0) {
                buf.setLength(bufPos);
                buf.append(this.a);
            }
        }
        return true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:32:0x0083  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int p(j$.time.format.A r19, java.lang.CharSequence r20, int r21) {
        /*
            r18 = this;
            r0 = r18
            r7 = r20
            r8 = r21
            int r9 = r20.length()
            java.lang.String r1 = r0.a
            int r10 = r1.length()
            if (r10 != 0) goto L_0x0023
            if (r8 != r9) goto L_0x0047
            j$.time.u.j r2 = j$.time.u.j.OFFSET_SECONDS
            r3 = 0
            r1 = r19
            r5 = r21
            r6 = r21
            int r1 = r1.o(r2, r3, r5, r6)
            return r1
        L_0x0023:
            if (r8 != r9) goto L_0x0028
            r1 = r8 ^ -1
            return r1
        L_0x0028:
            java.lang.String r4 = r0.a
            r5 = 0
            r1 = r19
            r2 = r20
            r3 = r21
            r6 = r10
            boolean r1 = r1.s(r2, r3, r4, r5, r6)
            if (r1 == 0) goto L_0x0047
            j$.time.u.j r2 = j$.time.u.j.OFFSET_SECONDS
            int r6 = r8 + r10
            r3 = 0
            r1 = r19
            r5 = r21
            int r1 = r1.o(r2, r3, r5, r6)
            return r1
        L_0x0047:
            char r11 = r20.charAt(r21)
            r1 = 43
            r2 = 45
            if (r11 == r1) goto L_0x0053
            if (r11 != r2) goto L_0x00a8
        L_0x0053:
            r1 = 1
            if (r11 != r2) goto L_0x0058
            r2 = -1
            goto L_0x0059
        L_0x0058:
            r2 = 1
        L_0x0059:
            r12 = r2
            r2 = 4
            int[] r13 = new int[r2]
            int r2 = r8 + 1
            r3 = 0
            r13[r3] = r2
            boolean r2 = r0.b(r13, r1, r7, r1)
            r4 = 2
            r5 = 3
            if (r2 != 0) goto L_0x0080
            int r2 = r0.b
            if (r2 < r5) goto L_0x0070
            r2 = 1
            goto L_0x0071
        L_0x0070:
            r2 = 0
        L_0x0071:
            boolean r2 = r0.b(r13, r4, r7, r2)
            if (r2 != 0) goto L_0x0080
            boolean r2 = r0.b(r13, r5, r7, r3)
            if (r2 == 0) goto L_0x007e
            goto L_0x0080
        L_0x007e:
            r2 = 0
            goto L_0x0081
        L_0x0080:
            r2 = 1
        L_0x0081:
            if (r2 != 0) goto L_0x00a8
            long r14 = (long) r12
            r1 = r13[r1]
            long r1 = (long) r1
            r16 = 3600(0xe10, double:1.7786E-320)
            long r1 = r1 * r16
            r4 = r13[r4]
            long r3 = (long) r4
            r16 = 60
            long r3 = r3 * r16
            long r1 = r1 + r3
            r3 = r13[r5]
            long r3 = (long) r3
            long r1 = r1 + r3
            long r14 = r14 * r1
            j$.time.u.j r2 = j$.time.u.j.OFFSET_SECONDS
            r1 = 0
            r6 = r13[r1]
            r1 = r19
            r3 = r14
            r5 = r21
            int r1 = r1.o(r2, r3, r5, r6)
            return r1
        L_0x00a8:
            if (r10 != 0) goto L_0x00b9
            j$.time.u.j r2 = j$.time.u.j.OFFSET_SECONDS
            int r6 = r8 + r10
            r3 = 0
            r1 = r19
            r5 = r21
            int r1 = r1.o(r2, r3, r5, r6)
            return r1
        L_0x00b9:
            r1 = r8 ^ -1
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.o.p(j$.time.format.A, java.lang.CharSequence, int):int");
    }

    private boolean b(int[] array, int arrayIndex, CharSequence parseText, boolean required) {
        int value;
        int i = this.b;
        if ((i + 3) / 2 < arrayIndex) {
            return false;
        }
        int pos = array[0];
        if (i % 2 == 0 && arrayIndex > 1) {
            if (pos + 1 > parseText.length() || parseText.charAt(pos) != ':') {
                return required;
            }
            pos++;
        }
        if (pos + 2 > parseText.length()) {
            return required;
        }
        int pos2 = pos + 1;
        char ch1 = parseText.charAt(pos);
        int pos3 = pos2 + 1;
        char ch2 = parseText.charAt(pos2);
        if (ch1 < '0' || ch1 > '9' || ch2 < '0' || ch2 > '9' || (value = ((ch1 - '0') * 10) + (ch2 - '0')) < 0 || value > 59) {
            return required;
        }
        array[arrayIndex] = value;
        array[0] = pos3;
        return false;
    }

    public String toString() {
        String converted = this.a.replace("'", "''");
        return "Offset(" + c[this.b] + ",'" + converted + "')";
    }
}

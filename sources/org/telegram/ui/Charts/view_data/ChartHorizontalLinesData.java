package org.telegram.ui.Charts.view_data;

import java.util.ArrayList;
import java.util.Locale;

public class ChartHorizontalLinesData {
    public static final String[] s = {"", "K", "M", "G", "T", "P"};
    public int alpha;
    public int fixedAlpha;
    public int[] values;
    public String[] valuesStr;
    public String[] valuesStr2;

    public ChartHorizontalLinesData(int i, int i2, boolean z) {
        this(i, i2, z, 0.0f);
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x008e  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0099  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x009c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public ChartHorizontalLinesData(int r8, int r9, boolean r10, float r11) {
        /*
            r7 = this;
            r7.<init>()
            r0 = 255(0xff, float:3.57E-43)
            r7.fixedAlpha = r0
            r0 = 1084227584(0x40a00000, float:5.0)
            r1 = 0
            r2 = 6
            r3 = 2
            r4 = 1
            if (r10 != 0) goto L_0x0053
            r9 = 100
            if (r8 <= r9) goto L_0x0017
            int r8 = round(r8)
        L_0x0017:
            float r9 = (float) r8
            float r9 = r9 / r0
            double r9 = (double) r9
            double r9 = java.lang.Math.ceil(r9)
            int r9 = (int) r9
            int r9 = java.lang.Math.max(r4, r9)
            if (r8 >= r2) goto L_0x002b
            int r8 = r8 + r4
            int r2 = java.lang.Math.max(r3, r8)
            goto L_0x0036
        L_0x002b:
            int r10 = r8 / 2
            if (r10 >= r2) goto L_0x0036
            int r2 = r10 + 1
            int r8 = r8 % r3
            if (r8 == 0) goto L_0x0036
            int r2 = r2 + 1
        L_0x0036:
            int[] r8 = new int[r2]
            r7.values = r8
            java.lang.String[] r8 = new java.lang.String[r2]
            r7.valuesStr = r8
        L_0x003e:
            if (r4 >= r2) goto L_0x00e7
            int[] r8 = r7.values
            int r10 = r4 * r9
            r8[r4] = r10
            java.lang.String[] r10 = r7.valuesStr
            r8 = r8[r4]
            java.lang.String r8 = formatWholeNumber(r8, r1)
            r10[r4] = r8
            int r4 = r4 + 1
            goto L_0x003e
        L_0x0053:
            int r8 = r8 - r9
            r10 = 0
            r5 = 1065353216(0x3var_, float:1.0)
            if (r8 != 0) goto L_0x005f
            int r9 = r9 + -1
            r2 = 3
        L_0x005c:
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x0082
        L_0x005f:
            if (r8 >= r2) goto L_0x0068
            int r0 = r8 + 1
            int r2 = java.lang.Math.max(r3, r0)
            goto L_0x005c
        L_0x0068:
            int r6 = r8 / 2
            if (r6 >= r2) goto L_0x0074
            int r0 = r8 % 2
            int r6 = r6 + r0
            int r2 = r6 + 1
            r0 = 1073741824(0x40000000, float:2.0)
            goto L_0x0082
        L_0x0074:
            float r6 = (float) r8
            float r0 = r6 / r0
            int r6 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r6 > 0) goto L_0x0082
            int r0 = r8 + 1
            int r2 = java.lang.Math.max(r3, r0)
            goto L_0x005c
        L_0x0082:
            int[] r3 = new int[r2]
            r7.values = r3
            java.lang.String[] r3 = new java.lang.String[r2]
            r7.valuesStr = r3
            int r3 = (r11 > r10 ? 1 : (r11 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x0092
            java.lang.String[] r3 = new java.lang.String[r2]
            r7.valuesStr2 = r3
        L_0x0092:
            float r3 = r0 / r11
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x0099
            goto L_0x009a
        L_0x0099:
            r4 = 0
        L_0x009a:
            if (r1 >= r2) goto L_0x00e7
            int[] r3 = r7.values
            float r5 = (float) r1
            float r5 = r5 * r0
            int r5 = (int) r5
            int r5 = r5 + r9
            r3[r1] = r5
            java.lang.String[] r5 = r7.valuesStr
            r3 = r3[r1]
            java.lang.String r3 = formatWholeNumber(r3, r8)
            r5[r1] = r3
            int r3 = (r11 > r10 ? 1 : (r11 == r10 ? 0 : -1))
            if (r3 <= 0) goto L_0x00e4
            int[] r3 = r7.values
            r3 = r3[r1]
            float r3 = (float) r3
            float r3 = r3 / r11
            if (r4 == 0) goto L_0x00d8
            int r5 = (int) r3
            float r6 = (float) r5
            float r3 = r3 - r6
            r6 = 1008981770(0x3CLASSNAMEd70a, float:0.01)
            int r3 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r3 >= 0) goto L_0x00d1
            java.lang.String[] r3 = r7.valuesStr2
            float r6 = (float) r8
            float r6 = r6 / r11
            int r6 = (int) r6
            java.lang.String r5 = formatWholeNumber(r5, r6)
            r3[r1] = r5
            goto L_0x00e4
        L_0x00d1:
            java.lang.String[] r3 = r7.valuesStr2
            java.lang.String r5 = ""
            r3[r1] = r5
            goto L_0x00e4
        L_0x00d8:
            java.lang.String[] r5 = r7.valuesStr2
            int r3 = (int) r3
            float r6 = (float) r8
            float r6 = r6 / r11
            int r6 = (int) r6
            java.lang.String r3 = formatWholeNumber(r3, r6)
            r5[r1] = r3
        L_0x00e4:
            int r1 = r1 + 1
            goto L_0x009a
        L_0x00e7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Charts.view_data.ChartHorizontalLinesData.<init>(int, int, boolean, float):void");
    }

    public static int lookupHeight(int i) {
        if (i > 100) {
            i = round(i);
        }
        return ((int) Math.ceil((double) (((float) i) / 5.0f))) * 5;
    }

    public static String formatWholeNumber(int i, int i2) {
        if (i == 0) {
            return "0";
        }
        float f = (float) i;
        if (i2 == 0) {
            i2 = i;
        }
        if (i2 < 1000) {
            return formatCount(i);
        }
        int i3 = 0;
        while (i2 >= 1000 && i3 < s.length - 1) {
            i2 /= 1000;
            f /= 1000.0f;
            i3++;
        }
        int i4 = (int) f;
        if (f == ((float) i4)) {
            return String.format(Locale.ENGLISH, "%s%s", new Object[]{formatCount(i4), s[i3]});
        }
        return String.format(Locale.ENGLISH, "%.1f%s", new Object[]{Float.valueOf(f), s[i3]});
    }

    private static int round(int i) {
        if (((float) (i / 5)) % 10.0f == 0.0f) {
            return i;
        }
        return ((i / 10) + 1) * 10;
    }

    public static String formatCount(int i) {
        if (i < 1000) {
            return Integer.toString(i);
        }
        ArrayList arrayList = new ArrayList();
        while (i != 0) {
            int i2 = i % 1000;
            i /= 1000;
            if (i > 0) {
                arrayList.add(String.format(Locale.ENGLISH, "%03d", new Object[]{Integer.valueOf(i2)}));
            } else {
                arrayList.add(Integer.toString(i2));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            sb.append((String) arrayList.get(size));
            if (size != 0) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

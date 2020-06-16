package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SvgHelper {
    /* access modifiers changed from: private */
    public static final double[] pow10 = new double[128];

    private static void drawArc(Path path, float f, float f2, float f3, float f4, float f5, float f6, float f7, int i, int i2) {
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x002c, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r1.close();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0030 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getBitmap(java.io.File r3, int r4, int r5, boolean r6) {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0031 }
            r1.<init>(r3)     // Catch:{ Exception -> 0x0031 }
            javax.xml.parsers.SAXParserFactory r3 = javax.xml.parsers.SAXParserFactory.newInstance()     // Catch:{ all -> 0x002a }
            javax.xml.parsers.SAXParser r3 = r3.newSAXParser()     // Catch:{ all -> 0x002a }
            org.xml.sax.XMLReader r3 = r3.getXMLReader()     // Catch:{ all -> 0x002a }
            org.telegram.ui.Components.SvgHelper$SVGHandler r2 = new org.telegram.ui.Components.SvgHelper$SVGHandler     // Catch:{ all -> 0x002a }
            r2.<init>(r4, r5, r6)     // Catch:{ all -> 0x002a }
            r3.setContentHandler(r2)     // Catch:{ all -> 0x002a }
            org.xml.sax.InputSource r4 = new org.xml.sax.InputSource     // Catch:{ all -> 0x002a }
            r4.<init>(r1)     // Catch:{ all -> 0x002a }
            r3.parse(r4)     // Catch:{ all -> 0x002a }
            android.graphics.Bitmap r3 = r2.getBitmap()     // Catch:{ all -> 0x002a }
            r1.close()     // Catch:{ Exception -> 0x0031 }
            return r3
        L_0x002a:
            r3 = move-exception
            throw r3     // Catch:{ all -> 0x002c }
        L_0x002c:
            r3 = move-exception
            r1.close()     // Catch:{ all -> 0x0030 }
        L_0x0030:
            throw r3     // Catch:{ Exception -> 0x0031 }
        L_0x0031:
            r3 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.getBitmap(java.io.File, int, int, boolean):android.graphics.Bitmap");
    }

    private static NumberParse parseNumbers(String str) {
        int length = str.length();
        ArrayList arrayList = new ArrayList();
        int i = 0;
        boolean z = false;
        for (int i2 = 1; i2 < length; i2++) {
            if (z) {
                z = false;
            } else {
                char charAt = str.charAt(i2);
                switch (charAt) {
                    case 9:
                    case 10:
                    case ' ':
                    case ',':
                    case '-':
                        if (charAt != '-' || str.charAt(i2 - 1) != 'e') {
                            String substring = str.substring(i, i2);
                            if (substring.trim().length() <= 0) {
                                i++;
                                break;
                            } else {
                                arrayList.add(Float.valueOf(Float.parseFloat(substring)));
                                if (charAt != '-') {
                                    i = i2 + 1;
                                    z = true;
                                    break;
                                } else {
                                    i = i2;
                                    break;
                                }
                            }
                        } else {
                            break;
                        }
                    case ')':
                    case 'A':
                    case 'C':
                    case 'H':
                    case 'L':
                    case 'M':
                    case 'Q':
                    case 'S':
                    case 'T':
                    case 'V':
                    case 'Z':
                    case 'a':
                    case 'c':
                    case 'h':
                    case 'l':
                    case 'm':
                    case 'q':
                    case 's':
                    case 't':
                    case 'v':
                    case 'z':
                        String substring2 = str.substring(i, i2);
                        if (substring2.trim().length() > 0) {
                            arrayList.add(Float.valueOf(Float.parseFloat(substring2)));
                        }
                        return new NumberParse(arrayList, i2);
                }
            }
        }
        String substring3 = str.substring(i);
        if (substring3.length() > 0) {
            try {
                arrayList.add(Float.valueOf(Float.parseFloat(substring3)));
            } catch (NumberFormatException unused) {
            }
            i = str.length();
        }
        return new NumberParse(arrayList, i);
    }

    /* access modifiers changed from: private */
    public static Matrix parseTransform(String str) {
        float f;
        float f2 = 0.0f;
        if (str.startsWith("matrix(")) {
            NumberParse parseNumbers = parseNumbers(str.substring(7));
            if (parseNumbers.numbers.size() != 6) {
                return null;
            }
            Matrix matrix = new Matrix();
            matrix.setValues(new float[]{((Float) parseNumbers.numbers.get(0)).floatValue(), ((Float) parseNumbers.numbers.get(2)).floatValue(), ((Float) parseNumbers.numbers.get(4)).floatValue(), ((Float) parseNumbers.numbers.get(1)).floatValue(), ((Float) parseNumbers.numbers.get(3)).floatValue(), ((Float) parseNumbers.numbers.get(5)).floatValue(), 0.0f, 0.0f, 1.0f});
            return matrix;
        } else if (str.startsWith("translate(")) {
            NumberParse parseNumbers2 = parseNumbers(str.substring(10));
            if (parseNumbers2.numbers.size() <= 0) {
                return null;
            }
            float floatValue = ((Float) parseNumbers2.numbers.get(0)).floatValue();
            if (parseNumbers2.numbers.size() > 1) {
                f2 = ((Float) parseNumbers2.numbers.get(1)).floatValue();
            }
            Matrix matrix2 = new Matrix();
            matrix2.postTranslate(floatValue, f2);
            return matrix2;
        } else if (str.startsWith("scale(")) {
            NumberParse parseNumbers3 = parseNumbers(str.substring(6));
            if (parseNumbers3.numbers.size() <= 0) {
                return null;
            }
            float floatValue2 = ((Float) parseNumbers3.numbers.get(0)).floatValue();
            if (parseNumbers3.numbers.size() > 1) {
                f2 = ((Float) parseNumbers3.numbers.get(1)).floatValue();
            }
            Matrix matrix3 = new Matrix();
            matrix3.postScale(floatValue2, f2);
            return matrix3;
        } else if (str.startsWith("skewX(")) {
            NumberParse parseNumbers4 = parseNumbers(str.substring(6));
            if (parseNumbers4.numbers.size() <= 0) {
                return null;
            }
            float floatValue3 = ((Float) parseNumbers4.numbers.get(0)).floatValue();
            Matrix matrix4 = new Matrix();
            matrix4.postSkew((float) Math.tan((double) floatValue3), 0.0f);
            return matrix4;
        } else if (str.startsWith("skewY(")) {
            NumberParse parseNumbers5 = parseNumbers(str.substring(6));
            if (parseNumbers5.numbers.size() <= 0) {
                return null;
            }
            float floatValue4 = ((Float) parseNumbers5.numbers.get(0)).floatValue();
            Matrix matrix5 = new Matrix();
            matrix5.postSkew(0.0f, (float) Math.tan((double) floatValue4));
            return matrix5;
        } else if (!str.startsWith("rotate(")) {
            return null;
        } else {
            NumberParse parseNumbers6 = parseNumbers(str.substring(7));
            if (parseNumbers6.numbers.size() <= 0) {
                return null;
            }
            float floatValue5 = ((Float) parseNumbers6.numbers.get(0)).floatValue();
            if (parseNumbers6.numbers.size() > 2) {
                f2 = ((Float) parseNumbers6.numbers.get(1)).floatValue();
                f = ((Float) parseNumbers6.numbers.get(2)).floatValue();
            } else {
                f = 0.0f;
            }
            Matrix matrix6 = new Matrix();
            matrix6.postTranslate(f2, f);
            matrix6.postRotate(floatValue5);
            matrix6.postTranslate(-f2, -f);
            return matrix6;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x004a, code lost:
        if (r4 != 'L') goto L_0x004d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004d, code lost:
        r2.advance();
        r4 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00ea, code lost:
        r5 = r5 + r7;
        r6 = r6 + r8;
     */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:50:0x0144  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0182  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0186 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Path doPath(java.lang.String r24) {
        /*
            r0 = r24
            int r1 = r24.length()
            org.telegram.ui.Components.SvgHelper$ParserHelper r2 = new org.telegram.ui.Components.SvgHelper$ParserHelper
            r3 = 0
            r2.<init>(r0, r3)
            r2.skipWhitespace()
            android.graphics.Path r14 = new android.graphics.Path
            r14.<init>()
            r15 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
        L_0x001e:
            int r7 = r2.pos
            if (r7 >= r1) goto L_0x018d
            char r7 = r0.charAt(r7)
            r8 = 43
            r9 = 108(0x6c, float:1.51E-43)
            r10 = 99
            r11 = 109(0x6d, float:1.53E-43)
            if (r7 == r8) goto L_0x0038
            r8 = 45
            if (r7 == r8) goto L_0x0038
            switch(r7) {
                case 48: goto L_0x0038;
                case 49: goto L_0x0038;
                case 50: goto L_0x0038;
                case 51: goto L_0x0038;
                case 52: goto L_0x0038;
                case 53: goto L_0x0038;
                case 54: goto L_0x0038;
                case 55: goto L_0x0038;
                case 56: goto L_0x0038;
                case 57: goto L_0x0038;
                default: goto L_0x0037;
            }
        L_0x0037:
            goto L_0x004d
        L_0x0038:
            if (r4 == r11) goto L_0x0054
            r8 = 77
            if (r4 != r8) goto L_0x003f
            goto L_0x0054
        L_0x003f:
            if (r4 == r10) goto L_0x0051
            r8 = 67
            if (r4 != r8) goto L_0x0046
            goto L_0x0051
        L_0x0046:
            if (r4 == r9) goto L_0x0051
            r8 = 76
            if (r4 != r8) goto L_0x004d
            goto L_0x0051
        L_0x004d:
            r2.advance()
            r4 = r7
        L_0x0051:
            r18 = r4
            goto L_0x005a
        L_0x0054:
            int r7 = r4 + -1
            char r7 = (char) r7
            r18 = r4
            r4 = r7
        L_0x005a:
            r19 = 1
            switch(r4) {
                case 65: goto L_0x0144;
                case 67: goto L_0x0109;
                case 72: goto L_0x00f5;
                case 76: goto L_0x00dd;
                case 77: goto L_0x00c5;
                case 83: goto L_0x0087;
                case 86: goto L_0x0075;
                case 90: goto L_0x0067;
                case 97: goto L_0x0144;
                case 99: goto L_0x0109;
                case 104: goto L_0x00f5;
                case 108: goto L_0x00dd;
                case 109: goto L_0x00c5;
                case 115: goto L_0x0087;
                case 118: goto L_0x0075;
                case 122: goto L_0x0067;
                default: goto L_0x005f;
            }
        L_0x005f:
            r23 = r12
            r22 = r13
        L_0x0063:
            r19 = 0
            goto L_0x0180
        L_0x0067:
            r14.close()
            r14.moveTo(r13, r12)
            r6 = r12
            r17 = r6
            r5 = r13
            r16 = r5
            goto L_0x0180
        L_0x0075:
            float r7 = r2.nextFloat()
            r8 = 118(0x76, float:1.65E-43)
            if (r4 != r8) goto L_0x0082
            r14.rLineTo(r15, r7)
            float r6 = r6 + r7
            goto L_0x0063
        L_0x0082:
            r14.lineTo(r5, r7)
            r6 = r7
            goto L_0x0063
        L_0x0087:
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            float r9 = r2.nextFloat()
            float r10 = r2.nextFloat()
            r11 = 115(0x73, float:1.61E-43)
            if (r4 != r11) goto L_0x009f
            float r7 = r7 + r5
            float r9 = r9 + r5
            float r8 = r8 + r6
            float r10 = r10 + r6
        L_0x009f:
            r11 = r7
            r20 = r8
            r21 = r9
            r22 = r10
            r4 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 * r4
            float r5 = r5 - r16
            float r6 = r6 * r4
            float r6 = r6 - r17
            r4 = r14
            r7 = r11
            r8 = r20
            r9 = r21
            r10 = r22
            r4.cubicTo(r5, r6, r7, r8, r9, r10)
            r16 = r11
            r17 = r20
            r5 = r21
            r6 = r22
            goto L_0x0180
        L_0x00c5:
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            if (r4 != r11) goto L_0x00d5
            float r13 = r13 + r7
            float r12 = r12 + r8
            r14.rMoveTo(r7, r8)
            goto L_0x00ea
        L_0x00d5:
            r14.moveTo(r7, r8)
            r5 = r7
            r13 = r5
            r6 = r8
            r12 = r6
            goto L_0x0063
        L_0x00dd:
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            if (r4 != r9) goto L_0x00ee
            r14.rLineTo(r7, r8)
        L_0x00ea:
            float r5 = r5 + r7
            float r6 = r6 + r8
            goto L_0x0063
        L_0x00ee:
            r14.lineTo(r7, r8)
            r5 = r7
            r6 = r8
            goto L_0x0063
        L_0x00f5:
            float r7 = r2.nextFloat()
            r8 = 104(0x68, float:1.46E-43)
            if (r4 != r8) goto L_0x0103
            r14.rLineTo(r7, r15)
            float r5 = r5 + r7
            goto L_0x0063
        L_0x0103:
            r14.lineTo(r7, r6)
            r5 = r7
            goto L_0x0063
        L_0x0109:
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            float r9 = r2.nextFloat()
            float r11 = r2.nextFloat()
            float r16 = r2.nextFloat()
            float r17 = r2.nextFloat()
            if (r4 != r10) goto L_0x012b
            float r7 = r7 + r5
            float r9 = r9 + r5
            float r16 = r16 + r5
            float r8 = r8 + r6
            float r11 = r11 + r6
            float r17 = r17 + r6
        L_0x012b:
            r5 = r7
            r6 = r8
            r20 = r11
            r11 = r9
            r4 = r14
            r7 = r11
            r8 = r20
            r9 = r16
            r10 = r17
            r4.cubicTo(r5, r6, r7, r8, r9, r10)
            r5 = r16
            r6 = r17
            r17 = r20
            r16 = r11
            goto L_0x0180
        L_0x0144:
            float r9 = r2.nextFloat()
            float r10 = r2.nextFloat()
            float r11 = r2.nextFloat()
            float r4 = r2.nextFloat()
            int r8 = (int) r4
            float r4 = r2.nextFloat()
            int r7 = (int) r4
            float r19 = r2.nextFloat()
            float r20 = r2.nextFloat()
            r4 = r14
            r21 = r7
            r7 = r19
            r22 = r8
            r8 = r20
            r23 = r12
            r12 = r22
            r22 = r13
            r13 = r21
            drawArc(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r5 = r19
            r6 = r20
            r13 = r22
            r12 = r23
            goto L_0x0063
        L_0x0180:
            if (r19 != 0) goto L_0x0186
            r16 = r5
            r17 = r6
        L_0x0186:
            r2.skipWhitespace()
            r4 = r18
            goto L_0x001e
        L_0x018d:
            return r14
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.doPath(java.lang.String):android.graphics.Path");
    }

    /* access modifiers changed from: private */
    public static NumberParse getNumberParseAttr(String str, Attributes attributes) {
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            if (attributes.getLocalName(i).equals(str)) {
                return parseNumbers(attributes.getValue(i));
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static String getStringAttr(String str, Attributes attributes) {
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            if (attributes.getLocalName(i).equals(str)) {
                return attributes.getValue(i);
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static Float getFloatAttr(String str, Attributes attributes) {
        return getFloatAttr(str, attributes, (Float) null);
    }

    private static Float getFloatAttr(String str, Attributes attributes, Float f) {
        String stringAttr = getStringAttr(str, attributes);
        if (stringAttr == null) {
            return f;
        }
        if (stringAttr.endsWith("px")) {
            stringAttr = stringAttr.substring(0, stringAttr.length() - 2);
        } else if (stringAttr.endsWith("mm")) {
            return null;
        }
        return Float.valueOf(Float.parseFloat(stringAttr));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Integer getColorByName(java.lang.String r2) {
        /*
            java.lang.String r2 = r2.toLowerCase()
            int r0 = r2.hashCode()
            r1 = -1
            switch(r0) {
                case -734239628: goto L_0x005e;
                case 112785: goto L_0x0054;
                case 3027034: goto L_0x004a;
                case 3068707: goto L_0x0040;
                case 3181155: goto L_0x0036;
                case 93818879: goto L_0x002c;
                case 98619139: goto L_0x0022;
                case 113101865: goto L_0x0017;
                case 828922025: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x0068
        L_0x000d:
            java.lang.String r0 = "magenta"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 7
            goto L_0x0069
        L_0x0017:
            java.lang.String r0 = "white"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 8
            goto L_0x0069
        L_0x0022:
            java.lang.String r0 = "green"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 3
            goto L_0x0069
        L_0x002c:
            java.lang.String r0 = "black"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 0
            goto L_0x0069
        L_0x0036:
            java.lang.String r0 = "gray"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 1
            goto L_0x0069
        L_0x0040:
            java.lang.String r0 = "cyan"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 6
            goto L_0x0069
        L_0x004a:
            java.lang.String r0 = "blue"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 4
            goto L_0x0069
        L_0x0054:
            java.lang.String r0 = "red"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 2
            goto L_0x0069
        L_0x005e:
            java.lang.String r0 = "yellow"
            boolean r2 = r2.equals(r0)
            if (r2 == 0) goto L_0x0068
            r2 = 5
            goto L_0x0069
        L_0x0068:
            r2 = -1
        L_0x0069:
            switch(r2) {
                case 0: goto L_0x00a9;
                case 1: goto L_0x00a1;
                case 2: goto L_0x009a;
                case 3: goto L_0x0092;
                case 4: goto L_0x008a;
                case 5: goto L_0x0083;
                case 6: goto L_0x007b;
                case 7: goto L_0x0073;
                case 8: goto L_0x006e;
                default: goto L_0x006c;
            }
        L_0x006c:
            r2 = 0
            return r2
        L_0x006e:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            return r2
        L_0x0073:
            r2 = -65281(0xfffffffffffvar_ff, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x007b:
            r2 = -16711681(0xfffffffffvar_ffff, float:-1.714704E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0083:
            r2 = -256(0xfffffffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x008a:
            r2 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0092:
            r2 = -16711936(0xfffffffffvar_fvar_, float:-1.7146522E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x009a:
            r2 = -65536(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00a1:
            r2 = -7829368(0xfffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00a9:
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.getColorByName(java.lang.String):java.lang.Integer");
    }

    private static class NumberParse {
        /* access modifiers changed from: private */
        public ArrayList<Float> numbers;

        public NumberParse(ArrayList<Float> arrayList, int i) {
            this.numbers = arrayList;
        }
    }

    private static class StyleSet {
        HashMap<String, String> styleMap;

        private StyleSet(String str) {
            this.styleMap = new HashMap<>();
            for (String split : str.split(";")) {
                String[] split2 = split.split(":");
                if (split2.length == 2) {
                    this.styleMap.put(split2[0].trim(), split2[1].trim());
                }
            }
        }

        public String getStyle(String str) {
            return this.styleMap.get(str);
        }
    }

    private static class Properties {
        Attributes atts;
        ArrayList<StyleSet> styles;

        private Properties(Attributes attributes, HashMap<String, StyleSet> hashMap) {
            this.atts = attributes;
            String access$200 = SvgHelper.getStringAttr("style", attributes);
            if (access$200 != null) {
                ArrayList<StyleSet> arrayList = new ArrayList<>();
                this.styles = arrayList;
                arrayList.add(new StyleSet(access$200));
                return;
            }
            String access$2002 = SvgHelper.getStringAttr("class", attributes);
            if (access$2002 != null) {
                this.styles = new ArrayList<>();
                String[] split = access$2002.split(" ");
                for (String trim : split) {
                    StyleSet styleSet = hashMap.get(trim.trim());
                    if (styleSet != null) {
                        this.styles.add(styleSet);
                    }
                }
            }
        }

        public String getAttr(String str) {
            ArrayList<StyleSet> arrayList = this.styles;
            String str2 = null;
            if (arrayList != null && !arrayList.isEmpty()) {
                int size = this.styles.size();
                for (int i = 0; i < size; i++) {
                    str2 = this.styles.get(i).getStyle(str);
                    if (str2 != null) {
                        break;
                    }
                }
            }
            return str2 == null ? SvgHelper.getStringAttr(str, this.atts) : str2;
        }

        public String getString(String str) {
            return getAttr(str);
        }

        public Integer getHex(String str) {
            String attr = getAttr(str);
            if (attr == null) {
                return null;
            }
            try {
                return Integer.valueOf(Integer.parseInt(attr.substring(1), 16));
            } catch (NumberFormatException unused) {
                return SvgHelper.getColorByName(attr);
            }
        }

        public Float getFloat(String str) {
            String attr = getAttr(str);
            if (attr == null) {
                return null;
            }
            try {
                return Float.valueOf(Float.parseFloat(attr));
            } catch (NumberFormatException unused) {
                return null;
            }
        }
    }

    private static class SVGHandler extends DefaultHandler {
        private Bitmap bitmap;
        private boolean boundsMode;
        private Canvas canvas;
        private int desiredHeight;
        private int desiredWidth;
        private HashMap<String, StyleSet> globalStyles;
        private Paint paint;
        boolean pushed;
        private RectF rect;
        private float scale;
        private StringBuilder styles;
        private boolean whiteOnly;

        public void endDocument() {
        }

        public void startDocument() {
        }

        private SVGHandler(int i, int i2, boolean z) {
            this.scale = 1.0f;
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.pushed = false;
            this.globalStyles = new HashMap<>();
            this.desiredWidth = i;
            this.desiredHeight = i2;
            this.whiteOnly = z;
        }

        private boolean doFill(Properties properties) {
            if ("none".equals(properties.getString("display"))) {
                return false;
            }
            String string = properties.getString("fill");
            if (string == null || !string.startsWith("url(#")) {
                Integer hex = properties.getHex("fill");
                if (hex != null) {
                    doColor(properties, hex, true);
                    this.paint.setStyle(Paint.Style.FILL);
                    return true;
                } else if (properties.getString("fill") != null || properties.getString("stroke") != null) {
                    return false;
                } else {
                    this.paint.setStyle(Paint.Style.FILL);
                    if (this.whiteOnly) {
                        this.paint.setColor(-1);
                    } else {
                        this.paint.setColor(-16777216);
                    }
                    return true;
                }
            } else {
                string.substring(5, string.length() - 1);
                return false;
            }
        }

        private boolean doStroke(Properties properties) {
            Integer hex;
            if ("none".equals(properties.getString("display")) || (hex = properties.getHex("stroke")) == null) {
                return false;
            }
            doColor(properties, hex, false);
            Float f = properties.getFloat("stroke-width");
            if (f != null) {
                this.paint.setStrokeWidth(f.floatValue());
            }
            String string = properties.getString("stroke-linecap");
            if ("round".equals(string)) {
                this.paint.setStrokeCap(Paint.Cap.ROUND);
            } else if ("square".equals(string)) {
                this.paint.setStrokeCap(Paint.Cap.SQUARE);
            } else if ("butt".equals(string)) {
                this.paint.setStrokeCap(Paint.Cap.BUTT);
            }
            String string2 = properties.getString("stroke-linejoin");
            if ("miter".equals(string2)) {
                this.paint.setStrokeJoin(Paint.Join.MITER);
            } else if ("round".equals(string2)) {
                this.paint.setStrokeJoin(Paint.Join.ROUND);
            } else if ("bevel".equals(string2)) {
                this.paint.setStrokeJoin(Paint.Join.BEVEL);
            }
            this.paint.setStyle(Paint.Style.STROKE);
            return true;
        }

        private void doColor(Properties properties, Integer num, boolean z) {
            if (this.whiteOnly) {
                this.paint.setColor(-1);
            } else {
                this.paint.setColor((num.intValue() & 16777215) | -16777216);
            }
            Float f = properties.getFloat("opacity");
            if (f == null) {
                f = properties.getFloat(z ? "fill-opacity" : "stroke-opacity");
            }
            if (f == null) {
                this.paint.setAlpha(255);
            } else {
                this.paint.setAlpha((int) (f.floatValue() * 255.0f));
            }
        }

        private void pushTransform(Attributes attributes) {
            String access$200 = SvgHelper.getStringAttr("transform", attributes);
            boolean z = access$200 != null;
            this.pushed = z;
            if (z) {
                Matrix access$500 = SvgHelper.parseTransform(access$200);
                this.canvas.save();
                this.canvas.concat(access$500);
            }
        }

        private void popTransform() {
            if (this.pushed) {
                this.canvas.restore();
            }
        }

        public void startElement(String str, String str2, String str3, Attributes attributes) {
            int i;
            int i2;
            String access$200;
            if (!this.boundsMode || str2.equals("style")) {
                char c = 65535;
                switch (str2.hashCode()) {
                    case -1656480802:
                        if (str2.equals("ellipse")) {
                            c = 8;
                            break;
                        }
                        break;
                    case -1360216880:
                        if (str2.equals("circle")) {
                            c = 7;
                            break;
                        }
                        break;
                    case -397519558:
                        if (str2.equals("polygon")) {
                            c = 9;
                            break;
                        }
                        break;
                    case 103:
                        if (str2.equals("g")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 114276:
                        if (str2.equals("svg")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 3079438:
                        if (str2.equals("defs")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 3321844:
                        if (str2.equals("line")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 3433509:
                        if (str2.equals("path")) {
                            c = 11;
                            break;
                        }
                        break;
                    case 3496420:
                        if (str2.equals("rect")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 109780401:
                        if (str2.equals("style")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 561938880:
                        if (str2.equals("polyline")) {
                            c = 10;
                            break;
                        }
                        break;
                    case 917656469:
                        if (str2.equals("clipPath")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        Float access$600 = SvgHelper.getFloatAttr("width", attributes);
                        Float access$6002 = SvgHelper.getFloatAttr("height", attributes);
                        if ((access$600 == null || access$6002 == null) && (access$200 = SvgHelper.getStringAttr("viewBox", attributes)) != null) {
                            String[] split = access$200.split(" ");
                            Float valueOf = Float.valueOf(Float.parseFloat(split[2]));
                            access$6002 = Float.valueOf(Float.parseFloat(split[3]));
                            access$600 = valueOf;
                        }
                        if (access$600 == null || access$6002 == null) {
                            access$600 = Float.valueOf((float) this.desiredWidth);
                            access$6002 = Float.valueOf((float) this.desiredHeight);
                        }
                        int ceil = (int) Math.ceil((double) access$600.floatValue());
                        int ceil2 = (int) Math.ceil((double) access$6002.floatValue());
                        if (ceil == 0 || ceil2 == 0) {
                            i2 = this.desiredWidth;
                            i = this.desiredHeight;
                        } else {
                            float f = (float) ceil;
                            float f2 = (float) ceil2;
                            float min = Math.min(((float) this.desiredWidth) / f, ((float) this.desiredHeight) / f2);
                            this.scale = min;
                            i2 = (int) (f * min);
                            i = (int) (f2 * min);
                        }
                        Bitmap createBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
                        this.bitmap = createBitmap;
                        createBitmap.eraseColor(0);
                        Canvas canvas2 = new Canvas(this.bitmap);
                        this.canvas = canvas2;
                        float f3 = this.scale;
                        if (f3 != 0.0f) {
                            canvas2.scale(f3, f3);
                            return;
                        }
                        return;
                    case 1:
                    case 2:
                        this.boundsMode = true;
                        return;
                    case 3:
                        this.styles = new StringBuilder();
                        return;
                    case 4:
                        if ("bounds".equalsIgnoreCase(SvgHelper.getStringAttr("id", attributes))) {
                            this.boundsMode = true;
                            return;
                        }
                        return;
                    case 5:
                        Float access$6003 = SvgHelper.getFloatAttr("x", attributes);
                        if (access$6003 == null) {
                            access$6003 = Float.valueOf(0.0f);
                        }
                        Float access$6004 = SvgHelper.getFloatAttr("y", attributes);
                        if (access$6004 == null) {
                            access$6004 = Float.valueOf(0.0f);
                        }
                        Float access$6005 = SvgHelper.getFloatAttr("width", attributes);
                        Float access$6006 = SvgHelper.getFloatAttr("height", attributes);
                        pushTransform(attributes);
                        Properties properties = new Properties(attributes, this.globalStyles);
                        if (doFill(properties)) {
                            this.canvas.drawRect(access$6003.floatValue(), access$6004.floatValue(), access$6005.floatValue() + access$6003.floatValue(), access$6006.floatValue() + access$6004.floatValue(), this.paint);
                        }
                        if (doStroke(properties)) {
                            this.canvas.drawRect(access$6003.floatValue(), access$6004.floatValue(), access$6003.floatValue() + access$6005.floatValue(), access$6004.floatValue() + access$6006.floatValue(), this.paint);
                        }
                        popTransform();
                        return;
                    case 6:
                        Float access$6007 = SvgHelper.getFloatAttr("x1", attributes);
                        Float access$6008 = SvgHelper.getFloatAttr("x2", attributes);
                        Float access$6009 = SvgHelper.getFloatAttr("y1", attributes);
                        Float access$60010 = SvgHelper.getFloatAttr("y2", attributes);
                        if (doStroke(new Properties(attributes, this.globalStyles))) {
                            pushTransform(attributes);
                            this.canvas.drawLine(access$6007.floatValue(), access$6009.floatValue(), access$6008.floatValue(), access$60010.floatValue(), this.paint);
                            popTransform();
                            return;
                        }
                        return;
                    case 7:
                        Float access$60011 = SvgHelper.getFloatAttr("cx", attributes);
                        Float access$60012 = SvgHelper.getFloatAttr("cy", attributes);
                        Float access$60013 = SvgHelper.getFloatAttr("r", attributes);
                        if (access$60011 != null && access$60012 != null && access$60013 != null) {
                            pushTransform(attributes);
                            Properties properties2 = new Properties(attributes, this.globalStyles);
                            if (doFill(properties2)) {
                                this.canvas.drawCircle(access$60011.floatValue(), access$60012.floatValue(), access$60013.floatValue(), this.paint);
                            }
                            if (doStroke(properties2)) {
                                this.canvas.drawCircle(access$60011.floatValue(), access$60012.floatValue(), access$60013.floatValue(), this.paint);
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 8:
                        Float access$60014 = SvgHelper.getFloatAttr("cx", attributes);
                        Float access$60015 = SvgHelper.getFloatAttr("cy", attributes);
                        Float access$60016 = SvgHelper.getFloatAttr("rx", attributes);
                        Float access$60017 = SvgHelper.getFloatAttr("ry", attributes);
                        if (access$60014 != null && access$60015 != null && access$60016 != null && access$60017 != null) {
                            pushTransform(attributes);
                            Properties properties3 = new Properties(attributes, this.globalStyles);
                            this.rect.set(access$60014.floatValue() - access$60016.floatValue(), access$60015.floatValue() - access$60017.floatValue(), access$60014.floatValue() + access$60016.floatValue(), access$60015.floatValue() + access$60017.floatValue());
                            if (doFill(properties3)) {
                                this.canvas.drawOval(this.rect, this.paint);
                            }
                            if (doStroke(properties3)) {
                                this.canvas.drawOval(this.rect, this.paint);
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 9:
                    case 10:
                        NumberParse access$800 = SvgHelper.getNumberParseAttr("points", attributes);
                        if (access$800 != null) {
                            Path path = new Path();
                            ArrayList access$100 = access$800.numbers;
                            if (access$100.size() > 1) {
                                pushTransform(attributes);
                                Properties properties4 = new Properties(attributes, this.globalStyles);
                                path.moveTo(((Float) access$100.get(0)).floatValue(), ((Float) access$100.get(1)).floatValue());
                                for (int i3 = 2; i3 < access$100.size(); i3 += 2) {
                                    path.lineTo(((Float) access$100.get(i3)).floatValue(), ((Float) access$100.get(i3 + 1)).floatValue());
                                }
                                if (str2.equals("polygon")) {
                                    path.close();
                                }
                                if (doFill(properties4)) {
                                    this.canvas.drawPath(path, this.paint);
                                }
                                if (doStroke(properties4)) {
                                    this.canvas.drawPath(path, this.paint);
                                }
                                popTransform();
                                return;
                            }
                            return;
                        }
                        return;
                    case 11:
                        Path access$900 = SvgHelper.doPath(SvgHelper.getStringAttr("d", attributes));
                        pushTransform(attributes);
                        Properties properties5 = new Properties(attributes, this.globalStyles);
                        if (doFill(properties5)) {
                            this.canvas.drawPath(access$900, this.paint);
                        }
                        if (doStroke(properties5)) {
                            this.canvas.drawPath(access$900, this.paint);
                        }
                        popTransform();
                        return;
                    default:
                        return;
                }
            }
        }

        public void characters(char[] cArr, int i, int i2) {
            StringBuilder sb = this.styles;
            if (sb != null) {
                sb.append(cArr, i, i2);
            }
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void endElement(java.lang.String r7, java.lang.String r8, java.lang.String r9) {
            /*
                r6 = this;
                int r7 = r8.hashCode()
                r9 = 4
                r0 = 3
                r1 = 2
                r2 = 1
                r3 = 0
                switch(r7) {
                    case 103: goto L_0x0035;
                    case 114276: goto L_0x002b;
                    case 3079438: goto L_0x0021;
                    case 109780401: goto L_0x0017;
                    case 917656469: goto L_0x000d;
                    default: goto L_0x000c;
                }
            L_0x000c:
                goto L_0x003f
            L_0x000d:
                java.lang.String r7 = "clipPath"
                boolean r7 = r8.equals(r7)
                if (r7 == 0) goto L_0x003f
                r7 = 4
                goto L_0x0040
            L_0x0017:
                java.lang.String r7 = "style"
                boolean r7 = r8.equals(r7)
                if (r7 == 0) goto L_0x003f
                r7 = 0
                goto L_0x0040
            L_0x0021:
                java.lang.String r7 = "defs"
                boolean r7 = r8.equals(r7)
                if (r7 == 0) goto L_0x003f
                r7 = 3
                goto L_0x0040
            L_0x002b:
                java.lang.String r7 = "svg"
                boolean r7 = r8.equals(r7)
                if (r7 == 0) goto L_0x003f
                r7 = 1
                goto L_0x0040
            L_0x0035:
                java.lang.String r7 = "g"
                boolean r7 = r8.equals(r7)
                if (r7 == 0) goto L_0x003f
                r7 = 2
                goto L_0x0040
            L_0x003f:
                r7 = -1
            L_0x0040:
                if (r7 == 0) goto L_0x004c
                if (r7 == r1) goto L_0x0049
                if (r7 == r0) goto L_0x0049
                if (r7 == r9) goto L_0x0049
                goto L_0x00b4
            L_0x0049:
                r6.boundsMode = r3
                goto L_0x00b4
            L_0x004c:
                java.lang.StringBuilder r7 = r6.styles
                if (r7 == 0) goto L_0x00b4
                java.lang.String r7 = r7.toString()
                java.lang.String r8 = "\\}"
                java.lang.String[] r7 = r7.split(r8)
                r8 = 0
            L_0x005b:
                int r9 = r7.length
                r0 = 0
                if (r8 >= r9) goto L_0x00b2
                r9 = r7[r8]
                java.lang.String r9 = r9.trim()
                java.lang.String r1 = "\t"
                java.lang.String r4 = ""
                java.lang.String r9 = r9.replace(r1, r4)
                java.lang.String r1 = "\n"
                java.lang.String r9 = r9.replace(r1, r4)
                r7[r8] = r9
                r9 = r7[r8]
                int r9 = r9.length()
                if (r9 == 0) goto L_0x00af
                r9 = r7[r8]
                char r9 = r9.charAt(r3)
                r1 = 46
                if (r9 == r1) goto L_0x0088
                goto L_0x00af
            L_0x0088:
                r9 = r7[r8]
                r1 = 123(0x7b, float:1.72E-43)
                int r9 = r9.indexOf(r1)
                if (r9 >= 0) goto L_0x0093
                goto L_0x00af
            L_0x0093:
                r1 = r7[r8]
                java.lang.String r1 = r1.substring(r2, r9)
                java.lang.String r1 = r1.trim()
                r4 = r7[r8]
                int r9 = r9 + 1
                java.lang.String r9 = r4.substring(r9)
                java.util.HashMap<java.lang.String, org.telegram.ui.Components.SvgHelper$StyleSet> r4 = r6.globalStyles
                org.telegram.ui.Components.SvgHelper$StyleSet r5 = new org.telegram.ui.Components.SvgHelper$StyleSet
                r5.<init>(r9)
                r4.put(r1, r5)
            L_0x00af:
                int r8 = r8 + 1
                goto L_0x005b
            L_0x00b2:
                r6.styles = r0
            L_0x00b4:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.SVGHandler.endElement(java.lang.String, java.lang.String, java.lang.String):void");
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }
    }

    static {
        int i = 0;
        while (true) {
            double[] dArr = pow10;
            if (i < dArr.length) {
                dArr[i] = Math.pow(10.0d, (double) i);
                i++;
            } else {
                return;
            }
        }
    }

    public static class ParserHelper {
        private char current;
        private int n;
        public int pos;
        private CharSequence s;

        public ParserHelper(CharSequence charSequence, int i) {
            this.s = charSequence;
            this.pos = i;
            this.n = charSequence.length();
            this.current = charSequence.charAt(i);
        }

        private char read() {
            int i = this.pos;
            if (i < this.n) {
                this.pos = i + 1;
            }
            int i2 = this.pos;
            if (i2 == this.n) {
                return 0;
            }
            return this.s.charAt(i2);
        }

        public void skipWhitespace() {
            while (true) {
                int i = this.pos;
                if (i < this.n && Character.isWhitespace(this.s.charAt(i))) {
                    advance();
                } else {
                    return;
                }
            }
        }

        public void skipNumberSeparator() {
            while (true) {
                int i = this.pos;
                if (i < this.n) {
                    char charAt = this.s.charAt(i);
                    if (charAt == 9 || charAt == 10 || charAt == ' ' || charAt == ',') {
                        advance();
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        public void advance() {
            this.current = read();
        }

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x002a, code lost:
            r6 = read();
            r0.current = r6;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0030, code lost:
            if (r6 == '.') goto L_0x0055;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0032, code lost:
            if (r6 == 'E') goto L_0x0055;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0034, code lost:
            if (r6 == 'e') goto L_0x0055;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0036, code lost:
            switch(r6) {
                case 48: goto L_0x002a;
                case 49: goto L_0x003a;
                case 50: goto L_0x003a;
                case 51: goto L_0x003a;
                case 52: goto L_0x003a;
                case 53: goto L_0x003a;
                case 54: goto L_0x003a;
                case 55: goto L_0x003a;
                case 56: goto L_0x003a;
                case 57: goto L_0x003a;
                default: goto L_0x0039;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0039, code lost:
            return 0.0f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x003a, code lost:
            r6 = 0;
            r12 = 0;
            r13 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x003d, code lost:
            if (r6 >= 9) goto L_0x0049;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003f, code lost:
            r6 = r6 + 1;
            r13 = (r13 * 10) + (r0.current - '0');
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0049, code lost:
            r12 = r12 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x004b, code lost:
            r14 = read();
            r0.current = r14;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0051, code lost:
            switch(r14) {
                case 48: goto L_0x003d;
                case 49: goto L_0x003d;
                case 50: goto L_0x003d;
                case 51: goto L_0x003d;
                case 52: goto L_0x003d;
                case 53: goto L_0x003d;
                case 54: goto L_0x003d;
                case 55: goto L_0x003d;
                case 56: goto L_0x003d;
                case 57: goto L_0x003d;
                default: goto L_0x0054;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0055, code lost:
            r6 = 0;
            r12 = 0;
            r13 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0058, code lost:
            r14 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:56:0x00c7, code lost:
            r2 = read();
            r0.current = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x00cd, code lost:
            switch(r2) {
                case 48: goto L_0x00c7;
                case 49: goto L_0x00d1;
                case 50: goto L_0x00d1;
                case 51: goto L_0x00d1;
                case 52: goto L_0x00d1;
                case 53: goto L_0x00d1;
                case 54: goto L_0x00d1;
                case 55: goto L_0x00d1;
                case 56: goto L_0x00d1;
                case 57: goto L_0x00d1;
                default: goto L_0x00d0;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00d1, code lost:
            r2 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:60:0x00d3, code lost:
            if (r5 >= 3) goto L_0x00de;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x00d5, code lost:
            r5 = r5 + 1;
            r2 = (r2 * 10) + (r0.current - '0');
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00de, code lost:
            r3 = read();
            r0.current = r3;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x00e4, code lost:
            switch(r3) {
                case 48: goto L_0x00d2;
                case 49: goto L_0x00d2;
                case 50: goto L_0x00d2;
                case 51: goto L_0x00d2;
                case 52: goto L_0x00d2;
                case 53: goto L_0x00d2;
                case 54: goto L_0x00d2;
                case 55: goto L_0x00d2;
                case 56: goto L_0x00d2;
                case 57: goto L_0x00d2;
                default: goto L_0x00e7;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x00e7, code lost:
            r5 = r2;
         */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x005a  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x0064  */
        /* JADX WARNING: Removed duplicated region for block: B:31:0x0072 A[LOOP_START, PHI: r12 
          PHI: (r12v6 int) = (r12v0 int), (r12v7 int) binds: [B:30:0x0070, B:32:0x007a] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:35:0x0080 A[LOOP_START, PHI: r6 r12 r13 
          PHI: (r6v4 int) = (r6v1 int), (r6v5 int) binds: [B:82:0x0080, B:38:0x0093] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r12v3 int) = (r12v2 int), (r12v4 int) binds: [B:82:0x0080, B:38:0x0093] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r13v4 int) = (r13v0 int), (r13v5 int) binds: [B:82:0x0080, B:38:0x0093] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00aa  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x00bd  */
        /* JADX WARNING: Removed duplicated region for block: B:66:0x00ea  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x00ee  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0027 A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public float parseFloat() {
            /*
                r17 = this;
                r0 = r17
                char r1 = r0.current
                r2 = 45
                r3 = 43
                r4 = 1
                r5 = 0
                if (r1 == r3) goto L_0x0012
                if (r1 == r2) goto L_0x0010
                r1 = 1
                goto L_0x0019
            L_0x0010:
                r1 = 0
                goto L_0x0013
            L_0x0012:
                r1 = 1
            L_0x0013:
                char r6 = r17.read()
                r0.current = r6
            L_0x0019:
                char r6 = r0.current
                r7 = 0
                r8 = 101(0x65, float:1.42E-43)
                r9 = 69
                r10 = 46
                r11 = 9
                switch(r6) {
                    case 46: goto L_0x005a;
                    case 47: goto L_0x0027;
                    case 48: goto L_0x002a;
                    case 49: goto L_0x003a;
                    case 50: goto L_0x003a;
                    case 51: goto L_0x003a;
                    case 52: goto L_0x003a;
                    case 53: goto L_0x003a;
                    case 54: goto L_0x003a;
                    case 55: goto L_0x003a;
                    case 56: goto L_0x003a;
                    case 57: goto L_0x003a;
                    default: goto L_0x0027;
                }
            L_0x0027:
                r1 = 2143289344(0x7fCLASSNAME, float:NaN)
                return r1
            L_0x002a:
                char r6 = r17.read()
                r0.current = r6
                if (r6 == r10) goto L_0x0055
                if (r6 == r9) goto L_0x0055
                if (r6 == r8) goto L_0x0055
                switch(r6) {
                    case 48: goto L_0x002a;
                    case 49: goto L_0x003a;
                    case 50: goto L_0x003a;
                    case 51: goto L_0x003a;
                    case 52: goto L_0x003a;
                    case 53: goto L_0x003a;
                    case 54: goto L_0x003a;
                    case 55: goto L_0x003a;
                    case 56: goto L_0x003a;
                    case 57: goto L_0x003a;
                    default: goto L_0x0039;
                }
            L_0x0039:
                return r7
            L_0x003a:
                r6 = 0
                r12 = 0
                r13 = 0
            L_0x003d:
                if (r6 >= r11) goto L_0x0049
                int r6 = r6 + 1
                int r13 = r13 * 10
                char r14 = r0.current
                int r14 = r14 + -48
                int r13 = r13 + r14
                goto L_0x004b
            L_0x0049:
                int r12 = r12 + 1
            L_0x004b:
                char r14 = r17.read()
                r0.current = r14
                switch(r14) {
                    case 48: goto L_0x003d;
                    case 49: goto L_0x003d;
                    case 50: goto L_0x003d;
                    case 51: goto L_0x003d;
                    case 52: goto L_0x003d;
                    case 53: goto L_0x003d;
                    case 54: goto L_0x003d;
                    case 55: goto L_0x003d;
                    case 56: goto L_0x003d;
                    case 57: goto L_0x003d;
                    default: goto L_0x0054;
                }
            L_0x0054:
                goto L_0x0058
            L_0x0055:
                r6 = 0
                r12 = 0
                r13 = 0
            L_0x0058:
                r14 = 1
                goto L_0x005e
            L_0x005a:
                r6 = 0
                r12 = 0
                r13 = 0
                r14 = 0
            L_0x005e:
                char r15 = r0.current
                r16 = 0
                if (r15 != r10) goto L_0x009b
                char r10 = r17.read()
                r0.current = r10
                switch(r10) {
                    case 48: goto L_0x0070;
                    case 49: goto L_0x0080;
                    case 50: goto L_0x0080;
                    case 51: goto L_0x0080;
                    case 52: goto L_0x0080;
                    case 53: goto L_0x0080;
                    case 54: goto L_0x0080;
                    case 55: goto L_0x0080;
                    case 56: goto L_0x0080;
                    case 57: goto L_0x0080;
                    default: goto L_0x006d;
                }
            L_0x006d:
                if (r14 == 0) goto L_0x0097
                goto L_0x009b
            L_0x0070:
                if (r6 != 0) goto L_0x0080
            L_0x0072:
                char r10 = r17.read()
                r0.current = r10
                int r12 = r12 + -1
                switch(r10) {
                    case 48: goto L_0x0072;
                    case 49: goto L_0x0080;
                    case 50: goto L_0x0080;
                    case 51: goto L_0x0080;
                    case 52: goto L_0x0080;
                    case 53: goto L_0x0080;
                    case 54: goto L_0x0080;
                    case 55: goto L_0x0080;
                    case 56: goto L_0x0080;
                    case 57: goto L_0x0080;
                    default: goto L_0x007d;
                }
            L_0x007d:
                if (r14 != 0) goto L_0x009b
                return r7
            L_0x0080:
                if (r6 >= r11) goto L_0x008d
                int r6 = r6 + 1
                int r13 = r13 * 10
                char r7 = r0.current
                int r7 = r7 + -48
                int r13 = r13 + r7
                int r12 = r12 + -1
            L_0x008d:
                char r7 = r17.read()
                r0.current = r7
                switch(r7) {
                    case 48: goto L_0x0080;
                    case 49: goto L_0x0080;
                    case 50: goto L_0x0080;
                    case 51: goto L_0x0080;
                    case 52: goto L_0x0080;
                    case 53: goto L_0x0080;
                    case 54: goto L_0x0080;
                    case 55: goto L_0x0080;
                    case 56: goto L_0x0080;
                    case 57: goto L_0x0080;
                    default: goto L_0x0096;
                }
            L_0x0096:
                goto L_0x009b
            L_0x0097:
                r0.reportUnexpectedCharacterError(r10)
                throw r16
            L_0x009b:
                char r6 = r0.current
                if (r6 == r9) goto L_0x00a2
                if (r6 == r8) goto L_0x00a2
                goto L_0x00e8
            L_0x00a2:
                char r6 = r17.read()
                r0.current = r6
                if (r6 == r3) goto L_0x00b4
                if (r6 == r2) goto L_0x00b3
                switch(r6) {
                    case 48: goto L_0x00c1;
                    case 49: goto L_0x00c1;
                    case 50: goto L_0x00c1;
                    case 51: goto L_0x00c1;
                    case 52: goto L_0x00c1;
                    case 53: goto L_0x00c1;
                    case 54: goto L_0x00c1;
                    case 55: goto L_0x00c1;
                    case 56: goto L_0x00c1;
                    case 57: goto L_0x00c1;
                    default: goto L_0x00af;
                }
            L_0x00af:
                r0.reportUnexpectedCharacterError(r6)
                throw r16
            L_0x00b3:
                r4 = 0
            L_0x00b4:
                char r2 = r17.read()
                r0.current = r2
                switch(r2) {
                    case 48: goto L_0x00c1;
                    case 49: goto L_0x00c1;
                    case 50: goto L_0x00c1;
                    case 51: goto L_0x00c1;
                    case 52: goto L_0x00c1;
                    case 53: goto L_0x00c1;
                    case 54: goto L_0x00c1;
                    case 55: goto L_0x00c1;
                    case 56: goto L_0x00c1;
                    case 57: goto L_0x00c1;
                    default: goto L_0x00bd;
                }
            L_0x00bd:
                r0.reportUnexpectedCharacterError(r2)
                goto L_0x00f4
            L_0x00c1:
                char r2 = r0.current
                switch(r2) {
                    case 48: goto L_0x00c7;
                    case 49: goto L_0x00d1;
                    case 50: goto L_0x00d1;
                    case 51: goto L_0x00d1;
                    case 52: goto L_0x00d1;
                    case 53: goto L_0x00d1;
                    case 54: goto L_0x00d1;
                    case 55: goto L_0x00d1;
                    case 56: goto L_0x00d1;
                    case 57: goto L_0x00d1;
                    default: goto L_0x00c6;
                }
            L_0x00c6:
                goto L_0x00e8
            L_0x00c7:
                char r2 = r17.read()
                r0.current = r2
                switch(r2) {
                    case 48: goto L_0x00c7;
                    case 49: goto L_0x00d1;
                    case 50: goto L_0x00d1;
                    case 51: goto L_0x00d1;
                    case 52: goto L_0x00d1;
                    case 53: goto L_0x00d1;
                    case 54: goto L_0x00d1;
                    case 55: goto L_0x00d1;
                    case 56: goto L_0x00d1;
                    case 57: goto L_0x00d1;
                    default: goto L_0x00d0;
                }
            L_0x00d0:
                goto L_0x00e8
            L_0x00d1:
                r2 = 0
            L_0x00d2:
                r3 = 3
                if (r5 >= r3) goto L_0x00de
                int r5 = r5 + 1
                int r2 = r2 * 10
                char r3 = r0.current
                int r3 = r3 + -48
                int r2 = r2 + r3
            L_0x00de:
                char r3 = r17.read()
                r0.current = r3
                switch(r3) {
                    case 48: goto L_0x00d2;
                    case 49: goto L_0x00d2;
                    case 50: goto L_0x00d2;
                    case 51: goto L_0x00d2;
                    case 52: goto L_0x00d2;
                    case 53: goto L_0x00d2;
                    case 54: goto L_0x00d2;
                    case 55: goto L_0x00d2;
                    case 56: goto L_0x00d2;
                    case 57: goto L_0x00d2;
                    default: goto L_0x00e7;
                }
            L_0x00e7:
                r5 = r2
            L_0x00e8:
                if (r4 != 0) goto L_0x00eb
                int r5 = -r5
            L_0x00eb:
                int r5 = r5 + r12
                if (r1 != 0) goto L_0x00ef
                int r13 = -r13
            L_0x00ef:
                float r1 = r0.buildFloat(r13, r5)
                return r1
            L_0x00f4:
                throw r16
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.ParserHelper.parseFloat():float");
        }

        private void reportUnexpectedCharacterError(char c) {
            throw new RuntimeException("Unexpected char '" + c + "'.");
        }

        public float buildFloat(int i, int i2) {
            double d;
            if (i2 < -125 || i == 0) {
                return 0.0f;
            }
            if (i2 >= 128) {
                return i > 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
            }
            if (i2 == 0) {
                return (float) i;
            }
            if (i >= 67108864) {
                i++;
            }
            double d2 = (double) i;
            double[] access$1000 = SvgHelper.pow10;
            if (i2 > 0) {
                double d3 = access$1000[i2];
                Double.isNaN(d2);
                d = d2 * d3;
            } else {
                double d4 = access$1000[-i2];
                Double.isNaN(d2);
                d = d2 / d4;
            }
            return (float) d;
        }

        public float nextFloat() {
            skipWhitespace();
            float parseFloat = parseFloat();
            skipNumberSeparator();
            return parseFloat;
        }
    }
}

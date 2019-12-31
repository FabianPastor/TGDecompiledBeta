package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import java.util.ArrayList;
import java.util.HashMap;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class SvgHelper {
    private static final double[] pow10 = new double[128];

    private static class NumberParse {
        private int nextCmd;
        private ArrayList<Float> numbers;

        public NumberParse(ArrayList<Float> arrayList, int i) {
            this.numbers = arrayList;
            this.nextCmd = i;
        }

        public int getNextCmd() {
            return this.nextCmd;
        }

        public float getNumber(int i) {
            return ((Float) this.numbers.get(i)).floatValue();
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
            i = this.pos;
            if (i == this.n) {
                return 0;
            }
            return this.s.charAt(i);
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
                }
                return;
            }
        }

        public void advance() {
            this.current = read();
        }

        /* JADX WARNING: Removed duplicated region for block: B:35:0x0078 A:{LOOP_START, PHI: r12 } */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0025  */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x005d  */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x0065  */
        /* JADX WARNING: Removed duplicated region for block: B:48:0x00b1  */
        /* JADX WARNING: Removed duplicated region for block: B:55:0x00c6  */
        /* JADX WARNING: Removed duplicated region for block: B:69:0x00f7  */
        /* JADX WARNING: Removed duplicated region for block: B:72:0x00fb  */
        /* JADX WARNING: Missing block: B:11:0x0028, code skipped:
            r15.current = read();
            r5 = r15.current;
     */
        /* JADX WARNING: Missing block: B:12:0x0030, code skipped:
            if (r5 == '.') goto L_0x005b;
     */
        /* JADX WARNING: Missing block: B:13:0x0032, code skipped:
            if (r5 == 'E') goto L_0x005b;
     */
        /* JADX WARNING: Missing block: B:14:0x0034, code skipped:
            if (r5 == 'e') goto L_0x005b;
     */
        /* JADX WARNING: Missing block: B:15:0x0036, code skipped:
            switch(r5) {
                case 48: goto L_0x0028;
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
        /* JADX WARNING: Missing block: B:16:0x0039, code skipped:
            return 0.0f;
     */
        /* JADX WARNING: Missing block: B:17:0x003a, code skipped:
            r5 = 0;
            r11 = 0;
            r12 = 0;
     */
        /* JADX WARNING: Missing block: B:18:0x003d, code skipped:
            if (r5 >= 9) goto L_0x0049;
     */
        /* JADX WARNING: Missing block: B:19:0x003f, code skipped:
            r5 = r5 + 1;
            r12 = (r12 * 10) + (r15.current - 48);
     */
        /* JADX WARNING: Missing block: B:20:0x0049, code skipped:
            r11 = r11 + 1;
     */
        /* JADX WARNING: Missing block: B:21:0x004b, code skipped:
            r15.current = read();
     */
        /* JADX WARNING: Missing block: B:22:0x0053, code skipped:
            switch(r15.current) {
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
                default: goto L_0x0056;
            };
     */
        /* JADX WARNING: Missing block: B:23:0x0056, code skipped:
            r13 = r12;
            r12 = r11;
            r11 = r5;
            r5 = 1;
     */
        /* JADX WARNING: Missing block: B:24:0x005b, code skipped:
            r5 = 1;
     */
        /* JADX WARNING: Missing block: B:26:0x005e, code skipped:
            r11 = 0;
            r12 = 0;
            r13 = 0;
     */
        /* JADX WARNING: Missing block: B:41:0x0095, code skipped:
            r15.current = read();
     */
        /* JADX WARNING: Missing block: B:59:0x00d0, code skipped:
            r15.current = read();
     */
        /* JADX WARNING: Missing block: B:60:0x00d8, code skipped:
            switch(r15.current) {
                case 48: goto L_0x00d0;
                case 49: goto L_0x00dc;
                case 50: goto L_0x00dc;
                case 51: goto L_0x00dc;
                case 52: goto L_0x00dc;
                case 53: goto L_0x00dc;
                case 54: goto L_0x00dc;
                case 55: goto L_0x00dc;
                case 56: goto L_0x00dc;
                case 57: goto L_0x00dc;
                default: goto L_0x00db;
            };
     */
        /* JADX WARNING: Missing block: B:61:0x00dc, code skipped:
            r1 = 0;
     */
        /* JADX WARNING: Missing block: B:63:0x00de, code skipped:
            if (r4 >= 3) goto L_0x00e9;
     */
        /* JADX WARNING: Missing block: B:64:0x00e0, code skipped:
            r4 = r4 + 1;
            r1 = (r1 * 10) + (r15.current - 48);
     */
        /* JADX WARNING: Missing block: B:65:0x00e9, code skipped:
            r15.current = read();
     */
        /* JADX WARNING: Missing block: B:66:0x00f1, code skipped:
            switch(r15.current) {
                case 48: goto L_0x00dd;
                case 49: goto L_0x00dd;
                case 50: goto L_0x00dd;
                case 51: goto L_0x00dd;
                case 52: goto L_0x00dd;
                case 53: goto L_0x00dd;
                case 54: goto L_0x00dd;
                case 55: goto L_0x00dd;
                case 56: goto L_0x00dd;
                case 57: goto L_0x00dd;
                default: goto L_0x00f4;
            };
     */
        /* JADX WARNING: Missing block: B:67:0x00f4, code skipped:
            r4 = r1;
     */
        public float parseFloat() {
            /*
            r15 = this;
            r0 = r15.current;
            r1 = 45;
            r2 = 43;
            r3 = 1;
            r4 = 0;
            if (r0 == r2) goto L_0x0010;
        L_0x000a:
            if (r0 == r1) goto L_0x000e;
        L_0x000c:
            r0 = 1;
            goto L_0x0017;
        L_0x000e:
            r0 = 0;
            goto L_0x0011;
        L_0x0010:
            r0 = 1;
        L_0x0011:
            r5 = r15.read();
            r15.current = r5;
        L_0x0017:
            r5 = r15.current;
            r6 = 101; // 0x65 float:1.42E-43 double:5.0E-322;
            r7 = 69;
            r8 = 46;
            r9 = 9;
            r10 = 0;
            switch(r5) {
                case 46: goto L_0x005d;
                case 47: goto L_0x0025;
                case 48: goto L_0x0028;
                case 49: goto L_0x003a;
                case 50: goto L_0x003a;
                case 51: goto L_0x003a;
                case 52: goto L_0x003a;
                case 53: goto L_0x003a;
                case 54: goto L_0x003a;
                case 55: goto L_0x003a;
                case 56: goto L_0x003a;
                case 57: goto L_0x003a;
                default: goto L_0x0025;
            };
        L_0x0025:
            r0 = NUM; // 0x7fCLASSNAME float:NaN double:1.058925634E-314;
            return r0;
        L_0x0028:
            r5 = r15.read();
            r15.current = r5;
            r5 = r15.current;
            if (r5 == r8) goto L_0x005b;
        L_0x0032:
            if (r5 == r7) goto L_0x005b;
        L_0x0034:
            if (r5 == r6) goto L_0x005b;
        L_0x0036:
            switch(r5) {
                case 48: goto L_0x0028;
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
        L_0x0039:
            return r10;
        L_0x003a:
            r5 = 0;
            r11 = 0;
            r12 = 0;
        L_0x003d:
            if (r5 >= r9) goto L_0x0049;
        L_0x003f:
            r5 = r5 + 1;
            r12 = r12 * 10;
            r13 = r15.current;
            r13 = r13 + -48;
            r12 = r12 + r13;
            goto L_0x004b;
        L_0x0049:
            r11 = r11 + 1;
        L_0x004b:
            r13 = r15.read();
            r15.current = r13;
            r13 = r15.current;
            switch(r13) {
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
                default: goto L_0x0056;
            };
        L_0x0056:
            r13 = r12;
            r12 = r11;
            r11 = r5;
            r5 = 1;
            goto L_0x0061;
        L_0x005b:
            r5 = 1;
            goto L_0x005e;
        L_0x005d:
            r5 = 0;
        L_0x005e:
            r11 = 0;
            r12 = 0;
            r13 = 0;
        L_0x0061:
            r14 = r15.current;
            if (r14 != r8) goto L_0x00a0;
        L_0x0065:
            r8 = r15.read();
            r15.current = r8;
            r8 = r15.current;
            switch(r8) {
                case 48: goto L_0x0076;
                case 49: goto L_0x0088;
                case 50: goto L_0x0088;
                case 51: goto L_0x0088;
                case 52: goto L_0x0088;
                case 53: goto L_0x0088;
                case 54: goto L_0x0088;
                case 55: goto L_0x0088;
                case 56: goto L_0x0088;
                case 57: goto L_0x0088;
                default: goto L_0x0070;
            };
        L_0x0070:
            if (r5 != 0) goto L_0x00a0;
        L_0x0072:
            r15.reportUnexpectedCharacterError(r8);
            return r10;
        L_0x0076:
            if (r11 != 0) goto L_0x0088;
        L_0x0078:
            r8 = r15.read();
            r15.current = r8;
            r12 = r12 + -1;
            r8 = r15.current;
            switch(r8) {
                case 48: goto L_0x0078;
                case 49: goto L_0x0088;
                case 50: goto L_0x0088;
                case 51: goto L_0x0088;
                case 52: goto L_0x0088;
                case 53: goto L_0x0088;
                case 54: goto L_0x0088;
                case 55: goto L_0x0088;
                case 56: goto L_0x0088;
                case 57: goto L_0x0088;
                default: goto L_0x0085;
            };
        L_0x0085:
            if (r5 != 0) goto L_0x00a0;
        L_0x0087:
            return r10;
        L_0x0088:
            if (r11 >= r9) goto L_0x0095;
        L_0x008a:
            r11 = r11 + 1;
            r13 = r13 * 10;
            r5 = r15.current;
            r5 = r5 + -48;
            r13 = r13 + r5;
            r12 = r12 + -1;
        L_0x0095:
            r5 = r15.read();
            r15.current = r5;
            r5 = r15.current;
            switch(r5) {
                case 48: goto L_0x0088;
                case 49: goto L_0x0088;
                case 50: goto L_0x0088;
                case 51: goto L_0x0088;
                case 52: goto L_0x0088;
                case 53: goto L_0x0088;
                case 54: goto L_0x0088;
                case 55: goto L_0x0088;
                case 56: goto L_0x0088;
                case 57: goto L_0x0088;
                default: goto L_0x00a0;
            };
        L_0x00a0:
            r5 = r15.current;
            if (r5 == r7) goto L_0x00a7;
        L_0x00a4:
            if (r5 == r6) goto L_0x00a7;
        L_0x00a6:
            goto L_0x00f5;
        L_0x00a7:
            r5 = r15.read();
            r15.current = r5;
            r5 = r15.current;
            if (r5 == r2) goto L_0x00bb;
        L_0x00b1:
            if (r5 == r1) goto L_0x00ba;
        L_0x00b3:
            switch(r5) {
                case 48: goto L_0x00ca;
                case 49: goto L_0x00ca;
                case 50: goto L_0x00ca;
                case 51: goto L_0x00ca;
                case 52: goto L_0x00ca;
                case 53: goto L_0x00ca;
                case 54: goto L_0x00ca;
                case 55: goto L_0x00ca;
                case 56: goto L_0x00ca;
                case 57: goto L_0x00ca;
                default: goto L_0x00b6;
            };
        L_0x00b6:
            r15.reportUnexpectedCharacterError(r5);
            return r10;
        L_0x00ba:
            r3 = 0;
        L_0x00bb:
            r1 = r15.read();
            r15.current = r1;
            r1 = r15.current;
            switch(r1) {
                case 48: goto L_0x00ca;
                case 49: goto L_0x00ca;
                case 50: goto L_0x00ca;
                case 51: goto L_0x00ca;
                case 52: goto L_0x00ca;
                case 53: goto L_0x00ca;
                case 54: goto L_0x00ca;
                case 55: goto L_0x00ca;
                case 56: goto L_0x00ca;
                case 57: goto L_0x00ca;
                default: goto L_0x00c6;
            };
        L_0x00c6:
            r15.reportUnexpectedCharacterError(r1);
            return r10;
        L_0x00ca:
            r1 = r15.current;
            switch(r1) {
                case 48: goto L_0x00d0;
                case 49: goto L_0x00dc;
                case 50: goto L_0x00dc;
                case 51: goto L_0x00dc;
                case 52: goto L_0x00dc;
                case 53: goto L_0x00dc;
                case 54: goto L_0x00dc;
                case 55: goto L_0x00dc;
                case 56: goto L_0x00dc;
                case 57: goto L_0x00dc;
                default: goto L_0x00cf;
            };
        L_0x00cf:
            goto L_0x00f5;
        L_0x00d0:
            r1 = r15.read();
            r15.current = r1;
            r1 = r15.current;
            switch(r1) {
                case 48: goto L_0x00d0;
                case 49: goto L_0x00dc;
                case 50: goto L_0x00dc;
                case 51: goto L_0x00dc;
                case 52: goto L_0x00dc;
                case 53: goto L_0x00dc;
                case 54: goto L_0x00dc;
                case 55: goto L_0x00dc;
                case 56: goto L_0x00dc;
                case 57: goto L_0x00dc;
                default: goto L_0x00db;
            };
        L_0x00db:
            goto L_0x00f5;
        L_0x00dc:
            r1 = 0;
        L_0x00dd:
            r2 = 3;
            if (r4 >= r2) goto L_0x00e9;
        L_0x00e0:
            r4 = r4 + 1;
            r1 = r1 * 10;
            r2 = r15.current;
            r2 = r2 + -48;
            r1 = r1 + r2;
        L_0x00e9:
            r2 = r15.read();
            r15.current = r2;
            r2 = r15.current;
            switch(r2) {
                case 48: goto L_0x00dd;
                case 49: goto L_0x00dd;
                case 50: goto L_0x00dd;
                case 51: goto L_0x00dd;
                case 52: goto L_0x00dd;
                case 53: goto L_0x00dd;
                case 54: goto L_0x00dd;
                case 55: goto L_0x00dd;
                case 56: goto L_0x00dd;
                case 57: goto L_0x00dd;
                default: goto L_0x00f4;
            };
        L_0x00f4:
            r4 = r1;
        L_0x00f5:
            if (r3 != 0) goto L_0x00f8;
        L_0x00f7:
            r4 = -r4;
        L_0x00f8:
            r4 = r4 + r12;
            if (r0 != 0) goto L_0x00fc;
        L_0x00fb:
            r13 = -r13;
        L_0x00fc:
            r0 = r15.buildFloat(r13, r4);
            return r0;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper$ParserHelper.parseFloat():float");
        }

        private void reportUnexpectedCharacterError(char c) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unexpected char '");
            stringBuilder.append(c);
            stringBuilder.append("'.");
            throw new RuntimeException(stringBuilder.toString());
        }

        public float buildFloat(int i, int i2) {
            if (i2 < -125 || i == 0) {
                return 0.0f;
            }
            if (i2 >= 128) {
                return i > 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
            } else if (i2 == 0) {
                return (float) i;
            } else {
                if (i >= 67108864) {
                    i++;
                }
                double d = (double) i;
                double[] access$1000 = SvgHelper.pow10;
                double d2;
                if (i2 > 0) {
                    d2 = access$1000[i2];
                    Double.isNaN(d);
                    d *= d2;
                } else {
                    d2 = access$1000[-i2];
                    Double.isNaN(d);
                    d /= d2;
                }
                return (float) d;
            }
        }

        public float nextFloat() {
            skipWhitespace();
            float parseFloat = parseFloat();
            skipNumberSeparator();
            return parseFloat;
        }
    }

    private static class Properties {
        Attributes atts;
        ArrayList<StyleSet> styles;

        private Properties(Attributes attributes, HashMap<String, StyleSet> hashMap) {
            this.atts = attributes;
            String access$200 = SvgHelper.getStringAttr("style", attributes);
            if (access$200 != null) {
                this.styles = new ArrayList();
                this.styles.add(new StyleSet(access$200));
                return;
            }
            String access$2002 = SvgHelper.getStringAttr("class", attributes);
            if (access$2002 != null) {
                this.styles = new ArrayList();
                String[] split = access$2002.split(" ");
                for (String trim : split) {
                    StyleSet styleSet = (StyleSet) hashMap.get(trim.trim());
                    if (styleSet != null) {
                        this.styles.add(styleSet);
                    }
                }
            }
        }

        public String getAttr(String str) {
            ArrayList arrayList = this.styles;
            String str2 = null;
            if (arrayList != null && !arrayList.isEmpty()) {
                int size = this.styles.size();
                for (int i = 0; i < size; i++) {
                    str2 = ((StyleSet) this.styles.get(i)).getStyle(str);
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
            str = getAttr(str);
            if (str == null) {
                return null;
            }
            try {
                str = Integer.valueOf(Integer.parseInt(str.substring(1), 16));
                return str;
            } catch (NumberFormatException unused) {
                return SvgHelper.getColorByName(str);
            }
        }

        public Float getFloat(String str, float f) {
            Float f2 = getFloat(str);
            return f2 == null ? Float.valueOf(f) : f2;
        }

        public Float getFloat(String str) {
            str = getAttr(str);
            if (str == null) {
                return null;
            }
            try {
                return Float.valueOf(Float.parseFloat(str));
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
            this.globalStyles = new HashMap();
            this.desiredWidth = i;
            this.desiredHeight = i2;
            this.whiteOnly = z;
        }

        private boolean doFill(Properties properties) {
            if ("none".equals(properties.getString("display"))) {
                return false;
            }
            String str = "fill";
            String string = properties.getString(str);
            if (string == null || !string.startsWith("url(#")) {
                Integer hex = properties.getHex(str);
                if (hex != null) {
                    doColor(properties, hex, true);
                    this.paint.setStyle(Style.FILL);
                    return true;
                } else if (properties.getString(str) != null || properties.getString("stroke") != null) {
                    return false;
                } else {
                    this.paint.setStyle(Style.FILL);
                    if (this.whiteOnly) {
                        this.paint.setColor(-1);
                    } else {
                        this.paint.setColor(-16777216);
                    }
                    return true;
                }
            }
            string.substring(5, string.length() - 1);
            return false;
        }

        private boolean doStroke(Properties properties) {
            if ("none".equals(properties.getString("display"))) {
                return false;
            }
            Integer hex = properties.getHex("stroke");
            if (hex == null) {
                return false;
            }
            doColor(properties, hex, false);
            Float f = properties.getFloat("stroke-width");
            if (f != null) {
                this.paint.setStrokeWidth(f.floatValue());
            }
            String string = properties.getString("stroke-linecap");
            String str = "round";
            if (str.equals(string)) {
                this.paint.setStrokeCap(Cap.ROUND);
            } else if ("square".equals(string)) {
                this.paint.setStrokeCap(Cap.SQUARE);
            } else if ("butt".equals(string)) {
                this.paint.setStrokeCap(Cap.BUTT);
            }
            String string2 = properties.getString("stroke-linejoin");
            if ("miter".equals(string2)) {
                this.paint.setStrokeJoin(Join.MITER);
            } else if (str.equals(string2)) {
                this.paint.setStrokeJoin(Join.ROUND);
            } else if ("bevel".equals(string2)) {
                this.paint.setStrokeJoin(Join.BEVEL);
            }
            this.paint.setStyle(Style.STROKE);
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
            this.pushed = access$200 != null;
            if (this.pushed) {
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
            str3 = "style";
            if (!this.boundsMode || str2.equals(str3)) {
                Object obj = -1;
                String str4 = "polygon";
                int i = 2;
                switch (str2.hashCode()) {
                    case -1656480802:
                        if (str2.equals("ellipse")) {
                            obj = 8;
                            break;
                        }
                        break;
                    case -1360216880:
                        if (str2.equals("circle")) {
                            obj = 7;
                            break;
                        }
                        break;
                    case -397519558:
                        if (str2.equals(str4)) {
                            obj = 9;
                            break;
                        }
                        break;
                    case 103:
                        if (str2.equals("g")) {
                            obj = 4;
                            break;
                        }
                        break;
                    case 114276:
                        if (str2.equals("svg")) {
                            obj = null;
                            break;
                        }
                        break;
                    case 3079438:
                        if (str2.equals("defs")) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 3321844:
                        if (str2.equals("line")) {
                            obj = 6;
                            break;
                        }
                        break;
                    case 3433509:
                        if (str2.equals("path")) {
                            obj = 11;
                            break;
                        }
                        break;
                    case 3496420:
                        if (str2.equals("rect")) {
                            obj = 5;
                            break;
                        }
                        break;
                    case 109780401:
                        if (str2.equals(str3)) {
                            obj = 3;
                            break;
                        }
                        break;
                    case 561938880:
                        if (str2.equals("polyline")) {
                            obj = 10;
                            break;
                        }
                        break;
                    case 917656469:
                        if (str2.equals("clipPath")) {
                            obj = 2;
                            break;
                        }
                        break;
                }
                str3 = "cy";
                String str5 = "cx";
                String str6 = "height";
                String str7 = "width";
                Float access$600;
                Float access$6002;
                Float access$6003;
                Float access$6004;
                Properties properties;
                Properties properties2;
                switch (obj) {
                    case null:
                        float f;
                        access$600 = SvgHelper.getFloatAttr(str7, attributes);
                        access$6002 = SvgHelper.getFloatAttr(str6, attributes);
                        if (access$600 == null || access$6002 == null) {
                            str3 = SvgHelper.getStringAttr("viewBox", attributes);
                            if (str3 != null) {
                                String[] split = str3.split(" ");
                                Float valueOf = Float.valueOf(Float.parseFloat(split[2]));
                                access$6002 = Float.valueOf(Float.parseFloat(split[3]));
                                access$600 = valueOf;
                            }
                        }
                        if (access$600 == null || r13 == null) {
                            access$600 = Float.valueOf((float) this.desiredWidth);
                            access$6002 = Float.valueOf((float) this.desiredHeight);
                        }
                        int ceil = (int) Math.ceil((double) access$600.floatValue());
                        int ceil2 = (int) Math.ceil((double) access$6002.floatValue());
                        if (ceil == 0 || ceil2 == 0) {
                            ceil = this.desiredWidth;
                            ceil2 = this.desiredHeight;
                        } else {
                            f = (float) ceil;
                            float f2 = (float) ceil2;
                            this.scale = Math.min(((float) this.desiredWidth) / f, ((float) this.desiredHeight) / f2);
                            float f3 = this.scale;
                            ceil = (int) (f * f3);
                            ceil2 = (int) (f2 * f3);
                        }
                        this.bitmap = Bitmap.createBitmap(ceil, ceil2, Config.ARGB_8888);
                        this.bitmap.eraseColor(0);
                        this.canvas = new Canvas(this.bitmap);
                        f = this.scale;
                        if (f != 0.0f) {
                            this.canvas.scale(f, f);
                            break;
                        }
                        break;
                    case 1:
                    case 2:
                        this.boundsMode = true;
                        break;
                    case 3:
                        this.styles = new StringBuilder();
                        break;
                    case 4:
                        if ("bounds".equalsIgnoreCase(SvgHelper.getStringAttr("id", attributes))) {
                            this.boundsMode = true;
                            break;
                        }
                        break;
                    case 5:
                        access$600 = SvgHelper.getFloatAttr("x", attributes);
                        if (access$600 == null) {
                            access$600 = Float.valueOf(0.0f);
                        }
                        access$6002 = SvgHelper.getFloatAttr("y", attributes);
                        if (access$6002 == null) {
                            access$6002 = Float.valueOf(0.0f);
                        }
                        access$6003 = SvgHelper.getFloatAttr(str7, attributes);
                        access$6004 = SvgHelper.getFloatAttr(str6, attributes);
                        pushTransform(attributes);
                        properties = new Properties(attributes, this.globalStyles);
                        if (doFill(properties)) {
                            this.canvas.drawRect(access$600.floatValue(), access$6002.floatValue(), access$6003.floatValue() + access$600.floatValue(), access$6004.floatValue() + access$6002.floatValue(), this.paint);
                        }
                        if (doStroke(properties)) {
                            this.canvas.drawRect(access$600.floatValue(), access$6002.floatValue(), access$600.floatValue() + access$6003.floatValue(), access$6002.floatValue() + access$6004.floatValue(), this.paint);
                        }
                        popTransform();
                        break;
                    case 6:
                        access$600 = SvgHelper.getFloatAttr("x1", attributes);
                        access$6002 = SvgHelper.getFloatAttr("x2", attributes);
                        access$6003 = SvgHelper.getFloatAttr("y1", attributes);
                        access$6004 = SvgHelper.getFloatAttr("y2", attributes);
                        if (doStroke(new Properties(attributes, this.globalStyles))) {
                            pushTransform(attributes);
                            this.canvas.drawLine(access$600.floatValue(), access$6003.floatValue(), access$6002.floatValue(), access$6004.floatValue(), this.paint);
                            popTransform();
                            break;
                        }
                        break;
                    case 7:
                        access$600 = SvgHelper.getFloatAttr(str5, attributes);
                        access$6002 = SvgHelper.getFloatAttr(str3, attributes);
                        access$6003 = SvgHelper.getFloatAttr("r", attributes);
                        if (!(access$600 == null || access$6002 == null || access$6003 == null)) {
                            pushTransform(attributes);
                            properties2 = new Properties(attributes, this.globalStyles);
                            if (doFill(properties2)) {
                                this.canvas.drawCircle(access$600.floatValue(), access$6002.floatValue(), access$6003.floatValue(), this.paint);
                            }
                            if (doStroke(properties2)) {
                                this.canvas.drawCircle(access$600.floatValue(), access$6002.floatValue(), access$6003.floatValue(), this.paint);
                            }
                            popTransform();
                            break;
                        }
                    case 8:
                        access$600 = SvgHelper.getFloatAttr(str5, attributes);
                        access$6002 = SvgHelper.getFloatAttr(str3, attributes);
                        access$6003 = SvgHelper.getFloatAttr("rx", attributes);
                        access$6004 = SvgHelper.getFloatAttr("ry", attributes);
                        if (!(access$600 == null || access$6002 == null || access$6003 == null || access$6004 == null)) {
                            pushTransform(attributes);
                            properties = new Properties(attributes, this.globalStyles);
                            this.rect.set(access$600.floatValue() - access$6003.floatValue(), access$6002.floatValue() - access$6004.floatValue(), access$600.floatValue() + access$6003.floatValue(), access$6002.floatValue() + access$6004.floatValue());
                            if (doFill(properties)) {
                                this.canvas.drawOval(this.rect, this.paint);
                            }
                            if (doStroke(properties)) {
                                this.canvas.drawOval(this.rect, this.paint);
                            }
                            popTransform();
                            break;
                        }
                    case 9:
                    case 10:
                        NumberParse access$800 = SvgHelper.getNumberParseAttr("points", attributes);
                        if (access$800 != null) {
                            Path path = new Path();
                            ArrayList access$100 = access$800.numbers;
                            if (access$100.size() > 1) {
                                pushTransform(attributes);
                                properties2 = new Properties(attributes, this.globalStyles);
                                path.moveTo(((Float) access$100.get(0)).floatValue(), ((Float) access$100.get(1)).floatValue());
                                while (i < access$100.size()) {
                                    path.lineTo(((Float) access$100.get(i)).floatValue(), ((Float) access$100.get(i + 1)).floatValue());
                                    i += 2;
                                }
                                if (str2.equals(str4)) {
                                    path.close();
                                }
                                if (doFill(properties2)) {
                                    this.canvas.drawPath(path, this.paint);
                                }
                                if (doStroke(properties2)) {
                                    this.canvas.drawPath(path, this.paint);
                                }
                                popTransform();
                                break;
                            }
                        }
                        break;
                    case 11:
                        Path access$900 = SvgHelper.doPath(SvgHelper.getStringAttr("d", attributes));
                        pushTransform(attributes);
                        Properties properties3 = new Properties(attributes, this.globalStyles);
                        if (doFill(properties3)) {
                            this.canvas.drawPath(access$900, this.paint);
                        }
                        if (doStroke(properties3)) {
                            this.canvas.drawPath(access$900, this.paint);
                        }
                        popTransform();
                        break;
                }
            }
        }

        public void characters(char[] cArr, int i, int i2) {
            StringBuilder stringBuilder = this.styles;
            if (stringBuilder != null) {
                stringBuilder.append(cArr, i, i2);
            }
        }

        public void endElement(java.lang.String r7, java.lang.String r8, java.lang.String r9) {
            /*
            r6 = this;
            r7 = r8.hashCode();
            r9 = 4;
            r0 = 3;
            r1 = 2;
            r2 = 0;
            r3 = 1;
            switch(r7) {
                case 103: goto L_0x0035;
                case 114276: goto L_0x002b;
                case 3079438: goto L_0x0021;
                case 109780401: goto L_0x0017;
                case 917656469: goto L_0x000d;
                default: goto L_0x000c;
            };
        L_0x000c:
            goto L_0x003f;
        L_0x000d:
            r7 = "clipPath";
            r7 = r8.equals(r7);
            if (r7 == 0) goto L_0x003f;
        L_0x0015:
            r7 = 4;
            goto L_0x0040;
        L_0x0017:
            r7 = "style";
            r7 = r8.equals(r7);
            if (r7 == 0) goto L_0x003f;
        L_0x001f:
            r7 = 0;
            goto L_0x0040;
        L_0x0021:
            r7 = "defs";
            r7 = r8.equals(r7);
            if (r7 == 0) goto L_0x003f;
        L_0x0029:
            r7 = 3;
            goto L_0x0040;
        L_0x002b:
            r7 = "svg";
            r7 = r8.equals(r7);
            if (r7 == 0) goto L_0x003f;
        L_0x0033:
            r7 = 1;
            goto L_0x0040;
        L_0x0035:
            r7 = "g";
            r7 = r8.equals(r7);
            if (r7 == 0) goto L_0x003f;
        L_0x003d:
            r7 = 2;
            goto L_0x0040;
        L_0x003f:
            r7 = -1;
        L_0x0040:
            if (r7 == 0) goto L_0x004e;
        L_0x0042:
            if (r7 == r3) goto L_0x00b6;
        L_0x0044:
            if (r7 == r1) goto L_0x004b;
        L_0x0046:
            if (r7 == r0) goto L_0x004b;
        L_0x0048:
            if (r7 == r9) goto L_0x004b;
        L_0x004a:
            goto L_0x00b6;
        L_0x004b:
            r6.boundsMode = r2;
            goto L_0x00b6;
        L_0x004e:
            r7 = r6.styles;
            if (r7 == 0) goto L_0x00b6;
        L_0x0052:
            r7 = r7.toString();
            r8 = "\\}";
            r7 = r7.split(r8);
            r8 = 0;
        L_0x005d:
            r9 = r7.length;
            r0 = 0;
            if (r8 >= r9) goto L_0x00b4;
        L_0x0061:
            r9 = r7[r8];
            r9 = r9.trim();
            r1 = "";
            r4 = "\t";
            r9 = r9.replace(r4, r1);
            r4 = "\n";
            r9 = r9.replace(r4, r1);
            r7[r8] = r9;
            r9 = r7[r8];
            r9 = r9.length();
            if (r9 == 0) goto L_0x00b1;
        L_0x007f:
            r9 = r7[r8];
            r9 = r9.charAt(r2);
            r1 = 46;
            if (r9 == r1) goto L_0x008a;
        L_0x0089:
            goto L_0x00b1;
        L_0x008a:
            r9 = r7[r8];
            r1 = 123; // 0x7b float:1.72E-43 double:6.1E-322;
            r9 = r9.indexOf(r1);
            if (r9 >= 0) goto L_0x0095;
        L_0x0094:
            goto L_0x00b1;
        L_0x0095:
            r1 = r7[r8];
            r1 = r1.substring(r3, r9);
            r1 = r1.trim();
            r4 = r7[r8];
            r9 = r9 + 1;
            r9 = r4.substring(r9);
            r4 = r6.globalStyles;
            r5 = new org.telegram.ui.Components.SvgHelper$StyleSet;
            r5.<init>(r9);
            r4.put(r1, r5);
        L_0x00b1:
            r8 = r8 + 1;
            goto L_0x005d;
        L_0x00b4:
            r6.styles = r0;
        L_0x00b6:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper$SVGHandler.endElement(java.lang.String, java.lang.String, java.lang.String):void");
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }
    }

    private static class StyleSet {
        HashMap<String, String> styleMap;

        private StyleSet(StyleSet styleSet) {
            this.styleMap = new HashMap();
            this.styleMap.putAll(styleSet.styleMap);
        }

        private StyleSet(String str) {
            this.styleMap = new HashMap();
            for (String split : str.split(";")) {
                String[] split2 = split.split(":");
                if (split2.length == 2) {
                    this.styleMap.put(split2[0].trim(), split2[1].trim());
                }
            }
        }

        public String getStyle(String str) {
            return (String) this.styleMap.get(str);
        }
    }

    private static void drawArc(Path path, float f, float f2, float f3, float f4, float f5, float f6, float f7, int i, int i2) {
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0030 */
    /* JADX WARNING: Missing block: B:13:?, code skipped:
            r1.close();
     */
    public static android.graphics.Bitmap getBitmap(java.io.File r3, int r4, int r5, boolean r6) {
        /*
        r0 = 0;
        r1 = new java.io.FileInputStream;	 Catch:{ Exception -> 0x0031 }
        r1.<init>(r3);	 Catch:{ Exception -> 0x0031 }
        r3 = javax.xml.parsers.SAXParserFactory.newInstance();	 Catch:{ all -> 0x002a }
        r3 = r3.newSAXParser();	 Catch:{ all -> 0x002a }
        r3 = r3.getXMLReader();	 Catch:{ all -> 0x002a }
        r2 = new org.telegram.ui.Components.SvgHelper$SVGHandler;	 Catch:{ all -> 0x002a }
        r2.<init>(r4, r5, r6);	 Catch:{ all -> 0x002a }
        r3.setContentHandler(r2);	 Catch:{ all -> 0x002a }
        r4 = new org.xml.sax.InputSource;	 Catch:{ all -> 0x002a }
        r4.<init>(r1);	 Catch:{ all -> 0x002a }
        r3.parse(r4);	 Catch:{ all -> 0x002a }
        r3 = r2.getBitmap();	 Catch:{ all -> 0x002a }
        r1.close();	 Catch:{ Exception -> 0x0031 }
        return r3;
    L_0x002a:
        r3 = move-exception;
        throw r3;	 Catch:{ all -> 0x002c }
    L_0x002c:
        r3 = move-exception;
        r1.close();	 Catch:{ all -> 0x0030 }
    L_0x0030:
        throw r3;	 Catch:{ Exception -> 0x0031 }
    L_0x0031:
        r3 = move-exception;
        org.telegram.messenger.FileLog.e(r3);
        throw r0;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.getBitmap(java.io.File, int, int, boolean):android.graphics.Bitmap");
    }

    private static NumberParse parseNumbers(String str) {
        int length = str.length();
        ArrayList arrayList = new ArrayList();
        int i = 1;
        int i2 = 0;
        Object obj = null;
        while (i < length) {
            if (obj == null) {
                char charAt = str.charAt(i);
                switch (charAt) {
                    case 9:
                    case 10:
                    case ' ':
                    case ',':
                    case '-':
                        if (charAt != '-' || str.charAt(i - 1) != 'e') {
                            String substring = str.substring(i2, i);
                            if (substring.trim().length() <= 0) {
                                i2++;
                                break;
                            }
                            arrayList.add(Float.valueOf(Float.parseFloat(substring)));
                            if (charAt != '-') {
                                i2 = i + 1;
                                obj = 1;
                                break;
                            }
                            i2 = i;
                            break;
                        }
                        break;
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
                        str = str.substring(i2, i);
                        if (str.trim().length() > 0) {
                            arrayList.add(Float.valueOf(Float.parseFloat(str)));
                        }
                        return new NumberParse(arrayList, i);
                    default:
                        break;
                }
            }
            obj = null;
            i++;
        }
        String substring2 = str.substring(i2);
        if (substring2.length() > 0) {
            try {
                arrayList.add(Float.valueOf(Float.parseFloat(substring2)));
            } catch (NumberFormatException unused) {
            }
            i2 = str.length();
        }
        return new NumberParse(arrayList, i2);
    }

    private static Matrix parseTransform(String str) {
        float f = 0.0f;
        Matrix matrix;
        NumberParse parseNumbers;
        float floatValue;
        Matrix matrix2;
        float floatValue2;
        if (str.startsWith("matrix(")) {
            if (parseNumbers(str.substring(7)).numbers.size() == 6) {
                matrix = new Matrix();
                matrix.setValues(new float[]{((Float) parseNumbers.numbers.get(0)).floatValue(), ((Float) parseNumbers.numbers.get(2)).floatValue(), ((Float) parseNumbers.numbers.get(4)).floatValue(), ((Float) parseNumbers.numbers.get(1)).floatValue(), ((Float) parseNumbers.numbers.get(3)).floatValue(), ((Float) parseNumbers.numbers.get(5)).floatValue(), 0.0f, 0.0f, 1.0f});
                return matrix;
            }
        } else if (str.startsWith("translate(")) {
            parseNumbers = parseNumbers(str.substring(10));
            if (parseNumbers.numbers.size() > 0) {
                floatValue = ((Float) parseNumbers.numbers.get(0)).floatValue();
                if (parseNumbers.numbers.size() > 1) {
                    f = ((Float) parseNumbers.numbers.get(1)).floatValue();
                }
                matrix2 = new Matrix();
                matrix2.postTranslate(floatValue, f);
                return matrix2;
            }
        } else if (str.startsWith("scale(")) {
            parseNumbers = parseNumbers(str.substring(6));
            if (parseNumbers.numbers.size() > 0) {
                floatValue = ((Float) parseNumbers.numbers.get(0)).floatValue();
                if (parseNumbers.numbers.size() > 1) {
                    f = ((Float) parseNumbers.numbers.get(1)).floatValue();
                }
                matrix2 = new Matrix();
                matrix2.postScale(floatValue, f);
                return matrix2;
            }
        } else if (str.startsWith("skewX(")) {
            parseNumbers = parseNumbers(str.substring(6));
            if (parseNumbers.numbers.size() > 0) {
                floatValue2 = ((Float) parseNumbers.numbers.get(0)).floatValue();
                matrix = new Matrix();
                matrix.postSkew((float) Math.tan((double) floatValue2), 0.0f);
                return matrix;
            }
        } else if (str.startsWith("skewY(")) {
            parseNumbers = parseNumbers(str.substring(6));
            if (parseNumbers.numbers.size() > 0) {
                floatValue2 = ((Float) parseNumbers.numbers.get(0)).floatValue();
                matrix = new Matrix();
                matrix.postSkew(0.0f, (float) Math.tan((double) floatValue2));
                return matrix;
            }
        } else if (str.startsWith("rotate(")) {
            parseNumbers = parseNumbers(str.substring(7));
            if (parseNumbers.numbers.size() > 0) {
                floatValue = ((Float) parseNumbers.numbers.get(0)).floatValue();
                if (parseNumbers.numbers.size() > 2) {
                    f = ((Float) parseNumbers.numbers.get(1)).floatValue();
                    floatValue2 = ((Float) parseNumbers.numbers.get(2)).floatValue();
                } else {
                    floatValue2 = 0.0f;
                }
                Matrix matrix3 = new Matrix();
                matrix3.postTranslate(f, floatValue2);
                matrix3.postRotate(floatValue);
                matrix3.postTranslate(-f, -floatValue2);
                return matrix3;
            }
        }
        return null;
    }

    /* JADX WARNING: Removed duplicated region for block: B:23:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0148  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x018a A:{SYNTHETIC} */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x005f  */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0148  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0109  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00f5  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00dd  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00c5  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0087  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x0186  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x018a A:{SYNTHETIC} */
    /* JADX WARNING: Missing block: B:41:0x00ea, code skipped:
            r5 = r5 + r4;
            r6 = r6 + r8;
     */
    private static android.graphics.Path doPath(java.lang.String r24) {
        /*
        r0 = r24;
        r1 = r24.length();
        r2 = new org.telegram.ui.Components.SvgHelper$ParserHelper;
        r3 = 0;
        r2.<init>(r0, r3);
        r2.skipWhitespace();
        r14 = new android.graphics.Path;
        r14.<init>();
        r15 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        r12 = 0;
        r13 = 0;
        r16 = 0;
        r17 = 0;
    L_0x001e:
        r7 = r2.pos;
        if (r7 >= r1) goto L_0x0191;
    L_0x0022:
        r7 = r0.charAt(r7);
        r8 = 43;
        r9 = 108; // 0x6c float:1.51E-43 double:5.34E-322;
        r10 = 99;
        r11 = 109; // 0x6d float:1.53E-43 double:5.4E-322;
        if (r7 == r8) goto L_0x0038;
    L_0x0030:
        r8 = 45;
        if (r7 == r8) goto L_0x0038;
    L_0x0034:
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
        };
    L_0x0037:
        goto L_0x004d;
    L_0x0038:
        if (r4 == r11) goto L_0x0055;
    L_0x003a:
        r8 = 77;
        if (r4 != r8) goto L_0x003f;
    L_0x003e:
        goto L_0x0055;
    L_0x003f:
        if (r4 == r10) goto L_0x0053;
    L_0x0041:
        r8 = 67;
        if (r4 != r8) goto L_0x0046;
    L_0x0045:
        goto L_0x0053;
    L_0x0046:
        if (r4 == r9) goto L_0x0053;
    L_0x0048:
        r8 = 76;
        if (r4 != r8) goto L_0x004d;
    L_0x004c:
        goto L_0x0053;
    L_0x004d:
        r2.advance();
    L_0x0050:
        r18 = r7;
        goto L_0x005a;
    L_0x0053:
        r7 = r4;
        goto L_0x0050;
    L_0x0055:
        r7 = r4 + -1;
        r7 = (char) r7;
        r18 = r4;
    L_0x005a:
        r19 = 1;
        switch(r7) {
            case 65: goto L_0x0148;
            case 67: goto L_0x0109;
            case 72: goto L_0x00f5;
            case 76: goto L_0x00dd;
            case 77: goto L_0x00c5;
            case 83: goto L_0x0087;
            case 86: goto L_0x0075;
            case 90: goto L_0x0067;
            case 97: goto L_0x0148;
            case 99: goto L_0x0109;
            case 104: goto L_0x00f5;
            case 108: goto L_0x00dd;
            case 109: goto L_0x00c5;
            case 115: goto L_0x0087;
            case 118: goto L_0x0075;
            case 122: goto L_0x0067;
            default: goto L_0x005f;
        };
    L_0x005f:
        r23 = r12;
        r22 = r13;
    L_0x0063:
        r19 = 0;
        goto L_0x0184;
    L_0x0067:
        r14.close();
        r14.moveTo(r13, r12);
        r6 = r12;
        r17 = r6;
        r5 = r13;
        r16 = r5;
        goto L_0x0184;
    L_0x0075:
        r4 = r2.nextFloat();
        r8 = 118; // 0x76 float:1.65E-43 double:5.83E-322;
        if (r7 != r8) goto L_0x0082;
    L_0x007d:
        r14.rLineTo(r15, r4);
        r6 = r6 + r4;
        goto L_0x0063;
    L_0x0082:
        r14.lineTo(r5, r4);
        r6 = r4;
        goto L_0x0063;
    L_0x0087:
        r4 = r2.nextFloat();
        r8 = r2.nextFloat();
        r9 = r2.nextFloat();
        r10 = r2.nextFloat();
        r11 = 115; // 0x73 float:1.61E-43 double:5.7E-322;
        if (r7 != r11) goto L_0x009f;
    L_0x009b:
        r4 = r4 + r5;
        r9 = r9 + r5;
        r8 = r8 + r6;
        r10 = r10 + r6;
    L_0x009f:
        r11 = r4;
        r20 = r8;
        r21 = r9;
        r22 = r10;
        r4 = NUM; // 0x40000000 float:2.0 double:5.304989477E-315;
        r5 = r5 * r4;
        r5 = r5 - r16;
        r6 = r6 * r4;
        r6 = r6 - r17;
        r4 = r14;
        r7 = r11;
        r8 = r20;
        r9 = r21;
        r10 = r22;
        r4.cubicTo(r5, r6, r7, r8, r9, r10);
        r16 = r11;
        r17 = r20;
        r5 = r21;
        r6 = r22;
        goto L_0x0184;
    L_0x00c5:
        r4 = r2.nextFloat();
        r8 = r2.nextFloat();
        if (r7 != r11) goto L_0x00d5;
    L_0x00cf:
        r13 = r13 + r4;
        r12 = r12 + r8;
        r14.rMoveTo(r4, r8);
        goto L_0x00ea;
    L_0x00d5:
        r14.moveTo(r4, r8);
        r5 = r4;
        r13 = r5;
        r6 = r8;
        r12 = r6;
        goto L_0x0063;
    L_0x00dd:
        r4 = r2.nextFloat();
        r8 = r2.nextFloat();
        if (r7 != r9) goto L_0x00ee;
    L_0x00e7:
        r14.rLineTo(r4, r8);
    L_0x00ea:
        r5 = r5 + r4;
        r6 = r6 + r8;
        goto L_0x0063;
    L_0x00ee:
        r14.lineTo(r4, r8);
        r5 = r4;
        r6 = r8;
        goto L_0x0063;
    L_0x00f5:
        r4 = r2.nextFloat();
        r8 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        if (r7 != r8) goto L_0x0103;
    L_0x00fd:
        r14.rLineTo(r4, r15);
        r5 = r5 + r4;
        goto L_0x0063;
    L_0x0103:
        r14.lineTo(r4, r6);
        r5 = r4;
        goto L_0x0063;
    L_0x0109:
        r4 = r2.nextFloat();
        r8 = r2.nextFloat();
        r9 = r2.nextFloat();
        r11 = r2.nextFloat();
        r16 = r2.nextFloat();
        r17 = r2.nextFloat();
        if (r7 != r10) goto L_0x012b;
    L_0x0123:
        r4 = r4 + r5;
        r9 = r9 + r5;
        r16 = r16 + r5;
        r8 = r8 + r6;
        r11 = r11 + r6;
        r17 = r17 + r6;
    L_0x012b:
        r5 = r4;
        r6 = r8;
        r20 = r17;
        r17 = r16;
        r16 = r11;
        r11 = r9;
        r4 = r14;
        r7 = r11;
        r8 = r16;
        r9 = r17;
        r10 = r20;
        r4.cubicTo(r5, r6, r7, r8, r9, r10);
        r5 = r17;
        r6 = r20;
        r17 = r16;
        r16 = r11;
        goto L_0x0184;
    L_0x0148:
        r9 = r2.nextFloat();
        r10 = r2.nextFloat();
        r11 = r2.nextFloat();
        r4 = r2.nextFloat();
        r8 = (int) r4;
        r4 = r2.nextFloat();
        r7 = (int) r4;
        r19 = r2.nextFloat();
        r20 = r2.nextFloat();
        r4 = r14;
        r21 = r7;
        r7 = r19;
        r22 = r8;
        r8 = r20;
        r23 = r12;
        r12 = r22;
        r22 = r13;
        r13 = r21;
        drawArc(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r5 = r19;
        r6 = r20;
        r13 = r22;
        r12 = r23;
        goto L_0x0063;
    L_0x0184:
        if (r19 != 0) goto L_0x018a;
    L_0x0186:
        r16 = r5;
        r17 = r6;
    L_0x018a:
        r2.skipWhitespace();
        r4 = r18;
        goto L_0x001e;
    L_0x0191:
        return r14;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.doPath(java.lang.String):android.graphics.Path");
    }

    private static NumberParse getNumberParseAttr(String str, Attributes attributes) {
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            if (attributes.getLocalName(i).equals(str)) {
                return parseNumbers(attributes.getValue(i));
            }
        }
        return null;
    }

    private static String getStringAttr(String str, Attributes attributes) {
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            if (attributes.getLocalName(i).equals(str)) {
                return attributes.getValue(i);
            }
        }
        return null;
    }

    private static Float getFloatAttr(String str, Attributes attributes) {
        return getFloatAttr(str, attributes, null);
    }

    private static Float getFloatAttr(String str, Attributes attributes, Float f) {
        str = getStringAttr(str, attributes);
        if (str == null) {
            return f;
        }
        if (str.endsWith("px")) {
            str = str.substring(0, str.length() - 2);
        } else if (str.endsWith("mm")) {
            return null;
        }
        return Float.valueOf(Float.parseFloat(str));
    }

    private static Integer getHexAttr(String str, Attributes attributes) {
        str = getStringAttr(str, attributes);
        if (str == null) {
            return null;
        }
        try {
            str = Integer.valueOf(Integer.parseInt(str.substring(1), 16));
            return str;
        } catch (NumberFormatException unused) {
            return getColorByName(str);
        }
    }

    private static java.lang.Integer getColorByName(java.lang.String r2) {
        /*
        r2 = r2.toLowerCase();
        r0 = r2.hashCode();
        r1 = -1;
        switch(r0) {
            case -734239628: goto L_0x005f;
            case 112785: goto L_0x0055;
            case 3027034: goto L_0x004b;
            case 3068707: goto L_0x0041;
            case 3181155: goto L_0x0037;
            case 93818879: goto L_0x002d;
            case 98619139: goto L_0x0023;
            case 113101865: goto L_0x0017;
            case 828922025: goto L_0x000d;
            default: goto L_0x000c;
        };
    L_0x000c:
        goto L_0x006a;
    L_0x000d:
        r0 = "magenta";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x0015:
        r2 = 7;
        goto L_0x006b;
    L_0x0017:
        r0 = "white";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x0020:
        r2 = 8;
        goto L_0x006b;
    L_0x0023:
        r0 = "green";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x002b:
        r2 = 3;
        goto L_0x006b;
    L_0x002d:
        r0 = "black";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x0035:
        r2 = 0;
        goto L_0x006b;
    L_0x0037:
        r0 = "gray";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x003f:
        r2 = 1;
        goto L_0x006b;
    L_0x0041:
        r0 = "cyan";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x0049:
        r2 = 6;
        goto L_0x006b;
    L_0x004b:
        r0 = "blue";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x0053:
        r2 = 4;
        goto L_0x006b;
    L_0x0055:
        r0 = "red";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x005d:
        r2 = 2;
        goto L_0x006b;
    L_0x005f:
        r0 = "yellow";
        r2 = r2.equals(r0);
        if (r2 == 0) goto L_0x006a;
    L_0x0068:
        r2 = 5;
        goto L_0x006b;
    L_0x006a:
        r2 = -1;
    L_0x006b:
        switch(r2) {
            case 0: goto L_0x00ab;
            case 1: goto L_0x00a3;
            case 2: goto L_0x009c;
            case 3: goto L_0x0094;
            case 4: goto L_0x008c;
            case 5: goto L_0x0085;
            case 6: goto L_0x007d;
            case 7: goto L_0x0075;
            case 8: goto L_0x0070;
            default: goto L_0x006e;
        };
    L_0x006e:
        r2 = 0;
        return r2;
    L_0x0070:
        r2 = java.lang.Integer.valueOf(r1);
        return r2;
    L_0x0075:
        r2 = -65281; // 0xfffffffffffvar_ff float:NaN double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
    L_0x007d:
        r2 = -16711681; // 0xfffffffffvar_ffff float:-1.714704E38 double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
    L_0x0085:
        r2 = -256; // 0xfffffffffffffvar_ float:NaN double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
    L_0x008c:
        r2 = -16776961; // 0xfffffffffvar_ff float:-1.7014636E38 double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
    L_0x0094:
        r2 = -16711936; // 0xfffffffffvar_fvar_ float:-1.7146522E38 double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
    L_0x009c:
        r2 = -65536; // 0xfffffffffffvar_ float:NaN double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
    L_0x00a3:
        r2 = -7829368; // 0xfffffffffvar_ float:NaN double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
    L_0x00ab:
        r2 = -16777216; // 0xfffffffffvar_ float:-1.7014118E38 double:NaN;
        r2 = java.lang.Integer.valueOf(r2);
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.SvgHelper.getColorByName(java.lang.String):java.lang.Integer");
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
}

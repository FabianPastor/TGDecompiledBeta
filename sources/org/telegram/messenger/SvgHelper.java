package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParserFactory;
import org.telegram.ui.ActionBar.Theme;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SvgHelper {
    /* access modifiers changed from: private */
    public static final double[] pow10 = new double[128];

    private static void drawArc(Path path, float f, float f2, float f3, float f4, float f5, float f6, float f7, int i, int i2) {
    }

    private static class Line {
        float x1;
        float x2;
        float y1;
        float y2;

        public Line(float f, float f2, float f3, float f4) {
            this.x1 = f;
            this.y1 = f2;
            this.x2 = f3;
            this.y2 = f4;
        }
    }

    private static class Circle {
        float rad;
        float x1;
        float y1;

        public Circle(float f, float f2, float f3) {
            this.x1 = f;
            this.y1 = f2;
            this.rad = f3;
        }
    }

    private static class Oval {
        RectF rect;

        public Oval(RectF rectF) {
            this.rect = rectF;
        }
    }

    public static class SvgDrawable extends Drawable {
        private static float gradientWidth;
        private static long lastUpdateTime;
        private static int[] parentPosition = new int[2];
        private static WeakReference<Drawable> shiftDrawable;
        /* access modifiers changed from: private */
        public static Runnable shiftRunnable;
        private static float totalTranslation;
        private LinearGradient backgroundGradient;
        /* access modifiers changed from: private */
        public ArrayList<Object> commands = new ArrayList<>();
        /* access modifiers changed from: private */
        public int height;
        /* access modifiers changed from: private */
        public HashMap<Object, Paint> paints = new HashMap<>();
        private ImageReceiver parentImageReceiver;
        private LinearGradient placeholderGradient;
        private Matrix placeholderMatrix;
        /* access modifiers changed from: private */
        public int width;

        public int getOpacity() {
            return -2;
        }

        public void setAlpha(int i) {
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            float max = Math.max(((float) bounds.width()) / ((float) this.width), ((float) bounds.height()) / ((float) this.height));
            canvas.save();
            canvas.translate((float) bounds.left, (float) bounds.top);
            canvas.scale(max, max);
            int size = this.commands.size();
            for (int i = 0; i < size; i++) {
                Object obj = this.commands.get(i);
                if (obj instanceof Matrix) {
                    canvas.save();
                    canvas.concat((Matrix) obj);
                } else if (obj == null) {
                    canvas.restore();
                } else {
                    Paint paint = this.paints.get(obj);
                    if (obj instanceof Path) {
                        canvas.drawPath((Path) obj, paint);
                    } else if (obj instanceof Rect) {
                        canvas.drawRect((Rect) obj, paint);
                    } else if (obj instanceof RectF) {
                        canvas.drawRect((RectF) obj, paint);
                    } else if (obj instanceof Line) {
                        Line line = (Line) obj;
                        canvas.drawLine(line.x1, line.y1, line.x2, line.y2, paint);
                    } else if (obj instanceof Circle) {
                        Circle circle = (Circle) obj;
                        canvas.drawCircle(circle.x1, circle.y1, circle.rad, paint);
                    } else if (obj instanceof Oval) {
                        canvas.drawOval(((Oval) obj).rect, paint);
                    }
                }
            }
            canvas.restore();
            if (this.placeholderGradient != null) {
                if (shiftRunnable == null || shiftDrawable.get() == this) {
                    long elapsedRealtime = SystemClock.elapsedRealtime();
                    long abs = Math.abs(lastUpdateTime - elapsedRealtime);
                    if (abs > 17) {
                        abs = 16;
                    }
                    lastUpdateTime = elapsedRealtime;
                    totalTranslation += (((float) abs) * gradientWidth) / 1800.0f;
                    while (true) {
                        float f = totalTranslation;
                        float f2 = gradientWidth;
                        if (f < f2 / 2.0f) {
                            break;
                        }
                        totalTranslation = f - f2;
                    }
                    shiftDrawable = new WeakReference<>(this);
                    Runnable runnable = shiftRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    $$Lambda$SvgHelper$SvgDrawable$V3AQlaeus1WjtJE7XfMxnsHItw r14 = $$Lambda$SvgHelper$SvgDrawable$V3AQlaeus1WjtJE7XfMxnsHItw.INSTANCE;
                    shiftRunnable = r14;
                    AndroidUtilities.runOnUIThread(r14, (long) (((int) (1000.0f / AndroidUtilities.screenRefreshRate)) - 1));
                }
                ImageReceiver imageReceiver = this.parentImageReceiver;
                if (imageReceiver != null) {
                    imageReceiver.getParentPosition(parentPosition);
                }
                this.placeholderMatrix.reset();
                this.placeholderMatrix.postTranslate((((float) (-parentPosition[0])) + totalTranslation) - ((float) bounds.left), 0.0f);
                float f3 = 1.0f / max;
                this.placeholderMatrix.postScale(f3, f3);
                this.placeholderGradient.setLocalMatrix(this.placeholderMatrix);
                ImageReceiver imageReceiver2 = this.parentImageReceiver;
                if (imageReceiver2 != null) {
                    imageReceiver2.invalidate();
                }
            }
        }

        /* access modifiers changed from: private */
        public void addCommand(Object obj, Paint paint) {
            this.commands.add(obj);
            this.paints.put(obj, new Paint(paint));
        }

        /* access modifiers changed from: private */
        public void addCommand(Object obj) {
            this.commands.add(obj);
        }

        public void setParent(ImageReceiver imageReceiver) {
            this.parentImageReceiver = imageReceiver;
        }

        public void setupGradient() {
            int color = Theme.getColor("chat_serviceBackground");
            gradientWidth = (float) (AndroidUtilities.displaySize.x * 2);
            float dp = ((float) AndroidUtilities.dp(180.0f)) / gradientWidth;
            int argb = Color.argb(Color.alpha(color) / 2, Color.red(color), Color.green(color), Color.blue(color));
            float f = (1.0f - dp) / 2.0f;
            float f2 = dp / 2.0f;
            this.placeholderGradient = new LinearGradient(0.0f, 0.0f, gradientWidth, 0.0f, new int[]{0, 0, argb, 0, 0}, new float[]{0.0f, f - f2, f, f + f2, 1.0f}, Shader.TileMode.REPEAT);
            this.backgroundGradient = new LinearGradient(0.0f, 0.0f, gradientWidth, 0.0f, new int[]{argb, argb}, (float[]) null, Shader.TileMode.REPEAT);
            Matrix matrix = new Matrix();
            this.placeholderMatrix = matrix;
            this.placeholderGradient.setLocalMatrix(matrix);
            for (Paint shader : this.paints.values()) {
                shader.setShader(new ComposeShader(this.placeholderGradient, this.backgroundGradient, PorterDuff.Mode.ADD));
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0031, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x0035 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getBitmap(java.io.File r8, int r9, int r10, boolean r11) {
        /*
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0036 }
            r0.<init>(r8)     // Catch:{ Exception -> 0x0036 }
            javax.xml.parsers.SAXParserFactory r8 = javax.xml.parsers.SAXParserFactory.newInstance()     // Catch:{ all -> 0x002f }
            javax.xml.parsers.SAXParser r8 = r8.newSAXParser()     // Catch:{ all -> 0x002f }
            org.xml.sax.XMLReader r8 = r8.getXMLReader()     // Catch:{ all -> 0x002f }
            org.telegram.messenger.SvgHelper$SVGHandler r7 = new org.telegram.messenger.SvgHelper$SVGHandler     // Catch:{ all -> 0x002f }
            r5 = 0
            r6 = 0
            r1 = r7
            r2 = r9
            r3 = r10
            r4 = r11
            r1.<init>(r2, r3, r4, r5)     // Catch:{ all -> 0x002f }
            r8.setContentHandler(r7)     // Catch:{ all -> 0x002f }
            org.xml.sax.InputSource r9 = new org.xml.sax.InputSource     // Catch:{ all -> 0x002f }
            r9.<init>(r0)     // Catch:{ all -> 0x002f }
            r8.parse(r9)     // Catch:{ all -> 0x002f }
            android.graphics.Bitmap r8 = r7.getBitmap()     // Catch:{ all -> 0x002f }
            r0.close()     // Catch:{ Exception -> 0x0036 }
            return r8
        L_0x002f:
            r8 = move-exception
            throw r8     // Catch:{ all -> 0x0031 }
        L_0x0031:
            r8 = move-exception
            r0.close()     // Catch:{ all -> 0x0035 }
        L_0x0035:
            throw r8     // Catch:{ Exception -> 0x0036 }
        L_0x0036:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            r8 = 0
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.getBitmap(java.io.File, int, int, boolean):android.graphics.Bitmap");
    }

    public static Bitmap getBitmap(String str, int i, int i2, boolean z) {
        try {
            XMLReader xMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SVGHandler sVGHandler = new SVGHandler(i, i2, z, false);
            xMLReader.setContentHandler(sVGHandler);
            xMLReader.parse(new InputSource(new StringReader(str)));
            return sVGHandler.getBitmap();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static SvgDrawable getDrawable(String str) {
        try {
            XMLReader xMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SVGHandler sVGHandler = new SVGHandler(0, 0, false, true);
            xMLReader.setContentHandler(sVGHandler);
            xMLReader.parse(new InputSource(new StringReader(str)));
            return sVGHandler.getDrawable();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static SvgDrawable getDrawableByPath(String str, int i, int i2) {
        try {
            Path doPath = doPath(str);
            SvgDrawable svgDrawable = new SvgDrawable();
            svgDrawable.commands.add(doPath);
            svgDrawable.paints.put(doPath, new Paint(1));
            int unused = svgDrawable.width = i;
            int unused2 = svgDrawable.height = i2;
            return svgDrawable;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static Bitmap getBitmapByPathOnly(String str, int i, int i2, int i3, int i4) {
        try {
            Path doPath = doPath(str);
            Bitmap createBitmap = Bitmap.createBitmap(i3, i4, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.scale(((float) i3) / ((float) i), ((float) i4) / ((float) i2));
            Paint paint = new Paint();
            paint.setColor(-1);
            canvas.drawPath(doPath, paint);
            return createBitmap;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
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
            org.telegram.messenger.SvgHelper$ParserHelper r2 = new org.telegram.messenger.SvgHelper$ParserHelper
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
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.doPath(java.lang.String):android.graphics.Path");
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

    private static Integer getHexAttr(String str, Attributes attributes) {
        String stringAttr = getStringAttr(str, attributes);
        if (stringAttr == null) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(stringAttr.substring(1), 16));
        } catch (NumberFormatException unused) {
            return getColorByName(stringAttr);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Integer getColorByName(java.lang.String r2) {
        /*
            java.lang.String r2 = r2.toLowerCase()
            r2.hashCode()
            int r0 = r2.hashCode()
            r1 = -1
            switch(r0) {
                case -734239628: goto L_0x006d;
                case 112785: goto L_0x0062;
                case 3027034: goto L_0x0057;
                case 3068707: goto L_0x004c;
                case 3181155: goto L_0x0041;
                case 93818879: goto L_0x0036;
                case 98619139: goto L_0x002b;
                case 113101865: goto L_0x001f;
                case 828922025: goto L_0x0012;
                default: goto L_0x000f;
            }
        L_0x000f:
            r2 = -1
            goto L_0x0078
        L_0x0012:
            java.lang.String r0 = "magenta"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x001b
            goto L_0x000f
        L_0x001b:
            r2 = 8
            goto L_0x0078
        L_0x001f:
            java.lang.String r0 = "white"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0029
            goto L_0x000f
        L_0x0029:
            r2 = 7
            goto L_0x0078
        L_0x002b:
            java.lang.String r0 = "green"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0034
            goto L_0x000f
        L_0x0034:
            r2 = 6
            goto L_0x0078
        L_0x0036:
            java.lang.String r0 = "black"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x003f
            goto L_0x000f
        L_0x003f:
            r2 = 5
            goto L_0x0078
        L_0x0041:
            java.lang.String r0 = "gray"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x004a
            goto L_0x000f
        L_0x004a:
            r2 = 4
            goto L_0x0078
        L_0x004c:
            java.lang.String r0 = "cyan"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0055
            goto L_0x000f
        L_0x0055:
            r2 = 3
            goto L_0x0078
        L_0x0057:
            java.lang.String r0 = "blue"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0060
            goto L_0x000f
        L_0x0060:
            r2 = 2
            goto L_0x0078
        L_0x0062:
            java.lang.String r0 = "red"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x006b
            goto L_0x000f
        L_0x006b:
            r2 = 1
            goto L_0x0078
        L_0x006d:
            java.lang.String r0 = "yellow"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0077
            goto L_0x000f
        L_0x0077:
            r2 = 0
        L_0x0078:
            switch(r2) {
                case 0: goto L_0x00b8;
                case 1: goto L_0x00b1;
                case 2: goto L_0x00a9;
                case 3: goto L_0x00a1;
                case 4: goto L_0x0099;
                case 5: goto L_0x0092;
                case 6: goto L_0x008a;
                case 7: goto L_0x0085;
                case 8: goto L_0x007d;
                default: goto L_0x007b;
            }
        L_0x007b:
            r2 = 0
            return r2
        L_0x007d:
            r2 = -65281(0xfffffffffffvar_ff, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0085:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            return r2
        L_0x008a:
            r2 = -16711936(0xfffffffffvar_fvar_, float:-1.7146522E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0092:
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0099:
            r2 = -7829368(0xfffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00a1:
            r2 = -16711681(0xfffffffffvar_ffff, float:-1.714704E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00a9:
            r2 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00b1:
            r2 = -65536(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00b8:
            r2 = -256(0xfffffffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.getColorByName(java.lang.String):java.lang.Integer");
    }

    private static class NumberParse {
        private int nextCmd;
        /* access modifiers changed from: private */
        public ArrayList<Float> numbers;

        public NumberParse(ArrayList<Float> arrayList, int i) {
            this.numbers = arrayList;
            this.nextCmd = i;
        }

        public int getNextCmd() {
            return this.nextCmd;
        }

        public float getNumber(int i) {
            return this.numbers.get(i).floatValue();
        }
    }

    private static class StyleSet {
        HashMap<String, String> styleMap;

        private StyleSet(StyleSet styleSet) {
            HashMap<String, String> hashMap = new HashMap<>();
            this.styleMap = hashMap;
            hashMap.putAll(styleSet.styleMap);
        }

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
            String access$600 = SvgHelper.getStringAttr("style", attributes);
            if (access$600 != null) {
                ArrayList<StyleSet> arrayList = new ArrayList<>();
                this.styles = arrayList;
                arrayList.add(new StyleSet(access$600));
                return;
            }
            String access$6002 = SvgHelper.getStringAttr("class", attributes);
            if (access$6002 != null) {
                this.styles = new ArrayList<>();
                String[] split = access$6002.split(" ");
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

        public Float getFloat(String str, float f) {
            Float f2 = getFloat(str);
            return f2 == null ? Float.valueOf(f) : f2;
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
        private SvgDrawable drawable;
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

        private SVGHandler(int i, int i2, boolean z, boolean z2) {
            this.scale = 1.0f;
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.pushed = false;
            this.globalStyles = new HashMap<>();
            this.desiredWidth = i;
            this.desiredHeight = i2;
            this.whiteOnly = z;
            if (z2) {
                this.drawable = new SvgDrawable();
            }
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
            String access$600 = SvgHelper.getStringAttr("transform", attributes);
            boolean z = access$600 != null;
            this.pushed = z;
            if (z) {
                Matrix access$900 = SvgHelper.parseTransform(access$600);
                SvgDrawable svgDrawable = this.drawable;
                if (svgDrawable != null) {
                    svgDrawable.addCommand(access$900);
                    return;
                }
                this.canvas.save();
                this.canvas.concat(access$900);
            }
        }

        private void popTransform() {
            if (this.pushed) {
                SvgDrawable svgDrawable = this.drawable;
                if (svgDrawable != null) {
                    svgDrawable.addCommand((Object) null);
                } else {
                    this.canvas.restore();
                }
            }
        }

        public void startElement(String str, String str2, String str3, Attributes attributes) {
            int i;
            String access$600;
            if (!this.boundsMode || str2.equals("style")) {
                str2.hashCode();
                char c = 65535;
                switch (str2.hashCode()) {
                    case -1656480802:
                        if (str2.equals("ellipse")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1360216880:
                        if (str2.equals("circle")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -397519558:
                        if (str2.equals("polygon")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 103:
                        if (str2.equals("g")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 114276:
                        if (str2.equals("svg")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 3079438:
                        if (str2.equals("defs")) {
                            c = 5;
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
                            c = 7;
                            break;
                        }
                        break;
                    case 3496420:
                        if (str2.equals("rect")) {
                            c = 8;
                            break;
                        }
                        break;
                    case 109780401:
                        if (str2.equals("style")) {
                            c = 9;
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
                            c = 11;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        Float access$1100 = SvgHelper.getFloatAttr("cx", attributes);
                        Float access$11002 = SvgHelper.getFloatAttr("cy", attributes);
                        Float access$11003 = SvgHelper.getFloatAttr("rx", attributes);
                        Float access$11004 = SvgHelper.getFloatAttr("ry", attributes);
                        if (access$1100 != null && access$11002 != null && access$11003 != null && access$11004 != null) {
                            pushTransform(attributes);
                            Properties properties = new Properties(attributes, this.globalStyles);
                            this.rect.set(access$1100.floatValue() - access$11003.floatValue(), access$11002.floatValue() - access$11004.floatValue(), access$1100.floatValue() + access$11003.floatValue(), access$11002.floatValue() + access$11004.floatValue());
                            if (doFill(properties)) {
                                SvgDrawable svgDrawable = this.drawable;
                                if (svgDrawable != null) {
                                    svgDrawable.addCommand(new Oval(this.rect), this.paint);
                                } else {
                                    this.canvas.drawOval(this.rect, this.paint);
                                }
                            }
                            if (doStroke(properties)) {
                                SvgDrawable svgDrawable2 = this.drawable;
                                if (svgDrawable2 != null) {
                                    svgDrawable2.addCommand(new Oval(this.rect), this.paint);
                                } else {
                                    this.canvas.drawOval(this.rect, this.paint);
                                }
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 1:
                        Float access$11005 = SvgHelper.getFloatAttr("cx", attributes);
                        Float access$11006 = SvgHelper.getFloatAttr("cy", attributes);
                        Float access$11007 = SvgHelper.getFloatAttr("r", attributes);
                        if (access$11005 != null && access$11006 != null && access$11007 != null) {
                            pushTransform(attributes);
                            Properties properties2 = new Properties(attributes, this.globalStyles);
                            if (doFill(properties2)) {
                                SvgDrawable svgDrawable3 = this.drawable;
                                if (svgDrawable3 != null) {
                                    svgDrawable3.addCommand(new Circle(access$11005.floatValue(), access$11006.floatValue(), access$11007.floatValue()), this.paint);
                                } else {
                                    this.canvas.drawCircle(access$11005.floatValue(), access$11006.floatValue(), access$11007.floatValue(), this.paint);
                                }
                            }
                            if (doStroke(properties2)) {
                                SvgDrawable svgDrawable4 = this.drawable;
                                if (svgDrawable4 != null) {
                                    svgDrawable4.addCommand(new Circle(access$11005.floatValue(), access$11006.floatValue(), access$11007.floatValue()), this.paint);
                                } else {
                                    this.canvas.drawCircle(access$11005.floatValue(), access$11006.floatValue(), access$11007.floatValue(), this.paint);
                                }
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 2:
                    case 10:
                        NumberParse access$1400 = SvgHelper.getNumberParseAttr("points", attributes);
                        if (access$1400 != null) {
                            Path path = new Path();
                            ArrayList access$500 = access$1400.numbers;
                            if (access$500.size() > 1) {
                                pushTransform(attributes);
                                Properties properties3 = new Properties(attributes, this.globalStyles);
                                path.moveTo(((Float) access$500.get(0)).floatValue(), ((Float) access$500.get(1)).floatValue());
                                for (int i2 = 2; i2 < access$500.size(); i2 += 2) {
                                    path.lineTo(((Float) access$500.get(i2)).floatValue(), ((Float) access$500.get(i2 + 1)).floatValue());
                                }
                                if (str2.equals("polygon")) {
                                    path.close();
                                }
                                if (doFill(properties3)) {
                                    SvgDrawable svgDrawable5 = this.drawable;
                                    if (svgDrawable5 != null) {
                                        svgDrawable5.addCommand(path, this.paint);
                                    } else {
                                        this.canvas.drawPath(path, this.paint);
                                    }
                                }
                                if (doStroke(properties3)) {
                                    SvgDrawable svgDrawable6 = this.drawable;
                                    if (svgDrawable6 != null) {
                                        svgDrawable6.addCommand(path, this.paint);
                                    } else {
                                        this.canvas.drawPath(path, this.paint);
                                    }
                                }
                                popTransform();
                                return;
                            }
                            return;
                        }
                        return;
                    case 3:
                        if ("bounds".equalsIgnoreCase(SvgHelper.getStringAttr("id", attributes))) {
                            this.boundsMode = true;
                            return;
                        }
                        return;
                    case 4:
                        Float access$11008 = SvgHelper.getFloatAttr("width", attributes);
                        Float access$11009 = SvgHelper.getFloatAttr("height", attributes);
                        if ((access$11008 == null || access$11009 == null) && (access$600 = SvgHelper.getStringAttr("viewBox", attributes)) != null) {
                            String[] split = access$600.split(" ");
                            Float valueOf = Float.valueOf(Float.parseFloat(split[2]));
                            access$11009 = Float.valueOf(Float.parseFloat(split[3]));
                            access$11008 = valueOf;
                        }
                        if (access$11008 == null || access$11009 == null) {
                            access$11008 = Float.valueOf((float) this.desiredWidth);
                            access$11009 = Float.valueOf((float) this.desiredHeight);
                        }
                        int ceil = (int) Math.ceil((double) access$11008.floatValue());
                        int ceil2 = (int) Math.ceil((double) access$11009.floatValue());
                        if (ceil == 0 || ceil2 == 0) {
                            ceil = this.desiredWidth;
                            ceil2 = this.desiredHeight;
                        } else {
                            int i3 = this.desiredWidth;
                            if (!(i3 == 0 || (i = this.desiredHeight) == 0)) {
                                float f = (float) ceil;
                                float f2 = (float) ceil2;
                                float min = Math.min(((float) i3) / f, ((float) i) / f2);
                                this.scale = min;
                                ceil = (int) (f * min);
                                ceil2 = (int) (f2 * min);
                            }
                        }
                        SvgDrawable svgDrawable7 = this.drawable;
                        if (svgDrawable7 == null) {
                            Bitmap createBitmap = Bitmap.createBitmap(ceil, ceil2, Bitmap.Config.ARGB_8888);
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
                        }
                        int unused = svgDrawable7.width = ceil;
                        int unused2 = this.drawable.height = ceil2;
                        return;
                    case 5:
                    case 11:
                        this.boundsMode = true;
                        return;
                    case 6:
                        Float access$110010 = SvgHelper.getFloatAttr("x1", attributes);
                        Float access$110011 = SvgHelper.getFloatAttr("x2", attributes);
                        Float access$110012 = SvgHelper.getFloatAttr("y1", attributes);
                        Float access$110013 = SvgHelper.getFloatAttr("y2", attributes);
                        if (doStroke(new Properties(attributes, this.globalStyles))) {
                            pushTransform(attributes);
                            SvgDrawable svgDrawable8 = this.drawable;
                            if (svgDrawable8 != null) {
                                svgDrawable8.addCommand(new Line(access$110010.floatValue(), access$110012.floatValue(), access$110011.floatValue(), access$110013.floatValue()), this.paint);
                            } else {
                                this.canvas.drawLine(access$110010.floatValue(), access$110012.floatValue(), access$110011.floatValue(), access$110013.floatValue(), this.paint);
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 7:
                        Path access$1500 = SvgHelper.doPath(SvgHelper.getStringAttr("d", attributes));
                        pushTransform(attributes);
                        Properties properties4 = new Properties(attributes, this.globalStyles);
                        if (doFill(properties4)) {
                            SvgDrawable svgDrawable9 = this.drawable;
                            if (svgDrawable9 != null) {
                                svgDrawable9.addCommand(access$1500, this.paint);
                            } else {
                                this.canvas.drawPath(access$1500, this.paint);
                            }
                        }
                        if (doStroke(properties4)) {
                            SvgDrawable svgDrawable10 = this.drawable;
                            if (svgDrawable10 != null) {
                                svgDrawable10.addCommand(access$1500, this.paint);
                            } else {
                                this.canvas.drawPath(access$1500, this.paint);
                            }
                        }
                        popTransform();
                        return;
                    case 8:
                        Float access$110014 = SvgHelper.getFloatAttr("x", attributes);
                        if (access$110014 == null) {
                            access$110014 = Float.valueOf(0.0f);
                        }
                        Float access$110015 = SvgHelper.getFloatAttr("y", attributes);
                        if (access$110015 == null) {
                            access$110015 = Float.valueOf(0.0f);
                        }
                        Float access$110016 = SvgHelper.getFloatAttr("width", attributes);
                        Float access$110017 = SvgHelper.getFloatAttr("height", attributes);
                        pushTransform(attributes);
                        Properties properties5 = new Properties(attributes, this.globalStyles);
                        if (doFill(properties5)) {
                            SvgDrawable svgDrawable11 = this.drawable;
                            if (svgDrawable11 != null) {
                                svgDrawable11.addCommand(new RectF(access$110014.floatValue(), access$110015.floatValue(), access$110014.floatValue() + access$110016.floatValue(), access$110015.floatValue() + access$110017.floatValue()), this.paint);
                            } else {
                                this.canvas.drawRect(access$110014.floatValue(), access$110015.floatValue(), access$110014.floatValue() + access$110016.floatValue(), access$110015.floatValue() + access$110017.floatValue(), this.paint);
                            }
                        }
                        if (doStroke(properties5)) {
                            SvgDrawable svgDrawable12 = this.drawable;
                            if (svgDrawable12 != null) {
                                svgDrawable12.addCommand(new RectF(access$110014.floatValue(), access$110015.floatValue(), access$110014.floatValue() + access$110016.floatValue(), access$110015.floatValue() + access$110017.floatValue()), this.paint);
                            } else {
                                this.canvas.drawRect(access$110014.floatValue(), access$110015.floatValue(), access$110014.floatValue() + access$110016.floatValue(), access$110015.floatValue() + access$110017.floatValue(), this.paint);
                            }
                        }
                        popTransform();
                        return;
                    case 9:
                        this.styles = new StringBuilder();
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

        public void endElement(String str, String str2, String str3) {
            int indexOf;
            str2.hashCode();
            char c = 65535;
            switch (str2.hashCode()) {
                case 103:
                    if (str2.equals("g")) {
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
                case 109780401:
                    if (str2.equals("style")) {
                        c = 2;
                        break;
                    }
                    break;
                case 917656469:
                    if (str2.equals("clipPath")) {
                        c = 3;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 3:
                    this.boundsMode = false;
                    return;
                case 2:
                    StringBuilder sb = this.styles;
                    if (sb != null) {
                        String[] split = sb.toString().split("\\}");
                        for (int i = 0; i < split.length; i++) {
                            split[i] = split[i].trim().replace("\t", "").replace("\n", "");
                            if (split[i].length() != 0 && split[i].charAt(0) == '.' && (indexOf = split[i].indexOf(123)) >= 0) {
                                this.globalStyles.put(split[i].substring(1, indexOf).trim(), new StyleSet(split[i].substring(indexOf + 1)));
                            }
                        }
                        this.styles = null;
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public Bitmap getBitmap() {
            return this.bitmap;
        }

        public SvgDrawable getDrawable() {
            return this.drawable;
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
            int i2 = this.n;
            if (i < i2) {
                this.pos = i + 1;
            }
            int i3 = this.pos;
            if (i3 == i2) {
                return 0;
            }
            return this.s.charAt(i3);
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

        /* JADX WARNING: Code restructure failed: missing block: B:10:0x0028, code lost:
            r5 = read();
            r15.current = r5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x002e, code lost:
            if (r5 == '.') goto L_0x0053;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0030, code lost:
            if (r5 == 'E') goto L_0x0053;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0032, code lost:
            if (r5 == 'e') goto L_0x0053;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0034, code lost:
            switch(r5) {
                case 48: goto L_0x0028;
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
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0037, code lost:
            return 0.0f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0038, code lost:
            r5 = 0;
            r11 = 0;
            r12 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x003b, code lost:
            if (r5 >= 9) goto L_0x0047;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x003d, code lost:
            r5 = r5 + 1;
            r12 = (r12 * 10) + (r15.current - '0');
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x0047, code lost:
            r11 = r11 + 1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0049, code lost:
            r13 = read();
            r15.current = r13;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x004f, code lost:
            switch(r13) {
                case 48: goto L_0x003b;
                case 49: goto L_0x003b;
                case 50: goto L_0x003b;
                case 51: goto L_0x003b;
                case 52: goto L_0x003b;
                case 53: goto L_0x003b;
                case 54: goto L_0x003b;
                case 55: goto L_0x003b;
                case 56: goto L_0x003b;
                case 57: goto L_0x003b;
                default: goto L_0x0052;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:22:0x0053, code lost:
            r5 = 0;
            r11 = 0;
            r12 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x0056, code lost:
            r13 = true;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:0x00c1, code lost:
            r1 = read();
            r15.current = r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x00c7, code lost:
            switch(r1) {
                case 48: goto L_0x00c1;
                case 49: goto L_0x00cb;
                case 50: goto L_0x00cb;
                case 51: goto L_0x00cb;
                case 52: goto L_0x00cb;
                case 53: goto L_0x00cb;
                case 54: goto L_0x00cb;
                case 55: goto L_0x00cb;
                case 56: goto L_0x00cb;
                case 57: goto L_0x00cb;
                default: goto L_0x00ca;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x00cb, code lost:
            r1 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:61:0x00cd, code lost:
            if (r4 >= 3) goto L_0x00d8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:62:0x00cf, code lost:
            r4 = r4 + 1;
            r1 = (r1 * 10) + (r15.current - '0');
         */
        /* JADX WARNING: Code restructure failed: missing block: B:63:0x00d8, code lost:
            r2 = read();
            r15.current = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:64:0x00de, code lost:
            switch(r2) {
                case 48: goto L_0x00cc;
                case 49: goto L_0x00cc;
                case 50: goto L_0x00cc;
                case 51: goto L_0x00cc;
                case 52: goto L_0x00cc;
                case 53: goto L_0x00cc;
                case 54: goto L_0x00cc;
                case 55: goto L_0x00cc;
                case 56: goto L_0x00cc;
                case 57: goto L_0x00cc;
                default: goto L_0x00e1;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:65:0x00e1, code lost:
            r4 = r1;
         */
        /* JADX WARNING: Removed duplicated region for block: B:24:0x0058  */
        /* JADX WARNING: Removed duplicated region for block: B:27:0x0060  */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x0071 A[LOOP_START, PHI: r11 
          PHI: (r11v6 int) = (r11v0 int), (r11v7 int) binds: [B:32:0x006f, B:34:0x0079] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:37:0x007f A[LOOP_START, PHI: r5 r11 r12 
          PHI: (r5v4 int) = (r5v1 int), (r5v5 int) binds: [B:82:0x007f, B:40:0x0092] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r11v3 int) = (r11v2 int), (r11v4 int) binds: [B:82:0x007f, B:40:0x0092] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r12v4 int) = (r12v0 int), (r12v5 int) binds: [B:82:0x007f, B:40:0x0092] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:46:0x00a4  */
        /* JADX WARNING: Removed duplicated region for block: B:53:0x00b7  */
        /* JADX WARNING: Removed duplicated region for block: B:67:0x00e4  */
        /* JADX WARNING: Removed duplicated region for block: B:70:0x00e8  */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0025 A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public float parseFloat() {
            /*
                r15 = this;
                char r0 = r15.current
                r1 = 45
                r2 = 43
                r3 = 1
                r4 = 0
                if (r0 == r2) goto L_0x0010
                if (r0 == r1) goto L_0x000e
                r0 = 1
                goto L_0x0017
            L_0x000e:
                r0 = 0
                goto L_0x0011
            L_0x0010:
                r0 = 1
            L_0x0011:
                char r5 = r15.read()
                r15.current = r5
            L_0x0017:
                char r5 = r15.current
                r6 = 101(0x65, float:1.42E-43)
                r7 = 69
                r8 = 46
                r9 = 9
                r10 = 0
                switch(r5) {
                    case 46: goto L_0x0058;
                    case 47: goto L_0x0025;
                    case 48: goto L_0x0028;
                    case 49: goto L_0x0038;
                    case 50: goto L_0x0038;
                    case 51: goto L_0x0038;
                    case 52: goto L_0x0038;
                    case 53: goto L_0x0038;
                    case 54: goto L_0x0038;
                    case 55: goto L_0x0038;
                    case 56: goto L_0x0038;
                    case 57: goto L_0x0038;
                    default: goto L_0x0025;
                }
            L_0x0025:
                r0 = 2143289344(0x7fCLASSNAME, float:NaN)
                return r0
            L_0x0028:
                char r5 = r15.read()
                r15.current = r5
                if (r5 == r8) goto L_0x0053
                if (r5 == r7) goto L_0x0053
                if (r5 == r6) goto L_0x0053
                switch(r5) {
                    case 48: goto L_0x0028;
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
                return r10
            L_0x0038:
                r5 = 0
                r11 = 0
                r12 = 0
            L_0x003b:
                if (r5 >= r9) goto L_0x0047
                int r5 = r5 + 1
                int r12 = r12 * 10
                char r13 = r15.current
                int r13 = r13 + -48
                int r12 = r12 + r13
                goto L_0x0049
            L_0x0047:
                int r11 = r11 + 1
            L_0x0049:
                char r13 = r15.read()
                r15.current = r13
                switch(r13) {
                    case 48: goto L_0x003b;
                    case 49: goto L_0x003b;
                    case 50: goto L_0x003b;
                    case 51: goto L_0x003b;
                    case 52: goto L_0x003b;
                    case 53: goto L_0x003b;
                    case 54: goto L_0x003b;
                    case 55: goto L_0x003b;
                    case 56: goto L_0x003b;
                    case 57: goto L_0x003b;
                    default: goto L_0x0052;
                }
            L_0x0052:
                goto L_0x0056
            L_0x0053:
                r5 = 0
                r11 = 0
                r12 = 0
            L_0x0056:
                r13 = 1
                goto L_0x005c
            L_0x0058:
                r5 = 0
                r11 = 0
                r12 = 0
                r13 = 0
            L_0x005c:
                char r14 = r15.current
                if (r14 != r8) goto L_0x0095
                char r8 = r15.read()
                r15.current = r8
                switch(r8) {
                    case 48: goto L_0x006f;
                    case 49: goto L_0x007f;
                    case 50: goto L_0x007f;
                    case 51: goto L_0x007f;
                    case 52: goto L_0x007f;
                    case 53: goto L_0x007f;
                    case 54: goto L_0x007f;
                    case 55: goto L_0x007f;
                    case 56: goto L_0x007f;
                    case 57: goto L_0x007f;
                    default: goto L_0x0069;
                }
            L_0x0069:
                if (r13 != 0) goto L_0x0095
                r15.reportUnexpectedCharacterError(r8)
                return r10
            L_0x006f:
                if (r5 != 0) goto L_0x007f
            L_0x0071:
                char r8 = r15.read()
                r15.current = r8
                int r11 = r11 + -1
                switch(r8) {
                    case 48: goto L_0x0071;
                    case 49: goto L_0x007f;
                    case 50: goto L_0x007f;
                    case 51: goto L_0x007f;
                    case 52: goto L_0x007f;
                    case 53: goto L_0x007f;
                    case 54: goto L_0x007f;
                    case 55: goto L_0x007f;
                    case 56: goto L_0x007f;
                    case 57: goto L_0x007f;
                    default: goto L_0x007c;
                }
            L_0x007c:
                if (r13 != 0) goto L_0x0095
                return r10
            L_0x007f:
                if (r5 >= r9) goto L_0x008c
                int r5 = r5 + 1
                int r12 = r12 * 10
                char r8 = r15.current
                int r8 = r8 + -48
                int r12 = r12 + r8
                int r11 = r11 + -1
            L_0x008c:
                char r8 = r15.read()
                r15.current = r8
                switch(r8) {
                    case 48: goto L_0x007f;
                    case 49: goto L_0x007f;
                    case 50: goto L_0x007f;
                    case 51: goto L_0x007f;
                    case 52: goto L_0x007f;
                    case 53: goto L_0x007f;
                    case 54: goto L_0x007f;
                    case 55: goto L_0x007f;
                    case 56: goto L_0x007f;
                    case 57: goto L_0x007f;
                    default: goto L_0x0095;
                }
            L_0x0095:
                char r5 = r15.current
                if (r5 == r7) goto L_0x009c
                if (r5 == r6) goto L_0x009c
                goto L_0x00e2
            L_0x009c:
                char r5 = r15.read()
                r15.current = r5
                if (r5 == r2) goto L_0x00ae
                if (r5 == r1) goto L_0x00ad
                switch(r5) {
                    case 48: goto L_0x00bb;
                    case 49: goto L_0x00bb;
                    case 50: goto L_0x00bb;
                    case 51: goto L_0x00bb;
                    case 52: goto L_0x00bb;
                    case 53: goto L_0x00bb;
                    case 54: goto L_0x00bb;
                    case 55: goto L_0x00bb;
                    case 56: goto L_0x00bb;
                    case 57: goto L_0x00bb;
                    default: goto L_0x00a9;
                }
            L_0x00a9:
                r15.reportUnexpectedCharacterError(r5)
                return r10
            L_0x00ad:
                r3 = 0
            L_0x00ae:
                char r1 = r15.read()
                r15.current = r1
                switch(r1) {
                    case 48: goto L_0x00bb;
                    case 49: goto L_0x00bb;
                    case 50: goto L_0x00bb;
                    case 51: goto L_0x00bb;
                    case 52: goto L_0x00bb;
                    case 53: goto L_0x00bb;
                    case 54: goto L_0x00bb;
                    case 55: goto L_0x00bb;
                    case 56: goto L_0x00bb;
                    case 57: goto L_0x00bb;
                    default: goto L_0x00b7;
                }
            L_0x00b7:
                r15.reportUnexpectedCharacterError(r1)
                return r10
            L_0x00bb:
                char r1 = r15.current
                switch(r1) {
                    case 48: goto L_0x00c1;
                    case 49: goto L_0x00cb;
                    case 50: goto L_0x00cb;
                    case 51: goto L_0x00cb;
                    case 52: goto L_0x00cb;
                    case 53: goto L_0x00cb;
                    case 54: goto L_0x00cb;
                    case 55: goto L_0x00cb;
                    case 56: goto L_0x00cb;
                    case 57: goto L_0x00cb;
                    default: goto L_0x00c0;
                }
            L_0x00c0:
                goto L_0x00e2
            L_0x00c1:
                char r1 = r15.read()
                r15.current = r1
                switch(r1) {
                    case 48: goto L_0x00c1;
                    case 49: goto L_0x00cb;
                    case 50: goto L_0x00cb;
                    case 51: goto L_0x00cb;
                    case 52: goto L_0x00cb;
                    case 53: goto L_0x00cb;
                    case 54: goto L_0x00cb;
                    case 55: goto L_0x00cb;
                    case 56: goto L_0x00cb;
                    case 57: goto L_0x00cb;
                    default: goto L_0x00ca;
                }
            L_0x00ca:
                goto L_0x00e2
            L_0x00cb:
                r1 = 0
            L_0x00cc:
                r2 = 3
                if (r4 >= r2) goto L_0x00d8
                int r4 = r4 + 1
                int r1 = r1 * 10
                char r2 = r15.current
                int r2 = r2 + -48
                int r1 = r1 + r2
            L_0x00d8:
                char r2 = r15.read()
                r15.current = r2
                switch(r2) {
                    case 48: goto L_0x00cc;
                    case 49: goto L_0x00cc;
                    case 50: goto L_0x00cc;
                    case 51: goto L_0x00cc;
                    case 52: goto L_0x00cc;
                    case 53: goto L_0x00cc;
                    case 54: goto L_0x00cc;
                    case 55: goto L_0x00cc;
                    case 56: goto L_0x00cc;
                    case 57: goto L_0x00cc;
                    default: goto L_0x00e1;
                }
            L_0x00e1:
                r4 = r1
            L_0x00e2:
                if (r3 != 0) goto L_0x00e5
                int r4 = -r4
            L_0x00e5:
                int r4 = r4 + r11
                if (r0 != 0) goto L_0x00e9
                int r12 = -r12
            L_0x00e9:
                float r0 = r15.buildFloat(r12, r4)
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.ParserHelper.parseFloat():float");
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
            double[] access$1600 = SvgHelper.pow10;
            if (i2 > 0) {
                double d3 = access$1600[i2];
                Double.isNaN(d2);
                d = d2 * d3;
            } else {
                double d4 = access$1600[-i2];
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

    public static String decompress(byte[] bArr) {
        try {
            StringBuilder sb = new StringBuilder(bArr.length * 2);
            sb.append('M');
            for (byte b : bArr) {
                byte b2 = b & 255;
                if (b2 >= 192) {
                    int i = (b2 - 128) - 64;
                    sb.append("AACAAAAHAAALMAAAQASTAVAAAZaacaaaahaaalmaaaqastava.az0123456789-,".substring(i, i + 1));
                } else {
                    if (b2 >= 128) {
                        sb.append(',');
                    } else if (b2 >= 64) {
                        sb.append('-');
                    }
                    sb.append(b2 & 63);
                }
            }
            sb.append('z');
            return sb.toString();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "";
        }
    }
}

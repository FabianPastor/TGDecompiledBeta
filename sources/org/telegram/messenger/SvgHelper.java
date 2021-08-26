package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
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
import android.os.Build;
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

    private static class RoundRect {
        RectF rect;
        float rx;

        public RoundRect(RectF rectF, float f) {
            this.rect = rectF;
            this.rx = f;
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
        private boolean aspectFill = true;
        private Bitmap backgroundBitmap;
        private Canvas backgroundCanvas;
        private Shader backgroundGradient;
        private float colorAlpha;
        protected ArrayList<Object> commands = new ArrayList<>();
        private float crossfadeAlpha = 1.0f;
        private int currentColor;
        private String currentColorKey;
        protected int height;
        protected HashMap<Object, Paint> paints = new HashMap<>();
        private ImageReceiver parentImageReceiver;
        private LinearGradient placeholderGradient;
        private Matrix placeholderMatrix;
        protected int width;

        public int getOpacity() {
            return -2;
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getIntrinsicHeight() {
            return this.width;
        }

        public int getIntrinsicWidth() {
            return this.height;
        }

        public void setAspectFill(boolean z) {
            this.aspectFill = z;
        }

        public void draw(Canvas canvas) {
            Canvas canvas2 = canvas;
            String str = this.currentColorKey;
            if (str != null) {
                setupGradient(str, this.colorAlpha);
            }
            Rect bounds = getBounds();
            float width2 = ((float) bounds.width()) / ((float) this.width);
            float height2 = ((float) bounds.height()) / ((float) this.height);
            float max = this.aspectFill ? Math.max(width2, height2) : Math.min(width2, height2);
            canvas.save();
            canvas2.translate((float) bounds.left, (float) bounds.top);
            if (!this.aspectFill) {
                canvas2.translate((((float) bounds.width()) - (((float) this.width) * max)) / 2.0f, (((float) bounds.height()) - (((float) this.height) * max)) / 2.0f);
            }
            canvas2.scale(max, max);
            int size = this.commands.size();
            for (int i = 0; i < size; i++) {
                Object obj = this.commands.get(i);
                if (obj instanceof Matrix) {
                    canvas.save();
                    canvas2.concat((Matrix) obj);
                } else if (obj == null) {
                    canvas.restore();
                } else {
                    Paint paint = this.paints.get(obj);
                    int alpha = paint.getAlpha();
                    paint.setAlpha((int) (this.crossfadeAlpha * ((float) alpha)));
                    if (obj instanceof Path) {
                        canvas2.drawPath((Path) obj, paint);
                    } else if (obj instanceof Rect) {
                        canvas2.drawRect((Rect) obj, paint);
                    } else if (obj instanceof RectF) {
                        canvas2.drawRect((RectF) obj, paint);
                    } else if (obj instanceof Line) {
                        Line line = (Line) obj;
                        canvas.drawLine(line.x1, line.y1, line.x2, line.y2, paint);
                    } else if (obj instanceof Circle) {
                        Circle circle = (Circle) obj;
                        canvas2.drawCircle(circle.x1, circle.y1, circle.rad, paint);
                    } else if (obj instanceof Oval) {
                        canvas2.drawOval(((Oval) obj).rect, paint);
                    } else if (obj instanceof RoundRect) {
                        RoundRect roundRect = (RoundRect) obj;
                        RectF rectF = roundRect.rect;
                        float f = roundRect.rx;
                        canvas2.drawRoundRect(rectF, f, f, paint);
                    }
                    paint.setAlpha(alpha);
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
                        float f2 = totalTranslation;
                        float f3 = gradientWidth;
                        if (f2 < f3 / 2.0f) {
                            break;
                        }
                        totalTranslation = f2 - f3;
                    }
                    shiftDrawable = new WeakReference<>(this);
                    Runnable runnable = shiftRunnable;
                    if (runnable != null) {
                        AndroidUtilities.cancelRunOnUIThread(runnable);
                    }
                    SvgHelper$SvgDrawable$$ExternalSyntheticLambda0 svgHelper$SvgDrawable$$ExternalSyntheticLambda0 = SvgHelper$SvgDrawable$$ExternalSyntheticLambda0.INSTANCE;
                    shiftRunnable = svgHelper$SvgDrawable$$ExternalSyntheticLambda0;
                    AndroidUtilities.runOnUIThread(svgHelper$SvgDrawable$$ExternalSyntheticLambda0, (long) (((int) (1000.0f / AndroidUtilities.screenRefreshRate)) - 1));
                }
                ImageReceiver imageReceiver = this.parentImageReceiver;
                if (imageReceiver != null) {
                    imageReceiver.getParentPosition(parentPosition);
                }
                this.placeholderMatrix.reset();
                this.placeholderMatrix.postTranslate((((float) (-parentPosition[0])) + totalTranslation) - ((float) bounds.left), 0.0f);
                float f4 = 1.0f / max;
                this.placeholderMatrix.postScale(f4, f4);
                this.placeholderGradient.setLocalMatrix(this.placeholderMatrix);
                ImageReceiver imageReceiver2 = this.parentImageReceiver;
                if (imageReceiver2 != null) {
                    imageReceiver2.invalidate();
                }
            }
        }

        public void setAlpha(int i) {
            this.crossfadeAlpha = ((float) i) / 255.0f;
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

        public void setupGradient(String str, float f) {
            int color = Theme.getColor(str);
            if (this.currentColor != color) {
                this.colorAlpha = f;
                this.currentColorKey = str;
                this.currentColor = color;
                gradientWidth = (float) (AndroidUtilities.displaySize.x * 2);
                float dp = ((float) AndroidUtilities.dp(180.0f)) / gradientWidth;
                int argb = Color.argb((int) (((float) (Color.alpha(color) / 2)) * this.colorAlpha), Color.red(color), Color.green(color), Color.blue(color));
                float f2 = (1.0f - dp) / 2.0f;
                float f3 = dp / 2.0f;
                this.placeholderGradient = new LinearGradient(0.0f, 0.0f, gradientWidth, 0.0f, new int[]{0, 0, argb, 0, 0}, new float[]{0.0f, f2 - f3, f2, f2 + f3, 1.0f}, Shader.TileMode.REPEAT);
                if (Build.VERSION.SDK_INT >= 28) {
                    this.backgroundGradient = new LinearGradient(0.0f, 0.0f, gradientWidth, 0.0f, new int[]{argb, argb}, (float[]) null, Shader.TileMode.REPEAT);
                } else {
                    if (this.backgroundBitmap == null) {
                        this.backgroundBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
                        this.backgroundCanvas = new Canvas(this.backgroundBitmap);
                    }
                    this.backgroundCanvas.drawColor(argb);
                    Bitmap bitmap = this.backgroundBitmap;
                    Shader.TileMode tileMode = Shader.TileMode.REPEAT;
                    this.backgroundGradient = new BitmapShader(bitmap, tileMode, tileMode);
                }
                Matrix matrix = new Matrix();
                this.placeholderMatrix = matrix;
                this.placeholderGradient.setLocalMatrix(matrix);
                for (Paint next : this.paints.values()) {
                    if (Build.VERSION.SDK_INT <= 22) {
                        next.setShader(this.backgroundGradient);
                    } else {
                        next.setShader(new ComposeShader(this.placeholderGradient, this.backgroundGradient, PorterDuff.Mode.ADD));
                    }
                }
            }
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x003f */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getBitmap(int r8, int r9, int r10, int r11) {
        /*
            android.content.Context r0 = org.telegram.messenger.ApplicationLoader.applicationContext     // Catch:{ Exception -> 0x0040 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x0040 }
            java.io.InputStream r8 = r0.openRawResource(r8)     // Catch:{ Exception -> 0x0040 }
            javax.xml.parsers.SAXParserFactory r0 = javax.xml.parsers.SAXParserFactory.newInstance()     // Catch:{ all -> 0x0039 }
            javax.xml.parsers.SAXParser r0 = r0.newSAXParser()     // Catch:{ all -> 0x0039 }
            org.xml.sax.XMLReader r0 = r0.getXMLReader()     // Catch:{ all -> 0x0039 }
            org.telegram.messenger.SvgHelper$SVGHandler r7 = new org.telegram.messenger.SvgHelper$SVGHandler     // Catch:{ all -> 0x0039 }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x0039 }
            r5 = 0
            r6 = 0
            r1 = r7
            r2 = r9
            r3 = r10
            r1.<init>(r2, r3, r4, r5)     // Catch:{ all -> 0x0039 }
            r0.setContentHandler(r7)     // Catch:{ all -> 0x0039 }
            org.xml.sax.InputSource r9 = new org.xml.sax.InputSource     // Catch:{ all -> 0x0039 }
            r9.<init>(r8)     // Catch:{ all -> 0x0039 }
            r0.parse(r9)     // Catch:{ all -> 0x0039 }
            android.graphics.Bitmap r9 = r7.getBitmap()     // Catch:{ all -> 0x0039 }
            if (r8 == 0) goto L_0x0038
            r8.close()     // Catch:{ Exception -> 0x0040 }
        L_0x0038:
            return r9
        L_0x0039:
            r9 = move-exception
            if (r8 == 0) goto L_0x003f
            r8.close()     // Catch:{ all -> 0x003f }
        L_0x003f:
            throw r9     // Catch:{ Exception -> 0x0040 }
        L_0x0040:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
            r8 = 0
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.getBitmap(int, int, int, int):android.graphics.Bitmap");
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:15:0x003d */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Bitmap getBitmap(java.io.File r9, int r10, int r11, boolean r12) {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ Exception -> 0x003e }
            r1.<init>(r9)     // Catch:{ Exception -> 0x003e }
            javax.xml.parsers.SAXParserFactory r9 = javax.xml.parsers.SAXParserFactory.newInstance()     // Catch:{ all -> 0x0039 }
            javax.xml.parsers.SAXParser r9 = r9.newSAXParser()     // Catch:{ all -> 0x0039 }
            org.xml.sax.XMLReader r9 = r9.getXMLReader()     // Catch:{ all -> 0x0039 }
            org.telegram.messenger.SvgHelper$SVGHandler r8 = new org.telegram.messenger.SvgHelper$SVGHandler     // Catch:{ all -> 0x0039 }
            if (r12 == 0) goto L_0x001d
            r12 = -1
            java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ all -> 0x0039 }
            r5 = r12
            goto L_0x001e
        L_0x001d:
            r5 = r0
        L_0x001e:
            r6 = 0
            r7 = 0
            r2 = r8
            r3 = r10
            r4 = r11
            r2.<init>(r3, r4, r5, r6)     // Catch:{ all -> 0x0039 }
            r9.setContentHandler(r8)     // Catch:{ all -> 0x0039 }
            org.xml.sax.InputSource r10 = new org.xml.sax.InputSource     // Catch:{ all -> 0x0039 }
            r10.<init>(r1)     // Catch:{ all -> 0x0039 }
            r9.parse(r10)     // Catch:{ all -> 0x0039 }
            android.graphics.Bitmap r9 = r8.getBitmap()     // Catch:{ all -> 0x0039 }
            r1.close()     // Catch:{ Exception -> 0x003e }
            return r9
        L_0x0039:
            r9 = move-exception
            r1.close()     // Catch:{ all -> 0x003d }
        L_0x003d:
            throw r9     // Catch:{ Exception -> 0x003e }
        L_0x003e:
            r9 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r9)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.getBitmap(java.io.File, int, int, boolean):android.graphics.Bitmap");
    }

    public static Bitmap getBitmap(String str, int i, int i2, boolean z) {
        try {
            XMLReader xMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SVGHandler sVGHandler = new SVGHandler(i, i2, z ? -1 : null, false);
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
            SVGHandler sVGHandler = new SVGHandler(0, 0, (Integer) null, true);
            xMLReader.setContentHandler(sVGHandler);
            xMLReader.parse(new InputSource(new StringReader(str)));
            return sVGHandler.getDrawable();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static SvgDrawable getDrawable(int i, int i2) {
        try {
            XMLReader xMLReader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SVGHandler sVGHandler = new SVGHandler(0, 0, Integer.valueOf(i2), true);
            xMLReader.setContentHandler(sVGHandler);
            xMLReader.parse(new InputSource(ApplicationLoader.applicationContext.getResources().openRawResource(i)));
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
            svgDrawable.width = i;
            svgDrawable.height = i2;
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
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0064, code lost:
        if (r4 != 'V') goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0067, code lost:
        r2.advance();
        r4 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007d, code lost:
        r21 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00fd, code lost:
        r5 = r5 + r3;
        r6 = r6 + r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x018b, code lost:
        if (r21 != false) goto L_0x0191;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x018d, code lost:
        r16 = r5;
        r17 = r6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0079  */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0081  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0090  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00d8  */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x011c  */
    /* JADX WARNING: Removed duplicated region for block: B:62:0x0153  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Path doPath(java.lang.String r23) {
        /*
            r0 = r23
            int r1 = r23.length()
            org.telegram.messenger.SvgHelper$ParserHelper r2 = new org.telegram.messenger.SvgHelper$ParserHelper
            r3 = 0
            r2.<init>(r0, r3)
            r2.skipWhitespace()
            android.graphics.Path r14 = new android.graphics.Path
            r14.<init>()
            r4 = 0
            r5 = 0
            r6 = 0
            r12 = 0
            r13 = 0
            r16 = 0
            r17 = 0
        L_0x001d:
            int r7 = r2.pos
            if (r7 >= r1) goto L_0x0199
            char r7 = r0.charAt(r7)
            r8 = 43
            r10 = 115(0x73, float:1.61E-43)
            r11 = 108(0x6c, float:1.51E-43)
            r3 = 104(0x68, float:1.46E-43)
            r15 = 99
            r9 = 109(0x6d, float:1.53E-43)
            if (r7 == r8) goto L_0x003b
            r8 = 45
            if (r7 == r8) goto L_0x003b
            switch(r7) {
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
                default: goto L_0x003a;
            }
        L_0x003a:
            goto L_0x0067
        L_0x003b:
            if (r4 == r9) goto L_0x006e
            r8 = 77
            if (r4 != r8) goto L_0x0042
            goto L_0x006e
        L_0x0042:
            if (r4 == r15) goto L_0x006b
            r8 = 67
            if (r4 != r8) goto L_0x0049
            goto L_0x006b
        L_0x0049:
            if (r4 == r11) goto L_0x006b
            r8 = 76
            if (r4 != r8) goto L_0x0050
            goto L_0x006b
        L_0x0050:
            if (r4 == r10) goto L_0x006b
            r8 = 83
            if (r4 != r8) goto L_0x0057
            goto L_0x006b
        L_0x0057:
            if (r4 == r3) goto L_0x006b
            r8 = 72
            if (r4 != r8) goto L_0x005e
            goto L_0x006b
        L_0x005e:
            r8 = 118(0x76, float:1.65E-43)
            if (r4 == r8) goto L_0x006b
            r8 = 86
            if (r4 != r8) goto L_0x0067
            goto L_0x006b
        L_0x0067:
            r2.advance()
            r4 = r7
        L_0x006b:
            r20 = r4
            goto L_0x0074
        L_0x006e:
            int r7 = r4 + -1
            char r7 = (char) r7
            r20 = r4
            r4 = r7
        L_0x0074:
            r21 = 1
            switch(r4) {
                case 65: goto L_0x0153;
                case 67: goto L_0x011c;
                case 72: goto L_0x0108;
                case 76: goto L_0x00f0;
                case 77: goto L_0x00d8;
                case 83: goto L_0x00a3;
                case 86: goto L_0x0090;
                case 90: goto L_0x0081;
                case 97: goto L_0x0153;
                case 99: goto L_0x011c;
                case 104: goto L_0x0108;
                case 108: goto L_0x00f0;
                case 109: goto L_0x00d8;
                case 115: goto L_0x00a3;
                case 118: goto L_0x0090;
                case 122: goto L_0x0081;
                default: goto L_0x0079;
            }
        L_0x0079:
            r22 = r12
            r15 = r13
        L_0x007c:
            r3 = 0
        L_0x007d:
            r21 = 0
            goto L_0x018b
        L_0x0081:
            r14.close()
            r14.moveTo(r13, r12)
            r6 = r12
            r17 = r6
            r5 = r13
            r16 = r5
        L_0x008d:
            r3 = 0
            goto L_0x018b
        L_0x0090:
            float r3 = r2.nextFloat()
            r7 = 118(0x76, float:1.65E-43)
            if (r4 != r7) goto L_0x009e
            r4 = 0
            r14.rLineTo(r4, r3)
            float r6 = r6 + r3
            goto L_0x007c
        L_0x009e:
            r14.lineTo(r5, r3)
            r6 = r3
            goto L_0x007c
        L_0x00a3:
            float r3 = r2.nextFloat()
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            float r9 = r2.nextFloat()
            if (r4 != r10) goto L_0x00b9
            float r3 = r3 + r5
            float r8 = r8 + r5
            float r7 = r7 + r6
            float r9 = r9 + r6
        L_0x00b9:
            r11 = r7
            r15 = r8
            r19 = r9
            r4 = 1073741824(0x40000000, float:2.0)
            float r5 = r5 * r4
            float r5 = r5 - r16
            float r6 = r6 * r4
            float r6 = r6 - r17
            r4 = r14
            r7 = r3
            r8 = r11
            r9 = r15
            r10 = r19
            r4.cubicTo(r5, r6, r7, r8, r9, r10)
            r16 = r3
            r17 = r11
            r5 = r15
            r6 = r19
            goto L_0x008d
        L_0x00d8:
            float r3 = r2.nextFloat()
            float r7 = r2.nextFloat()
            if (r4 != r9) goto L_0x00e8
            float r13 = r13 + r3
            float r12 = r12 + r7
            r14.rMoveTo(r3, r7)
            goto L_0x00fd
        L_0x00e8:
            r14.moveTo(r3, r7)
            r5 = r3
            r13 = r5
            r6 = r7
            r12 = r6
            goto L_0x007c
        L_0x00f0:
            float r3 = r2.nextFloat()
            float r7 = r2.nextFloat()
            if (r4 != r11) goto L_0x0101
            r14.rLineTo(r3, r7)
        L_0x00fd:
            float r5 = r5 + r3
            float r6 = r6 + r7
            goto L_0x007c
        L_0x0101:
            r14.lineTo(r3, r7)
            r5 = r3
            r6 = r7
            goto L_0x007c
        L_0x0108:
            float r7 = r2.nextFloat()
            if (r4 != r3) goto L_0x0115
            r3 = 0
            r14.rLineTo(r7, r3)
            float r5 = r5 + r7
            goto L_0x007d
        L_0x0115:
            r3 = 0
            r14.lineTo(r7, r6)
            r5 = r7
            goto L_0x007d
        L_0x011c:
            r3 = 0
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            float r9 = r2.nextFloat()
            float r10 = r2.nextFloat()
            float r11 = r2.nextFloat()
            float r16 = r2.nextFloat()
            if (r4 != r15) goto L_0x013e
            float r7 = r7 + r5
            float r9 = r9 + r5
            float r11 = r11 + r5
            float r8 = r8 + r6
            float r10 = r10 + r6
            float r16 = r16 + r6
        L_0x013e:
            r5 = r7
            r6 = r8
            r15 = r9
            r17 = r10
            r4 = r14
            r7 = r15
            r8 = r17
            r9 = r11
            r10 = r16
            r4.cubicTo(r5, r6, r7, r8, r9, r10)
            r5 = r11
            r6 = r16
            r16 = r15
            goto L_0x018b
        L_0x0153:
            r3 = 0
            float r9 = r2.nextFloat()
            float r10 = r2.nextFloat()
            float r11 = r2.nextFloat()
            float r4 = r2.nextFloat()
            int r15 = (int) r4
            float r4 = r2.nextFloat()
            int r8 = (int) r4
            float r18 = r2.nextFloat()
            float r19 = r2.nextFloat()
            r4 = r14
            r7 = r18
            r21 = r8
            r8 = r19
            r22 = r12
            r12 = r15
            r15 = r13
            r13 = r21
            drawArc(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r13 = r15
            r5 = r18
            r6 = r19
            r12 = r22
            goto L_0x007d
        L_0x018b:
            if (r21 != 0) goto L_0x0191
            r16 = r5
            r17 = r6
        L_0x0191:
            r2.skipWhitespace()
            r4 = r20
            r3 = 0
            goto L_0x001d
        L_0x0199:
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

    /* access modifiers changed from: private */
    public static Float getFloatAttr(String str, Attributes attributes, Float f) {
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
                case -734239628: goto L_0x006c;
                case 112785: goto L_0x0061;
                case 3027034: goto L_0x0056;
                case 3068707: goto L_0x004b;
                case 3181155: goto L_0x0040;
                case 93818879: goto L_0x0035;
                case 98619139: goto L_0x002a;
                case 113101865: goto L_0x001f;
                case 828922025: goto L_0x0012;
                default: goto L_0x000f;
            }
        L_0x000f:
            r2 = -1
            goto L_0x0076
        L_0x0012:
            java.lang.String r0 = "magenta"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x001b
            goto L_0x000f
        L_0x001b:
            r2 = 8
            goto L_0x0076
        L_0x001f:
            java.lang.String r0 = "white"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0028
            goto L_0x000f
        L_0x0028:
            r2 = 7
            goto L_0x0076
        L_0x002a:
            java.lang.String r0 = "green"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0033
            goto L_0x000f
        L_0x0033:
            r2 = 6
            goto L_0x0076
        L_0x0035:
            java.lang.String r0 = "black"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x003e
            goto L_0x000f
        L_0x003e:
            r2 = 5
            goto L_0x0076
        L_0x0040:
            java.lang.String r0 = "gray"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0049
            goto L_0x000f
        L_0x0049:
            r2 = 4
            goto L_0x0076
        L_0x004b:
            java.lang.String r0 = "cyan"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0054
            goto L_0x000f
        L_0x0054:
            r2 = 3
            goto L_0x0076
        L_0x0056:
            java.lang.String r0 = "blue"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x005f
            goto L_0x000f
        L_0x005f:
            r2 = 2
            goto L_0x0076
        L_0x0061:
            java.lang.String r0 = "red"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x006a
            goto L_0x000f
        L_0x006a:
            r2 = 1
            goto L_0x0076
        L_0x006c:
            java.lang.String r0 = "yellow"
            boolean r2 = r2.equals(r0)
            if (r2 != 0) goto L_0x0075
            goto L_0x000f
        L_0x0075:
            r2 = 0
        L_0x0076:
            switch(r2) {
                case 0: goto L_0x00b6;
                case 1: goto L_0x00af;
                case 2: goto L_0x00a7;
                case 3: goto L_0x009f;
                case 4: goto L_0x0097;
                case 5: goto L_0x0090;
                case 6: goto L_0x0088;
                case 7: goto L_0x0083;
                case 8: goto L_0x007b;
                default: goto L_0x0079;
            }
        L_0x0079:
            r2 = 0
            return r2
        L_0x007b:
            r2 = -65281(0xfffffffffffvar_ff, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0083:
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)
            return r2
        L_0x0088:
            r2 = -16711936(0xfffffffffvar_fvar_, float:-1.7146522E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0090:
            r2 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x0097:
            r2 = -7829368(0xfffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x009f:
            r2 = -16711681(0xfffffffffvar_ffff, float:-1.714704E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00a7:
            r2 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00af:
            r2 = -65536(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            return r2
        L_0x00b6:
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
        private Integer paintColor;
        boolean pushed;
        private RectF rect;
        private RectF rectTmp;
        private float scale;
        private StringBuilder styles;

        public void endDocument() {
        }

        public void startDocument() {
        }

        private SVGHandler(int i, int i2, Integer num, boolean z) {
            this.scale = 1.0f;
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.rectTmp = new RectF();
            this.pushed = false;
            this.globalStyles = new HashMap<>();
            this.desiredWidth = i;
            this.desiredHeight = i2;
            this.paintColor = num;
            if (z) {
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
                    Integer num = this.paintColor;
                    if (num != null) {
                        this.paint.setColor(num.intValue());
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
            Integer num2 = this.paintColor;
            if (num2 != null) {
                this.paint.setColor(num2.intValue());
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
                SvgDrawable svgDrawable = this.drawable;
                if (svgDrawable != null) {
                    svgDrawable.addCommand(access$500);
                    return;
                }
                this.canvas.save();
                this.canvas.concat(access$500);
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
            String access$200;
            String str4 = str2;
            Attributes attributes2 = attributes;
            if (!this.boundsMode || str4.equals("style")) {
                str2.hashCode();
                char c = 65535;
                switch (str2.hashCode()) {
                    case -1656480802:
                        if (str4.equals("ellipse")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1360216880:
                        if (str4.equals("circle")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -397519558:
                        if (str4.equals("polygon")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 103:
                        if (str4.equals("g")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 114276:
                        if (str4.equals("svg")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 3079438:
                        if (str4.equals("defs")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 3321844:
                        if (str4.equals("line")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 3433509:
                        if (str4.equals("path")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 3496420:
                        if (str4.equals("rect")) {
                            c = 8;
                            break;
                        }
                        break;
                    case 109780401:
                        if (str4.equals("style")) {
                            c = 9;
                            break;
                        }
                        break;
                    case 561938880:
                        if (str4.equals("polyline")) {
                            c = 10;
                            break;
                        }
                        break;
                    case 917656469:
                        if (str4.equals("clipPath")) {
                            c = 11;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        Float access$700 = SvgHelper.getFloatAttr("cx", attributes2);
                        Float access$7002 = SvgHelper.getFloatAttr("cy", attributes2);
                        Float access$7003 = SvgHelper.getFloatAttr("rx", attributes2);
                        Float access$7004 = SvgHelper.getFloatAttr("ry", attributes2);
                        if (access$700 != null && access$7002 != null && access$7003 != null && access$7004 != null) {
                            pushTransform(attributes2);
                            Properties properties = new Properties(attributes2, this.globalStyles);
                            this.rect.set(access$700.floatValue() - access$7003.floatValue(), access$7002.floatValue() - access$7004.floatValue(), access$700.floatValue() + access$7003.floatValue(), access$7002.floatValue() + access$7004.floatValue());
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
                        Float access$7005 = SvgHelper.getFloatAttr("cx", attributes2);
                        Float access$7006 = SvgHelper.getFloatAttr("cy", attributes2);
                        Float access$7007 = SvgHelper.getFloatAttr("r", attributes2);
                        if (access$7005 != null && access$7006 != null && access$7007 != null) {
                            pushTransform(attributes2);
                            Properties properties2 = new Properties(attributes2, this.globalStyles);
                            if (doFill(properties2)) {
                                SvgDrawable svgDrawable3 = this.drawable;
                                if (svgDrawable3 != null) {
                                    svgDrawable3.addCommand(new Circle(access$7005.floatValue(), access$7006.floatValue(), access$7007.floatValue()), this.paint);
                                } else {
                                    this.canvas.drawCircle(access$7005.floatValue(), access$7006.floatValue(), access$7007.floatValue(), this.paint);
                                }
                            }
                            if (doStroke(properties2)) {
                                SvgDrawable svgDrawable4 = this.drawable;
                                if (svgDrawable4 != null) {
                                    svgDrawable4.addCommand(new Circle(access$7005.floatValue(), access$7006.floatValue(), access$7007.floatValue()), this.paint);
                                } else {
                                    this.canvas.drawCircle(access$7005.floatValue(), access$7006.floatValue(), access$7007.floatValue(), this.paint);
                                }
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 2:
                    case 10:
                        NumberParse access$1100 = SvgHelper.getNumberParseAttr("points", attributes2);
                        if (access$1100 != null) {
                            Path path = new Path();
                            ArrayList access$100 = access$1100.numbers;
                            if (access$100.size() > 1) {
                                pushTransform(attributes2);
                                Properties properties3 = new Properties(attributes2, this.globalStyles);
                                path.moveTo(((Float) access$100.get(0)).floatValue(), ((Float) access$100.get(1)).floatValue());
                                for (int i2 = 2; i2 < access$100.size(); i2 += 2) {
                                    path.lineTo(((Float) access$100.get(i2)).floatValue(), ((Float) access$100.get(i2 + 1)).floatValue());
                                }
                                if (str4.equals("polygon")) {
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
                        if ("bounds".equalsIgnoreCase(SvgHelper.getStringAttr("id", attributes2))) {
                            this.boundsMode = true;
                            return;
                        }
                        return;
                    case 4:
                        Float access$7008 = SvgHelper.getFloatAttr("width", attributes2);
                        Float access$7009 = SvgHelper.getFloatAttr("height", attributes2);
                        if ((access$7008 == null || access$7009 == null) && (access$200 = SvgHelper.getStringAttr("viewBox", attributes2)) != null) {
                            String[] split = access$200.split(" ");
                            Float valueOf = Float.valueOf(Float.parseFloat(split[2]));
                            access$7009 = Float.valueOf(Float.parseFloat(split[3]));
                            access$7008 = valueOf;
                        }
                        if (access$7008 == null || access$7009 == null) {
                            access$7008 = Float.valueOf((float) this.desiredWidth);
                            access$7009 = Float.valueOf((float) this.desiredHeight);
                        }
                        int ceil = (int) Math.ceil((double) access$7008.floatValue());
                        int ceil2 = (int) Math.ceil((double) access$7009.floatValue());
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
                        svgDrawable7.width = ceil;
                        svgDrawable7.height = ceil2;
                        return;
                    case 5:
                    case 11:
                        this.boundsMode = true;
                        return;
                    case 6:
                        Float access$70010 = SvgHelper.getFloatAttr("x1", attributes2);
                        Float access$70011 = SvgHelper.getFloatAttr("x2", attributes2);
                        Float access$70012 = SvgHelper.getFloatAttr("y1", attributes2);
                        Float access$70013 = SvgHelper.getFloatAttr("y2", attributes2);
                        if (doStroke(new Properties(attributes2, this.globalStyles))) {
                            pushTransform(attributes2);
                            SvgDrawable svgDrawable8 = this.drawable;
                            if (svgDrawable8 != null) {
                                svgDrawable8.addCommand(new Line(access$70010.floatValue(), access$70012.floatValue(), access$70011.floatValue(), access$70013.floatValue()), this.paint);
                            } else {
                                this.canvas.drawLine(access$70010.floatValue(), access$70012.floatValue(), access$70011.floatValue(), access$70013.floatValue(), this.paint);
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 7:
                        Path access$1200 = SvgHelper.doPath(SvgHelper.getStringAttr("d", attributes2));
                        pushTransform(attributes2);
                        Properties properties4 = new Properties(attributes2, this.globalStyles);
                        if (doFill(properties4)) {
                            SvgDrawable svgDrawable9 = this.drawable;
                            if (svgDrawable9 != null) {
                                svgDrawable9.addCommand(access$1200, this.paint);
                            } else {
                                this.canvas.drawPath(access$1200, this.paint);
                            }
                        }
                        if (doStroke(properties4)) {
                            SvgDrawable svgDrawable10 = this.drawable;
                            if (svgDrawable10 != null) {
                                svgDrawable10.addCommand(access$1200, this.paint);
                            } else {
                                this.canvas.drawPath(access$1200, this.paint);
                            }
                        }
                        popTransform();
                        return;
                    case 8:
                        Float access$70014 = SvgHelper.getFloatAttr("x", attributes2);
                        if (access$70014 == null) {
                            access$70014 = Float.valueOf(0.0f);
                        }
                        Float access$70015 = SvgHelper.getFloatAttr("y", attributes2);
                        if (access$70015 == null) {
                            access$70015 = Float.valueOf(0.0f);
                        }
                        Float access$70016 = SvgHelper.getFloatAttr("width", attributes2);
                        Float access$70017 = SvgHelper.getFloatAttr("height", attributes2);
                        Float access$800 = SvgHelper.getFloatAttr("rx", attributes2, (Float) null);
                        pushTransform(attributes2);
                        Properties properties5 = new Properties(attributes2, this.globalStyles);
                        if (doFill(properties5)) {
                            SvgDrawable svgDrawable11 = this.drawable;
                            if (svgDrawable11 != null) {
                                if (access$800 != null) {
                                    svgDrawable11.addCommand(new RoundRect(new RectF(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue()), access$800.floatValue()), this.paint);
                                } else {
                                    svgDrawable11.addCommand(new RectF(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue()), this.paint);
                                }
                            } else if (access$800 != null) {
                                this.rectTmp.set(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue());
                                this.canvas.drawRoundRect(this.rectTmp, access$800.floatValue(), access$800.floatValue(), this.paint);
                            } else {
                                this.canvas.drawRect(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue(), this.paint);
                            }
                        }
                        if (doStroke(properties5)) {
                            SvgDrawable svgDrawable12 = this.drawable;
                            if (svgDrawable12 != null) {
                                if (access$800 != null) {
                                    svgDrawable12.addCommand(new RoundRect(new RectF(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue()), access$800.floatValue()), this.paint);
                                } else {
                                    svgDrawable12.addCommand(new RectF(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue()), this.paint);
                                }
                            } else if (access$800 != null) {
                                this.rectTmp.set(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue());
                                this.canvas.drawRoundRect(this.rectTmp, access$800.floatValue(), access$800.floatValue(), this.paint);
                            } else {
                                this.canvas.drawRect(access$70014.floatValue(), access$70015.floatValue(), access$70014.floatValue() + access$70016.floatValue(), access$70015.floatValue() + access$70017.floatValue(), this.paint);
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
            double[] access$1300 = SvgHelper.pow10;
            if (i2 > 0) {
                double d3 = access$1300[i2];
                Double.isNaN(d2);
                d = d2 * d3;
            } else {
                double d4 = access$1300[-i2];
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

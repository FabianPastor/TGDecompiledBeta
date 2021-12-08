package org.telegram.messenger;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class SvgHelper {
    /* access modifiers changed from: private */
    public static final double[] pow10 = new double[128];

    private static class Line {
        float x1;
        float x2;
        float y1;
        float y2;

        public Line(float x12, float y12, float x22, float y22) {
            this.x1 = x12;
            this.y1 = y12;
            this.x2 = x22;
            this.y2 = y22;
        }
    }

    private static class Circle {
        float rad;
        float x1;
        float y1;

        public Circle(float x12, float y12, float rad2) {
            this.x1 = x12;
            this.y1 = y12;
            this.rad = rad2;
        }
    }

    private static class Oval {
        RectF rect;

        public Oval(RectF rect2) {
            this.rect = rect2;
        }
    }

    private static class RoundRect {
        RectF rect;
        float rx;

        public RoundRect(RectF rect2, float rx2) {
            this.rect = rect2;
            this.rx = rx2;
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

        public int getIntrinsicHeight() {
            return this.width;
        }

        public int getIntrinsicWidth() {
            return this.height;
        }

        public void setAspectFill(boolean value) {
            this.aspectFill = value;
        }

        public void overrideWidthAndHeight(int w, int h) {
            this.width = w;
            this.height = h;
        }

        public void draw(Canvas canvas) {
            float scaleX;
            Paint paint;
            int originalAlpha;
            Canvas canvas2 = canvas;
            String str = this.currentColorKey;
            if (str != null) {
                setupGradient(str, this.colorAlpha);
            }
            Rect bounds = getBounds();
            float scaleX2 = ((float) bounds.width()) / ((float) this.width);
            float scaleY = ((float) bounds.height()) / ((float) this.height);
            float scale = this.aspectFill ? Math.max(scaleX2, scaleY) : Math.min(scaleX2, scaleY);
            canvas.save();
            canvas2.translate((float) bounds.left, (float) bounds.top);
            if (!this.aspectFill) {
                canvas2.translate((((float) bounds.width()) - (((float) this.width) * scale)) / 2.0f, (((float) bounds.height()) - (((float) this.height) * scale)) / 2.0f);
            }
            canvas2.scale(scale, scale);
            int N = this.commands.size();
            int a = 0;
            while (a < N) {
                Object object = this.commands.get(a);
                if (object instanceof Matrix) {
                    canvas.save();
                    canvas2.concat((Matrix) object);
                    scaleX = scaleX2;
                } else if (object == null) {
                    canvas.restore();
                    scaleX = scaleX2;
                } else {
                    Paint paint2 = this.paints.get(object);
                    int originalAlpha2 = paint2.getAlpha();
                    paint2.setAlpha((int) (this.crossfadeAlpha * ((float) originalAlpha2)));
                    if (object instanceof Path) {
                        canvas2.drawPath((Path) object, paint2);
                        paint = paint2;
                        scaleX = scaleX2;
                        originalAlpha = originalAlpha2;
                    } else if (object instanceof Rect) {
                        canvas2.drawRect((Rect) object, paint2);
                        paint = paint2;
                        scaleX = scaleX2;
                        originalAlpha = originalAlpha2;
                    } else if (object instanceof RectF) {
                        canvas2.drawRect((RectF) object, paint2);
                        paint = paint2;
                        scaleX = scaleX2;
                        originalAlpha = originalAlpha2;
                    } else if (object instanceof Line) {
                        Line line = (Line) object;
                        float f = line.x1;
                        float f2 = line.y1;
                        float f3 = line.x2;
                        Line line2 = line;
                        scaleX = scaleX2;
                        originalAlpha = originalAlpha2;
                        float f4 = line.y2;
                        paint = paint2;
                        canvas.drawLine(f, f2, f3, f4, paint2);
                    } else {
                        paint = paint2;
                        scaleX = scaleX2;
                        originalAlpha = originalAlpha2;
                        if (object instanceof Circle) {
                            Circle circle = (Circle) object;
                            canvas2.drawCircle(circle.x1, circle.y1, circle.rad, paint);
                        } else if (object instanceof Oval) {
                            canvas2.drawOval(((Oval) object).rect, paint);
                        } else if (object instanceof RoundRect) {
                            RoundRect rect = (RoundRect) object;
                            canvas2.drawRoundRect(rect.rect, rect.rx, rect.rx, paint);
                        }
                    }
                    paint.setAlpha(originalAlpha);
                }
                a++;
                scaleX2 = scaleX;
            }
            canvas.restore();
            if (this.placeholderGradient != null) {
                if (shiftRunnable == null || shiftDrawable.get() == this) {
                    long newUpdateTime = SystemClock.elapsedRealtime();
                    long dt = Math.abs(lastUpdateTime - newUpdateTime);
                    if (dt > 17) {
                        dt = 16;
                    }
                    lastUpdateTime = newUpdateTime;
                    totalTranslation += (((float) dt) * gradientWidth) / 1800.0f;
                    while (true) {
                        float f5 = totalTranslation;
                        float f6 = gradientWidth;
                        if (f5 < f6 / 2.0f) {
                            break;
                        }
                        totalTranslation = f5 - f6;
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
                this.placeholderMatrix.postScale(1.0f / scale, 1.0f / scale);
                this.placeholderGradient.setLocalMatrix(this.placeholderMatrix);
                ImageReceiver imageReceiver2 = this.parentImageReceiver;
                if (imageReceiver2 != null) {
                    imageReceiver2.invalidate();
                }
            }
        }

        public void setAlpha(int alpha) {
            this.crossfadeAlpha = ((float) alpha) / 255.0f;
        }

        public void setColorFilter(ColorFilter colorFilter) {
        }

        public int getOpacity() {
            return -2;
        }

        /* access modifiers changed from: private */
        public void addCommand(Object command, Paint paint) {
            this.commands.add(command);
            this.paints.put(command, new Paint(paint));
        }

        /* access modifiers changed from: private */
        public void addCommand(Object command) {
            this.commands.add(command);
        }

        public void setParent(ImageReceiver imageReceiver) {
            this.parentImageReceiver = imageReceiver;
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v3, resolved type: android.graphics.LinearGradient} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v6, resolved type: android.graphics.BitmapShader} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v10, resolved type: android.graphics.LinearGradient} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r8v11, resolved type: android.graphics.LinearGradient} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void setupGradient(java.lang.String r22, float r23) {
            /*
                r21 = this;
                r0 = r21
                int r1 = org.telegram.ui.ActionBar.Theme.getColor(r22)
                int r2 = r0.currentColor
                if (r2 == r1) goto L_0x0103
                r2 = r23
                r0.colorAlpha = r2
                r3 = r22
                r0.currentColorKey = r3
                r0.currentColor = r1
                android.graphics.Point r4 = org.telegram.messenger.AndroidUtilities.displaySize
                int r4 = r4.x
                r5 = 2
                int r4 = r4 * 2
                float r4 = (float) r4
                gradientWidth = r4
                r4 = 1127481344(0x43340000, float:180.0)
                int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
                float r4 = (float) r4
                float r6 = gradientWidth
                float r4 = r4 / r6
                int r6 = android.graphics.Color.alpha(r1)
                int r6 = r6 / r5
                float r6 = (float) r6
                float r7 = r0.colorAlpha
                float r6 = r6 * r7
                int r6 = (int) r6
                int r7 = android.graphics.Color.red(r1)
                int r8 = android.graphics.Color.green(r1)
                int r9 = android.graphics.Color.blue(r1)
                int r1 = android.graphics.Color.argb(r6, r7, r8, r9)
                r6 = 1065353216(0x3var_, float:1.0)
                float r7 = r6 - r4
                r8 = 1073741824(0x40000000, float:2.0)
                float r7 = r7 / r8
                android.graphics.LinearGradient r15 = new android.graphics.LinearGradient
                r10 = 0
                r11 = 0
                float r12 = gradientWidth
                r9 = 5
                int[] r14 = new int[r9]
                r17 = 0
                r14[r17] = r17
                r13 = 1
                r14[r13] = r17
                r14[r5] = r1
                r18 = 3
                r14[r18] = r17
                r19 = 4
                r14[r19] = r17
                float[] r9 = new float[r9]
                r20 = 0
                r9[r17] = r20
                float r20 = r4 / r8
                float r20 = r7 - r20
                r9[r13] = r20
                r9[r5] = r7
                float r8 = r4 / r8
                float r8 = r8 + r7
                r9[r18] = r8
                r9[r19] = r6
                android.graphics.Shader$TileMode r6 = android.graphics.Shader.TileMode.REPEAT
                r8 = r9
                r9 = r15
                r13 = 0
                r5 = r15
                r15 = r8
                r16 = r6
                r9.<init>(r10, r11, r12, r13, r14, r15, r16)
                r0.placeholderGradient = r5
                int r5 = android.os.Build.VERSION.SDK_INT
                r6 = 28
                if (r5 < r6) goto L_0x00a4
                android.graphics.LinearGradient r5 = new android.graphics.LinearGradient
                r9 = 0
                r10 = 0
                float r11 = gradientWidth
                r12 = 0
                r6 = 2
                int[] r13 = new int[r6]
                r13[r17] = r1
                r6 = 1
                r13[r6] = r1
                r14 = 0
                android.graphics.Shader$TileMode r15 = android.graphics.Shader.TileMode.REPEAT
                r8 = r5
                r8.<init>(r9, r10, r11, r12, r13, r14, r15)
                goto L_0x00ca
            L_0x00a4:
                r6 = 1
                android.graphics.Bitmap r5 = r0.backgroundBitmap
                if (r5 != 0) goto L_0x00ba
                android.graphics.Bitmap$Config r5 = android.graphics.Bitmap.Config.ARGB_8888
                android.graphics.Bitmap r5 = android.graphics.Bitmap.createBitmap(r6, r6, r5)
                r0.backgroundBitmap = r5
                android.graphics.Canvas r5 = new android.graphics.Canvas
                android.graphics.Bitmap r6 = r0.backgroundBitmap
                r5.<init>(r6)
                r0.backgroundCanvas = r5
            L_0x00ba:
                android.graphics.Canvas r5 = r0.backgroundCanvas
                r5.drawColor(r1)
                android.graphics.BitmapShader r5 = new android.graphics.BitmapShader
                android.graphics.Bitmap r6 = r0.backgroundBitmap
                android.graphics.Shader$TileMode r8 = android.graphics.Shader.TileMode.REPEAT
                android.graphics.Shader$TileMode r9 = android.graphics.Shader.TileMode.REPEAT
                r5.<init>(r6, r8, r9)
            L_0x00ca:
                android.graphics.Matrix r6 = new android.graphics.Matrix
                r6.<init>()
                r0.placeholderMatrix = r6
                android.graphics.LinearGradient r8 = r0.placeholderGradient
                r8.setLocalMatrix(r6)
                java.util.HashMap<java.lang.Object, android.graphics.Paint> r6 = r0.paints
                java.util.Collection r6 = r6.values()
                java.util.Iterator r6 = r6.iterator()
            L_0x00e0:
                boolean r8 = r6.hasNext()
                if (r8 == 0) goto L_0x0107
                java.lang.Object r8 = r6.next()
                android.graphics.Paint r8 = (android.graphics.Paint) r8
                int r9 = android.os.Build.VERSION.SDK_INT
                r10 = 22
                if (r9 > r10) goto L_0x00f6
                r8.setShader(r5)
                goto L_0x0102
            L_0x00f6:
                android.graphics.ComposeShader r9 = new android.graphics.ComposeShader
                android.graphics.LinearGradient r10 = r0.placeholderGradient
                android.graphics.PorterDuff$Mode r11 = android.graphics.PorterDuff.Mode.ADD
                r9.<init>(r10, r5, r11)
                r8.setShader(r9)
            L_0x0102:
                goto L_0x00e0
            L_0x0103:
                r3 = r22
                r2 = r23
            L_0x0107:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.SvgDrawable.setupGradient(java.lang.String, float):void");
        }
    }

    public static Bitmap getBitmap(int res, int width, int height, int color) {
        return getBitmap(res, width, height, color, 1.0f);
    }

    public static Bitmap getBitmap(int res, int width, int height, int color, float scale) {
        InputStream stream;
        Throwable th;
        try {
            int i = res;
            try {
                stream = ApplicationLoader.applicationContext.getResources().openRawResource(res);
                XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                SVGHandler handler = new SVGHandler(width, height, Integer.valueOf(color), false, scale);
                xr.setContentHandler(handler);
                xr.parse(new InputSource(stream));
                Bitmap bitmap = handler.getBitmap();
                if (stream != null) {
                    stream.close();
                }
                return bitmap;
            } catch (Exception e) {
                e = e;
            } catch (Throwable th2) {
            }
            throw th;
        } catch (Exception e2) {
            e = e2;
            int i2 = res;
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static Bitmap getBitmap(File file, int width, int height, boolean white) {
        FileInputStream stream;
        Throwable th;
        try {
            File file2 = file;
            try {
                stream = new FileInputStream(file);
                XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
                SVGHandler handler = new SVGHandler(width, height, white ? -1 : null, false, 1.0f);
                xr.setContentHandler(handler);
                xr.parse(new InputSource(stream));
                Bitmap bitmap = handler.getBitmap();
                stream.close();
                return bitmap;
            } catch (Exception e) {
                e = e;
            } catch (Throwable th2) {
            }
            throw th;
        } catch (Exception e2) {
            e = e2;
            File file3 = file;
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static Bitmap getBitmap(String xml, int width, int height, boolean white) {
        try {
            XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SVGHandler handler = new SVGHandler(width, height, white ? -1 : null, false, 1.0f);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new StringReader(xml)));
            return handler.getBitmap();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static SvgDrawable getDrawable(String xml) {
        try {
            XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SVGHandler handler = new SVGHandler(0, 0, (Integer) null, true, 1.0f);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(new StringReader(xml)));
            return handler.getDrawable();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static SvgDrawable getDrawable(int resId, int color) {
        try {
            XMLReader xr = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            SVGHandler handler = new SVGHandler(0, 0, Integer.valueOf(color), true, 1.0f);
            xr.setContentHandler(handler);
            xr.parse(new InputSource(ApplicationLoader.applicationContext.getResources().openRawResource(resId)));
            return handler.getDrawable();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static SvgDrawable getDrawableByPath(String pathString, int w, int h) {
        try {
            Path path = doPath(pathString);
            SvgDrawable drawable = new SvgDrawable();
            drawable.commands.add(path);
            drawable.paints.put(path, new Paint(1));
            drawable.width = w;
            drawable.height = h;
            return drawable;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    public static Bitmap getBitmapByPathOnly(String pathString, int svgWidth, int svgHeight, int width, int height) {
        try {
            Path path = doPath(pathString);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            canvas.scale(((float) width) / ((float) svgWidth), ((float) height) / ((float) svgHeight));
            Paint paint = new Paint();
            paint.setColor(-1);
            canvas.drawPath(path, paint);
            return bitmap;
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return null;
        }
    }

    private static NumberParse parseNumbers(String s) {
        int n = s.length();
        int p = 0;
        ArrayList<Float> numbers = new ArrayList<>();
        boolean skipChar = false;
        for (int i = 1; i < n; i++) {
            if (skipChar) {
                skipChar = false;
            } else {
                char c = s.charAt(i);
                switch (c) {
                    case 9:
                    case 10:
                    case ' ':
                    case ',':
                    case '-':
                        if (c != '-' || s.charAt(i - 1) != 'e') {
                            String str = s.substring(p, i);
                            if (str.trim().length() <= 0) {
                                p++;
                                break;
                            } else {
                                numbers.add(Float.valueOf(Float.parseFloat(str)));
                                if (c != '-') {
                                    p = i + 1;
                                    skipChar = true;
                                    break;
                                } else {
                                    p = i;
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
                        String str2 = s.substring(p, i);
                        if (str2.trim().length() > 0) {
                            numbers.add(Float.valueOf(Float.parseFloat(str2)));
                        }
                        return new NumberParse(numbers, i);
                }
            }
        }
        String last = s.substring(p);
        if (last.length() > 0) {
            try {
                numbers.add(Float.valueOf(Float.parseFloat(last)));
            } catch (NumberFormatException e) {
            }
            p = s.length();
        }
        return new NumberParse(numbers, p);
    }

    /* access modifiers changed from: private */
    public static Matrix parseTransform(String s) {
        if (s.startsWith("matrix(")) {
            NumberParse np = parseNumbers(s.substring("matrix(".length()));
            if (np.numbers.size() != 6) {
                return null;
            }
            Matrix matrix = new Matrix();
            matrix.setValues(new float[]{((Float) np.numbers.get(0)).floatValue(), ((Float) np.numbers.get(2)).floatValue(), ((Float) np.numbers.get(4)).floatValue(), ((Float) np.numbers.get(1)).floatValue(), ((Float) np.numbers.get(3)).floatValue(), ((Float) np.numbers.get(5)).floatValue(), 0.0f, 0.0f, 1.0f});
            return matrix;
        } else if (s.startsWith("translate(")) {
            NumberParse np2 = parseNumbers(s.substring("translate(".length()));
            if (np2.numbers.size() <= 0) {
                return null;
            }
            float tx = ((Float) np2.numbers.get(0)).floatValue();
            float ty = 0.0f;
            if (np2.numbers.size() > 1) {
                ty = ((Float) np2.numbers.get(1)).floatValue();
            }
            Matrix matrix2 = new Matrix();
            matrix2.postTranslate(tx, ty);
            return matrix2;
        } else if (s.startsWith("scale(")) {
            NumberParse np3 = parseNumbers(s.substring("scale(".length()));
            if (np3.numbers.size() <= 0) {
                return null;
            }
            float sx = ((Float) np3.numbers.get(0)).floatValue();
            float sy = 0.0f;
            if (np3.numbers.size() > 1) {
                sy = ((Float) np3.numbers.get(1)).floatValue();
            }
            Matrix matrix3 = new Matrix();
            matrix3.postScale(sx, sy);
            return matrix3;
        } else if (s.startsWith("skewX(")) {
            NumberParse np4 = parseNumbers(s.substring("skewX(".length()));
            if (np4.numbers.size() <= 0) {
                return null;
            }
            float angle = ((Float) np4.numbers.get(0)).floatValue();
            Matrix matrix4 = new Matrix();
            matrix4.postSkew((float) Math.tan((double) angle), 0.0f);
            return matrix4;
        } else if (s.startsWith("skewY(")) {
            NumberParse np5 = parseNumbers(s.substring("skewY(".length()));
            if (np5.numbers.size() <= 0) {
                return null;
            }
            float angle2 = ((Float) np5.numbers.get(0)).floatValue();
            Matrix matrix5 = new Matrix();
            matrix5.postSkew(0.0f, (float) Math.tan((double) angle2));
            return matrix5;
        } else if (!s.startsWith("rotate(")) {
            return null;
        } else {
            NumberParse np6 = parseNumbers(s.substring("rotate(".length()));
            if (np6.numbers.size() <= 0) {
                return null;
            }
            float angle3 = ((Float) np6.numbers.get(0)).floatValue();
            float cx = 0.0f;
            float cy = 0.0f;
            if (np6.numbers.size() > 2) {
                cx = ((Float) np6.numbers.get(1)).floatValue();
                cy = ((Float) np6.numbers.get(2)).floatValue();
            }
            Matrix matrix6 = new Matrix();
            matrix6.postTranslate(cx, cy);
            matrix6.postRotate(angle3);
            matrix6.postTranslate(-cx, -cy);
            return matrix6;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static android.graphics.Path doPath(java.lang.String r30) {
        /*
            r0 = r30
            int r1 = r30.length()
            org.telegram.messenger.SvgHelper$ParserHelper r2 = new org.telegram.messenger.SvgHelper$ParserHelper
            r3 = 0
            r2.<init>(r0, r3)
            r2.skipWhitespace()
            android.graphics.Path r3 = new android.graphics.Path
            r3.<init>()
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r14 = r4
            r15 = r5
            r16 = r6
            r17 = r7
            r13 = r8
            r12 = r9
        L_0x0023:
            int r4 = r2.pos
            if (r4 >= r1) goto L_0x0220
            int r4 = r2.pos
            char r4 = r0.charAt(r4)
            r5 = 118(0x76, float:1.65E-43)
            r6 = 115(0x73, float:1.61E-43)
            r7 = 108(0x6c, float:1.51E-43)
            r8 = 104(0x68, float:1.46E-43)
            r9 = 99
            r11 = 109(0x6d, float:1.53E-43)
            switch(r4) {
                case 43: goto L_0x003e;
                case 44: goto L_0x003c;
                case 45: goto L_0x003e;
                case 46: goto L_0x003c;
                case 47: goto L_0x003c;
                case 48: goto L_0x003e;
                case 49: goto L_0x003e;
                case 50: goto L_0x003e;
                case 51: goto L_0x003e;
                case 52: goto L_0x003e;
                case 53: goto L_0x003e;
                case 54: goto L_0x003e;
                case 55: goto L_0x003e;
                case 56: goto L_0x003e;
                case 57: goto L_0x003e;
                default: goto L_0x003c;
            }
        L_0x003c:
            goto L_0x0087
        L_0x003e:
            if (r10 == r11) goto L_0x0080
            r11 = 77
            if (r10 != r11) goto L_0x0045
            goto L_0x0080
        L_0x0045:
            if (r10 == r9) goto L_0x007b
            r11 = 67
            if (r10 != r11) goto L_0x004c
            goto L_0x007b
        L_0x004c:
            if (r10 == r7) goto L_0x0076
            r11 = 76
            if (r10 != r11) goto L_0x0053
            goto L_0x0076
        L_0x0053:
            if (r10 == r6) goto L_0x0071
            r11 = 83
            if (r10 != r11) goto L_0x005a
            goto L_0x0071
        L_0x005a:
            if (r10 == r8) goto L_0x006c
            r11 = 72
            if (r10 != r11) goto L_0x0061
            goto L_0x006c
        L_0x0061:
            if (r10 == r5) goto L_0x0067
            r11 = 86
            if (r10 != r11) goto L_0x0087
        L_0x0067:
            r4 = r10
            r11 = r4
            r19 = r10
            goto L_0x008e
        L_0x006c:
            r4 = r10
            r11 = r4
            r19 = r10
            goto L_0x008e
        L_0x0071:
            r4 = r10
            r11 = r4
            r19 = r10
            goto L_0x008e
        L_0x0076:
            r4 = r10
            r11 = r4
            r19 = r10
            goto L_0x008e
        L_0x007b:
            r4 = r10
            r11 = r4
            r19 = r10
            goto L_0x008e
        L_0x0080:
            int r11 = r10 + -1
            char r4 = (char) r11
            r11 = r4
            r19 = r10
            goto L_0x008e
        L_0x0087:
            r2.advance()
            r10 = r4
            r11 = r4
            r19 = r10
        L_0x008e:
            r20 = 0
            r4 = 0
            switch(r11) {
                case 65: goto L_0x01cb;
                case 67: goto L_0x0173;
                case 72: goto L_0x015c;
                case 76: goto L_0x013e;
                case 77: goto L_0x0118;
                case 83: goto L_0x00c7;
                case 86: goto L_0x00b0;
                case 90: goto L_0x009c;
                case 97: goto L_0x01cb;
                case 99: goto L_0x0173;
                case 104: goto L_0x015c;
                case 108: goto L_0x013e;
                case 109: goto L_0x0118;
                case 115: goto L_0x00c7;
                case 118: goto L_0x00b0;
                case 122: goto L_0x009c;
                default: goto L_0x0094;
            }
        L_0x0094:
            r27 = r11
            r28 = r12
            r29 = r13
            goto L_0x0211
        L_0x009c:
            r3.close()
            r3.moveTo(r13, r12)
            r4 = r13
            r5 = r12
            r16 = r13
            r17 = r12
            r20 = 1
            r14 = r4
            r15 = r5
            r27 = r11
            goto L_0x0211
        L_0x00b0:
            float r6 = r2.nextFloat()
            if (r11 != r5) goto L_0x00be
            r3.rLineTo(r4, r6)
            float r15 = r15 + r6
            r27 = r11
            goto L_0x0211
        L_0x00be:
            r3.lineTo(r14, r6)
            r4 = r6
            r15 = r4
            r27 = r11
            goto L_0x0211
        L_0x00c7:
            r20 = 1
            float r4 = r2.nextFloat()
            float r5 = r2.nextFloat()
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            if (r11 != r6) goto L_0x00e8
            float r4 = r4 + r14
            float r7 = r7 + r14
            float r5 = r5 + r15
            float r8 = r8 + r15
            r18 = r4
            r21 = r5
            r22 = r7
            r23 = r8
            goto L_0x00f0
        L_0x00e8:
            r18 = r4
            r21 = r5
            r22 = r7
            r23 = r8
        L_0x00f0:
            r4 = 1073741824(0x40000000, float:2.0)
            float r5 = r14 * r4
            float r24 = r5 - r16
            float r4 = r4 * r15
            float r25 = r4 - r17
            r4 = r3
            r5 = r24
            r6 = r25
            r7 = r18
            r8 = r21
            r9 = r22
            r10 = r23
            r4.cubicTo(r5, r6, r7, r8, r9, r10)
            r16 = r18
            r17 = r21
            r4 = r22
            r5 = r23
            r14 = r4
            r15 = r5
            r27 = r11
            goto L_0x0211
        L_0x0118:
            float r4 = r2.nextFloat()
            float r5 = r2.nextFloat()
            r6 = 109(0x6d, float:1.53E-43)
            if (r11 != r6) goto L_0x012f
            float r13 = r13 + r4
            float r12 = r12 + r5
            r3.rMoveTo(r4, r5)
            float r14 = r14 + r4
            float r15 = r15 + r5
            r27 = r11
            goto L_0x0211
        L_0x012f:
            r6 = r4
            r7 = r5
            r3.moveTo(r4, r5)
            r8 = r4
            r9 = r5
            r13 = r6
            r12 = r7
            r14 = r8
            r15 = r9
            r27 = r11
            goto L_0x0211
        L_0x013e:
            float r4 = r2.nextFloat()
            float r5 = r2.nextFloat()
            if (r11 != r7) goto L_0x0151
            r3.rLineTo(r4, r5)
            float r14 = r14 + r4
            float r15 = r15 + r5
            r27 = r11
            goto L_0x0211
        L_0x0151:
            r3.lineTo(r4, r5)
            r6 = r4
            r7 = r5
            r14 = r6
            r15 = r7
            r27 = r11
            goto L_0x0211
        L_0x015c:
            float r5 = r2.nextFloat()
            if (r11 != r8) goto L_0x016a
            r3.rLineTo(r5, r4)
            float r14 = r14 + r5
            r27 = r11
            goto L_0x0211
        L_0x016a:
            r3.lineTo(r5, r15)
            r4 = r5
            r14 = r4
            r27 = r11
            goto L_0x0211
        L_0x0173:
            r20 = 1
            float r4 = r2.nextFloat()
            float r5 = r2.nextFloat()
            float r6 = r2.nextFloat()
            float r7 = r2.nextFloat()
            float r8 = r2.nextFloat()
            float r10 = r2.nextFloat()
            if (r11 != r9) goto L_0x01a2
            float r4 = r4 + r14
            float r6 = r6 + r14
            float r8 = r8 + r14
            float r5 = r5 + r15
            float r7 = r7 + r15
            float r10 = r10 + r15
            r18 = r4
            r21 = r5
            r22 = r6
            r23 = r7
            r24 = r8
            r25 = r10
            goto L_0x01ae
        L_0x01a2:
            r18 = r4
            r21 = r5
            r22 = r6
            r23 = r7
            r24 = r8
            r25 = r10
        L_0x01ae:
            r4 = r3
            r5 = r18
            r6 = r21
            r7 = r22
            r8 = r23
            r9 = r24
            r10 = r25
            r4.cubicTo(r5, r6, r7, r8, r9, r10)
            r16 = r22
            r17 = r23
            r4 = r24
            r5 = r25
            r14 = r4
            r15 = r5
            r27 = r11
            goto L_0x0211
        L_0x01cb:
            float r18 = r2.nextFloat()
            float r21 = r2.nextFloat()
            float r22 = r2.nextFloat()
            float r4 = r2.nextFloat()
            int r10 = (int) r4
            float r4 = r2.nextFloat()
            int r9 = (int) r4
            float r23 = r2.nextFloat()
            float r24 = r2.nextFloat()
            r4 = r3
            r5 = r14
            r6 = r15
            r7 = r23
            r8 = r24
            r25 = r9
            r9 = r18
            r26 = r10
            r10 = r21
            r27 = r11
            r11 = r22
            r28 = r12
            r12 = r26
            r29 = r13
            r13 = r25
            drawArc(r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)
            r4 = r23
            r5 = r24
            r14 = r4
            r15 = r5
            r12 = r28
            r13 = r29
        L_0x0211:
            if (r20 != 0) goto L_0x0219
            r4 = r14
            r5 = r15
            r16 = r4
            r17 = r5
        L_0x0219:
            r2.skipWhitespace()
            r10 = r19
            goto L_0x0023
        L_0x0220:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.doPath(java.lang.String):android.graphics.Path");
    }

    private static void drawArc(Path p, float lastX, float lastY, float x, float y, float rx, float ry, float theta, int largeArc, int sweepArc) {
    }

    /* access modifiers changed from: private */
    public static NumberParse getNumberParseAttr(String name, Attributes attributes) {
        int n = attributes.getLength();
        for (int i = 0; i < n; i++) {
            if (attributes.getLocalName(i).equals(name)) {
                return parseNumbers(attributes.getValue(i));
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static String getStringAttr(String name, Attributes attributes) {
        int n = attributes.getLength();
        for (int i = 0; i < n; i++) {
            if (attributes.getLocalName(i).equals(name)) {
                return attributes.getValue(i);
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static Float getFloatAttr(String name, Attributes attributes) {
        return getFloatAttr(name, attributes, (Float) null);
    }

    /* access modifiers changed from: private */
    public static Float getFloatAttr(String name, Attributes attributes, Float defaultValue) {
        String v = getStringAttr(name, attributes);
        if (v == null) {
            return defaultValue;
        }
        if (v.endsWith("px")) {
            v = v.substring(0, v.length() - 2);
        } else if (v.endsWith("mm")) {
            return null;
        }
        return Float.valueOf(Float.parseFloat(v));
    }

    private static Integer getHexAttr(String name, Attributes attributes) {
        String v = getStringAttr(name, attributes);
        if (v == null) {
            return null;
        }
        try {
            return Integer.valueOf(Integer.parseInt(v.substring(1), 16));
        } catch (NumberFormatException e) {
            return getColorByName(v);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.Integer getColorByName(java.lang.String r3) {
        /*
            java.lang.String r0 = r3.toLowerCase()
            int r1 = r0.hashCode()
            r2 = -1
            switch(r1) {
                case -734239628: goto L_0x0060;
                case 112785: goto L_0x0055;
                case 3027034: goto L_0x004b;
                case 3068707: goto L_0x0041;
                case 3181155: goto L_0x0037;
                case 93818879: goto L_0x002d;
                case 98619139: goto L_0x0023;
                case 113101865: goto L_0x0017;
                case 828922025: goto L_0x000d;
                default: goto L_0x000c;
            }
        L_0x000c:
            goto L_0x006b
        L_0x000d:
            java.lang.String r1 = "magenta"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 7
            goto L_0x006c
        L_0x0017:
            java.lang.String r1 = "white"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 8
            goto L_0x006c
        L_0x0023:
            java.lang.String r1 = "green"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 3
            goto L_0x006c
        L_0x002d:
            java.lang.String r1 = "black"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 0
            goto L_0x006c
        L_0x0037:
            java.lang.String r1 = "gray"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 1
            goto L_0x006c
        L_0x0041:
            java.lang.String r1 = "cyan"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 6
            goto L_0x006c
        L_0x004b:
            java.lang.String r1 = "blue"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 4
            goto L_0x006c
        L_0x0055:
            java.lang.String r1 = "red"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 2
            goto L_0x006c
        L_0x0060:
            java.lang.String r1 = "yellow"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x000c
            r0 = 5
            goto L_0x006c
        L_0x006b:
            r0 = -1
        L_0x006c:
            switch(r0) {
                case 0: goto L_0x00ac;
                case 1: goto L_0x00a4;
                case 2: goto L_0x009d;
                case 3: goto L_0x0095;
                case 4: goto L_0x008d;
                case 5: goto L_0x0086;
                case 6: goto L_0x007e;
                case 7: goto L_0x0076;
                case 8: goto L_0x0071;
                default: goto L_0x006f;
            }
        L_0x006f:
            r0 = 0
            return r0
        L_0x0071:
            java.lang.Integer r0 = java.lang.Integer.valueOf(r2)
            return r0
        L_0x0076:
            r0 = -65281(0xfffffffffffvar_ff, float:NaN)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x007e:
            r0 = -16711681(0xfffffffffvar_ffff, float:-1.714704E38)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x0086:
            r0 = -256(0xfffffffffffffvar_, float:NaN)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x008d:
            r0 = -16776961(0xfffffffffvar_ff, float:-1.7014636E38)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x0095:
            r0 = -16711936(0xfffffffffvar_fvar_, float:-1.7146522E38)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x009d:
            r0 = -65536(0xfffffffffffvar_, float:NaN)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x00a4:
            r0 = -7829368(0xfffffffffvar_, float:NaN)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        L_0x00ac:
            r0 = -16777216(0xfffffffffvar_, float:-1.7014118E38)
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.getColorByName(java.lang.String):java.lang.Integer");
    }

    private static class NumberParse {
        private int nextCmd;
        /* access modifiers changed from: private */
        public ArrayList<Float> numbers;

        public NumberParse(ArrayList<Float> numbers2, int nextCmd2) {
            this.numbers = numbers2;
            this.nextCmd = nextCmd2;
        }

        public int getNextCmd() {
            return this.nextCmd;
        }

        public float getNumber(int index) {
            return this.numbers.get(index).floatValue();
        }
    }

    private static class StyleSet {
        HashMap<String, String> styleMap;

        private StyleSet(StyleSet styleSet) {
            HashMap<String, String> hashMap = new HashMap<>();
            this.styleMap = hashMap;
            hashMap.putAll(styleSet.styleMap);
        }

        private StyleSet(String string) {
            this.styleMap = new HashMap<>();
            for (String s : string.split(";")) {
                String[] style = s.split(":");
                if (style.length == 2) {
                    this.styleMap.put(style[0].trim(), style[1].trim());
                }
            }
        }

        public String getStyle(String name) {
            return this.styleMap.get(name);
        }
    }

    private static class Properties {
        Attributes atts;
        ArrayList<StyleSet> styles;

        private Properties(Attributes atts2, HashMap<String, StyleSet> globalStyles) {
            this.atts = atts2;
            String styleAttr = SvgHelper.getStringAttr("style", atts2);
            if (styleAttr != null) {
                ArrayList<StyleSet> arrayList = new ArrayList<>();
                this.styles = arrayList;
                arrayList.add(new StyleSet(styleAttr));
                return;
            }
            String classAttr = SvgHelper.getStringAttr("class", atts2);
            if (classAttr != null) {
                this.styles = new ArrayList<>();
                String[] args = classAttr.split(" ");
                for (String trim : args) {
                    StyleSet set = globalStyles.get(trim.trim());
                    if (set != null) {
                        this.styles.add(set);
                    }
                }
            }
        }

        public String getAttr(String name) {
            String v = null;
            ArrayList<StyleSet> arrayList = this.styles;
            if (arrayList != null && !arrayList.isEmpty()) {
                int N = this.styles.size();
                for (int a = 0; a < N; a++) {
                    v = this.styles.get(a).getStyle(name);
                    if (v != null) {
                        break;
                    }
                }
            }
            if (v == null) {
                return SvgHelper.getStringAttr(name, this.atts);
            }
            return v;
        }

        public String getString(String name) {
            return getAttr(name);
        }

        public Integer getHex(String name) {
            String v = getAttr(name);
            if (v == null) {
                return null;
            }
            try {
                return Integer.valueOf(Integer.parseInt(v.substring(1), 16));
            } catch (NumberFormatException e) {
                return SvgHelper.getColorByName(v);
            }
        }

        public Float getFloat(String name, float defaultValue) {
            Float v = getFloat(name);
            if (v == null) {
                return Float.valueOf(defaultValue);
            }
            return v;
        }

        public Float getFloat(String name) {
            String v = getAttr(name);
            if (v == null) {
                return null;
            }
            try {
                return Float.valueOf(Float.parseFloat(v));
            } catch (NumberFormatException e) {
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
        private float globalScale;
        private HashMap<String, StyleSet> globalStyles;
        private Paint paint;
        private Integer paintColor;
        boolean pushed;
        private RectF rect;
        private RectF rectTmp;
        private float scale;
        private StringBuilder styles;

        private SVGHandler(int dw, int dh, Integer color, boolean asDrawable, float scale2) {
            this.scale = 1.0f;
            this.paint = new Paint(1);
            this.rect = new RectF();
            this.rectTmp = new RectF();
            this.globalScale = 1.0f;
            this.pushed = false;
            this.globalStyles = new HashMap<>();
            this.globalScale = scale2;
            this.desiredWidth = dw;
            this.desiredHeight = dh;
            this.paintColor = color;
            if (asDrawable) {
                this.drawable = new SvgDrawable();
            }
        }

        public void startDocument() {
        }

        public void endDocument() {
        }

        private boolean doFill(Properties atts) {
            if ("none".equals(atts.getString("display"))) {
                return false;
            }
            String fillString = atts.getString("fill");
            if (fillString == null || !fillString.startsWith("url(#")) {
                Integer color = atts.getHex("fill");
                if (color != null) {
                    doColor(atts, color, true);
                    this.paint.setStyle(Paint.Style.FILL);
                    return true;
                } else if (atts.getString("fill") != null || atts.getString("stroke") != null) {
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
                String substring = fillString.substring("url(#".length(), fillString.length() - 1);
                return false;
            }
        }

        private boolean doStroke(Properties atts) {
            Integer color;
            if ("none".equals(atts.getString("display")) || (color = atts.getHex("stroke")) == null) {
                return false;
            }
            doColor(atts, color, false);
            Float width = atts.getFloat("stroke-width");
            if (width != null) {
                this.paint.setStrokeWidth(width.floatValue());
            }
            String linecap = atts.getString("stroke-linecap");
            if ("round".equals(linecap)) {
                this.paint.setStrokeCap(Paint.Cap.ROUND);
            } else if ("square".equals(linecap)) {
                this.paint.setStrokeCap(Paint.Cap.SQUARE);
            } else if ("butt".equals(linecap)) {
                this.paint.setStrokeCap(Paint.Cap.BUTT);
            }
            String linejoin = atts.getString("stroke-linejoin");
            if ("miter".equals(linejoin)) {
                this.paint.setStrokeJoin(Paint.Join.MITER);
            } else if ("round".equals(linejoin)) {
                this.paint.setStrokeJoin(Paint.Join.ROUND);
            } else if ("bevel".equals(linejoin)) {
                this.paint.setStrokeJoin(Paint.Join.BEVEL);
            }
            this.paint.setStyle(Paint.Style.STROKE);
            return true;
        }

        private void doColor(Properties atts, Integer color, boolean fillMode) {
            Integer num = this.paintColor;
            if (num != null) {
                this.paint.setColor(num.intValue());
            } else {
                this.paint.setColor((16777215 & color.intValue()) | -16777216);
            }
            Float opacity = atts.getFloat("opacity");
            if (opacity == null) {
                opacity = atts.getFloat(fillMode ? "fill-opacity" : "stroke-opacity");
            }
            if (opacity == null) {
                this.paint.setAlpha(255);
            } else {
                this.paint.setAlpha((int) (opacity.floatValue() * 255.0f));
            }
        }

        private void pushTransform(Attributes atts) {
            String transform = SvgHelper.getStringAttr("transform", atts);
            boolean z = transform != null;
            this.pushed = z;
            if (z) {
                Matrix matrix = SvgHelper.parseTransform(transform);
                SvgDrawable svgDrawable = this.drawable;
                if (svgDrawable != null) {
                    svgDrawable.addCommand(matrix);
                    return;
                }
                this.canvas.save();
                this.canvas.concat(matrix);
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

        public void startElement(String namespaceURI, String localName, String qName, Attributes atts) {
            int i;
            String viewBox;
            String str = localName;
            Attributes attributes = atts;
            if (!this.boundsMode || str.equals("style")) {
                char c = 65535;
                switch (localName.hashCode()) {
                    case -1656480802:
                        if (str.equals("ellipse")) {
                            c = 8;
                            break;
                        }
                        break;
                    case -1360216880:
                        if (str.equals("circle")) {
                            c = 7;
                            break;
                        }
                        break;
                    case -397519558:
                        if (str.equals("polygon")) {
                            c = 9;
                            break;
                        }
                        break;
                    case 103:
                        if (str.equals("g")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 114276:
                        if (str.equals("svg")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 3079438:
                        if (str.equals("defs")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 3321844:
                        if (str.equals("line")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 3433509:
                        if (str.equals("path")) {
                            c = 11;
                            break;
                        }
                        break;
                    case 3496420:
                        if (str.equals("rect")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 109780401:
                        if (str.equals("style")) {
                            c = 3;
                            break;
                        }
                        break;
                    case 561938880:
                        if (str.equals("polyline")) {
                            c = 10;
                            break;
                        }
                        break;
                    case 917656469:
                        if (str.equals("clipPath")) {
                            c = 2;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        Float w = SvgHelper.getFloatAttr("width", attributes);
                        Float h = SvgHelper.getFloatAttr("height", attributes);
                        if ((w == null || h == null) && (viewBox = SvgHelper.getStringAttr("viewBox", attributes)) != null) {
                            String[] args = viewBox.split(" ");
                            w = Float.valueOf(Float.parseFloat(args[2]));
                            h = Float.valueOf(Float.parseFloat(args[3]));
                        }
                        if (w == null || h == null) {
                            w = Float.valueOf((float) this.desiredWidth);
                            h = Float.valueOf((float) this.desiredHeight);
                        }
                        int width = (int) Math.ceil((double) w.floatValue());
                        int height = (int) Math.ceil((double) h.floatValue());
                        if (width == 0 || height == 0) {
                            width = this.desiredWidth;
                            height = this.desiredHeight;
                        } else {
                            int i2 = this.desiredWidth;
                            if (!(i2 == 0 || (i = this.desiredHeight) == 0)) {
                                float min = Math.min(((float) i2) / ((float) width), ((float) i) / ((float) height));
                                this.scale = min;
                                width = (int) (((float) width) * min);
                                height = (int) (((float) height) * min);
                            }
                        }
                        SvgDrawable svgDrawable = this.drawable;
                        if (svgDrawable == null) {
                            Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                            this.bitmap = createBitmap;
                            createBitmap.eraseColor(0);
                            Canvas canvas2 = new Canvas(this.bitmap);
                            this.canvas = canvas2;
                            float f = this.scale;
                            if (f != 0.0f) {
                                float f2 = this.globalScale;
                                canvas2.scale(f2 * f, f2 * f);
                                return;
                            }
                            return;
                        }
                        svgDrawable.width = width;
                        this.drawable.height = height;
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
                        Float x = SvgHelper.getFloatAttr("x", attributes);
                        if (x == null) {
                            x = Float.valueOf(0.0f);
                        }
                        Float y = SvgHelper.getFloatAttr("y", attributes);
                        if (y == null) {
                            y = Float.valueOf(0.0f);
                        }
                        Float width2 = SvgHelper.getFloatAttr("width", attributes);
                        Float height2 = SvgHelper.getFloatAttr("height", attributes);
                        Float rx = SvgHelper.getFloatAttr("rx", attributes, (Float) null);
                        pushTransform(attributes);
                        Properties props = new Properties(attributes, this.globalStyles);
                        if (doFill(props)) {
                            SvgDrawable svgDrawable2 = this.drawable;
                            if (svgDrawable2 != null) {
                                if (rx != null) {
                                    svgDrawable2.addCommand(new RoundRect(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), rx.floatValue()), this.paint);
                                } else {
                                    svgDrawable2.addCommand(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), this.paint);
                                }
                            } else if (rx != null) {
                                this.rectTmp.set(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue());
                                this.canvas.drawRoundRect(this.rectTmp, rx.floatValue(), rx.floatValue(), this.paint);
                            } else {
                                this.canvas.drawRect(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue(), this.paint);
                            }
                        }
                        if (doStroke(props)) {
                            SvgDrawable svgDrawable3 = this.drawable;
                            if (svgDrawable3 != null) {
                                if (rx != null) {
                                    svgDrawable3.addCommand(new RoundRect(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), rx.floatValue()), this.paint);
                                } else {
                                    svgDrawable3.addCommand(new RectF(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue()), this.paint);
                                }
                            } else if (rx != null) {
                                this.rectTmp.set(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue());
                                this.canvas.drawRoundRect(this.rectTmp, rx.floatValue(), rx.floatValue(), this.paint);
                            } else {
                                this.canvas.drawRect(x.floatValue(), y.floatValue(), x.floatValue() + width2.floatValue(), y.floatValue() + height2.floatValue(), this.paint);
                            }
                        }
                        popTransform();
                        return;
                    case 6:
                        Float x1 = SvgHelper.getFloatAttr("x1", attributes);
                        Float x2 = SvgHelper.getFloatAttr("x2", attributes);
                        Float y1 = SvgHelper.getFloatAttr("y1", attributes);
                        Float y2 = SvgHelper.getFloatAttr("y2", attributes);
                        if (doStroke(new Properties(attributes, this.globalStyles))) {
                            pushTransform(attributes);
                            SvgDrawable svgDrawable4 = this.drawable;
                            if (svgDrawable4 != null) {
                                svgDrawable4.addCommand(new Line(x1.floatValue(), y1.floatValue(), x2.floatValue(), y2.floatValue()), this.paint);
                            } else {
                                this.canvas.drawLine(x1.floatValue(), y1.floatValue(), x2.floatValue(), y2.floatValue(), this.paint);
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 7:
                        Float centerX = SvgHelper.getFloatAttr("cx", attributes);
                        Float centerY = SvgHelper.getFloatAttr("cy", attributes);
                        Float radius = SvgHelper.getFloatAttr("r", attributes);
                        if (centerX != null && centerY != null && radius != null) {
                            pushTransform(attributes);
                            Properties props2 = new Properties(attributes, this.globalStyles);
                            if (doFill(props2)) {
                                SvgDrawable svgDrawable5 = this.drawable;
                                if (svgDrawable5 != null) {
                                    svgDrawable5.addCommand(new Circle(centerX.floatValue(), centerY.floatValue(), radius.floatValue()), this.paint);
                                } else {
                                    this.canvas.drawCircle(centerX.floatValue(), centerY.floatValue(), radius.floatValue(), this.paint);
                                }
                            }
                            if (doStroke(props2)) {
                                SvgDrawable svgDrawable6 = this.drawable;
                                if (svgDrawable6 != null) {
                                    svgDrawable6.addCommand(new Circle(centerX.floatValue(), centerY.floatValue(), radius.floatValue()), this.paint);
                                } else {
                                    this.canvas.drawCircle(centerX.floatValue(), centerY.floatValue(), radius.floatValue(), this.paint);
                                }
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 8:
                        Float centerX2 = SvgHelper.getFloatAttr("cx", attributes);
                        Float centerY2 = SvgHelper.getFloatAttr("cy", attributes);
                        Float radiusX = SvgHelper.getFloatAttr("rx", attributes);
                        Float radiusY = SvgHelper.getFloatAttr("ry", attributes);
                        if (centerX2 != null && centerY2 != null && radiusX != null && radiusY != null) {
                            pushTransform(attributes);
                            Properties props3 = new Properties(attributes, this.globalStyles);
                            this.rect.set(centerX2.floatValue() - radiusX.floatValue(), centerY2.floatValue() - radiusY.floatValue(), centerX2.floatValue() + radiusX.floatValue(), centerY2.floatValue() + radiusY.floatValue());
                            if (doFill(props3)) {
                                SvgDrawable svgDrawable7 = this.drawable;
                                if (svgDrawable7 != null) {
                                    svgDrawable7.addCommand(new Oval(this.rect), this.paint);
                                } else {
                                    this.canvas.drawOval(this.rect, this.paint);
                                }
                            }
                            if (doStroke(props3)) {
                                SvgDrawable svgDrawable8 = this.drawable;
                                if (svgDrawable8 != null) {
                                    svgDrawable8.addCommand(new Oval(this.rect), this.paint);
                                } else {
                                    this.canvas.drawOval(this.rect, this.paint);
                                }
                            }
                            popTransform();
                            return;
                        }
                        return;
                    case 9:
                    case 10:
                        NumberParse numbers = SvgHelper.getNumberParseAttr("points", attributes);
                        if (numbers != null) {
                            Path p = new Path();
                            ArrayList<Float> points = numbers.numbers;
                            if (points.size() > 1) {
                                pushTransform(attributes);
                                Properties props4 = new Properties(attributes, this.globalStyles);
                                p.moveTo(points.get(0).floatValue(), points.get(1).floatValue());
                                for (int i3 = 2; i3 < points.size(); i3 += 2) {
                                    p.lineTo(points.get(i3).floatValue(), points.get(i3 + 1).floatValue());
                                }
                                if (str.equals("polygon")) {
                                    p.close();
                                }
                                if (doFill(props4)) {
                                    SvgDrawable svgDrawable9 = this.drawable;
                                    if (svgDrawable9 != null) {
                                        svgDrawable9.addCommand(p, this.paint);
                                    } else {
                                        this.canvas.drawPath(p, this.paint);
                                    }
                                }
                                if (doStroke(props4)) {
                                    SvgDrawable svgDrawable10 = this.drawable;
                                    if (svgDrawable10 != null) {
                                        svgDrawable10.addCommand(p, this.paint);
                                    } else {
                                        this.canvas.drawPath(p, this.paint);
                                    }
                                }
                                popTransform();
                                return;
                            }
                            return;
                        }
                        return;
                    case 11:
                        Path p2 = SvgHelper.doPath(SvgHelper.getStringAttr("d", attributes));
                        pushTransform(attributes);
                        Properties props5 = new Properties(attributes, this.globalStyles);
                        if (doFill(props5)) {
                            SvgDrawable svgDrawable11 = this.drawable;
                            if (svgDrawable11 != null) {
                                svgDrawable11.addCommand(p2, this.paint);
                            } else {
                                this.canvas.drawPath(p2, this.paint);
                            }
                        }
                        if (doStroke(props5)) {
                            SvgDrawable svgDrawable12 = this.drawable;
                            if (svgDrawable12 != null) {
                                svgDrawable12.addCommand(p2, this.paint);
                            } else {
                                this.canvas.drawPath(p2, this.paint);
                            }
                        }
                        popTransform();
                        return;
                    default:
                        return;
                }
            }
        }

        public void characters(char[] ch, int start, int length) {
            StringBuilder sb = this.styles;
            if (sb != null) {
                sb.append(ch, start, length);
            }
        }

        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void endElement(java.lang.String r11, java.lang.String r12, java.lang.String r13) {
            /*
                r10 = this;
                int r0 = r12.hashCode()
                r1 = 0
                r2 = 1
                switch(r0) {
                    case 103: goto L_0x0034;
                    case 114276: goto L_0x0029;
                    case 3079438: goto L_0x001f;
                    case 109780401: goto L_0x0014;
                    case 917656469: goto L_0x000a;
                    default: goto L_0x0009;
                }
            L_0x0009:
                goto L_0x003e
            L_0x000a:
                java.lang.String r0 = "clipPath"
                boolean r0 = r12.equals(r0)
                if (r0 == 0) goto L_0x0009
                r0 = 4
                goto L_0x003f
            L_0x0014:
                java.lang.String r0 = "style"
                boolean r0 = r12.equals(r0)
                if (r0 == 0) goto L_0x0009
                r0 = 0
                goto L_0x003f
            L_0x001f:
                java.lang.String r0 = "defs"
                boolean r0 = r12.equals(r0)
                if (r0 == 0) goto L_0x0009
                r0 = 3
                goto L_0x003f
            L_0x0029:
                java.lang.String r0 = "svg"
                boolean r0 = r12.equals(r0)
                if (r0 == 0) goto L_0x0009
                r0 = 1
                goto L_0x003f
            L_0x0034:
                java.lang.String r0 = "g"
                boolean r0 = r12.equals(r0)
                if (r0 == 0) goto L_0x0009
                r0 = 2
                goto L_0x003f
            L_0x003e:
                r0 = -1
            L_0x003f:
                switch(r0) {
                    case 0: goto L_0x0047;
                    case 1: goto L_0x0046;
                    case 2: goto L_0x0043;
                    case 3: goto L_0x0043;
                    case 4: goto L_0x0043;
                    default: goto L_0x0042;
                }
            L_0x0042:
                goto L_0x00af
            L_0x0043:
                r10.boundsMode = r1
                goto L_0x00af
            L_0x0046:
                goto L_0x00af
            L_0x0047:
                java.lang.StringBuilder r0 = r10.styles
                if (r0 == 0) goto L_0x00af
                java.lang.String r0 = r0.toString()
                java.lang.String r3 = "\\}"
                java.lang.String[] r0 = r0.split(r3)
                r3 = 0
            L_0x0056:
                int r4 = r0.length
                r5 = 0
                if (r3 >= r4) goto L_0x00ad
                r4 = r0[r3]
                java.lang.String r4 = r4.trim()
                java.lang.String r6 = "\t"
                java.lang.String r7 = ""
                java.lang.String r4 = r4.replace(r6, r7)
                java.lang.String r6 = "\n"
                java.lang.String r4 = r4.replace(r6, r7)
                r0[r3] = r4
                r4 = r0[r3]
                int r4 = r4.length()
                if (r4 == 0) goto L_0x00aa
                r4 = r0[r3]
                char r4 = r4.charAt(r1)
                r6 = 46
                if (r4 == r6) goto L_0x0083
                goto L_0x00aa
            L_0x0083:
                r4 = r0[r3]
                r6 = 123(0x7b, float:1.72E-43)
                int r4 = r4.indexOf(r6)
                if (r4 >= 0) goto L_0x008e
                goto L_0x00aa
            L_0x008e:
                r6 = r0[r3]
                java.lang.String r6 = r6.substring(r2, r4)
                java.lang.String r6 = r6.trim()
                r7 = r0[r3]
                int r8 = r4 + 1
                java.lang.String r7 = r7.substring(r8)
                java.util.HashMap<java.lang.String, org.telegram.messenger.SvgHelper$StyleSet> r8 = r10.globalStyles
                org.telegram.messenger.SvgHelper$StyleSet r9 = new org.telegram.messenger.SvgHelper$StyleSet
                r9.<init>(r7)
                r8.put(r6, r9)
            L_0x00aa:
                int r3 = r3 + 1
                goto L_0x0056
            L_0x00ad:
                r10.styles = r5
            L_0x00af:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.SVGHandler.endElement(java.lang.String, java.lang.String, java.lang.String):void");
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

        public ParserHelper(CharSequence s2, int pos2) {
            this.s = s2;
            this.pos = pos2;
            this.n = s2.length();
            this.current = s2.charAt(pos2);
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
                    switch (this.s.charAt(i)) {
                        case 9:
                        case 10:
                        case ' ':
                        case ',':
                            advance();
                        default:
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

        /* JADX WARNING: Code restructure failed: missing block: B:40:0x009e, code lost:
            r8 = read();
            r12.current = r8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00a4, code lost:
            switch(r8) {
                case 48: goto L_0x00ab;
                case 49: goto L_0x00ab;
                case 50: goto L_0x00ab;
                case 51: goto L_0x00ab;
                case 52: goto L_0x00ab;
                case 53: goto L_0x00ab;
                case 54: goto L_0x00ab;
                case 55: goto L_0x00ab;
                case 56: goto L_0x00ab;
                case 57: goto L_0x00ab;
                default: goto L_0x00a7;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00a7, code lost:
            reportUnexpectedCharacterError(r8);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:43:0x00aa, code lost:
            return 0.0f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:45:0x00ad, code lost:
            switch(r12.current) {
                case 48: goto L_0x00b1;
                case 49: goto L_0x00bc;
                case 50: goto L_0x00bc;
                case 51: goto L_0x00bc;
                case 52: goto L_0x00bc;
                case 53: goto L_0x00bc;
                case 54: goto L_0x00bc;
                case 55: goto L_0x00bc;
                case 56: goto L_0x00bc;
                case 57: goto L_0x00bc;
                default: goto L_0x00b0;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:46:0x00b1, code lost:
            r8 = read();
            r12.current = r8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b7, code lost:
            switch(r8) {
                case 48: goto L_0x00d4;
                case 49: goto L_0x00bb;
                case 50: goto L_0x00bb;
                case 51: goto L_0x00bb;
                case 52: goto L_0x00bb;
                case 53: goto L_0x00bb;
                case 54: goto L_0x00bb;
                case 55: goto L_0x00bb;
                case 56: goto L_0x00bb;
                case 57: goto L_0x00bb;
                default: goto L_0x00ba;
            };
         */
        /* JADX WARNING: Code restructure failed: missing block: B:49:0x00bd, code lost:
            if (r5 >= 3) goto L_0x00c9;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:50:0x00bf, code lost:
            r5 = r5 + 1;
            r4 = (r4 * 10) + (r12.current - '0');
         */
        /* JADX WARNING: Code restructure failed: missing block: B:51:0x00c9, code lost:
            r8 = read();
            r12.current = r8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x00cf, code lost:
            switch(r8) {
                case 48: goto L_0x00d3;
                case 49: goto L_0x00d3;
                case 50: goto L_0x00d3;
                case 51: goto L_0x00d3;
                case 52: goto L_0x00d3;
                case 53: goto L_0x00d3;
                case 54: goto L_0x00d3;
                case 55: goto L_0x00d3;
                case 56: goto L_0x00d3;
                case 57: goto L_0x00d3;
                default: goto L_0x00d2;
            };
         */
        /* JADX WARNING: Removed duplicated region for block: B:12:0x002d A[LOOP_START, PHI: r0 r1 r6 
          PHI: (r0v8 'mant' int) = (r0v0 'mant' int), (r0v9 'mant' int) binds: [B:11:0x002c, B:16:0x0042] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r1v5 'mantDig' int) = (r1v0 'mantDig' int), (r1v6 'mantDig' int) binds: [B:11:0x002c, B:16:0x0042] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r6v9 'expAdj' int) = (r6v0 'expAdj' int), (r6v10 'expAdj' int) binds: [B:11:0x002c, B:16:0x0042] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:25:0x0061 A[LOOP_START, PHI: r6 
          PHI: (r6v7 'expAdj' int) = (r6v1 'expAdj' int), (r6v8 'expAdj' int) binds: [B:24:0x005f, B:26:0x0069] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x0071 A[LOOP_START, PHI: r0 r1 r6 
          PHI: (r0v5 'mant' int) = (r0v1 'mant' int), (r0v6 'mant' int) binds: [B:67:0x0071, B:32:0x0085] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r1v2 'mantDig' int) = (r1v1 'mantDig' int), (r1v3 'mantDig' int) binds: [B:67:0x0071, B:32:0x0085] A[DONT_GENERATE, DONT_INLINE]
          PHI: (r6v4 'expAdj' int) = (r6v3 'expAdj' int), (r6v5 'expAdj' int) binds: [B:67:0x0071, B:32:0x0085] A[DONT_GENERATE, DONT_INLINE]] */
        /* JADX WARNING: Removed duplicated region for block: B:8:0x0021 A[LOOP_START] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public float parseFloat() {
            /*
                r12 = this;
                r0 = 0
                r1 = 0
                r2 = 1
                r3 = 0
                r4 = 0
                r5 = 0
                r6 = 0
                r7 = 1
                char r8 = r12.current
                switch(r8) {
                    case 43: goto L_0x000f;
                    case 44: goto L_0x000d;
                    case 45: goto L_0x000e;
                    default: goto L_0x000d;
                }
            L_0x000d:
                goto L_0x0015
            L_0x000e:
                r2 = 0
            L_0x000f:
                char r8 = r12.read()
                r12.current = r8
            L_0x0015:
                char r8 = r12.current
                r9 = 9
                r10 = 0
                switch(r8) {
                    case 46: goto L_0x0049;
                    case 47: goto L_0x001d;
                    case 48: goto L_0x0020;
                    case 49: goto L_0x002c;
                    case 50: goto L_0x002c;
                    case 51: goto L_0x002c;
                    case 52: goto L_0x002c;
                    case 53: goto L_0x002c;
                    case 54: goto L_0x002c;
                    case 55: goto L_0x002c;
                    case 56: goto L_0x002c;
                    case 57: goto L_0x002c;
                    default: goto L_0x001d;
                }
            L_0x001d:
                r8 = 2143289344(0x7fCLASSNAME, float:NaN)
                return r8
            L_0x0020:
                r3 = 1
            L_0x0021:
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 46: goto L_0x0048;
                    case 48: goto L_0x0047;
                    case 49: goto L_0x002b;
                    case 50: goto L_0x002b;
                    case 51: goto L_0x002b;
                    case 52: goto L_0x002b;
                    case 53: goto L_0x002b;
                    case 54: goto L_0x002b;
                    case 55: goto L_0x002b;
                    case 56: goto L_0x002b;
                    case 57: goto L_0x002b;
                    case 69: goto L_0x0048;
                    case 101: goto L_0x0048;
                    default: goto L_0x002a;
                }
            L_0x002a:
                return r10
            L_0x002b:
            L_0x002c:
                r3 = 1
            L_0x002d:
                if (r1 >= r9) goto L_0x003a
                int r1 = r1 + 1
                int r8 = r0 * 10
                char r11 = r12.current
                int r11 = r11 + -48
                int r8 = r8 + r11
                r0 = r8
                goto L_0x003c
            L_0x003a:
                int r6 = r6 + 1
            L_0x003c:
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 48: goto L_0x0046;
                    case 49: goto L_0x0046;
                    case 50: goto L_0x0046;
                    case 51: goto L_0x0046;
                    case 52: goto L_0x0046;
                    case 53: goto L_0x0046;
                    case 54: goto L_0x0046;
                    case 55: goto L_0x0046;
                    case 56: goto L_0x0046;
                    case 57: goto L_0x0046;
                    default: goto L_0x0045;
                }
            L_0x0045:
                goto L_0x004a
            L_0x0046:
                goto L_0x002d
            L_0x0047:
                goto L_0x0021
            L_0x0048:
                goto L_0x004a
            L_0x0049:
            L_0x004a:
                char r8 = r12.current
                r11 = 46
                if (r8 != r11) goto L_0x008a
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 48: goto L_0x005f;
                    case 49: goto L_0x0071;
                    case 50: goto L_0x0071;
                    case 51: goto L_0x0071;
                    case 52: goto L_0x0071;
                    case 53: goto L_0x0071;
                    case 54: goto L_0x0071;
                    case 55: goto L_0x0071;
                    case 56: goto L_0x0071;
                    case 57: goto L_0x0071;
                    default: goto L_0x0059;
                }
            L_0x0059:
                if (r3 != 0) goto L_0x008a
                r12.reportUnexpectedCharacterError(r8)
                return r10
            L_0x005f:
                if (r1 != 0) goto L_0x0071
            L_0x0061:
                char r8 = r12.read()
                r12.current = r8
                int r6 = r6 + -1
                switch(r8) {
                    case 48: goto L_0x0070;
                    case 49: goto L_0x006f;
                    case 50: goto L_0x006f;
                    case 51: goto L_0x006f;
                    case 52: goto L_0x006f;
                    case 53: goto L_0x006f;
                    case 54: goto L_0x006f;
                    case 55: goto L_0x006f;
                    case 56: goto L_0x006f;
                    case 57: goto L_0x006f;
                    default: goto L_0x006c;
                }
            L_0x006c:
                if (r3 != 0) goto L_0x008a
                return r10
            L_0x006f:
                goto L_0x0071
            L_0x0070:
                goto L_0x0061
            L_0x0071:
                if (r1 >= r9) goto L_0x007f
                int r1 = r1 + 1
                int r8 = r0 * 10
                char r11 = r12.current
                int r11 = r11 + -48
                int r8 = r8 + r11
                int r6 = r6 + -1
                r0 = r8
            L_0x007f:
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 48: goto L_0x0089;
                    case 49: goto L_0x0089;
                    case 50: goto L_0x0089;
                    case 51: goto L_0x0089;
                    case 52: goto L_0x0089;
                    case 53: goto L_0x0089;
                    case 54: goto L_0x0089;
                    case 55: goto L_0x0089;
                    case 56: goto L_0x0089;
                    case 57: goto L_0x0089;
                    default: goto L_0x0088;
                }
            L_0x0088:
                goto L_0x008a
            L_0x0089:
                goto L_0x0071
            L_0x008a:
                char r8 = r12.current
                switch(r8) {
                    case 69: goto L_0x0090;
                    case 101: goto L_0x0090;
                    default: goto L_0x008f;
                }
            L_0x008f:
                goto L_0x00d5
            L_0x0090:
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 43: goto L_0x009e;
                    case 44: goto L_0x0099;
                    case 45: goto L_0x009d;
                    case 46: goto L_0x0099;
                    case 47: goto L_0x0099;
                    case 48: goto L_0x00ab;
                    case 49: goto L_0x00ab;
                    case 50: goto L_0x00ab;
                    case 51: goto L_0x00ab;
                    case 52: goto L_0x00ab;
                    case 53: goto L_0x00ab;
                    case 54: goto L_0x00ab;
                    case 55: goto L_0x00ab;
                    case 56: goto L_0x00ab;
                    case 57: goto L_0x00ab;
                    default: goto L_0x0099;
                }
            L_0x0099:
                r12.reportUnexpectedCharacterError(r8)
                return r10
            L_0x009d:
                r7 = 0
            L_0x009e:
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 48: goto L_0x00ab;
                    case 49: goto L_0x00ab;
                    case 50: goto L_0x00ab;
                    case 51: goto L_0x00ab;
                    case 52: goto L_0x00ab;
                    case 53: goto L_0x00ab;
                    case 54: goto L_0x00ab;
                    case 55: goto L_0x00ab;
                    case 56: goto L_0x00ab;
                    case 57: goto L_0x00ab;
                    default: goto L_0x00a7;
                }
            L_0x00a7:
                r12.reportUnexpectedCharacterError(r8)
                return r10
            L_0x00ab:
                char r8 = r12.current
                switch(r8) {
                    case 48: goto L_0x00b1;
                    case 49: goto L_0x00bc;
                    case 50: goto L_0x00bc;
                    case 51: goto L_0x00bc;
                    case 52: goto L_0x00bc;
                    case 53: goto L_0x00bc;
                    case 54: goto L_0x00bc;
                    case 55: goto L_0x00bc;
                    case 56: goto L_0x00bc;
                    case 57: goto L_0x00bc;
                    default: goto L_0x00b0;
                }
            L_0x00b0:
                goto L_0x00d5
            L_0x00b1:
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 48: goto L_0x00d4;
                    case 49: goto L_0x00bb;
                    case 50: goto L_0x00bb;
                    case 51: goto L_0x00bb;
                    case 52: goto L_0x00bb;
                    case 53: goto L_0x00bb;
                    case 54: goto L_0x00bb;
                    case 55: goto L_0x00bb;
                    case 56: goto L_0x00bb;
                    case 57: goto L_0x00bb;
                    default: goto L_0x00ba;
                }
            L_0x00ba:
                goto L_0x00d5
            L_0x00bb:
            L_0x00bc:
                r8 = 3
                if (r5 >= r8) goto L_0x00c9
                int r5 = r5 + 1
                int r8 = r4 * 10
                char r9 = r12.current
                int r9 = r9 + -48
                int r8 = r8 + r9
                r4 = r8
            L_0x00c9:
                char r8 = r12.read()
                r12.current = r8
                switch(r8) {
                    case 48: goto L_0x00d3;
                    case 49: goto L_0x00d3;
                    case 50: goto L_0x00d3;
                    case 51: goto L_0x00d3;
                    case 52: goto L_0x00d3;
                    case 53: goto L_0x00d3;
                    case 54: goto L_0x00d3;
                    case 55: goto L_0x00d3;
                    case 56: goto L_0x00d3;
                    case 57: goto L_0x00d3;
                    default: goto L_0x00d2;
                }
            L_0x00d2:
                goto L_0x00d5
            L_0x00d3:
                goto L_0x00bc
            L_0x00d4:
                goto L_0x00b1
            L_0x00d5:
                if (r7 != 0) goto L_0x00d8
                int r4 = -r4
            L_0x00d8:
                int r4 = r4 + r6
                if (r2 != 0) goto L_0x00dc
                int r0 = -r0
            L_0x00dc:
                float r8 = r12.buildFloat(r0, r4)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.SvgHelper.ParserHelper.parseFloat():float");
        }

        private void reportUnexpectedCharacterError(char c) {
            throw new RuntimeException("Unexpected char '" + c + "'.");
        }

        public float buildFloat(int mant, int exp) {
            double d;
            if (exp < -125 || mant == 0) {
                return 0.0f;
            }
            if (exp >= 128) {
                return mant > 0 ? Float.POSITIVE_INFINITY : Float.NEGATIVE_INFINITY;
            }
            if (exp == 0) {
                return (float) mant;
            }
            if (mant >= 67108864) {
                mant++;
            }
            double d2 = (double) mant;
            double[] access$1300 = SvgHelper.pow10;
            if (exp > 0) {
                double d3 = access$1300[exp];
                Double.isNaN(d2);
                d = d2 * d3;
            } else {
                double d4 = access$1300[-exp];
                Double.isNaN(d2);
                d = d2 / d4;
            }
            return (float) d;
        }

        public float nextFloat() {
            skipWhitespace();
            float f = parseFloat();
            skipNumberSeparator();
            return f;
        }
    }

    public static String decompress(byte[] encoded) {
        try {
            StringBuilder path = new StringBuilder(encoded.length * 2);
            path.append('M');
            for (byte b : encoded) {
                int num = b & 255;
                if (num >= 192) {
                    path.append("AACAAAAHAAALMAAAQASTAVAAAZaacaaaahaaalmaaaqastava.az0123456789-,".charAt((num - 128) - 64));
                } else {
                    if (num >= 128) {
                        path.append(',');
                    } else if (num >= 64) {
                        path.append('-');
                    }
                    path.append(num & 63);
                }
            }
            path.append('z');
            return path.toString();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            return "";
        }
    }
}

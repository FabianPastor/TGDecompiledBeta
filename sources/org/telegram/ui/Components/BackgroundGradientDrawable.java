package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import androidx.collection.ArrayMap;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.BackgroundGradientDrawable;

public class BackgroundGradientDrawable extends GradientDrawable {
    public static final float DEFAULT_COMPRESS_RATIO = 0.5f;
    private final Paint bitmapPaint = new Paint(1);
    private final ArrayMap<IntSize, Bitmap> bitmaps = new ArrayMap<>();
    private final int[] colors;
    private final ArrayMap<View, Disposable> disposables = new ArrayMap<>();
    private boolean disposed = false;
    private final List<Runnable[]> ditheringRunnables = new ArrayList();
    private final ArrayMap<IntSize, Boolean> isForExactBounds = new ArrayMap<>();

    public interface Disposable {
        void dispose();
    }

    public interface Listener {
        void onAllSizesReady();

        void onSizeReady(int i, int i2);
    }

    public static class ListenerAdapter implements Listener {
        public void onAllSizesReady() {
        }

        public void onSizeReady(int i, int i2) {
        }
    }

    public static class Sizes {
        /* access modifiers changed from: private */
        public final IntSize[] arr;

        public enum Orientation {
            PORTRAIT,
            LANDSCAPE,
            BOTH
        }

        private Sizes(int i, int i2, int... iArr) {
            this.arr = new IntSize[((iArr.length / 2) + 1)];
            IntSize[] intSizeArr = this.arr;
            IntSize intSize = new IntSize(i, i2);
            int i3 = 0;
            intSizeArr[0] = intSize;
            while (i3 < iArr.length / 2) {
                int i4 = i3 + 1;
                int i5 = i3 * 2;
                this.arr[i4] = new IntSize(iArr[i5], iArr[i5 + 1]);
                i3 = i4;
            }
        }

        public static Sizes of(int i, int i2, int... iArr) {
            return new Sizes(i, i2, iArr);
        }

        public static Sizes ofDeviceScreen() {
            return ofDeviceScreen(0.5f);
        }

        public static Sizes ofDeviceScreen(float f) {
            return ofDeviceScreen(f, Orientation.BOTH);
        }

        public static Sizes ofDeviceScreen(float f, Orientation orientation) {
            Point point = AndroidUtilities.displaySize;
            int i = (int) (((float) point.x) * f);
            int i2 = (int) (((float) point.y) * f);
            if (i == i2) {
                return of(i, i2, new int[0]);
            }
            boolean z = true;
            if (orientation == Orientation.BOTH) {
                return of(i, i2, i2, i);
            }
            boolean z2 = orientation == Orientation.PORTRAIT;
            if (i >= i2) {
                z = false;
            }
            return z2 == z ? of(i, i2, new int[0]) : of(i2, i, new int[0]);
        }
    }

    public BackgroundGradientDrawable(GradientDrawable.Orientation orientation, int[] iArr) {
        super(orientation, iArr);
        setDither(true);
        this.colors = iArr;
        this.bitmapPaint.setDither(true);
    }

    public void draw(Canvas canvas) {
        if (this.disposed) {
            super.draw(canvas);
            return;
        }
        Rect bounds = getBounds();
        Bitmap findBestBitmapForSize = findBestBitmapForSize(bounds.width(), bounds.height());
        if (findBestBitmapForSize != null) {
            canvas.drawBitmap(findBestBitmapForSize, (Rect) null, bounds, this.bitmapPaint);
        } else {
            super.draw(canvas);
        }
    }

    public Disposable drawExactBoundsSize(Canvas canvas, View view) {
        return drawExactBoundsSize(canvas, view, 0.5f);
    }

    public Disposable drawExactBoundsSize(Canvas canvas, final View view, float f) {
        if (this.disposed) {
            super.draw(canvas);
            return null;
        }
        Rect bounds = getBounds();
        int width = (int) (((float) bounds.width()) * f);
        int height = (int) (((float) bounds.height()) * f);
        int size = this.bitmaps.size();
        for (int i = 0; i < size; i++) {
            IntSize keyAt = this.bitmaps.keyAt(i);
            if (keyAt.width == width && keyAt.height == height) {
                Bitmap valueAt = this.bitmaps.valueAt(i);
                if (valueAt != null) {
                    canvas.drawBitmap(valueAt, (Rect) null, bounds, this.bitmapPaint);
                } else {
                    super.draw(canvas);
                }
                return this.disposables.get(view);
            }
        }
        Disposable remove = this.disposables.remove(view);
        if (remove != null) {
            remove.dispose();
        }
        IntSize intSize = new IntSize(width, height);
        this.bitmaps.put(intSize, null);
        this.isForExactBounds.put(intSize, true);
        Disposable put = this.disposables.put(view, new Disposable(view, startDitheringInternal(new IntSize[]{intSize}, new ListenerAdapter() {
            public void onAllSizesReady() {
                view.invalidate();
            }
        }, 0)) {
            private final /* synthetic */ View f$1;
            private final /* synthetic */ BackgroundGradientDrawable.Disposable f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void dispose() {
                BackgroundGradientDrawable.this.lambda$drawExactBoundsSize$0$BackgroundGradientDrawable(this.f$1, this.f$2);
            }
        });
        super.draw(canvas);
        return put;
    }

    public /* synthetic */ void lambda$drawExactBoundsSize$0$BackgroundGradientDrawable(View view, Disposable disposable) {
        this.disposables.remove(view);
        disposable.dispose();
    }

    public void setAlpha(int i) {
        super.setAlpha(i);
        this.bitmapPaint.setAlpha(i);
    }

    public void setColorFilter(ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        this.bitmapPaint.setColorFilter(colorFilter);
    }

    public int[] getColorsList() {
        return this.colors;
    }

    /* access modifiers changed from: protected */
    public void finalize() throws Throwable {
        try {
            dispose();
        } finally {
            super.finalize();
        }
    }

    public Disposable startDithering(Sizes sizes, Listener listener) {
        return startDithering(sizes, listener, 0);
    }

    public Disposable startDithering(Sizes sizes, Listener listener, long j) {
        if (this.disposed) {
            return null;
        }
        ArrayList arrayList = new ArrayList(sizes.arr.length);
        for (IntSize intSize : sizes.arr) {
            if (!this.bitmaps.containsKey(intSize)) {
                this.bitmaps.put(intSize, null);
                arrayList.add(intSize);
            }
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        return startDitheringInternal((IntSize[]) arrayList.toArray(new IntSize[0]), listener, j);
    }

    private Disposable startDitheringInternal(IntSize[] intSizeArr, Listener listener, long j) {
        if (intSizeArr.length == 0) {
            return null;
        }
        Listener[] listenerArr = {listener};
        Runnable[] runnableArr = new Runnable[intSizeArr.length];
        this.ditheringRunnables.add(runnableArr);
        for (int i = 0; i < intSizeArr.length; i++) {
            IntSize intSize = intSizeArr[i];
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            $$Lambda$BackgroundGradientDrawable$hxdrjDm4pRekbnxNfgt2P7Oug r1 = new Runnable(intSize, runnableArr, i, listenerArr) {
                private final /* synthetic */ IntSize f$1;
                private final /* synthetic */ Runnable[] f$2;
                private final /* synthetic */ int f$3;
                private final /* synthetic */ BackgroundGradientDrawable.Listener[] f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run() {
                    BackgroundGradientDrawable.this.lambda$startDitheringInternal$2$BackgroundGradientDrawable(this.f$1, this.f$2, this.f$3, this.f$4);
                }
            };
            runnableArr[i] = r1;
            dispatchQueue.postRunnable(r1, j);
        }
        return new Disposable(listenerArr, runnableArr, intSizeArr) {
            private final /* synthetic */ BackgroundGradientDrawable.Listener[] f$1;
            private final /* synthetic */ Runnable[] f$2;
            private final /* synthetic */ IntSize[] f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void dispose() {
                BackgroundGradientDrawable.this.lambda$startDitheringInternal$3$BackgroundGradientDrawable(this.f$1, this.f$2, this.f$3);
            }
        };
    }

    public /* synthetic */ void lambda$startDitheringInternal$2$BackgroundGradientDrawable(IntSize intSize, Runnable[] runnableArr, int i, Listener[] listenerArr) {
        try {
            AndroidUtilities.runOnUIThread(new Runnable(runnableArr, createDitheredGradientBitmap(getOrientation(), this.colors, intSize.width, intSize.height), intSize, i, listenerArr) {
                private final /* synthetic */ Runnable[] f$1;
                private final /* synthetic */ Bitmap f$2;
                private final /* synthetic */ IntSize f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ BackgroundGradientDrawable.Listener[] f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    BackgroundGradientDrawable.this.lambda$null$1$BackgroundGradientDrawable(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
        } catch (Throwable th) {
            AndroidUtilities.runOnUIThread(new Runnable(runnableArr, (Bitmap) null, intSize, i, listenerArr) {
                private final /* synthetic */ Runnable[] f$1;
                private final /* synthetic */ Bitmap f$2;
                private final /* synthetic */ IntSize f$3;
                private final /* synthetic */ int f$4;
                private final /* synthetic */ BackgroundGradientDrawable.Listener[] f$5;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run() {
                    BackgroundGradientDrawable.this.lambda$null$1$BackgroundGradientDrawable(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                }
            });
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:17:0x0037  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0040  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$null$1$BackgroundGradientDrawable(java.lang.Runnable[] r4, android.graphics.Bitmap r5, org.telegram.ui.Components.IntSize r6, int r7, org.telegram.ui.Components.BackgroundGradientDrawable.Listener[] r8) {
        /*
            r3 = this;
            java.util.List<java.lang.Runnable[]> r0 = r3.ditheringRunnables
            boolean r0 = r0.contains(r4)
            if (r0 != 0) goto L_0x000e
            if (r5 == 0) goto L_0x000d
            r5.recycle()
        L_0x000d:
            return
        L_0x000e:
            if (r5 == 0) goto L_0x0016
            androidx.collection.ArrayMap<org.telegram.ui.Components.IntSize, android.graphics.Bitmap> r0 = r3.bitmaps
            r0.put(r6, r5)
            goto L_0x0020
        L_0x0016:
            androidx.collection.ArrayMap<org.telegram.ui.Components.IntSize, android.graphics.Bitmap> r5 = r3.bitmaps
            r5.remove(r6)
            androidx.collection.ArrayMap<org.telegram.ui.Components.IntSize, java.lang.Boolean> r5 = r3.isForExactBounds
            r5.remove(r6)
        L_0x0020:
            r5 = 0
            r4[r7] = r5
            int r7 = r4.length
            r0 = 1
            r1 = 0
            if (r7 <= r0) goto L_0x0034
            r7 = 0
        L_0x0029:
            int r2 = r4.length
            if (r7 >= r2) goto L_0x0034
            r2 = r4[r7]
            if (r2 == 0) goto L_0x0031
            goto L_0x0035
        L_0x0031:
            int r7 = r7 + 1
            goto L_0x0029
        L_0x0034:
            r0 = 0
        L_0x0035:
            if (r0 != 0) goto L_0x003c
            java.util.List<java.lang.Runnable[]> r7 = r3.ditheringRunnables
            r7.remove(r4)
        L_0x003c:
            r4 = r8[r1]
            if (r4 == 0) goto L_0x0052
            r4 = r8[r1]
            int r7 = r6.width
            int r6 = r6.height
            r4.onSizeReady(r7, r6)
            if (r0 != 0) goto L_0x0052
            r4 = r8[r1]
            r4.onAllSizesReady()
            r8[r1] = r5
        L_0x0052:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackgroundGradientDrawable.lambda$null$1$BackgroundGradientDrawable(java.lang.Runnable[], android.graphics.Bitmap, org.telegram.ui.Components.IntSize, int, org.telegram.ui.Components.BackgroundGradientDrawable$Listener[]):void");
    }

    public /* synthetic */ void lambda$startDitheringInternal$3$BackgroundGradientDrawable(Listener[] listenerArr, Runnable[] runnableArr, IntSize[] intSizeArr) {
        listenerArr[0] = null;
        if (this.ditheringRunnables.contains(runnableArr)) {
            Utilities.globalQueue.cancelRunnables(runnableArr);
            this.ditheringRunnables.remove(runnableArr);
        }
        for (IntSize intSize : intSizeArr) {
            Bitmap remove = this.bitmaps.remove(intSize);
            this.isForExactBounds.remove(intSize);
            if (remove != null) {
                remove.recycle();
            }
        }
    }

    public void dispose() {
        if (!this.disposed) {
            for (int size = this.ditheringRunnables.size() - 1; size >= 0; size--) {
                Utilities.globalQueue.cancelRunnables(this.ditheringRunnables.remove(size));
            }
            for (int size2 = this.bitmaps.size() - 1; size2 >= 0; size2--) {
                Bitmap removeAt = this.bitmaps.removeAt(size2);
                if (removeAt != null) {
                    removeAt.recycle();
                }
            }
            this.isForExactBounds.clear();
            this.disposables.clear();
            this.disposed = true;
        }
    }

    private Bitmap findBestBitmapForSize(int i, int i2) {
        Bitmap valueAt;
        Boolean bool;
        int size = this.bitmaps.size();
        Bitmap bitmap = null;
        float f = Float.MAX_VALUE;
        for (int i3 = 0; i3 < size; i3++) {
            IntSize keyAt = this.bitmaps.keyAt(i3);
            float sqrt = (float) Math.sqrt(Math.pow((double) (i - keyAt.width), 2.0d) + Math.pow((double) (i2 - keyAt.height), 2.0d));
            if (sqrt < f && (valueAt = this.bitmaps.valueAt(i3)) != null && ((bool = this.isForExactBounds.get(keyAt)) == null || !bool.booleanValue())) {
                f = sqrt;
                bitmap = valueAt;
            }
        }
        return bitmap;
    }

    /* renamed from: org.telegram.ui.Components.BackgroundGradientDrawable$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation = new int[GradientDrawable.Orientation.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        static {
            /*
                android.graphics.drawable.GradientDrawable$Orientation[] r0 = android.graphics.drawable.GradientDrawable.Orientation.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation = r0
                int[] r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation     // Catch:{ NoSuchFieldError -> 0x0014 }
                android.graphics.drawable.GradientDrawable$Orientation r1 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM     // Catch:{ NoSuchFieldError -> 0x0014 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
            L_0x0014:
                int[] r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation     // Catch:{ NoSuchFieldError -> 0x001f }
                android.graphics.drawable.GradientDrawable$Orientation r1 = android.graphics.drawable.GradientDrawable.Orientation.TR_BL     // Catch:{ NoSuchFieldError -> 0x001f }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
            L_0x001f:
                int[] r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation     // Catch:{ NoSuchFieldError -> 0x002a }
                android.graphics.drawable.GradientDrawable$Orientation r1 = android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT     // Catch:{ NoSuchFieldError -> 0x002a }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
            L_0x002a:
                int[] r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation     // Catch:{ NoSuchFieldError -> 0x0035 }
                android.graphics.drawable.GradientDrawable$Orientation r1 = android.graphics.drawable.GradientDrawable.Orientation.BR_TL     // Catch:{ NoSuchFieldError -> 0x0035 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
                r2 = 4
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
            L_0x0035:
                int[] r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation     // Catch:{ NoSuchFieldError -> 0x0040 }
                android.graphics.drawable.GradientDrawable$Orientation r1 = android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP     // Catch:{ NoSuchFieldError -> 0x0040 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0040 }
                r2 = 5
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0040 }
            L_0x0040:
                int[] r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation     // Catch:{ NoSuchFieldError -> 0x004b }
                android.graphics.drawable.GradientDrawable$Orientation r1 = android.graphics.drawable.GradientDrawable.Orientation.BL_TR     // Catch:{ NoSuchFieldError -> 0x004b }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x004b }
                r2 = 6
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x004b }
            L_0x004b:
                int[] r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation     // Catch:{ NoSuchFieldError -> 0x0056 }
                android.graphics.drawable.GradientDrawable$Orientation r1 = android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT     // Catch:{ NoSuchFieldError -> 0x0056 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0056 }
                r2 = 7
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0056 }
            L_0x0056:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackgroundGradientDrawable.AnonymousClass2.<clinit>():void");
        }
    }

    public static Rect getGradientPoints(GradientDrawable.Orientation orientation, int i, int i2) {
        Rect rect = new Rect();
        switch (AnonymousClass2.$SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[orientation.ordinal()]) {
            case 1:
                rect.left = i / 2;
                rect.top = 0;
                rect.right = rect.left;
                rect.bottom = i2;
                break;
            case 2:
                rect.left = i;
                rect.top = 0;
                rect.right = 0;
                rect.bottom = i2;
                break;
            case 3:
                rect.left = i;
                rect.top = i2 / 2;
                rect.right = 0;
                rect.bottom = rect.top;
                break;
            case 4:
                rect.left = i;
                rect.top = i2;
                rect.right = 0;
                rect.bottom = 0;
                break;
            case 5:
                rect.left = i / 2;
                rect.top = i2;
                rect.right = rect.left;
                rect.bottom = 0;
                break;
            case 6:
                rect.left = 0;
                rect.top = i2;
                rect.right = i;
                rect.bottom = 0;
                break;
            case 7:
                rect.left = 0;
                rect.top = i2 / 2;
                rect.right = i;
                rect.bottom = rect.top;
                break;
            default:
                rect.left = 0;
                rect.top = 0;
                rect.right = i;
                rect.bottom = i2;
                break;
        }
        return rect;
    }

    public static Rect getGradientPoints(int i, int i2, int i3) {
        return getGradientPoints(getGradientOrientation(i), i2, i3);
    }

    public static GradientDrawable.Orientation getGradientOrientation(int i) {
        if (i == 0) {
            return GradientDrawable.Orientation.BOTTOM_TOP;
        }
        if (i == 90) {
            return GradientDrawable.Orientation.LEFT_RIGHT;
        }
        if (i == 135) {
            return GradientDrawable.Orientation.TL_BR;
        }
        if (i == 180) {
            return GradientDrawable.Orientation.TOP_BOTTOM;
        }
        if (i == 225) {
            return GradientDrawable.Orientation.TR_BL;
        }
        if (i == 270) {
            return GradientDrawable.Orientation.RIGHT_LEFT;
        }
        if (i != 315) {
            return GradientDrawable.Orientation.BL_TR;
        }
        return GradientDrawable.Orientation.BR_TL;
    }

    public static BitmapDrawable createDitheredGradientBitmapDrawable(int i, int[] iArr, int i2, int i3) {
        return createDitheredGradientBitmapDrawable(getGradientOrientation(i), iArr, i2, i3);
    }

    public static BitmapDrawable createDitheredGradientBitmapDrawable(GradientDrawable.Orientation orientation, int[] iArr, int i, int i2) {
        return new BitmapDrawable(ApplicationLoader.applicationContext.getResources(), createDitheredGradientBitmap(orientation, iArr, i, i2));
    }

    private static Bitmap createDitheredGradientBitmap(GradientDrawable.Orientation orientation, int[] iArr, int i, int i2) {
        Rect gradientPoints = getGradientPoints(orientation, i, i2);
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        Utilities.drawDitheredGradient(createBitmap, iArr, gradientPoints.left, gradientPoints.top, gradientPoints.right, gradientPoints.bottom);
        return createBitmap;
    }
}

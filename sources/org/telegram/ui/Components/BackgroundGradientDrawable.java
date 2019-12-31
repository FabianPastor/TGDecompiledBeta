package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import androidx.collection.ArrayMap;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Utilities;

public class BackgroundGradientDrawable extends GradientDrawable {
    public static final float DEFAULT_COMPRESS_RATIO = 0.5f;
    private final Paint bitmapPaint = new Paint(1);
    private final ArrayMap<IntSize, Bitmap> bitmaps = new ArrayMap();
    private final int[] colors;
    private final ArrayMap<View, Disposable> disposables = new ArrayMap();
    private boolean disposed = false;
    private final List<Runnable[]> ditheringRunnables = new ArrayList();
    private final ArrayMap<IntSize, Boolean> isForExactBounds = new ArrayMap();

    /* renamed from: org.telegram.ui.Components.BackgroundGradientDrawable$2 */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation = new int[Orientation.values().length];

        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:11:0x0040 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
        /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x004b */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:9:0x0035 */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Can't wrap try/catch for region: R(16:0|1|2|3|4|5|6|7|8|9|10|11|12|13|14|16) */
        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        /* JADX WARNING: Can't wrap try/catch for region: R(14:0|1|2|3|4|5|6|7|8|9|10|11|12|(3:13|14|16)) */
        static {
            /*
            r0 = android.graphics.drawable.GradientDrawable.Orientation.values();
            r0 = r0.length;
            r0 = new int[r0];
            $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation = r0;
            r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = android.graphics.drawable.GradientDrawable.Orientation.TOP_BOTTOM;	 Catch:{ NoSuchFieldError -> 0x0014 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0014 }
            r2 = 1;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0014 }
        L_0x0014:
            r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = android.graphics.drawable.GradientDrawable.Orientation.TR_BL;	 Catch:{ NoSuchFieldError -> 0x001f }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x001f }
            r2 = 2;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x001f }
        L_0x001f:
            r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = android.graphics.drawable.GradientDrawable.Orientation.RIGHT_LEFT;	 Catch:{ NoSuchFieldError -> 0x002a }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x002a }
            r2 = 3;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x002a }
        L_0x002a:
            r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = android.graphics.drawable.GradientDrawable.Orientation.BR_TL;	 Catch:{ NoSuchFieldError -> 0x0035 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0035 }
            r2 = 4;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0035 }
        L_0x0035:
            r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;	 Catch:{ NoSuchFieldError -> 0x0040 }
            r1 = android.graphics.drawable.GradientDrawable.Orientation.BOTTOM_TOP;	 Catch:{ NoSuchFieldError -> 0x0040 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0040 }
            r2 = 5;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0040 }
        L_0x0040:
            r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;	 Catch:{ NoSuchFieldError -> 0x004b }
            r1 = android.graphics.drawable.GradientDrawable.Orientation.BL_TR;	 Catch:{ NoSuchFieldError -> 0x004b }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x004b }
            r2 = 6;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x004b }
        L_0x004b:
            r0 = $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;	 Catch:{ NoSuchFieldError -> 0x0056 }
            r1 = android.graphics.drawable.GradientDrawable.Orientation.LEFT_RIGHT;	 Catch:{ NoSuchFieldError -> 0x0056 }
            r1 = r1.ordinal();	 Catch:{ NoSuchFieldError -> 0x0056 }
            r2 = 7;
            r0[r1] = r2;	 Catch:{ NoSuchFieldError -> 0x0056 }
        L_0x0056:
            return;
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.BackgroundGradientDrawable$AnonymousClass2.<clinit>():void");
        }
    }

    public interface Disposable {
        void dispose();
    }

    public interface Listener {
        void onAllSizesReady();

        void onSizeReady(int i, int i2);
    }

    public static class Sizes {
        private final IntSize[] arr;

        public enum Orientation {
            PORTRAIT,
            LANDSCAPE,
            BOTH
        }

        private Sizes(int i, int i2, int... iArr) {
            this.arr = new IntSize[((iArr.length / 2) + 1)];
            IntSize[] intSizeArr = this.arr;
            IntSize intSize = new IntSize(i, i2);
            i = 0;
            intSizeArr[0] = intSize;
            while (i < iArr.length / 2) {
                int i3 = i + 1;
                i *= 2;
                this.arr[i3] = new IntSize(iArr[i], iArr[i + 1]);
                i = i3;
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
            int i3 = 1;
            if (orientation == Orientation.BOTH) {
                return of(i, i2, i2, i);
            }
            int i4 = orientation == Orientation.PORTRAIT ? 1 : 0;
            if (i >= i2) {
                i3 = 0;
            }
            return i4 == i3 ? of(i, i2, new int[0]) : of(i2, i, new int[0]);
        }
    }

    public static class ListenerAdapter implements Listener {
        public void onAllSizesReady() {
        }

        public void onSizeReady(int i, int i2) {
        }
    }

    public BackgroundGradientDrawable(Orientation orientation, int[] iArr) {
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
            canvas.drawBitmap(findBestBitmapForSize, null, bounds, this.bitmapPaint);
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
            IntSize intSize = (IntSize) this.bitmaps.keyAt(i);
            if (intSize.width == width && intSize.height == height) {
                Bitmap bitmap = (Bitmap) this.bitmaps.valueAt(i);
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, null, bounds, this.bitmapPaint);
                } else {
                    super.draw(canvas);
                }
                return (Disposable) this.disposables.get(view);
            }
        }
        Disposable disposable = (Disposable) this.disposables.remove(view);
        if (disposable != null) {
            disposable.dispose();
        }
        IntSize intSize2 = new IntSize(width, height);
        this.bitmaps.put(intSize2, null);
        this.isForExactBounds.put(intSize2, Boolean.valueOf(true));
        Disposable disposable2 = (Disposable) this.disposables.put(view, new -$$Lambda$BackgroundGradientDrawable$GzEdPAGaK9eizgcofyUmITEmyAs(this, view, startDitheringInternal(new IntSize[]{intSize2}, new ListenerAdapter() {
            public void onAllSizesReady() {
                view.invalidate();
            }
        }, 0)));
        super.draw(canvas);
        return disposable2;
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

    /* Access modifiers changed, original: protected */
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
        for (Object obj : sizes.arr) {
            if (!this.bitmaps.containsKey(obj)) {
                this.bitmaps.put(obj, null);
                arrayList.add(obj);
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
        Listener[] listenerArr = new Listener[]{listener};
        Runnable[] runnableArr = new Runnable[intSizeArr.length];
        this.ditheringRunnables.add(runnableArr);
        for (int i = 0; i < intSizeArr.length; i++) {
            IntSize intSize = intSizeArr[i];
            DispatchQueue dispatchQueue = Utilities.globalQueue;
            -$$Lambda$BackgroundGradientDrawable$hxdrjDm4pRekb-nxNfgt2P7Ou-g -__lambda_backgroundgradientdrawable_hxdrjdm4prekb-nxnfgt2p7ou-g = new -$$Lambda$BackgroundGradientDrawable$hxdrjDm4pRekb-nxNfgt2P7Ou-g(this, intSize, runnableArr, i, listenerArr);
            runnableArr[i] = -__lambda_backgroundgradientdrawable_hxdrjdm4prekb-nxnfgt2p7ou-g;
            dispatchQueue.postRunnable(-__lambda_backgroundgradientdrawable_hxdrjdm4prekb-nxnfgt2p7ou-g, j);
        }
        return new -$$Lambda$BackgroundGradientDrawable$A-pstgPecKYALRS-Lp4n1_gbMG4(this, listenerArr, runnableArr, intSizeArr);
    }

    public /* synthetic */ void lambda$startDitheringInternal$2$BackgroundGradientDrawable(IntSize intSize, Runnable[] runnableArr, int i, Listener[] listenerArr) {
        try {
            AndroidUtilities.runOnUIThread(new -$$Lambda$BackgroundGradientDrawable$J9mCZNpwXG0PEj5DU9Y7dVW3AHA(this, runnableArr, createDitheredGradientBitmap(getOrientation(), this.colors, intSize.width, intSize.height), intSize, i, listenerArr));
        } catch (Throwable th) {
            AndroidUtilities.runOnUIThread(new -$$Lambda$BackgroundGradientDrawable$J9mCZNpwXG0PEj5DU9Y7dVW3AHA(this, runnableArr, null, intSize, i, listenerArr));
        }
    }

    public /* synthetic */ void lambda$null$1$BackgroundGradientDrawable(Runnable[] runnableArr, Bitmap bitmap, IntSize intSize, int i, Listener[] listenerArr) {
        if (this.ditheringRunnables.contains(runnableArr)) {
            if (bitmap != null) {
                this.bitmaps.put(intSize, bitmap);
            } else {
                this.bitmaps.remove(intSize);
                this.isForExactBounds.remove(intSize);
            }
            runnableArr[i] = null;
            Object obj = 1;
            if (runnableArr.length > 1) {
                for (Runnable runnable : runnableArr) {
                    if (runnable != null) {
                        break;
                    }
                }
            }
            obj = null;
            if (obj == null) {
                this.ditheringRunnables.remove(runnableArr);
            }
            if (listenerArr[0] != null) {
                listenerArr[0].onSizeReady(intSize.width, intSize.height);
                if (obj == null) {
                    listenerArr[0].onAllSizesReady();
                    listenerArr[0] = null;
                }
            }
            return;
        }
        if (bitmap != null) {
            bitmap.recycle();
        }
    }

    public /* synthetic */ void lambda$startDitheringInternal$3$BackgroundGradientDrawable(Listener[] listenerArr, Runnable[] runnableArr, IntSize[] intSizeArr) {
        int i = 0;
        listenerArr[0] = null;
        if (this.ditheringRunnables.contains(runnableArr)) {
            Utilities.globalQueue.cancelRunnables(runnableArr);
            this.ditheringRunnables.remove(runnableArr);
        }
        while (i < intSizeArr.length) {
            Object obj = intSizeArr[i];
            Bitmap bitmap = (Bitmap) this.bitmaps.remove(obj);
            this.isForExactBounds.remove(obj);
            if (bitmap != null) {
                bitmap.recycle();
            }
            i++;
        }
    }

    public void dispose() {
        if (!this.disposed) {
            int size;
            for (size = this.ditheringRunnables.size() - 1; size >= 0; size--) {
                Utilities.globalQueue.cancelRunnables((Runnable[]) this.ditheringRunnables.remove(size));
            }
            for (size = this.bitmaps.size() - 1; size >= 0; size--) {
                Bitmap bitmap = (Bitmap) this.bitmaps.removeAt(size);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            this.isForExactBounds.clear();
            this.disposables.clear();
            this.disposed = true;
        }
    }

    private Bitmap findBestBitmapForSize(int i, int i2) {
        int size = this.bitmaps.size();
        Bitmap bitmap = null;
        float f = Float.MAX_VALUE;
        for (int i3 = 0; i3 < size; i3++) {
            IntSize intSize = (IntSize) this.bitmaps.keyAt(i3);
            float sqrt = (float) Math.sqrt(Math.pow((double) (i - intSize.width), 2.0d) + Math.pow((double) (i2 - intSize.height), 2.0d));
            if (sqrt < f) {
                Bitmap bitmap2 = (Bitmap) this.bitmaps.valueAt(i3);
                if (bitmap2 != null) {
                    Boolean bool = (Boolean) this.isForExactBounds.get(intSize);
                    if (bool == null || !bool.booleanValue()) {
                        f = sqrt;
                        bitmap = bitmap2;
                    }
                }
            }
        }
        return bitmap;
    }

    public static Rect getGradientPoints(Orientation orientation, int i, int i2) {
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

    public static Orientation getGradientOrientation(int i) {
        if (i == 0) {
            return Orientation.BOTTOM_TOP;
        }
        if (i == 90) {
            return Orientation.LEFT_RIGHT;
        }
        if (i == 135) {
            return Orientation.TL_BR;
        }
        if (i == 180) {
            return Orientation.TOP_BOTTOM;
        }
        if (i == 225) {
            return Orientation.TR_BL;
        }
        if (i == 270) {
            return Orientation.RIGHT_LEFT;
        }
        if (i != 315) {
            return Orientation.BL_TR;
        }
        return Orientation.BR_TL;
    }

    public static BitmapDrawable createDitheredGradientBitmapDrawable(int i, int[] iArr, int i2, int i3) {
        return createDitheredGradientBitmapDrawable(getGradientOrientation(i), iArr, i2, i3);
    }

    public static BitmapDrawable createDitheredGradientBitmapDrawable(Orientation orientation, int[] iArr, int i, int i2) {
        return new BitmapDrawable(ApplicationLoader.applicationContext.getResources(), createDitheredGradientBitmap(orientation, iArr, i, i2));
    }

    private static Bitmap createDitheredGradientBitmap(Orientation orientation, int[] iArr, int i, int i2) {
        Rect gradientPoints = getGradientPoints(orientation, i, i2);
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Config.ARGB_8888);
        Utilities.drawDitheredGradient(createBitmap, iArr, gradientPoints.left, gradientPoints.top, gradientPoints.right, gradientPoints.bottom);
        return createBitmap;
    }
}

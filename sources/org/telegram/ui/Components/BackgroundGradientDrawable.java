package org.telegram.ui.Components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
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

public class BackgroundGradientDrawable extends GradientDrawable {
    public static final float DEFAULT_COMPRESS_RATIO = 0.5f;
    private final Paint bitmapPaint;
    private final ArrayMap<IntSize, Bitmap> bitmaps = new ArrayMap<>();
    private final int[] colors;
    private final ArrayMap<View, Disposable> disposables = new ArrayMap<>();
    private boolean disposed;
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
        public void onSizeReady(int width, int height) {
        }

        public void onAllSizesReady() {
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

        private Sizes(int width, int height, int... additionalSizes) {
            IntSize[] intSizeArr = new IntSize[((additionalSizes.length / 2) + 1)];
            this.arr = intSizeArr;
            intSizeArr[0] = new IntSize(width, height);
            for (int i = 0; i < additionalSizes.length / 2; i++) {
                this.arr[i + 1] = new IntSize(additionalSizes[i * 2], additionalSizes[(i * 2) + 1]);
            }
        }

        public static Sizes of(int width, int height, int... additionalSizes) {
            return new Sizes(width, height, additionalSizes);
        }

        public static Sizes ofDeviceScreen() {
            return ofDeviceScreen(0.5f);
        }

        public static Sizes ofDeviceScreen(float compressRatio) {
            return ofDeviceScreen(compressRatio, Orientation.BOTH);
        }

        public static Sizes ofDeviceScreen(Orientation orientation) {
            return ofDeviceScreen(0.5f, orientation);
        }

        public static Sizes ofDeviceScreen(float compressRatio, Orientation orientation) {
            int width = (int) (((float) AndroidUtilities.displaySize.x) * compressRatio);
            int height = (int) (((float) AndroidUtilities.displaySize.y) * compressRatio);
            if (width == height) {
                return of(width, height, new int[0]);
            }
            boolean z = true;
            if (orientation == Orientation.BOTH) {
                return of(width, height, height, width);
            }
            boolean z2 = orientation == Orientation.PORTRAIT;
            if (width >= height) {
                z = false;
            }
            int[] iArr = new int[0];
            return z2 == z ? of(width, height, iArr) : of(height, width, iArr);
        }
    }

    public BackgroundGradientDrawable(GradientDrawable.Orientation orientation, int[] colors2) {
        super(orientation, colors2);
        Paint paint = new Paint(1);
        this.bitmapPaint = paint;
        this.disposed = false;
        setDither(true);
        this.colors = colors2;
        paint.setDither(true);
    }

    public void draw(Canvas canvas) {
        if (this.disposed) {
            super.draw(canvas);
            return;
        }
        Rect bounds = getBounds();
        Bitmap bitmap = findBestBitmapForSize(bounds.width(), bounds.height());
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, (Rect) null, bounds, this.bitmapPaint);
        } else {
            super.draw(canvas);
        }
    }

    public Disposable drawExactBoundsSize(Canvas canvas, View ownerView) {
        return drawExactBoundsSize(canvas, ownerView, 0.5f);
    }

    public Disposable drawExactBoundsSize(Canvas canvas, final View ownerView, float compressRatio) {
        if (this.disposed) {
            super.draw(canvas);
            return null;
        }
        Rect bounds = getBounds();
        int width = (int) (((float) bounds.width()) * compressRatio);
        int height = (int) (((float) bounds.height()) * compressRatio);
        int count = this.bitmaps.size();
        for (int i = 0; i < count; i++) {
            IntSize size = this.bitmaps.keyAt(i);
            if (size.width == width && size.height == height) {
                Bitmap bitmap = this.bitmaps.valueAt(i);
                if (bitmap != null) {
                    canvas.drawBitmap(bitmap, (Rect) null, bounds, this.bitmapPaint);
                } else {
                    super.draw(canvas);
                }
                return this.disposables.get(ownerView);
            }
        }
        Disposable oldDisposable = this.disposables.remove(ownerView);
        if (oldDisposable != null) {
            oldDisposable.dispose();
        }
        IntSize size2 = new IntSize(width, height);
        this.bitmaps.put(size2, null);
        this.isForExactBounds.put(size2, true);
        Disposable disposable = this.disposables.put(ownerView, new BackgroundGradientDrawable$$ExternalSyntheticLambda2(this, ownerView, startDitheringInternal(new IntSize[]{size2}, new ListenerAdapter() {
            public void onAllSizesReady() {
                ownerView.invalidate();
            }
        }, 0)));
        super.draw(canvas);
        return disposable;
    }

    /* renamed from: lambda$drawExactBoundsSize$0$org-telegram-ui-Components-BackgroundGradientDrawable  reason: not valid java name */
    public /* synthetic */ void m564xc8CLASSNAMEd9(View ownerView, Disposable delegate) {
        this.disposables.remove(ownerView);
        delegate.dispose();
    }

    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        this.bitmapPaint.setAlpha(alpha);
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

    public Disposable startDithering(Sizes sizes, Listener listener, long delay) {
        if (this.disposed) {
            return null;
        }
        List<IntSize> sizesList = new ArrayList<>(sizes.arr.length);
        for (IntSize size : sizes.arr) {
            if (!this.bitmaps.containsKey(size)) {
                this.bitmaps.put(size, null);
                sizesList.add(size);
            }
        }
        if (sizesList.isEmpty() != 0) {
            return null;
        }
        return startDitheringInternal((IntSize[]) sizesList.toArray(new IntSize[0]), listener, delay);
    }

    private Disposable startDitheringInternal(IntSize[] sizesArr, Listener listener, long delay) {
        IntSize[] intSizeArr = sizesArr;
        if (intSizeArr.length == 0) {
            return null;
        }
        Listener[] listenerReference = {listener};
        Runnable[] runnables = new Runnable[intSizeArr.length];
        this.ditheringRunnables.add(runnables);
        for (int i = 0; i < intSizeArr.length; i++) {
            IntSize size = intSizeArr[i];
            if (size.width == 0) {
                long j = delay;
            } else if (size.height == 0) {
                long j2 = delay;
            } else {
                DispatchQueue dispatchQueue = Utilities.globalQueue;
                BackgroundGradientDrawable$$ExternalSyntheticLambda0 backgroundGradientDrawable$$ExternalSyntheticLambda0 = new BackgroundGradientDrawable$$ExternalSyntheticLambda0(this, size, runnables, i, listenerReference);
                runnables[i] = backgroundGradientDrawable$$ExternalSyntheticLambda0;
                dispatchQueue.postRunnable(backgroundGradientDrawable$$ExternalSyntheticLambda0, delay);
            }
        }
        long j3 = delay;
        return new BackgroundGradientDrawable$$ExternalSyntheticLambda3(this, listenerReference, runnables, sizesArr);
    }

    /* renamed from: lambda$startDitheringInternal$2$org-telegram-ui-Components-BackgroundGradientDrawable  reason: not valid java name */
    public /* synthetic */ void m566xfdd06cfb(IntSize size, Runnable[] runnables, int index, Listener[] listenerReference) {
        IntSize intSize = size;
        try {
            try {
                AndroidUtilities.runOnUIThread(new BackgroundGradientDrawable$$ExternalSyntheticLambda1(this, runnables, createDitheredGradientBitmap(getOrientation(), this.colors, intSize.width, intSize.height), size, index, listenerReference));
            } catch (Throwable th) {
                th = th;
                AndroidUtilities.runOnUIThread(new BackgroundGradientDrawable$$ExternalSyntheticLambda1(this, runnables, (Bitmap) null, size, index, listenerReference));
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            AndroidUtilities.runOnUIThread(new BackgroundGradientDrawable$$ExternalSyntheticLambda1(this, runnables, (Bitmap) null, size, index, listenerReference));
            throw th;
        }
    }

    /* renamed from: lambda$startDitheringInternal$1$org-telegram-ui-Components-BackgroundGradientDrawable  reason: not valid java name */
    public /* synthetic */ void m565xd47CLASSNAMEba(Runnable[] runnables, Bitmap bitmap, IntSize size, int index, Listener[] listenerReference) {
        if (this.ditheringRunnables.contains(runnables)) {
            if (bitmap != null) {
                this.bitmaps.put(size, bitmap);
            } else {
                this.bitmaps.remove(size);
                this.isForExactBounds.remove(size);
            }
            runnables[index] = null;
            boolean hasNotNull = false;
            if (runnables.length > 1) {
                int j = 0;
                while (true) {
                    if (j >= runnables.length) {
                        break;
                    } else if (runnables[j] != null) {
                        hasNotNull = true;
                        break;
                    } else {
                        j++;
                    }
                }
            }
            if (!hasNotNull) {
                this.ditheringRunnables.remove(runnables);
            }
            if (listenerReference[0] != null) {
                listenerReference[0].onSizeReady(size.width, size.height);
                if (!hasNotNull) {
                    listenerReference[0].onAllSizesReady();
                    listenerReference[0] = null;
                }
            }
        } else if (bitmap != null) {
            bitmap.recycle();
        }
    }

    /* renamed from: lambda$startDitheringInternal$3$org-telegram-ui-Components-BackgroundGradientDrawable  reason: not valid java name */
    public /* synthetic */ void m567x2724CLASSNAMEc(Listener[] listenerReference, Runnable[] runnables, IntSize[] sizesArr) {
        listenerReference[0] = null;
        if (this.ditheringRunnables.contains(runnables)) {
            Utilities.globalQueue.cancelRunnables(runnables);
            this.ditheringRunnables.remove(runnables);
        }
        for (IntSize size : sizesArr) {
            Bitmap bitmap = this.bitmaps.remove(size);
            this.isForExactBounds.remove(size);
            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }

    public void dispose() {
        if (!this.disposed) {
            for (int i = this.ditheringRunnables.size() - 1; i >= 0; i--) {
                Utilities.globalQueue.cancelRunnables(this.ditheringRunnables.remove(i));
            }
            for (int i2 = this.bitmaps.size() - 1; i2 >= 0; i2--) {
                Bitmap bitmap = this.bitmaps.removeAt(i2);
                if (bitmap != null) {
                    bitmap.recycle();
                }
            }
            this.isForExactBounds.clear();
            this.disposables.clear();
            this.disposed = true;
        }
    }

    private Bitmap findBestBitmapForSize(int width, int height) {
        Bitmap bitmap;
        Boolean forExactBounds;
        Bitmap bestBitmap = null;
        float minDist = Float.MAX_VALUE;
        int count = this.bitmaps.size();
        for (int i = 0; i < count; i++) {
            IntSize size = this.bitmaps.keyAt(i);
            float dist = (float) Math.sqrt(Math.pow((double) (width - size.width), 2.0d) + Math.pow((double) (height - size.height), 2.0d));
            if (dist < minDist && (bitmap = this.bitmaps.valueAt(i)) != null && ((forExactBounds = this.isForExactBounds.get(size)) == null || !forExactBounds.booleanValue())) {
                bestBitmap = bitmap;
                minDist = dist;
            }
        }
        return bestBitmap;
    }

    /* renamed from: org.telegram.ui.Components.BackgroundGradientDrawable$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation;

        static {
            int[] iArr = new int[GradientDrawable.Orientation.values().length];
            $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation = iArr;
            try {
                iArr[GradientDrawable.Orientation.TOP_BOTTOM.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[GradientDrawable.Orientation.TR_BL.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[GradientDrawable.Orientation.RIGHT_LEFT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[GradientDrawable.Orientation.BR_TL.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[GradientDrawable.Orientation.BOTTOM_TOP.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[GradientDrawable.Orientation.BL_TR.ordinal()] = 6;
            } catch (NoSuchFieldError e6) {
            }
            try {
                $SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[GradientDrawable.Orientation.LEFT_RIGHT.ordinal()] = 7;
            } catch (NoSuchFieldError e7) {
            }
        }
    }

    public static Rect getGradientPoints(GradientDrawable.Orientation orientation, int width, int height) {
        Rect outRect = new Rect();
        switch (AnonymousClass2.$SwitchMap$android$graphics$drawable$GradientDrawable$Orientation[orientation.ordinal()]) {
            case 1:
                outRect.left = width / 2;
                outRect.top = 0;
                outRect.right = outRect.left;
                outRect.bottom = height;
                break;
            case 2:
                outRect.left = width;
                outRect.top = 0;
                outRect.right = 0;
                outRect.bottom = height;
                break;
            case 3:
                outRect.left = width;
                outRect.top = height / 2;
                outRect.right = 0;
                outRect.bottom = outRect.top;
                break;
            case 4:
                outRect.left = width;
                outRect.top = height;
                outRect.right = 0;
                outRect.bottom = 0;
                break;
            case 5:
                outRect.left = width / 2;
                outRect.top = height;
                outRect.right = outRect.left;
                outRect.bottom = 0;
                break;
            case 6:
                outRect.left = 0;
                outRect.top = height;
                outRect.right = width;
                outRect.bottom = 0;
                break;
            case 7:
                outRect.left = 0;
                outRect.top = height / 2;
                outRect.right = width;
                outRect.bottom = outRect.top;
                break;
            default:
                outRect.left = 0;
                outRect.top = 0;
                outRect.right = width;
                outRect.bottom = height;
                break;
        }
        return outRect;
    }

    public static Rect getGradientPoints(int gradientAngle, int width, int height) {
        return getGradientPoints(getGradientOrientation(gradientAngle), width, height);
    }

    public static GradientDrawable.Orientation getGradientOrientation(int gradientAngle) {
        switch (gradientAngle) {
            case 0:
                return GradientDrawable.Orientation.BOTTOM_TOP;
            case 90:
                return GradientDrawable.Orientation.LEFT_RIGHT;
            case 135:
                return GradientDrawable.Orientation.TL_BR;
            case 180:
                return GradientDrawable.Orientation.TOP_BOTTOM;
            case 225:
                return GradientDrawable.Orientation.TR_BL;
            case 270:
                return GradientDrawable.Orientation.RIGHT_LEFT;
            case 315:
                return GradientDrawable.Orientation.BR_TL;
            default:
                return GradientDrawable.Orientation.BL_TR;
        }
    }

    public static BitmapDrawable createDitheredGradientBitmapDrawable(int angle, int[] colors2, int width, int height) {
        return createDitheredGradientBitmapDrawable(getGradientOrientation(angle), colors2, width, height);
    }

    public static BitmapDrawable createDitheredGradientBitmapDrawable(GradientDrawable.Orientation orientation, int[] colors2, int width, int height) {
        return new BitmapDrawable(ApplicationLoader.applicationContext.getResources(), createDitheredGradientBitmap(orientation, colors2, width, height));
    }

    private static Bitmap createDitheredGradientBitmap(GradientDrawable.Orientation orientation, int[] colors2, int width, int height) {
        Rect r = getGradientPoints(orientation, width, height);
        Bitmap ditheredGradientBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Utilities.drawDitheredGradient(ditheredGradientBitmap, colors2, r.left, r.top, r.right, r.bottom);
        return ditheredGradientBitmap;
    }
}

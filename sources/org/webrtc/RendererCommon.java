package org.webrtc;

import android.graphics.Point;
import android.opengl.Matrix;

public class RendererCommon {
    private static float BALANCED_VISIBLE_FRACTION = 0.5625f;

    public interface GlDrawer {
        void drawOes(int i, float[] fArr, int i2, int i3, int i4, int i5, int i6, int i7);

        void drawRgb(int i, float[] fArr, int i2, int i3, int i4, int i5, int i6, int i7);

        void drawYuv(int[] iArr, float[] fArr, int i, int i2, int i3, int i4, int i5, int i6);

        void release();
    }

    public interface RendererEvents {
        void onFirstFrameRendered();

        void onFrameResolutionChanged(int i, int i2, int i3);
    }

    public enum ScalingType {
        SCALE_ASPECT_FIT,
        SCALE_ASPECT_FILL,
        SCALE_ASPECT_BALANCED
    }

    public static class VideoLayoutMeasure {
        private float visibleFractionMatchOrientation;
        private float visibleFractionMismatchOrientation;

        public VideoLayoutMeasure() {
            ScalingType scalingType = ScalingType.SCALE_ASPECT_BALANCED;
            this.visibleFractionMatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(scalingType);
            this.visibleFractionMismatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(scalingType);
        }

        public void setScalingType(ScalingType scalingType) {
            setScalingType(scalingType, scalingType);
        }

        public void setScalingType(ScalingType scalingType, ScalingType scalingType2) {
            this.visibleFractionMatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(scalingType);
            this.visibleFractionMismatchOrientation = RendererCommon.convertScalingTypeToVisibleFraction(scalingType2);
        }

        public void setVisibleFraction(float f, float f2) {
            this.visibleFractionMatchOrientation = f;
            this.visibleFractionMismatchOrientation = f2;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:29:0x0056, code lost:
            if (r8 == r2) goto L_0x0058;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public android.graphics.Point measure(boolean r8, int r9, int r10, int r11, int r12) {
            /*
                r7 = this;
                r0 = 2147483647(0x7fffffff, float:NaN)
                int r1 = android.view.View.getDefaultSize(r0, r9)
                int r0 = android.view.View.getDefaultSize(r0, r10)
                if (r11 == 0) goto L_0x005b
                if (r12 == 0) goto L_0x005b
                if (r1 == 0) goto L_0x005b
                if (r0 != 0) goto L_0x0014
                goto L_0x005b
            L_0x0014:
                float r11 = (float) r11
                float r12 = (float) r12
                float r11 = r11 / r12
                float r12 = (float) r1
                float r2 = (float) r0
                float r12 = r12 / r2
                r2 = 1
                r3 = 0
                r4 = 1065353216(0x3var_, float:1.0)
                int r5 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
                if (r5 <= 0) goto L_0x0024
                r5 = 1
                goto L_0x0025
            L_0x0024:
                r5 = 0
            L_0x0025:
                int r6 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
                if (r6 <= 0) goto L_0x002b
                r6 = 1
                goto L_0x002c
            L_0x002b:
                r6 = 0
            L_0x002c:
                if (r5 != r6) goto L_0x0031
                float r5 = r7.visibleFractionMatchOrientation
                goto L_0x0033
            L_0x0031:
                float r5 = r7.visibleFractionMismatchOrientation
            L_0x0033:
                android.graphics.Point r5 = org.webrtc.RendererCommon.getDisplaySize((float) r5, (float) r11, (int) r1, (int) r0)
                int r9 = android.view.View.MeasureSpec.getMode(r9)
                r6 = 1073741824(0x40000000, float:2.0)
                if (r9 != r6) goto L_0x0041
                r5.x = r1
            L_0x0041:
                int r9 = android.view.View.MeasureSpec.getMode(r10)
                if (r9 == r6) goto L_0x0058
                if (r8 != 0) goto L_0x005a
                int r8 = (r11 > r4 ? 1 : (r11 == r4 ? 0 : -1))
                if (r8 <= 0) goto L_0x004f
                r8 = 1
                goto L_0x0050
            L_0x004f:
                r8 = 0
            L_0x0050:
                int r9 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
                if (r9 <= 0) goto L_0x0055
                goto L_0x0056
            L_0x0055:
                r2 = 0
            L_0x0056:
                if (r8 != r2) goto L_0x005a
            L_0x0058:
                r5.y = r0
            L_0x005a:
                return r5
            L_0x005b:
                android.graphics.Point r8 = new android.graphics.Point
                r8.<init>(r1, r0)
                return r8
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.RendererCommon.VideoLayoutMeasure.measure(boolean, int, int, int, int):android.graphics.Point");
        }
    }

    public static float[] getLayoutMatrix(boolean z, float f, float f2) {
        float f3;
        float f4;
        if (f2 > f) {
            f4 = f / f2;
            f3 = 1.0f;
        } else {
            f3 = f2 / f;
            f4 = 1.0f;
        }
        if (z) {
            f3 *= -1.0f;
        }
        float[] fArr = new float[16];
        Matrix.setIdentityM(fArr, 0);
        Matrix.scaleM(fArr, 0, f3, f4, 1.0f);
        adjustOrigin(fArr);
        return fArr;
    }

    public static android.graphics.Matrix convertMatrixToAndroidGraphicsMatrix(float[] fArr) {
        float[] fArr2 = {fArr[0], fArr[4], fArr[12], fArr[1], fArr[5], fArr[13], fArr[3], fArr[7], fArr[15]};
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.setValues(fArr2);
        return matrix;
    }

    public static float[] convertMatrixFromAndroidGraphicsMatrix(android.graphics.Matrix matrix) {
        float[] fArr = new float[9];
        matrix.getValues(fArr);
        return new float[]{fArr[0], fArr[3], 0.0f, fArr[6], fArr[1], fArr[4], 0.0f, fArr[7], 0.0f, 0.0f, 1.0f, 0.0f, fArr[2], fArr[5], 0.0f, fArr[8]};
    }

    public static Point getDisplaySize(ScalingType scalingType, float f, int i, int i2) {
        return getDisplaySize(convertScalingTypeToVisibleFraction(scalingType), f, i, i2);
    }

    private static void adjustOrigin(float[] fArr) {
        fArr[12] = fArr[12] - ((fArr[0] + fArr[4]) * 0.5f);
        fArr[13] = fArr[13] - ((fArr[1] + fArr[5]) * 0.5f);
        fArr[12] = fArr[12] + 0.5f;
        fArr[13] = fArr[13] + 0.5f;
    }

    /* renamed from: org.webrtc.RendererCommon$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$org$webrtc$RendererCommon$ScalingType;

        /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(3:5|6|8)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0012 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001d */
        static {
            /*
                org.webrtc.RendererCommon$ScalingType[] r0 = org.webrtc.RendererCommon.ScalingType.values()
                int r0 = r0.length
                int[] r0 = new int[r0]
                $SwitchMap$org$webrtc$RendererCommon$ScalingType = r0
                org.webrtc.RendererCommon$ScalingType r1 = org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FIT     // Catch:{ NoSuchFieldError -> 0x0012 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0012 }
                r2 = 1
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0012 }
            L_0x0012:
                int[] r0 = $SwitchMap$org$webrtc$RendererCommon$ScalingType     // Catch:{ NoSuchFieldError -> 0x001d }
                org.webrtc.RendererCommon$ScalingType r1 = org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_FILL     // Catch:{ NoSuchFieldError -> 0x001d }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001d }
                r2 = 2
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001d }
            L_0x001d:
                int[] r0 = $SwitchMap$org$webrtc$RendererCommon$ScalingType     // Catch:{ NoSuchFieldError -> 0x0028 }
                org.webrtc.RendererCommon$ScalingType r1 = org.webrtc.RendererCommon.ScalingType.SCALE_ASPECT_BALANCED     // Catch:{ NoSuchFieldError -> 0x0028 }
                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0028 }
                r2 = 3
                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0028 }
            L_0x0028:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: org.webrtc.RendererCommon.AnonymousClass1.<clinit>():void");
        }
    }

    /* access modifiers changed from: private */
    public static float convertScalingTypeToVisibleFraction(ScalingType scalingType) {
        int i = AnonymousClass1.$SwitchMap$org$webrtc$RendererCommon$ScalingType[scalingType.ordinal()];
        if (i == 1) {
            return 1.0f;
        }
        if (i == 2) {
            return 0.0f;
        }
        if (i == 3) {
            return BALANCED_VISIBLE_FRACTION;
        }
        throw new IllegalArgumentException();
    }

    public static Point getDisplaySize(float f, float f2, int i, int i2) {
        return (f == 0.0f || f2 == 0.0f) ? new Point(i, i2) : new Point(Math.min(i, Math.round((((float) i2) / f) * f2)), Math.min(i2, Math.round((((float) i) / f) / f2)));
    }
}

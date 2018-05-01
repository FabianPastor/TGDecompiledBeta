package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;

public class GLMatrix {
    public static float[] LoadOrtho(float f, float f2, float f3, float f4, float f5, float f6) {
        f = (-(f2 + f)) / (f2 - f);
        f2 = (-(f4 + f3)) / (f4 - f3);
        f3 = (-(f6 + f5)) / (f6 - f5);
        return new float[]{2.0f / (f2 - f), null, null, null, null, 2.0f / (f4 - f3), null, null, null, null, -2.0f / (f6 - f5), null, f, f2, f3, 1.0f};
    }

    public static float[] LoadGraphicsMatrix(Matrix matrix) {
        r0 = new float[16];
        r2 = new float[9];
        matrix.getValues(r2);
        r0[0] = r2[0];
        r0[1] = r2[1];
        r0[2] = 0.0f;
        r0[3] = 0.0f;
        r0[4] = r2[3];
        r0[5] = r2[4];
        r0[6] = 0.0f;
        r0[7] = 0.0f;
        r0[8] = 0.0f;
        r0[9] = 0.0f;
        r0[10] = 1.0f;
        r0[11] = 0.0f;
        r0[12] = r2[2];
        r0[13] = r2[5];
        r0[14] = 0.0f;
        r0[15] = 1.0f;
        return r0;
    }

    public static float[] MultiplyMat4f(float[] fArr, float[] fArr2) {
        return new float[]{(((fArr[0] * fArr2[0]) + (fArr[4] * fArr2[1])) + (fArr[8] * fArr2[2])) + (fArr[12] * fArr2[3]), (((fArr[1] * fArr2[0]) + (fArr[5] * fArr2[1])) + (fArr[9] * fArr2[2])) + (fArr[13] * fArr2[3]), (((fArr[2] * fArr2[0]) + (fArr[6] * fArr2[1])) + (fArr[10] * fArr2[2])) + (fArr[14] * fArr2[3]), (((fArr[3] * fArr2[0]) + (fArr[7] * fArr2[1])) + (fArr[11] * fArr2[2])) + (fArr[15] * fArr2[3]), (((fArr[0] * fArr2[4]) + (fArr[4] * fArr2[5])) + (fArr[8] * fArr2[6])) + (fArr[12] * fArr2[7]), (((fArr[1] * fArr2[4]) + (fArr[5] * fArr2[5])) + (fArr[9] * fArr2[6])) + (fArr[13] * fArr2[7]), (((fArr[2] * fArr2[4]) + (fArr[6] * fArr2[5])) + (fArr[10] * fArr2[6])) + (fArr[14] * fArr2[7]), (((fArr[3] * fArr2[4]) + (fArr[7] * fArr2[5])) + (fArr[11] * fArr2[6])) + (fArr[15] * fArr2[7]), (((fArr[0] * fArr2[8]) + (fArr[4] * fArr2[9])) + (fArr[8] * fArr2[10])) + (fArr[12] * fArr2[11]), (((fArr[1] * fArr2[8]) + (fArr[5] * fArr2[9])) + (fArr[9] * fArr2[10])) + (fArr[13] * fArr2[11]), (((fArr[2] * fArr2[8]) + (fArr[6] * fArr2[9])) + (fArr[10] * fArr2[10])) + (fArr[14] * fArr2[11]), (((fArr[3] * fArr2[8]) + (fArr[7] * fArr2[9])) + (fArr[11] * fArr2[10])) + (fArr[15] * fArr2[11]), (((fArr[0] * fArr2[12]) + (fArr[4] * fArr2[13])) + (fArr[8] * fArr2[14])) + (fArr[12] * fArr2[15]), (((fArr[1] * fArr2[12]) + (fArr[5] * fArr2[13])) + (fArr[9] * fArr2[14])) + (fArr[13] * fArr2[15]), (((fArr[2] * fArr2[12]) + (fArr[6] * fArr2[13])) + (fArr[10] * fArr2[14])) + (fArr[14] * fArr2[15]), (((fArr[3] * fArr2[12]) + (fArr[7] * fArr2[13])) + (fArr[11] * fArr2[14])) + (fArr[15] * fArr2[15])};
    }
}

package org.telegram.ui.Components.Paint;

import android.graphics.Matrix;

public class GLMatrix {
    public static float[] LoadOrtho(float left, float right, float bottom, float top, float near, float far) {
        return new float[]{2.0f / (right - left), 0.0f, 0.0f, 0.0f, 0.0f, 2.0f / (top - bottom), 0.0f, 0.0f, 0.0f, 0.0f, -2.0f / (far - near), 0.0f, (-(right + left)) / (right - left), (-(top + bottom)) / (top - bottom), (-(far + near)) / (far - near), 1.0f};
    }

    public static float[] LoadGraphicsMatrix(Matrix matrix) {
        float[] v = new float[9];
        matrix.getValues(v);
        return new float[]{v[0], v[1], 0.0f, 0.0f, v[3], v[4], 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, v[2], v[5], 0.0f, 1.0f};
    }

    public static float[] MultiplyMat4f(float[] a, float[] b) {
        return new float[]{(a[0] * b[0]) + (a[4] * b[1]) + (a[8] * b[2]) + (a[12] * b[3]), (a[1] * b[0]) + (a[5] * b[1]) + (a[9] * b[2]) + (a[13] * b[3]), (a[2] * b[0]) + (a[6] * b[1]) + (a[10] * b[2]) + (a[14] * b[3]), (a[3] * b[0]) + (a[7] * b[1]) + (a[11] * b[2]) + (a[15] * b[3]), (a[0] * b[4]) + (a[4] * b[5]) + (a[8] * b[6]) + (a[12] * b[7]), (a[1] * b[4]) + (a[5] * b[5]) + (a[9] * b[6]) + (a[13] * b[7]), (a[2] * b[4]) + (a[6] * b[5]) + (a[10] * b[6]) + (a[14] * b[7]), (a[3] * b[4]) + (a[7] * b[5]) + (a[11] * b[6]) + (a[15] * b[7]), (a[0] * b[8]) + (a[4] * b[9]) + (a[8] * b[10]) + (a[12] * b[11]), (a[1] * b[8]) + (a[5] * b[9]) + (a[9] * b[10]) + (a[13] * b[11]), (a[2] * b[8]) + (a[6] * b[9]) + (a[10] * b[10]) + (a[14] * b[11]), (a[3] * b[8]) + (a[7] * b[9]) + (a[11] * b[10]) + (a[15] * b[11]), (a[0] * b[12]) + (a[4] * b[13]) + (a[8] * b[14]) + (a[12] * b[15]), (a[1] * b[12]) + (a[5] * b[13]) + (a[9] * b[14]) + (a[13] * b[15]), (a[2] * b[12]) + (a[6] * b[13]) + (a[10] * b[14]) + (a[14] * b[15]), (a[3] * b[12]) + (a[7] * b[13]) + (a[11] * b[14]) + (a[15] * b[15])};
    }
}

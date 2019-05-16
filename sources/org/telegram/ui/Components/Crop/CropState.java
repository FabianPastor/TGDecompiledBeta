package org.telegram.ui.Components.Crop;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class CropState {
    private float height;
    private Matrix matrix = new Matrix();
    private float minimumScale;
    private float rotation = 0.0f;
    private float scale = 1.0f;
    private float[] values = new float[9];
    private float width;
    private float x = 0.0f;
    private float y = 0.0f;

    public CropState(Bitmap bitmap) {
        this.width = (float) bitmap.getWidth();
        this.height = (float) bitmap.getHeight();
    }

    private void updateValues() {
        this.matrix.getValues(this.values);
    }

    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public void translate(float f, float f2) {
        this.x += f;
        this.y += f2;
        this.matrix.postTranslate(f, f2);
    }

    public float getX() {
        updateValues();
        float[] fArr = this.values;
        Matrix matrix = this.matrix;
        return fArr[2];
    }

    public float getY() {
        updateValues();
        float[] fArr = this.values;
        Matrix matrix = this.matrix;
        return fArr[5];
    }

    public void scale(float f, float f2, float f3) {
        this.scale *= f;
        this.matrix.postScale(f, f, f2, f3);
    }

    public float getScale() {
        return this.scale;
    }

    public void rotate(float f, float f2, float f3) {
        this.rotation += f;
        this.matrix.postRotate(f, f2, f3);
    }

    public float getRotation() {
        return this.rotation;
    }

    public void reset(CropAreaView cropAreaView) {
        this.matrix.reset();
        this.x = 0.0f;
        this.y = 0.0f;
        this.rotation = 0.0f;
        this.minimumScale = cropAreaView.getCropWidth() / this.width;
        this.scale = this.minimumScale;
        Matrix matrix = this.matrix;
        float f = this.scale;
        matrix.postScale(f, f);
    }

    public void getConcatMatrix(Matrix matrix) {
        matrix.postConcat(this.matrix);
    }

    public Matrix getMatrix() {
        Matrix matrix = new Matrix();
        matrix.set(this.matrix);
        return matrix;
    }
}

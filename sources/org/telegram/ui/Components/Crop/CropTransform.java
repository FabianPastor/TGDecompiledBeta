package org.telegram.ui.Components.Crop;

public class CropTransform {
    private float cropAreaX;
    private float cropAreaY;
    private int cropOrientation;
    private float cropPh;
    private float cropPw;
    private float cropPx;
    private float cropPy;
    private float cropRotation;
    private float cropScale;
    private boolean hasTransform;
    private boolean isMirrored;
    private float minScale;
    private float trueCropScale;

    public void setViewTransform(boolean set) {
        this.hasTransform = set;
    }

    public void setViewTransform(boolean set, float px, float py, float rotate, int orientation, float scale, float cs, float ms, float pw, float ph, float cx, float cy, boolean mirrored) {
        this.hasTransform = set;
        this.cropPx = px;
        this.cropPy = py;
        this.cropScale = scale;
        this.cropRotation = rotate;
        this.cropOrientation = orientation;
        while (true) {
            int i = this.cropOrientation;
            if (i >= 0) {
                break;
            }
            this.cropOrientation = i + 360;
        }
        while (true) {
            int i2 = this.cropOrientation;
            if (i2 >= 360) {
                this.cropOrientation = i2 - 360;
            } else {
                this.cropPw = pw;
                this.cropPh = ph;
                this.cropAreaX = cx;
                this.cropAreaY = cy;
                this.trueCropScale = cs;
                this.minScale = ms;
                this.isMirrored = mirrored;
                return;
            }
        }
    }

    public boolean hasViewTransform() {
        return this.hasTransform;
    }

    public float getCropAreaX() {
        return this.cropAreaX;
    }

    public float getCropAreaY() {
        return this.cropAreaY;
    }

    public float getCropPx() {
        return this.cropPx;
    }

    public float getCropPy() {
        return this.cropPy;
    }

    public float getScale() {
        return this.cropScale;
    }

    public float getRotation() {
        return this.cropRotation;
    }

    public int getOrientation() {
        return this.cropOrientation;
    }

    public float getTrueCropScale() {
        return this.trueCropScale;
    }

    public float getMinScale() {
        return this.minScale;
    }

    public float getCropPw() {
        return this.cropPw;
    }

    public float getCropPh() {
        return this.cropPh;
    }

    public boolean isMirrored() {
        return this.isMirrored;
    }

    public CropTransform clone() {
        CropTransform cloned = new CropTransform();
        cloned.hasTransform = this.hasTransform;
        cloned.cropPx = this.cropPx;
        cloned.cropPy = this.cropPy;
        cloned.cropAreaX = this.cropAreaX;
        cloned.cropAreaY = this.cropAreaY;
        cloned.cropScale = this.cropScale;
        cloned.cropRotation = this.cropRotation;
        cloned.isMirrored = this.isMirrored;
        cloned.cropOrientation = this.cropOrientation;
        cloned.cropPw = this.cropPw;
        cloned.cropPh = this.cropPh;
        cloned.trueCropScale = this.trueCropScale;
        cloned.minScale = this.minScale;
        return cloned;
    }
}

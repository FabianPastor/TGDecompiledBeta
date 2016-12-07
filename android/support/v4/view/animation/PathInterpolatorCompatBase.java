package android.support.v4.view.animation;

import android.graphics.Path;
import android.view.animation.Interpolator;

class PathInterpolatorCompatBase {
    private PathInterpolatorCompatBase() {
    }

    public static Interpolator create(Path path) {
        return new PathInterpolatorGingerbread(path);
    }

    public static Interpolator create(float controlX, float controlY) {
        return new PathInterpolatorGingerbread(controlX, controlY);
    }

    public static Interpolator create(float controlX1, float controlY1, float controlX2, float controlY2) {
        return new PathInterpolatorGingerbread(controlX1, controlY1, controlX2, controlY2);
    }
}

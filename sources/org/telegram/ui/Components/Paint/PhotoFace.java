package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.PointF;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Size;

public class PhotoFace {
    private float angle;
    private Point chinPoint;
    private Point eyesCenterPoint;
    private float eyesDistance;
    private Point foreheadPoint;
    private Point mouthPoint;
    private float width;

    public PhotoFace(Face face, Bitmap bitmap, Size size, boolean z) {
        float f;
        Point point = null;
        Point point2 = null;
        Point point3 = point2;
        Point point4 = point3;
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            int type = landmark.getType();
            if (type == 4) {
                point = transposePoint(position, bitmap, size, z);
            } else if (type == 5) {
                point3 = transposePoint(position, bitmap, size, z);
            } else if (type == 10) {
                point2 = transposePoint(position, bitmap, size, z);
            } else if (type == 11) {
                point4 = transposePoint(position, bitmap, size, z);
            }
        }
        if (!(point == null || point2 == null)) {
            this.eyesCenterPoint = new Point((point.x * 0.5f) + (point2.x * 0.5f), (point.y * 0.5f) + (point2.y * 0.5f));
            this.eyesDistance = (float) Math.hypot((double) (point2.x - point.x), (double) (point2.y - point.y));
            this.angle = (float) Math.toDegrees(Math.atan2((double) (point2.y - point.y), (double) (point2.x - point.x)) + 3.141592653589793d);
            f = this.eyesDistance;
            this.width = 2.35f * f;
            f *= 0.8f;
            double toRadians = (double) ((float) Math.toRadians((double) (this.angle - 90.0f)));
            this.foreheadPoint = new Point(this.eyesCenterPoint.x + (((float) Math.cos(toRadians)) * f), this.eyesCenterPoint.y + (f * ((float) Math.sin(toRadians))));
        }
        if (point3 != null && point4 != null) {
            this.mouthPoint = new Point((point3.x * 0.5f) + (point4.x * 0.5f), (point3.y * 0.5f) + (point4.y * 0.5f));
            f = this.eyesDistance * 0.7f;
            double toRadians2 = (double) ((float) Math.toRadians((double) (this.angle + 90.0f)));
            this.chinPoint = new Point(this.mouthPoint.x + (((float) Math.cos(toRadians2)) * f), this.mouthPoint.y + (f * ((float) Math.sin(toRadians2))));
        }
    }

    public boolean isSufficient() {
        return this.eyesCenterPoint != null;
    }

    private Point transposePoint(PointF pointF, Bitmap bitmap, Size size, boolean z) {
        return new Point((size.width * pointF.x) / ((float) (z ? bitmap.getHeight() : bitmap.getWidth())), (size.height * pointF.y) / ((float) (z ? bitmap.getWidth() : bitmap.getHeight())));
    }

    public Point getPointForAnchor(int i) {
        if (i == 0) {
            return this.foreheadPoint;
        }
        if (i == 1) {
            return this.eyesCenterPoint;
        }
        if (i != 2) {
            return i != 3 ? null : this.chinPoint;
        } else {
            return this.mouthPoint;
        }
    }

    public float getWidthForAnchor(int i) {
        if (i == 1) {
            return this.eyesDistance;
        }
        return this.width;
    }

    public float getAngle() {
        return this.angle;
    }
}

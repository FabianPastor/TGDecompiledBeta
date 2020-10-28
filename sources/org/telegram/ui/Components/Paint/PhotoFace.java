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
        Point point = null;
        Point point2 = null;
        Point point3 = null;
        Point point4 = null;
        for (Landmark next : face.getLandmarks()) {
            PointF position = next.getPosition();
            int type = next.getType();
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
            if (point.x < point2.x) {
                Point point5 = point2;
                point2 = point;
                point = point5;
            }
            float f = point.x;
            float f2 = point2.x;
            float f3 = point.y;
            float f4 = point2.y;
            this.eyesCenterPoint = new Point((f * 0.5f) + (f2 * 0.5f), (f3 * 0.5f) + (f4 * 0.5f));
            this.eyesDistance = (float) Math.hypot((double) (f2 - f), (double) (f4 - f3));
            float degrees = (float) Math.toDegrees(Math.atan2((double) (point2.y - point.y), (double) (point2.x - point.x)) + 3.141592653589793d);
            this.angle = degrees;
            float f5 = this.eyesDistance;
            this.width = 2.35f * f5;
            float f6 = f5 * 0.8f;
            double radians = (double) ((float) Math.toRadians((double) (degrees - 90.0f)));
            this.foreheadPoint = new Point(this.eyesCenterPoint.x + (((float) Math.cos(radians)) * f6), this.eyesCenterPoint.y + (f6 * ((float) Math.sin(radians))));
        }
        if (point3 != null && point4 != null) {
            if (point3.x < point4.x) {
                Point point6 = point4;
                point4 = point3;
                point3 = point6;
            }
            this.mouthPoint = new Point((point3.x * 0.5f) + (point4.x * 0.5f), (point3.y * 0.5f) + (point4.y * 0.5f));
            float f7 = this.eyesDistance * 0.7f;
            double radians2 = (double) ((float) Math.toRadians((double) (this.angle + 90.0f)));
            this.chinPoint = new Point(this.mouthPoint.x + (((float) Math.cos(radians2)) * f7), this.mouthPoint.y + (f7 * ((float) Math.sin(radians2))));
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
        if (i == 2) {
            return this.mouthPoint;
        }
        if (i != 3) {
            return null;
        }
        return this.chinPoint;
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

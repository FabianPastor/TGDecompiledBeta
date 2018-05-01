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
        Point point3 = point2;
        Point point4 = point3;
        for (Landmark landmark : face.getLandmarks()) {
            PointF position = landmark.getPosition();
            switch (landmark.getType()) {
                case 4:
                    point = transposePoint(position, bitmap, size, z);
                    break;
                case 5:
                    point3 = transposePoint(position, bitmap, size, z);
                    break;
                case 10:
                    point2 = transposePoint(position, bitmap, size, z);
                    break;
                case 11:
                    point4 = transposePoint(position, bitmap, size, z);
                    break;
                default:
                    break;
            }
        }
        if (!(point == null || point2 == null)) {
            this.eyesCenterPoint = new Point((point.f24x * true) + (point2.f24x * 0.5f), (point.f25y * 0.5f) + (point2.f25y * 0.5f));
            this.eyesDistance = (float) Math.hypot((double) (point2.f24x - point.f24x), (double) (point2.f25y - point.f25y));
            this.angle = (float) Math.toDegrees(NUM + Math.atan2((double) (point2.f25y - point.f25y), (double) (point2.f24x - point.f24x)));
            this.width = this.eyesDistance * true;
            size = NUM * this.eyesDistance;
            double toRadians = (double) ((float) Math.toRadians((double) (this.angle - true)));
            this.foreheadPoint = new Point(this.eyesCenterPoint.f24x + (((float) Math.cos(toRadians)) * size), this.eyesCenterPoint.f25y + (size * ((float) Math.sin(toRadians))));
        }
        if (point3 != null && point4 != null) {
            this.mouthPoint = new Point((point3.f24x * true) + (point4.f24x * 0.5f), (point3.f25y * 0.5f) + (NUM * point4.f25y));
            bitmap = NUM * this.eyesDistance;
            double toRadians2 = (double) ((float) Math.toRadians((double) (this.angle + NUM)));
            this.chinPoint = new Point(this.mouthPoint.f24x + (((float) Math.cos(toRadians2)) * bitmap), this.mouthPoint.f25y + (bitmap * ((float) Math.sin(toRadians2))));
        }
    }

    public boolean isSufficient() {
        return this.eyesCenterPoint != null;
    }

    private Point transposePoint(PointF pointF, Bitmap bitmap, Size size, boolean z) {
        return new Point((size.width * pointF.x) / ((float) (z ? bitmap.getHeight() : bitmap.getWidth())), (size.height * pointF.y) / ((float) (z ? bitmap.getWidth() : bitmap.getHeight())));
    }

    public Point getPointForAnchor(int i) {
        switch (i) {
            case 0:
                return this.foreheadPoint;
            case 1:
                return this.eyesCenterPoint;
            case 2:
                return this.mouthPoint;
            case 3:
                return this.chinPoint;
            default:
                return 0;
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

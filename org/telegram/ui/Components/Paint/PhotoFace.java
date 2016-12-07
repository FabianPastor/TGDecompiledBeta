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

    public PhotoFace(Face face, Bitmap sourceBitmap, Size targetSize, boolean sideward) {
        Point leftEyePoint = null;
        Point rightEyePoint = null;
        Point leftMouthPoint = null;
        Point rightMouthPoint = null;
        for (Landmark landmark : face.getLandmarks()) {
            PointF point = landmark.getPosition();
            switch (landmark.getType()) {
                case 4:
                    leftEyePoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
                case 5:
                    leftMouthPoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
                case 10:
                    rightEyePoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
                case 11:
                    rightMouthPoint = transposePoint(point, sourceBitmap, targetSize, sideward);
                    break;
                default:
                    break;
            }
        }
        if (!(leftEyePoint == null || rightEyePoint == null)) {
            this.eyesCenterPoint = new Point((0.5f * leftEyePoint.x) + (0.5f * rightEyePoint.x), (0.5f * leftEyePoint.y) + (0.5f * rightEyePoint.y));
            this.eyesDistance = (float) Math.hypot((double) (rightEyePoint.x - leftEyePoint.x), (double) (rightEyePoint.y - leftEyePoint.y));
            this.angle = (float) Math.toDegrees(3.141592653589793d + Math.atan2((double) (rightEyePoint.y - leftEyePoint.y), (double) (rightEyePoint.x - leftEyePoint.x)));
            this.width = this.eyesDistance * 2.35f;
            float foreheadHeight = 0.8f * this.eyesDistance;
            float upAngle = (float) Math.toRadians((double) (this.angle - 90.0f));
            this.foreheadPoint = new Point(this.eyesCenterPoint.x + (((float) Math.cos((double) upAngle)) * foreheadHeight), this.eyesCenterPoint.y + (((float) Math.sin((double) upAngle)) * foreheadHeight));
        }
        if (leftMouthPoint != null && rightMouthPoint != null) {
            this.mouthPoint = new Point((0.5f * leftMouthPoint.x) + (0.5f * rightMouthPoint.x), (0.5f * leftMouthPoint.y) + (0.5f * rightMouthPoint.y));
            float chinDepth = 0.7f * this.eyesDistance;
            float downAngle = (float) Math.toRadians((double) (this.angle + 90.0f));
            this.chinPoint = new Point(this.mouthPoint.x + (((float) Math.cos((double) downAngle)) * chinDepth), this.mouthPoint.y + (((float) Math.sin((double) downAngle)) * chinDepth));
        }
    }

    public boolean isSufficient() {
        return this.eyesCenterPoint != null;
    }

    private Point transposePoint(PointF point, Bitmap sourceBitmap, Size targetSize, boolean sideward) {
        return new Point((targetSize.width * point.x) / (sideward ? (float) sourceBitmap.getHeight() : (float) sourceBitmap.getWidth()), (targetSize.height * point.y) / (sideward ? (float) sourceBitmap.getWidth() : (float) sourceBitmap.getHeight()));
    }

    public Point getPointForAnchor(int anchor) {
        switch (anchor) {
            case 0:
                return this.foreheadPoint;
            case 1:
                return this.eyesCenterPoint;
            case 2:
                return this.mouthPoint;
            case 3:
                return this.chinPoint;
            default:
                return null;
        }
    }

    public float getWidthForAnchor(int anchor) {
        if (anchor == 1) {
            return this.eyesDistance;
        }
        return this.width;
    }

    public float getAngle() {
        return this.angle;
    }
}

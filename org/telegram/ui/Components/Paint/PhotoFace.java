package org.telegram.ui.Components.Paint;

import android.graphics.Bitmap;
import android.graphics.PointF;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.Landmark;
import java.util.Iterator;
import java.util.List;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Size;

public class PhotoFace
{
  private float angle;
  private Point chinPoint;
  private Point eyesCenterPoint;
  private float eyesDistance;
  private Point foreheadPoint;
  private Point mouthPoint;
  private float width;
  
  public PhotoFace(Face paramFace, Bitmap paramBitmap, Size paramSize, boolean paramBoolean)
  {
    Object localObject = paramFace.getLandmarks();
    Point localPoint3 = null;
    Point localPoint1 = null;
    Point localPoint2 = null;
    paramFace = null;
    localObject = ((List)localObject).iterator();
    while (((Iterator)localObject).hasNext())
    {
      Landmark localLandmark = (Landmark)((Iterator)localObject).next();
      PointF localPointF = localLandmark.getPosition();
      switch (localLandmark.getType())
      {
      case 6: 
      case 7: 
      case 8: 
      case 9: 
      default: 
        break;
      case 4: 
        localPoint3 = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
        break;
      case 10: 
        localPoint1 = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
        break;
      case 5: 
        localPoint2 = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
        break;
      case 11: 
        paramFace = transposePoint(localPointF, paramBitmap, paramSize, paramBoolean);
      }
    }
    float f1;
    float f2;
    if ((localPoint3 != null) && (localPoint1 != null))
    {
      this.eyesCenterPoint = new Point(0.5F * localPoint3.x + 0.5F * localPoint1.x, 0.5F * localPoint3.y + 0.5F * localPoint1.y);
      this.eyesDistance = ((float)Math.hypot(localPoint1.x - localPoint3.x, localPoint1.y - localPoint3.y));
      this.angle = ((float)Math.toDegrees(3.141592653589793D + Math.atan2(localPoint1.y - localPoint3.y, localPoint1.x - localPoint3.x)));
      this.width = (this.eyesDistance * 2.35F);
      f1 = 0.8F * this.eyesDistance;
      f2 = (float)Math.toRadians(this.angle - 90.0F);
      this.foreheadPoint = new Point(this.eyesCenterPoint.x + (float)Math.cos(f2) * f1, this.eyesCenterPoint.y + (float)Math.sin(f2) * f1);
    }
    if ((localPoint2 != null) && (paramFace != null))
    {
      this.mouthPoint = new Point(0.5F * localPoint2.x + 0.5F * paramFace.x, 0.5F * localPoint2.y + 0.5F * paramFace.y);
      f1 = 0.7F * this.eyesDistance;
      f2 = (float)Math.toRadians(this.angle + 90.0F);
      this.chinPoint = new Point(this.mouthPoint.x + (float)Math.cos(f2) * f1, this.mouthPoint.y + (float)Math.sin(f2) * f1);
    }
  }
  
  private Point transposePoint(PointF paramPointF, Bitmap paramBitmap, Size paramSize, boolean paramBoolean)
  {
    float f1;
    if (paramBoolean)
    {
      f1 = paramBitmap.getHeight();
      if (!paramBoolean) {
        break label66;
      }
    }
    label66:
    for (float f2 = paramBitmap.getWidth();; f2 = paramBitmap.getHeight())
    {
      return new Point(paramSize.width * paramPointF.x / f1, paramSize.height * paramPointF.y / f2);
      f1 = paramBitmap.getWidth();
      break;
    }
  }
  
  public float getAngle()
  {
    return this.angle;
  }
  
  public Point getPointForAnchor(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return null;
    case 0: 
      return this.foreheadPoint;
    case 1: 
      return this.eyesCenterPoint;
    case 2: 
      return this.mouthPoint;
    }
    return this.chinPoint;
  }
  
  public float getWidthForAnchor(int paramInt)
  {
    if (paramInt == 1) {
      return this.eyesDistance;
    }
    return this.width;
  }
  
  public boolean isSufficient()
  {
    return this.eyesCenterPoint != null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/PhotoFace.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
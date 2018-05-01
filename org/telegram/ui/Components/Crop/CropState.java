package org.telegram.ui.Components.Crop;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public class CropState
{
  private float height;
  private Matrix matrix;
  private float minimumScale;
  private float rotation;
  private float scale;
  private float[] values;
  private float width;
  private float x;
  private float y;
  
  public CropState(Bitmap paramBitmap)
  {
    this.width = paramBitmap.getWidth();
    this.height = paramBitmap.getHeight();
    this.x = 0.0F;
    this.y = 0.0F;
    this.scale = 1.0F;
    this.rotation = 0.0F;
    this.matrix = new Matrix();
    this.values = new float[9];
  }
  
  private void updateValues()
  {
    this.matrix.getValues(this.values);
  }
  
  public void getConcatMatrix(Matrix paramMatrix)
  {
    paramMatrix.postConcat(this.matrix);
  }
  
  public float getHeight()
  {
    return this.height;
  }
  
  public Matrix getMatrix()
  {
    Matrix localMatrix = new Matrix();
    localMatrix.set(this.matrix);
    return localMatrix;
  }
  
  public float getRotation()
  {
    return this.rotation;
  }
  
  public float getScale()
  {
    return this.scale;
  }
  
  public float getWidth()
  {
    return this.width;
  }
  
  public float getX()
  {
    updateValues();
    float[] arrayOfFloat = this.values;
    Matrix localMatrix = this.matrix;
    return arrayOfFloat[2];
  }
  
  public float getY()
  {
    updateValues();
    float[] arrayOfFloat = this.values;
    Matrix localMatrix = this.matrix;
    return arrayOfFloat[5];
  }
  
  public void reset(CropAreaView paramCropAreaView)
  {
    this.matrix.reset();
    this.x = 0.0F;
    this.y = 0.0F;
    this.rotation = 0.0F;
    this.minimumScale = (paramCropAreaView.getCropWidth() / this.width);
    this.scale = this.minimumScale;
    this.matrix.postScale(this.scale, this.scale);
  }
  
  public void rotate(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.rotation += paramFloat1;
    this.matrix.postRotate(paramFloat1, paramFloat2, paramFloat3);
  }
  
  public void scale(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.scale *= paramFloat1;
    this.matrix.postScale(paramFloat1, paramFloat1, paramFloat2, paramFloat3);
  }
  
  public void translate(float paramFloat1, float paramFloat2)
  {
    this.x += paramFloat1;
    this.y += paramFloat2;
    this.matrix.postTranslate(paramFloat1, paramFloat2);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Crop/CropState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
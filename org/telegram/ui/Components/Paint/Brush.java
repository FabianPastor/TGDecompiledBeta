package org.telegram.ui.Components.Paint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import org.telegram.messenger.ApplicationLoader;

public abstract interface Brush
{
  public abstract float getAlpha();
  
  public abstract float getAngle();
  
  public abstract float getScale();
  
  public abstract float getSpacing();
  
  public abstract Bitmap getStamp();
  
  public abstract boolean isLightSaber();
  
  public static class Elliptical
    implements Brush
  {
    public float getAlpha()
    {
      return 0.3F;
    }
    
    public float getAngle()
    {
      return (float)Math.toRadians(125.0D);
    }
    
    public float getScale()
    {
      return 1.5F;
    }
    
    public float getSpacing()
    {
      return 0.04F;
    }
    
    public Bitmap getStamp()
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inScaled = false;
      return BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), 2130837864, localOptions);
    }
    
    public boolean isLightSaber()
    {
      return false;
    }
  }
  
  public static class Neon
    implements Brush
  {
    public float getAlpha()
    {
      return 0.7F;
    }
    
    public float getAngle()
    {
      return 0.0F;
    }
    
    public float getScale()
    {
      return 1.45F;
    }
    
    public float getSpacing()
    {
      return 0.07F;
    }
    
    public Bitmap getStamp()
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inScaled = false;
      return BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), 2130837866, localOptions);
    }
    
    public boolean isLightSaber()
    {
      return true;
    }
  }
  
  public static class Radial
    implements Brush
  {
    public float getAlpha()
    {
      return 0.85F;
    }
    
    public float getAngle()
    {
      return 0.0F;
    }
    
    public float getScale()
    {
      return 1.0F;
    }
    
    public float getSpacing()
    {
      return 0.15F;
    }
    
    public Bitmap getStamp()
    {
      BitmapFactory.Options localOptions = new BitmapFactory.Options();
      localOptions.inScaled = false;
      return BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), 2130837868, localOptions);
    }
    
    public boolean isLightSaber()
    {
      return false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/Paint/Brush.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package android.support.v7.graphics;

public final class Target
{
  public static final Target DARK_MUTED;
  public static final Target DARK_VIBRANT;
  public static final Target LIGHT_MUTED;
  public static final Target LIGHT_VIBRANT = new Target();
  public static final Target MUTED;
  public static final Target VIBRANT;
  boolean mIsExclusive = true;
  final float[] mLightnessTargets = new float[3];
  final float[] mSaturationTargets = new float[3];
  final float[] mWeights = new float[3];
  
  static
  {
    setDefaultLightLightnessValues(LIGHT_VIBRANT);
    setDefaultVibrantSaturationValues(LIGHT_VIBRANT);
    VIBRANT = new Target();
    setDefaultNormalLightnessValues(VIBRANT);
    setDefaultVibrantSaturationValues(VIBRANT);
    DARK_VIBRANT = new Target();
    setDefaultDarkLightnessValues(DARK_VIBRANT);
    setDefaultVibrantSaturationValues(DARK_VIBRANT);
    LIGHT_MUTED = new Target();
    setDefaultLightLightnessValues(LIGHT_MUTED);
    setDefaultMutedSaturationValues(LIGHT_MUTED);
    MUTED = new Target();
    setDefaultNormalLightnessValues(MUTED);
    setDefaultMutedSaturationValues(MUTED);
    DARK_MUTED = new Target();
    setDefaultDarkLightnessValues(DARK_MUTED);
    setDefaultMutedSaturationValues(DARK_MUTED);
  }
  
  Target()
  {
    setTargetDefaultValues(this.mSaturationTargets);
    setTargetDefaultValues(this.mLightnessTargets);
    setDefaultWeights();
  }
  
  private static void setDefaultDarkLightnessValues(Target paramTarget)
  {
    paramTarget.mLightnessTargets[1] = 0.26F;
    paramTarget.mLightnessTargets[2] = 0.45F;
  }
  
  private static void setDefaultLightLightnessValues(Target paramTarget)
  {
    paramTarget.mLightnessTargets[0] = 0.55F;
    paramTarget.mLightnessTargets[1] = 0.74F;
  }
  
  private static void setDefaultMutedSaturationValues(Target paramTarget)
  {
    paramTarget.mSaturationTargets[1] = 0.3F;
    paramTarget.mSaturationTargets[2] = 0.4F;
  }
  
  private static void setDefaultNormalLightnessValues(Target paramTarget)
  {
    paramTarget.mLightnessTargets[0] = 0.3F;
    paramTarget.mLightnessTargets[1] = 0.5F;
    paramTarget.mLightnessTargets[2] = 0.7F;
  }
  
  private static void setDefaultVibrantSaturationValues(Target paramTarget)
  {
    paramTarget.mSaturationTargets[0] = 0.35F;
    paramTarget.mSaturationTargets[1] = 1.0F;
  }
  
  private void setDefaultWeights()
  {
    this.mWeights[0] = 0.24F;
    this.mWeights[1] = 0.52F;
    this.mWeights[2] = 0.24F;
  }
  
  private static void setTargetDefaultValues(float[] paramArrayOfFloat)
  {
    paramArrayOfFloat[0] = 0.0F;
    paramArrayOfFloat[1] = 0.5F;
    paramArrayOfFloat[2] = 1.0F;
  }
  
  public float getLightnessWeight()
  {
    return this.mWeights[1];
  }
  
  public float getMaximumLightness()
  {
    return this.mLightnessTargets[2];
  }
  
  public float getMaximumSaturation()
  {
    return this.mSaturationTargets[2];
  }
  
  public float getMinimumLightness()
  {
    return this.mLightnessTargets[0];
  }
  
  public float getMinimumSaturation()
  {
    return this.mSaturationTargets[0];
  }
  
  public float getPopulationWeight()
  {
    return this.mWeights[2];
  }
  
  public float getSaturationWeight()
  {
    return this.mWeights[0];
  }
  
  public float getTargetLightness()
  {
    return this.mLightnessTargets[1];
  }
  
  public float getTargetSaturation()
  {
    return this.mSaturationTargets[1];
  }
  
  public boolean isExclusive()
  {
    return this.mIsExclusive;
  }
  
  void normalizeWeights()
  {
    float f1 = 0.0F;
    int i = 0;
    int j = this.mWeights.length;
    while (i < j)
    {
      float f2 = this.mWeights[i];
      float f3 = f1;
      if (f2 > 0.0F) {
        f3 = f1 + f2;
      }
      i++;
      f1 = f3;
    }
    if (f1 != 0.0F)
    {
      i = 0;
      j = this.mWeights.length;
      while (i < j)
      {
        if (this.mWeights[i] > 0.0F)
        {
          float[] arrayOfFloat = this.mWeights;
          arrayOfFloat[i] /= f1;
        }
        i++;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v7/graphics/Target.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
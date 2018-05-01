package android.support.v7.graphics;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.util.ArrayMap;
import android.util.SparseBooleanArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Palette
{
  static final Filter DEFAULT_FILTER = new Filter()
  {
    private boolean isBlack(float[] paramAnonymousArrayOfFloat)
    {
      if (paramAnonymousArrayOfFloat[2] <= 0.05F) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    private boolean isNearRedILine(float[] paramAnonymousArrayOfFloat)
    {
      boolean bool = true;
      if ((paramAnonymousArrayOfFloat[0] >= 10.0F) && (paramAnonymousArrayOfFloat[0] <= 37.0F) && (paramAnonymousArrayOfFloat[1] <= 0.82F)) {}
      for (;;)
      {
        return bool;
        bool = false;
      }
    }
    
    private boolean isWhite(float[] paramAnonymousArrayOfFloat)
    {
      if (paramAnonymousArrayOfFloat[2] >= 0.95F) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean isAllowed(int paramAnonymousInt, float[] paramAnonymousArrayOfFloat)
    {
      if ((!isWhite(paramAnonymousArrayOfFloat)) && (!isBlack(paramAnonymousArrayOfFloat)) && (!isNearRedILine(paramAnonymousArrayOfFloat))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
  };
  private final Swatch mDominantSwatch;
  private final Map<Target, Swatch> mSelectedSwatches;
  private final List<Swatch> mSwatches;
  private final List<Target> mTargets;
  private final SparseBooleanArray mUsedColors;
  
  Palette(List<Swatch> paramList, List<Target> paramList1)
  {
    this.mSwatches = paramList;
    this.mTargets = paramList1;
    this.mUsedColors = new SparseBooleanArray();
    this.mSelectedSwatches = new ArrayMap();
    this.mDominantSwatch = findDominantSwatch();
  }
  
  private Swatch findDominantSwatch()
  {
    int i = Integer.MIN_VALUE;
    Object localObject = null;
    int j = 0;
    int k = this.mSwatches.size();
    while (j < k)
    {
      Swatch localSwatch = (Swatch)this.mSwatches.get(j);
      int m = i;
      if (localSwatch.getPopulation() > i)
      {
        localObject = localSwatch;
        m = localSwatch.getPopulation();
      }
      j++;
      i = m;
    }
    return (Swatch)localObject;
  }
  
  public static Builder from(Bitmap paramBitmap)
  {
    return new Builder(paramBitmap);
  }
  
  private float generateScore(Swatch paramSwatch, Target paramTarget)
  {
    float[] arrayOfFloat = paramSwatch.getHsl();
    float f1 = 0.0F;
    float f2 = 0.0F;
    float f3 = 0.0F;
    if (this.mDominantSwatch != null) {}
    for (int i = this.mDominantSwatch.getPopulation();; i = 1)
    {
      if (paramTarget.getSaturationWeight() > 0.0F) {
        f1 = paramTarget.getSaturationWeight() * (1.0F - Math.abs(arrayOfFloat[1] - paramTarget.getTargetSaturation()));
      }
      if (paramTarget.getLightnessWeight() > 0.0F) {
        f2 = paramTarget.getLightnessWeight() * (1.0F - Math.abs(arrayOfFloat[2] - paramTarget.getTargetLightness()));
      }
      if (paramTarget.getPopulationWeight() > 0.0F) {
        f3 = paramTarget.getPopulationWeight() * (paramSwatch.getPopulation() / i);
      }
      return f1 + f2 + f3;
    }
  }
  
  private Swatch generateScoredTarget(Target paramTarget)
  {
    Swatch localSwatch = getMaxScoredSwatchForTarget(paramTarget);
    if ((localSwatch != null) && (paramTarget.isExclusive())) {
      this.mUsedColors.append(localSwatch.getRgb(), true);
    }
    return localSwatch;
  }
  
  private Swatch getMaxScoredSwatchForTarget(Target paramTarget)
  {
    float f1 = 0.0F;
    Object localObject1 = null;
    int i = 0;
    int j = this.mSwatches.size();
    while (i < j)
    {
      Swatch localSwatch = (Swatch)this.mSwatches.get(i);
      float f2 = f1;
      Object localObject2 = localObject1;
      if (shouldBeScoredForTarget(localSwatch, paramTarget))
      {
        float f3 = generateScore(localSwatch, paramTarget);
        if (localObject1 != null)
        {
          f2 = f1;
          localObject2 = localObject1;
          if (f3 <= f1) {}
        }
        else
        {
          localObject2 = localSwatch;
          f2 = f3;
        }
      }
      i++;
      f1 = f2;
      localObject1 = localObject2;
    }
    return (Swatch)localObject1;
  }
  
  private boolean shouldBeScoredForTarget(Swatch paramSwatch, Target paramTarget)
  {
    boolean bool = true;
    float[] arrayOfFloat = paramSwatch.getHsl();
    if ((arrayOfFloat[1] >= paramTarget.getMinimumSaturation()) && (arrayOfFloat[1] <= paramTarget.getMaximumSaturation()) && (arrayOfFloat[2] >= paramTarget.getMinimumLightness()) && (arrayOfFloat[2] <= paramTarget.getMaximumLightness()) && (!this.mUsedColors.get(paramSwatch.getRgb()))) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
  
  void generate()
  {
    int i = 0;
    int j = this.mTargets.size();
    while (i < j)
    {
      Target localTarget = (Target)this.mTargets.get(i);
      localTarget.normalizeWeights();
      this.mSelectedSwatches.put(localTarget, generateScoredTarget(localTarget));
      i++;
    }
    this.mUsedColors.clear();
  }
  
  public int getColorForTarget(Target paramTarget, int paramInt)
  {
    paramTarget = getSwatchForTarget(paramTarget);
    if (paramTarget != null) {
      paramInt = paramTarget.getRgb();
    }
    return paramInt;
  }
  
  public int getDarkMutedColor(int paramInt)
  {
    return getColorForTarget(Target.DARK_MUTED, paramInt);
  }
  
  public Swatch getSwatchForTarget(Target paramTarget)
  {
    return (Swatch)this.mSelectedSwatches.get(paramTarget);
  }
  
  public static final class Builder
  {
    private final Bitmap mBitmap;
    private final List<Palette.Filter> mFilters = new ArrayList();
    private int mMaxColors = 16;
    private Rect mRegion;
    private int mResizeArea = 12544;
    private int mResizeMaxDimension = -1;
    private final List<Palette.Swatch> mSwatches;
    private final List<Target> mTargets = new ArrayList();
    
    public Builder(Bitmap paramBitmap)
    {
      if ((paramBitmap == null) || (paramBitmap.isRecycled())) {
        throw new IllegalArgumentException("Bitmap is not valid");
      }
      this.mFilters.add(Palette.DEFAULT_FILTER);
      this.mBitmap = paramBitmap;
      this.mSwatches = null;
      this.mTargets.add(Target.LIGHT_VIBRANT);
      this.mTargets.add(Target.VIBRANT);
      this.mTargets.add(Target.DARK_VIBRANT);
      this.mTargets.add(Target.LIGHT_MUTED);
      this.mTargets.add(Target.MUTED);
      this.mTargets.add(Target.DARK_MUTED);
    }
    
    private int[] getPixelsFromBitmap(Bitmap paramBitmap)
    {
      int i = paramBitmap.getWidth();
      int j = paramBitmap.getHeight();
      int[] arrayOfInt = new int[i * j];
      paramBitmap.getPixels(arrayOfInt, 0, i, 0, 0, i, j);
      if (this.mRegion == null) {
        paramBitmap = arrayOfInt;
      }
      for (;;)
      {
        return paramBitmap;
        int k = this.mRegion.width();
        int m = this.mRegion.height();
        paramBitmap = new int[k * m];
        for (j = 0; j < m; j++) {
          System.arraycopy(arrayOfInt, (this.mRegion.top + j) * i + this.mRegion.left, paramBitmap, j * k, k);
        }
      }
    }
    
    private Bitmap scaleBitmapDown(Bitmap paramBitmap)
    {
      double d1 = -1.0D;
      int i;
      double d2;
      if (this.mResizeArea > 0)
      {
        i = paramBitmap.getWidth() * paramBitmap.getHeight();
        d2 = d1;
        if (i > this.mResizeArea) {
          d2 = Math.sqrt(this.mResizeArea / i);
        }
        if (d2 > 0.0D) {
          break label106;
        }
      }
      for (;;)
      {
        return paramBitmap;
        d2 = d1;
        if (this.mResizeMaxDimension <= 0) {
          break;
        }
        i = Math.max(paramBitmap.getWidth(), paramBitmap.getHeight());
        d2 = d1;
        if (i <= this.mResizeMaxDimension) {
          break;
        }
        d2 = this.mResizeMaxDimension / i;
        break;
        label106:
        paramBitmap = Bitmap.createScaledBitmap(paramBitmap, (int)Math.ceil(paramBitmap.getWidth() * d2), (int)Math.ceil(paramBitmap.getHeight() * d2), false);
      }
    }
    
    public Palette generate()
    {
      Object localObject;
      if (this.mBitmap != null)
      {
        Bitmap localBitmap = scaleBitmapDown(this.mBitmap);
        if (0 != 0) {
          throw new NullPointerException();
        }
        localObject = this.mRegion;
        if ((localBitmap != this.mBitmap) && (localObject != null))
        {
          double d = localBitmap.getWidth() / this.mBitmap.getWidth();
          ((Rect)localObject).left = ((int)Math.floor(((Rect)localObject).left * d));
          ((Rect)localObject).top = ((int)Math.floor(((Rect)localObject).top * d));
          ((Rect)localObject).right = Math.min((int)Math.ceil(((Rect)localObject).right * d), localBitmap.getWidth());
          ((Rect)localObject).bottom = Math.min((int)Math.ceil(((Rect)localObject).bottom * d), localBitmap.getHeight());
        }
        int[] arrayOfInt = getPixelsFromBitmap(localBitmap);
        int i = this.mMaxColors;
        if (this.mFilters.isEmpty())
        {
          localObject = null;
          localObject = new ColorCutQuantizer(arrayOfInt, i, (Palette.Filter[])localObject);
          if (localBitmap != this.mBitmap) {
            localBitmap.recycle();
          }
          localObject = ((ColorCutQuantizer)localObject).getQuantizedColors();
          if (0 != 0) {
            throw new NullPointerException();
          }
        }
      }
      for (;;)
      {
        localObject = new Palette((List)localObject, this.mTargets);
        ((Palette)localObject).generate();
        if (0 != 0) {
          throw new NullPointerException();
        }
        return (Palette)localObject;
        localObject = (Palette.Filter[])this.mFilters.toArray(new Palette.Filter[this.mFilters.size()]);
        break;
        localObject = this.mSwatches;
      }
    }
  }
  
  public static abstract interface Filter
  {
    public abstract boolean isAllowed(int paramInt, float[] paramArrayOfFloat);
  }
  
  public static final class Swatch
  {
    private final int mBlue;
    private int mBodyTextColor;
    private boolean mGeneratedTextColors;
    private final int mGreen;
    private float[] mHsl;
    private final int mPopulation;
    private final int mRed;
    private final int mRgb;
    private int mTitleTextColor;
    
    public Swatch(int paramInt1, int paramInt2)
    {
      this.mRed = Color.red(paramInt1);
      this.mGreen = Color.green(paramInt1);
      this.mBlue = Color.blue(paramInt1);
      this.mRgb = paramInt1;
      this.mPopulation = paramInt2;
    }
    
    private void ensureTextColorsGenerated()
    {
      int i;
      int j;
      if (!this.mGeneratedTextColors)
      {
        i = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, 4.5F);
        j = ColorUtils.calculateMinimumAlpha(-1, this.mRgb, 3.0F);
        if ((i == -1) || (j == -1)) {
          break label63;
        }
        this.mBodyTextColor = ColorUtils.setAlphaComponent(-1, i);
        this.mTitleTextColor = ColorUtils.setAlphaComponent(-1, j);
      }
      label63:
      int m;
      for (this.mGeneratedTextColors = true;; this.mGeneratedTextColors = true)
      {
        return;
        k = ColorUtils.calculateMinimumAlpha(-16777216, this.mRgb, 4.5F);
        m = ColorUtils.calculateMinimumAlpha(-16777216, this.mRgb, 3.0F);
        if ((k == -1) || (m == -1)) {
          break;
        }
        this.mBodyTextColor = ColorUtils.setAlphaComponent(-16777216, k);
        this.mTitleTextColor = ColorUtils.setAlphaComponent(-16777216, m);
      }
      if (i != -1)
      {
        k = ColorUtils.setAlphaComponent(-1, i);
        label139:
        this.mBodyTextColor = k;
        if (j == -1) {
          break label178;
        }
      }
      label178:
      for (int k = ColorUtils.setAlphaComponent(-1, j);; k = ColorUtils.setAlphaComponent(-16777216, m))
      {
        this.mTitleTextColor = k;
        this.mGeneratedTextColors = true;
        break;
        k = ColorUtils.setAlphaComponent(-16777216, k);
        break label139;
      }
    }
    
    public boolean equals(Object paramObject)
    {
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (getClass() != paramObject.getClass()))
        {
          bool = false;
        }
        else
        {
          paramObject = (Swatch)paramObject;
          if ((this.mPopulation != ((Swatch)paramObject).mPopulation) || (this.mRgb != ((Swatch)paramObject).mRgb)) {
            bool = false;
          }
        }
      }
    }
    
    public int getBodyTextColor()
    {
      ensureTextColorsGenerated();
      return this.mBodyTextColor;
    }
    
    public float[] getHsl()
    {
      if (this.mHsl == null) {
        this.mHsl = new float[3];
      }
      ColorUtils.RGBToHSL(this.mRed, this.mGreen, this.mBlue, this.mHsl);
      return this.mHsl;
    }
    
    public int getPopulation()
    {
      return this.mPopulation;
    }
    
    public int getRgb()
    {
      return this.mRgb;
    }
    
    public int getTitleTextColor()
    {
      ensureTextColorsGenerated();
      return this.mTitleTextColor;
    }
    
    public int hashCode()
    {
      return this.mRgb * 31 + this.mPopulation;
    }
    
    public String toString()
    {
      return getClass().getSimpleName() + " [RGB: #" + Integer.toHexString(getRgb()) + ']' + " [HSL: " + Arrays.toString(getHsl()) + ']' + " [Population: " + this.mPopulation + ']' + " [Title Text: #" + Integer.toHexString(getTitleTextColor()) + ']' + " [Body Text: #" + Integer.toHexString(getBodyTextColor()) + ']';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v7/graphics/Palette.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package android.support.v7.graphics;

import android.graphics.Color;
import android.support.v4.graphics.ColorUtils;
import android.util.TimingLogger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

final class ColorCutQuantizer
{
  private static final Comparator<Vbox> VBOX_COMPARATOR_VOLUME = new Comparator()
  {
    public int compare(ColorCutQuantizer.Vbox paramAnonymousVbox1, ColorCutQuantizer.Vbox paramAnonymousVbox2)
    {
      return paramAnonymousVbox2.getVolume() - paramAnonymousVbox1.getVolume();
    }
  };
  final int[] mColors;
  final Palette.Filter[] mFilters;
  final int[] mHistogram;
  final List<Palette.Swatch> mQuantizedColors;
  private final float[] mTempHsl = new float[3];
  final TimingLogger mTimingLogger = null;
  
  ColorCutQuantizer(int[] paramArrayOfInt, int paramInt, Palette.Filter[] paramArrayOfFilter)
  {
    this.mFilters = paramArrayOfFilter;
    paramArrayOfFilter = new int[32768];
    this.mHistogram = paramArrayOfFilter;
    for (int i = 0; i < paramArrayOfInt.length; i++)
    {
      j = quantizeFromRgb888(paramArrayOfInt[i]);
      paramArrayOfInt[i] = j;
      paramArrayOfFilter[j] += 1;
    }
    i = 0;
    int j = 0;
    while (j < paramArrayOfFilter.length)
    {
      if ((paramArrayOfFilter[j] > 0) && (shouldIgnoreColor(j))) {
        paramArrayOfFilter[j] = 0;
      }
      k = i;
      if (paramArrayOfFilter[j] > 0) {
        k = i + 1;
      }
      j++;
      i = k;
    }
    paramArrayOfInt = new int[i];
    this.mColors = paramArrayOfInt;
    int k = 0;
    int m = 0;
    while (m < paramArrayOfFilter.length)
    {
      j = k;
      if (paramArrayOfFilter[m] > 0)
      {
        paramArrayOfInt[k] = m;
        j = k + 1;
      }
      m++;
      k = j;
    }
    if (i <= paramInt)
    {
      this.mQuantizedColors = new ArrayList();
      i = paramArrayOfInt.length;
      for (paramInt = 0; paramInt < i; paramInt++)
      {
        j = paramArrayOfInt[paramInt];
        this.mQuantizedColors.add(new Palette.Swatch(approximateToRgb888(j), paramArrayOfFilter[j]));
      }
    }
    this.mQuantizedColors = quantizePixels(paramInt);
  }
  
  private static int approximateToRgb888(int paramInt)
  {
    return approximateToRgb888(quantizedRed(paramInt), quantizedGreen(paramInt), quantizedBlue(paramInt));
  }
  
  static int approximateToRgb888(int paramInt1, int paramInt2, int paramInt3)
  {
    return Color.rgb(modifyWordWidth(paramInt1, 5, 8), modifyWordWidth(paramInt2, 5, 8), modifyWordWidth(paramInt3, 5, 8));
  }
  
  private List<Palette.Swatch> generateAverageColors(Collection<Vbox> paramCollection)
  {
    ArrayList localArrayList = new ArrayList(paramCollection.size());
    Iterator localIterator = paramCollection.iterator();
    while (localIterator.hasNext())
    {
      paramCollection = ((Vbox)localIterator.next()).getAverageColor();
      if (!shouldIgnoreColor(paramCollection)) {
        localArrayList.add(paramCollection);
      }
    }
    return localArrayList;
  }
  
  static void modifySignificantOctet(int[] paramArrayOfInt, int paramInt1, int paramInt2, int paramInt3)
  {
    switch (paramInt1)
    {
    }
    for (;;)
    {
      return;
      for (paramInt1 = paramInt2; paramInt1 <= paramInt3; paramInt1++)
      {
        paramInt2 = paramArrayOfInt[paramInt1];
        paramArrayOfInt[paramInt1] = (quantizedGreen(paramInt2) << 10 | quantizedRed(paramInt2) << 5 | quantizedBlue(paramInt2));
      }
      continue;
      for (paramInt1 = paramInt2; paramInt1 <= paramInt3; paramInt1++)
      {
        paramInt2 = paramArrayOfInt[paramInt1];
        paramArrayOfInt[paramInt1] = (quantizedBlue(paramInt2) << 10 | quantizedGreen(paramInt2) << 5 | quantizedRed(paramInt2));
      }
    }
  }
  
  private static int modifyWordWidth(int paramInt1, int paramInt2, int paramInt3)
  {
    if (paramInt3 > paramInt2) {
      paramInt1 <<= paramInt3 - paramInt2;
    }
    for (;;)
    {
      return (1 << paramInt3) - 1 & paramInt1;
      paramInt1 >>= paramInt2 - paramInt3;
    }
  }
  
  private static int quantizeFromRgb888(int paramInt)
  {
    return modifyWordWidth(Color.red(paramInt), 8, 5) << 10 | modifyWordWidth(Color.green(paramInt), 8, 5) << 5 | modifyWordWidth(Color.blue(paramInt), 8, 5);
  }
  
  private List<Palette.Swatch> quantizePixels(int paramInt)
  {
    PriorityQueue localPriorityQueue = new PriorityQueue(paramInt, VBOX_COMPARATOR_VOLUME);
    localPriorityQueue.offer(new Vbox(0, this.mColors.length - 1));
    splitBoxes(localPriorityQueue, paramInt);
    return generateAverageColors(localPriorityQueue);
  }
  
  static int quantizedBlue(int paramInt)
  {
    return paramInt & 0x1F;
  }
  
  static int quantizedGreen(int paramInt)
  {
    return paramInt >> 5 & 0x1F;
  }
  
  static int quantizedRed(int paramInt)
  {
    return paramInt >> 10 & 0x1F;
  }
  
  private boolean shouldIgnoreColor(int paramInt)
  {
    paramInt = approximateToRgb888(paramInt);
    ColorUtils.colorToHSL(paramInt, this.mTempHsl);
    return shouldIgnoreColor(paramInt, this.mTempHsl);
  }
  
  private boolean shouldIgnoreColor(int paramInt, float[] paramArrayOfFloat)
  {
    int i;
    if ((this.mFilters != null) && (this.mFilters.length > 0))
    {
      i = 0;
      int j = this.mFilters.length;
      if (i < j) {
        if (this.mFilters[i].isAllowed(paramInt, paramArrayOfFloat)) {}
      }
    }
    for (boolean bool = true;; bool = false)
    {
      return bool;
      i++;
      break;
    }
  }
  
  private boolean shouldIgnoreColor(Palette.Swatch paramSwatch)
  {
    return shouldIgnoreColor(paramSwatch.getRgb(), paramSwatch.getHsl());
  }
  
  private void splitBoxes(PriorityQueue<Vbox> paramPriorityQueue, int paramInt)
  {
    while (paramPriorityQueue.size() < paramInt)
    {
      Vbox localVbox = (Vbox)paramPriorityQueue.poll();
      if ((localVbox == null) || (!localVbox.canSplit())) {
        break;
      }
      paramPriorityQueue.offer(localVbox.splitBox());
      paramPriorityQueue.offer(localVbox);
    }
  }
  
  List<Palette.Swatch> getQuantizedColors()
  {
    return this.mQuantizedColors;
  }
  
  private class Vbox
  {
    private int mLowerIndex;
    private int mMaxBlue;
    private int mMaxGreen;
    private int mMaxRed;
    private int mMinBlue;
    private int mMinGreen;
    private int mMinRed;
    private int mPopulation;
    private int mUpperIndex;
    
    Vbox(int paramInt1, int paramInt2)
    {
      this.mLowerIndex = paramInt1;
      this.mUpperIndex = paramInt2;
      fitBox();
    }
    
    final boolean canSplit()
    {
      boolean bool = true;
      if (getColorCount() > 1) {}
      for (;;)
      {
        return bool;
        bool = false;
      }
    }
    
    final int findSplitPoint()
    {
      int i = getLongestColorDimension();
      int[] arrayOfInt1 = ColorCutQuantizer.this.mColors;
      int[] arrayOfInt2 = ColorCutQuantizer.this.mHistogram;
      ColorCutQuantizer.modifySignificantOctet(arrayOfInt1, i, this.mLowerIndex, this.mUpperIndex);
      Arrays.sort(arrayOfInt1, this.mLowerIndex, this.mUpperIndex + 1);
      ColorCutQuantizer.modifySignificantOctet(arrayOfInt1, i, this.mLowerIndex, this.mUpperIndex);
      int j = this.mPopulation / 2;
      i = this.mLowerIndex;
      int k = 0;
      if (i <= this.mUpperIndex)
      {
        k += arrayOfInt2[arrayOfInt1[i]];
        if (k < j) {}
      }
      for (i = Math.min(this.mUpperIndex - 1, i);; i = this.mLowerIndex)
      {
        return i;
        i++;
        break;
      }
    }
    
    final void fitBox()
    {
      int[] arrayOfInt1 = ColorCutQuantizer.this.mColors;
      int[] arrayOfInt2 = ColorCutQuantizer.this.mHistogram;
      int i = Integer.MAX_VALUE;
      int j = Integer.MAX_VALUE;
      int k = Integer.MAX_VALUE;
      int m = Integer.MIN_VALUE;
      int n = Integer.MIN_VALUE;
      int i1 = Integer.MIN_VALUE;
      int i2 = 0;
      int i3 = this.mLowerIndex;
      while (i3 <= this.mUpperIndex)
      {
        int i4 = arrayOfInt1[i3];
        int i5 = i2 + arrayOfInt2[i4];
        int i6 = ColorCutQuantizer.quantizedRed(i4);
        int i7 = ColorCutQuantizer.quantizedGreen(i4);
        i2 = ColorCutQuantizer.quantizedBlue(i4);
        i4 = i1;
        if (i6 > i1) {
          i4 = i6;
        }
        int i8 = k;
        if (i6 < k) {
          i8 = i6;
        }
        k = n;
        if (i7 > n) {
          k = i7;
        }
        i6 = j;
        if (i7 < j) {
          i6 = i7;
        }
        n = m;
        if (i2 > m) {
          n = i2;
        }
        j = i;
        if (i2 < i) {
          j = i2;
        }
        i3++;
        i2 = i5;
        m = n;
        n = k;
        i1 = i4;
        i = j;
        j = i6;
        k = i8;
      }
      this.mMinRed = k;
      this.mMaxRed = i1;
      this.mMinGreen = j;
      this.mMaxGreen = n;
      this.mMinBlue = i;
      this.mMaxBlue = m;
      this.mPopulation = i2;
    }
    
    final Palette.Swatch getAverageColor()
    {
      int[] arrayOfInt1 = ColorCutQuantizer.this.mColors;
      int[] arrayOfInt2 = ColorCutQuantizer.this.mHistogram;
      int i = 0;
      int j = 0;
      int k = 0;
      int m = 0;
      for (int n = this.mLowerIndex; n <= this.mUpperIndex; n++)
      {
        int i1 = arrayOfInt1[n];
        int i2 = arrayOfInt2[i1];
        m += i2;
        i += ColorCutQuantizer.quantizedRed(i1) * i2;
        j += ColorCutQuantizer.quantizedGreen(i1) * i2;
        k += ColorCutQuantizer.quantizedBlue(i1) * i2;
      }
      return new Palette.Swatch(ColorCutQuantizer.approximateToRgb888(Math.round(i / m), Math.round(j / m), Math.round(k / m)), m);
    }
    
    final int getColorCount()
    {
      return this.mUpperIndex + 1 - this.mLowerIndex;
    }
    
    final int getLongestColorDimension()
    {
      int i = this.mMaxRed - this.mMinRed;
      int j = this.mMaxGreen - this.mMinGreen;
      int k = this.mMaxBlue - this.mMinBlue;
      if ((i >= j) && (i >= k)) {
        i = -3;
      }
      for (;;)
      {
        return i;
        if ((j >= i) && (j >= k)) {
          i = -2;
        } else {
          i = -1;
        }
      }
    }
    
    final int getVolume()
    {
      return (this.mMaxRed - this.mMinRed + 1) * (this.mMaxGreen - this.mMinGreen + 1) * (this.mMaxBlue - this.mMinBlue + 1);
    }
    
    final Vbox splitBox()
    {
      if (!canSplit()) {
        throw new IllegalStateException("Can not split a box with only 1 color");
      }
      int i = findSplitPoint();
      Vbox localVbox = new Vbox(ColorCutQuantizer.this, i + 1, this.mUpperIndex);
      this.mUpperIndex = i;
      fitBox();
      return localVbox;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/android/support/v7/graphics/ColorCutQuantizer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
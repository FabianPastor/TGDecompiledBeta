package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseArray;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.support.widget.GridLayoutManager;

public class ExtendedGridLayoutManager
  extends GridLayoutManager
{
  private int calculatedWidth;
  private SparseArray<Integer> itemSpans = new SparseArray();
  private SparseArray<Integer> itemsToRow = new SparseArray();
  private ArrayList<ArrayList<Integer>> rows;
  
  public ExtendedGridLayoutManager(Context paramContext, int paramInt)
  {
    super(paramContext, paramInt);
  }
  
  private void checkLayout()
  {
    if ((this.itemSpans.size() != getFlowItemCount()) || (this.calculatedWidth != getWidth()))
    {
      this.calculatedWidth = getWidth();
      prepareLayout(getWidth());
    }
  }
  
  private ArrayList<ArrayList<Integer>> getLinearPartitionForSequence(int[] paramArrayOfInt, int paramInt)
  {
    int j = paramArrayOfInt.length;
    if (paramInt <= 0) {
      return new ArrayList();
    }
    if ((paramInt >= j) || (j == 1))
    {
      localArrayList1 = new ArrayList(paramArrayOfInt.length);
      paramInt = 0;
      while (paramInt < paramArrayOfInt.length)
      {
        localObject = new ArrayList(1);
        ((ArrayList)localObject).add(Integer.valueOf(paramArrayOfInt[paramInt]));
        localArrayList1.add(localObject);
        paramInt += 1;
      }
      return localArrayList1;
    }
    Object localObject = getLinearPartitionTable(paramArrayOfInt, paramInt);
    int k = paramInt - 1;
    int i = paramInt - 2;
    paramInt = j - 1;
    ArrayList localArrayList1 = new ArrayList();
    if (i >= 0)
    {
      if (paramInt < 1) {
        localArrayList1.add(0, new ArrayList());
      }
      for (;;)
      {
        i -= 1;
        break;
        ArrayList localArrayList2 = new ArrayList();
        j = localObject[((paramInt - 1) * k + i)] + 1;
        while (j < paramInt + 1)
        {
          localArrayList2.add(Integer.valueOf(paramArrayOfInt[j]));
          j += 1;
        }
        localArrayList1.add(0, localArrayList2);
        paramInt = localObject[((paramInt - 1) * k + i)];
      }
    }
    localObject = new ArrayList();
    i = 0;
    while (i < paramInt + 1)
    {
      ((ArrayList)localObject).add(Integer.valueOf(paramArrayOfInt[i]));
      i += 1;
    }
    localArrayList1.add(0, localObject);
    return localArrayList1;
  }
  
  private int[] getLinearPartitionTable(int[] paramArrayOfInt, int paramInt)
  {
    int i3 = paramArrayOfInt.length;
    int[] arrayOfInt1 = new int[i3 * paramInt];
    int[] arrayOfInt2 = new int[(i3 - 1) * (paramInt - 1)];
    int i = 0;
    int k;
    if (i < i3)
    {
      k = paramArrayOfInt[i];
      if (i != 0) {}
      for (j = arrayOfInt1[((i - 1) * paramInt)];; j = 0)
      {
        arrayOfInt1[(i * paramInt)] = (j + k);
        i += 1;
        break;
      }
    }
    i = 0;
    while (i < paramInt)
    {
      arrayOfInt1[i] = paramArrayOfInt[0];
      i += 1;
    }
    int j = 1;
    while (j < i3)
    {
      k = 1;
      while (k < paramInt)
      {
        int m = 0;
        int n = Integer.MAX_VALUE;
        i = 0;
        while (i < j)
        {
          int i2 = Math.max(arrayOfInt1[(i * paramInt + (k - 1))], arrayOfInt1[(j * paramInt)] - arrayOfInt1[(i * paramInt)]);
          int i1;
          if (i != 0)
          {
            i1 = m;
            if (i2 >= m) {}
          }
          else
          {
            i1 = i2;
            n = i;
          }
          i += 1;
          m = i1;
        }
        arrayOfInt1[(j * paramInt + k)] = m;
        arrayOfInt2[((j - 1) * (paramInt - 1) + (k - 1))] = n;
        k += 1;
      }
      j += 1;
    }
    return arrayOfInt2;
  }
  
  private void prepareLayout(float paramFloat)
  {
    this.itemSpans.clear();
    this.itemsToRow.clear();
    int j = AndroidUtilities.dp(100.0F);
    float f1 = 0.0F;
    int i1 = getFlowItemCount();
    Object localObject = new int[i1];
    int i = 0;
    Size localSize;
    while (i < i1)
    {
      localSize = sizeForItem(i);
      f1 += localSize.width / localSize.height * j;
      localObject[i] = Math.round(localSize.width / localSize.height * 100.0F);
      i += 1;
    }
    this.rows = getLinearPartitionForSequence((int[])localObject, Math.max(Math.round(f1 / paramFloat), 1));
    i = 0;
    int k = 0;
    while (k < this.rows.size())
    {
      localObject = (ArrayList)this.rows.get(k);
      float f2 = 0.0F;
      j = i;
      int m = ((ArrayList)localObject).size();
      while (j < i + m)
      {
        localSize = sizeForItem(j);
        f2 += localSize.width / localSize.height;
        j += 1;
      }
      float f3 = paramFloat;
      f1 = f3;
      if (this.rows.size() == 1)
      {
        f1 = f3;
        if (k == this.rows.size() - 1)
        {
          if (((ArrayList)localObject).size() >= 2) {
            break label375;
          }
          f1 = (float)Math.floor(paramFloat / 3.0F);
        }
      }
      j = getSpanCount();
      m = i;
      int i2 = i + ((ArrayList)localObject).size();
      label281:
      if (m < i2)
      {
        localSize = sizeForItem(m);
        int n = Math.round(f1 / f2 * (localSize.width / localSize.height));
        if ((i1 < 3) || (m != i2 - 1))
        {
          n = (int)(n / paramFloat * getSpanCount());
          j -= n;
        }
        for (;;)
        {
          this.itemSpans.put(m, Integer.valueOf(n));
          m += 1;
          break label281;
          label375:
          f1 = f3;
          if (((ArrayList)localObject).size() >= 3) {
            break;
          }
          f1 = (float)Math.floor(2.0F * paramFloat / 3.0F);
          break;
          this.itemsToRow.put(m, Integer.valueOf(k));
          n = j;
        }
      }
      i += ((ArrayList)localObject).size();
      k += 1;
    }
  }
  
  private Size sizeForItem(int paramInt)
  {
    Size localSize = getSizeForItem(paramInt);
    if (localSize.width == 0.0F) {
      localSize.width = 100.0F;
    }
    if (localSize.height == 0.0F) {
      localSize.height = 100.0F;
    }
    float f = localSize.width / localSize.height;
    if ((f > 4.0F) || (f < 0.2F))
    {
      f = Math.max(localSize.width, localSize.height);
      localSize.width = f;
      localSize.height = f;
    }
    return localSize;
  }
  
  protected int getFlowItemCount()
  {
    return getItemCount();
  }
  
  public int getRowsCount(int paramInt)
  {
    if (this.rows == null) {
      prepareLayout(paramInt);
    }
    if (this.rows != null) {
      return this.rows.size();
    }
    return 0;
  }
  
  protected Size getSizeForItem(int paramInt)
  {
    return new Size(100.0F, 100.0F);
  }
  
  public int getSpanSizeForItem(int paramInt)
  {
    checkLayout();
    return ((Integer)this.itemSpans.get(paramInt)).intValue();
  }
  
  public boolean isFirstRow(int paramInt)
  {
    checkLayout();
    return (this.rows != null) && (!this.rows.isEmpty()) && (paramInt < ((ArrayList)this.rows.get(0)).size());
  }
  
  public boolean isLastInRow(int paramInt)
  {
    checkLayout();
    return this.itemsToRow.get(paramInt) != null;
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ExtendedGridLayoutManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
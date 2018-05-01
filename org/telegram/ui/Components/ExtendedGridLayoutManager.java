package org.telegram.ui.Components;

import android.content.Context;
import android.util.SparseIntArray;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.support.widget.GridLayoutManager;

public class ExtendedGridLayoutManager
  extends GridLayoutManager
{
  private int calculatedWidth;
  private SparseIntArray itemSpans = new SparseIntArray();
  private SparseIntArray itemsToRow = new SparseIntArray();
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
    int i = paramArrayOfInt.length;
    if (paramInt <= 0) {
      paramArrayOfInt = new ArrayList();
    }
    for (;;)
    {
      return paramArrayOfInt;
      ArrayList localArrayList1;
      ArrayList localArrayList2;
      if ((paramInt >= i) || (i == 1))
      {
        localArrayList1 = new ArrayList(paramArrayOfInt.length);
        for (paramInt = 0; paramInt < paramArrayOfInt.length; paramInt++)
        {
          localArrayList2 = new ArrayList(1);
          localArrayList2.add(Integer.valueOf(paramArrayOfInt[paramInt]));
          localArrayList1.add(localArrayList2);
        }
        paramArrayOfInt = localArrayList1;
      }
      else
      {
        int[] arrayOfInt = getLinearPartitionTable(paramArrayOfInt, paramInt);
        int j = paramInt - 1;
        int k = paramInt - 2;
        paramInt = i - 1;
        localArrayList1 = new ArrayList();
        if (k >= 0)
        {
          if (paramInt < 1) {
            localArrayList1.add(0, new ArrayList());
          }
          for (;;)
          {
            k--;
            break;
            localArrayList2 = new ArrayList();
            for (i = arrayOfInt[((paramInt - 1) * j + k)] + 1; i < paramInt + 1; i++) {
              localArrayList2.add(Integer.valueOf(paramArrayOfInt[i]));
            }
            localArrayList1.add(0, localArrayList2);
            paramInt = arrayOfInt[((paramInt - 1) * j + k)];
          }
        }
        localArrayList2 = new ArrayList();
        for (k = 0; k < paramInt + 1; k++) {
          localArrayList2.add(Integer.valueOf(paramArrayOfInt[k]));
        }
        localArrayList1.add(0, localArrayList2);
        paramArrayOfInt = localArrayList1;
      }
    }
  }
  
  private int[] getLinearPartitionTable(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramArrayOfInt.length;
    int[] arrayOfInt1 = new int[i * paramInt];
    int[] arrayOfInt2 = new int[(i - 1) * (paramInt - 1)];
    int j = 0;
    int k;
    if (j < i)
    {
      k = paramArrayOfInt[j];
      if (j != 0) {}
      for (m = arrayOfInt1[((j - 1) * paramInt)];; m = 0)
      {
        arrayOfInt1[(j * paramInt)] = (m + k);
        j++;
        break;
      }
    }
    for (j = 0; j < paramInt; j++) {
      arrayOfInt1[j] = paramArrayOfInt[0];
    }
    for (int m = 1; m < i; m++) {
      for (k = 1; k < paramInt; k++)
      {
        int n = 0;
        int i1 = Integer.MAX_VALUE;
        j = 0;
        while (j < m)
        {
          int i2 = Math.max(arrayOfInt1[(j * paramInt + (k - 1))], arrayOfInt1[(m * paramInt)] - arrayOfInt1[(j * paramInt)]);
          int i3;
          if (j != 0)
          {
            i3 = n;
            if (i2 >= n) {}
          }
          else
          {
            i3 = i2;
            i1 = j;
          }
          j++;
          n = i3;
        }
        arrayOfInt1[(m * paramInt + k)] = n;
        arrayOfInt2[((m - 1) * (paramInt - 1) + (k - 1))] = i1;
      }
    }
    return arrayOfInt2;
  }
  
  private void prepareLayout(float paramFloat)
  {
    this.itemSpans.clear();
    this.itemsToRow.clear();
    int i = AndroidUtilities.dp(100.0F);
    float f1 = 0.0F;
    int j = getFlowItemCount();
    Object localObject1 = new int[j];
    Object localObject2;
    for (int k = 0; k < j; k++)
    {
      localObject2 = sizeForItem(k);
      f1 += ((Size)localObject2).width / ((Size)localObject2).height * i;
      localObject1[k] = Math.round(((Size)localObject2).width / ((Size)localObject2).height * 100.0F);
    }
    this.rows = getLinearPartitionForSequence((int[])localObject1, Math.max(Math.round(f1 / paramFloat), 1));
    k = 0;
    for (int m = 0; m < this.rows.size(); m++)
    {
      localObject2 = (ArrayList)this.rows.get(m);
      float f2 = 0.0F;
      i = k;
      int n = ((ArrayList)localObject2).size();
      while (i < k + n)
      {
        localObject1 = sizeForItem(i);
        f2 += ((Size)localObject1).width / ((Size)localObject1).height;
        i++;
      }
      float f3 = paramFloat;
      f1 = f3;
      if (this.rows.size() == 1)
      {
        f1 = f3;
        if (m == this.rows.size() - 1)
        {
          if (((ArrayList)localObject2).size() >= 2) {
            break label359;
          }
          f1 = (float)Math.floor(paramFloat / 3.0F);
        }
      }
      i = getSpanCount();
      n = k;
      int i1 = k + ((ArrayList)localObject2).size();
      label272:
      if (n < i1)
      {
        localObject1 = sizeForItem(n);
        int i2 = Math.round(f1 / f2 * (((Size)localObject1).width / ((Size)localObject1).height));
        if ((j < 3) || (n != i1 - 1))
        {
          i2 = (int)(i2 / paramFloat * getSpanCount());
          i -= i2;
        }
        for (;;)
        {
          this.itemSpans.put(n, i2);
          n++;
          break label272;
          label359:
          f1 = f3;
          if (((ArrayList)localObject2).size() >= 3) {
            break;
          }
          f1 = (float)Math.floor(2.0F * paramFloat / 3.0F);
          break;
          this.itemsToRow.put(n, m);
          i2 = i;
        }
      }
      k += ((ArrayList)localObject2).size();
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
    if (this.rows != null) {}
    for (paramInt = this.rows.size();; paramInt = 0) {
      return paramInt;
    }
  }
  
  protected Size getSizeForItem(int paramInt)
  {
    return new Size(100.0F, 100.0F);
  }
  
  public int getSpanSizeForItem(int paramInt)
  {
    checkLayout();
    return this.itemSpans.get(paramInt);
  }
  
  public boolean isFirstRow(int paramInt)
  {
    checkLayout();
    if ((this.rows != null) && (!this.rows.isEmpty()) && (paramInt < ((ArrayList)this.rows.get(0)).size())) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean isLastInRow(int paramInt)
  {
    checkLayout();
    if (this.itemsToRow.get(paramInt, Integer.MAX_VALUE) != Integer.MAX_VALUE) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
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
package com.google.android.gms.internal;

import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

public final class hc
{
  private static int zza(String paramString, hg[] paramArrayOfhg)
  {
    int m = paramArrayOfhg.length;
    int j = 0;
    int i = 14;
    if (j < m)
    {
      hg localhg = paramArrayOfhg[j];
      int k;
      if (i == 14) {
        if ((localhg.type == 9) || (localhg.type == 2) || (localhg.type == 6)) {
          k = localhg.type;
        }
      }
      do
      {
        do
        {
          j += 1;
          i = k;
          break;
          k = i;
        } while (localhg.type == 14);
        i = localhg.type;
        throw new IllegalArgumentException(String.valueOf(paramString).length() + 48 + "Unexpected TypedValue type: " + i + " for key " + paramString);
        k = i;
      } while (localhg.type == i);
      j = localhg.type;
      throw new IllegalArgumentException(String.valueOf(paramString).length() + 126 + "The ArrayList elements should all be the same type, but ArrayList with key " + paramString + " contains items of type " + i + " and " + j);
    }
    return i;
  }
  
  public static hd zza(DataMap paramDataMap)
  {
    he localhe = new he();
    ArrayList localArrayList = new ArrayList();
    localhe.zzbTH = zza(paramDataMap, localArrayList);
    return new hd(localhe, localArrayList);
  }
  
  private static hg zza(List<Asset> paramList, Object paramObject)
  {
    hg localhg1 = new hg();
    if (paramObject == null)
    {
      localhg1.type = 14;
      return localhg1;
    }
    localhg1.zzbTL = new hh();
    if ((paramObject instanceof String))
    {
      localhg1.type = 2;
      localhg1.zzbTL.zzbTN = ((String)paramObject);
    }
    Object localObject1;
    Object localObject2;
    int i;
    Object localObject3;
    for (;;)
    {
      return localhg1;
      if ((paramObject instanceof Integer))
      {
        localhg1.type = 6;
        localhg1.zzbTL.zzbTR = ((Integer)paramObject).intValue();
      }
      else if ((paramObject instanceof Long))
      {
        localhg1.type = 5;
        localhg1.zzbTL.zzbTQ = ((Long)paramObject).longValue();
      }
      else if ((paramObject instanceof Double))
      {
        localhg1.type = 3;
        localhg1.zzbTL.zzbTO = ((Double)paramObject).doubleValue();
      }
      else if ((paramObject instanceof Float))
      {
        localhg1.type = 4;
        localhg1.zzbTL.zzbTP = ((Float)paramObject).floatValue();
      }
      else if ((paramObject instanceof Boolean))
      {
        localhg1.type = 8;
        localhg1.zzbTL.zzbTT = ((Boolean)paramObject).booleanValue();
      }
      else if ((paramObject instanceof Byte))
      {
        localhg1.type = 7;
        localhg1.zzbTL.zzbTS = ((Byte)paramObject).byteValue();
      }
      else if ((paramObject instanceof byte[]))
      {
        localhg1.type = 1;
        localhg1.zzbTL.zzbTM = ((byte[])paramObject);
      }
      else if ((paramObject instanceof String[]))
      {
        localhg1.type = 11;
        localhg1.zzbTL.zzbTW = ((String[])paramObject);
      }
      else if ((paramObject instanceof long[]))
      {
        localhg1.type = 12;
        localhg1.zzbTL.zzbTX = ((long[])paramObject);
      }
      else if ((paramObject instanceof float[]))
      {
        localhg1.type = 15;
        localhg1.zzbTL.zzbTY = ((float[])paramObject);
      }
      else if ((paramObject instanceof Asset))
      {
        localhg1.type = 13;
        localObject1 = localhg1.zzbTL;
        paramList.add((Asset)paramObject);
        ((hh)localObject1).zzbTZ = (paramList.size() - 1);
      }
      else
      {
        if (!(paramObject instanceof DataMap)) {
          break;
        }
        localhg1.type = 9;
        paramObject = (DataMap)paramObject;
        localObject2 = new TreeSet(((DataMap)paramObject).keySet());
        localObject1 = new hf[((TreeSet)localObject2).size()];
        localObject2 = ((TreeSet)localObject2).iterator();
        i = 0;
        while (((Iterator)localObject2).hasNext())
        {
          localObject3 = (String)((Iterator)localObject2).next();
          localObject1[i] = new hf();
          localObject1[i].name = ((String)localObject3);
          localObject1[i].zzbTJ = zza(paramList, ((DataMap)paramObject).get((String)localObject3));
          i += 1;
        }
        localhg1.zzbTL.zzbTU = ((hf[])localObject1);
      }
    }
    int j;
    label590:
    hg localhg2;
    if ((paramObject instanceof ArrayList))
    {
      localhg1.type = 10;
      localObject2 = (ArrayList)paramObject;
      localObject3 = new hg[((ArrayList)localObject2).size()];
      paramObject = null;
      int k = ((ArrayList)localObject2).size();
      j = 0;
      i = 14;
      if (j < k)
      {
        localObject1 = ((ArrayList)localObject2).get(j);
        localhg2 = zza(paramList, localObject1);
        if ((localhg2.type != 14) && (localhg2.type != 2) && (localhg2.type != 6) && (localhg2.type != 9))
        {
          paramList = String.valueOf(localObject1.getClass());
          throw new IllegalArgumentException(String.valueOf(paramList).length() + 130 + "The only ArrayList element types supported by DataBundleUtil are String, Integer, Bundle, and null, but this ArrayList contains a " + paramList);
        }
        if ((i == 14) && (localhg2.type != 14))
        {
          i = localhg2.type;
          paramObject = localObject1;
        }
      }
    }
    for (;;)
    {
      localObject3[j] = localhg2;
      j += 1;
      break label590;
      if (localhg2.type != i)
      {
        paramList = String.valueOf(paramObject.getClass());
        paramObject = String.valueOf(localObject1.getClass());
        throw new IllegalArgumentException(String.valueOf(paramList).length() + 80 + String.valueOf(paramObject).length() + "ArrayList elements must all be of the sameclass, but this one contains a " + paramList + " and a " + (String)paramObject);
        localhg1.zzbTL.zzbTV = ((hg[])localObject3);
        break;
        paramList = String.valueOf(paramObject.getClass().getSimpleName());
        if (paramList.length() != 0) {}
        for (paramList = "newFieldValueFromValue: unexpected value ".concat(paramList);; paramList = new String("newFieldValueFromValue: unexpected value ")) {
          throw new RuntimeException(paramList);
        }
      }
    }
  }
  
  public static DataMap zza(hd paramhd)
  {
    DataMap localDataMap = new DataMap();
    hf[] arrayOfhf = paramhd.zzbTF.zzbTH;
    int j = arrayOfhf.length;
    int i = 0;
    while (i < j)
    {
      hf localhf = arrayOfhf[i];
      zza(paramhd.zzbTG, localDataMap, localhf.name, localhf.zzbTJ);
      i += 1;
    }
    return localDataMap;
  }
  
  private static ArrayList zza(List<Asset> paramList, hh paramhh, int paramInt)
  {
    ArrayList localArrayList = new ArrayList(paramhh.zzbTV.length);
    paramhh = paramhh.zzbTV;
    int k = paramhh.length;
    int i = 0;
    if (i < k)
    {
      hf[] arrayOfhf = paramhh[i];
      if (arrayOfhf.type == 14) {
        localArrayList.add(null);
      }
      for (;;)
      {
        i += 1;
        break;
        if (paramInt == 9)
        {
          DataMap localDataMap = new DataMap();
          arrayOfhf = arrayOfhf.zzbTL.zzbTU;
          int m = arrayOfhf.length;
          int j = 0;
          while (j < m)
          {
            hf localhf = arrayOfhf[j];
            zza(paramList, localDataMap, localhf.name, localhf.zzbTJ);
            j += 1;
          }
          localArrayList.add(localDataMap);
        }
        else if (paramInt == 2)
        {
          localArrayList.add(arrayOfhf.zzbTL.zzbTN);
        }
        else
        {
          if (paramInt != 6) {
            break label191;
          }
          localArrayList.add(Integer.valueOf(arrayOfhf.zzbTL.zzbTR));
        }
      }
      label191:
      throw new IllegalArgumentException(39 + "Unexpected typeOfArrayList: " + paramInt);
    }
    return localArrayList;
  }
  
  private static void zza(List<Asset> paramList, DataMap paramDataMap, String paramString, hg paramhg)
  {
    int i = paramhg.type;
    if (i == 14)
    {
      paramDataMap.putString(paramString, null);
      return;
    }
    Object localObject1 = paramhg.zzbTL;
    if (i == 1)
    {
      paramDataMap.putByteArray(paramString, ((hh)localObject1).zzbTM);
      return;
    }
    if (i == 11)
    {
      paramDataMap.putStringArray(paramString, ((hh)localObject1).zzbTW);
      return;
    }
    if (i == 12)
    {
      paramDataMap.putLongArray(paramString, ((hh)localObject1).zzbTX);
      return;
    }
    if (i == 15)
    {
      paramDataMap.putFloatArray(paramString, ((hh)localObject1).zzbTY);
      return;
    }
    if (i == 2)
    {
      paramDataMap.putString(paramString, ((hh)localObject1).zzbTN);
      return;
    }
    if (i == 3)
    {
      paramDataMap.putDouble(paramString, ((hh)localObject1).zzbTO);
      return;
    }
    if (i == 4)
    {
      paramDataMap.putFloat(paramString, ((hh)localObject1).zzbTP);
      return;
    }
    if (i == 5)
    {
      paramDataMap.putLong(paramString, ((hh)localObject1).zzbTQ);
      return;
    }
    if (i == 6)
    {
      paramDataMap.putInt(paramString, ((hh)localObject1).zzbTR);
      return;
    }
    if (i == 7)
    {
      paramDataMap.putByte(paramString, (byte)((hh)localObject1).zzbTS);
      return;
    }
    if (i == 8)
    {
      paramDataMap.putBoolean(paramString, ((hh)localObject1).zzbTT);
      return;
    }
    if (i == 13)
    {
      if (paramList == null)
      {
        paramList = String.valueOf(paramString);
        if (paramList.length() != 0) {}
        for (paramList = "populateBundle: unexpected type for: ".concat(paramList);; paramList = new String("populateBundle: unexpected type for: ")) {
          throw new RuntimeException(paramList);
        }
      }
      paramDataMap.putAsset(paramString, (Asset)paramList.get((int)((hh)localObject1).zzbTZ));
      return;
    }
    if (i == 9)
    {
      paramhg = new DataMap();
      localObject1 = ((hh)localObject1).zzbTU;
      int j = localObject1.length;
      i = 0;
      while (i < j)
      {
        Object localObject2 = localObject1[i];
        zza(paramList, paramhg, ((hf)localObject2).name, ((hf)localObject2).zzbTJ);
        i += 1;
      }
      paramDataMap.putDataMap(paramString, paramhg);
      return;
    }
    if (i == 10)
    {
      i = zza(paramString, ((hh)localObject1).zzbTV);
      paramList = zza(paramList, (hh)localObject1, i);
      if (i == 14)
      {
        paramDataMap.putStringArrayList(paramString, paramList);
        return;
      }
      if (i == 9)
      {
        paramDataMap.putDataMapArrayList(paramString, paramList);
        return;
      }
      if (i == 2)
      {
        paramDataMap.putStringArrayList(paramString, paramList);
        return;
      }
      if (i == 6)
      {
        paramDataMap.putIntegerArrayList(paramString, paramList);
        return;
      }
      throw new IllegalStateException(39 + "Unexpected typeOfArrayList: " + i);
    }
    throw new RuntimeException(43 + "populateBundle: unexpected type " + i);
  }
  
  private static hf[] zza(DataMap paramDataMap, List<Asset> paramList)
  {
    Object localObject1 = new TreeSet(paramDataMap.keySet());
    hf[] arrayOfhf = new hf[((TreeSet)localObject1).size()];
    localObject1 = ((TreeSet)localObject1).iterator();
    int i = 0;
    while (((Iterator)localObject1).hasNext())
    {
      String str = (String)((Iterator)localObject1).next();
      Object localObject2 = paramDataMap.get(str);
      arrayOfhf[i] = new hf();
      arrayOfhf[i].name = str;
      arrayOfhf[i].zzbTJ = zza(paramList, localObject2);
      i += 1;
    }
    return arrayOfhf;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/hc.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
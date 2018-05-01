package com.google.android.gms.wearable;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.internal.ado;
import com.google.android.gms.internal.adp;
import com.google.android.gms.internal.hc;
import com.google.android.gms.internal.hd;
import com.google.android.gms.internal.he;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class DataMap
{
  public static final String TAG = "DataMap";
  private final HashMap<String, Object> zzrO = new HashMap();
  
  public static ArrayList<DataMap> arrayListFromBundleArrayList(ArrayList<Bundle> paramArrayList)
  {
    ArrayList localArrayList = new ArrayList();
    paramArrayList = (ArrayList)paramArrayList;
    int j = paramArrayList.size();
    int i = 0;
    while (i < j)
    {
      Object localObject = paramArrayList.get(i);
      i += 1;
      localArrayList.add(fromBundle((Bundle)localObject));
    }
    return localArrayList;
  }
  
  public static DataMap fromBundle(Bundle paramBundle)
  {
    paramBundle.setClassLoader(Asset.class.getClassLoader());
    DataMap localDataMap = new DataMap();
    Iterator localIterator = paramBundle.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      Object localObject = paramBundle.get(str);
      if ((localObject instanceof String)) {
        localDataMap.putString(str, (String)localObject);
      } else if ((localObject instanceof Integer)) {
        localDataMap.putInt(str, ((Integer)localObject).intValue());
      } else if ((localObject instanceof Long)) {
        localDataMap.putLong(str, ((Long)localObject).longValue());
      } else if ((localObject instanceof Double)) {
        localDataMap.putDouble(str, ((Double)localObject).doubleValue());
      } else if ((localObject instanceof Float)) {
        localDataMap.putFloat(str, ((Float)localObject).floatValue());
      } else if ((localObject instanceof Boolean)) {
        localDataMap.putBoolean(str, ((Boolean)localObject).booleanValue());
      } else if ((localObject instanceof Byte)) {
        localDataMap.putByte(str, ((Byte)localObject).byteValue());
      } else if ((localObject instanceof byte[])) {
        localDataMap.putByteArray(str, (byte[])localObject);
      } else if ((localObject instanceof String[])) {
        localDataMap.putStringArray(str, (String[])localObject);
      } else if ((localObject instanceof long[])) {
        localDataMap.putLongArray(str, (long[])localObject);
      } else if ((localObject instanceof float[])) {
        localDataMap.putFloatArray(str, (float[])localObject);
      } else if ((localObject instanceof Asset)) {
        localDataMap.putAsset(str, (Asset)localObject);
      } else if ((localObject instanceof Bundle)) {
        localDataMap.putDataMap(str, fromBundle((Bundle)localObject));
      } else if ((localObject instanceof ArrayList)) {
        switch (zze((ArrayList)localObject))
        {
        case 4: 
        default: 
          break;
        case 0: 
          localDataMap.putStringArrayList(str, (ArrayList)localObject);
          break;
        case 1: 
          localDataMap.putStringArrayList(str, (ArrayList)localObject);
          break;
        case 3: 
          localDataMap.putStringArrayList(str, (ArrayList)localObject);
          break;
        case 2: 
          localDataMap.putIntegerArrayList(str, (ArrayList)localObject);
          break;
        case 5: 
          localDataMap.putDataMapArrayList(str, arrayListFromBundleArrayList((ArrayList)localObject));
        }
      }
    }
    return localDataMap;
  }
  
  public static DataMap fromByteArray(byte[] paramArrayOfByte)
  {
    try
    {
      paramArrayOfByte = hc.zza(new hd(he.zzy(paramArrayOfByte), new ArrayList()));
      return paramArrayOfByte;
    }
    catch (ado paramArrayOfByte)
    {
      throw new IllegalArgumentException("Unable to convert data", paramArrayOfByte);
    }
  }
  
  private static void zza(String paramString1, Object paramObject, String paramString2, ClassCastException paramClassCastException)
  {
    zza(paramString1, paramObject, paramString2, "<null>", paramClassCastException);
  }
  
  private static void zza(String paramString1, Object paramObject1, String paramString2, Object paramObject2, ClassCastException paramClassCastException)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Key ");
    localStringBuilder.append(paramString1);
    localStringBuilder.append(" expected ");
    localStringBuilder.append(paramString2);
    localStringBuilder.append(" but value was a ");
    localStringBuilder.append(paramObject1.getClass().getName());
    localStringBuilder.append(".  The default value ");
    localStringBuilder.append(paramObject2);
    localStringBuilder.append(" was returned.");
    Log.w("DataMap", localStringBuilder.toString());
    Log.w("DataMap", "Attempt to cast generated internal exception:", paramClassCastException);
  }
  
  private static void zzb(Bundle paramBundle, String paramString, Object paramObject)
  {
    if ((paramObject instanceof String)) {
      paramBundle.putString(paramString, (String)paramObject);
    }
    do
    {
      return;
      if ((paramObject instanceof Integer))
      {
        paramBundle.putInt(paramString, ((Integer)paramObject).intValue());
        return;
      }
      if ((paramObject instanceof Long))
      {
        paramBundle.putLong(paramString, ((Long)paramObject).longValue());
        return;
      }
      if ((paramObject instanceof Double))
      {
        paramBundle.putDouble(paramString, ((Double)paramObject).doubleValue());
        return;
      }
      if ((paramObject instanceof Float))
      {
        paramBundle.putFloat(paramString, ((Float)paramObject).floatValue());
        return;
      }
      if ((paramObject instanceof Boolean))
      {
        paramBundle.putBoolean(paramString, ((Boolean)paramObject).booleanValue());
        return;
      }
      if ((paramObject instanceof Byte))
      {
        paramBundle.putByte(paramString, ((Byte)paramObject).byteValue());
        return;
      }
      if ((paramObject instanceof byte[]))
      {
        paramBundle.putByteArray(paramString, (byte[])paramObject);
        return;
      }
      if ((paramObject instanceof String[]))
      {
        paramBundle.putStringArray(paramString, (String[])paramObject);
        return;
      }
      if ((paramObject instanceof long[]))
      {
        paramBundle.putLongArray(paramString, (long[])paramObject);
        return;
      }
      if ((paramObject instanceof float[]))
      {
        paramBundle.putFloatArray(paramString, (float[])paramObject);
        return;
      }
      if ((paramObject instanceof Asset))
      {
        paramBundle.putParcelable(paramString, (Asset)paramObject);
        return;
      }
      if ((paramObject instanceof DataMap))
      {
        paramBundle.putParcelable(paramString, ((DataMap)paramObject).toBundle());
        return;
      }
    } while (!(paramObject instanceof ArrayList));
    switch (zze((ArrayList)paramObject))
    {
    default: 
      return;
    case 0: 
      paramBundle.putStringArrayList(paramString, (ArrayList)paramObject);
      return;
    case 1: 
      paramBundle.putStringArrayList(paramString, (ArrayList)paramObject);
      return;
    case 3: 
      paramBundle.putStringArrayList(paramString, (ArrayList)paramObject);
      return;
    case 2: 
      paramBundle.putIntegerArrayList(paramString, (ArrayList)paramObject);
      return;
    }
    ArrayList localArrayList = new ArrayList();
    paramObject = (ArrayList)paramObject;
    int j = ((ArrayList)paramObject).size();
    int i = 0;
    while (i < j)
    {
      Object localObject = ((ArrayList)paramObject).get(i);
      i += 1;
      localArrayList.add(((DataMap)localObject).toBundle());
    }
    paramBundle.putParcelableArrayList(paramString, localArrayList);
  }
  
  private static int zze(ArrayList<?> paramArrayList)
  {
    int i = 0;
    if (paramArrayList.isEmpty()) {
      return 0;
    }
    paramArrayList = (ArrayList)paramArrayList;
    int k = paramArrayList.size();
    while (i < k)
    {
      Object localObject = paramArrayList.get(i);
      int j = i + 1;
      i = j;
      if (localObject != null)
      {
        if ((localObject instanceof Integer)) {
          return 2;
        }
        if ((localObject instanceof String)) {
          return 3;
        }
        if ((localObject instanceof DataMap)) {
          return 4;
        }
        i = j;
        if ((localObject instanceof Bundle)) {
          return 5;
        }
      }
    }
    return 1;
  }
  
  public void clear()
  {
    this.zzrO.clear();
  }
  
  public boolean containsKey(String paramString)
  {
    return this.zzrO.containsKey(paramString);
  }
  
  public boolean equals(Object paramObject)
  {
    if (!(paramObject instanceof DataMap)) {
      return false;
    }
    paramObject = (DataMap)paramObject;
    if (size() != ((DataMap)paramObject).size()) {
      return false;
    }
    Iterator localIterator = keySet().iterator();
    while (localIterator.hasNext())
    {
      Object localObject2 = (String)localIterator.next();
      Object localObject1 = get((String)localObject2);
      localObject2 = ((DataMap)paramObject).get((String)localObject2);
      if ((localObject1 instanceof Asset))
      {
        if (!(localObject2 instanceof Asset)) {
          return false;
        }
        localObject1 = (Asset)localObject1;
        localObject2 = (Asset)localObject2;
        boolean bool;
        if ((localObject1 == null) || (localObject2 == null)) {
          if (localObject1 == localObject2) {
            bool = true;
          }
        }
        while (!bool)
        {
          return false;
          bool = false;
          continue;
          if (!TextUtils.isEmpty(((Asset)localObject1).getDigest())) {
            bool = ((Asset)localObject1).getDigest().equals(((Asset)localObject2).getDigest());
          } else {
            bool = Arrays.equals(((Asset)localObject1).getData(), ((Asset)localObject2).getData());
          }
        }
      }
      else if ((localObject1 instanceof String[]))
      {
        if (!(localObject2 instanceof String[])) {
          return false;
        }
        if (!Arrays.equals((String[])localObject1, (String[])localObject2)) {
          return false;
        }
      }
      else if ((localObject1 instanceof long[]))
      {
        if (!(localObject2 instanceof long[])) {
          return false;
        }
        if (!Arrays.equals((long[])localObject1, (long[])localObject2)) {
          return false;
        }
      }
      else if ((localObject1 instanceof float[]))
      {
        if (!(localObject2 instanceof float[])) {
          return false;
        }
        if (!Arrays.equals((float[])localObject1, (float[])localObject2)) {
          return false;
        }
      }
      else if ((localObject1 instanceof byte[]))
      {
        if (!(localObject2 instanceof byte[])) {
          return false;
        }
        if (!Arrays.equals((byte[])localObject1, (byte[])localObject2)) {
          return false;
        }
      }
      else if ((localObject1 == null) || (localObject2 == null))
      {
        if (localObject1 != localObject2) {
          return false;
        }
      }
      else if (!localObject1.equals(localObject2))
      {
        return false;
      }
    }
    return true;
  }
  
  public <T> T get(String paramString)
  {
    return (T)this.zzrO.get(paramString);
  }
  
  public Asset getAsset(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      Asset localAsset = (Asset)localObject;
      return localAsset;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "Asset", localClassCastException);
    }
    return null;
  }
  
  public boolean getBoolean(String paramString)
  {
    return getBoolean(paramString, false);
  }
  
  public boolean getBoolean(String paramString, boolean paramBoolean)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return paramBoolean;
    }
    try
    {
      boolean bool = ((Boolean)localObject).booleanValue();
      return bool;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "Boolean", Boolean.valueOf(paramBoolean), localClassCastException);
    }
    return paramBoolean;
  }
  
  public byte getByte(String paramString)
  {
    return getByte(paramString, (byte)0);
  }
  
  public byte getByte(String paramString, byte paramByte)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return paramByte;
    }
    try
    {
      byte b = ((Byte)localObject).byteValue();
      return b;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "Byte", Byte.valueOf(paramByte), localClassCastException);
    }
    return paramByte;
  }
  
  public byte[] getByteArray(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      byte[] arrayOfByte = (byte[])localObject;
      return arrayOfByte;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "byte[]", localClassCastException);
    }
    return null;
  }
  
  public DataMap getDataMap(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      DataMap localDataMap = (DataMap)localObject;
      return localDataMap;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "DataMap", localClassCastException);
    }
    return null;
  }
  
  public ArrayList<DataMap> getDataMapArrayList(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      ArrayList localArrayList = (ArrayList)localObject;
      return localArrayList;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "ArrayList<DataMap>", localClassCastException);
    }
    return null;
  }
  
  public double getDouble(String paramString)
  {
    return getDouble(paramString, 0.0D);
  }
  
  public double getDouble(String paramString, double paramDouble)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return paramDouble;
    }
    try
    {
      double d = ((Double)localObject).doubleValue();
      return d;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "Double", Double.valueOf(paramDouble), localClassCastException);
    }
    return paramDouble;
  }
  
  public float getFloat(String paramString)
  {
    return getFloat(paramString, 0.0F);
  }
  
  public float getFloat(String paramString, float paramFloat)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return paramFloat;
    }
    try
    {
      float f = ((Float)localObject).floatValue();
      return f;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "Float", Float.valueOf(paramFloat), localClassCastException);
    }
    return paramFloat;
  }
  
  public float[] getFloatArray(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      float[] arrayOfFloat = (float[])localObject;
      return arrayOfFloat;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "float[]", localClassCastException);
    }
    return null;
  }
  
  public int getInt(String paramString)
  {
    return getInt(paramString, 0);
  }
  
  public int getInt(String paramString, int paramInt)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return paramInt;
    }
    try
    {
      int i = ((Integer)localObject).intValue();
      return i;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "Integer", localClassCastException);
    }
    return paramInt;
  }
  
  public ArrayList<Integer> getIntegerArrayList(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      ArrayList localArrayList = (ArrayList)localObject;
      return localArrayList;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "ArrayList<Integer>", localClassCastException);
    }
    return null;
  }
  
  public long getLong(String paramString)
  {
    return getLong(paramString, 0L);
  }
  
  public long getLong(String paramString, long paramLong)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return paramLong;
    }
    try
    {
      long l = ((Long)localObject).longValue();
      return l;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "long", localClassCastException);
    }
    return paramLong;
  }
  
  public long[] getLongArray(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      long[] arrayOfLong = (long[])localObject;
      return arrayOfLong;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "long[]", localClassCastException);
    }
    return null;
  }
  
  public String getString(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      String str = (String)localObject;
      return str;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "String", localClassCastException);
    }
    return null;
  }
  
  public String getString(String paramString1, String paramString2)
  {
    paramString1 = getString(paramString1);
    if (paramString1 == null) {
      return paramString2;
    }
    return paramString1;
  }
  
  public String[] getStringArray(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      String[] arrayOfString = (String[])localObject;
      return arrayOfString;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "String[]", localClassCastException);
    }
    return null;
  }
  
  public ArrayList<String> getStringArrayList(String paramString)
  {
    Object localObject = this.zzrO.get(paramString);
    if (localObject == null) {
      return null;
    }
    try
    {
      ArrayList localArrayList = (ArrayList)localObject;
      return localArrayList;
    }
    catch (ClassCastException localClassCastException)
    {
      zza(paramString, localObject, "ArrayList<String>", localClassCastException);
    }
    return null;
  }
  
  public int hashCode()
  {
    return this.zzrO.hashCode() * 29;
  }
  
  public boolean isEmpty()
  {
    return this.zzrO.isEmpty();
  }
  
  public Set<String> keySet()
  {
    return this.zzrO.keySet();
  }
  
  public void putAll(DataMap paramDataMap)
  {
    Iterator localIterator = paramDataMap.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      this.zzrO.put(str, paramDataMap.get(str));
    }
  }
  
  public void putAsset(String paramString, Asset paramAsset)
  {
    this.zzrO.put(paramString, paramAsset);
  }
  
  public void putBoolean(String paramString, boolean paramBoolean)
  {
    this.zzrO.put(paramString, Boolean.valueOf(paramBoolean));
  }
  
  public void putByte(String paramString, byte paramByte)
  {
    this.zzrO.put(paramString, Byte.valueOf(paramByte));
  }
  
  public void putByteArray(String paramString, byte[] paramArrayOfByte)
  {
    this.zzrO.put(paramString, paramArrayOfByte);
  }
  
  public void putDataMap(String paramString, DataMap paramDataMap)
  {
    this.zzrO.put(paramString, paramDataMap);
  }
  
  public void putDataMapArrayList(String paramString, ArrayList<DataMap> paramArrayList)
  {
    this.zzrO.put(paramString, paramArrayList);
  }
  
  public void putDouble(String paramString, double paramDouble)
  {
    this.zzrO.put(paramString, Double.valueOf(paramDouble));
  }
  
  public void putFloat(String paramString, float paramFloat)
  {
    this.zzrO.put(paramString, Float.valueOf(paramFloat));
  }
  
  public void putFloatArray(String paramString, float[] paramArrayOfFloat)
  {
    this.zzrO.put(paramString, paramArrayOfFloat);
  }
  
  public void putInt(String paramString, int paramInt)
  {
    this.zzrO.put(paramString, Integer.valueOf(paramInt));
  }
  
  public void putIntegerArrayList(String paramString, ArrayList<Integer> paramArrayList)
  {
    this.zzrO.put(paramString, paramArrayList);
  }
  
  public void putLong(String paramString, long paramLong)
  {
    this.zzrO.put(paramString, Long.valueOf(paramLong));
  }
  
  public void putLongArray(String paramString, long[] paramArrayOfLong)
  {
    this.zzrO.put(paramString, paramArrayOfLong);
  }
  
  public void putString(String paramString1, String paramString2)
  {
    this.zzrO.put(paramString1, paramString2);
  }
  
  public void putStringArray(String paramString, String[] paramArrayOfString)
  {
    this.zzrO.put(paramString, paramArrayOfString);
  }
  
  public void putStringArrayList(String paramString, ArrayList<String> paramArrayList)
  {
    this.zzrO.put(paramString, paramArrayList);
  }
  
  public Object remove(String paramString)
  {
    return this.zzrO.remove(paramString);
  }
  
  public int size()
  {
    return this.zzrO.size();
  }
  
  public Bundle toBundle()
  {
    Bundle localBundle = new Bundle();
    Iterator localIterator = this.zzrO.keySet().iterator();
    while (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      zzb(localBundle, str, this.zzrO.get(str));
    }
    return localBundle;
  }
  
  public byte[] toByteArray()
  {
    return adp.zzc(hc.zza(this).zzbTF);
  }
  
  public String toString()
  {
    return this.zzrO.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/DataMap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package com.google.android.gms.common.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class Objects
{
  public static boolean equal(Object paramObject1, Object paramObject2)
  {
    if ((paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject1.equals(paramObject2)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static int hashCode(Object... paramVarArgs)
  {
    return Arrays.hashCode(paramVarArgs);
  }
  
  public static ToStringHelper toStringHelper(Object paramObject)
  {
    return new ToStringHelper(paramObject, null);
  }
  
  public static final class ToStringHelper
  {
    private final List<String> zzul;
    private final Object zzum;
    
    private ToStringHelper(Object paramObject)
    {
      this.zzum = Preconditions.checkNotNull(paramObject);
      this.zzul = new ArrayList();
    }
    
    public final ToStringHelper add(String paramString, Object paramObject)
    {
      List localList = this.zzul;
      paramString = (String)Preconditions.checkNotNull(paramString);
      paramObject = String.valueOf(paramObject);
      localList.add(String.valueOf(paramString).length() + 1 + String.valueOf(paramObject).length() + paramString + "=" + (String)paramObject);
      return this;
    }
    
    public final String toString()
    {
      StringBuilder localStringBuilder = new StringBuilder(100).append(this.zzum.getClass().getSimpleName()).append('{');
      int i = this.zzul.size();
      for (int j = 0; j < i; j++)
      {
        localStringBuilder.append((String)this.zzul.get(j));
        if (j < i - 1) {
          localStringBuilder.append(", ");
        }
      }
      return '}';
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/Objects.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
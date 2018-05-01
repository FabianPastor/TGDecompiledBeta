package com.google.android.gms.wearable;

import com.google.android.gms.common.api.CommonStatusCodes;

public final class WearableStatusCodes
  extends CommonStatusCodes
{
  public static String getStatusCodeString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    default: 
      str = CommonStatusCodes.getStatusCodeString(paramInt);
    }
    for (;;)
    {
      return str;
      str = "TARGET_NODE_NOT_CONNECTED";
      continue;
      str = "DUPLICATE_LISTENER";
      continue;
      str = "UNKNOWN_LISTENER";
      continue;
      str = "DATA_ITEM_TOO_LARGE";
      continue;
      str = "INVALID_TARGET_NODE";
      continue;
      str = "ASSET_UNAVAILABLE";
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/WearableStatusCodes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
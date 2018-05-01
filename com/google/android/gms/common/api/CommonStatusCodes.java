package com.google.android.gms.common.api;

public class CommonStatusCodes
{
  public static String getStatusCodeString(int paramInt)
  {
    String str;
    switch (paramInt)
    {
    case 1: 
    case 9: 
    case 11: 
    case 12: 
    default: 
      str = 32 + "unknown status code: " + paramInt;
    }
    for (;;)
    {
      return str;
      str = "SUCCESS_CACHE";
      continue;
      str = "SUCCESS";
      continue;
      str = "SERVICE_VERSION_UPDATE_REQUIRED";
      continue;
      str = "SERVICE_DISABLED";
      continue;
      str = "SIGN_IN_REQUIRED";
      continue;
      str = "INVALID_ACCOUNT";
      continue;
      str = "RESOLUTION_REQUIRED";
      continue;
      str = "NETWORK_ERROR";
      continue;
      str = "INTERNAL_ERROR";
      continue;
      str = "DEVELOPER_ERROR";
      continue;
      str = "ERROR";
      continue;
      str = "INTERRUPTED";
      continue;
      str = "TIMEOUT";
      continue;
      str = "CANCELED";
      continue;
      str = "API_NOT_CONNECTED";
      continue;
      str = "DEAD_CLIENT";
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/api/CommonStatusCodes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
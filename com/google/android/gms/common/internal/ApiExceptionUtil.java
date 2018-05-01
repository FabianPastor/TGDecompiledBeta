package com.google.android.gms.common.internal;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;

public class ApiExceptionUtil
{
  public static ApiException fromStatus(Status paramStatus)
  {
    if (paramStatus.hasResolution()) {}
    for (paramStatus = new ResolvableApiException(paramStatus);; paramStatus = new ApiException(paramStatus)) {
      return paramStatus;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/ApiExceptionUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package com.google.firebase.messaging;

import java.util.Locale;

public final class SendException
  extends Exception
{
  private final int errorCode;
  
  SendException(String paramString)
  {
    super(paramString);
    int j;
    if (paramString != null)
    {
      paramString = paramString.toLowerCase(Locale.US);
      j = -1;
    }
    switch (paramString.hashCode())
    {
    default: 
      switch (j)
      {
      default: 
        i = 0;
      }
      break;
    }
    for (;;)
    {
      this.errorCode = i;
      return;
      if (!paramString.equals("invalid_parameters")) {
        break;
      }
      j = 0;
      break;
      if (!paramString.equals("missing_to")) {
        break;
      }
      j = 1;
      break;
      if (!paramString.equals("messagetoobig")) {
        break;
      }
      j = 2;
      break;
      if (!paramString.equals("service_not_available")) {
        break;
      }
      j = 3;
      break;
      if (!paramString.equals("toomanymessages")) {
        break;
      }
      j = 4;
      break;
      i = 2;
      continue;
      i = 3;
      continue;
      i = 4;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/messaging/SendException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
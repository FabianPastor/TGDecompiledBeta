package com.google.android.gms.common.internal;

import android.content.Context;
import android.content.res.Resources;
import com.google.android.gms.common.R.string;
import javax.annotation.Nullable;

public class StringResourceValueReader
{
  private final Resources zzvb;
  private final String zzvc;
  
  public StringResourceValueReader(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext);
    this.zzvb = paramContext.getResources();
    this.zzvc = this.zzvb.getResourcePackageName(R.string.common_google_play_services_unknown_issue);
  }
  
  @Nullable
  public String getString(String paramString)
  {
    int i = this.zzvb.getIdentifier(paramString, "string", this.zzvc);
    if (i == 0) {}
    for (paramString = null;; paramString = this.zzvb.getString(i)) {
      return paramString;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/common/internal/StringResourceValueReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
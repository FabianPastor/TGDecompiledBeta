package com.google.android.gms.internal.measurement;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.android.gms.common.internal.Preconditions;

public final class zzft
{
  private boolean value;
  private final boolean zzakn;
  private boolean zzako;
  private final String zznt;
  
  public zzft(zzfr paramzzfr, String paramString, boolean paramBoolean)
  {
    Preconditions.checkNotEmpty(paramString);
    this.zznt = paramString;
    this.zzakn = true;
  }
  
  public final boolean get()
  {
    if (!this.zzako)
    {
      this.zzako = true;
      this.value = zzfr.zza(this.zzakp).getBoolean(this.zznt, this.zzakn);
    }
    return this.value;
  }
  
  public final void set(boolean paramBoolean)
  {
    SharedPreferences.Editor localEditor = zzfr.zza(this.zzakp).edit();
    localEditor.putBoolean(this.zznt, paramBoolean);
    localEditor.apply();
    this.value = paramBoolean;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzft.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package com.google.firebase.remoteconfig;

public class FirebaseRemoteConfigSettings
{
  private final boolean zzap;
  
  private FirebaseRemoteConfigSettings(Builder paramBuilder)
  {
    this.zzap = Builder.zza(paramBuilder);
  }
  
  public boolean isDeveloperModeEnabled()
  {
    return this.zzap;
  }
  
  public static class Builder
  {
    private boolean zzap = false;
    
    public FirebaseRemoteConfigSettings build()
    {
      return new FirebaseRemoteConfigSettings(this, null);
    }
    
    public Builder setDeveloperModeEnabled(boolean paramBoolean)
    {
      this.zzap = paramBoolean;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/remoteconfig/FirebaseRemoteConfigSettings.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
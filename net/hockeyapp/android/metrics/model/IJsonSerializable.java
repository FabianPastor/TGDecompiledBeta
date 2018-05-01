package net.hockeyapp.android.metrics.model;

import java.io.IOException;
import java.io.Writer;

public abstract interface IJsonSerializable
{
  public abstract void serialize(Writer paramWriter)
    throws IOException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/net/hockeyapp/android/metrics/model/IJsonSerializable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
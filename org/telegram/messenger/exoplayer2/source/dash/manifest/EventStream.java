package org.telegram.messenger.exoplayer2.source.dash.manifest;

import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;

public final class EventStream
{
  public final EventMessage[] events;
  public final long[] presentationTimesUs;
  public final String schemeIdUri;
  public final long timescale;
  public final String value;
  
  public EventStream(String paramString1, String paramString2, long paramLong, long[] paramArrayOfLong, EventMessage[] paramArrayOfEventMessage)
  {
    this.schemeIdUri = paramString1;
    this.value = paramString2;
    this.timescale = paramLong;
    this.presentationTimesUs = paramArrayOfLong;
    this.events = paramArrayOfEventMessage;
  }
  
  public String id()
  {
    return this.schemeIdUri + "/" + this.value;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/EventStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
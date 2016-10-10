package org.telegram.messenger.exoplayer.metadata;

import org.telegram.messenger.exoplayer.ParserException;

public abstract interface MetadataParser<T>
{
  public abstract boolean canParse(String paramString);
  
  public abstract T parse(byte[] paramArrayOfByte, int paramInt)
    throws ParserException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/metadata/MetadataParser.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
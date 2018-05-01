package org.telegram.messenger.exoplayer2.metadata;

public abstract interface MetadataDecoder
{
  public abstract Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
    throws MetadataDecoderException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/MetadataDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
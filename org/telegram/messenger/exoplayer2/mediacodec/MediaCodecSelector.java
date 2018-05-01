package org.telegram.messenger.exoplayer2.mediacodec;

public abstract interface MediaCodecSelector
{
  public static final MediaCodecSelector DEFAULT = new MediaCodecSelector()
  {
    public MediaCodecInfo getDecoderInfo(String paramAnonymousString, boolean paramAnonymousBoolean)
      throws MediaCodecUtil.DecoderQueryException
    {
      return MediaCodecUtil.getDecoderInfo(paramAnonymousString, paramAnonymousBoolean);
    }
    
    public MediaCodecInfo getPassthroughDecoderInfo()
      throws MediaCodecUtil.DecoderQueryException
    {
      return MediaCodecUtil.getPassthroughDecoderInfo();
    }
  };
  
  public abstract MediaCodecInfo getDecoderInfo(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException;
  
  public abstract MediaCodecInfo getPassthroughDecoderInfo()
    throws MediaCodecUtil.DecoderQueryException;
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/mediacodec/MediaCodecSelector.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
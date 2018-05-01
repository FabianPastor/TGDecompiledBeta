package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.text.cea.Cea608Decoder;
import org.telegram.messenger.exoplayer2.text.cea.Cea708Decoder;
import org.telegram.messenger.exoplayer2.text.dvb.DvbDecoder;
import org.telegram.messenger.exoplayer2.text.pgs.PgsDecoder;
import org.telegram.messenger.exoplayer2.text.ssa.SsaDecoder;
import org.telegram.messenger.exoplayer2.text.subrip.SubripDecoder;
import org.telegram.messenger.exoplayer2.text.ttml.TtmlDecoder;
import org.telegram.messenger.exoplayer2.text.tx3g.Tx3gDecoder;
import org.telegram.messenger.exoplayer2.text.webvtt.Mp4WebvttDecoder;
import org.telegram.messenger.exoplayer2.text.webvtt.WebvttDecoder;

public abstract interface SubtitleDecoderFactory
{
  public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory()
  {
    public SubtitleDecoder createDecoder(Format paramAnonymousFormat)
    {
      String str = paramAnonymousFormat.sampleMimeType;
      int i = -1;
      switch (str.hashCode())
      {
      }
      for (;;)
      {
        switch (i)
        {
        default: 
          throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
          if (str.equals("text/vtt"))
          {
            i = 0;
            continue;
            if (str.equals("text/x-ssa"))
            {
              i = 1;
              continue;
              if (str.equals("application/x-mp4-vtt"))
              {
                i = 2;
                continue;
                if (str.equals("application/ttml+xml"))
                {
                  i = 3;
                  continue;
                  if (str.equals("application/x-subrip"))
                  {
                    i = 4;
                    continue;
                    if (str.equals("application/x-quicktime-tx3g"))
                    {
                      i = 5;
                      continue;
                      if (str.equals("application/cea-608"))
                      {
                        i = 6;
                        continue;
                        if (str.equals("application/x-mp4-cea-608"))
                        {
                          i = 7;
                          continue;
                          if (str.equals("application/cea-708"))
                          {
                            i = 8;
                            continue;
                            if (str.equals("application/dvbsubs"))
                            {
                              i = 9;
                              continue;
                              if (str.equals("application/pgs")) {
                                i = 10;
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          break;
        }
      }
      paramAnonymousFormat = new WebvttDecoder();
      for (;;)
      {
        return paramAnonymousFormat;
        paramAnonymousFormat = new SsaDecoder(paramAnonymousFormat.initializationData);
        continue;
        paramAnonymousFormat = new Mp4WebvttDecoder();
        continue;
        paramAnonymousFormat = new TtmlDecoder();
        continue;
        paramAnonymousFormat = new SubripDecoder();
        continue;
        paramAnonymousFormat = new Tx3gDecoder(paramAnonymousFormat.initializationData);
        continue;
        paramAnonymousFormat = new Cea608Decoder(paramAnonymousFormat.sampleMimeType, paramAnonymousFormat.accessibilityChannel);
        continue;
        paramAnonymousFormat = new Cea708Decoder(paramAnonymousFormat.accessibilityChannel);
        continue;
        paramAnonymousFormat = new DvbDecoder(paramAnonymousFormat.initializationData);
        continue;
        paramAnonymousFormat = new PgsDecoder();
      }
    }
    
    public boolean supportsFormat(Format paramAnonymousFormat)
    {
      paramAnonymousFormat = paramAnonymousFormat.sampleMimeType;
      if (("text/vtt".equals(paramAnonymousFormat)) || ("text/x-ssa".equals(paramAnonymousFormat)) || ("application/ttml+xml".equals(paramAnonymousFormat)) || ("application/x-mp4-vtt".equals(paramAnonymousFormat)) || ("application/x-subrip".equals(paramAnonymousFormat)) || ("application/x-quicktime-tx3g".equals(paramAnonymousFormat)) || ("application/cea-608".equals(paramAnonymousFormat)) || ("application/x-mp4-cea-608".equals(paramAnonymousFormat)) || ("application/cea-708".equals(paramAnonymousFormat)) || ("application/dvbsubs".equals(paramAnonymousFormat)) || ("application/pgs".equals(paramAnonymousFormat))) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
  };
  
  public abstract SubtitleDecoder createDecoder(Format paramFormat);
  
  public abstract boolean supportsFormat(Format paramFormat);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/SubtitleDecoderFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
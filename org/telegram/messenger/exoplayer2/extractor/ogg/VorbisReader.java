package org.telegram.messenger.exoplayer2.extractor.ogg;

import java.io.IOException;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class VorbisReader
  extends StreamReader
{
  private VorbisUtil.CommentHeader commentHeader;
  private int previousPacketBlockSize;
  private boolean seenFirstAudioPacket;
  private VorbisUtil.VorbisIdHeader vorbisIdHeader;
  private VorbisSetup vorbisSetup;
  
  static void appendNumberOfSamples(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    paramParsableByteArray.setLimit(paramParsableByteArray.limit() + 4);
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 4)] = ((byte)(byte)(int)(paramLong & 0xFF));
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 3)] = ((byte)(byte)(int)(paramLong >>> 8 & 0xFF));
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 2)] = ((byte)(byte)(int)(paramLong >>> 16 & 0xFF));
    paramParsableByteArray.data[(paramParsableByteArray.limit() - 1)] = ((byte)(byte)(int)(paramLong >>> 24 & 0xFF));
  }
  
  private static int decodeBlockSize(byte paramByte, VorbisSetup paramVorbisSetup)
  {
    int i = readBits(paramByte, paramVorbisSetup.iLogModes, 1);
    if (!paramVorbisSetup.modes[i].blockFlag) {}
    for (i = paramVorbisSetup.idHeader.blockSize0;; i = paramVorbisSetup.idHeader.blockSize1) {
      return i;
    }
  }
  
  static int readBits(byte paramByte, int paramInt1, int paramInt2)
  {
    return paramByte >> paramInt2 & 255 >>> 8 - paramInt1;
  }
  
  public static boolean verifyBitstreamType(ParsableByteArray paramParsableByteArray)
  {
    try
    {
      bool = VorbisUtil.verifyVorbisHeaderCapturePattern(1, paramParsableByteArray, true);
      return bool;
    }
    catch (ParserException paramParsableByteArray)
    {
      for (;;)
      {
        boolean bool = false;
      }
    }
  }
  
  protected void onSeekEnd(long paramLong)
  {
    int i = 0;
    super.onSeekEnd(paramLong);
    if (paramLong != 0L) {}
    for (boolean bool = true;; bool = false)
    {
      this.seenFirstAudioPacket = bool;
      if (this.vorbisIdHeader != null) {
        i = this.vorbisIdHeader.blockSize0;
      }
      this.previousPacketBlockSize = i;
      return;
    }
  }
  
  protected long preparePayload(ParsableByteArray paramParsableByteArray)
  {
    int i = 0;
    if ((paramParsableByteArray.data[0] & 0x1) == 1) {}
    for (long l = -1L;; l = i)
    {
      return l;
      int j = decodeBlockSize(paramParsableByteArray.data[0], this.vorbisSetup);
      if (this.seenFirstAudioPacket) {
        i = (this.previousPacketBlockSize + j) / 4;
      }
      appendNumberOfSamples(paramParsableByteArray, i);
      this.seenFirstAudioPacket = true;
      this.previousPacketBlockSize = j;
    }
  }
  
  protected boolean readHeaders(ParsableByteArray paramParsableByteArray, long paramLong, StreamReader.SetupData paramSetupData)
    throws IOException, InterruptedException
  {
    boolean bool;
    if (this.vorbisSetup != null) {
      bool = false;
    }
    for (;;)
    {
      return bool;
      this.vorbisSetup = readSetupHeaders(paramParsableByteArray);
      if (this.vorbisSetup == null)
      {
        bool = true;
      }
      else
      {
        paramParsableByteArray = new ArrayList();
        paramParsableByteArray.add(this.vorbisSetup.idHeader.data);
        paramParsableByteArray.add(this.vorbisSetup.setupHeaderData);
        paramSetupData.format = Format.createAudioSampleFormat(null, "audio/vorbis", null, this.vorbisSetup.idHeader.bitrateNominal, -1, this.vorbisSetup.idHeader.channels, (int)this.vorbisSetup.idHeader.sampleRate, paramParsableByteArray, null, 0, null);
        bool = true;
      }
    }
  }
  
  VorbisSetup readSetupHeaders(ParsableByteArray paramParsableByteArray)
    throws IOException
  {
    byte[] arrayOfByte = null;
    if (this.vorbisIdHeader == null)
    {
      this.vorbisIdHeader = VorbisUtil.readVorbisIdentificationHeader(paramParsableByteArray);
      paramParsableByteArray = arrayOfByte;
    }
    for (;;)
    {
      return paramParsableByteArray;
      if (this.commentHeader == null)
      {
        this.commentHeader = VorbisUtil.readVorbisCommentHeader(paramParsableByteArray);
        paramParsableByteArray = arrayOfByte;
      }
      else
      {
        arrayOfByte = new byte[paramParsableByteArray.limit()];
        System.arraycopy(paramParsableByteArray.data, 0, arrayOfByte, 0, paramParsableByteArray.limit());
        paramParsableByteArray = VorbisUtil.readVorbisModes(paramParsableByteArray, this.vorbisIdHeader.channels);
        int i = VorbisUtil.iLog(paramParsableByteArray.length - 1);
        paramParsableByteArray = new VorbisSetup(this.vorbisIdHeader, this.commentHeader, arrayOfByte, paramParsableByteArray, i);
      }
    }
  }
  
  protected void reset(boolean paramBoolean)
  {
    super.reset(paramBoolean);
    if (paramBoolean)
    {
      this.vorbisSetup = null;
      this.vorbisIdHeader = null;
      this.commentHeader = null;
    }
    this.previousPacketBlockSize = 0;
    this.seenFirstAudioPacket = false;
  }
  
  static final class VorbisSetup
  {
    public final VorbisUtil.CommentHeader commentHeader;
    public final int iLogModes;
    public final VorbisUtil.VorbisIdHeader idHeader;
    public final VorbisUtil.Mode[] modes;
    public final byte[] setupHeaderData;
    
    public VorbisSetup(VorbisUtil.VorbisIdHeader paramVorbisIdHeader, VorbisUtil.CommentHeader paramCommentHeader, byte[] paramArrayOfByte, VorbisUtil.Mode[] paramArrayOfMode, int paramInt)
    {
      this.idHeader = paramVorbisIdHeader;
      this.commentHeader = paramCommentHeader;
      this.setupHeaderData = paramArrayOfByte;
      this.modes = paramArrayOfMode;
      this.iLogModes = paramInt;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ogg/VorbisReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.messenger.exoplayer2.decoder;

import android.annotation.TargetApi;
import android.media.MediaCodec.CryptoInfo;
import android.media.MediaCodec.CryptoInfo.Pattern;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CryptoInfo
{
  public int clearBlocks;
  public int encryptedBlocks;
  private final MediaCodec.CryptoInfo frameworkCryptoInfo;
  public byte[] iv;
  public byte[] key;
  public int mode;
  public int[] numBytesOfClearData;
  public int[] numBytesOfEncryptedData;
  public int numSubSamples;
  private final PatternHolderV24 patternHolder;
  
  public CryptoInfo()
  {
    if (Util.SDK_INT >= 16) {}
    for (Object localObject2 = newFrameworkCryptoInfoV16();; localObject2 = null)
    {
      this.frameworkCryptoInfo = ((MediaCodec.CryptoInfo)localObject2);
      localObject2 = localObject1;
      if (Util.SDK_INT >= 24) {
        localObject2 = new PatternHolderV24(this.frameworkCryptoInfo, null);
      }
      this.patternHolder = ((PatternHolderV24)localObject2);
      return;
    }
  }
  
  @TargetApi(16)
  private MediaCodec.CryptoInfo newFrameworkCryptoInfoV16()
  {
    return new MediaCodec.CryptoInfo();
  }
  
  @TargetApi(16)
  private void updateFrameworkCryptoInfoV16()
  {
    this.frameworkCryptoInfo.numSubSamples = this.numSubSamples;
    this.frameworkCryptoInfo.numBytesOfClearData = this.numBytesOfClearData;
    this.frameworkCryptoInfo.numBytesOfEncryptedData = this.numBytesOfEncryptedData;
    this.frameworkCryptoInfo.key = this.key;
    this.frameworkCryptoInfo.iv = this.iv;
    this.frameworkCryptoInfo.mode = this.mode;
    if (Util.SDK_INT >= 24) {
      this.patternHolder.set(this.encryptedBlocks, this.clearBlocks);
    }
  }
  
  @TargetApi(16)
  public MediaCodec.CryptoInfo getFrameworkCryptoInfoV16()
  {
    return this.frameworkCryptoInfo;
  }
  
  public void set(int paramInt1, int[] paramArrayOfInt1, int[] paramArrayOfInt2, byte[] paramArrayOfByte1, byte[] paramArrayOfByte2, int paramInt2, int paramInt3, int paramInt4)
  {
    this.numSubSamples = paramInt1;
    this.numBytesOfClearData = paramArrayOfInt1;
    this.numBytesOfEncryptedData = paramArrayOfInt2;
    this.key = paramArrayOfByte1;
    this.iv = paramArrayOfByte2;
    this.mode = paramInt2;
    this.encryptedBlocks = paramInt3;
    this.clearBlocks = paramInt4;
    if (Util.SDK_INT >= 16) {
      updateFrameworkCryptoInfoV16();
    }
  }
  
  @TargetApi(24)
  private static final class PatternHolderV24
  {
    private final MediaCodec.CryptoInfo frameworkCryptoInfo;
    private final MediaCodec.CryptoInfo.Pattern pattern;
    
    private PatternHolderV24(MediaCodec.CryptoInfo paramCryptoInfo)
    {
      this.frameworkCryptoInfo = paramCryptoInfo;
      this.pattern = new MediaCodec.CryptoInfo.Pattern(0, 0);
    }
    
    private void set(int paramInt1, int paramInt2)
    {
      this.pattern.set(paramInt1, paramInt2);
      this.frameworkCryptoInfo.setPattern(this.pattern);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/decoder/CryptoInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
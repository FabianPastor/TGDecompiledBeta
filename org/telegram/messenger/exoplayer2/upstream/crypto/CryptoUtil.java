package org.telegram.messenger.exoplayer2.upstream.crypto;

final class CryptoUtil
{
  public static long getFNV64Hash(String paramString)
  {
    long l1;
    if (paramString == null)
    {
      l1 = 0L;
      return l1;
    }
    long l2 = 0L;
    for (int i = 0;; i++)
    {
      l1 = l2;
      if (i >= paramString.length()) {
        break;
      }
      l2 ^= paramString.charAt(i);
      l2 += (l2 << 1) + (l2 << 4) + (l2 << 5) + (l2 << 7) + (l2 << 8) + (l2 << 40);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/upstream/crypto/CryptoUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
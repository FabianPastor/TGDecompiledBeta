package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class PsshAtomUtil
{
  private static final String TAG = "PsshAtomUtil";
  
  public static byte[] buildPsshAtom(UUID paramUUID, byte[] paramArrayOfByte)
  {
    return buildPsshAtom(paramUUID, null, paramArrayOfByte);
  }
  
  public static byte[] buildPsshAtom(UUID paramUUID, UUID[] paramArrayOfUUID, byte[] paramArrayOfByte)
  {
    int i = 0;
    int j;
    int k;
    label17:
    ByteBuffer localByteBuffer;
    if (paramArrayOfUUID != null)
    {
      j = 1;
      if (paramArrayOfByte == null) {
        break label170;
      }
      k = paramArrayOfByte.length;
      int m = k + 32;
      n = m;
      if (j != 0) {
        n = m + (paramArrayOfUUID.length * 16 + 4);
      }
      localByteBuffer = ByteBuffer.allocate(n);
      localByteBuffer.putInt(n);
      localByteBuffer.putInt(Atom.TYPE_pssh);
      if (j == 0) {
        break label176;
      }
    }
    label170:
    label176:
    for (int n = 16777216;; n = 0)
    {
      localByteBuffer.putInt(n);
      localByteBuffer.putLong(paramUUID.getMostSignificantBits());
      localByteBuffer.putLong(paramUUID.getLeastSignificantBits());
      if (j == 0) {
        break label182;
      }
      localByteBuffer.putInt(paramArrayOfUUID.length);
      n = paramArrayOfUUID.length;
      for (j = i; j < n; j++)
      {
        paramUUID = paramArrayOfUUID[j];
        localByteBuffer.putLong(paramUUID.getMostSignificantBits());
        localByteBuffer.putLong(paramUUID.getLeastSignificantBits());
      }
      j = 0;
      break;
      k = 0;
      break label17;
    }
    label182:
    if (k != 0)
    {
      localByteBuffer.putInt(paramArrayOfByte.length);
      localByteBuffer.put(paramArrayOfByte);
    }
    return localByteBuffer.array();
  }
  
  private static PsshAtom parsePsshAtom(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte);
    if (paramArrayOfByte.limit() < 32) {
      paramArrayOfByte = null;
    }
    for (;;)
    {
      return paramArrayOfByte;
      paramArrayOfByte.setPosition(0);
      if (paramArrayOfByte.readInt() != paramArrayOfByte.bytesLeft() + 4)
      {
        paramArrayOfByte = null;
      }
      else if (paramArrayOfByte.readInt() != Atom.TYPE_pssh)
      {
        paramArrayOfByte = null;
      }
      else
      {
        int i = Atom.parseFullAtomVersion(paramArrayOfByte.readInt());
        if (i > 1)
        {
          Log.w("PsshAtomUtil", "Unsupported pssh version: " + i);
          paramArrayOfByte = null;
        }
        else
        {
          UUID localUUID = new UUID(paramArrayOfByte.readLong(), paramArrayOfByte.readLong());
          if (i == 1) {
            paramArrayOfByte.skipBytes(paramArrayOfByte.readUnsignedIntToInt() * 16);
          }
          int j = paramArrayOfByte.readUnsignedIntToInt();
          if (j != paramArrayOfByte.bytesLeft())
          {
            paramArrayOfByte = null;
          }
          else
          {
            byte[] arrayOfByte = new byte[j];
            paramArrayOfByte.readBytes(arrayOfByte, 0, j);
            paramArrayOfByte = new PsshAtom(localUUID, i, arrayOfByte);
          }
        }
      }
    }
  }
  
  public static byte[] parseSchemeSpecificData(byte[] paramArrayOfByte, UUID paramUUID)
  {
    Object localObject = null;
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      paramArrayOfByte = (byte[])localObject;
    }
    for (;;)
    {
      return paramArrayOfByte;
      if ((paramUUID != null) && (!paramUUID.equals(paramArrayOfByte.uuid)))
      {
        Log.w("PsshAtomUtil", "UUID mismatch. Expected: " + paramUUID + ", got: " + paramArrayOfByte.uuid + ".");
        paramArrayOfByte = (byte[])localObject;
      }
      else
      {
        paramArrayOfByte = paramArrayOfByte.schemeData;
      }
    }
  }
  
  public static UUID parseUuid(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {}
    for (paramArrayOfByte = null;; paramArrayOfByte = paramArrayOfByte.uuid) {
      return paramArrayOfByte;
    }
  }
  
  public static int parseVersion(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {}
    for (int i = -1;; i = paramArrayOfByte.version) {
      return i;
    }
  }
  
  private static class PsshAtom
  {
    private final byte[] schemeData;
    private final UUID uuid;
    private final int version;
    
    public PsshAtom(UUID paramUUID, int paramInt, byte[] paramArrayOfByte)
    {
      this.uuid = paramUUID;
      this.version = paramInt;
      this.schemeData = paramArrayOfByte;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/PsshAtomUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.messenger.exoplayer.extractor.mp4;

import android.util.Log;
import android.util.Pair;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;

public final class PsshAtomUtil
{
  private static final String TAG = "PsshAtomUtil";
  
  public static byte[] buildPsshAtom(UUID paramUUID, byte[] paramArrayOfByte)
  {
    int i = paramArrayOfByte.length + 32;
    ByteBuffer localByteBuffer = ByteBuffer.allocate(i);
    localByteBuffer.putInt(i);
    localByteBuffer.putInt(Atom.TYPE_pssh);
    localByteBuffer.putInt(0);
    localByteBuffer.putLong(paramUUID.getMostSignificantBits());
    localByteBuffer.putLong(paramUUID.getLeastSignificantBits());
    localByteBuffer.putInt(paramArrayOfByte.length);
    localByteBuffer.put(paramArrayOfByte);
    return localByteBuffer.array();
  }
  
  private static Pair<UUID, byte[]> parsePsshAtom(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = new ParsableByteArray(paramArrayOfByte);
    if (paramArrayOfByte.limit() < 32) {
      return null;
    }
    paramArrayOfByte.setPosition(0);
    if (paramArrayOfByte.readInt() != paramArrayOfByte.bytesLeft() + 4) {
      return null;
    }
    if (paramArrayOfByte.readInt() != Atom.TYPE_pssh) {
      return null;
    }
    int i = Atom.parseFullAtomVersion(paramArrayOfByte.readInt());
    if (i > 1)
    {
      Log.w("PsshAtomUtil", "Unsupported pssh version: " + i);
      return null;
    }
    UUID localUUID = new UUID(paramArrayOfByte.readLong(), paramArrayOfByte.readLong());
    if (i == 1) {
      paramArrayOfByte.skipBytes(paramArrayOfByte.readUnsignedIntToInt() * 16);
    }
    i = paramArrayOfByte.readUnsignedIntToInt();
    if (i != paramArrayOfByte.bytesLeft()) {
      return null;
    }
    byte[] arrayOfByte = new byte[i];
    paramArrayOfByte.readBytes(arrayOfByte, 0, i);
    return Pair.create(localUUID, arrayOfByte);
  }
  
  public static byte[] parseSchemeSpecificData(byte[] paramArrayOfByte, UUID paramUUID)
  {
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      return null;
    }
    if ((paramUUID != null) && (!paramUUID.equals(paramArrayOfByte.first)))
    {
      Log.w("PsshAtomUtil", "UUID mismatch. Expected: " + paramUUID + ", got: " + paramArrayOfByte.first + ".");
      return null;
    }
    return (byte[])paramArrayOfByte.second;
  }
  
  public static UUID parseUuid(byte[] paramArrayOfByte)
  {
    paramArrayOfByte = parsePsshAtom(paramArrayOfByte);
    if (paramArrayOfByte == null) {
      return null;
    }
    return (UUID)paramArrayOfByte.first;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/mp4/PsshAtomUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
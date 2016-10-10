package org.telegram.tgnet;

public abstract class AbstractSerializedData
{
  public abstract int getPosition();
  
  public abstract int length();
  
  public abstract boolean readBool(boolean paramBoolean);
  
  public abstract byte[] readByteArray(boolean paramBoolean);
  
  public abstract NativeByteBuffer readByteBuffer(boolean paramBoolean);
  
  public abstract void readBytes(byte[] paramArrayOfByte, boolean paramBoolean);
  
  public abstract byte[] readData(int paramInt, boolean paramBoolean);
  
  public abstract double readDouble(boolean paramBoolean);
  
  public abstract int readInt32(boolean paramBoolean);
  
  public abstract long readInt64(boolean paramBoolean);
  
  public abstract String readString(boolean paramBoolean);
  
  public abstract void skip(int paramInt);
  
  public abstract void writeBool(boolean paramBoolean);
  
  public abstract void writeByte(byte paramByte);
  
  public abstract void writeByte(int paramInt);
  
  public abstract void writeByteArray(byte[] paramArrayOfByte);
  
  public abstract void writeByteArray(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract void writeByteBuffer(NativeByteBuffer paramNativeByteBuffer);
  
  public abstract void writeBytes(byte[] paramArrayOfByte);
  
  public abstract void writeBytes(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  
  public abstract void writeDouble(double paramDouble);
  
  public abstract void writeInt32(int paramInt);
  
  public abstract void writeInt64(long paramLong);
  
  public abstract void writeString(String paramString);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/AbstractSerializedData.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
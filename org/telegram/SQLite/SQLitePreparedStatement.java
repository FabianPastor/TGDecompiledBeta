package org.telegram.SQLite;

import java.nio.ByteBuffer;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class SQLitePreparedStatement
{
  private boolean finalizeAfterQuery = false;
  private boolean isFinalized = false;
  private long sqliteStatementHandle;
  
  public SQLitePreparedStatement(SQLiteDatabase paramSQLiteDatabase, String paramString, boolean paramBoolean)
    throws SQLiteException
  {
    this.finalizeAfterQuery = paramBoolean;
    this.sqliteStatementHandle = prepare(paramSQLiteDatabase.getSQLiteHandle(), paramString);
  }
  
  public void bindByteBuffer(int paramInt, ByteBuffer paramByteBuffer)
    throws SQLiteException
  {
    bindByteBuffer(this.sqliteStatementHandle, paramInt, paramByteBuffer, paramByteBuffer.limit());
  }
  
  public void bindByteBuffer(int paramInt, NativeByteBuffer paramNativeByteBuffer)
    throws SQLiteException
  {
    bindByteBuffer(this.sqliteStatementHandle, paramInt, paramNativeByteBuffer.buffer, paramNativeByteBuffer.limit());
  }
  
  native void bindByteBuffer(long paramLong, int paramInt1, ByteBuffer paramByteBuffer, int paramInt2)
    throws SQLiteException;
  
  public void bindDouble(int paramInt, double paramDouble)
    throws SQLiteException
  {
    bindDouble(this.sqliteStatementHandle, paramInt, paramDouble);
  }
  
  native void bindDouble(long paramLong, int paramInt, double paramDouble)
    throws SQLiteException;
  
  native void bindInt(long paramLong, int paramInt1, int paramInt2)
    throws SQLiteException;
  
  public void bindInteger(int paramInt1, int paramInt2)
    throws SQLiteException
  {
    bindInt(this.sqliteStatementHandle, paramInt1, paramInt2);
  }
  
  public void bindLong(int paramInt, long paramLong)
    throws SQLiteException
  {
    bindLong(this.sqliteStatementHandle, paramInt, paramLong);
  }
  
  native void bindLong(long paramLong1, int paramInt, long paramLong2)
    throws SQLiteException;
  
  public void bindNull(int paramInt)
    throws SQLiteException
  {
    bindNull(this.sqliteStatementHandle, paramInt);
  }
  
  native void bindNull(long paramLong, int paramInt)
    throws SQLiteException;
  
  public void bindString(int paramInt, String paramString)
    throws SQLiteException
  {
    bindString(this.sqliteStatementHandle, paramInt, paramString);
  }
  
  native void bindString(long paramLong, int paramInt, String paramString)
    throws SQLiteException;
  
  void checkFinalized()
    throws SQLiteException
  {
    if (this.isFinalized) {
      throw new SQLiteException("Prepared query finalized");
    }
  }
  
  public void dispose()
  {
    if (this.finalizeAfterQuery) {
      finalizeQuery();
    }
  }
  
  native void finalize(long paramLong)
    throws SQLiteException;
  
  public void finalizeQuery()
  {
    if (this.isFinalized) {}
    for (;;)
    {
      return;
      try
      {
        this.isFinalized = true;
        finalize(this.sqliteStatementHandle);
      }
      catch (SQLiteException localSQLiteException) {}
      if (BuildVars.LOGS_ENABLED) {
        FileLog.e(localSQLiteException.getMessage(), localSQLiteException);
      }
    }
  }
  
  public long getStatementHandle()
  {
    return this.sqliteStatementHandle;
  }
  
  native long prepare(long paramLong, String paramString)
    throws SQLiteException;
  
  public SQLiteCursor query(Object[] paramArrayOfObject)
    throws SQLiteException
  {
    if (paramArrayOfObject == null) {
      throw new IllegalArgumentException();
    }
    checkFinalized();
    reset(this.sqliteStatementHandle);
    int i = 1;
    int j = paramArrayOfObject.length;
    int k = 0;
    if (k < j)
    {
      Object localObject = paramArrayOfObject[k];
      if (localObject == null) {
        bindNull(this.sqliteStatementHandle, i);
      }
      for (;;)
      {
        i++;
        k++;
        break;
        if ((localObject instanceof Integer))
        {
          bindInt(this.sqliteStatementHandle, i, ((Integer)localObject).intValue());
        }
        else if ((localObject instanceof Double))
        {
          bindDouble(this.sqliteStatementHandle, i, ((Double)localObject).doubleValue());
        }
        else
        {
          if (!(localObject instanceof String)) {
            break label148;
          }
          bindString(this.sqliteStatementHandle, i, (String)localObject);
        }
      }
      label148:
      throw new IllegalArgumentException();
    }
    return new SQLiteCursor(this);
  }
  
  public void requery()
    throws SQLiteException
  {
    checkFinalized();
    reset(this.sqliteStatementHandle);
  }
  
  native void reset(long paramLong)
    throws SQLiteException;
  
  public int step()
    throws SQLiteException
  {
    return step(this.sqliteStatementHandle);
  }
  
  native int step(long paramLong)
    throws SQLiteException;
  
  public SQLitePreparedStatement stepThis()
    throws SQLiteException
  {
    step(this.sqliteStatementHandle);
    return this;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/SQLite/SQLitePreparedStatement.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
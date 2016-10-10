package org.telegram.SQLite;

import java.nio.ByteBuffer;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class SQLitePreparedStatement
{
  private boolean finalizeAfterQuery = false;
  private boolean isFinalized = false;
  private int sqliteStatementHandle;
  
  public SQLitePreparedStatement(SQLiteDatabase paramSQLiteDatabase, String paramString, boolean paramBoolean)
    throws SQLiteException
  {
    this.finalizeAfterQuery = paramBoolean;
    this.sqliteStatementHandle = prepare(paramSQLiteDatabase.getSQLiteHandle(), paramString);
  }
  
  native void bindByteBuffer(int paramInt1, int paramInt2, ByteBuffer paramByteBuffer, int paramInt3)
    throws SQLiteException;
  
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
  
  public void bindDouble(int paramInt, double paramDouble)
    throws SQLiteException
  {
    bindDouble(this.sqliteStatementHandle, paramInt, paramDouble);
  }
  
  native void bindDouble(int paramInt1, int paramInt2, double paramDouble)
    throws SQLiteException;
  
  native void bindInt(int paramInt1, int paramInt2, int paramInt3)
    throws SQLiteException;
  
  public void bindInteger(int paramInt1, int paramInt2)
    throws SQLiteException
  {
    bindInt(this.sqliteStatementHandle, paramInt1, paramInt2);
  }
  
  native void bindLong(int paramInt1, int paramInt2, long paramLong)
    throws SQLiteException;
  
  public void bindLong(int paramInt, long paramLong)
    throws SQLiteException
  {
    bindLong(this.sqliteStatementHandle, paramInt, paramLong);
  }
  
  public void bindNull(int paramInt)
    throws SQLiteException
  {
    bindNull(this.sqliteStatementHandle, paramInt);
  }
  
  native void bindNull(int paramInt1, int paramInt2)
    throws SQLiteException;
  
  native void bindString(int paramInt1, int paramInt2, String paramString)
    throws SQLiteException;
  
  public void bindString(int paramInt, String paramString)
    throws SQLiteException
  {
    bindString(this.sqliteStatementHandle, paramInt, paramString);
  }
  
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
  
  native void finalize(int paramInt)
    throws SQLiteException;
  
  public void finalizeQuery()
  {
    if (this.isFinalized) {
      return;
    }
    try
    {
      this.isFinalized = true;
      finalize(this.sqliteStatementHandle);
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      FileLog.e("tmessages", localSQLiteException.getMessage(), localSQLiteException);
    }
  }
  
  public int getStatementHandle()
  {
    return this.sqliteStatementHandle;
  }
  
  native int prepare(int paramInt, String paramString)
    throws SQLiteException;
  
  public SQLiteCursor query(Object[] paramArrayOfObject)
    throws SQLiteException
  {
    if (paramArrayOfObject == null) {
      throw new IllegalArgumentException();
    }
    checkFinalized();
    reset(this.sqliteStatementHandle);
    int j = 1;
    int k = paramArrayOfObject.length;
    int i = 0;
    if (i < k)
    {
      Object localObject = paramArrayOfObject[i];
      if (localObject == null) {
        bindNull(this.sqliteStatementHandle, j);
      }
      for (;;)
      {
        j += 1;
        i += 1;
        break;
        if ((localObject instanceof Integer))
        {
          bindInt(this.sqliteStatementHandle, j, ((Integer)localObject).intValue());
        }
        else if ((localObject instanceof Double))
        {
          bindDouble(this.sqliteStatementHandle, j, ((Double)localObject).doubleValue());
        }
        else
        {
          if (!(localObject instanceof String)) {
            break label149;
          }
          bindString(this.sqliteStatementHandle, j, (String)localObject);
        }
      }
      label149:
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
  
  native void reset(int paramInt)
    throws SQLiteException;
  
  public int step()
    throws SQLiteException
  {
    return step(this.sqliteStatementHandle);
  }
  
  native int step(int paramInt)
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
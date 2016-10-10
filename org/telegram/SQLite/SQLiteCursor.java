package org.telegram.SQLite;

import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class SQLiteCursor
{
  public static final int FIELD_TYPE_BYTEARRAY = 4;
  public static final int FIELD_TYPE_FLOAT = 2;
  public static final int FIELD_TYPE_INT = 1;
  public static final int FIELD_TYPE_NULL = 5;
  public static final int FIELD_TYPE_STRING = 3;
  boolean inRow = false;
  SQLitePreparedStatement preparedStatement;
  
  public SQLiteCursor(SQLitePreparedStatement paramSQLitePreparedStatement)
  {
    this.preparedStatement = paramSQLitePreparedStatement;
  }
  
  public byte[] byteArrayValue(int paramInt)
    throws SQLiteException
  {
    checkRow();
    return columnByteArrayValue(this.preparedStatement.getStatementHandle(), paramInt);
  }
  
  public NativeByteBuffer byteBufferValue(int paramInt)
    throws SQLiteException
  {
    checkRow();
    paramInt = columnByteBufferValue(this.preparedStatement.getStatementHandle(), paramInt);
    if (paramInt != 0) {
      return NativeByteBuffer.wrap(paramInt);
    }
    return null;
  }
  
  void checkRow()
    throws SQLiteException
  {
    if (!this.inRow) {
      throw new SQLiteException("You must call next before");
    }
  }
  
  native byte[] columnByteArrayValue(int paramInt1, int paramInt2);
  
  native int columnByteBufferValue(int paramInt1, int paramInt2);
  
  native double columnDoubleValue(int paramInt1, int paramInt2);
  
  native int columnIntValue(int paramInt1, int paramInt2);
  
  native int columnIsNull(int paramInt1, int paramInt2);
  
  native long columnLongValue(int paramInt1, int paramInt2);
  
  native String columnStringValue(int paramInt1, int paramInt2);
  
  native int columnType(int paramInt1, int paramInt2);
  
  public void dispose()
  {
    this.preparedStatement.dispose();
  }
  
  public double doubleValue(int paramInt)
    throws SQLiteException
  {
    checkRow();
    return columnDoubleValue(this.preparedStatement.getStatementHandle(), paramInt);
  }
  
  public int getStatementHandle()
  {
    return this.preparedStatement.getStatementHandle();
  }
  
  public int getTypeOf(int paramInt)
    throws SQLiteException
  {
    checkRow();
    return columnType(this.preparedStatement.getStatementHandle(), paramInt);
  }
  
  public int intValue(int paramInt)
    throws SQLiteException
  {
    checkRow();
    return columnIntValue(this.preparedStatement.getStatementHandle(), paramInt);
  }
  
  public boolean isNull(int paramInt)
    throws SQLiteException
  {
    checkRow();
    return columnIsNull(this.preparedStatement.getStatementHandle(), paramInt) == 1;
  }
  
  public long longValue(int paramInt)
    throws SQLiteException
  {
    checkRow();
    return columnLongValue(this.preparedStatement.getStatementHandle(), paramInt);
  }
  
  public boolean next()
    throws SQLiteException
  {
    int i = this.preparedStatement.step(this.preparedStatement.getStatementHandle());
    int j = i;
    if (i == -1)
    {
      j = 6;
      for (;;)
      {
        int m = j - 1;
        int k = i;
        if (j != 0) {}
        try
        {
          FileLog.e("tmessages", "sqlite busy, waiting...");
          Thread.sleep(500L);
          j = this.preparedStatement.step();
          i = j;
          if (i == 0)
          {
            k = i;
            j = k;
            if (k != -1) {
              break;
            }
            throw new SQLiteException("sqlite busy");
          }
          j = m;
        }
        catch (Exception localException)
        {
          FileLog.e("tmessages", localException);
          j = m;
        }
      }
    }
    if (j == 0) {}
    for (boolean bool = true;; bool = false)
    {
      this.inRow = bool;
      return this.inRow;
    }
  }
  
  public String stringValue(int paramInt)
    throws SQLiteException
  {
    checkRow();
    return columnStringValue(this.preparedStatement.getStatementHandle(), paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/SQLite/SQLiteCursor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
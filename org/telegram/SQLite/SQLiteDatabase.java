package org.telegram.SQLite;

import java.io.File;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;

public class SQLiteDatabase
{
  private boolean inTransaction = false;
  private boolean isOpen = false;
  private final long sqliteHandle = opendb(paramString, ApplicationLoader.getFilesDirFixed().getPath());
  
  public SQLiteDatabase(String paramString)
    throws SQLiteException
  {}
  
  public void beginTransaction()
    throws SQLiteException
  {
    if (this.inTransaction) {
      throw new SQLiteException("database already in transaction");
    }
    this.inTransaction = true;
    beginTransaction(this.sqliteHandle);
  }
  
  native void beginTransaction(long paramLong);
  
  void checkOpened()
    throws SQLiteException
  {
    if (!this.isOpen) {
      throw new SQLiteException("Database closed");
    }
  }
  
  public void close()
  {
    if (this.isOpen) {}
    try
    {
      commitTransaction();
      closedb(this.sqliteHandle);
      this.isOpen = false;
      return;
    }
    catch (SQLiteException localSQLiteException)
    {
      for (;;)
      {
        if (BuildVars.LOGS_ENABLED) {
          FileLog.e(localSQLiteException.getMessage(), localSQLiteException);
        }
      }
    }
  }
  
  native void closedb(long paramLong)
    throws SQLiteException;
  
  public void commitTransaction()
  {
    if (!this.inTransaction) {}
    for (;;)
    {
      return;
      this.inTransaction = false;
      commitTransaction(this.sqliteHandle);
    }
  }
  
  native void commitTransaction(long paramLong);
  
  public SQLitePreparedStatement executeFast(String paramString)
    throws SQLiteException
  {
    return new SQLitePreparedStatement(this, paramString, true);
  }
  
  /* Error */
  public Integer executeInt(String paramString, Object... paramVarArgs)
    throws SQLiteException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokevirtual 85	org/telegram/SQLite/SQLiteDatabase:checkOpened	()V
    //   4: aload_0
    //   5: aload_1
    //   6: aload_2
    //   7: invokevirtual 89	org/telegram/SQLite/SQLiteDatabase:queryFinalized	(Ljava/lang/String;[Ljava/lang/Object;)Lorg/telegram/SQLite/SQLiteCursor;
    //   10: astore_2
    //   11: aload_2
    //   12: invokevirtual 95	org/telegram/SQLite/SQLiteCursor:next	()Z
    //   15: istore_3
    //   16: iload_3
    //   17: ifne +11 -> 28
    //   20: aconst_null
    //   21: astore_1
    //   22: aload_2
    //   23: invokevirtual 98	org/telegram/SQLite/SQLiteCursor:dispose	()V
    //   26: aload_1
    //   27: areturn
    //   28: aload_2
    //   29: iconst_0
    //   30: invokevirtual 102	org/telegram/SQLite/SQLiteCursor:intValue	(I)I
    //   33: istore 4
    //   35: iload 4
    //   37: invokestatic 108	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   40: astore_1
    //   41: aload_2
    //   42: invokevirtual 98	org/telegram/SQLite/SQLiteCursor:dispose	()V
    //   45: goto -19 -> 26
    //   48: astore_1
    //   49: aload_2
    //   50: invokevirtual 98	org/telegram/SQLite/SQLiteCursor:dispose	()V
    //   53: aload_1
    //   54: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	55	0	this	SQLiteDatabase
    //   0	55	1	paramString	String
    //   0	55	2	paramVarArgs	Object[]
    //   15	2	3	bool	boolean
    //   33	3	4	i	int
    // Exception table:
    //   from	to	target	type
    //   11	16	48	finally
    //   28	35	48	finally
  }
  
  public void finalize()
    throws Throwable
  {
    super.finalize();
    close();
  }
  
  public long getSQLiteHandle()
  {
    return this.sqliteHandle;
  }
  
  native long opendb(String paramString1, String paramString2)
    throws SQLiteException;
  
  public SQLiteCursor queryFinalized(String paramString, Object... paramVarArgs)
    throws SQLiteException
  {
    checkOpened();
    return new SQLitePreparedStatement(this, paramString, true).query(paramVarArgs);
  }
  
  public boolean tableExists(String paramString)
    throws SQLiteException
  {
    boolean bool = true;
    checkOpened();
    if (executeInt("SELECT rowid FROM sqlite_master WHERE type='table' AND name=?;", new Object[] { paramString }) != null) {}
    for (;;)
    {
      return bool;
      bool = false;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/SQLite/SQLiteDatabase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
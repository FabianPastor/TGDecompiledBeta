package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.File;
import java.io.FileDescriptor;
import java.io.RandomAccessFile;

public class StatsController
{
  private static volatile StatsController[] Instance = new StatsController[3];
  private static final int TYPES_COUNT = 7;
  public static final int TYPE_AUDIOS = 3;
  public static final int TYPE_CALLS = 0;
  public static final int TYPE_FILES = 5;
  public static final int TYPE_MESSAGES = 1;
  public static final int TYPE_MOBILE = 0;
  public static final int TYPE_PHOTOS = 4;
  public static final int TYPE_ROAMING = 2;
  public static final int TYPE_TOTAL = 6;
  public static final int TYPE_VIDEOS = 2;
  public static final int TYPE_WIFI = 1;
  private static final ThreadLocal<Long> lastStatsSaveTime;
  private static DispatchQueue statsSaveQueue = new DispatchQueue("statsSaveQueue");
  private byte[] buffer = new byte[8];
  private int[] callsTotalTime = new int[3];
  private long lastInternalStatsSaveTime;
  private long[][] receivedBytes = new long[3][7];
  private int[][] receivedItems = new int[3][7];
  private long[] resetStatsDate = new long[3];
  private Runnable saveRunnable = new Runnable()
  {
    public void run()
    {
      long l = System.currentTimeMillis();
      if (Math.abs(l - StatsController.this.lastInternalStatsSaveTime) < 2000L) {}
      for (;;)
      {
        return;
        StatsController.access$002(StatsController.this, l);
        try
        {
          StatsController.this.statsFile.seek(0L);
          for (int i = 0; i < 3; i++)
          {
            for (int j = 0; j < 7; j++)
            {
              StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.sentBytes[i][j]), 0, 8);
              StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.receivedBytes[i][j]), 0, 8);
              StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.sentItems[i][j]), 0, 4);
              StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.receivedItems[i][j]), 0, 4);
            }
            StatsController.this.statsFile.write(StatsController.this.intToBytes(StatsController.this.callsTotalTime[i]), 0, 4);
            StatsController.this.statsFile.write(StatsController.this.longToBytes(StatsController.this.resetStatsDate[i]), 0, 8);
          }
          StatsController.this.statsFile.getFD().sync();
        }
        catch (Exception localException) {}
      }
    }
  };
  private long[][] sentBytes = new long[3][7];
  private int[][] sentItems = new int[3][7];
  private RandomAccessFile statsFile;
  
  static
  {
    lastStatsSaveTime = new ThreadLocal()
    {
      protected Long initialValue()
      {
        return Long.valueOf(System.currentTimeMillis() - 1000L);
      }
    };
  }
  
  private StatsController(int paramInt)
  {
    Object localObject = ApplicationLoader.getFilesDirFixed();
    if (paramInt != 0)
    {
      localObject = new File(ApplicationLoader.getFilesDirFixed(), "account" + paramInt + "/");
      ((File)localObject).mkdirs();
    }
    i = 1;
    try
    {
      RandomAccessFile localRandomAccessFile = new java/io/RandomAccessFile;
      File localFile = new java/io/File;
      localFile.<init>((File)localObject, "stats2.dat");
      localRandomAccessFile.<init>(localFile, "rw");
      this.statsFile = localRandomAccessFile;
      j = i;
      if (this.statsFile.length() > 0L)
      {
        k = 0;
        for (j = 0; j < 3; j++)
        {
          for (int m = 0; m < 7; m++)
          {
            this.statsFile.readFully(this.buffer, 0, 8);
            this.sentBytes[j][m] = bytesToLong(this.buffer);
            this.statsFile.readFully(this.buffer, 0, 8);
            this.receivedBytes[j][m] = bytesToLong(this.buffer);
            this.statsFile.readFully(this.buffer, 0, 4);
            this.sentItems[j][m] = bytesToInt(this.buffer);
            this.statsFile.readFully(this.buffer, 0, 4);
            this.receivedItems[j][m] = bytesToInt(this.buffer);
          }
          this.statsFile.readFully(this.buffer, 0, 4);
          this.callsTotalTime[j] = bytesToInt(this.buffer);
          this.statsFile.readFully(this.buffer, 0, 8);
          this.resetStatsDate[j] = bytesToLong(this.buffer);
          if (this.resetStatsDate[j] == 0L)
          {
            k = 1;
            this.resetStatsDate[j] = System.currentTimeMillis();
          }
        }
        if (k != 0) {
          saveStats();
        }
        j = 0;
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        int k;
        label782:
        int j = i;
      }
    }
    if (j != 0)
    {
      if (paramInt == 0)
      {
        localObject = ApplicationLoader.applicationContext.getSharedPreferences("stats", 0);
        j = 0;
      }
      for (paramInt = 0;; paramInt++)
      {
        if (paramInt >= 3) {
          break label782;
        }
        this.callsTotalTime[paramInt] = ((SharedPreferences)localObject).getInt("callsTotalTime" + paramInt, 0);
        this.resetStatsDate[paramInt] = ((SharedPreferences)localObject).getLong("resetStatsDate" + paramInt, 0L);
        k = 0;
        for (;;)
        {
          if (k < 7)
          {
            this.sentBytes[paramInt][k] = ((SharedPreferences)localObject).getLong("sentBytes" + paramInt + "_" + k, 0L);
            this.receivedBytes[paramInt][k] = ((SharedPreferences)localObject).getLong("receivedBytes" + paramInt + "_" + k, 0L);
            this.sentItems[paramInt][k] = ((SharedPreferences)localObject).getInt("sentItems" + paramInt + "_" + k, 0);
            this.receivedItems[paramInt][k] = ((SharedPreferences)localObject).getInt("receivedItems" + paramInt + "_" + k, 0);
            k++;
            continue;
            localObject = ApplicationLoader.applicationContext.getSharedPreferences("stats" + paramInt, 0);
            break;
          }
        }
        if (this.resetStatsDate[paramInt] == 0L)
        {
          j = 1;
          this.resetStatsDate[paramInt] = System.currentTimeMillis();
        }
      }
      if (j != 0) {
        saveStats();
      }
    }
  }
  
  private int bytesToInt(byte[] paramArrayOfByte)
  {
    return paramArrayOfByte[0] << 24 | (paramArrayOfByte[1] & 0xFF) << 16 | (paramArrayOfByte[2] & 0xFF) << 8 | paramArrayOfByte[3] & 0xFF;
  }
  
  private long bytesToLong(byte[] paramArrayOfByte)
  {
    return (paramArrayOfByte[0] & 0xFF) << 56 | (paramArrayOfByte[1] & 0xFF) << 48 | (paramArrayOfByte[2] & 0xFF) << 40 | (paramArrayOfByte[3] & 0xFF) << 32 | (paramArrayOfByte[4] & 0xFF) << 24 | (paramArrayOfByte[5] & 0xFF) << 16 | (paramArrayOfByte[6] & 0xFF) << 8 | paramArrayOfByte[7] & 0xFF;
  }
  
  public static StatsController getInstance(int paramInt)
  {
    Object localObject1 = Instance[paramInt];
    Object localObject2 = localObject1;
    if (localObject1 == null) {}
    try
    {
      localObject1 = Instance[paramInt];
      localObject2 = localObject1;
      if (localObject1 == null)
      {
        localObject1 = Instance;
        localObject2 = new org/telegram/messenger/StatsController;
        ((StatsController)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (StatsController)localObject2;
    }
    finally {}
  }
  
  private byte[] intToBytes(int paramInt)
  {
    this.buffer[0] = ((byte)(byte)(paramInt >>> 24));
    this.buffer[1] = ((byte)(byte)(paramInt >>> 16));
    this.buffer[2] = ((byte)(byte)(paramInt >>> 8));
    this.buffer[3] = ((byte)(byte)paramInt);
    return this.buffer;
  }
  
  private byte[] longToBytes(long paramLong)
  {
    this.buffer[0] = ((byte)(byte)(int)(paramLong >>> 56));
    this.buffer[1] = ((byte)(byte)(int)(paramLong >>> 48));
    this.buffer[2] = ((byte)(byte)(int)(paramLong >>> 40));
    this.buffer[3] = ((byte)(byte)(int)(paramLong >>> 32));
    this.buffer[4] = ((byte)(byte)(int)(paramLong >>> 24));
    this.buffer[5] = ((byte)(byte)(int)(paramLong >>> 16));
    this.buffer[6] = ((byte)(byte)(int)(paramLong >>> 8));
    this.buffer[7] = ((byte)(byte)(int)paramLong);
    return this.buffer;
  }
  
  private void saveStats()
  {
    long l = System.currentTimeMillis();
    if (Math.abs(l - ((Long)lastStatsSaveTime.get()).longValue()) >= 2000L)
    {
      lastStatsSaveTime.set(Long.valueOf(l));
      statsSaveQueue.postRunnable(this.saveRunnable);
    }
  }
  
  public int getCallsTotalTime(int paramInt)
  {
    return this.callsTotalTime[paramInt];
  }
  
  public long getReceivedBytesCount(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1) {}
    for (long l = this.receivedBytes[paramInt1][6] - this.receivedBytes[paramInt1][5] - this.receivedBytes[paramInt1][3] - this.receivedBytes[paramInt1][2] - this.receivedBytes[paramInt1][4];; l = this.receivedBytes[paramInt1][paramInt2]) {
      return l;
    }
  }
  
  public int getRecivedItemsCount(int paramInt1, int paramInt2)
  {
    return this.receivedItems[paramInt1][paramInt2];
  }
  
  public long getResetStatsDate(int paramInt)
  {
    return this.resetStatsDate[paramInt];
  }
  
  public long getSentBytesCount(int paramInt1, int paramInt2)
  {
    if (paramInt2 == 1) {}
    for (long l = this.sentBytes[paramInt1][6] - this.sentBytes[paramInt1][5] - this.sentBytes[paramInt1][3] - this.sentBytes[paramInt1][2] - this.sentBytes[paramInt1][4];; l = this.sentBytes[paramInt1][paramInt2]) {
      return l;
    }
  }
  
  public int getSentItemsCount(int paramInt1, int paramInt2)
  {
    return this.sentItems[paramInt1][paramInt2];
  }
  
  public void incrementReceivedBytesCount(int paramInt1, int paramInt2, long paramLong)
  {
    long[] arrayOfLong = this.receivedBytes[paramInt1];
    arrayOfLong[paramInt2] += paramLong;
    saveStats();
  }
  
  public void incrementReceivedItemsCount(int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = this.receivedItems[paramInt1];
    arrayOfInt[paramInt2] += paramInt3;
    saveStats();
  }
  
  public void incrementSentBytesCount(int paramInt1, int paramInt2, long paramLong)
  {
    long[] arrayOfLong = this.sentBytes[paramInt1];
    arrayOfLong[paramInt2] += paramLong;
    saveStats();
  }
  
  public void incrementSentItemsCount(int paramInt1, int paramInt2, int paramInt3)
  {
    int[] arrayOfInt = this.sentItems[paramInt1];
    arrayOfInt[paramInt2] += paramInt3;
    saveStats();
  }
  
  public void incrementTotalCallsTime(int paramInt1, int paramInt2)
  {
    int[] arrayOfInt = this.callsTotalTime;
    arrayOfInt[paramInt1] += paramInt2;
    saveStats();
  }
  
  public void resetStats(int paramInt)
  {
    this.resetStatsDate[paramInt] = System.currentTimeMillis();
    for (int i = 0; i < 7; i++)
    {
      this.sentBytes[paramInt][i] = 0L;
      this.receivedBytes[paramInt][i] = 0L;
      this.sentItems[paramInt][i] = 0;
      this.receivedItems[paramInt][i] = 0;
    }
    this.callsTotalTime[paramInt] = 0;
    saveStats();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/StatsController.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
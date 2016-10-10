package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import java.io.File;
import java.security.SecureRandom;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.User;

public class UserConfig
{
  public static boolean appLocked;
  public static int autoLockIn;
  public static boolean blockedUsersLoaded;
  public static String contactsHash;
  private static TLRPC.User currentUser;
  public static boolean draftsLoaded;
  public static boolean isWaitingForPasscodeEnter;
  public static int lastBroadcastId;
  public static int lastContactsSyncTime;
  public static int lastHintsSyncTime;
  public static int lastLocalId;
  public static int lastPauseTime;
  public static int lastSendMessageId;
  public static String lastUpdateVersion;
  public static long migrateOffsetAccess = -1L;
  public static int migrateOffsetChannelId;
  public static int migrateOffsetChatId;
  public static int migrateOffsetDate;
  public static int migrateOffsetId;
  public static int migrateOffsetUserId;
  public static String passcodeHash;
  public static byte[] passcodeSalt;
  public static int passcodeType;
  public static String pushString = "";
  public static boolean registeredForPush;
  public static boolean saveIncomingPhotos;
  private static final Object sync;
  public static boolean useFingerprint;
  
  static
  {
    lastSendMessageId = -210000;
    lastLocalId = -210000;
    lastBroadcastId = -1;
    contactsHash = "";
    sync = new Object();
    passcodeHash = "";
    passcodeSalt = new byte[0];
    autoLockIn = 3600;
    useFingerprint = true;
    migrateOffsetId = -1;
    migrateOffsetDate = -1;
    migrateOffsetUserId = -1;
    migrateOffsetChatId = -1;
    migrateOffsetChannelId = -1;
  }
  
  public static boolean checkPasscode(String paramString)
  {
    boolean bool;
    byte[] arrayOfByte;
    if (passcodeSalt.length == 0)
    {
      bool = Utilities.MD5(paramString).equals(passcodeHash);
      if (bool) {}
      try
      {
        passcodeSalt = new byte[16];
        Utilities.random.nextBytes(passcodeSalt);
        paramString = paramString.getBytes("UTF-8");
        arrayOfByte = new byte[paramString.length + 32];
        System.arraycopy(passcodeSalt, 0, arrayOfByte, 0, 16);
        System.arraycopy(paramString, 0, arrayOfByte, 16, paramString.length);
        System.arraycopy(passcodeSalt, 0, arrayOfByte, paramString.length + 16, 16);
        passcodeHash = Utilities.bytesToHex(Utilities.computeSHA256(arrayOfByte, 0, arrayOfByte.length));
        saveConfig(false);
        return bool;
      }
      catch (Exception paramString)
      {
        FileLog.e("tmessages", paramString);
        return bool;
      }
    }
    try
    {
      paramString = paramString.getBytes("UTF-8");
      arrayOfByte = new byte[paramString.length + 32];
      System.arraycopy(passcodeSalt, 0, arrayOfByte, 0, 16);
      System.arraycopy(paramString, 0, arrayOfByte, 16, paramString.length);
      System.arraycopy(passcodeSalt, 0, arrayOfByte, paramString.length + 16, 16);
      paramString = Utilities.bytesToHex(Utilities.computeSHA256(arrayOfByte, 0, arrayOfByte.length));
      bool = passcodeHash.equals(paramString);
      return bool;
    }
    catch (Exception paramString)
    {
      FileLog.e("tmessages", paramString);
    }
    return false;
  }
  
  public static void clearConfig()
  {
    currentUser = null;
    registeredForPush = false;
    contactsHash = "";
    lastSendMessageId = -210000;
    lastBroadcastId = -1;
    saveIncomingPhotos = false;
    blockedUsersLoaded = false;
    migrateOffsetId = -1;
    migrateOffsetDate = -1;
    migrateOffsetUserId = -1;
    migrateOffsetChatId = -1;
    migrateOffsetChannelId = -1;
    migrateOffsetAccess = -1L;
    appLocked = false;
    passcodeType = 0;
    passcodeHash = "";
    passcodeSalt = new byte[0];
    autoLockIn = 3600;
    lastPauseTime = 0;
    useFingerprint = true;
    draftsLoaded = true;
    isWaitingForPasscodeEnter = false;
    lastUpdateVersion = BuildVars.BUILD_VERSION_STRING;
    lastContactsSyncTime = (int)(System.currentTimeMillis() / 1000L) - 82800;
    lastHintsSyncTime = (int)(System.currentTimeMillis() / 1000L) - 90000;
    saveConfig(true);
  }
  
  public static int getClientUserId()
  {
    for (;;)
    {
      synchronized (sync)
      {
        if (currentUser != null)
        {
          i = currentUser.id;
          return i;
        }
      }
      int i = 0;
    }
  }
  
  public static TLRPC.User getCurrentUser()
  {
    synchronized (sync)
    {
      TLRPC.User localUser = currentUser;
      return localUser;
    }
  }
  
  public static int getNewMessageId()
  {
    synchronized (sync)
    {
      int i = lastSendMessageId;
      lastSendMessageId -= 1;
      return i;
    }
  }
  
  public static boolean isClientActivated()
  {
    for (;;)
    {
      synchronized (sync)
      {
        if (currentUser != null)
        {
          bool = true;
          return bool;
        }
      }
      boolean bool = false;
    }
  }
  
  public static void loadConfig()
  {
    for (;;)
    {
      synchronized (sync)
      {
        File localFile = new File(ApplicationLoader.getFilesDirFixed(), "user.dat");
        boolean bool = localFile.exists();
        if (bool)
        {
          try
          {
            localObject4 = new SerializedData(localFile);
            i = ((SerializedData)localObject4).readInt32(false);
            if (i != 1) {
              continue;
            }
            currentUser = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject4, ((SerializedData)localObject4).readInt32(false), false);
            MessagesStorage.lastDateValue = ((SerializedData)localObject4).readInt32(false);
            MessagesStorage.lastPtsValue = ((SerializedData)localObject4).readInt32(false);
            MessagesStorage.lastSeqValue = ((SerializedData)localObject4).readInt32(false);
            registeredForPush = ((SerializedData)localObject4).readBool(false);
            pushString = ((SerializedData)localObject4).readString(false);
            lastSendMessageId = ((SerializedData)localObject4).readInt32(false);
            lastLocalId = ((SerializedData)localObject4).readInt32(false);
            contactsHash = ((SerializedData)localObject4).readString(false);
            ((SerializedData)localObject4).readString(false);
            saveIncomingPhotos = ((SerializedData)localObject4).readBool(false);
            MessagesStorage.lastQtsValue = ((SerializedData)localObject4).readInt32(false);
            MessagesStorage.lastSecretVersion = ((SerializedData)localObject4).readInt32(false);
            if (((SerializedData)localObject4).readInt32(false) == 1) {
              MessagesStorage.secretPBytes = ((SerializedData)localObject4).readByteArray(false);
            }
            MessagesStorage.secretG = ((SerializedData)localObject4).readInt32(false);
            Utilities.stageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                UserConfig.saveConfig(true, this.val$configFile);
              }
            });
            if (lastLocalId > -210000) {
              lastLocalId = -210000;
            }
            if (lastSendMessageId > -210000) {
              lastSendMessageId = -210000;
            }
            ((SerializedData)localObject4).cleanup();
            Utilities.stageQueue.postRunnable(new Runnable()
            {
              public void run()
              {
                UserConfig.saveConfig(true, this.val$configFile);
              }
            });
          }
          catch (Exception localException)
          {
            int i;
            SharedPreferences localSharedPreferences;
            FileLog.e("tmessages", localException);
            continue;
          }
          return;
          if (i != 2) {
            continue;
          }
          currentUser = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject4, ((SerializedData)localObject4).readInt32(false), false);
          localSharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
          registeredForPush = localSharedPreferences.getBoolean("registeredForPush", false);
          pushString = localSharedPreferences.getString("pushString2", "");
          lastSendMessageId = localSharedPreferences.getInt("lastSendMessageId", -210000);
          lastLocalId = localSharedPreferences.getInt("lastLocalId", -210000);
          contactsHash = localSharedPreferences.getString("contactsHash", "");
          saveIncomingPhotos = localSharedPreferences.getBoolean("saveIncomingPhotos", false);
        }
      }
      Object localObject3 = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
      registeredForPush = ((SharedPreferences)localObject3).getBoolean("registeredForPush", false);
      pushString = ((SharedPreferences)localObject3).getString("pushString2", "");
      lastSendMessageId = ((SharedPreferences)localObject3).getInt("lastSendMessageId", -210000);
      lastLocalId = ((SharedPreferences)localObject3).getInt("lastLocalId", -210000);
      contactsHash = ((SharedPreferences)localObject3).getString("contactsHash", "");
      saveIncomingPhotos = ((SharedPreferences)localObject3).getBoolean("saveIncomingPhotos", false);
      lastBroadcastId = ((SharedPreferences)localObject3).getInt("lastBroadcastId", -1);
      blockedUsersLoaded = ((SharedPreferences)localObject3).getBoolean("blockedUsersLoaded", false);
      passcodeHash = ((SharedPreferences)localObject3).getString("passcodeHash1", "");
      appLocked = ((SharedPreferences)localObject3).getBoolean("appLocked", false);
      passcodeType = ((SharedPreferences)localObject3).getInt("passcodeType", 0);
      autoLockIn = ((SharedPreferences)localObject3).getInt("autoLockIn", 3600);
      lastPauseTime = ((SharedPreferences)localObject3).getInt("lastPauseTime", 0);
      useFingerprint = ((SharedPreferences)localObject3).getBoolean("useFingerprint", true);
      lastUpdateVersion = ((SharedPreferences)localObject3).getString("lastUpdateVersion2", "3.5");
      lastContactsSyncTime = ((SharedPreferences)localObject3).getInt("lastContactsSyncTime", (int)(System.currentTimeMillis() / 1000L) - 82800);
      lastHintsSyncTime = ((SharedPreferences)localObject3).getInt("lastHintsSyncTime", (int)(System.currentTimeMillis() / 1000L) - 90000);
      draftsLoaded = ((SharedPreferences)localObject3).getBoolean("draftsLoaded", false);
      migrateOffsetId = ((SharedPreferences)localObject3).getInt("migrateOffsetId", 0);
      if (migrateOffsetId != -1)
      {
        migrateOffsetDate = ((SharedPreferences)localObject3).getInt("migrateOffsetDate", 0);
        migrateOffsetUserId = ((SharedPreferences)localObject3).getInt("migrateOffsetUserId", 0);
        migrateOffsetChatId = ((SharedPreferences)localObject3).getInt("migrateOffsetChatId", 0);
        migrateOffsetChannelId = ((SharedPreferences)localObject3).getInt("migrateOffsetChannelId", 0);
        migrateOffsetAccess = ((SharedPreferences)localObject3).getLong("migrateOffsetAccess", 0L);
      }
      Object localObject4 = ((SharedPreferences)localObject3).getString("user", null);
      if (localObject4 != null)
      {
        localObject4 = Base64.decode((String)localObject4, 0);
        if (localObject4 != null)
        {
          localObject4 = new SerializedData((byte[])localObject4);
          currentUser = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject4, ((SerializedData)localObject4).readInt32(false), false);
          ((SerializedData)localObject4).cleanup();
        }
      }
      localObject3 = ((SharedPreferences)localObject3).getString("passcodeSalt", "");
      if (((String)localObject3).length() > 0) {
        passcodeSalt = Base64.decode((String)localObject3, 0);
      } else {
        passcodeSalt = new byte[0];
      }
    }
  }
  
  public static void saveConfig(boolean paramBoolean)
  {
    saveConfig(paramBoolean, null);
  }
  
  public static void saveConfig(boolean paramBoolean, File paramFile)
  {
    synchronized (sync)
    {
      for (;;)
      {
        try
        {
          localEditor = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0).edit();
          localEditor.putBoolean("registeredForPush", registeredForPush);
          localEditor.putString("pushString2", pushString);
          localEditor.putInt("lastSendMessageId", lastSendMessageId);
          localEditor.putInt("lastLocalId", lastLocalId);
          localEditor.putString("contactsHash", contactsHash);
          localEditor.putBoolean("saveIncomingPhotos", saveIncomingPhotos);
          localEditor.putInt("lastBroadcastId", lastBroadcastId);
          localEditor.putBoolean("blockedUsersLoaded", blockedUsersLoaded);
          localEditor.putString("passcodeHash1", passcodeHash);
          if (passcodeSalt.length <= 0) {
            continue;
          }
          localObject1 = Base64.encodeToString(passcodeSalt, 0);
          localEditor.putString("passcodeSalt", (String)localObject1);
          localEditor.putBoolean("appLocked", appLocked);
          localEditor.putInt("passcodeType", passcodeType);
          localEditor.putInt("autoLockIn", autoLockIn);
          localEditor.putInt("lastPauseTime", lastPauseTime);
          localEditor.putString("lastUpdateVersion2", lastUpdateVersion);
          localEditor.putInt("lastContactsSyncTime", lastContactsSyncTime);
          localEditor.putBoolean("useFingerprint", useFingerprint);
          localEditor.putInt("lastHintsSyncTime", lastHintsSyncTime);
          localEditor.putBoolean("draftsLoaded", draftsLoaded);
          localEditor.putInt("migrateOffsetId", migrateOffsetId);
          if (migrateOffsetId != -1)
          {
            localEditor.putInt("migrateOffsetDate", migrateOffsetDate);
            localEditor.putInt("migrateOffsetUserId", migrateOffsetUserId);
            localEditor.putInt("migrateOffsetChatId", migrateOffsetChatId);
            localEditor.putInt("migrateOffsetChannelId", migrateOffsetChannelId);
            localEditor.putLong("migrateOffsetAccess", migrateOffsetAccess);
          }
          if (currentUser == null) {
            continue;
          }
          if (paramBoolean)
          {
            localObject1 = new SerializedData();
            currentUser.serializeToStream((AbstractSerializedData)localObject1);
            localEditor.putString("user", Base64.encodeToString(((SerializedData)localObject1).toByteArray(), 0));
            ((SerializedData)localObject1).cleanup();
          }
          localEditor.commit();
          if (paramFile != null) {
            paramFile.delete();
          }
        }
        catch (Exception paramFile)
        {
          SharedPreferences.Editor localEditor;
          Object localObject1;
          FileLog.e("tmessages", paramFile);
          continue;
        }
        return;
        localObject1 = "";
      }
      localEditor.remove("user");
    }
  }
  
  public static void setCurrentUser(TLRPC.User paramUser)
  {
    synchronized (sync)
    {
      currentUser = paramUser;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/UserConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
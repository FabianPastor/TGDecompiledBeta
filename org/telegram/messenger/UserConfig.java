package org.telegram.messenger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import java.io.File;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.TL_account_tmpPassword;
import org.telegram.tgnet.TLRPC.User;

public class UserConfig
{
  private static volatile UserConfig[] Instance = new UserConfig[3];
  public static final int MAX_ACCOUNT_COUNT = 3;
  public static int selectedAccount;
  public boolean blockedUsersLoaded;
  public int botRatingLoadTime;
  public int clientUserId;
  private boolean configLoaded;
  public boolean contactsReimported;
  public int contactsSavedCount;
  private int currentAccount;
  private TLRPC.User currentUser;
  public long dialogsLoadOffsetAccess = 0L;
  public int dialogsLoadOffsetChannelId = 0;
  public int dialogsLoadOffsetChatId = 0;
  public int dialogsLoadOffsetDate = 0;
  public int dialogsLoadOffsetId = 0;
  public int dialogsLoadOffsetUserId = 0;
  public boolean draftsLoaded;
  public int lastBroadcastId = -1;
  public int lastContactsSyncTime;
  public int lastHintsSyncTime;
  public int lastSendMessageId = -210000;
  public int loginTime;
  public long migrateOffsetAccess = -1L;
  public int migrateOffsetChannelId = -1;
  public int migrateOffsetChatId = -1;
  public int migrateOffsetDate = -1;
  public int migrateOffsetId = -1;
  public int migrateOffsetUserId = -1;
  public boolean pinnedDialogsLoaded = true;
  public int ratingLoadTime;
  public boolean registeredForPush;
  private final Object sync = new Object();
  public boolean syncContacts = true;
  public TLRPC.TL_account_tmpPassword tmpPassword;
  public int totalDialogsLoadCount = 0;
  
  public UserConfig(int paramInt)
  {
    this.currentAccount = paramInt;
  }
  
  public static int getActivatedAccountsCount()
  {
    int i = 0;
    int j = 0;
    while (j < 3)
    {
      int k = i;
      if (getInstance(j).isClientActivated()) {
        k = i + 1;
      }
      j++;
      i = k;
    }
    return i;
  }
  
  public static UserConfig getInstance(int paramInt)
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
        localObject2 = new org/telegram/messenger/UserConfig;
        ((UserConfig)localObject2).<init>(paramInt);
        localObject1[paramInt] = localObject2;
      }
      return (UserConfig)localObject2;
    }
    finally {}
  }
  
  public void clearConfig()
  {
    this.currentUser = null;
    this.clientUserId = 0;
    this.registeredForPush = false;
    this.contactsSavedCount = 0;
    this.lastSendMessageId = -210000;
    this.lastBroadcastId = -1;
    this.blockedUsersLoaded = false;
    this.migrateOffsetId = -1;
    this.migrateOffsetDate = -1;
    this.migrateOffsetUserId = -1;
    this.migrateOffsetChatId = -1;
    this.migrateOffsetChannelId = -1;
    this.migrateOffsetAccess = -1L;
    this.dialogsLoadOffsetId = 0;
    this.totalDialogsLoadCount = 0;
    this.dialogsLoadOffsetDate = 0;
    this.dialogsLoadOffsetUserId = 0;
    this.dialogsLoadOffsetChatId = 0;
    this.dialogsLoadOffsetChannelId = 0;
    this.dialogsLoadOffsetAccess = 0L;
    this.ratingLoadTime = 0;
    this.botRatingLoadTime = 0;
    this.draftsLoaded = true;
    this.contactsReimported = true;
    this.syncContacts = true;
    this.pinnedDialogsLoaded = false;
    this.loginTime = ((int)(System.currentTimeMillis() / 1000L));
    this.lastContactsSyncTime = ((int)(System.currentTimeMillis() / 1000L) - 82800);
    this.lastHintsSyncTime = ((int)(System.currentTimeMillis() / 1000L) - 90000);
    int i = 0;
    for (int j = 0;; j++)
    {
      int k = i;
      if (j < 3)
      {
        if (getInstance(j).isClientActivated()) {
          k = 1;
        }
      }
      else
      {
        if (k == 0) {
          SharedConfig.clearConfig();
        }
        saveConfig(true);
        return;
      }
    }
  }
  
  public int getClientUserId()
  {
    synchronized (this.sync)
    {
      if (this.currentUser != null)
      {
        i = this.currentUser.id;
        return i;
      }
      int i = 0;
    }
  }
  
  public TLRPC.User getCurrentUser()
  {
    synchronized (this.sync)
    {
      TLRPC.User localUser = this.currentUser;
      return localUser;
    }
  }
  
  public int getNewMessageId()
  {
    synchronized (this.sync)
    {
      int i = this.lastSendMessageId;
      this.lastSendMessageId -= 1;
      return i;
    }
  }
  
  public boolean isClientActivated()
  {
    synchronized (this.sync)
    {
      if (this.currentUser != null)
      {
        bool = true;
        return bool;
      }
      boolean bool = false;
    }
  }
  
  public void loadConfig()
  {
    for (;;)
    {
      synchronized (this.sync)
      {
        if (this.configLoaded) {
          return;
        }
        if (this.currentAccount == 0)
        {
          Object localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
          selectedAccount = ((SharedPreferences)localObject2).getInt("selectedAccount", 0);
          this.registeredForPush = ((SharedPreferences)localObject2).getBoolean("registeredForPush", false);
          this.lastSendMessageId = ((SharedPreferences)localObject2).getInt("lastSendMessageId", -210000);
          this.contactsSavedCount = ((SharedPreferences)localObject2).getInt("contactsSavedCount", 0);
          this.lastBroadcastId = ((SharedPreferences)localObject2).getInt("lastBroadcastId", -1);
          this.blockedUsersLoaded = ((SharedPreferences)localObject2).getBoolean("blockedUsersLoaded", false);
          this.lastContactsSyncTime = ((SharedPreferences)localObject2).getInt("lastContactsSyncTime", (int)(System.currentTimeMillis() / 1000L) - 82800);
          this.lastHintsSyncTime = ((SharedPreferences)localObject2).getInt("lastHintsSyncTime", (int)(System.currentTimeMillis() / 1000L) - 90000);
          this.draftsLoaded = ((SharedPreferences)localObject2).getBoolean("draftsLoaded", false);
          this.pinnedDialogsLoaded = ((SharedPreferences)localObject2).getBoolean("pinnedDialogsLoaded", false);
          this.contactsReimported = ((SharedPreferences)localObject2).getBoolean("contactsReimported", false);
          this.ratingLoadTime = ((SharedPreferences)localObject2).getInt("ratingLoadTime", 0);
          this.botRatingLoadTime = ((SharedPreferences)localObject2).getInt("botRatingLoadTime", 0);
          this.loginTime = ((SharedPreferences)localObject2).getInt("loginTime", this.currentAccount);
          this.syncContacts = ((SharedPreferences)localObject2).getBoolean("syncContacts", this.syncContacts);
          this.migrateOffsetId = ((SharedPreferences)localObject2).getInt("3migrateOffsetId", 0);
          if (this.migrateOffsetId != -1)
          {
            this.migrateOffsetDate = ((SharedPreferences)localObject2).getInt("3migrateOffsetDate", 0);
            this.migrateOffsetUserId = ((SharedPreferences)localObject2).getInt("3migrateOffsetUserId", 0);
            this.migrateOffsetChatId = ((SharedPreferences)localObject2).getInt("3migrateOffsetChatId", 0);
            this.migrateOffsetChannelId = ((SharedPreferences)localObject2).getInt("3migrateOffsetChannelId", 0);
            this.migrateOffsetAccess = ((SharedPreferences)localObject2).getLong("3migrateOffsetAccess", 0L);
          }
          this.dialogsLoadOffsetId = ((SharedPreferences)localObject2).getInt("2dialogsLoadOffsetId", -1);
          this.totalDialogsLoadCount = ((SharedPreferences)localObject2).getInt("2totalDialogsLoadCount", 0);
          this.dialogsLoadOffsetDate = ((SharedPreferences)localObject2).getInt("2dialogsLoadOffsetDate", -1);
          this.dialogsLoadOffsetUserId = ((SharedPreferences)localObject2).getInt("2dialogsLoadOffsetUserId", -1);
          this.dialogsLoadOffsetChatId = ((SharedPreferences)localObject2).getInt("2dialogsLoadOffsetChatId", -1);
          this.dialogsLoadOffsetChannelId = ((SharedPreferences)localObject2).getInt("2dialogsLoadOffsetChannelId", -1);
          this.dialogsLoadOffsetAccess = ((SharedPreferences)localObject2).getLong("2dialogsLoadOffsetAccess", -1L);
          localObject5 = ((SharedPreferences)localObject2).getString("tmpPassword", null);
          if (localObject5 != null)
          {
            byte[] arrayOfByte = Base64.decode((String)localObject5, 0);
            if (arrayOfByte != null)
            {
              localObject5 = new org/telegram/tgnet/SerializedData;
              ((SerializedData)localObject5).<init>(arrayOfByte);
              this.tmpPassword = TLRPC.TL_account_tmpPassword.TLdeserialize((AbstractSerializedData)localObject5, ((SerializedData)localObject5).readInt32(false), false);
              ((SerializedData)localObject5).cleanup();
            }
          }
          localObject2 = ((SharedPreferences)localObject2).getString("user", null);
          if (localObject2 != null)
          {
            localObject5 = Base64.decode((String)localObject2, 0);
            if (localObject5 != null)
            {
              localObject2 = new org/telegram/tgnet/SerializedData;
              ((SerializedData)localObject2).<init>((byte[])localObject5);
              this.currentUser = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject2, ((SerializedData)localObject2).readInt32(false), false);
              ((SerializedData)localObject2).cleanup();
            }
          }
          if (this.currentUser != null) {
            this.clientUserId = this.currentUser.id;
          }
          this.configLoaded = true;
        }
      }
      Object localObject4 = ApplicationLoader.applicationContext;
      Object localObject5 = new java/lang/StringBuilder;
      ((StringBuilder)localObject5).<init>();
      localObject4 = ((Context)localObject4).getSharedPreferences("userconfig" + this.currentAccount, 0);
    }
  }
  
  public void saveConfig(boolean paramBoolean)
  {
    saveConfig(paramBoolean, null);
  }
  
  public void saveConfig(boolean paramBoolean, File paramFile)
  {
    for (;;)
    {
      Object localObject2;
      synchronized (this.sync)
      {
        try
        {
          if (this.currentAccount != 0) {
            continue;
          }
          localObject2 = ApplicationLoader.applicationContext.getSharedPreferences("userconfing", 0);
          localObject2 = ((SharedPreferences)localObject2).edit();
          if (this.currentAccount == 0) {
            ((SharedPreferences.Editor)localObject2).putInt("selectedAccount", selectedAccount);
          }
          ((SharedPreferences.Editor)localObject2).putBoolean("registeredForPush", this.registeredForPush);
          ((SharedPreferences.Editor)localObject2).putInt("lastSendMessageId", this.lastSendMessageId);
          ((SharedPreferences.Editor)localObject2).putInt("contactsSavedCount", this.contactsSavedCount);
          ((SharedPreferences.Editor)localObject2).putInt("lastBroadcastId", this.lastBroadcastId);
          ((SharedPreferences.Editor)localObject2).putBoolean("blockedUsersLoaded", this.blockedUsersLoaded);
          ((SharedPreferences.Editor)localObject2).putInt("lastContactsSyncTime", this.lastContactsSyncTime);
          ((SharedPreferences.Editor)localObject2).putInt("lastHintsSyncTime", this.lastHintsSyncTime);
          ((SharedPreferences.Editor)localObject2).putBoolean("draftsLoaded", this.draftsLoaded);
          ((SharedPreferences.Editor)localObject2).putBoolean("pinnedDialogsLoaded", this.pinnedDialogsLoaded);
          ((SharedPreferences.Editor)localObject2).putInt("ratingLoadTime", this.ratingLoadTime);
          ((SharedPreferences.Editor)localObject2).putInt("botRatingLoadTime", this.botRatingLoadTime);
          ((SharedPreferences.Editor)localObject2).putBoolean("contactsReimported", this.contactsReimported);
          ((SharedPreferences.Editor)localObject2).putInt("loginTime", this.loginTime);
          ((SharedPreferences.Editor)localObject2).putBoolean("syncContacts", this.syncContacts);
          ((SharedPreferences.Editor)localObject2).putInt("3migrateOffsetId", this.migrateOffsetId);
          if (this.migrateOffsetId != -1)
          {
            ((SharedPreferences.Editor)localObject2).putInt("3migrateOffsetDate", this.migrateOffsetDate);
            ((SharedPreferences.Editor)localObject2).putInt("3migrateOffsetUserId", this.migrateOffsetUserId);
            ((SharedPreferences.Editor)localObject2).putInt("3migrateOffsetChatId", this.migrateOffsetChatId);
            ((SharedPreferences.Editor)localObject2).putInt("3migrateOffsetChannelId", this.migrateOffsetChannelId);
            ((SharedPreferences.Editor)localObject2).putLong("3migrateOffsetAccess", this.migrateOffsetAccess);
          }
          ((SharedPreferences.Editor)localObject2).putInt("2totalDialogsLoadCount", this.totalDialogsLoadCount);
          ((SharedPreferences.Editor)localObject2).putInt("2dialogsLoadOffsetId", this.dialogsLoadOffsetId);
          ((SharedPreferences.Editor)localObject2).putInt("2dialogsLoadOffsetDate", this.dialogsLoadOffsetDate);
          ((SharedPreferences.Editor)localObject2).putInt("2dialogsLoadOffsetUserId", this.dialogsLoadOffsetUserId);
          ((SharedPreferences.Editor)localObject2).putInt("2dialogsLoadOffsetChatId", this.dialogsLoadOffsetChatId);
          ((SharedPreferences.Editor)localObject2).putInt("2dialogsLoadOffsetChannelId", this.dialogsLoadOffsetChannelId);
          ((SharedPreferences.Editor)localObject2).putLong("2dialogsLoadOffsetAccess", this.dialogsLoadOffsetAccess);
          SharedConfig.saveConfig();
          if (this.tmpPassword == null) {
            continue;
          }
          localObject3 = new org/telegram/tgnet/SerializedData;
          ((SerializedData)localObject3).<init>();
          this.tmpPassword.serializeToStream((AbstractSerializedData)localObject3);
          ((SharedPreferences.Editor)localObject2).putString("tmpPassword", Base64.encodeToString(((SerializedData)localObject3).toByteArray(), 0));
          ((SerializedData)localObject3).cleanup();
          if (this.currentUser == null) {
            break label638;
          }
          if (paramBoolean)
          {
            localObject3 = new org/telegram/tgnet/SerializedData;
            ((SerializedData)localObject3).<init>();
            this.currentUser.serializeToStream((AbstractSerializedData)localObject3);
            ((SharedPreferences.Editor)localObject2).putString("user", Base64.encodeToString(((SerializedData)localObject3).toByteArray(), 0));
            ((SerializedData)localObject3).cleanup();
          }
          ((SharedPreferences.Editor)localObject2).commit();
          if (paramFile != null) {
            paramFile.delete();
          }
        }
        catch (Exception paramFile)
        {
          Object localObject3;
          FileLog.e(paramFile);
          continue;
        }
        return;
        localObject3 = ApplicationLoader.applicationContext;
        localObject2 = new java/lang/StringBuilder;
        ((StringBuilder)localObject2).<init>();
        localObject2 = ((Context)localObject3).getSharedPreferences("userconfig" + this.currentAccount, 0);
        continue;
        ((SharedPreferences.Editor)localObject2).remove("tmpPassword");
      }
      label638:
      ((SharedPreferences.Editor)localObject2).remove("user");
    }
  }
  
  public void setCurrentUser(TLRPC.User paramUser)
  {
    synchronized (this.sync)
    {
      this.currentUser = paramUser;
      this.clientUserId = paramUser.id;
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/UserConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
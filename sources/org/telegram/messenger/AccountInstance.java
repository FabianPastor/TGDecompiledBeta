package org.telegram.messenger;

import android.content.SharedPreferences;
import org.telegram.tgnet.ConnectionsManager;

public class AccountInstance {
    private static volatile AccountInstance[] Instance = new AccountInstance[3];
    private int currentAccount;

    public static AccountInstance getInstance(int num) {
        AccountInstance localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (AccountInstance.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    AccountInstance[] accountInstanceArr = Instance;
                    AccountInstance accountInstance = new AccountInstance(num);
                    localInstance = accountInstance;
                    accountInstanceArr[num] = accountInstance;
                }
            }
        }
        return localInstance;
    }

    public AccountInstance(int instance) {
        this.currentAccount = instance;
    }

    public MessagesController getMessagesController() {
        return MessagesController.getInstance(this.currentAccount);
    }

    public MessagesStorage getMessagesStorage() {
        return MessagesStorage.getInstance(this.currentAccount);
    }

    public ContactsController getContactsController() {
        return ContactsController.getInstance(this.currentAccount);
    }

    public MediaDataController getMediaDataController() {
        return MediaDataController.getInstance(this.currentAccount);
    }

    public ConnectionsManager getConnectionsManager() {
        return ConnectionsManager.getInstance(this.currentAccount);
    }

    public NotificationsController getNotificationsController() {
        return NotificationsController.getInstance(this.currentAccount);
    }

    public NotificationCenter getNotificationCenter() {
        return NotificationCenter.getInstance(this.currentAccount);
    }

    public LocationController getLocationController() {
        return LocationController.getInstance(this.currentAccount);
    }

    public UserConfig getUserConfig() {
        return UserConfig.getInstance(this.currentAccount);
    }

    public DownloadController getDownloadController() {
        return DownloadController.getInstance(this.currentAccount);
    }

    public SendMessagesHelper getSendMessagesHelper() {
        return SendMessagesHelper.getInstance(this.currentAccount);
    }

    public SecretChatHelper getSecretChatHelper() {
        return SecretChatHelper.getInstance(this.currentAccount);
    }

    public StatsController getStatsController() {
        return StatsController.getInstance(this.currentAccount);
    }

    public FileLoader getFileLoader() {
        return FileLoader.getInstance(this.currentAccount);
    }

    public FileRefController getFileRefController() {
        return FileRefController.getInstance(this.currentAccount);
    }

    public SharedPreferences getNotificationsSettings() {
        return MessagesController.getNotificationsSettings(this.currentAccount);
    }

    public MemberRequestsController getMemberRequestsController() {
        return MemberRequestsController.getInstance(this.currentAccount);
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }
}

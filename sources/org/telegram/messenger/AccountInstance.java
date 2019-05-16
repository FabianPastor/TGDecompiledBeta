package org.telegram.messenger;

import org.telegram.tgnet.ConnectionsManager;

public class AccountInstance {
    private static volatile AccountInstance[] Instance = new AccountInstance[3];
    private int currentAccount;

    public static AccountInstance getInstance(int i) {
        AccountInstance accountInstance = Instance[i];
        if (accountInstance == null) {
            synchronized (AccountInstance.class) {
                accountInstance = Instance[i];
                if (accountInstance == null) {
                    AccountInstance[] accountInstanceArr = Instance;
                    AccountInstance accountInstance2 = new AccountInstance(i);
                    accountInstanceArr[i] = accountInstance2;
                    accountInstance = accountInstance2;
                }
            }
        }
        return accountInstance;
    }

    public AccountInstance(int i) {
        this.currentAccount = i;
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

    public DataQuery getDataQuery() {
        return DataQuery.getInstance(this.currentAccount);
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

    public UserConfig getUserConfig() {
        return UserConfig.getInstance(this.currentAccount);
    }
}

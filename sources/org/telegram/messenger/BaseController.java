package org.telegram.messenger;

import org.telegram.tgnet.ConnectionsManager;

public class BaseController {
    protected int currentAccount;
    private AccountInstance parentAccountInstance;

    public BaseController(int i) {
        this.parentAccountInstance = AccountInstance.getInstance(i);
        this.currentAccount = i;
    }

    /* Access modifiers changed, original: protected */
    public AccountInstance getAccountInstance() {
        return this.parentAccountInstance;
    }

    /* Access modifiers changed, original: protected */
    public MessagesController getMessagesController() {
        return this.parentAccountInstance.getMessagesController();
    }

    /* Access modifiers changed, original: protected */
    public ContactsController getContactsController() {
        return this.parentAccountInstance.getContactsController();
    }

    /* Access modifiers changed, original: protected */
    public MediaDataController getMediaDataController() {
        return this.parentAccountInstance.getMediaDataController();
    }

    /* Access modifiers changed, original: protected */
    public ConnectionsManager getConnectionsManager() {
        return this.parentAccountInstance.getConnectionsManager();
    }

    /* Access modifiers changed, original: protected */
    public LocationController getLocationController() {
        return this.parentAccountInstance.getLocationController();
    }

    /* Access modifiers changed, original: protected */
    public NotificationsController getNotificationsController() {
        return this.parentAccountInstance.getNotificationsController();
    }

    /* Access modifiers changed, original: protected */
    public NotificationCenter getNotificationCenter() {
        return this.parentAccountInstance.getNotificationCenter();
    }

    /* Access modifiers changed, original: protected */
    public UserConfig getUserConfig() {
        return this.parentAccountInstance.getUserConfig();
    }

    /* Access modifiers changed, original: protected */
    public MessagesStorage getMessagesStorage() {
        return this.parentAccountInstance.getMessagesStorage();
    }

    /* Access modifiers changed, original: protected */
    public DownloadController getDownloadController() {
        return this.parentAccountInstance.getDownloadController();
    }

    /* Access modifiers changed, original: protected */
    public SendMessagesHelper getSendMessagesHelper() {
        return this.parentAccountInstance.getSendMessagesHelper();
    }

    /* Access modifiers changed, original: protected */
    public SecretChatHelper getSecretChatHelper() {
        return this.parentAccountInstance.getSecretChatHelper();
    }

    /* Access modifiers changed, original: protected */
    public StatsController getStatsController() {
        return this.parentAccountInstance.getStatsController();
    }

    /* Access modifiers changed, original: protected */
    public FileLoader getFileLoader() {
        return this.parentAccountInstance.getFileLoader();
    }

    /* Access modifiers changed, original: protected */
    public FileRefController getFileRefController() {
        return this.parentAccountInstance.getFileRefController();
    }
}

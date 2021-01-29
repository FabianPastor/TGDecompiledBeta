package org.telegram.messenger;

import org.telegram.tgnet.ConnectionsManager;

public class BaseController {
    protected int currentAccount;
    private AccountInstance parentAccountInstance;

    public BaseController(int i) {
        this.parentAccountInstance = AccountInstance.getInstance(i);
        this.currentAccount = i;
    }

    /* access modifiers changed from: protected */
    public final AccountInstance getAccountInstance() {
        return this.parentAccountInstance;
    }

    /* access modifiers changed from: protected */
    public final MessagesController getMessagesController() {
        return this.parentAccountInstance.getMessagesController();
    }

    /* access modifiers changed from: protected */
    public final ContactsController getContactsController() {
        return this.parentAccountInstance.getContactsController();
    }

    /* access modifiers changed from: protected */
    public final MediaDataController getMediaDataController() {
        return this.parentAccountInstance.getMediaDataController();
    }

    /* access modifiers changed from: protected */
    public final ConnectionsManager getConnectionsManager() {
        return this.parentAccountInstance.getConnectionsManager();
    }

    /* access modifiers changed from: protected */
    public final LocationController getLocationController() {
        return this.parentAccountInstance.getLocationController();
    }

    /* access modifiers changed from: protected */
    public final NotificationsController getNotificationsController() {
        return this.parentAccountInstance.getNotificationsController();
    }

    /* access modifiers changed from: protected */
    public final NotificationCenter getNotificationCenter() {
        return this.parentAccountInstance.getNotificationCenter();
    }

    /* access modifiers changed from: protected */
    public final UserConfig getUserConfig() {
        return this.parentAccountInstance.getUserConfig();
    }

    /* access modifiers changed from: protected */
    public final MessagesStorage getMessagesStorage() {
        return this.parentAccountInstance.getMessagesStorage();
    }

    /* access modifiers changed from: protected */
    public final DownloadController getDownloadController() {
        return this.parentAccountInstance.getDownloadController();
    }

    /* access modifiers changed from: protected */
    public final SendMessagesHelper getSendMessagesHelper() {
        return this.parentAccountInstance.getSendMessagesHelper();
    }

    /* access modifiers changed from: protected */
    public final SecretChatHelper getSecretChatHelper() {
        return this.parentAccountInstance.getSecretChatHelper();
    }

    /* access modifiers changed from: protected */
    public final StatsController getStatsController() {
        return this.parentAccountInstance.getStatsController();
    }

    /* access modifiers changed from: protected */
    public final FileLoader getFileLoader() {
        return this.parentAccountInstance.getFileLoader();
    }

    /* access modifiers changed from: protected */
    public final FileRefController getFileRefController() {
        return this.parentAccountInstance.getFileRefController();
    }
}

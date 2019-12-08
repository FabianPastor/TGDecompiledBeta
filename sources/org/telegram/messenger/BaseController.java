package org.telegram.messenger;

import org.telegram.tgnet.ConnectionsManager;

public class BaseController {
    protected int currentAccount;
    private AccountInstance parentAccountInstance;

    public BaseController(int i) {
        this.parentAccountInstance = AccountInstance.getInstance(i);
        this.currentAccount = i;
    }

    /* Access modifiers changed, original: protected|final */
    public final AccountInstance getAccountInstance() {
        return this.parentAccountInstance;
    }

    /* Access modifiers changed, original: protected|final */
    public final MessagesController getMessagesController() {
        return this.parentAccountInstance.getMessagesController();
    }

    /* Access modifiers changed, original: protected|final */
    public final ContactsController getContactsController() {
        return this.parentAccountInstance.getContactsController();
    }

    /* Access modifiers changed, original: protected|final */
    public final MediaDataController getMediaDataController() {
        return this.parentAccountInstance.getMediaDataController();
    }

    /* Access modifiers changed, original: protected|final */
    public final ConnectionsManager getConnectionsManager() {
        return this.parentAccountInstance.getConnectionsManager();
    }

    /* Access modifiers changed, original: protected|final */
    public final LocationController getLocationController() {
        return this.parentAccountInstance.getLocationController();
    }

    /* Access modifiers changed, original: protected|final */
    public final NotificationsController getNotificationsController() {
        return this.parentAccountInstance.getNotificationsController();
    }

    /* Access modifiers changed, original: protected|final */
    public final NotificationCenter getNotificationCenter() {
        return this.parentAccountInstance.getNotificationCenter();
    }

    /* Access modifiers changed, original: protected|final */
    public final UserConfig getUserConfig() {
        return this.parentAccountInstance.getUserConfig();
    }

    /* Access modifiers changed, original: protected|final */
    public final MessagesStorage getMessagesStorage() {
        return this.parentAccountInstance.getMessagesStorage();
    }

    /* Access modifiers changed, original: protected|final */
    public final DownloadController getDownloadController() {
        return this.parentAccountInstance.getDownloadController();
    }

    /* Access modifiers changed, original: protected|final */
    public final SendMessagesHelper getSendMessagesHelper() {
        return this.parentAccountInstance.getSendMessagesHelper();
    }

    /* Access modifiers changed, original: protected|final */
    public final SecretChatHelper getSecretChatHelper() {
        return this.parentAccountInstance.getSecretChatHelper();
    }

    /* Access modifiers changed, original: protected|final */
    public final StatsController getStatsController() {
        return this.parentAccountInstance.getStatsController();
    }

    /* Access modifiers changed, original: protected|final */
    public final FileLoader getFileLoader() {
        return this.parentAccountInstance.getFileLoader();
    }

    /* Access modifiers changed, original: protected|final */
    public final FileRefController getFileRefController() {
        return this.parentAccountInstance.getFileRefController();
    }

    /* Access modifiers changed, original: protected|final */
    public final TonController getTonController() {
        return this.parentAccountInstance.getTonController();
    }
}

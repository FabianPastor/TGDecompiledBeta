package org.telegram.messenger;

import org.telegram.tgnet.ConnectionsManager;
/* loaded from: classes.dex */
public class BaseController {
    protected final int currentAccount;
    private AccountInstance parentAccountInstance;

    public BaseController(int i) {
        this.parentAccountInstance = AccountInstance.getInstance(i);
        this.currentAccount = i;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final AccountInstance getAccountInstance() {
        return this.parentAccountInstance;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final MessagesController getMessagesController() {
        return this.parentAccountInstance.getMessagesController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ContactsController getContactsController() {
        return this.parentAccountInstance.getContactsController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final MediaDataController getMediaDataController() {
        return this.parentAccountInstance.getMediaDataController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final ConnectionsManager getConnectionsManager() {
        return this.parentAccountInstance.getConnectionsManager();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final LocationController getLocationController() {
        return this.parentAccountInstance.getLocationController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final NotificationsController getNotificationsController() {
        return this.parentAccountInstance.getNotificationsController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final NotificationCenter getNotificationCenter() {
        return this.parentAccountInstance.getNotificationCenter();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final UserConfig getUserConfig() {
        return this.parentAccountInstance.getUserConfig();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final MessagesStorage getMessagesStorage() {
        return this.parentAccountInstance.getMessagesStorage();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final DownloadController getDownloadController() {
        return this.parentAccountInstance.getDownloadController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final SendMessagesHelper getSendMessagesHelper() {
        return this.parentAccountInstance.getSendMessagesHelper();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final SecretChatHelper getSecretChatHelper() {
        return this.parentAccountInstance.getSecretChatHelper();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final StatsController getStatsController() {
        return this.parentAccountInstance.getStatsController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final FileLoader getFileLoader() {
        return this.parentAccountInstance.getFileLoader();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final FileRefController getFileRefController() {
        return this.parentAccountInstance.getFileRefController();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final MemberRequestsController getMemberRequestsController() {
        return this.parentAccountInstance.getMemberRequestsController();
    }
}

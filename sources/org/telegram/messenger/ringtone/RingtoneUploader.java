package org.telegram.messenger.ringtone;

import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class RingtoneUploader implements NotificationCenter.NotificationCenterDelegate {
    private boolean canceled;
    private int currentAccount;
    public final String filePath;

    public RingtoneUploader(String filePath2, int currentAccount2) {
        this.currentAccount = currentAccount2;
        this.filePath = filePath2;
        subscribe();
        FileLoader.getInstance(currentAccount2).uploadFile(filePath2, false, true, 50331648);
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileUploaded) {
            String location = args[0];
            if (!this.canceled && location.equals(this.filePath)) {
                TLRPC.InputFile file = args[1];
                TLRPC.TL_account_uploadRingtone req = new TLRPC.TL_account_uploadRingtone();
                req.file = file;
                req.file_name = file.name;
                req.mime_type = FileLoader.getFileExtension(new File(file.name));
                if ("ogg".equals(req.mime_type)) {
                    req.mime_type = "audio/ogg";
                } else {
                    req.mime_type = "audio/mpeg";
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(req, new RingtoneUploader$$ExternalSyntheticLambda2(this));
            }
        }
    }

    /* renamed from: lambda$didReceivedNotification$1$org-telegram-messenger-ringtone-RingtoneUploader  reason: not valid java name */
    public /* synthetic */ void m2423x23b86CLASSNAME(TLObject response, TLRPC.TL_error error) {
        AndroidUtilities.runOnUIThread(new RingtoneUploader$$ExternalSyntheticLambda0(this, response, error));
    }

    /* renamed from: lambda$didReceivedNotification$0$org-telegram-messenger-ringtone-RingtoneUploader  reason: not valid java name */
    public /* synthetic */ void m2422x320eCLASSNAME(TLObject response, TLRPC.TL_error error) {
        if (response != null) {
            onComplete((TLRPC.Document) response);
        } else {
            error(error);
        }
        unsubscribe();
    }

    private void subscribe() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.fileUploadFailed);
    }

    private void unsubscribe() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.fileUploadFailed);
    }

    private void onComplete(TLRPC.Document document) {
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, document, false);
    }

    public void cancel() {
        this.canceled = true;
        unsubscribe();
        FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.filePath, false);
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, (TLRPC.Document) null, true);
    }

    public void error(TLRPC.TL_error error) {
        unsubscribe();
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, (TLRPC.Document) null, true);
        if (error != null) {
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(new RingtoneUploader$$ExternalSyntheticLambda1(this, error));
        }
    }

    /* renamed from: lambda$error$2$org-telegram-messenger-ringtone-RingtoneUploader  reason: not valid java name */
    public /* synthetic */ void m2424lambda$error$2$orgtelegrammessengerringtoneRingtoneUploader(TLRPC.TL_error error) {
        if (error.text.equals("RINGTONE_DURATION_TOO_LONG")) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", NUM, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneDurationMax)));
        } else if (error.text.equals("RINGTONE_SIZE_TOO_BIG")) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", NUM, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneSizeMax / 1024)));
        } else {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("InvalidFormatError", NUM, new Object[0]), LocaleController.formatString("ErrorRingtoneInvalidFormat", NUM, new Object[0]));
        }
    }
}

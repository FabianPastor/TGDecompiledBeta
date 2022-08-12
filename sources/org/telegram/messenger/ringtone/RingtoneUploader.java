package org.telegram.messenger.ringtone;

import java.io.File;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$InputFile;
import org.telegram.tgnet.TLRPC$TL_account_uploadRingtone;
import org.telegram.tgnet.TLRPC$TL_error;

public class RingtoneUploader implements NotificationCenter.NotificationCenterDelegate {
    private boolean canceled;
    private int currentAccount;
    public final String filePath;

    public RingtoneUploader(String str, int i) {
        this.currentAccount = i;
        this.filePath = str;
        subscribe();
        FileLoader.getInstance(i).uploadFile(str, false, true, 50331648);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileUploaded) {
            String str = objArr[0];
            if (!this.canceled && str.equals(this.filePath)) {
                TLRPC$InputFile tLRPC$InputFile = objArr[1];
                TLRPC$TL_account_uploadRingtone tLRPC$TL_account_uploadRingtone = new TLRPC$TL_account_uploadRingtone();
                tLRPC$TL_account_uploadRingtone.file = tLRPC$InputFile;
                tLRPC$TL_account_uploadRingtone.file_name = tLRPC$InputFile.name;
                String fileExtension = FileLoader.getFileExtension(new File(tLRPC$InputFile.name));
                tLRPC$TL_account_uploadRingtone.mime_type = fileExtension;
                if ("ogg".equals(fileExtension)) {
                    tLRPC$TL_account_uploadRingtone.mime_type = "audio/ogg";
                } else {
                    tLRPC$TL_account_uploadRingtone.mime_type = "audio/mpeg";
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLRPC$TL_account_uploadRingtone, new RingtoneUploader$$ExternalSyntheticLambda2(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$1(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new RingtoneUploader$$ExternalSyntheticLambda0(this, tLObject, tLRPC$TL_error));
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$didReceivedNotification$0(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        if (tLObject != null) {
            onComplete((TLRPC$Document) tLObject);
        } else {
            error(tLRPC$TL_error);
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

    private void onComplete(TLRPC$Document tLRPC$Document) {
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, tLRPC$Document, false);
    }

    public void cancel() {
        this.canceled = true;
        unsubscribe();
        FileLoader.getInstance(this.currentAccount).cancelFileUpload(this.filePath, false);
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, (TLRPC$Document) null, true);
    }

    public void error(TLRPC$TL_error tLRPC$TL_error) {
        unsubscribe();
        MediaDataController.getInstance(this.currentAccount).onRingtoneUploaded(this.filePath, (TLRPC$Document) null, true);
        if (tLRPC$TL_error != null) {
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(new RingtoneUploader$$ExternalSyntheticLambda1(this, tLRPC$TL_error));
        }
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$error$2(TLRPC$TL_error tLRPC$TL_error) {
        if (tLRPC$TL_error.text.equals("RINGTONE_DURATION_TOO_LONG")) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLongError", R.string.TooLongError, new Object[0]), LocaleController.formatString("ErrorRingtoneDurationTooLong", R.string.ErrorRingtoneDurationTooLong, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneDurationMax)));
        } else if (tLRPC$TL_error.text.equals("RINGTONE_SIZE_TOO_BIG")) {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("TooLargeError", R.string.TooLargeError, new Object[0]), LocaleController.formatString("ErrorRingtoneSizeTooBig", R.string.ErrorRingtoneSizeTooBig, Integer.valueOf(MessagesController.getInstance(this.currentAccount).ringtoneSizeMax / 1024)));
        } else {
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.showBulletin, 4, LocaleController.formatString("InvalidFormatError", R.string.InvalidFormatError, new Object[0]), LocaleController.formatString("ErrorRingtoneInvalidFormat", R.string.ErrorRingtoneInvalidFormat, new Object[0]));
        }
    }
}

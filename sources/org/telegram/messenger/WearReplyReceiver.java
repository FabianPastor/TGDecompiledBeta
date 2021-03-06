package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import androidx.core.app.RemoteInput;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$MessageEntity;
import org.telegram.tgnet.TLRPC$ReplyMarkup;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$WebPage;

public class WearReplyReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        Bundle resultsFromIntent = RemoteInput.getResultsFromIntent(intent);
        if (resultsFromIntent != null) {
            CharSequence charSequence = resultsFromIntent.getCharSequence("extra_voice_reply");
            if (!TextUtils.isEmpty(charSequence)) {
                long longExtra = intent.getLongExtra("dialog_id", 0);
                int intExtra = intent.getIntExtra("max_id", 0);
                int intExtra2 = intent.getIntExtra("currentAccount", 0);
                if (longExtra != 0 && intExtra != 0 && UserConfig.isValidAccount(intExtra2)) {
                    int i = (int) longExtra;
                    AccountInstance instance = AccountInstance.getInstance(intExtra2);
                    if (i > 0) {
                        if (instance.getMessagesController().getUser(Integer.valueOf(i)) == null) {
                            Utilities.globalQueue.postRunnable(new Runnable(instance, i, charSequence, longExtra, intExtra) {
                                public final /* synthetic */ AccountInstance f$1;
                                public final /* synthetic */ int f$2;
                                public final /* synthetic */ CharSequence f$3;
                                public final /* synthetic */ long f$4;
                                public final /* synthetic */ int f$5;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                    this.f$4 = r5;
                                    this.f$5 = r7;
                                }

                                public final void run() {
                                    WearReplyReceiver.this.lambda$onReceive$1$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                                }
                            });
                            return;
                        }
                    } else if (i < 0 && instance.getMessagesController().getChat(Integer.valueOf(-i)) == null) {
                        Utilities.globalQueue.postRunnable(new Runnable(instance, i, charSequence, longExtra, intExtra) {
                            public final /* synthetic */ AccountInstance f$1;
                            public final /* synthetic */ int f$2;
                            public final /* synthetic */ CharSequence f$3;
                            public final /* synthetic */ long f$4;
                            public final /* synthetic */ int f$5;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r7;
                            }

                            public final void run() {
                                WearReplyReceiver.this.lambda$onReceive$3$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
                            }
                        });
                        return;
                    }
                    sendMessage(instance, charSequence, longExtra, intExtra);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onReceive$1 */
    public /* synthetic */ void lambda$onReceive$1$WearReplyReceiver(AccountInstance accountInstance, int i, CharSequence charSequence, long j, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable(accountInstance, accountInstance.getMessagesStorage().getUserSync(i), charSequence, j, i2) {
            public final /* synthetic */ AccountInstance f$1;
            public final /* synthetic */ TLRPC$User f$2;
            public final /* synthetic */ CharSequence f$3;
            public final /* synthetic */ long f$4;
            public final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
            }

            public final void run() {
                WearReplyReceiver.this.lambda$onReceive$0$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onReceive$0 */
    public /* synthetic */ void lambda$onReceive$0$WearReplyReceiver(AccountInstance accountInstance, TLRPC$User tLRPC$User, CharSequence charSequence, long j, int i) {
        accountInstance.getMessagesController().putUser(tLRPC$User, true);
        sendMessage(accountInstance, charSequence, j, i);
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onReceive$3 */
    public /* synthetic */ void lambda$onReceive$3$WearReplyReceiver(AccountInstance accountInstance, int i, CharSequence charSequence, long j, int i2) {
        AndroidUtilities.runOnUIThread(new Runnable(accountInstance, accountInstance.getMessagesStorage().getChatSync(-i), charSequence, j, i2) {
            public final /* synthetic */ AccountInstance f$1;
            public final /* synthetic */ TLRPC$Chat f$2;
            public final /* synthetic */ CharSequence f$3;
            public final /* synthetic */ long f$4;
            public final /* synthetic */ int f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r7;
            }

            public final void run() {
                WearReplyReceiver.this.lambda$onReceive$2$WearReplyReceiver(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        });
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$onReceive$2 */
    public /* synthetic */ void lambda$onReceive$2$WearReplyReceiver(AccountInstance accountInstance, TLRPC$Chat tLRPC$Chat, CharSequence charSequence, long j, int i) {
        accountInstance.getMessagesController().putChat(tLRPC$Chat, true);
        sendMessage(accountInstance, charSequence, j, i);
    }

    private void sendMessage(AccountInstance accountInstance, CharSequence charSequence, long j, int i) {
        accountInstance.getSendMessagesHelper().sendMessage(charSequence.toString(), j, (MessageObject) null, (MessageObject) null, (TLRPC$WebPage) null, true, (ArrayList<TLRPC$MessageEntity>) null, (TLRPC$ReplyMarkup) null, (HashMap<String, String>) null, true, 0, (MessageObject.SendAnimationData) null);
        accountInstance.getMessagesController().markDialogAsRead(j, i, i, 0, false, 0, 0, true, 0);
    }
}

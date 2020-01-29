package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.telegram.tgnet.TLRPC;

public class AutoMessageHeardReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ApplicationLoader.postInitApplication();
        long longExtra = intent.getLongExtra("dialog_id", 0);
        int intExtra = intent.getIntExtra("max_id", 0);
        int intExtra2 = intent.getIntExtra("currentAccount", 0);
        if (longExtra != 0 && intExtra != 0) {
            int i = (int) longExtra;
            AccountInstance instance = AccountInstance.getInstance(intExtra2);
            if (i > 0) {
                if (instance.getMessagesController().getUser(Integer.valueOf(i)) == null) {
                    Utilities.globalQueue.postRunnable(new Runnable(i, intExtra2, longExtra, intExtra) {
                        private final /* synthetic */ int f$1;
                        private final /* synthetic */ int f$2;
                        private final /* synthetic */ long f$3;
                        private final /* synthetic */ int f$4;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r6;
                        }

                        public final void run() {
                            AndroidUtilities.runOnUIThread(new Runnable(AccountInstance.this.getMessagesStorage().getUserSync(this.f$1), this.f$2, this.f$3, this.f$4) {
                                private final /* synthetic */ TLRPC.User f$1;
                                private final /* synthetic */ int f$2;
                                private final /* synthetic */ long f$3;
                                private final /* synthetic */ int f$4;

                                {
                                    this.f$1 = r2;
                                    this.f$2 = r3;
                                    this.f$3 = r4;
                                    this.f$4 = r6;
                                }

                                public final void run() {
                                    AutoMessageHeardReceiver.lambda$null$0(AccountInstance.this, this.f$1, this.f$2, this.f$3, this.f$4);
                                }
                            });
                        }
                    });
                    return;
                }
            } else if (i < 0 && instance.getMessagesController().getChat(Integer.valueOf(-i)) == null) {
                Utilities.globalQueue.postRunnable(new Runnable(i, intExtra2, longExtra, intExtra) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ long f$3;
                    private final /* synthetic */ int f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r6;
                    }

                    public final void run() {
                        AndroidUtilities.runOnUIThread(new Runnable(AccountInstance.this.getMessagesStorage().getChatSync(-this.f$1), this.f$2, this.f$3, this.f$4) {
                            private final /* synthetic */ TLRPC.Chat f$1;
                            private final /* synthetic */ int f$2;
                            private final /* synthetic */ long f$3;
                            private final /* synthetic */ int f$4;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r6;
                            }

                            public final void run() {
                                AutoMessageHeardReceiver.lambda$null$2(AccountInstance.this, this.f$1, this.f$2, this.f$3, this.f$4);
                            }
                        });
                    }
                });
                return;
            }
            MessagesController.getInstance(intExtra2).markDialogAsRead(longExtra, intExtra, intExtra, 0, false, 0, true, 0);
        }
    }

    static /* synthetic */ void lambda$null$0(AccountInstance accountInstance, TLRPC.User user, int i, long j, int i2) {
        TLRPC.User user2 = user;
        accountInstance.getMessagesController().putUser(user, true);
        MessagesController.getInstance(i).markDialogAsRead(j, i2, i2, 0, false, 0, true, 0);
    }

    static /* synthetic */ void lambda$null$2(AccountInstance accountInstance, TLRPC.Chat chat, int i, long j, int i2) {
        TLRPC.Chat chat2 = chat;
        accountInstance.getMessagesController().putChat(chat, true);
        MessagesController.getInstance(i).markDialogAsRead(j, i2, i2, 0, false, 0, true, 0);
    }
}

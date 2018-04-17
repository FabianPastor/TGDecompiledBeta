package org.telegram.messenger;

import android.text.TextUtils;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.Builder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.Channel;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.User;

public class WearDataLayerListenerService extends WearableListenerService {
    private static boolean watchConnected;
    private int currentAccount = UserConfig.selectedAccount;

    /* renamed from: org.telegram.messenger.WearDataLayerListenerService$9 */
    static class C18299 implements OnCompleteListener<CapabilityInfo> {
        C18299() {
        }

        public void onComplete(Task<CapabilityInfo> task) {
            WearDataLayerListenerService.watchConnected = false;
            try {
                CapabilityInfo capabilityInfo = (CapabilityInfo) task.getResult();
                if (capabilityInfo != null) {
                    for (Node node : capabilityInfo.getNodes()) {
                        if (node.isNearby()) {
                            WearDataLayerListenerService.watchConnected = true;
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("WearableDataLayer service created");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m0d("WearableDataLayer service destroyed");
        }
    }

    public void onChannelOpened(Channel ch) {
        Channel channel = ch;
        GoogleApiClient apiClient = new Builder(this).addApi(Wearable.API).build();
        if (apiClient.blockingConnect().isSuccess()) {
            String path = ch.getPath();
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("wear channel path: ");
                stringBuilder.append(path);
                FileLog.m0d(stringBuilder.toString());
            }
            DataInputStream in;
            DataOutputStream out;
            try {
                final File photo;
                final CyclicBarrier barrier;
                if ("/getCurrentUser".equals(path)) {
                    DataOutputStream out2 = new DataOutputStream(new BufferedOutputStream(((GetOutputStreamResult) channel.getOutputStream(apiClient).await()).getOutputStream()));
                    if (UserConfig.getInstance(r1.currentAccount).isClientActivated()) {
                        final User user = UserConfig.getInstance(r1.currentAccount).getCurrentUser();
                        out2.writeInt(user.id);
                        out2.writeUTF(user.first_name);
                        out2.writeUTF(user.last_name);
                        out2.writeUTF(user.phone);
                        if (user.photo != null) {
                            photo = FileLoader.getPathToAttach(user.photo.photo_small, true);
                            barrier = new CyclicBarrier(2);
                            if (!photo.exists()) {
                                final NotificationCenterDelegate listener = new NotificationCenterDelegate() {
                                    public void didReceivedNotification(int id, int account, Object... args) {
                                        if (id == NotificationCenter.FileDidLoaded) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                StringBuilder stringBuilder = new StringBuilder();
                                                stringBuilder.append("file loaded: ");
                                                stringBuilder.append(args[0]);
                                                stringBuilder.append(" ");
                                                stringBuilder.append(args[0].getClass().getName());
                                                FileLog.m0d(stringBuilder.toString());
                                            }
                                            if (args[0].equals(photo.getName())) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.m1e("LOADED USER PHOTO");
                                                }
                                                try {
                                                    barrier.await(10, TimeUnit.MILLISECONDS);
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                    }
                                };
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).addObserver(listener, NotificationCenter.FileDidLoaded);
                                        FileLoader.getInstance(WearDataLayerListenerService.this.currentAccount).loadFile(user.photo.photo_small, null, 0, 1);
                                    }
                                });
                                try {
                                    barrier.await(10, TimeUnit.SECONDS);
                                } catch (Exception e) {
                                }
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).removeObserver(listener, NotificationCenter.FileDidLoaded);
                                    }
                                });
                            }
                            if (!photo.exists() || photo.length() > 52428800) {
                                out2.writeInt(0);
                            } else {
                                byte[] photoData = new byte[((int) photo.length())];
                                FileInputStream photoIn = new FileInputStream(photo);
                                new DataInputStream(photoIn).readFully(photoData);
                                photoIn.close();
                                out2.writeInt(photoData.length);
                                out2.write(photoData);
                            }
                        } else {
                            out2.writeInt(0);
                        }
                    } else {
                        out2.writeInt(0);
                    }
                    out2.flush();
                    out2.close();
                } else if ("/waitForAuthCode".equals(path)) {
                    ConnectionsManager.getInstance(r1.currentAccount).setAppPaused(false, false);
                    final String[] code = new String[]{null};
                    barrier = new CyclicBarrier(2);
                    final NotificationCenterDelegate listener2 = new NotificationCenterDelegate() {
                        public void didReceivedNotification(int id, int account, Object... args) {
                            if (id == NotificationCenter.didReceivedNewMessages && ((Long) args[0]).longValue() == 777000) {
                                ArrayList<MessageObject> arr = args[1];
                                if (arr.size() > 0) {
                                    MessageObject msg = (MessageObject) arr.get(0);
                                    if (!TextUtils.isEmpty(msg.messageText)) {
                                        Matcher matcher = Pattern.compile("[0-9]+").matcher(msg.messageText);
                                        if (matcher.find()) {
                                            code[0] = matcher.group();
                                            try {
                                                barrier.await(10, TimeUnit.MILLISECONDS);
                                            } catch (Exception e) {
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    };
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).addObserver(listener2, NotificationCenter.didReceivedNewMessages);
                        }
                    });
                    try {
                        barrier.await(15, TimeUnit.SECONDS);
                    } catch (Exception e2) {
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).removeObserver(listener2, NotificationCenter.didReceivedNewMessages);
                        }
                    });
                    DataOutputStream out3 = new DataOutputStream(((GetOutputStreamResult) channel.getOutputStream(apiClient).await()).getOutputStream());
                    if (code[0] != null) {
                        out3.writeUTF(code[0]);
                    } else {
                        out3.writeUTF(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    out3.flush();
                    out3.close();
                    ConnectionsManager.getInstance(r1.currentAccount).setAppPaused(true, false);
                } else if ("/getChatPhoto".equals(path)) {
                    in = new DataInputStream(((GetInputStreamResult) channel.getInputStream(apiClient).await()).getInputStream());
                    out = new DataOutputStream(((GetOutputStreamResult) channel.getOutputStream(apiClient).await()).getOutputStream());
                    String _req = in.readUTF();
                    JSONObject req = new JSONObject(_req);
                    int chatID = req.getInt("chat_id");
                    int accountID = req.getInt("account_id");
                    int currentAccount = -1;
                    for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                        if (UserConfig.getInstance(i).getClientUserId() == accountID) {
                            currentAccount = i;
                            break;
                        }
                    }
                    if (currentAccount != -1) {
                        FileLocation location = null;
                        if (chatID > 0) {
                            User user2 = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(chatID));
                            if (!(user2 == null || user2.photo == null)) {
                                location = user2.photo.photo_small;
                            }
                        } else {
                            Chat chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(-chatID));
                            if (!(chat == null || chat.photo == null)) {
                                location = chat.photo.photo_small;
                            }
                        }
                        if (location != null) {
                            photo = FileLoader.getPathToAttach(location, true);
                            if (!photo.exists() || photo.length() >= 102400) {
                                out.writeInt(0);
                            } else {
                                out.writeInt((int) photo.length());
                                FileInputStream fin = new FileInputStream(photo);
                                _req = new byte[10240];
                                while (true) {
                                    int read = fin.read(_req);
                                    int read2 = read;
                                    if (read <= 0) {
                                        break;
                                    }
                                    out.write(_req, 0, read2);
                                    WearDataLayerListenerService wearDataLayerListenerService = this;
                                }
                                fin.close();
                            }
                        } else {
                            out.writeInt(0);
                        }
                    } else {
                        out.writeInt(0);
                    }
                    out.flush();
                    in.close();
                    out.close();
                }
            } catch (Exception e3) {
                in.close();
            } catch (Exception e4) {
                Exception x = e4;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.m2e("error processing wear request", x);
                }
            } catch (Throwable th) {
                Throwable th2 = th;
                in.close();
                out.close();
            }
            channel.close(apiClient).await();
            apiClient.disconnect();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.m0d("WearableDataLayer channel thread exiting");
            }
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.m1e("failed to connect google api client");
        }
    }

    public void onMessageReceived(final MessageEvent messageEvent) {
        if ("/reply".equals(messageEvent.getPath())) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    Throwable e;
                    Throwable x;
                    try {
                        ApplicationLoader.postInitApplication();
                        try {
                            JSONObject r = new JSONObject(new String(messageEvent.getData(), C0542C.UTF8_NAME));
                            CharSequence text = r.getString(MimeTypes.BASE_TYPE_TEXT);
                            if (text != null) {
                                if (text.length() != 0) {
                                    long dialog_id = r.getLong("chat_id");
                                    int max_id = r.getInt("max_id");
                                    int currentAccount = -1;
                                    int accountID = r.getInt("account_id");
                                    for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                                        if (UserConfig.getInstance(i).getClientUserId() == accountID) {
                                            currentAccount = i;
                                            break;
                                        }
                                    }
                                    int currentAccount2 = currentAccount;
                                    int i2;
                                    int i3;
                                    if (dialog_id == 0 || max_id == 0) {
                                        i2 = currentAccount2;
                                        i3 = accountID;
                                    } else if (currentAccount2 == -1) {
                                        i2 = currentAccount2;
                                        i3 = accountID;
                                    } else {
                                        int currentAccount3 = currentAccount2;
                                        SendMessagesHelper.getInstance(currentAccount2).sendMessage(text.toString(), dialog_id, null, null, true, null, null, null);
                                        accountID = currentAccount3;
                                        MessagesController.getInstance(accountID).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, 1);
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            x = e;
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.m3e(x);
                            }
                        }
                    } catch (Exception e3) {
                        e = e3;
                        C05207 c05207 = this;
                        x = e;
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.m3e(x);
                        }
                    }
                }
            });
        }
    }

    public static void sendMessageToWatch(final String path, final byte[] data, String capability) {
        Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability(capability, 1).addOnCompleteListener(new OnCompleteListener<CapabilityInfo>() {
            public void onComplete(Task<CapabilityInfo> task) {
                CapabilityInfo info = (CapabilityInfo) task.getResult();
                if (info != null) {
                    MessageClient mc = Wearable.getMessageClient(ApplicationLoader.applicationContext);
                    for (Node node : info.getNodes()) {
                        mc.sendMessage(node.getId(), path, data);
                    }
                }
            }
        });
    }

    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        if ("remote_notifications".equals(capabilityInfo.getName())) {
            watchConnected = false;
            for (Node node : capabilityInfo.getNodes()) {
                if (node.isNearby()) {
                    watchConnected = true;
                }
            }
        }
    }

    public static void updateWatchConnectionState() {
        Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability("remote_notifications", 1).addOnCompleteListener(new C18299());
    }

    public static boolean isWatchConnected() {
        return watchConnected;
    }
}

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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;

public class WearDataLayerListenerService extends WearableListenerService {
    private static boolean watchConnected;
    private int currentAccount = UserConfig.selectedAccount;

    public void onCreate() {
        super.onCreate();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("WearableDataLayer service created");
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("WearableDataLayer service destroyed");
        }
    }

    public void onChannelOpened(Channel ch) {
        GoogleApiClient apiClient = new Builder(this).addApi(Wearable.API).build();
        if (apiClient.blockingConnect().isSuccess()) {
            String path = ch.getPath();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("wear channel path: " + path);
            }
            DataOutputStream dataOutputStream;
            DataInputStream dataInputStream;
            try {
                User user;
                final CyclicBarrier barrier;
                NotificationCenterDelegate anonymousClass1;
                final NotificationCenterDelegate notificationCenterDelegate;
                if ("/getCurrentUser".equals(path)) {
                    dataOutputStream = new DataOutputStream(new BufferedOutputStream(((GetOutputStreamResult) ch.getOutputStream(apiClient).await()).getOutputStream()));
                    if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
                        user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                        dataOutputStream.writeInt(user.id);
                        dataOutputStream.writeUTF(user.first_name);
                        dataOutputStream.writeUTF(user.last_name);
                        dataOutputStream.writeUTF(user.phone);
                        if (user.photo != null) {
                            File photo = FileLoader.getPathToAttach(user.photo.photo_small, true);
                            barrier = new CyclicBarrier(2);
                            if (!photo.exists()) {
                                final File file = photo;
                                anonymousClass1 = new NotificationCenterDelegate() {
                                    public void didReceivedNotification(int id, int account, Object... args) {
                                        if (id == NotificationCenter.FileDidLoaded) {
                                            if (BuildVars.LOGS_ENABLED) {
                                                FileLog.d("file loaded: " + args[0] + " " + args[0].getClass().getName());
                                            }
                                            if (args[0].equals(file.getName())) {
                                                if (BuildVars.LOGS_ENABLED) {
                                                    FileLog.e("LOADED USER PHOTO");
                                                }
                                                try {
                                                    barrier.await(10, TimeUnit.MILLISECONDS);
                                                } catch (Exception e) {
                                                }
                                            }
                                        }
                                    }
                                };
                                notificationCenterDelegate = anonymousClass1;
                                final User user2 = user;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).addObserver(notificationCenterDelegate, NotificationCenter.FileDidLoaded);
                                        FileLoader.getInstance(WearDataLayerListenerService.this.currentAccount).loadFile(user2.photo.photo_small, null, 0, 1);
                                    }
                                });
                                try {
                                    barrier.await(10, TimeUnit.SECONDS);
                                } catch (Exception e) {
                                }
                                notificationCenterDelegate = anonymousClass1;
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    public void run() {
                                        NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).removeObserver(notificationCenterDelegate, NotificationCenter.FileDidLoaded);
                                    }
                                });
                            }
                            if (!photo.exists() || photo.length() > 52428800) {
                                dataOutputStream.writeInt(0);
                            } else {
                                byte[] photoData = new byte[((int) photo.length())];
                                InputStream fileInputStream = new FileInputStream(photo);
                                new DataInputStream(fileInputStream).readFully(photoData);
                                fileInputStream.close();
                                dataOutputStream.writeInt(photoData.length);
                                dataOutputStream.write(photoData);
                            }
                        } else {
                            dataOutputStream.writeInt(0);
                        }
                    } else {
                        dataOutputStream.writeInt(0);
                    }
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    ch.close(apiClient).await();
                    apiClient.disconnect();
                    if (!BuildVars.LOGS_ENABLED) {
                        FileLog.d("WearableDataLayer channel thread exiting");
                    }
                } else if ("/waitForAuthCode".equals(path)) {
                    ConnectionsManager.getInstance(this.currentAccount).setAppPaused(false, false);
                    final String[] code = new String[]{null};
                    barrier = new CyclicBarrier(2);
                    anonymousClass1 = new NotificationCenterDelegate() {
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
                    notificationCenterDelegate = anonymousClass1;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).addObserver(notificationCenterDelegate, NotificationCenter.didReceivedNewMessages);
                        }
                    });
                    try {
                        barrier.await(15, TimeUnit.SECONDS);
                    } catch (Exception e2) {
                    }
                    notificationCenterDelegate = anonymousClass1;
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            NotificationCenter.getInstance(WearDataLayerListenerService.this.currentAccount).removeObserver(notificationCenterDelegate, NotificationCenter.didReceivedNewMessages);
                        }
                    });
                    dataOutputStream = new DataOutputStream(((GetOutputStreamResult) ch.getOutputStream(apiClient).await()).getOutputStream());
                    if (code[0] != null) {
                        dataOutputStream.writeUTF(code[0]);
                    } else {
                        dataOutputStream.writeUTF(TtmlNode.ANONYMOUS_REGION_ID);
                    }
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    ConnectionsManager.getInstance(this.currentAccount).setAppPaused(true, false);
                    ch.close(apiClient).await();
                    apiClient.disconnect();
                    if (!BuildVars.LOGS_ENABLED) {
                        FileLog.d("WearableDataLayer channel thread exiting");
                    }
                } else {
                    if ("/getChatPhoto".equals(path)) {
                        dataInputStream = new DataInputStream(((GetInputStreamResult) ch.getInputStream(apiClient).await()).getInputStream());
                        dataOutputStream = new DataOutputStream(((GetOutputStreamResult) ch.getOutputStream(apiClient).await()).getOutputStream());
                        JSONObject jSONObject = new JSONObject(dataInputStream.readUTF());
                        int chatID = jSONObject.getInt("chat_id");
                        int accountID = jSONObject.getInt("account_id");
                        int currentAccount = -1;
                        for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                            if (UserConfig.getInstance(i).getClientUserId() == accountID) {
                                currentAccount = i;
                                break;
                            }
                        }
                        if (currentAccount != -1) {
                            TLObject location = null;
                            if (chatID > 0) {
                                user = MessagesController.getInstance(currentAccount).getUser(Integer.valueOf(chatID));
                                if (!(user == null || user.photo == null)) {
                                    location = user.photo.photo_small;
                                }
                            } else {
                                Chat chat = MessagesController.getInstance(currentAccount).getChat(Integer.valueOf(-chatID));
                                if (!(chat == null || chat.photo == null)) {
                                    location = chat.photo.photo_small;
                                }
                            }
                            if (location != null) {
                                File file2 = FileLoader.getPathToAttach(location, true);
                                if (!file2.exists() || file2.length() >= 102400) {
                                    dataOutputStream.writeInt(0);
                                } else {
                                    dataOutputStream.writeInt((int) file2.length());
                                    FileInputStream fin = new FileInputStream(file2);
                                    byte[] buf = new byte[10240];
                                    while (true) {
                                        int read = fin.read(buf);
                                        if (read <= 0) {
                                            break;
                                        }
                                        dataOutputStream.write(buf, 0, read);
                                    }
                                    fin.close();
                                }
                            } else {
                                dataOutputStream.writeInt(0);
                            }
                        } else {
                            dataOutputStream.writeInt(0);
                        }
                        dataOutputStream.flush();
                        dataInputStream.close();
                        dataOutputStream.close();
                    }
                    ch.close(apiClient).await();
                    apiClient.disconnect();
                    if (!BuildVars.LOGS_ENABLED) {
                        FileLog.d("WearableDataLayer channel thread exiting");
                    }
                }
            } catch (Exception e3) {
                dataInputStream.close();
                dataOutputStream.close();
            } catch (Throwable x) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("error processing wear request", x);
                }
            } catch (Throwable th) {
                dataInputStream.close();
                dataOutputStream.close();
            }
        } else if (BuildVars.LOGS_ENABLED) {
            FileLog.e("failed to connect google api client");
        }
    }

    public void onMessageReceived(final MessageEvent messageEvent) {
        if ("/reply".equals(messageEvent.getPath())) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                public void run() {
                    try {
                        ApplicationLoader.postInitApplication();
                        JSONObject jSONObject = new JSONObject(new String(messageEvent.getData(), C.UTF8_NAME));
                        CharSequence text = jSONObject.getString(MimeTypes.BASE_TYPE_TEXT);
                        if (text != null && text.length() != 0) {
                            long dialog_id = jSONObject.getLong("chat_id");
                            int max_id = jSONObject.getInt("max_id");
                            int currentAccount = -1;
                            int accountID = jSONObject.getInt("account_id");
                            for (int i = 0; i < UserConfig.getActivatedAccountsCount(); i++) {
                                if (UserConfig.getInstance(i).getClientUserId() == accountID) {
                                    currentAccount = i;
                                    break;
                                }
                            }
                            if (dialog_id != 0 && max_id != 0 && currentAccount != -1) {
                                SendMessagesHelper.getInstance(currentAccount).sendMessage(text.toString(), dialog_id, null, null, true, null, null, null);
                                MessagesController.getInstance(currentAccount).markDialogAsRead(dialog_id, max_id, max_id, 0, false, 0, true);
                            }
                        }
                    } catch (Throwable x) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.e(x);
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
        Wearable.getCapabilityClient(ApplicationLoader.applicationContext).getCapability("remote_notifications", 1).addOnCompleteListener(new OnCompleteListener<CapabilityInfo>() {
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
        });
    }

    public static boolean isWatchConnected() {
        return watchConnected;
    }
}

package org.telegram.ui.Components.voip;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.JoinCallByUrlAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;

public class VoIPHelper {
    private static final int VOIP_SUPPORT_ID = 4244000;
    public static long lastCallTime = 0;

    public static void startCall(TLRPC.User user, boolean videoCall, boolean canVideoCall, Activity activity, TLRPC.UserFull userFull, AccountInstance accountInstance) {
        String str;
        int i;
        String str2;
        int i2;
        TLRPC.User user2 = user;
        Activity activity2 = activity;
        TLRPC.UserFull userFull2 = userFull;
        boolean isAirplaneMode = true;
        if (userFull2 != null && userFull2.phone_calls_private) {
            new AlertDialog.Builder((Context) activity2).setTitle(LocaleController.getString("VoipFailed", NUM)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, ContactsController.formatName(user2.first_name, user2.last_name)))).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                isAirplaneMode = false;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
            if (isAirplaneMode) {
                i = NUM;
                str = "VoipOfflineAirplaneTitle";
            } else {
                i = NUM;
                str = "VoipOfflineTitle";
            }
            AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i));
            if (isAirplaneMode) {
                i2 = NUM;
                str2 = "VoipOfflineAirplane";
            } else {
                i2 = NUM;
                str2 = "VoipOffline";
            }
            AlertDialog.Builder bldr = title.setMessage(LocaleController.getString(str2, i2)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            if (isAirplaneMode) {
                Intent settingsIntent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (settingsIntent.resolveActivity(activity.getPackageManager()) != null) {
                    bldr.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", NUM), new VoIPHelper$$ExternalSyntheticLambda11(activity2, settingsIntent));
                }
            }
            try {
                bldr.show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> permissions = new ArrayList<>();
            if (activity2.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                permissions.add("android.permission.RECORD_AUDIO");
            }
            if (videoCall && activity2.checkSelfPermission("android.permission.CAMERA") != 0) {
                permissions.add("android.permission.CAMERA");
            }
            if (permissions.isEmpty()) {
                initiateCall(user, (TLRPC.Chat) null, (String) null, videoCall, canVideoCall, false, activity, (BaseFragment) null, accountInstance);
            } else {
                activity2.requestPermissions((String[]) permissions.toArray(new String[0]), videoCall ? 102 : 101);
            }
        } else {
            initiateCall(user, (TLRPC.Chat) null, (String) null, videoCall, canVideoCall, false, activity, (BaseFragment) null, accountInstance);
        }
    }

    public static void startCall(TLRPC.Chat chat, TLRPC.InputPeer peer, String hash, boolean createCall, Activity activity, BaseFragment fragment, AccountInstance accountInstance) {
        String str;
        int i;
        String str2;
        int i2;
        if (activity != null) {
            boolean z = false;
            if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
                if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) != 0) {
                    z = true;
                }
                boolean isAirplaneMode = z;
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                if (isAirplaneMode) {
                    i = NUM;
                    str = "VoipOfflineAirplaneTitle";
                } else {
                    i = NUM;
                    str = "VoipOfflineTitle";
                }
                AlertDialog.Builder title = builder.setTitle(LocaleController.getString(str, i));
                if (isAirplaneMode) {
                    i2 = NUM;
                    str2 = "VoipGroupOfflineAirplane";
                } else {
                    i2 = NUM;
                    str2 = "VoipGroupOffline";
                }
                AlertDialog.Builder bldr = title.setMessage(LocaleController.getString(str2, i2)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                if (isAirplaneMode) {
                    Intent settingsIntent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                    if (settingsIntent.resolveActivity(activity.getPackageManager()) != null) {
                        bldr.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", NUM), new VoIPHelper$$ExternalSyntheticLambda13(activity, settingsIntent));
                    }
                }
                try {
                    bldr.show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (Build.VERSION.SDK_INT >= 23) {
                ArrayList<String> permissions = new ArrayList<>();
                if (activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                    permissions.add("android.permission.RECORD_AUDIO");
                }
                if (permissions.isEmpty()) {
                    initiateCall((TLRPC.User) null, chat, hash, false, false, createCall, activity, fragment, accountInstance);
                } else {
                    activity.requestPermissions((String[]) permissions.toArray(new String[0]), 103);
                }
            } else {
                initiateCall((TLRPC.User) null, chat, hash, false, false, createCall, activity, fragment, accountInstance);
            }
        }
    }

    private static void initiateCall(TLRPC.User user, TLRPC.Chat chat, String hash, boolean videoCall, boolean canVideoCall, boolean createCall, Activity activity, BaseFragment fragment, AccountInstance accountInstance) {
        VoIPService voIPService;
        String oldName;
        String key1;
        int key2;
        String newName;
        String str;
        int i;
        int key22;
        String key12;
        TLRPC.User user2 = user;
        TLRPC.Chat chat2 = chat;
        Activity activity2 = activity;
        if (activity2 == null) {
            return;
        }
        if (user2 != null || chat2 != null) {
            VoIPService voIPService2 = VoIPService.getSharedInstance();
            if (voIPService2 != null) {
                long newId = user2 != null ? user2.id : -chat2.id;
                long callerId = VoIPService.getSharedInstance().getCallerId();
                if (callerId != newId) {
                    String str2 = hash;
                } else if (voIPService2.getAccount() != accountInstance.getCurrentAccount()) {
                    String str3 = hash;
                } else {
                    if (user2 != null) {
                        String str4 = hash;
                    } else if (!(activity2 instanceof LaunchActivity)) {
                        String str5 = hash;
                    } else {
                        if (!TextUtils.isEmpty(hash)) {
                            voIPService2.setGroupCallHash(hash);
                        } else {
                            String str6 = hash;
                        }
                        GroupCallActivity.create((LaunchActivity) activity2, AccountInstance.getInstance(UserConfig.selectedAccount), (TLRPC.Chat) null, (TLRPC.InputPeer) null, false, (String) null);
                        voIPService = voIPService2;
                        VoIPService voIPService3 = voIPService;
                        return;
                    }
                    activity2.startActivity(new Intent(activity2, LaunchActivity.class).setAction(user2 != null ? "voip" : "voip_chat"));
                    voIPService = voIPService2;
                    VoIPService voIPService32 = voIPService;
                    return;
                }
                if (callerId > 0) {
                    TLRPC.User callUser = voIPService2.getUser();
                    String oldName2 = ContactsController.formatName(callUser.first_name, callUser.last_name);
                    if (newId > 0) {
                        key12 = "VoipOngoingAlert";
                        key22 = NUM;
                    } else {
                        key12 = "VoipOngoingAlert2";
                        key22 = NUM;
                    }
                    oldName = oldName2;
                    key1 = key12;
                    key2 = key22;
                } else {
                    String oldName3 = voIPService2.getChat().title;
                    if (newId > 0) {
                        oldName = oldName3;
                        key1 = "VoipOngoingChatAlert2";
                        key2 = NUM;
                    } else {
                        oldName = oldName3;
                        key1 = "VoipOngoingChatAlert";
                        key2 = NUM;
                    }
                }
                if (user2 != null) {
                    newName = ContactsController.formatName(user2.first_name, user2.last_name);
                } else {
                    newName = chat2.title;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
                if (callerId < 0) {
                    i = NUM;
                    str = "VoipOngoingChatAlertTitle";
                } else {
                    i = NUM;
                    str = "VoipOngoingAlertTitle";
                }
                AlertDialog.Builder message = builder.setTitle(LocaleController.getString(str, i)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(key1, key2, oldName, newName)));
                long j = newId;
                VoIPHelper$$ExternalSyntheticLambda16 voIPHelper$$ExternalSyntheticLambda16 = r0;
                String string = LocaleController.getString("OK", NUM);
                voIPService = voIPService2;
                int i2 = key2;
                String str7 = key1;
                VoIPHelper$$ExternalSyntheticLambda16 voIPHelper$$ExternalSyntheticLambda162 = new VoIPHelper$$ExternalSyntheticLambda16(user, chat, hash, videoCall, canVideoCall, createCall, activity, fragment, accountInstance);
                message.setPositiveButton(string, voIPHelper$$ExternalSyntheticLambda16).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).show();
                VoIPService voIPService322 = voIPService;
                return;
            }
            VoIPService voIPService4 = voIPService2;
            if (VoIPService.callIShouldHavePutIntoIntent == null) {
                VoIPService voIPService5 = voIPService4;
                doInitiateCall(user, chat, hash, (TLRPC.InputPeer) null, false, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, true, true);
                return;
            }
        }
    }

    static /* synthetic */ void lambda$initiateCall$3(TLRPC.User user, TLRPC.Chat chat, String hash, boolean videoCall, boolean canVideoCall, boolean createCall, Activity activity, BaseFragment fragment, AccountInstance accountInstance, DialogInterface dialog, int which) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp((Runnable) new VoIPHelper$$ExternalSyntheticLambda6(user, chat, hash, videoCall, canVideoCall, createCall, activity, fragment, accountInstance));
        } else {
            doInitiateCall(user, chat, hash, (TLRPC.InputPeer) null, false, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, true, true);
        }
    }

    static /* synthetic */ void lambda$initiateCall$2(TLRPC.User user, TLRPC.Chat chat, String hash, boolean videoCall, boolean canVideoCall, boolean createCall, Activity activity, BaseFragment fragment, AccountInstance accountInstance) {
        lastCallTime = 0;
        doInitiateCall(user, chat, hash, (TLRPC.InputPeer) null, false, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, true, true);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:81:0x01e2  */
    /* JADX WARNING: Removed duplicated region for block: B:82:0x01ee  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void doInitiateCall(org.telegram.tgnet.TLRPC.User r20, org.telegram.tgnet.TLRPC.Chat r21, java.lang.String r22, org.telegram.tgnet.TLRPC.InputPeer r23, boolean r24, boolean r25, boolean r26, boolean r27, android.app.Activity r28, org.telegram.ui.ActionBar.BaseFragment r29, org.telegram.messenger.AccountInstance r30, boolean r31, boolean r32) {
        /*
            r12 = r20
            r13 = r21
            r14 = r23
            r15 = r24
            r11 = r27
            r10 = r28
            if (r10 == 0) goto L_0x025f
            if (r12 != 0) goto L_0x001b
            if (r13 != 0) goto L_0x001b
            r6 = r22
            r3 = r10
            r7 = r11
            r2 = r12
            r5 = r15
            r15 = r14
            goto L_0x0266
        L_0x001b:
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = lastCallTime
            long r0 = r0 - r2
            if (r13 == 0) goto L_0x0027
            r2 = 200(0xc8, float:2.8E-43)
            goto L_0x0029
        L_0x0027:
            r2 = 2000(0x7d0, float:2.803E-42)
        L_0x0029:
            long r2 = (long) r2
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x002f
            return
        L_0x002f:
            if (r31 == 0) goto L_0x007f
            if (r13 == 0) goto L_0x007f
            if (r11 != 0) goto L_0x007f
            org.telegram.messenger.MessagesController r0 = r30.getMessagesController()
            long r1 = r13.id
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.getChatFull(r1)
            if (r0 == 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$Peer r1 = r0.groupcall_default_join_as
            if (r1 == 0) goto L_0x0079
            org.telegram.tgnet.TLRPC$Peer r1 = r0.groupcall_default_join_as
            long r8 = org.telegram.messenger.MessageObject.getPeerId(r1)
            org.telegram.messenger.MessagesController r1 = r30.getMessagesController()
            org.telegram.tgnet.TLRPC$InputPeer r16 = r1.getInputPeer((long) r8)
            long r1 = r13.id
            long r6 = -r1
            org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda7 r5 = new org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda7
            r1 = r5
            r2 = r22
            r3 = r28
            r4 = r21
            r17 = r0
            r0 = r5
            r5 = r20
            r14 = r6
            r6 = r16
            r7 = r25
            r18 = r8
            r8 = r26
            r9 = r29
            r12 = r10
            r10 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            org.telegram.ui.Components.JoinCallAlert.checkFewUsers(r12, r14, r10, r0)
            return
        L_0x0079:
            r17 = r0
            r12 = r10
            r10 = r30
            goto L_0x0082
        L_0x007f:
            r12 = r10
            r10 = r30
        L_0x0082:
            if (r31 == 0) goto L_0x00b4
            if (r13 == 0) goto L_0x00b4
            long r0 = r13.id
            long r14 = -r0
            r0 = r11 ^ 1
            r16 = 0
            org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda12 r17 = new org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda12
            r1 = r17
            r2 = r27
            r3 = r28
            r4 = r30
            r5 = r21
            r6 = r22
            r7 = r20
            r8 = r25
            r9 = r26
            r10 = r29
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10)
            r1 = r28
            r2 = r14
            r5 = r29
            r6 = r0
            r7 = r16
            r8 = r17
            org.telegram.ui.Components.JoinCallAlert.open(r1, r2, r4, r5, r6, r7, r8)
            return
        L_0x00b4:
            if (r32 == 0) goto L_0x0142
            r14 = r24
            if (r14 != 0) goto L_0x0142
            r15 = r23
            boolean r0 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerUser
            if (r0 == 0) goto L_0x0144
            boolean r0 = org.telegram.messenger.ChatObject.shouldSendAnonymously(r21)
            if (r0 == 0) goto L_0x0144
            boolean r0 = org.telegram.messenger.ChatObject.isChannel(r21)
            if (r0 == 0) goto L_0x00d0
            boolean r0 = r13.megagroup
            if (r0 == 0) goto L_0x0144
        L_0x00d0:
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = new org.telegram.ui.ActionBar.AlertDialog$Builder
            r0.<init>((android.content.Context) r12)
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r21)
            if (r1 == 0) goto L_0x00e1
            r1 = 2131628459(0x7f0e11ab, float:1.8884211E38)
            java.lang.String r2 = "VoipChannelVoiceChat"
            goto L_0x00e6
        L_0x00e1:
            r1 = 2131628585(0x7f0e1229, float:1.8884467E38)
            java.lang.String r2 = "VoipGroupVoiceChat"
        L_0x00e6:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setTitle(r1)
            boolean r1 = org.telegram.messenger.ChatObject.isChannelOrGiga(r21)
            if (r1 == 0) goto L_0x00fa
            r1 = 2131628434(0x7f0e1192, float:1.888416E38)
            java.lang.String r2 = "VoipChannelJoinAnonymouseAlert"
            goto L_0x00ff
        L_0x00fa:
            r1 = 2131628521(0x7f0e11e9, float:1.8884337E38)
            java.lang.String r2 = "VoipGroupJoinAnonymouseAlert"
        L_0x00ff:
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setMessage(r1)
            r1 = 2131628463(0x7f0e11af, float:1.888422E38)
            java.lang.String r2 = "VoipChatJoin"
            java.lang.String r10 = org.telegram.messenger.LocaleController.getString(r2, r1)
            org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda15 r9 = new org.telegram.ui.Components.voip.VoIPHelper$$ExternalSyntheticLambda15
            r1 = r9
            r2 = r20
            r3 = r21
            r4 = r22
            r5 = r23
            r6 = r25
            r7 = r26
            r8 = r27
            r14 = r9
            r9 = r28
            r12 = r10
            r10 = r29
            r11 = r30
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setPositiveButton(r12, r14)
            r1 = 2131624692(0x7f0e02f4, float:1.887657E38)
            java.lang.String r2 = "Cancel"
            java.lang.String r1 = org.telegram.messenger.LocaleController.getString(r2, r1)
            r2 = 0
            org.telegram.ui.ActionBar.AlertDialog$Builder r0 = r0.setNegativeButton(r1, r2)
            r0.show()
            return
        L_0x0142:
            r15 = r23
        L_0x0144:
            if (r13 == 0) goto L_0x019d
            if (r15 == 0) goto L_0x019d
            org.telegram.messenger.MessagesController r0 = r30.getMessagesController()
            long r1 = r13.id
            org.telegram.tgnet.TLRPC$ChatFull r0 = r0.getChatFull(r1)
            if (r0 == 0) goto L_0x019d
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerUser
            if (r1 == 0) goto L_0x0166
            org.telegram.tgnet.TLRPC$TL_peerUser r1 = new org.telegram.tgnet.TLRPC$TL_peerUser
            r1.<init>()
            r0.groupcall_default_join_as = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r0.groupcall_default_join_as
            long r2 = r15.user_id
            r1.user_id = r2
            goto L_0x0189
        L_0x0166:
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChat
            if (r1 == 0) goto L_0x0178
            org.telegram.tgnet.TLRPC$TL_peerChat r1 = new org.telegram.tgnet.TLRPC$TL_peerChat
            r1.<init>()
            r0.groupcall_default_join_as = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r0.groupcall_default_join_as
            long r2 = r15.chat_id
            r1.chat_id = r2
            goto L_0x0189
        L_0x0178:
            boolean r1 = r15 instanceof org.telegram.tgnet.TLRPC.TL_inputPeerChannel
            if (r1 == 0) goto L_0x0189
            org.telegram.tgnet.TLRPC$TL_peerChannel r1 = new org.telegram.tgnet.TLRPC$TL_peerChannel
            r1.<init>()
            r0.groupcall_default_join_as = r1
            org.telegram.tgnet.TLRPC$Peer r1 = r0.groupcall_default_join_as
            long r2 = r15.channel_id
            r1.channel_id = r2
        L_0x0189:
            boolean r1 = r0 instanceof org.telegram.tgnet.TLRPC.TL_chatFull
            if (r1 == 0) goto L_0x0196
            int r1 = r0.flags
            r2 = 32768(0x8000, float:4.5918E-41)
            r1 = r1 | r2
            r0.flags = r1
            goto L_0x019d
        L_0x0196:
            int r1 = r0.flags
            r2 = 67108864(0x4000000, float:1.5046328E-36)
            r1 = r1 | r2
            r0.flags = r1
        L_0x019d:
            r0 = 0
            if (r13 == 0) goto L_0x01cc
            r7 = r27
            if (r7 != 0) goto L_0x01ce
            org.telegram.messenger.MessagesController r1 = r30.getMessagesController()
            long r2 = r13.id
            org.telegram.messenger.ChatObject$Call r8 = r1.getGroupCall(r2, r0)
            if (r8 == 0) goto L_0x01c9
            boolean r1 = r8.isScheduled()
            if (r1 == 0) goto L_0x01c9
            r9 = r28
            r1 = r9
            org.telegram.ui.LaunchActivity r1 = (org.telegram.ui.LaunchActivity) r1
            r2 = r30
            r3 = r21
            r4 = r23
            r5 = r24
            r6 = r22
            org.telegram.ui.GroupCallActivity.create(r1, r2, r3, r4, r5, r6)
            return
        L_0x01c9:
            r9 = r28
            goto L_0x01d0
        L_0x01cc:
            r7 = r27
        L_0x01ce:
            r9 = r28
        L_0x01d0:
            long r1 = android.os.SystemClock.elapsedRealtime()
            lastCallTime = r1
            android.content.Intent r1 = new android.content.Intent
            java.lang.Class<org.telegram.messenger.voip.VoIPService> r2 = org.telegram.messenger.voip.VoIPService.class
            r1.<init>(r9, r2)
            r2 = r20
            r3 = r9
            if (r2 == 0) goto L_0x01ee
            long r4 = r2.id
            java.lang.String r6 = "user_id"
            r1.putExtra(r6, r4)
            r6 = r22
            r5 = r24
            goto L_0x0226
        L_0x01ee:
            long r4 = r13.id
            java.lang.String r6 = "chat_id"
            r1.putExtra(r6, r4)
            java.lang.String r4 = "createGroupCall"
            r1.putExtra(r4, r7)
            java.lang.String r4 = "hasFewPeers"
            r5 = r24
            r1.putExtra(r4, r5)
            java.lang.String r4 = "hash"
            r6 = r22
            r1.putExtra(r4, r6)
            if (r15 == 0) goto L_0x0226
            long r8 = r15.channel_id
            java.lang.String r4 = "peerChannelId"
            r1.putExtra(r4, r8)
            long r8 = r15.chat_id
            java.lang.String r4 = "peerChatId"
            r1.putExtra(r4, r8)
            long r8 = r15.user_id
            java.lang.String r4 = "peerUserId"
            r1.putExtra(r4, r8)
            long r8 = r15.access_hash
            java.lang.String r4 = "peerAccessHash"
            r1.putExtra(r4, r8)
        L_0x0226:
            java.lang.String r4 = "is_outgoing"
            r8 = 1
            r1.putExtra(r4, r8)
            java.lang.String r4 = "start_incall_activity"
            r1.putExtra(r4, r8)
            int r4 = android.os.Build.VERSION.SDK_INT
            r9 = 18
            if (r4 < r9) goto L_0x023b
            if (r25 == 0) goto L_0x023b
            r4 = 1
            goto L_0x023c
        L_0x023b:
            r4 = 0
        L_0x023c:
            java.lang.String r10 = "video_call"
            r1.putExtra(r10, r4)
            int r4 = android.os.Build.VERSION.SDK_INT
            if (r4 < r9) goto L_0x0248
            if (r26 == 0) goto L_0x0248
            r0 = 1
        L_0x0248:
            java.lang.String r4 = "can_video_call"
            r1.putExtra(r4, r0)
            int r0 = org.telegram.messenger.UserConfig.selectedAccount
            java.lang.String r4 = "account"
            r1.putExtra(r4, r0)
            r3.startService(r1)     // Catch:{ all -> 0x0258 }
            goto L_0x025e
        L_0x0258:
            r0 = move-exception
            r4 = r0
            r0 = r4
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r0)
        L_0x025e:
            return
        L_0x025f:
            r6 = r22
            r3 = r10
            r7 = r11
            r2 = r12
            r5 = r15
            r15 = r14
        L_0x0266:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPHelper.doInitiateCall(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat, java.lang.String, org.telegram.tgnet.TLRPC$InputPeer, boolean, boolean, boolean, boolean, android.app.Activity, org.telegram.ui.ActionBar.BaseFragment, org.telegram.messenger.AccountInstance, boolean, boolean):void");
    }

    static /* synthetic */ void lambda$doInitiateCall$4(String hash, Activity activity, TLRPC.Chat chat, TLRPC.User user, TLRPC.InputPeer inputPeer, boolean videoCall, boolean canVideoCall, BaseFragment fragment, AccountInstance accountInstance, boolean param) {
        BaseFragment baseFragment = fragment;
        if (param || hash == null) {
            doInitiateCall(user, chat, hash, inputPeer, !param, videoCall, canVideoCall, false, activity, fragment, accountInstance, false, false);
            return;
        }
        final TLRPC.User user2 = user;
        final TLRPC.Chat chat2 = chat;
        final String str = hash;
        final TLRPC.InputPeer inputPeer2 = inputPeer;
        final boolean z = videoCall;
        final boolean z2 = canVideoCall;
        final Activity activity2 = activity;
        final BaseFragment baseFragment2 = fragment;
        final AccountInstance accountInstance2 = accountInstance;
        JoinCallByUrlAlert alert = new JoinCallByUrlAlert(activity, chat) {
            /* access modifiers changed from: protected */
            public void onJoin() {
                VoIPHelper.doInitiateCall(user2, chat2, str, inputPeer2, true, z, z2, false, activity2, baseFragment2, accountInstance2, false, false);
            }
        };
        if (baseFragment != null) {
            baseFragment.showDialog(alert);
        }
    }

    static /* synthetic */ void lambda$doInitiateCall$5(boolean createCall, Activity activity, AccountInstance accountInstance, TLRPC.Chat chat, String hash, TLRPC.User user, boolean videoCall, boolean canVideoCall, BaseFragment fragment, TLRPC.InputPeer selectedPeer, boolean hasFew, boolean schedule) {
        BaseFragment baseFragment = fragment;
        if (createCall && schedule) {
            GroupCallActivity.create((LaunchActivity) activity, accountInstance, chat, selectedPeer, hasFew, hash);
        } else if (hasFew || hash == null) {
            doInitiateCall(user, chat, hash, selectedPeer, hasFew, videoCall, canVideoCall, createCall, activity, fragment, accountInstance, false, true);
        } else {
            final TLRPC.User user2 = user;
            final TLRPC.Chat chat2 = chat;
            final String str = hash;
            final TLRPC.InputPeer inputPeer = selectedPeer;
            final boolean z = videoCall;
            final boolean z2 = canVideoCall;
            final boolean z3 = createCall;
            final Activity activity2 = activity;
            final BaseFragment baseFragment2 = fragment;
            final AccountInstance accountInstance2 = accountInstance;
            JoinCallByUrlAlert alert = new JoinCallByUrlAlert(activity, chat) {
                /* access modifiers changed from: protected */
                public void onJoin() {
                    VoIPHelper.doInitiateCall(user2, chat2, str, inputPeer, false, z, z2, z3, activity2, baseFragment2, accountInstance2, false, true);
                }
            };
            if (baseFragment != null) {
                baseFragment.showDialog(alert);
            }
        }
    }

    public static void permissionDenied(Activity activity, Runnable onFinish, int code) {
        String str;
        int i;
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") || (code == 102 && !activity.shouldShowRequestPermissionRationale("android.permission.CAMERA"))) {
            AlertDialog.Builder title = new AlertDialog.Builder((Context) activity).setTitle(LocaleController.getString("AppName", NUM));
            if (code == 102) {
                i = NUM;
                str = "VoipNeedMicCameraPermission";
            } else {
                i = NUM;
                str = "VoipNeedMicPermission";
            }
            title.setMessage(LocaleController.getString(str, i)).setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null).setNegativeButton(LocaleController.getString("Settings", NUM), new VoIPHelper$$ExternalSyntheticLambda0(activity)).show().setOnDismissListener(new VoIPHelper$$ExternalSyntheticLambda18(onFinish));
        }
    }

    static /* synthetic */ void lambda$permissionDenied$7(Activity activity, DialogInterface dialog, int which) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), (String) null));
        activity.startActivity(intent);
    }

    static /* synthetic */ void lambda$permissionDenied$8(Runnable onFinish, DialogInterface dialog) {
        if (onFinish != null) {
            onFinish.run();
        }
    }

    public static File getLogsDir() {
        File logsDir = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
        return logsDir;
    }

    public static boolean canRateCall(TLRPC.TL_messageActionPhoneCall call) {
        if (!(call.reason instanceof TLRPC.TL_phoneCallDiscardReasonBusy) && !(call.reason instanceof TLRPC.TL_phoneCallDiscardReasonMissed)) {
            for (String hash : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
                String[] d = hash.split(" ");
                if (d.length >= 2) {
                    String str = d[0];
                    if (str.equals(call.call_id + "")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(Context context, TLRPC.TL_messageActionPhoneCall call) {
        for (String hash : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
            String[] d = hash.split(" ");
            if (d.length >= 2) {
                String str = d[0];
                if (str.equals(call.call_id + "")) {
                    try {
                        Context context2 = context;
                        showRateAlert(context2, (Runnable) null, call.video, call.call_id, Long.parseLong(d[1]), UserConfig.selectedAccount, true);
                        return;
                    } catch (Exception e) {
                        return;
                    }
                }
            }
        }
    }

    public static void showRateAlert(Context context, Runnable onDismiss, boolean isVideo, long callID, long accessHash, int account, boolean userInitiative) {
        VoIPHelper$$ExternalSyntheticLambda4 voIPHelper$$ExternalSyntheticLambda4;
        Context context2 = context;
        File log = getLogFile(callID);
        int i = 1;
        boolean z = false;
        int[] page = {0};
        LinearLayout alertView = new LinearLayout(context2);
        alertView.setOrientation(1);
        int pad = AndroidUtilities.dp(16.0f);
        alertView.setPadding(pad, pad, pad, 0);
        TextView text = new TextView(context2);
        text.setTextSize(2, 16.0f);
        text.setTextColor(Theme.getColor("dialogTextBlack"));
        text.setGravity(17);
        text.setText(LocaleController.getString("VoipRateCallAlert", NUM));
        alertView.addView(text);
        BetterRatingView bar = new BetterRatingView(context2);
        alertView.addView(bar, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        LinearLayout problemsWrap = new LinearLayout(context2);
        problemsWrap.setOrientation(1);
        VoIPHelper$$ExternalSyntheticLambda5 voIPHelper$$ExternalSyntheticLambda5 = VoIPHelper$$ExternalSyntheticLambda5.INSTANCE;
        String[] strArr = new String[9];
        strArr[0] = isVideo ? "distorted_video" : null;
        strArr[1] = isVideo ? "pixelated_video" : null;
        strArr[2] = "echo";
        strArr[3] = "noise";
        strArr[4] = "interruptions";
        strArr[5] = "distorted_speech";
        strArr[6] = "silent_local";
        strArr[7] = "silent_remote";
        strArr[8] = "dropped";
        String[] problems = strArr;
        int i2 = 0;
        while (i2 < problems.length) {
            if (problems[i2] != null) {
                CheckBoxCell check = new CheckBoxCell(context2, i);
                check.setClipToPadding(z);
                check.setTag(problems[i2]);
                String label = null;
                switch (i2) {
                    case 0:
                        label = LocaleController.getString("RateCallVideoDistorted", NUM);
                        break;
                    case 1:
                        label = LocaleController.getString("RateCallVideoPixelated", NUM);
                        break;
                    case 2:
                        label = LocaleController.getString("RateCallEcho", NUM);
                        break;
                    case 3:
                        label = LocaleController.getString("RateCallNoise", NUM);
                        break;
                    case 4:
                        label = LocaleController.getString("RateCallInterruptions", NUM);
                        break;
                    case 5:
                        label = LocaleController.getString("RateCallDistorted", NUM);
                        break;
                    case 6:
                        label = LocaleController.getString("RateCallSilentLocal", NUM);
                        break;
                    case 7:
                        label = LocaleController.getString("RateCallSilentRemote", NUM);
                        break;
                    case 8:
                        label = LocaleController.getString("RateCallDropped", NUM);
                        break;
                }
                check.setText(label, (String) null, false, false);
                check.setOnClickListener(voIPHelper$$ExternalSyntheticLambda5);
                check.setTag(problems[i2]);
                problemsWrap.addView(check);
            }
            i2++;
            i = 1;
            z = false;
        }
        alertView.addView(problemsWrap, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        problemsWrap.setVisibility(8);
        EditTextBoldCursor commentBox = new EditTextBoldCursor(context2);
        commentBox.setHint(LocaleController.getString("VoipFeedbackCommentHint", NUM));
        commentBox.setInputType(147457);
        commentBox.setTextColor(Theme.getColor("dialogTextBlack"));
        commentBox.setHintTextColor(Theme.getColor("dialogTextHint"));
        commentBox.setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
        commentBox.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        commentBox.setTextSize(1, 18.0f);
        commentBox.setVisibility(8);
        alertView.addView(commentBox, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        boolean[] includeLogs = {true};
        CheckBoxCell checkbox = new CheckBoxCell(context2, 1);
        VoIPHelper$$ExternalSyntheticLambda4 voIPHelper$$ExternalSyntheticLambda42 = new VoIPHelper$$ExternalSyntheticLambda4(includeLogs, checkbox);
        String[] strArr2 = problems;
        VoIPHelper$$ExternalSyntheticLambda5 voIPHelper$$ExternalSyntheticLambda52 = voIPHelper$$ExternalSyntheticLambda5;
        checkbox.setText(LocaleController.getString("CallReportIncludeLogs", NUM), (String) null, true, false);
        checkbox.setClipToPadding(false);
        checkbox.setOnClickListener(voIPHelper$$ExternalSyntheticLambda42);
        alertView.addView(checkbox, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        TextView logsText = new TextView(context2);
        logsText.setTextSize(2, 14.0f);
        logsText.setTextColor(Theme.getColor("dialogTextGray3"));
        logsText.setText(LocaleController.getString("CallReportLogsExplain", NUM));
        logsText.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        logsText.setOnClickListener(voIPHelper$$ExternalSyntheticLambda42);
        alertView.addView(logsText);
        checkbox.setVisibility(8);
        logsText.setVisibility(8);
        if (!log.exists()) {
            includeLogs[0] = false;
        }
        AlertDialog alert = new AlertDialog.Builder(context2).setTitle(LocaleController.getString("CallMessageReportProblem", NUM)).setView(alertView).setPositiveButton(LocaleController.getString("Send", NUM), VoIPHelper$$ExternalSyntheticLambda17.INSTANCE).setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null).setOnDismissListener(new VoIPHelper$$ExternalSyntheticLambda19(onDismiss)).create();
        if (!BuildVars.LOGS_ENABLED || !log.exists()) {
            voIPHelper$$ExternalSyntheticLambda4 = voIPHelper$$ExternalSyntheticLambda42;
        } else {
            voIPHelper$$ExternalSyntheticLambda4 = voIPHelper$$ExternalSyntheticLambda42;
            alert.setNeutralButton("Send log", new VoIPHelper$$ExternalSyntheticLambda14(context2, log));
        }
        alert.show();
        alert.getWindow().setSoftInputMode(3);
        View btn = alert.getButton(-1);
        btn.setEnabled(false);
        bar.setOnRatingChangeListener(new VoIPHelper$$ExternalSyntheticLambda10(btn));
        VoIPHelper$$ExternalSyntheticLambda3 voIPHelper$$ExternalSyntheticLambda3 = r0;
        VoIPHelper$$ExternalSyntheticLambda4 voIPHelper$$ExternalSyntheticLambda43 = voIPHelper$$ExternalSyntheticLambda4;
        boolean[] zArr = includeLogs;
        EditTextBoldCursor editTextBoldCursor = commentBox;
        VoIPHelper$$ExternalSyntheticLambda5 voIPHelper$$ExternalSyntheticLambda53 = voIPHelper$$ExternalSyntheticLambda52;
        LinearLayout linearLayout = problemsWrap;
        BetterRatingView betterRatingView = bar;
        int i3 = pad;
        LinearLayout linearLayout2 = alertView;
        File file = log;
        VoIPHelper$$ExternalSyntheticLambda3 voIPHelper$$ExternalSyntheticLambda32 = new VoIPHelper$$ExternalSyntheticLambda3(bar, page, problemsWrap, commentBox, includeLogs, accessHash, callID, userInitiative, account, log, context, alert, text, checkbox, logsText, btn);
        btn.setOnClickListener(voIPHelper$$ExternalSyntheticLambda3);
    }

    static /* synthetic */ void lambda$showRateAlert$9(View v) {
        CheckBoxCell check = (CheckBoxCell) v;
        check.setChecked(!check.isChecked(), true);
    }

    static /* synthetic */ void lambda$showRateAlert$10(boolean[] includeLogs, CheckBoxCell checkbox, View v) {
        includeLogs[0] = !includeLogs[0];
        checkbox.setChecked(includeLogs[0], true);
    }

    static /* synthetic */ void lambda$showRateAlert$11(DialogInterface dialog, int which) {
    }

    static /* synthetic */ void lambda$showRateAlert$12(Runnable onDismiss, DialogInterface dialog) {
        if (onDismiss != null) {
            onDismiss.run();
        }
    }

    static /* synthetic */ void lambda$showRateAlert$13(Context context, File log, DialogInterface dialog, int which) {
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(log));
        context.startActivity(intent);
    }

    static /* synthetic */ void lambda$showRateAlert$14(View btn, int rating) {
        String str;
        int i;
        btn.setEnabled(rating > 0);
        TextView textView = (TextView) btn;
        if (rating < 4) {
            i = NUM;
            str = "Next";
        } else {
            i = NUM;
            str = "Send";
        }
        textView.setText(LocaleController.getString(str, i).toUpperCase());
    }

    static /* synthetic */ void lambda$showRateAlert$16(BetterRatingView bar, int[] page, LinearLayout problemsWrap, EditTextBoldCursor commentBox, boolean[] includeLogs, long accessHash, long callID, boolean userInitiative, int account, File log, Context context, AlertDialog alert, TextView text, CheckBoxCell checkbox, TextView logsText, View btn, View v) {
        LinearLayout linearLayout = problemsWrap;
        int rating = bar.getRating();
        if (rating >= 4) {
            BetterRatingView betterRatingView = bar;
            EditTextBoldCursor editTextBoldCursor = commentBox;
            AlertDialog alertDialog = alert;
            TextView textView = text;
            CheckBoxCell checkBoxCell = checkbox;
            TextView textView2 = logsText;
        } else if (page[0] == 1) {
            BetterRatingView betterRatingView2 = bar;
            EditTextBoldCursor editTextBoldCursor2 = commentBox;
            AlertDialog alertDialog2 = alert;
            TextView textView3 = text;
            CheckBoxCell checkBoxCell2 = checkbox;
            TextView textView4 = logsText;
        } else {
            page[0] = 1;
            bar.setVisibility(8);
            text.setVisibility(8);
            alert.setTitle(LocaleController.getString("CallReportHint", NUM));
            commentBox.setVisibility(0);
            if (log.exists()) {
                checkbox.setVisibility(0);
                logsText.setVisibility(0);
            } else {
                CheckBoxCell checkBoxCell3 = checkbox;
                TextView textView5 = logsText;
            }
            linearLayout.setVisibility(0);
            ((TextView) btn).setText(LocaleController.getString("Send", NUM).toUpperCase());
            int i = rating;
            return;
        }
        int currentAccount = UserConfig.selectedAccount;
        TLRPC.TL_phone_setCallRating req = new TLRPC.TL_phone_setCallRating();
        req.rating = bar.getRating();
        ArrayList<String> problemTags = new ArrayList<>();
        for (int i2 = 0; i2 < problemsWrap.getChildCount(); i2++) {
            CheckBoxCell check = (CheckBoxCell) linearLayout.getChildAt(i2);
            if (check.isChecked()) {
                problemTags.add("#" + check.getTag());
            }
        }
        if (req.rating < 5) {
            req.comment = commentBox.getText().toString();
        } else {
            req.comment = "";
        }
        if (!problemTags.isEmpty() && !includeLogs[0]) {
            req.comment += " " + TextUtils.join(" ", problemTags);
        }
        req.peer = new TLRPC.TL_inputPhoneCall();
        req.peer.access_hash = accessHash;
        req.peer.id = callID;
        req.user_initiative = userInitiative;
        int i3 = rating;
        ConnectionsManager.getInstance(account).sendRequest(req, new VoIPHelper$$ExternalSyntheticLambda9(currentAccount, includeLogs, log, req, problemTags, context));
        alert.dismiss();
    }

    static /* synthetic */ void lambda$showRateAlert$15(int currentAccount, boolean[] includeLogs, File log, TLRPC.TL_phone_setCallRating req, ArrayList problemTags, Context context, TLObject response, TLRPC.TL_error error) {
        TLObject tLObject = response;
        if (tLObject instanceof TLRPC.TL_updates) {
            MessagesController.getInstance(currentAccount).processUpdates((TLRPC.TL_updates) tLObject, false);
        }
        if (!includeLogs[0] || !log.exists()) {
            TLRPC.TL_phone_setCallRating tL_phone_setCallRating = req;
        } else if (req.rating < 4) {
            SendMessagesHelper.prepareSendingDocument(AccountInstance.getInstance(UserConfig.selectedAccount), log.getAbsolutePath(), log.getAbsolutePath(), (Uri) null, TextUtils.join(" ", problemTags), "text/plain", 4244000, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
            Toast.makeText(context, LocaleController.getString("CallReportSent", NUM), 1).show();
            return;
        }
        Context context2 = context;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:2:0x0004, code lost:
        r0 = new java.io.File(org.telegram.messenger.ApplicationLoader.applicationContext.getExternalFilesDir((java.lang.String) null), "logs");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.io.File getLogFile(long r7) {
        /*
            boolean r0 = org.telegram.messenger.BuildVars.DEBUG_VERSION
            if (r0 == 0) goto L_0x0043
            java.io.File r0 = new java.io.File
            android.content.Context r1 = org.telegram.messenger.ApplicationLoader.applicationContext
            r2 = 0
            java.io.File r1 = r1.getExternalFilesDir(r2)
            java.lang.String r2 = "logs"
            r0.<init>(r1, r2)
            java.lang.String[] r1 = r0.list()
            if (r1 == 0) goto L_0x0043
            int r2 = r1.length
            r3 = 0
        L_0x001a:
            if (r3 >= r2) goto L_0x0043
            r4 = r1[r3]
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "voip"
            r5.append(r6)
            r5.append(r7)
            java.lang.String r6 = ".txt"
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            boolean r5 = r4.endsWith(r5)
            if (r5 == 0) goto L_0x0040
            java.io.File r2 = new java.io.File
            r2.<init>(r0, r4)
            return r2
        L_0x0040:
            int r3 = r3 + 1
            goto L_0x001a
        L_0x0043:
            java.io.File r0 = new java.io.File
            java.io.File r1 = getLogsDir()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            java.lang.String r3 = ".log"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r0.<init>(r1, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPHelper.getLogFile(long):java.io.File");
    }

    public static void showCallDebugSettings(Context context) {
        SharedPreferences preferences = MessagesController.getGlobalMainSettings();
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(1);
        TextView warning = new TextView(context);
        warning.setTextSize(1, 15.0f);
        warning.setText("Please only change these settings if you know exactly what they do.");
        warning.setTextColor(Theme.getColor("dialogTextBlack"));
        ll.addView(warning, LayoutHelper.createLinear(-1, -2, 16.0f, 8.0f, 16.0f, 8.0f));
        TextCheckCell tcpCell = new TextCheckCell(context);
        tcpCell.setTextAndCheck("Force TCP", preferences.getBoolean("dbg_force_tcp_in_calls", false), false);
        tcpCell.setOnClickListener(new VoIPHelper$$ExternalSyntheticLambda20(preferences, tcpCell));
        ll.addView(tcpCell);
        if (BuildVars.DEBUG_VERSION && BuildVars.LOGS_ENABLED) {
            TextCheckCell dumpCell = new TextCheckCell(context);
            dumpCell.setTextAndCheck("Dump detailed stats", preferences.getBoolean("dbg_dump_call_stats", false), false);
            dumpCell.setOnClickListener(new VoIPHelper$$ExternalSyntheticLambda1(preferences, dumpCell));
            ll.addView(dumpCell);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            TextCheckCell connectionServiceCell = new TextCheckCell(context);
            connectionServiceCell.setTextAndCheck("Enable ConnectionService", preferences.getBoolean("dbg_force_connection_service", false), false);
            connectionServiceCell.setOnClickListener(new VoIPHelper$$ExternalSyntheticLambda2(preferences, connectionServiceCell));
            ll.addView(connectionServiceCell);
        }
        new AlertDialog.Builder(context).setTitle(LocaleController.getString("DebugMenuCallSettings", NUM)).setView(ll).show();
    }

    static /* synthetic */ void lambda$showCallDebugSettings$17(SharedPreferences preferences, TextCheckCell tcpCell, View v) {
        boolean force = preferences.getBoolean("dbg_force_tcp_in_calls", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dbg_force_tcp_in_calls", !force);
        editor.commit();
        tcpCell.setChecked(!force);
    }

    static /* synthetic */ void lambda$showCallDebugSettings$18(SharedPreferences preferences, TextCheckCell dumpCell, View v) {
        boolean force = preferences.getBoolean("dbg_dump_call_stats", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dbg_dump_call_stats", !force);
        editor.commit();
        dumpCell.setChecked(!force);
    }

    static /* synthetic */ void lambda$showCallDebugSettings$19(SharedPreferences preferences, TextCheckCell connectionServiceCell, View v) {
        boolean force = preferences.getBoolean("dbg_force_connection_service", false);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("dbg_force_connection_service", !force);
        editor.commit();
        connectionServiceCell.setChecked(!force);
    }

    public static int getDataSavingDefault() {
        boolean low = DownloadController.getInstance(0).lowPreset.lessCallData;
        boolean medium = DownloadController.getInstance(0).mediumPreset.lessCallData;
        boolean high = DownloadController.getInstance(0).highPreset.lessCallData;
        if (!low && !medium && !high) {
            return 0;
        }
        if (low && !medium && !high) {
            return 3;
        }
        if (low && medium && !high) {
            return 1;
        }
        if (low && medium && high) {
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("Invalid call data saving preset configuration: " + low + "/" + medium + "/" + high);
        }
        return 0;
    }

    public static String getLogFilePath(String name) {
        Calendar c = Calendar.getInstance();
        return new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", new Object[]{Integer.valueOf(c.get(5)), Integer.valueOf(c.get(2) + 1), Integer.valueOf(c.get(1)), Integer.valueOf(c.get(11)), Integer.valueOf(c.get(12)), Integer.valueOf(c.get(13)), name})).getAbsolutePath();
    }

    public static String getLogFilePath(long callId, boolean stats) {
        File[] _logs;
        File logsDir = getLogsDir();
        if (!BuildVars.DEBUG_VERSION && (_logs = logsDir.listFiles()) != null) {
            ArrayList<File> logs = new ArrayList<>(Arrays.asList(_logs));
            while (logs.size() > 20) {
                File oldest = logs.get(0);
                Iterator<File> it = logs.iterator();
                while (it.hasNext()) {
                    File file = it.next();
                    if (file.getName().endsWith(".log") && file.lastModified() < oldest.lastModified()) {
                        oldest = file;
                    }
                }
                oldest.delete();
                logs.remove(oldest);
            }
        }
        return new File(logsDir, callId + ".log").getAbsolutePath();
    }

    public static void showGroupCallAlert(BaseFragment fragment, TLRPC.Chat currentChat, TLRPC.InputPeer peer, boolean recreate, AccountInstance accountInstance) {
        if (fragment != null && fragment.getParentActivity() != null) {
            JoinCallAlert.checkFewUsers(fragment.getParentActivity(), -currentChat.id, accountInstance, new VoIPHelper$$ExternalSyntheticLambda8(currentChat, peer, fragment, accountInstance));
        }
    }
}

package org.telegram.ui.Components.voip;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
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
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$InputPeer;
import org.telegram.tgnet.TLRPC$Peer;
import org.telegram.tgnet.TLRPC$PhoneCallDiscardReason;
import org.telegram.tgnet.TLRPC$TL_chatFull;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputPeerChannel;
import org.telegram.tgnet.TLRPC$TL_inputPeerChat;
import org.telegram.tgnet.TLRPC$TL_inputPeerUser;
import org.telegram.tgnet.TLRPC$TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC$TL_messageActionPhoneCall;
import org.telegram.tgnet.TLRPC$TL_peerChannel;
import org.telegram.tgnet.TLRPC$TL_peerChat;
import org.telegram.tgnet.TLRPC$TL_peerUser;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonBusy;
import org.telegram.tgnet.TLRPC$TL_phoneCallDiscardReasonMissed;
import org.telegram.tgnet.TLRPC$TL_phone_setCallRating;
import org.telegram.tgnet.TLRPC$TL_updates;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserFull;
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
    public static long lastCallTime;

    static /* synthetic */ void lambda$showRateAlert$11(DialogInterface dialogInterface, int i) {
    }

    public static void startCall(TLRPC$User tLRPC$User, boolean z, boolean z2, Activity activity, TLRPC$UserFull tLRPC$UserFull, AccountInstance accountInstance) {
        String str;
        int i;
        String str2;
        int i2;
        boolean z3 = true;
        if (tLRPC$UserFull != null && tLRPC$UserFull.phone_calls_private) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
            builder.setTitle(LocaleController.getString("VoipFailed", NUM));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("CallNotAvailable", NUM, ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name))));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.show();
        } else if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
            if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) == 0) {
                z3 = false;
            }
            AlertDialog.Builder builder2 = new AlertDialog.Builder((Context) activity);
            if (z3) {
                i = NUM;
                str = "VoipOfflineAirplaneTitle";
            } else {
                i = NUM;
                str = "VoipOfflineTitle";
            }
            builder2.setTitle(LocaleController.getString(str, i));
            if (z3) {
                i2 = NUM;
                str2 = "VoipOfflineAirplane";
            } else {
                i2 = NUM;
                str2 = "VoipOffline";
            }
            builder2.setMessage(LocaleController.getString(str2, i2));
            builder2.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            if (z3) {
                Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                if (intent.resolveActivity(activity.getPackageManager()) != null) {
                    builder2.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", NUM), new DialogInterface.OnClickListener(activity, intent) {
                        public final /* synthetic */ Activity f$0;
                        public final /* synthetic */ Intent f$1;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            this.f$0.startActivity(this.f$1);
                        }
                    });
                }
            }
            try {
                builder2.show();
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        } else if (Build.VERSION.SDK_INT >= 23) {
            ArrayList arrayList = new ArrayList();
            if (activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                arrayList.add("android.permission.RECORD_AUDIO");
            }
            if (z && activity.checkSelfPermission("android.permission.CAMERA") != 0) {
                arrayList.add("android.permission.CAMERA");
            }
            if (arrayList.isEmpty()) {
                initiateCall(tLRPC$User, (TLRPC$Chat) null, (String) null, z, z2, false, activity, (BaseFragment) null, accountInstance);
            } else {
                activity.requestPermissions((String[]) arrayList.toArray(new String[0]), z ? 102 : 101);
            }
        } else {
            initiateCall(tLRPC$User, (TLRPC$Chat) null, (String) null, z, z2, false, activity, (BaseFragment) null, accountInstance);
        }
    }

    public static void startCall(TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, String str, boolean z, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        String str2;
        int i;
        String str3;
        int i2;
        if (activity != null) {
            boolean z2 = false;
            if (ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState() != 3) {
                if (Settings.System.getInt(activity.getContentResolver(), "airplane_mode_on", 0) != 0) {
                    z2 = true;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
                if (z2) {
                    i = NUM;
                    str2 = "VoipOfflineAirplaneTitle";
                } else {
                    i = NUM;
                    str2 = "VoipOfflineTitle";
                }
                builder.setTitle(LocaleController.getString(str2, i));
                if (z2) {
                    i2 = NUM;
                    str3 = "VoipGroupOfflineAirplane";
                } else {
                    i2 = NUM;
                    str3 = "VoipGroupOffline";
                }
                builder.setMessage(LocaleController.getString(str3, i2));
                builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
                if (z2) {
                    Intent intent = new Intent("android.settings.AIRPLANE_MODE_SETTINGS");
                    if (intent.resolveActivity(activity.getPackageManager()) != null) {
                        builder.setNeutralButton(LocaleController.getString("VoipOfflineOpenSettings", NUM), new DialogInterface.OnClickListener(activity, intent) {
                            public final /* synthetic */ Activity f$0;
                            public final /* synthetic */ Intent f$1;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                this.f$0.startActivity(this.f$1);
                            }
                        });
                    }
                }
                try {
                    builder.show();
                } catch (Exception e) {
                    FileLog.e((Throwable) e);
                }
            } else if (Build.VERSION.SDK_INT >= 23) {
                ArrayList arrayList = new ArrayList();
                if (activity.checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
                    arrayList.add("android.permission.RECORD_AUDIO");
                }
                if (arrayList.isEmpty()) {
                    initiateCall((TLRPC$User) null, tLRPC$Chat, str, false, false, z, activity, baseFragment, accountInstance);
                } else {
                    activity.requestPermissions((String[]) arrayList.toArray(new String[0]), 103);
                }
            } else {
                initiateCall((TLRPC$User) null, tLRPC$Chat, str, false, false, z, activity, baseFragment, accountInstance);
            }
        }
    }

    private static void initiateCall(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, String str, boolean z, boolean z2, boolean z3, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        String str2;
        int i;
        String str3;
        String str4;
        String str5;
        int i2;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        Activity activity2 = activity;
        if (activity2 == null) {
            return;
        }
        if (tLRPC$User2 != null || tLRPC$Chat2 != null) {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null) {
                int i3 = tLRPC$User2 != null ? tLRPC$User2.id : -tLRPC$Chat2.id;
                int callerId = VoIPService.getSharedInstance().getCallerId();
                if (callerId != i3 || sharedInstance.getAccount() != accountInstance.getCurrentAccount()) {
                    String str6 = str;
                    if (callerId > 0) {
                        TLRPC$User user = sharedInstance.getUser();
                        str3 = ContactsController.formatName(user.first_name, user.last_name);
                        if (i3 > 0) {
                            i = NUM;
                            str2 = "VoipOngoingAlert";
                        } else {
                            i = NUM;
                            str2 = "VoipOngoingAlert2";
                        }
                    } else {
                        str3 = sharedInstance.getChat().title;
                        if (i3 > 0) {
                            i = NUM;
                            str2 = "VoipOngoingChatAlert2";
                        } else {
                            i = NUM;
                            str2 = "VoipOngoingChatAlert";
                        }
                    }
                    if (tLRPC$User2 != null) {
                        str4 = ContactsController.formatName(tLRPC$User2.first_name, tLRPC$User2.last_name);
                    } else {
                        str4 = tLRPC$Chat2.title;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
                    if (callerId < 0) {
                        i2 = NUM;
                        str5 = "VoipOngoingChatAlertTitle";
                    } else {
                        i2 = NUM;
                        str5 = "VoipOngoingAlertTitle";
                    }
                    builder.setTitle(LocaleController.getString(str5, i2));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(str2, i, str3, str4)));
                    builder.setPositiveButton(LocaleController.getString("OK", NUM), new DialogInterface.OnClickListener(tLRPC$Chat, str, z, z2, z3, activity, baseFragment, accountInstance) {
                        public final /* synthetic */ TLRPC$Chat f$1;
                        public final /* synthetic */ String f$2;
                        public final /* synthetic */ boolean f$3;
                        public final /* synthetic */ boolean f$4;
                        public final /* synthetic */ boolean f$5;
                        public final /* synthetic */ Activity f$6;
                        public final /* synthetic */ BaseFragment f$7;
                        public final /* synthetic */ AccountInstance f$8;

                        {
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                            this.f$8 = r9;
                        }

                        public final void onClick(DialogInterface dialogInterface, int i) {
                            VoIPHelper.lambda$initiateCall$3(TLRPC$User.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, dialogInterface, i);
                        }
                    });
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                    builder.show();
                } else if (tLRPC$User2 != null || !(activity2 instanceof LaunchActivity)) {
                    activity2.startActivity(new Intent(activity2, LaunchActivity.class).setAction(tLRPC$User2 != null ? "voip" : "voip_chat"));
                } else {
                    if (!TextUtils.isEmpty(str)) {
                        String str7 = str;
                        sharedInstance.setGroupCallHash(str);
                    }
                    GroupCallActivity.create((LaunchActivity) activity2, AccountInstance.getInstance(UserConfig.selectedAccount), (TLRPC$Chat) null, (TLRPC$InputPeer) null, false, (String) null);
                }
            } else {
                String str8 = str;
                if (VoIPService.callIShouldHavePutIntoIntent == null) {
                    doInitiateCall(tLRPC$User, tLRPC$Chat, str, (TLRPC$InputPeer) null, false, z, z2, z3, activity, baseFragment, accountInstance, true, true);
                }
            }
        }
    }

    static /* synthetic */ void lambda$initiateCall$3(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, String str, boolean z, boolean z2, boolean z3, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance, DialogInterface dialogInterface, int i) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp((Runnable) new Runnable(tLRPC$Chat, str, z, z2, z3, activity, baseFragment, accountInstance) {
                public final /* synthetic */ TLRPC$Chat f$1;
                public final /* synthetic */ String f$2;
                public final /* synthetic */ boolean f$3;
                public final /* synthetic */ boolean f$4;
                public final /* synthetic */ boolean f$5;
                public final /* synthetic */ Activity f$6;
                public final /* synthetic */ BaseFragment f$7;
                public final /* synthetic */ AccountInstance f$8;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                    this.f$6 = r7;
                    this.f$7 = r8;
                    this.f$8 = r9;
                }

                public final void run() {
                    VoIPHelper.lambda$null$2(TLRPC$User.this, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
                }
            });
        } else {
            doInitiateCall(tLRPC$User, tLRPC$Chat, str, (TLRPC$InputPeer) null, false, z, z2, z3, activity, baseFragment, accountInstance, true, true);
        }
    }

    static /* synthetic */ void lambda$null$2(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, String str, boolean z, boolean z2, boolean z3, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance) {
        lastCallTime = 0;
        doInitiateCall(tLRPC$User, tLRPC$Chat, str, (TLRPC$InputPeer) null, false, z, z2, z3, activity, baseFragment, accountInstance, true, true);
    }

    /* access modifiers changed from: private */
    public static void doInitiateCall(TLRPC$User tLRPC$User, TLRPC$Chat tLRPC$Chat, String str, TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2, boolean z3, boolean z4, Activity activity, BaseFragment baseFragment, AccountInstance accountInstance, boolean z5, boolean z6) {
        ChatObject.Call groupCall;
        TLRPC$ChatFull chatFull;
        TLRPC$ChatFull chatFull2;
        TLRPC$Peer tLRPC$Peer;
        TLRPC$User tLRPC$User2 = tLRPC$User;
        TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        TLRPC$InputPeer tLRPC$InputPeer2 = tLRPC$InputPeer;
        boolean z7 = z;
        boolean z8 = z4;
        Activity activity2 = activity;
        if (activity2 == null) {
            return;
        }
        if (tLRPC$User2 != null || tLRPC$Chat2 != null) {
            if (SystemClock.elapsedRealtime() - lastCallTime >= ((long) (tLRPC$Chat2 != null ? 200 : 2000))) {
                if (!z5 || tLRPC$Chat2 == null || z8 || (chatFull2 = accountInstance.getMessagesController().getChatFull(tLRPC$Chat2.id)) == null || (tLRPC$Peer = chatFull2.groupcall_default_join_as) == null) {
                    AccountInstance accountInstance2 = accountInstance;
                    if (z5 && tLRPC$Chat2 != null) {
                        JoinCallAlert.open(activity, -tLRPC$Chat2.id, accountInstance, baseFragment, z8 ^ true ? 1 : 0, (TLRPC$Peer) null, new JoinCallAlert.JoinCallAlertDelegate(z4, activity, accountInstance, tLRPC$Chat, str, tLRPC$User, z2, z3, baseFragment) {
                            public final /* synthetic */ boolean f$0;
                            public final /* synthetic */ Activity f$1;
                            public final /* synthetic */ AccountInstance f$2;
                            public final /* synthetic */ TLRPC$Chat f$3;
                            public final /* synthetic */ String f$4;
                            public final /* synthetic */ TLRPC$User f$5;
                            public final /* synthetic */ boolean f$6;
                            public final /* synthetic */ boolean f$7;
                            public final /* synthetic */ BaseFragment f$8;

                            {
                                this.f$0 = r1;
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                                this.f$6 = r7;
                                this.f$7 = r8;
                                this.f$8 = r9;
                            }

                            public final void didSelectChat(TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2) {
                                VoIPHelper.lambda$doInitiateCall$5(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, tLRPC$InputPeer, z, z2);
                            }
                        });
                    } else if (!z6 || z7 || !(tLRPC$InputPeer2 instanceof TLRPC$TL_inputPeerUser) || !ChatObject.shouldSendAnonymously(tLRPC$Chat) || (ChatObject.isChannel(tLRPC$Chat) && !tLRPC$Chat2.megagroup)) {
                        if (!(tLRPC$Chat2 == null || tLRPC$InputPeer2 == null || (chatFull = accountInstance.getMessagesController().getChatFull(tLRPC$Chat2.id)) == null)) {
                            if (tLRPC$InputPeer2 instanceof TLRPC$TL_inputPeerUser) {
                                TLRPC$TL_peerUser tLRPC$TL_peerUser = new TLRPC$TL_peerUser();
                                chatFull.groupcall_default_join_as = tLRPC$TL_peerUser;
                                tLRPC$TL_peerUser.user_id = tLRPC$InputPeer2.user_id;
                            } else if (tLRPC$InputPeer2 instanceof TLRPC$TL_inputPeerChat) {
                                TLRPC$TL_peerChat tLRPC$TL_peerChat = new TLRPC$TL_peerChat();
                                chatFull.groupcall_default_join_as = tLRPC$TL_peerChat;
                                tLRPC$TL_peerChat.chat_id = tLRPC$InputPeer2.chat_id;
                            } else if (tLRPC$InputPeer2 instanceof TLRPC$TL_inputPeerChannel) {
                                TLRPC$TL_peerChannel tLRPC$TL_peerChannel = new TLRPC$TL_peerChannel();
                                chatFull.groupcall_default_join_as = tLRPC$TL_peerChannel;
                                tLRPC$TL_peerChannel.channel_id = tLRPC$InputPeer2.channel_id;
                            }
                            if (chatFull instanceof TLRPC$TL_chatFull) {
                                chatFull.flags |= 32768;
                            } else {
                                chatFull.flags |= 67108864;
                            }
                        }
                        boolean z9 = false;
                        if (tLRPC$Chat2 == null || z8 || (groupCall = accountInstance.getMessagesController().getGroupCall(tLRPC$Chat2.id, false)) == null || !groupCall.isScheduled()) {
                            lastCallTime = SystemClock.elapsedRealtime();
                            Intent intent = new Intent(activity2, VoIPService.class);
                            if (tLRPC$User2 != null) {
                                intent.putExtra("user_id", tLRPC$User2.id);
                            } else {
                                intent.putExtra("chat_id", tLRPC$Chat2.id);
                                intent.putExtra("createGroupCall", z8);
                                intent.putExtra("hasFewPeers", z7);
                                intent.putExtra("hash", str);
                                if (tLRPC$InputPeer2 != null) {
                                    intent.putExtra("peerChannelId", tLRPC$InputPeer2.channel_id);
                                    intent.putExtra("peerChatId", tLRPC$InputPeer2.chat_id);
                                    intent.putExtra("peerUserId", tLRPC$InputPeer2.user_id);
                                    intent.putExtra("peerAccessHash", tLRPC$InputPeer2.access_hash);
                                }
                            }
                            intent.putExtra("is_outgoing", true);
                            intent.putExtra("start_incall_activity", true);
                            int i = Build.VERSION.SDK_INT;
                            intent.putExtra("video_call", i >= 18 && z2);
                            if (i >= 18 && z3) {
                                z9 = true;
                            }
                            intent.putExtra("can_video_call", z9);
                            intent.putExtra("account", UserConfig.selectedAccount);
                            try {
                                activity2.startService(intent);
                            } catch (Throwable th) {
                                FileLog.e(th);
                            }
                        } else {
                            GroupCallActivity.create((LaunchActivity) activity2, accountInstance, tLRPC$Chat, tLRPC$InputPeer, z, str);
                        }
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity2);
                        builder.setTitle(LocaleController.getString("VoipGroupVoiceChat", NUM));
                        builder.setMessage(LocaleController.getString("VoipGroupJoinAnonymouseAlert", NUM));
                        builder.setPositiveButton(LocaleController.getString("VoipChatJoin", NUM), new DialogInterface.OnClickListener(tLRPC$Chat, str, tLRPC$InputPeer, z2, z3, z4, activity, baseFragment, accountInstance) {
                            public final /* synthetic */ TLRPC$Chat f$1;
                            public final /* synthetic */ String f$2;
                            public final /* synthetic */ TLRPC$InputPeer f$3;
                            public final /* synthetic */ boolean f$4;
                            public final /* synthetic */ boolean f$5;
                            public final /* synthetic */ boolean f$6;
                            public final /* synthetic */ Activity f$7;
                            public final /* synthetic */ BaseFragment f$8;
                            public final /* synthetic */ AccountInstance f$9;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                                this.f$3 = r4;
                                this.f$4 = r5;
                                this.f$5 = r6;
                                this.f$6 = r7;
                                this.f$7 = r8;
                                this.f$8 = r9;
                                this.f$9 = r10;
                            }

                            public final void onClick(DialogInterface dialogInterface, int i) {
                                VoIPHelper.doInitiateCall(TLRPC$User.this, this.f$1, this.f$2, this.f$3, false, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, false, false);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
                        builder.show();
                    }
                } else {
                    JoinCallAlert.checkFewUsers(activity2, -tLRPC$Chat2.id, accountInstance, new MessagesStorage.BooleanCallback(str, activity, tLRPC$Chat, tLRPC$User, accountInstance.getMessagesController().getInputPeer(MessageObject.getPeerId(tLRPC$Peer)), z2, z3, baseFragment, accountInstance) {
                        public final /* synthetic */ String f$0;
                        public final /* synthetic */ Activity f$1;
                        public final /* synthetic */ TLRPC$Chat f$2;
                        public final /* synthetic */ TLRPC$User f$3;
                        public final /* synthetic */ TLRPC$InputPeer f$4;
                        public final /* synthetic */ boolean f$5;
                        public final /* synthetic */ boolean f$6;
                        public final /* synthetic */ BaseFragment f$7;
                        public final /* synthetic */ AccountInstance f$8;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                            this.f$3 = r4;
                            this.f$4 = r5;
                            this.f$5 = r6;
                            this.f$6 = r7;
                            this.f$7 = r8;
                            this.f$8 = r9;
                        }

                        public final void run(boolean z) {
                            VoIPHelper.lambda$doInitiateCall$4(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, z);
                        }
                    });
                }
            }
        }
    }

    static /* synthetic */ void lambda$doInitiateCall$4(String str, Activity activity, TLRPC$Chat tLRPC$Chat, TLRPC$User tLRPC$User, TLRPC$InputPeer tLRPC$InputPeer, boolean z, boolean z2, BaseFragment baseFragment, AccountInstance accountInstance, boolean z3) {
        BaseFragment baseFragment2 = baseFragment;
        if (z3 || str == null) {
            doInitiateCall(tLRPC$User, tLRPC$Chat, str, tLRPC$InputPeer, !z3, z, z2, false, activity, baseFragment, accountInstance, false, false);
            return;
        }
        final TLRPC$User tLRPC$User2 = tLRPC$User;
        final TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
        final String str2 = str;
        final TLRPC$InputPeer tLRPC$InputPeer2 = tLRPC$InputPeer;
        final boolean z4 = z;
        final boolean z5 = z2;
        final Activity activity2 = activity;
        final BaseFragment baseFragment3 = baseFragment;
        final AccountInstance accountInstance2 = accountInstance;
        AnonymousClass1 r0 = new JoinCallByUrlAlert(activity, tLRPC$Chat) {
            /* access modifiers changed from: protected */
            public void onJoin() {
                VoIPHelper.doInitiateCall(tLRPC$User2, tLRPC$Chat2, str2, tLRPC$InputPeer2, true, z4, z5, false, activity2, baseFragment3, accountInstance2, false, false);
            }
        };
        if (baseFragment2 != null) {
            baseFragment2.showDialog(r0);
        }
    }

    static /* synthetic */ void lambda$doInitiateCall$5(boolean z, Activity activity, AccountInstance accountInstance, TLRPC$Chat tLRPC$Chat, String str, TLRPC$User tLRPC$User, boolean z2, boolean z3, BaseFragment baseFragment, TLRPC$InputPeer tLRPC$InputPeer, boolean z4, boolean z5) {
        BaseFragment baseFragment2 = baseFragment;
        if (z && z5) {
            GroupCallActivity.create((LaunchActivity) activity, accountInstance, tLRPC$Chat, tLRPC$InputPeer, z4, str);
        } else if (z4 || str == null) {
            doInitiateCall(tLRPC$User, tLRPC$Chat, str, tLRPC$InputPeer, z4, z2, z3, z, activity, baseFragment, accountInstance, false, true);
        } else {
            final TLRPC$User tLRPC$User2 = tLRPC$User;
            final TLRPC$Chat tLRPC$Chat2 = tLRPC$Chat;
            final String str2 = str;
            final TLRPC$InputPeer tLRPC$InputPeer2 = tLRPC$InputPeer;
            final boolean z6 = z2;
            final boolean z7 = z3;
            final boolean z8 = z;
            final Activity activity2 = activity;
            final BaseFragment baseFragment3 = baseFragment;
            final AccountInstance accountInstance2 = accountInstance;
            AnonymousClass2 r0 = new JoinCallByUrlAlert(activity, tLRPC$Chat) {
                /* access modifiers changed from: protected */
                public void onJoin() {
                    VoIPHelper.doInitiateCall(tLRPC$User2, tLRPC$Chat2, str2, tLRPC$InputPeer2, false, z6, z7, z8, activity2, baseFragment3, accountInstance2, false, true);
                }
            };
            if (baseFragment2 != null) {
                baseFragment2.showDialog(r0);
            }
        }
    }

    @TargetApi(23)
    public static void permissionDenied(Activity activity, Runnable runnable, int i) {
        int i2;
        String str;
        if (!activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") || (i == 102 && !activity.shouldShowRequestPermissionRationale("android.permission.CAMERA"))) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) activity);
            builder.setTitle(LocaleController.getString("AppName", NUM));
            if (i == 102) {
                i2 = NUM;
                str = "VoipNeedMicCameraPermission";
            } else {
                i2 = NUM;
                str = "VoipNeedMicPermission";
            }
            builder.setMessage(LocaleController.getString(str, i2));
            builder.setPositiveButton(LocaleController.getString("OK", NUM), (DialogInterface.OnClickListener) null);
            builder.setNegativeButton(LocaleController.getString("Settings", NUM), new DialogInterface.OnClickListener(activity) {
                public final /* synthetic */ Activity f$0;

                {
                    this.f$0 = r1;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPHelper.lambda$permissionDenied$7(this.f$0, dialogInterface, i);
                }
            });
            builder.show().setOnDismissListener(new DialogInterface.OnDismissListener(runnable) {
                public final /* synthetic */ Runnable f$0;

                {
                    this.f$0 = r1;
                }

                public final void onDismiss(DialogInterface dialogInterface) {
                    VoIPHelper.lambda$permissionDenied$8(this.f$0, dialogInterface);
                }
            });
        }
    }

    static /* synthetic */ void lambda$permissionDenied$7(Activity activity, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.fromParts("package", activity.getPackageName(), (String) null));
        activity.startActivity(intent);
    }

    static /* synthetic */ void lambda$permissionDenied$8(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public static File getLogsDir() {
        File file = new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static boolean canRateCall(TLRPC$TL_messageActionPhoneCall tLRPC$TL_messageActionPhoneCall) {
        TLRPC$PhoneCallDiscardReason tLRPC$PhoneCallDiscardReason = tLRPC$TL_messageActionPhoneCall.reason;
        if (!(tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonBusy) && !(tLRPC$PhoneCallDiscardReason instanceof TLRPC$TL_phoneCallDiscardReasonMissed)) {
            for (String split : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
                String[] split2 = split.split(" ");
                if (split2.length >= 2) {
                    String str = split2[0];
                    if (str.equals(tLRPC$TL_messageActionPhoneCall.call_id + "")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void showRateAlert(Context context, TLRPC$TL_messageActionPhoneCall tLRPC$TL_messageActionPhoneCall) {
        for (String split : MessagesController.getNotificationsSettings(UserConfig.selectedAccount).getStringSet("calls_access_hashes", Collections.EMPTY_SET)) {
            String[] split2 = split.split(" ");
            if (split2.length >= 2) {
                String str = split2[0];
                if (str.equals(tLRPC$TL_messageActionPhoneCall.call_id + "")) {
                    try {
                        long parseLong = Long.parseLong(split2[1]);
                        showRateAlert(context, (Runnable) null, tLRPC$TL_messageActionPhoneCall.video, tLRPC$TL_messageActionPhoneCall.call_id, parseLong, UserConfig.selectedAccount, true);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                }
            }
        }
    }

    public static void showRateAlert(Context context, Runnable runnable, boolean z, long j, long j2, int i, boolean z2) {
        String str;
        Context context2 = context;
        File logFile = getLogFile(j);
        int i2 = 1;
        int[] iArr = {0};
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        int dp = AndroidUtilities.dp(16.0f);
        linearLayout.setPadding(dp, dp, dp, 0);
        TextView textView = new TextView(context2);
        textView.setTextSize(2, 16.0f);
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        textView.setGravity(17);
        textView.setText(LocaleController.getString("VoipRateCallAlert", NUM));
        linearLayout.addView(textView);
        BetterRatingView betterRatingView = new BetterRatingView(context2);
        linearLayout.addView(betterRatingView, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(1);
        $$Lambda$VoIPHelper$ElnhII4qc_3AsgN3hLWyzMfVEE r8 = $$Lambda$VoIPHelper$ElnhII4qc_3AsgN3hLWyzMfVEE.INSTANCE;
        String[] strArr = new String[9];
        strArr[0] = z ? "distorted_video" : null;
        strArr[1] = z ? "pixelated_video" : null;
        strArr[2] = "echo";
        strArr[3] = "noise";
        strArr[4] = "interruptions";
        strArr[5] = "distorted_speech";
        strArr[6] = "silent_local";
        strArr[7] = "silent_remote";
        strArr[8] = "dropped";
        int i3 = 0;
        for (int i4 = 9; i3 < i4; i4 = 9) {
            if (strArr[i3] != null) {
                CheckBoxCell checkBoxCell = new CheckBoxCell(context2, i2);
                checkBoxCell.setClipToPadding(false);
                checkBoxCell.setTag(strArr[i3]);
                switch (i3) {
                    case 0:
                        str = LocaleController.getString("RateCallVideoDistorted", NUM);
                        break;
                    case 1:
                        str = LocaleController.getString("RateCallVideoPixelated", NUM);
                        break;
                    case 2:
                        str = LocaleController.getString("RateCallEcho", NUM);
                        break;
                    case 3:
                        str = LocaleController.getString("RateCallNoise", NUM);
                        break;
                    case 4:
                        str = LocaleController.getString("RateCallInterruptions", NUM);
                        break;
                    case 5:
                        str = LocaleController.getString("RateCallDistorted", NUM);
                        break;
                    case 6:
                        str = LocaleController.getString("RateCallSilentLocal", NUM);
                        break;
                    case 7:
                        str = LocaleController.getString("RateCallSilentRemote", NUM);
                        break;
                    case 8:
                        str = LocaleController.getString("RateCallDropped", NUM);
                        break;
                    default:
                        str = null;
                        break;
                }
                checkBoxCell.setText(str, (String) null, false, false);
                checkBoxCell.setOnClickListener(r8);
                checkBoxCell.setTag(strArr[i3]);
                linearLayout2.addView(checkBoxCell);
            }
            i3++;
            i2 = 1;
        }
        linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        linearLayout2.setVisibility(8);
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context2);
        editTextBoldCursor.setHint(LocaleController.getString("VoipFeedbackCommentHint", NUM));
        editTextBoldCursor.setInputType(147457);
        editTextBoldCursor.setTextColor(Theme.getColor("dialogTextBlack"));
        editTextBoldCursor.setHintTextColor(Theme.getColor("dialogTextHint"));
        editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(context2, true));
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setVisibility(8);
        linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, -2, 8.0f, 8.0f, 8.0f, 0.0f));
        boolean[] zArr = {true};
        CheckBoxCell checkBoxCell2 = new CheckBoxCell(context2, 1);
        $$Lambda$VoIPHelper$zp297l_60oRv4YSKJ7yXkjNAnVU r4 = new View.OnClickListener(zArr, checkBoxCell2) {
            public final /* synthetic */ boolean[] f$0;
            public final /* synthetic */ CheckBoxCell f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                VoIPHelper.lambda$showRateAlert$10(this.f$0, this.f$1, view);
            }
        };
        checkBoxCell2.setText(LocaleController.getString("CallReportIncludeLogs", NUM), (String) null, true, false);
        checkBoxCell2.setClipToPadding(false);
        checkBoxCell2.setOnClickListener(r4);
        linearLayout.addView(checkBoxCell2, LayoutHelper.createLinear(-1, -2, -8.0f, 0.0f, -8.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        textView2.setTextSize(2, 14.0f);
        textView2.setTextColor(Theme.getColor("dialogTextGray3"));
        textView2.setText(LocaleController.getString("CallReportLogsExplain", NUM));
        textView2.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        textView2.setOnClickListener(r4);
        linearLayout.addView(textView2);
        checkBoxCell2.setVisibility(8);
        textView2.setVisibility(8);
        if (!logFile.exists()) {
            zArr[0] = false;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context2);
        builder.setTitle(LocaleController.getString("CallMessageReportProblem", NUM));
        builder.setView(linearLayout);
        builder.setPositiveButton(LocaleController.getString("Send", NUM), $$Lambda$VoIPHelper$zc_o5I9wFPzgBxnHDKWGlwtwgHM.INSTANCE);
        builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener(runnable) {
            public final /* synthetic */ Runnable f$0;

            {
                this.f$0 = r1;
            }

            public final void onDismiss(DialogInterface dialogInterface) {
                VoIPHelper.lambda$showRateAlert$12(this.f$0, dialogInterface);
            }
        });
        AlertDialog create = builder.create();
        if (BuildVars.LOGS_ENABLED && logFile.exists()) {
            create.setNeutralButton("Send log", new DialogInterface.OnClickListener(context2, logFile) {
                public final /* synthetic */ Context f$0;
                public final /* synthetic */ File f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPHelper.lambda$showRateAlert$13(this.f$0, this.f$1, dialogInterface, i);
                }
            });
        }
        create.show();
        create.getWindow().setSoftInputMode(3);
        View button = create.getButton(-1);
        button.setEnabled(false);
        betterRatingView.setOnRatingChangeListener(new BetterRatingView.OnRatingChangeListener(button) {
            public final /* synthetic */ View f$0;

            {
                this.f$0 = r1;
            }

            public final void onRatingChanged(int i) {
                VoIPHelper.lambda$showRateAlert$14(this.f$0, i);
            }
        });
        $$Lambda$VoIPHelper$J37QO91ferKsFqpieBlycNmPKQ r27 = r0;
        $$Lambda$VoIPHelper$J37QO91ferKsFqpieBlycNmPKQ r0 = new View.OnClickListener(betterRatingView, iArr, linearLayout2, editTextBoldCursor, zArr, j2, j, z2, i, logFile, context, create, textView, checkBoxCell2, textView2, button) {
            public final /* synthetic */ BetterRatingView f$0;
            public final /* synthetic */ int[] f$1;
            public final /* synthetic */ Context f$10;
            public final /* synthetic */ AlertDialog f$11;
            public final /* synthetic */ TextView f$12;
            public final /* synthetic */ CheckBoxCell f$13;
            public final /* synthetic */ TextView f$14;
            public final /* synthetic */ View f$15;
            public final /* synthetic */ LinearLayout f$2;
            public final /* synthetic */ EditTextBoldCursor f$3;
            public final /* synthetic */ boolean[] f$4;
            public final /* synthetic */ long f$5;
            public final /* synthetic */ long f$6;
            public final /* synthetic */ boolean f$7;
            public final /* synthetic */ int f$8;
            public final /* synthetic */ File f$9;

            {
                this.f$0 = r4;
                this.f$1 = r5;
                this.f$2 = r6;
                this.f$3 = r7;
                this.f$4 = r8;
                this.f$5 = r9;
                this.f$6 = r11;
                this.f$7 = r13;
                this.f$8 = r14;
                this.f$9 = r15;
                this.f$10 = r16;
                this.f$11 = r17;
                this.f$12 = r18;
                this.f$13 = r19;
                this.f$14 = r20;
                this.f$15 = r21;
            }

            public final void onClick(View view) {
                View view2 = view;
                BetterRatingView betterRatingView = this.f$0;
                BetterRatingView betterRatingView2 = betterRatingView;
                VoIPHelper.lambda$showRateAlert$16(betterRatingView2, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, this.f$14, this.f$15, view2);
            }
        };
        button.setOnClickListener(r27);
    }

    static /* synthetic */ void lambda$showRateAlert$9(View view) {
        CheckBoxCell checkBoxCell = (CheckBoxCell) view;
        checkBoxCell.setChecked(!checkBoxCell.isChecked(), true);
    }

    static /* synthetic */ void lambda$showRateAlert$10(boolean[] zArr, CheckBoxCell checkBoxCell, View view) {
        zArr[0] = !zArr[0];
        checkBoxCell.setChecked(zArr[0], true);
    }

    static /* synthetic */ void lambda$showRateAlert$12(Runnable runnable, DialogInterface dialogInterface) {
        if (runnable != null) {
            runnable.run();
        }
    }

    static /* synthetic */ void lambda$showRateAlert$13(Context context, File file, DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setAction("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(file));
        context.startActivity(intent);
    }

    static /* synthetic */ void lambda$showRateAlert$14(View view, int i) {
        int i2;
        String str;
        view.setEnabled(i > 0);
        TextView textView = (TextView) view;
        if (i < 4) {
            i2 = NUM;
            str = "Next";
        } else {
            i2 = NUM;
            str = "Send";
        }
        textView.setText(LocaleController.getString(str, i2).toUpperCase());
    }

    static /* synthetic */ void lambda$showRateAlert$16(BetterRatingView betterRatingView, int[] iArr, LinearLayout linearLayout, EditTextBoldCursor editTextBoldCursor, boolean[] zArr, long j, long j2, boolean z, int i, File file, Context context, AlertDialog alertDialog, TextView textView, CheckBoxCell checkBoxCell, TextView textView2, View view, View view2) {
        LinearLayout linearLayout2 = linearLayout;
        if (betterRatingView.getRating() >= 4 || iArr[0] == 1) {
            BetterRatingView betterRatingView2 = betterRatingView;
            EditTextBoldCursor editTextBoldCursor2 = editTextBoldCursor;
            AlertDialog alertDialog2 = alertDialog;
            int i2 = UserConfig.selectedAccount;
            TLRPC$TL_phone_setCallRating tLRPC$TL_phone_setCallRating = new TLRPC$TL_phone_setCallRating();
            tLRPC$TL_phone_setCallRating.rating = betterRatingView.getRating();
            ArrayList arrayList = new ArrayList();
            for (int i3 = 0; i3 < linearLayout.getChildCount(); i3++) {
                CheckBoxCell checkBoxCell2 = (CheckBoxCell) linearLayout.getChildAt(i3);
                if (checkBoxCell2.isChecked()) {
                    arrayList.add("#" + checkBoxCell2.getTag());
                }
            }
            if (tLRPC$TL_phone_setCallRating.rating < 5) {
                tLRPC$TL_phone_setCallRating.comment = editTextBoldCursor.getText().toString();
            } else {
                tLRPC$TL_phone_setCallRating.comment = "";
            }
            if (!arrayList.isEmpty() && !zArr[0]) {
                tLRPC$TL_phone_setCallRating.comment += " " + TextUtils.join(" ", arrayList);
            }
            TLRPC$TL_inputPhoneCall tLRPC$TL_inputPhoneCall = new TLRPC$TL_inputPhoneCall();
            tLRPC$TL_phone_setCallRating.peer = tLRPC$TL_inputPhoneCall;
            tLRPC$TL_inputPhoneCall.access_hash = j;
            tLRPC$TL_inputPhoneCall.id = j2;
            tLRPC$TL_phone_setCallRating.user_initiative = z;
            ConnectionsManager.getInstance(i).sendRequest(tLRPC$TL_phone_setCallRating, new RequestDelegate(i2, zArr, file, tLRPC$TL_phone_setCallRating, arrayList, context) {
                public final /* synthetic */ int f$0;
                public final /* synthetic */ boolean[] f$1;
                public final /* synthetic */ File f$2;
                public final /* synthetic */ TLRPC$TL_phone_setCallRating f$3;
                public final /* synthetic */ ArrayList f$4;
                public final /* synthetic */ Context f$5;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                    this.f$5 = r6;
                }

                public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                    VoIPHelper.lambda$null$15(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, tLObject, tLRPC$TL_error);
                }
            });
            alertDialog.dismiss();
            return;
        }
        iArr[0] = 1;
        BetterRatingView betterRatingView3 = betterRatingView;
        betterRatingView.setVisibility(8);
        textView.setVisibility(8);
        alertDialog.setTitle(LocaleController.getString("CallReportHint", NUM));
        editTextBoldCursor.setVisibility(0);
        if (file.exists()) {
            checkBoxCell.setVisibility(0);
            textView2.setVisibility(0);
        }
        linearLayout.setVisibility(0);
        ((TextView) view).setText(LocaleController.getString("Send", NUM).toUpperCase());
    }

    static /* synthetic */ void lambda$null$15(int i, boolean[] zArr, File file, TLRPC$TL_phone_setCallRating tLRPC$TL_phone_setCallRating, ArrayList arrayList, Context context, TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
        TLObject tLObject2 = tLObject;
        if (tLObject2 instanceof TLRPC$TL_updates) {
            MessagesController.getInstance(i).processUpdates((TLRPC$TL_updates) tLObject2, false);
        }
        if (zArr[0] && file.exists() && tLRPC$TL_phone_setCallRating.rating < 4) {
            SendMessagesHelper.prepareSendingDocument(AccountInstance.getInstance(UserConfig.selectedAccount), file.getAbsolutePath(), file.getAbsolutePath(), (Uri) null, TextUtils.join(" ", arrayList), "text/plain", 4244000, (MessageObject) null, (MessageObject) null, (InputContentInfoCompat) null, (MessageObject) null, true, 0);
            Toast.makeText(context, LocaleController.getString("CallReportSent", NUM), 1).show();
        }
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
            java.io.File r7 = new java.io.File
            r7.<init>(r0, r4)
            return r7
        L_0x0040:
            int r3 = r3 + 1
            goto L_0x001a
        L_0x0043:
            java.io.File r0 = new java.io.File
            java.io.File r1 = getLogsDir()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            java.lang.String r7 = ".log"
            r2.append(r7)
            java.lang.String r7 = r2.toString()
            r0.<init>(r1, r7)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.VoIPHelper.getLogFile(long):java.io.File");
    }

    public static void showCallDebugSettings(Context context) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 15.0f);
        textView.setText("Please only change these settings if you know exactly what they do.");
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 16.0f, 8.0f, 16.0f, 8.0f));
        TextCheckCell textCheckCell = new TextCheckCell(context);
        textCheckCell.setTextAndCheck("Force TCP", globalMainSettings.getBoolean("dbg_force_tcp_in_calls", false), false);
        textCheckCell.setOnClickListener(new View.OnClickListener(globalMainSettings, textCheckCell) {
            public final /* synthetic */ SharedPreferences f$0;
            public final /* synthetic */ TextCheckCell f$1;

            {
                this.f$0 = r1;
                this.f$1 = r2;
            }

            public final void onClick(View view) {
                VoIPHelper.lambda$showCallDebugSettings$17(this.f$0, this.f$1, view);
            }
        });
        linearLayout.addView(textCheckCell);
        if (BuildVars.DEBUG_VERSION && BuildVars.LOGS_ENABLED) {
            TextCheckCell textCheckCell2 = new TextCheckCell(context);
            textCheckCell2.setTextAndCheck("Dump detailed stats", globalMainSettings.getBoolean("dbg_dump_call_stats", false), false);
            textCheckCell2.setOnClickListener(new View.OnClickListener(globalMainSettings, textCheckCell2) {
                public final /* synthetic */ SharedPreferences f$0;
                public final /* synthetic */ TextCheckCell f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    VoIPHelper.lambda$showCallDebugSettings$18(this.f$0, this.f$1, view);
                }
            });
            linearLayout.addView(textCheckCell2);
        }
        if (Build.VERSION.SDK_INT >= 26) {
            TextCheckCell textCheckCell3 = new TextCheckCell(context);
            textCheckCell3.setTextAndCheck("Enable ConnectionService", globalMainSettings.getBoolean("dbg_force_connection_service", false), false);
            textCheckCell3.setOnClickListener(new View.OnClickListener(globalMainSettings, textCheckCell3) {
                public final /* synthetic */ SharedPreferences f$0;
                public final /* synthetic */ TextCheckCell f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    VoIPHelper.lambda$showCallDebugSettings$19(this.f$0, this.f$1, view);
                }
            });
            linearLayout.addView(textCheckCell3);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString("DebugMenuCallSettings", NUM));
        builder.setView(linearLayout);
        builder.show();
    }

    static /* synthetic */ void lambda$showCallDebugSettings$17(SharedPreferences sharedPreferences, TextCheckCell textCheckCell, View view) {
        boolean z = sharedPreferences.getBoolean("dbg_force_tcp_in_calls", false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("dbg_force_tcp_in_calls", !z);
        edit.commit();
        textCheckCell.setChecked(!z);
    }

    static /* synthetic */ void lambda$showCallDebugSettings$18(SharedPreferences sharedPreferences, TextCheckCell textCheckCell, View view) {
        boolean z = sharedPreferences.getBoolean("dbg_dump_call_stats", false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("dbg_dump_call_stats", !z);
        edit.commit();
        textCheckCell.setChecked(!z);
    }

    static /* synthetic */ void lambda$showCallDebugSettings$19(SharedPreferences sharedPreferences, TextCheckCell textCheckCell, View view) {
        boolean z = sharedPreferences.getBoolean("dbg_force_connection_service", false);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean("dbg_force_connection_service", !z);
        edit.commit();
        textCheckCell.setChecked(!z);
    }

    public static int getDataSavingDefault() {
        boolean z = DownloadController.getInstance(0).lowPreset.lessCallData;
        boolean z2 = DownloadController.getInstance(0).mediumPreset.lessCallData;
        boolean z3 = DownloadController.getInstance(0).highPreset.lessCallData;
        if (!z && !z2 && !z3) {
            return 0;
        }
        if (z && !z2 && !z3) {
            return 3;
        }
        if (z && z2 && !z3) {
            return 1;
        }
        if (z && z2 && z3) {
            return 2;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.w("Invalid call data saving preset configuration: " + z + "/" + z2 + "/" + z3);
        }
        return 0;
    }

    public static String getLogFilePath(String str) {
        Calendar instance = Calendar.getInstance();
        return new File(ApplicationLoader.applicationContext.getExternalFilesDir((String) null), String.format(Locale.US, "logs/%02d_%02d_%04d_%02d_%02d_%02d_%s.txt", new Object[]{Integer.valueOf(instance.get(5)), Integer.valueOf(instance.get(2) + 1), Integer.valueOf(instance.get(1)), Integer.valueOf(instance.get(11)), Integer.valueOf(instance.get(12)), Integer.valueOf(instance.get(13)), str})).getAbsolutePath();
    }

    public static String getLogFilePath(long j, boolean z) {
        File[] listFiles;
        File logsDir = getLogsDir();
        if (!BuildVars.DEBUG_VERSION && (listFiles = logsDir.listFiles()) != null) {
            ArrayList arrayList = new ArrayList(Arrays.asList(listFiles));
            while (arrayList.size() > 20) {
                File file = (File) arrayList.get(0);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    File file2 = (File) it.next();
                    if (file2.getName().endsWith(".log") && file2.lastModified() < file.lastModified()) {
                        file = file2;
                    }
                }
                file.delete();
                arrayList.remove(file);
            }
        }
        return new File(logsDir, j + ".log").getAbsolutePath();
    }

    public static void showGroupCallAlert(BaseFragment baseFragment, TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, boolean z, AccountInstance accountInstance) {
        if (baseFragment != null && baseFragment.getParentActivity() != null) {
            JoinCallAlert.checkFewUsers(baseFragment.getParentActivity(), -tLRPC$Chat.id, accountInstance, new MessagesStorage.BooleanCallback(z, tLRPC$Chat, tLRPC$InputPeer, accountInstance) {
                public final /* synthetic */ boolean f$1;
                public final /* synthetic */ TLRPC$Chat f$2;
                public final /* synthetic */ TLRPC$InputPeer f$3;
                public final /* synthetic */ AccountInstance f$4;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                    this.f$4 = r5;
                }

                public final void run(boolean z) {
                    VoIPHelper.lambda$showGroupCallAlert$21(BaseFragment.this, this.f$1, this.f$2, this.f$3, this.f$4, z);
                }
            });
        }
    }

    static /* synthetic */ void lambda$showGroupCallAlert$21(BaseFragment baseFragment, boolean z, TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, AccountInstance accountInstance, boolean z2) {
        if (!z2) {
            startCall(tLRPC$Chat, tLRPC$InputPeer, (String) null, true, baseFragment.getParentActivity(), baseFragment, accountInstance);
        } else if (baseFragment.getParentActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder((Context) baseFragment.getParentActivity());
            builder.setTitle(LocaleController.getString("StartVoipChatTitle", NUM));
            if (z) {
                builder.setMessage(LocaleController.getString("VoipGroupEndedStartNew", NUM));
            } else if (!ChatObject.isChannel(tLRPC$Chat) || tLRPC$Chat.megagroup) {
                builder.setMessage(LocaleController.getString("StartVoipChatAlertText", NUM));
            } else {
                builder.setMessage(LocaleController.getString("StartVoipChannelAlertText", NUM));
            }
            builder.setPositiveButton(LocaleController.getString("Start", NUM), new DialogInterface.OnClickListener(tLRPC$Chat, tLRPC$InputPeer, accountInstance) {
                public final /* synthetic */ TLRPC$Chat f$1;
                public final /* synthetic */ TLRPC$InputPeer f$2;
                public final /* synthetic */ AccountInstance f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void onClick(DialogInterface dialogInterface, int i) {
                    VoIPHelper.lambda$null$20(BaseFragment.this, this.f$1, this.f$2, this.f$3, dialogInterface, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString("Cancel", NUM), (DialogInterface.OnClickListener) null);
            baseFragment.showDialog(builder.create());
        }
    }

    static /* synthetic */ void lambda$null$20(BaseFragment baseFragment, TLRPC$Chat tLRPC$Chat, TLRPC$InputPeer tLRPC$InputPeer, AccountInstance accountInstance, DialogInterface dialogInterface, int i) {
        if (baseFragment.getParentActivity() != null) {
            startCall(tLRPC$Chat, tLRPC$InputPeer, (String) null, true, baseFragment.getParentActivity(), baseFragment, accountInstance);
        }
    }
}

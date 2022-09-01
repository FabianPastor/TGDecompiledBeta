package org.telegram.ui;

import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.messenger.ringtone.RingtoneUploader;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$TL_account_saveRingtone;
import org.telegram.tgnet.TLRPC$TL_document;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_inputDocument;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CreationTextCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.RadioButton;
import org.telegram.ui.Components.RecyclerListView;

public class NotificationsSoundActivity extends BaseFragment implements ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate, NotificationCenter.NotificationCenterDelegate {
    Adapter adapter;
    ChatAvatarContainer avatarContainer;
    ChatAttachAlert chatAttachAlert;
    int currentType = -1;
    long dialogId;
    int dividerRow;
    int dividerRow2;
    Ringtone lastPlayedRingtone;
    RecyclerListView listView;
    Theme.ResourcesProvider resourcesProvider;
    int rowCount;
    Tone selectedTone;
    boolean selectedToneChanged;
    SparseArray<Tone> selectedTones = new SparseArray<>();
    NumberTextView selectedTonesCountTextView;
    ArrayList<Tone> serverTones = new ArrayList<>();
    int serverTonesEndRow;
    int serverTonesHeaderRow;
    int serverTonesStartRow;
    private int stableIds = 100;
    /* access modifiers changed from: private */
    public Tone startSelectedTone;
    ArrayList<Tone> systemTones = new ArrayList<>();
    int systemTonesEndRow;
    int systemTonesHeaderRow;
    int systemTonesStartRow;
    int uploadRow;
    ArrayList<Tone> uploadingTones = new ArrayList<>();

    /* access modifiers changed from: private */
    public static /* synthetic */ boolean lambda$createView$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public /* synthetic */ void didSelectPhotos(ArrayList arrayList, boolean z, int i) {
        ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public /* synthetic */ void startMusicSelectActivity() {
        ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate.CC.$default$startMusicSelectActivity(this);
    }

    public NotificationsSoundActivity(Bundle bundle, Theme.ResourcesProvider resourcesProvider2) {
        super(bundle);
        this.resourcesProvider = resourcesProvider2;
    }

    public boolean onFragmentCreate() {
        String str;
        String str2;
        if (getArguments() != null) {
            this.dialogId = getArguments().getLong("dialog_id", 0);
            this.currentType = getArguments().getInt("type", -1);
        }
        if (this.dialogId != 0) {
            str2 = "sound_document_id_" + this.dialogId;
            str = "sound_path_" + this.dialogId;
        } else {
            int i = this.currentType;
            if (i == 1) {
                str = "GlobalSoundPath";
                str2 = "GlobalSoundDocId";
            } else if (i == 0) {
                str = "GroupSoundPath";
                str2 = "GroupSoundDocId";
            } else if (i == 2) {
                str = "ChannelSoundPath";
                str2 = "ChannelSoundDocId";
            } else {
                throw new RuntimeException("Unsupported type");
            }
        }
        SharedPreferences notificationsSettings = getNotificationsSettings();
        long j = notificationsSettings.getLong(str2, 0);
        String string = notificationsSettings.getString(str, "NoSound");
        Tone tone = new Tone();
        this.startSelectedTone = tone;
        if (j != 0) {
            tone.document = new TLRPC$TL_document();
            this.startSelectedTone.document.id = j;
        } else {
            tone.uri = string;
        }
        return super.onFragmentCreate();
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourcesProvider;
    }

    public View createView(Context context) {
        final Context context2 = context;
        this.actionBar.setItemsBackgroundColor(Theme.getColor("avatar_actionBarSelectorBlue", this.resourcesProvider), false);
        this.actionBar.setItemsColor(Theme.getColor("actionBarDefaultIcon", this.resourcesProvider), false);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            /* access modifiers changed from: private */
            public static /* synthetic */ void lambda$deleteSelectedMessages$2(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
            }

            public void onItemClick(int i) {
                Class<LaunchActivity> cls = LaunchActivity.class;
                if (i != -1) {
                    if (i == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(NotificationsSoundActivity.this.getParentActivity(), NotificationsSoundActivity.this.resourcesProvider);
                        builder.setTitle(LocaleController.formatPluralString("DeleteTones", NotificationsSoundActivity.this.selectedTones.size(), new Object[0]));
                        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DeleteTonesMessage", NotificationsSoundActivity.this.selectedTones.size(), new Object[0])));
                        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), NotificationsSoundActivity$1$$ExternalSyntheticLambda1.INSTANCE);
                        builder.setPositiveButton(LocaleController.getString("Delete", R.string.Delete), new NotificationsSoundActivity$1$$ExternalSyntheticLambda0(this));
                        TextView textView = (TextView) builder.show().getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor("dialogTextRed2", NotificationsSoundActivity.this.resourcesProvider));
                        }
                    } else if (i == 2) {
                        if (NotificationsSoundActivity.this.selectedTones.size() == 1) {
                            Intent intent = new Intent(context2, cls);
                            intent.setAction("android.intent.action.SEND");
                            Uri uriForShare = NotificationsSoundActivity.this.selectedTones.valueAt(0).getUriForShare(NotificationsSoundActivity.this.currentAccount);
                            if (uriForShare != null) {
                                intent.putExtra("android.intent.extra.STREAM", uriForShare);
                                context2.startActivity(intent);
                            }
                        } else {
                            Intent intent2 = new Intent(context2, cls);
                            intent2.setAction("android.intent.action.SEND_MULTIPLE");
                            ArrayList arrayList = new ArrayList();
                            for (int i2 = 0; i2 < NotificationsSoundActivity.this.selectedTones.size(); i2++) {
                                Uri uriForShare2 = NotificationsSoundActivity.this.selectedTones.valueAt(i2).getUriForShare(NotificationsSoundActivity.this.currentAccount);
                                if (uriForShare2 != null) {
                                    arrayList.add(uriForShare2);
                                }
                            }
                            if (!arrayList.isEmpty()) {
                                intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", arrayList);
                                context2.startActivity(intent2);
                            }
                        }
                        NotificationsSoundActivity.this.hideActionMode();
                        NotificationsSoundActivity.this.updateRows();
                        NotificationsSoundActivity.this.adapter.notifyDataSetChanged();
                    }
                } else if (NotificationsSoundActivity.this.actionBar.isActionModeShowed()) {
                    NotificationsSoundActivity.this.hideActionMode();
                } else {
                    NotificationsSoundActivity.this.finishFragment();
                }
            }

            /* access modifiers changed from: private */
            public /* synthetic */ void lambda$onItemClick$1(DialogInterface dialogInterface, int i) {
                deleteSelectedMessages();
                dialogInterface.dismiss();
            }

            private void deleteSelectedMessages() {
                RingtoneUploader ringtoneUploader;
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < NotificationsSoundActivity.this.selectedTones.size(); i++) {
                    Tone valueAt = NotificationsSoundActivity.this.selectedTones.valueAt(i);
                    TLRPC$Document tLRPC$Document = valueAt.document;
                    if (tLRPC$Document != null) {
                        arrayList.add(tLRPC$Document);
                        NotificationsSoundActivity.this.getMediaDataController().ringtoneDataStore.remove(valueAt.document);
                    }
                    if (!(valueAt.uri == null || (ringtoneUploader = NotificationsSoundActivity.this.getMediaDataController().ringtoneUploaderHashMap.get(valueAt.uri)) == null)) {
                        ringtoneUploader.cancel();
                    }
                    NotificationsSoundActivity notificationsSoundActivity = NotificationsSoundActivity.this;
                    if (valueAt == notificationsSoundActivity.selectedTone) {
                        Tone unused = notificationsSoundActivity.startSelectedTone = null;
                        NotificationsSoundActivity notificationsSoundActivity2 = NotificationsSoundActivity.this;
                        notificationsSoundActivity2.selectedTone = notificationsSoundActivity2.systemTones.get(0);
                        NotificationsSoundActivity.this.selectedToneChanged = true;
                    }
                    NotificationsSoundActivity.this.serverTones.remove(valueAt);
                    NotificationsSoundActivity.this.uploadingTones.remove(valueAt);
                }
                NotificationsSoundActivity.this.getMediaDataController().ringtoneDataStore.saveTones();
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    TLRPC$Document tLRPC$Document2 = (TLRPC$Document) arrayList.get(i2);
                    TLRPC$TL_account_saveRingtone tLRPC$TL_account_saveRingtone = new TLRPC$TL_account_saveRingtone();
                    TLRPC$TL_inputDocument tLRPC$TL_inputDocument = new TLRPC$TL_inputDocument();
                    tLRPC$TL_account_saveRingtone.id = tLRPC$TL_inputDocument;
                    tLRPC$TL_inputDocument.id = tLRPC$Document2.id;
                    tLRPC$TL_inputDocument.access_hash = tLRPC$Document2.access_hash;
                    byte[] bArr = tLRPC$Document2.file_reference;
                    tLRPC$TL_inputDocument.file_reference = bArr;
                    if (bArr == null) {
                        tLRPC$TL_inputDocument.file_reference = new byte[0];
                    }
                    tLRPC$TL_account_saveRingtone.unsave = true;
                    NotificationsSoundActivity.this.getConnectionsManager().sendRequest(tLRPC$TL_account_saveRingtone, NotificationsSoundActivity$1$$ExternalSyntheticLambda2.INSTANCE);
                }
                NotificationsSoundActivity.this.hideActionMode();
                NotificationsSoundActivity.this.updateRows();
                NotificationsSoundActivity.this.adapter.notifyDataSetChanged();
            }
        });
        if (this.dialogId == 0) {
            int i = this.currentType;
            if (i == 1) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundPrivate", R.string.NotificationsSoundPrivate));
            } else if (i == 0) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundGroup", R.string.NotificationsSoundGroup));
            } else if (i == 2) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundChannels", R.string.NotificationsSoundChannels));
            }
        } else {
            ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context2, (ChatActivity) null, false, this.resourcesProvider);
            this.avatarContainer = chatAvatarContainer;
            chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
            this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
            if (this.dialogId < 0) {
                TLRPC$Chat chat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
                this.avatarContainer.setChatAvatar(chat);
                this.avatarContainer.setTitle(chat.title);
            } else {
                TLRPC$User user = getMessagesController().getUser(Long.valueOf(this.dialogId));
                if (user != null) {
                    this.avatarContainer.setUserAvatar(user);
                    this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            }
            this.avatarContainer.setSubtitle(LocaleController.getString("NotificationsSound", R.string.NotificationsSound));
        }
        ActionBarMenu createActionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(createActionMode.getContext());
        this.selectedTonesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedTonesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedTonesCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon", this.resourcesProvider));
        createActionMode.addView(this.selectedTonesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedTonesCountTextView.setOnTouchListener(NotificationsSoundActivity$$ExternalSyntheticLambda0.INSTANCE);
        createActionMode.addItemWithWidth(2, R.drawable.msg_forward, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("ShareFile", R.string.ShareFile));
        createActionMode.addItemWithWidth(1, R.drawable.msg_delete, AndroidUtilities.dp(54.0f), (CharSequence) LocaleController.getString("Delete", R.string.Delete));
        FrameLayout frameLayout = new FrameLayout(context2);
        this.fragmentView = frameLayout;
        FrameLayout frameLayout2 = frameLayout;
        frameLayout2.setBackgroundColor(Theme.getColor("windowBackgroundGray", this.resourcesProvider));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        frameLayout2.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
        Adapter adapter2 = new Adapter();
        this.adapter = adapter2;
        adapter2.setHasStableIds(true);
        this.listView.setAdapter(this.adapter);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setSupportsChangeAnimations(false);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context2));
        this.listView.setOnItemClickListener((RecyclerListView.OnItemClickListener) new NotificationsSoundActivity$$ExternalSyntheticLambda1(this, context2));
        this.listView.setOnItemLongClickListener((RecyclerListView.OnItemLongClickListener) new NotificationsSoundActivity$$ExternalSyntheticLambda2(this));
        loadTones();
        updateRows();
        return this.fragmentView;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0098 A[Catch:{ Exception -> 0x00d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00ac A[Catch:{ Exception -> 0x00d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00c5 A[Catch:{ Exception -> 0x00d2 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$createView$1(android.content.Context r8, android.view.View r9, int r10) {
        /*
            r7 = this;
            int r0 = r7.uploadRow
            if (r10 != r0) goto L_0x001f
            org.telegram.ui.Components.ChatAttachAlert r10 = new org.telegram.ui.Components.ChatAttachAlert
            r4 = 0
            r5 = 0
            org.telegram.ui.ActionBar.Theme$ResourcesProvider r6 = r7.resourcesProvider
            r1 = r10
            r2 = r8
            r3 = r7
            r1.<init>(r2, r3, r4, r5, r6)
            r7.chatAttachAlert = r10
            r10.setSoundPicker()
            org.telegram.ui.Components.ChatAttachAlert r10 = r7.chatAttachAlert
            r10.init()
            org.telegram.ui.Components.ChatAttachAlert r10 = r7.chatAttachAlert
            r10.show()
        L_0x001f:
            boolean r10 = r9 instanceof org.telegram.ui.NotificationsSoundActivity.ToneCell
            if (r10 == 0) goto L_0x00ee
            org.telegram.ui.NotificationsSoundActivity$ToneCell r9 = (org.telegram.ui.NotificationsSoundActivity.ToneCell) r9
            org.telegram.ui.ActionBar.ActionBar r10 = r7.actionBar
            boolean r10 = r10.isActionModeShowed()
            if (r10 != 0) goto L_0x00e9
            org.telegram.ui.NotificationsSoundActivity$Tone r10 = r9.tone
            if (r10 != 0) goto L_0x0033
            goto L_0x00e9
        L_0x0033:
            android.media.Ringtone r10 = r7.lastPlayedRingtone
            if (r10 == 0) goto L_0x003a
            r10.stop()
        L_0x003a:
            r10 = 0
            r0 = 0
            org.telegram.ui.NotificationsSoundActivity$Tone r1 = r9.tone     // Catch:{ Exception -> 0x00d2 }
            boolean r2 = r1.isSystemDefault     // Catch:{ Exception -> 0x00d2 }
            r3 = 4
            if (r2 == 0) goto L_0x005a
            android.content.Context r8 = r8.getApplicationContext()     // Catch:{ Exception -> 0x00d2 }
            r1 = 2
            android.net.Uri r1 = android.media.RingtoneManager.getDefaultUri(r1)     // Catch:{ Exception -> 0x00d2 }
            android.media.Ringtone r8 = android.media.RingtoneManager.getRingtone(r8, r1)     // Catch:{ Exception -> 0x00d2 }
            r8.setStreamType(r3)     // Catch:{ Exception -> 0x00d2 }
            r7.lastPlayedRingtone = r8     // Catch:{ Exception -> 0x00d2 }
            r8.play()     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d6
        L_0x005a:
            java.lang.String r2 = r1.uri     // Catch:{ Exception -> 0x00d2 }
            if (r2 == 0) goto L_0x007b
            boolean r4 = r1.fromServer     // Catch:{ Exception -> 0x00d2 }
            if (r4 != 0) goto L_0x007b
            android.content.Context r8 = r8.getApplicationContext()     // Catch:{ Exception -> 0x00d2 }
            org.telegram.ui.NotificationsSoundActivity$Tone r1 = r9.tone     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r1 = r1.uri     // Catch:{ Exception -> 0x00d2 }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x00d2 }
            android.media.Ringtone r8 = android.media.RingtoneManager.getRingtone(r8, r1)     // Catch:{ Exception -> 0x00d2 }
            r8.setStreamType(r3)     // Catch:{ Exception -> 0x00d2 }
            r7.lastPlayedRingtone = r8     // Catch:{ Exception -> 0x00d2 }
            r8.play()     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d6
        L_0x007b:
            boolean r1 = r1.fromServer     // Catch:{ Exception -> 0x00d2 }
            if (r1 == 0) goto L_0x00d6
            boolean r1 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x00d2 }
            if (r1 != 0) goto L_0x0095
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x00d2 }
            org.telegram.ui.NotificationsSoundActivity$Tone r2 = r9.tone     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r2 = r2.uri     // Catch:{ Exception -> 0x00d2 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x00d2 }
            boolean r2 = r1.exists()     // Catch:{ Exception -> 0x00d2 }
            if (r2 == 0) goto L_0x0095
            goto L_0x0096
        L_0x0095:
            r1 = r0
        L_0x0096:
            if (r1 != 0) goto L_0x00a4
            org.telegram.messenger.FileLoader r1 = r7.getFileLoader()     // Catch:{ Exception -> 0x00d2 }
            org.telegram.ui.NotificationsSoundActivity$Tone r2 = r9.tone     // Catch:{ Exception -> 0x00d2 }
            org.telegram.tgnet.TLRPC$Document r2 = r2.document     // Catch:{ Exception -> 0x00d2 }
            java.io.File r1 = r1.getPathToAttach(r2)     // Catch:{ Exception -> 0x00d2 }
        L_0x00a4:
            if (r1 == 0) goto L_0x00c5
            boolean r2 = r1.exists()     // Catch:{ Exception -> 0x00d2 }
            if (r2 == 0) goto L_0x00c5
            android.content.Context r8 = r8.getApplicationContext()     // Catch:{ Exception -> 0x00d2 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x00d2 }
            android.net.Uri r1 = android.net.Uri.parse(r1)     // Catch:{ Exception -> 0x00d2 }
            android.media.Ringtone r8 = android.media.RingtoneManager.getRingtone(r8, r1)     // Catch:{ Exception -> 0x00d2 }
            r8.setStreamType(r3)     // Catch:{ Exception -> 0x00d2 }
            r7.lastPlayedRingtone = r8     // Catch:{ Exception -> 0x00d2 }
            r8.play()     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d6
        L_0x00c5:
            org.telegram.messenger.FileLoader r8 = r7.getFileLoader()     // Catch:{ Exception -> 0x00d2 }
            org.telegram.ui.NotificationsSoundActivity$Tone r1 = r9.tone     // Catch:{ Exception -> 0x00d2 }
            org.telegram.tgnet.TLRPC$Document r1 = r1.document     // Catch:{ Exception -> 0x00d2 }
            r2 = 3
            r8.loadFile(r1, r1, r2, r10)     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d6
        L_0x00d2:
            r8 = move-exception
            org.telegram.messenger.FileLog.e((java.lang.Throwable) r8)
        L_0x00d6:
            r7.startSelectedTone = r0
            org.telegram.ui.NotificationsSoundActivity$Tone r8 = r9.tone
            r7.selectedTone = r8
            r8 = 1
            r7.selectedToneChanged = r8
            org.telegram.ui.NotificationsSoundActivity$Adapter r8 = r7.adapter
            int r9 = r8.getItemCount()
            r8.notifyItemRangeChanged(r10, r9)
            goto L_0x00ee
        L_0x00e9:
            org.telegram.ui.NotificationsSoundActivity$Tone r8 = r9.tone
            r7.checkSelection(r8)
        L_0x00ee:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSoundActivity.lambda$createView$1(android.content.Context, android.view.View, int):void");
    }

    /* access modifiers changed from: private */
    public /* synthetic */ boolean lambda$createView$2(View view, int i) {
        if (view instanceof ToneCell) {
            ToneCell toneCell = (ToneCell) view;
            checkSelection(toneCell.tone);
            toneCell.performHapticFeedback(0);
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void hideActionMode() {
        this.selectedTones.clear();
        Adapter adapter2 = this.adapter;
        adapter2.notifyItemRangeChanged(0, adapter2.getItemCount());
        updateActionMode();
    }

    private void checkSelection(Tone tone) {
        boolean z = true;
        if (this.selectedTones.get(tone.stableId) != null) {
            this.selectedTones.remove(tone.stableId);
        } else if (tone.fromServer) {
            this.selectedTones.put(tone.stableId, tone);
        } else {
            z = false;
        }
        if (z) {
            updateActionMode();
            Adapter adapter2 = this.adapter;
            adapter2.notifyItemRangeChanged(0, adapter2.getItemCount());
        }
    }

    private void updateActionMode() {
        if (this.selectedTones.size() > 0) {
            this.selectedTonesCountTextView.setNumber(this.selectedTones.size(), this.actionBar.isActionModeShowed());
            this.actionBar.showActionMode();
            return;
        }
        this.actionBar.hideActionMode();
    }

    private void loadTones() {
        TLRPC$Document tLRPC$Document;
        TLRPC$Document tLRPC$Document2;
        getMediaDataController().ringtoneDataStore.lambda$new$0();
        this.serverTones.clear();
        this.systemTones.clear();
        for (int i = 0; i < getMediaDataController().ringtoneDataStore.userRingtones.size(); i++) {
            RingtoneDataStore.CachedTone cachedTone = getMediaDataController().ringtoneDataStore.userRingtones.get(i);
            Tone tone = new Tone();
            int i2 = this.stableIds;
            this.stableIds = i2 + 1;
            tone.stableId = i2;
            tone.fromServer = true;
            tone.localId = cachedTone.localId;
            TLRPC$Document tLRPC$Document3 = cachedTone.document;
            tone.title = tLRPC$Document3.file_name_fixed;
            tone.document = tLRPC$Document3;
            trimTitle(tone);
            tone.uri = cachedTone.localUri;
            Tone tone2 = this.startSelectedTone;
            if (!(tone2 == null || (tLRPC$Document = tone2.document) == null || (tLRPC$Document2 = cachedTone.document) == null || tLRPC$Document.id != tLRPC$Document2.id)) {
                this.startSelectedTone = null;
                this.selectedTone = tone;
            }
            this.serverTones.add(tone);
        }
        RingtoneManager ringtoneManager = new RingtoneManager(ApplicationLoader.applicationContext);
        ringtoneManager.setType(2);
        Cursor cursor = ringtoneManager.getCursor();
        Tone tone3 = new Tone();
        int i3 = this.stableIds;
        this.stableIds = i3 + 1;
        tone3.stableId = i3;
        tone3.title = LocaleController.getString("NoSound", R.string.NoSound);
        this.systemTones.add(tone3);
        Tone tone4 = new Tone();
        int i4 = this.stableIds;
        this.stableIds = i4 + 1;
        tone4.stableId = i4;
        tone4.title = LocaleController.getString("DefaultRingtone", R.string.DefaultRingtone);
        tone4.isSystemDefault = true;
        this.systemTones.add(tone4);
        Tone tone5 = this.startSelectedTone;
        if (tone5 != null && tone5.document == null && tone5.uri.equals("NoSound")) {
            this.startSelectedTone = null;
            this.selectedTone = tone3;
        }
        Tone tone6 = this.startSelectedTone;
        if (tone6 != null && tone6.document == null && tone6.uri.equals("Default")) {
            this.startSelectedTone = null;
            this.selectedTone = tone4;
        }
        while (cursor.moveToNext()) {
            String string = cursor.getString(1);
            String str = cursor.getString(2) + "/" + cursor.getString(0);
            Tone tone7 = new Tone();
            int i5 = this.stableIds;
            this.stableIds = i5 + 1;
            tone7.stableId = i5;
            tone7.title = string;
            tone7.uri = str;
            Tone tone8 = this.startSelectedTone;
            if (tone8 != null && tone8.document == null && tone8.uri.equals(str)) {
                this.startSelectedTone = null;
                this.selectedTone = tone7;
            }
            this.systemTones.add(tone7);
        }
        if (getMediaDataController().ringtoneDataStore.isLoaded() && this.selectedTone == null) {
            this.selectedTone = tone4;
            this.selectedToneChanged = true;
        }
        updateRows();
    }

    /* access modifiers changed from: private */
    public void updateRows() {
        this.serverTonesHeaderRow = -1;
        this.serverTonesStartRow = -1;
        this.serverTonesEndRow = -1;
        this.uploadRow = -1;
        this.dividerRow = -1;
        this.systemTonesHeaderRow = -1;
        this.systemTonesStartRow = -1;
        this.systemTonesEndRow = -1;
        this.rowCount = 0;
        this.rowCount = 0 + 1;
        this.serverTonesHeaderRow = 0;
        if (!this.serverTones.isEmpty()) {
            int i = this.rowCount;
            this.serverTonesStartRow = i;
            int size = i + this.serverTones.size();
            this.rowCount = size;
            this.serverTonesEndRow = size;
        }
        int i2 = this.rowCount;
        int i3 = i2 + 1;
        this.rowCount = i3;
        this.uploadRow = i2;
        this.rowCount = i3 + 1;
        this.dividerRow = i3;
        if (!this.systemTones.isEmpty()) {
            int i4 = this.rowCount;
            int i5 = i4 + 1;
            this.rowCount = i5;
            this.systemTonesHeaderRow = i4;
            this.systemTonesStartRow = i5;
            int size2 = i5 + this.systemTones.size();
            this.rowCount = size2;
            this.systemTonesEndRow = size2;
        }
        int i6 = this.rowCount;
        this.rowCount = i6 + 1;
        this.dividerRow2 = i6;
    }

    public void didSelectFiles(ArrayList<String> arrayList, String str, ArrayList<MessageObject> arrayList2, boolean z, int i) {
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            getMediaDataController().uploadRingtone(arrayList.get(i2));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public long getItemId(int i) {
            Tone tone = getTone(i);
            if (tone != null) {
                return (long) tone.stableId;
            }
            NotificationsSoundActivity notificationsSoundActivity = NotificationsSoundActivity.this;
            if (i == notificationsSoundActivity.serverTonesHeaderRow) {
                return 1;
            }
            if (i == notificationsSoundActivity.systemTonesHeaderRow) {
                return 2;
            }
            if (i == notificationsSoundActivity.uploadRow) {
                return 3;
            }
            if (i == notificationsSoundActivity.dividerRow) {
                return 4;
            }
            if (i == notificationsSoundActivity.dividerRow2) {
                return 5;
            }
            throw new RuntimeException();
        }

        private Tone getTone(int i) {
            NotificationsSoundActivity notificationsSoundActivity = NotificationsSoundActivity.this;
            int i2 = notificationsSoundActivity.systemTonesStartRow;
            if (i >= i2 && i < notificationsSoundActivity.systemTonesEndRow) {
                return notificationsSoundActivity.systemTones.get(i - i2);
            }
            int i3 = notificationsSoundActivity.serverTonesStartRow;
            if (i < i3 || i >= notificationsSoundActivity.serverTonesEndRow) {
                return null;
            }
            return notificationsSoundActivity.serverTones.get(i - i3);
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v1, resolved type: org.telegram.ui.NotificationsSoundActivity$ToneCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v3, resolved type: org.telegram.ui.Cells.CreationTextCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v6, resolved type: org.telegram.ui.NotificationsSoundActivity$ToneCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v7, resolved type: org.telegram.ui.NotificationsSoundActivity$ToneCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v8, resolved type: org.telegram.ui.Cells.ShadowSectionCell} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r4v9, resolved type: org.telegram.ui.Cells.HeaderCell} */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public androidx.recyclerview.widget.RecyclerView.ViewHolder onCreateViewHolder(android.view.ViewGroup r3, int r4) {
            /*
                r2 = this;
                android.content.Context r3 = r3.getContext()
                java.lang.String r0 = "windowBackgroundWhite"
                if (r4 == 0) goto L_0x0046
                r1 = 2
                if (r4 == r1) goto L_0x002d
                r1 = 3
                if (r4 == r1) goto L_0x0023
                org.telegram.ui.Cells.HeaderCell r4 = new org.telegram.ui.Cells.HeaderCell
                org.telegram.ui.NotificationsSoundActivity r1 = org.telegram.ui.NotificationsSoundActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r1.resourcesProvider
                r4.<init>((android.content.Context) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r1)
                org.telegram.ui.NotificationsSoundActivity r3 = org.telegram.ui.NotificationsSoundActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
                r4.setBackgroundColor(r3)
                goto L_0x005a
            L_0x0023:
                org.telegram.ui.Cells.ShadowSectionCell r4 = new org.telegram.ui.Cells.ShadowSectionCell
                org.telegram.ui.NotificationsSoundActivity r0 = org.telegram.ui.NotificationsSoundActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r0 = r0.resourcesProvider
                r4.<init>((android.content.Context) r3, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r0)
                goto L_0x005a
            L_0x002d:
                org.telegram.ui.Cells.CreationTextCell r4 = new org.telegram.ui.Cells.CreationTextCell
                org.telegram.ui.NotificationsSoundActivity r1 = org.telegram.ui.NotificationsSoundActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r1.resourcesProvider
                r4.<init>(r3, r1)
                r3 = 61
                r4.startPadding = r3
                org.telegram.ui.NotificationsSoundActivity r3 = org.telegram.ui.NotificationsSoundActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
                r4.setBackgroundColor(r3)
                goto L_0x005a
            L_0x0046:
                org.telegram.ui.NotificationsSoundActivity$ToneCell r4 = new org.telegram.ui.NotificationsSoundActivity$ToneCell
                org.telegram.ui.NotificationsSoundActivity r1 = org.telegram.ui.NotificationsSoundActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r1 = r1.resourcesProvider
                r4.<init>(r3, r1)
                org.telegram.ui.NotificationsSoundActivity r3 = org.telegram.ui.NotificationsSoundActivity.this
                org.telegram.ui.ActionBar.Theme$ResourcesProvider r3 = r3.resourcesProvider
                int r3 = org.telegram.ui.ActionBar.Theme.getColor((java.lang.String) r0, (org.telegram.ui.ActionBar.Theme.ResourcesProvider) r3)
                r4.setBackgroundColor(r3)
            L_0x005a:
                androidx.recyclerview.widget.RecyclerView$LayoutParams r3 = new androidx.recyclerview.widget.RecyclerView$LayoutParams
                r0 = -1
                r1 = -2
                r3.<init>((int) r0, (int) r1)
                r4.setLayoutParams(r3)
                org.telegram.ui.Components.RecyclerListView$Holder r3 = new org.telegram.ui.Components.RecyclerListView$Holder
                r3.<init>(r4)
                return r3
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.NotificationsSoundActivity.Adapter.onCreateViewHolder(android.view.ViewGroup, int):androidx.recyclerview.widget.RecyclerView$ViewHolder");
        }

        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            boolean z = false;
            if (itemViewType == 0) {
                ToneCell toneCell = (ToneCell) viewHolder.itemView;
                Tone tone = null;
                NotificationsSoundActivity notificationsSoundActivity = NotificationsSoundActivity.this;
                int i2 = notificationsSoundActivity.systemTonesStartRow;
                if (i >= i2 && i < notificationsSoundActivity.systemTonesEndRow) {
                    tone = notificationsSoundActivity.systemTones.get(i - i2);
                }
                NotificationsSoundActivity notificationsSoundActivity2 = NotificationsSoundActivity.this;
                int i3 = notificationsSoundActivity2.serverTonesStartRow;
                if (i >= i3 && i < notificationsSoundActivity2.serverTonesEndRow) {
                    tone = notificationsSoundActivity2.serverTones.get(i - i3);
                }
                if (tone != null) {
                    boolean z2 = toneCell.tone == tone;
                    NotificationsSoundActivity notificationsSoundActivity3 = NotificationsSoundActivity.this;
                    boolean z3 = tone == notificationsSoundActivity3.selectedTone;
                    boolean z4 = notificationsSoundActivity3.selectedTones.get(tone.stableId) != null;
                    toneCell.tone = tone;
                    toneCell.textView.setText(tone.title);
                    if (i != NotificationsSoundActivity.this.systemTonesEndRow - 1) {
                        z = true;
                    }
                    boolean unused = toneCell.needDivider = z;
                    toneCell.radioButton.setChecked(z3, z2);
                    toneCell.checkBox.setChecked(z4, z2);
                }
            } else if (itemViewType == 1) {
                HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
                NotificationsSoundActivity notificationsSoundActivity4 = NotificationsSoundActivity.this;
                if (i == notificationsSoundActivity4.serverTonesHeaderRow) {
                    headerCell.setText(LocaleController.getString("TelegramTones", R.string.TelegramTones));
                } else if (i == notificationsSoundActivity4.systemTonesHeaderRow) {
                    headerCell.setText(LocaleController.getString("SystemTones", R.string.SystemTones));
                }
            } else if (itemViewType == 2) {
                CreationTextCell creationTextCell = (CreationTextCell) viewHolder.itemView;
                Drawable drawable = creationTextCell.getContext().getResources().getDrawable(R.drawable.poll_add_circle);
                Drawable drawable2 = creationTextCell.getContext().getResources().getDrawable(R.drawable.poll_add_plus);
                drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked", NotificationsSoundActivity.this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
                drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck", NotificationsSoundActivity.this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
                creationTextCell.setTextAndIcon(LocaleController.getString("UploadSound", R.string.UploadSound), new CombinedDrawable(drawable, drawable2), false);
            }
        }

        public int getItemViewType(int i) {
            NotificationsSoundActivity notificationsSoundActivity = NotificationsSoundActivity.this;
            if (i >= notificationsSoundActivity.systemTonesStartRow && i < notificationsSoundActivity.systemTonesEndRow) {
                return 0;
            }
            if (i == notificationsSoundActivity.serverTonesHeaderRow || i == notificationsSoundActivity.systemTonesHeaderRow) {
                return 1;
            }
            if (i == notificationsSoundActivity.uploadRow) {
                return 2;
            }
            if (i == notificationsSoundActivity.dividerRow || i == notificationsSoundActivity.dividerRow2) {
                return 3;
            }
            return super.getItemViewType(i);
        }

        public int getItemCount() {
            return NotificationsSoundActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0 || viewHolder.getItemViewType() == 2;
        }
    }

    private static class ToneCell extends FrameLayout {
        /* access modifiers changed from: private */
        public CheckBox2 checkBox;
        /* access modifiers changed from: private */
        public boolean needDivider;
        /* access modifiers changed from: private */
        public RadioButton radioButton;
        /* access modifiers changed from: private */
        public TextView textView;
        Tone tone;

        public ToneCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            RadioButton radioButton2 = new RadioButton(context);
            this.radioButton = radioButton2;
            radioButton2.setSize(AndroidUtilities.dp(20.0f));
            this.radioButton.setColor(Theme.getColor("radioBackground", resourcesProvider), Theme.getColor("radioBackgroundChecked", resourcesProvider));
            RadioButton radioButton3 = this.radioButton;
            boolean z = LocaleController.isRTL;
            int i = 5;
            addView(radioButton3, LayoutHelper.createFrame(22, 22.0f, (z ? 5 : 3) | 16, (float) (z ? 0 : 20), 0.0f, (float) (!z ? 0 : 20), 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 24, resourcesProvider);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox22 = this.checkBox;
            boolean z2 = LocaleController.isRTL;
            addView(checkBox22, LayoutHelper.createFrame(26, 26.0f, (z2 ? 5 : 3) | 16, (float) (z2 ? 0 : 18), 0.0f, (float) (!z2 ? 0 : 18), 0.0f));
            this.checkBox.setChecked(true, false);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText", resourcesProvider));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            TextView textView3 = this.textView;
            boolean z3 = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-2, -2.0f, (!z3 ? 3 : i) | 16, (float) (z3 ? 23 : 61), 0.0f, (float) (z3 ? 61 : 23), 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            if (this.needDivider) {
                float f = 0.0f;
                float dp = (float) AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : 60.0f);
                float height = (float) (getHeight() - 1);
                int measuredWidth = getMeasuredWidth();
                if (LocaleController.isRTL) {
                    f = 60.0f;
                }
                canvas.drawLine(dp, height, (float) (measuredWidth - AndroidUtilities.dp(f)), (float) (getHeight() - 1), Theme.dividerPaint);
            }
        }

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.widget.RadioButton");
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(this.radioButton.isChecked());
        }
    }

    public void onResume() {
        super.onResume();
        getNotificationCenter().addObserver(this, NotificationCenter.onUserRingtonesUpdated);
    }

    public void onPause() {
        super.onPause();
        getNotificationCenter().removeObserver(this, NotificationCenter.onUserRingtonesUpdated);
    }

    public int getNavigationBarColor() {
        return getThemedColor("windowBackgroundGray");
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC$Document tLRPC$Document;
        TLRPC$Document tLRPC$Document2;
        if (i == NotificationCenter.onUserRingtonesUpdated) {
            HashMap hashMap = new HashMap();
            for (int i3 = 0; i3 < this.serverTones.size(); i3++) {
                hashMap.put(Integer.valueOf(this.serverTones.get(i3).localId), this.serverTones.get(i3));
            }
            this.serverTones.clear();
            for (int i4 = 0; i4 < getMediaDataController().ringtoneDataStore.userRingtones.size(); i4++) {
                RingtoneDataStore.CachedTone cachedTone = getMediaDataController().ringtoneDataStore.userRingtones.get(i4);
                Tone tone = new Tone();
                Tone tone2 = (Tone) hashMap.get(Integer.valueOf(cachedTone.localId));
                if (tone2 != null) {
                    if (tone2 == this.selectedTone) {
                        this.selectedTone = tone;
                    }
                    tone.stableId = tone2.stableId;
                } else {
                    int i5 = this.stableIds;
                    this.stableIds = i5 + 1;
                    tone.stableId = i5;
                }
                tone.fromServer = true;
                tone.localId = cachedTone.localId;
                TLRPC$Document tLRPC$Document3 = cachedTone.document;
                if (tLRPC$Document3 != null) {
                    tone.title = tLRPC$Document3.file_name_fixed;
                } else {
                    tone.title = new File(cachedTone.localUri).getName();
                }
                tone.document = cachedTone.document;
                trimTitle(tone);
                tone.uri = cachedTone.localUri;
                Tone tone3 = this.startSelectedTone;
                if (!(tone3 == null || (tLRPC$Document = tone3.document) == null || (tLRPC$Document2 = cachedTone.document) == null || tLRPC$Document.id != tLRPC$Document2.id)) {
                    this.startSelectedTone = null;
                    this.selectedTone = tone;
                }
                this.serverTones.add(tone);
            }
            updateRows();
            this.adapter.notifyDataSetChanged();
            if (getMediaDataController().ringtoneDataStore.isLoaded() && this.selectedTone == null && this.systemTones.size() > 0) {
                this.startSelectedTone = null;
                this.selectedTone = this.systemTones.get(0);
            }
        }
    }

    private void trimTitle(Tone tone) {
        tone.title = trimTitle(tone.document, tone.title);
    }

    public static String trimTitle(TLRPC$Document tLRPC$Document, String str) {
        int lastIndexOf;
        if (!(str == null || (lastIndexOf = str.lastIndexOf(46)) == -1)) {
            str = str.substring(0, lastIndexOf);
        }
        if (!TextUtils.isEmpty(str) || tLRPC$Document == null) {
            return str;
        }
        return LocaleController.formatString("SoundNameEmpty", R.string.SoundNameEmpty, LocaleController.formatDateChat((long) tLRPC$Document.date, true));
    }

    public void onFragmentDestroy() {
        String str;
        String str2;
        String str3;
        TLRPC$Document tLRPC$Document;
        super.onFragmentDestroy();
        if (this.selectedTone != null && this.selectedToneChanged) {
            SharedPreferences.Editor edit = getNotificationsSettings().edit();
            if (this.dialogId != 0) {
                str3 = "sound_" + this.dialogId;
                str2 = "sound_path_" + this.dialogId;
                str = "sound_document_id_" + this.dialogId;
                edit.putBoolean("sound_enabled_" + this.dialogId, true);
            } else {
                int i = this.currentType;
                if (i == 1) {
                    str3 = "GlobalSound";
                    str2 = "GlobalSoundPath";
                    str = "GlobalSoundDocId";
                } else if (i == 0) {
                    str3 = "GroupSound";
                    str2 = "GroupSoundPath";
                    str = "GroupSoundDocId";
                } else if (i == 2) {
                    str3 = "ChannelSound";
                    str2 = "ChannelSoundPath";
                    str = "ChannelSoundDocId";
                } else {
                    throw new RuntimeException("Unsupported type");
                }
            }
            Tone tone = this.selectedTone;
            if (tone.fromServer && (tLRPC$Document = tone.document) != null) {
                edit.putLong(str, tLRPC$Document.id);
                edit.putString(str3, this.selectedTone.title);
                edit.putString(str2, "NoSound");
            } else if (tone.uri != null) {
                edit.putString(str3, tone.title);
                edit.putString(str2, this.selectedTone.uri);
                edit.remove(str);
            } else if (tone.isSystemDefault) {
                edit.putString(str3, "Default");
                edit.putString(str2, "Default");
                edit.remove(str);
            } else {
                edit.putString(str3, "NoSound");
                edit.putString(str2, "NoSound");
                edit.remove(str);
            }
            edit.apply();
            if (this.dialogId != 0) {
                getNotificationsController().updateServerNotificationsSettings(this.dialogId);
                return;
            }
            getNotificationsController().updateServerNotificationsSettings(this.currentType);
            getNotificationCenter().postNotificationName(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    public void startDocumentSelectActivity() {
        try {
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            if (Build.VERSION.SDK_INT >= 18) {
                intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            }
            intent.setType("audio/mpeg");
            startActivityForResult(intent, 21);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
        if (i == 21 && intent != null && this.chatAttachAlert != null) {
            boolean z = true;
            boolean z2 = false;
            if (intent.getData() != null) {
                String path = AndroidUtilities.getPath(intent.getData());
                if (path != null) {
                    if (this.chatAttachAlert.getDocumentLayout().isRingtone(new File(path))) {
                        getMediaDataController().uploadRingtone(path);
                        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
                        z2 = z;
                    }
                }
                z = false;
                z2 = z;
            } else if (intent.getClipData() != null) {
                ClipData clipData = intent.getClipData();
                boolean z3 = false;
                for (int i3 = 0; i3 < clipData.getItemCount(); i3++) {
                    String uri = clipData.getItemAt(i3).getUri().toString();
                    if (this.chatAttachAlert.getDocumentLayout().isRingtone(new File(uri))) {
                        getMediaDataController().uploadRingtone(uri);
                        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
                        z3 = true;
                    }
                }
                z2 = z3;
            }
            if (z2) {
                this.chatAttachAlert.dismiss();
            }
        }
    }

    private static class Tone {
        TLRPC$Document document;
        public boolean fromServer;
        boolean isSystemDefault;
        int localId;
        int stableId;
        String title;
        String uri;

        private Tone() {
        }

        public Uri getUriForShare(int i) {
            if (!TextUtils.isEmpty(this.uri)) {
                return Uri.fromFile(new File(this.uri));
            }
            TLRPC$Document tLRPC$Document = this.document;
            if (tLRPC$Document == null) {
                return null;
            }
            String str = tLRPC$Document.file_name_fixed;
            String documentExtension = FileLoader.getDocumentExtension(tLRPC$Document);
            if (documentExtension == null) {
                return null;
            }
            String lowerCase = documentExtension.toLowerCase();
            if (!str.endsWith(lowerCase)) {
                str = str + "." + lowerCase;
            }
            File file = new File(AndroidUtilities.getCacheDir(), str);
            if (!file.exists()) {
                try {
                    AndroidUtilities.copyFile(FileLoader.getInstance(i).getPathToAttach(this.document), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return Uri.fromFile(file);
        }
    }
}

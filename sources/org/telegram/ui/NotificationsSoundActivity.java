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
import android.view.ViewGroup;
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
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.messenger.ringtone.RingtoneUploader;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CreationTextCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
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
    private static final int deleteId = 1;
    private static final int shareId = 2;
    Adapter adapter;
    ChatAvatarContainer avatarContainer;
    ChatAttachAlert chatAttachAlert;
    int currentType = -1;
    long dialogId;
    int dividerRow;
    int dividerRow2;
    Ringtone lastPlayedRingtone;
    RecyclerListView listView;
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
    private final int tonesStreamType = 4;
    int uploadRow;
    ArrayList<Tone> uploadingTones = new ArrayList<>();

    public /* synthetic */ void didSelectPhotos(ArrayList arrayList, boolean z, int i) {
        ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate.CC.$default$didSelectPhotos(this, arrayList, z, i);
    }

    public /* synthetic */ void startMusicSelectActivity() {
        ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate.CC.$default$startMusicSelectActivity(this);
    }

    public NotificationsSoundActivity(Bundle args) {
        super(args);
    }

    public boolean onFragmentCreate() {
        String prefPath;
        String prefDocId;
        if (getArguments() != null) {
            this.dialogId = getArguments().getLong("dialog_id", 0);
            this.currentType = getArguments().getInt("type", -1);
        }
        if (this.dialogId != 0) {
            prefDocId = "sound_document_id_" + this.dialogId;
            prefPath = "sound_path_" + this.dialogId;
        } else {
            int i = this.currentType;
            if (i == 1) {
                prefPath = "GlobalSoundPath";
                prefDocId = "GlobalSoundDocId";
            } else if (i == 0) {
                prefPath = "GroupSoundPath";
                prefDocId = "GroupSoundDocId";
            } else if (i == 2) {
                prefPath = "ChannelSoundPath";
                prefDocId = "ChannelSoundDocId";
            } else {
                throw new RuntimeException("Unsupported type");
            }
        }
        SharedPreferences preferences = getNotificationsSettings();
        long documentId = preferences.getLong(prefDocId, 0);
        String localUri = preferences.getString(prefPath, "NoSound");
        Tone tone = new Tone();
        this.startSelectedTone = tone;
        if (documentId != 0) {
            tone.document = new TLRPC.TL_document();
            this.startSelectedTone.document.id = documentId;
        } else {
            tone.uri = localUri;
        }
        return super.onFragmentCreate();
    }

    public View createView(Context context) {
        final Context context2 = context;
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    if (NotificationsSoundActivity.this.actionBar.isActionModeShowed()) {
                        NotificationsSoundActivity.this.hideActionMode();
                    } else {
                        NotificationsSoundActivity.this.finishFragment();
                    }
                } else if (id == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder((Context) NotificationsSoundActivity.this.getParentActivity());
                    builder.setTitle(LocaleController.formatPluralString("DeleteTones", NotificationsSoundActivity.this.selectedTones.size()));
                    builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatPluralString("DeleteTonesMessage", NotificationsSoundActivity.this.selectedTones.size())));
                    builder.setNegativeButton(LocaleController.getString("Cancel", NUM), NotificationsSoundActivity$1$$ExternalSyntheticLambda1.INSTANCE);
                    builder.setPositiveButton(LocaleController.getString("Delete", NUM), new NotificationsSoundActivity$1$$ExternalSyntheticLambda0(this));
                    TextView button = (TextView) builder.show().getButton(-1);
                    if (button != null) {
                        button.setTextColor(Theme.getColor("dialogTextRed2"));
                    }
                } else if (id == 2) {
                    if (NotificationsSoundActivity.this.selectedTones.size() == 1) {
                        Intent intent = new Intent(context2, LaunchActivity.class);
                        intent.setAction("android.intent.action.SEND");
                        Uri uri = NotificationsSoundActivity.this.selectedTones.valueAt(0).getUriForShare(NotificationsSoundActivity.this.currentAccount);
                        if (uri != null) {
                            intent.putExtra("android.intent.extra.STREAM", uri);
                            context2.startActivity(intent);
                        }
                    } else {
                        Intent intent2 = new Intent(context2, LaunchActivity.class);
                        intent2.setAction("android.intent.action.SEND_MULTIPLE");
                        ArrayList<Uri> uries = new ArrayList<>();
                        for (int i = 0; i < NotificationsSoundActivity.this.selectedTones.size(); i++) {
                            Uri uri2 = NotificationsSoundActivity.this.selectedTones.valueAt(i).getUriForShare(NotificationsSoundActivity.this.currentAccount);
                            if (uri2 != null) {
                                uries.add(uri2);
                            }
                        }
                        if (uries.isEmpty() == 0) {
                            intent2.putParcelableArrayListExtra("android.intent.extra.STREAM", uries);
                            context2.startActivity(intent2);
                        }
                    }
                    NotificationsSoundActivity.this.hideActionMode();
                    NotificationsSoundActivity.this.updateRows();
                    NotificationsSoundActivity.this.adapter.notifyDataSetChanged();
                }
            }

            /* renamed from: lambda$onItemClick$1$org-telegram-ui-NotificationsSoundActivity$1  reason: not valid java name */
            public /* synthetic */ void m2674xe3a080d7(DialogInterface dialog, int which) {
                deleteSelectedMessages();
                dialog.dismiss();
            }

            private void deleteSelectedMessages() {
                RingtoneUploader ringtoneUploader;
                ArrayList<TLRPC.Document> documentsToRemove = new ArrayList<>();
                for (int i = 0; i < NotificationsSoundActivity.this.selectedTones.size(); i++) {
                    Tone tone = NotificationsSoundActivity.this.selectedTones.valueAt(i);
                    if (tone.document != null) {
                        documentsToRemove.add(tone.document);
                        NotificationsSoundActivity.this.getMediaDataController().ringtoneDataStore.remove(tone.document);
                    }
                    if (!(tone.uri == null || (ringtoneUploader = NotificationsSoundActivity.this.getMediaDataController().ringtoneUploaderHashMap.get(tone.uri)) == null)) {
                        ringtoneUploader.cancel();
                    }
                    if (tone == NotificationsSoundActivity.this.selectedTone) {
                        Tone unused = NotificationsSoundActivity.this.startSelectedTone = null;
                        NotificationsSoundActivity notificationsSoundActivity = NotificationsSoundActivity.this;
                        notificationsSoundActivity.selectedTone = notificationsSoundActivity.systemTones.get(0);
                        NotificationsSoundActivity.this.selectedToneChanged = true;
                    }
                    NotificationsSoundActivity.this.serverTones.remove(tone);
                    NotificationsSoundActivity.this.uploadingTones.remove(tone);
                }
                NotificationsSoundActivity.this.getMediaDataController().ringtoneDataStore.saveTones();
                for (int i2 = 0; i2 < documentsToRemove.size(); i2++) {
                    TLRPC.Document document = documentsToRemove.get(i2);
                    TLRPC.TL_account_saveRingtone req = new TLRPC.TL_account_saveRingtone();
                    req.id = new TLRPC.TL_inputDocument();
                    req.id.id = document.id;
                    req.id.access_hash = document.access_hash;
                    req.id.file_reference = document.file_reference;
                    if (req.id.file_reference == null) {
                        req.id.file_reference = new byte[0];
                    }
                    req.unsave = true;
                    NotificationsSoundActivity.this.getConnectionsManager().sendRequest(req, NotificationsSoundActivity$1$$ExternalSyntheticLambda2.INSTANCE);
                }
                NotificationsSoundActivity.this.hideActionMode();
                NotificationsSoundActivity.this.updateRows();
                NotificationsSoundActivity.this.adapter.notifyDataSetChanged();
            }

            static /* synthetic */ void lambda$deleteSelectedMessages$2(TLObject response, TLRPC.TL_error error) {
            }
        });
        if (this.dialogId == 0) {
            int i = this.currentType;
            if (i == 1) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundPrivate", NUM));
            } else if (i == 0) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundGroup", NUM));
            } else if (i == 2) {
                this.actionBar.setTitle(LocaleController.getString("NotificationsSoundChannels", NUM));
            }
        } else {
            ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context2, (ChatActivity) null, false);
            this.avatarContainer = chatAvatarContainer;
            chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
            this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, !this.inPreviewMode ? 56.0f : 0.0f, 0.0f, 40.0f, 0.0f));
            if (this.dialogId < 0) {
                TLRPC.Chat chatLocal = getMessagesController().getChat(Long.valueOf(-this.dialogId));
                this.avatarContainer.setChatAvatar(chatLocal);
                this.avatarContainer.setTitle(chatLocal.title);
            } else {
                TLRPC.User user = getMessagesController().getUser(Long.valueOf(this.dialogId));
                if (user != null) {
                    this.avatarContainer.setUserAvatar(user);
                    this.avatarContainer.setTitle(ContactsController.formatName(user.first_name, user.last_name));
                }
            }
            this.avatarContainer.setSubtitle(LocaleController.getString("NotificationsSound", NUM));
        }
        ActionBarMenu actionMode = this.actionBar.createActionMode();
        NumberTextView numberTextView = new NumberTextView(actionMode.getContext());
        this.selectedTonesCountTextView = numberTextView;
        numberTextView.setTextSize(18);
        this.selectedTonesCountTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.selectedTonesCountTextView.setTextColor(Theme.getColor("actionBarActionModeDefaultIcon"));
        actionMode.addView(this.selectedTonesCountTextView, LayoutHelper.createLinear(0, -1, 1.0f, 72, 0, 0, 0));
        this.selectedTonesCountTextView.setOnTouchListener(NotificationsSoundActivity$$ExternalSyntheticLambda0.INSTANCE);
        actionMode.addItemWithWidth(2, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("ShareFile", NUM));
        actionMode.addItemWithWidth(1, NUM, AndroidUtilities.dp(54.0f), LocaleController.getString("Delete", NUM));
        this.fragmentView = new FrameLayout(context2);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        frameLayout.setBackgroundColor(Theme.getColor("windowBackgroundGray"));
        RecyclerListView recyclerListView = new RecyclerListView(context2);
        this.listView = recyclerListView;
        frameLayout.addView(recyclerListView, LayoutHelper.createFrame(-1, -1.0f));
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

    static /* synthetic */ boolean lambda$createView$0(View v, MotionEvent event) {
        return true;
    }

    /* renamed from: lambda$createView$1$org-telegram-ui-NotificationsSoundActivity  reason: not valid java name */
    public /* synthetic */ void m2672lambda$createView$1$orgtelegramuiNotificationsSoundActivity(Context context, View view, int position) {
        if (position == this.uploadRow) {
            ChatAttachAlert chatAttachAlert2 = new ChatAttachAlert(context, this, false, false);
            this.chatAttachAlert = chatAttachAlert2;
            chatAttachAlert2.setSoundPicker();
            this.chatAttachAlert.init();
            this.chatAttachAlert.show();
        }
        if (view instanceof ToneCell) {
            ToneCell cell = (ToneCell) view;
            if (this.actionBar.isActionModeShowed() || cell.tone == null) {
                checkSelection(cell.tone);
                return;
            }
            Ringtone ringtone = this.lastPlayedRingtone;
            if (ringtone != null) {
                ringtone.stop();
            }
            if (cell.tone.isSystemDefault) {
                Ringtone r = RingtoneManager.getRingtone(context.getApplicationContext(), RingtoneManager.getDefaultUri(2));
                r.setStreamType(4);
                this.lastPlayedRingtone = r;
                r.play();
            } else if (cell.tone.uri != null && !cell.tone.fromServer) {
                Ringtone r2 = RingtoneManager.getRingtone(context.getApplicationContext(), Uri.parse(cell.tone.uri));
                r2.setStreamType(4);
                this.lastPlayedRingtone = r2;
                r2.play();
            } else if (cell.tone.fromServer) {
                File file = null;
                if (!TextUtils.isEmpty(cell.tone.uri)) {
                    File localUriFile = new File(cell.tone.uri);
                    if (localUriFile.exists()) {
                        file = localUriFile;
                    }
                }
                if (file == null) {
                    file = FileLoader.getPathToAttach(cell.tone.document);
                }
                if (file == null || !file.exists()) {
                    getFileLoader().loadFile(cell.tone.document, cell.tone.document, 2, 0);
                } else {
                    Ringtone r3 = RingtoneManager.getRingtone(context.getApplicationContext(), Uri.parse(file.toString()));
                    r3.setStreamType(4);
                    this.lastPlayedRingtone = r3;
                    r3.play();
                }
            }
            this.startSelectedTone = null;
            this.selectedTone = cell.tone;
            this.selectedToneChanged = true;
            Adapter adapter2 = this.adapter;
            adapter2.notifyItemRangeChanged(0, adapter2.getItemCount());
        }
    }

    /* renamed from: lambda$createView$2$org-telegram-ui-NotificationsSoundActivity  reason: not valid java name */
    public /* synthetic */ boolean m2673lambda$createView$2$orgtelegramuiNotificationsSoundActivity(View view, int position) {
        if (view instanceof ToneCell) {
            ToneCell cell = (ToneCell) view;
            checkSelection(cell.tone);
            cell.performHapticFeedback(0);
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
        boolean changed = false;
        if (this.selectedTones.get(tone.stableId) != null) {
            this.selectedTones.remove(tone.stableId);
            changed = true;
        } else if (tone.fromServer) {
            this.selectedTones.put(tone.stableId, tone);
            changed = true;
        }
        if (changed) {
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
        getMediaDataController().ringtoneDataStore.m1134lambda$new$0$orgtelegrammessengerringtoneRingtoneDataStore();
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
            tone.title = cachedTone.document.file_name_fixed;
            tone.document = cachedTone.document;
            trimTitle(tone);
            tone.uri = cachedTone.localUri;
            Tone tone2 = this.startSelectedTone;
            if (!(tone2 == null || tone2.document == null || cachedTone.document == null || this.startSelectedTone.document.id != cachedTone.document.id)) {
                this.startSelectedTone = null;
                this.selectedTone = tone;
            }
            this.serverTones.add(tone);
        }
        RingtoneManager manager = new RingtoneManager(ApplicationLoader.applicationContext);
        manager.setType(2);
        Cursor cursor = manager.getCursor();
        Tone noSoundTone = new Tone();
        int i3 = this.stableIds;
        this.stableIds = i3 + 1;
        noSoundTone.stableId = i3;
        noSoundTone.title = LocaleController.getString("NoSound", NUM);
        noSoundTone.isSystemNoSound = true;
        this.systemTones.add(noSoundTone);
        Tone defaultTone = new Tone();
        int i4 = this.stableIds;
        this.stableIds = i4 + 1;
        defaultTone.stableId = i4;
        defaultTone.title = LocaleController.getString("DefaultRingtone", NUM);
        defaultTone.isSystemDefault = true;
        this.systemTones.add(defaultTone);
        Tone tone3 = this.startSelectedTone;
        if (tone3 != null && tone3.document == null && this.startSelectedTone.uri.equals("NoSound")) {
            this.startSelectedTone = null;
            this.selectedTone = noSoundTone;
        }
        Tone tone4 = this.startSelectedTone;
        if (tone4 != null && tone4.document == null && this.startSelectedTone.uri.equals("Default")) {
            this.startSelectedTone = null;
            this.selectedTone = defaultTone;
        }
        while (cursor.moveToNext()) {
            String notificationTitle = cursor.getString(1);
            String notificationUri = cursor.getString(2) + "/" + cursor.getString(0);
            Tone tone5 = new Tone();
            int i5 = this.stableIds;
            this.stableIds = i5 + 1;
            tone5.stableId = i5;
            tone5.title = notificationTitle;
            tone5.uri = notificationUri;
            Tone tone6 = this.startSelectedTone;
            if (tone6 != null && tone6.document == null && this.startSelectedTone.uri.equals(notificationUri)) {
                this.startSelectedTone = null;
                this.selectedTone = tone5;
            }
            this.systemTones.add(tone5);
        }
        if (getMediaDataController().ringtoneDataStore.isLoaded() && this.selectedTone == null) {
            this.selectedTone = defaultTone;
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

    public void didSelectFiles(ArrayList<String> files, String caption, ArrayList<MessageObject> arrayList, boolean notify, int scheduleDate) {
        for (int i = 0; i < files.size(); i++) {
            getMediaDataController().uploadRingtone(files.get(i));
        }
        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
    }

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public long getItemId(int position) {
            Tone tone = getTone(position);
            if (tone != null) {
                return (long) tone.stableId;
            }
            if (position == NotificationsSoundActivity.this.serverTonesHeaderRow) {
                return 1;
            }
            if (position == NotificationsSoundActivity.this.systemTonesHeaderRow) {
                return 2;
            }
            if (position == NotificationsSoundActivity.this.uploadRow) {
                return 3;
            }
            if (position == NotificationsSoundActivity.this.dividerRow) {
                return 4;
            }
            if (position == NotificationsSoundActivity.this.dividerRow2) {
                return 5;
            }
            throw new RuntimeException();
        }

        private Tone getTone(int position) {
            if (position >= NotificationsSoundActivity.this.systemTonesStartRow && position < NotificationsSoundActivity.this.systemTonesEndRow) {
                return NotificationsSoundActivity.this.systemTones.get(position - NotificationsSoundActivity.this.systemTonesStartRow);
            }
            if (position < NotificationsSoundActivity.this.serverTonesStartRow || position >= NotificationsSoundActivity.this.serverTonesEndRow) {
                return null;
            }
            return NotificationsSoundActivity.this.serverTones.get(position - NotificationsSoundActivity.this.serverTonesStartRow);
        }

        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            Context context = parent.getContext();
            switch (viewType) {
                case 0:
                    View view2 = new ToneCell(context);
                    view2.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view2;
                    break;
                case 2:
                    CreationTextCell creationTextCell = new CreationTextCell(context);
                    creationTextCell.startPadding = 61;
                    View view3 = creationTextCell;
                    view3.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view3;
                    break;
                case 3:
                    view = new ShadowSectionCell(context);
                    break;
                default:
                    View view4 = new HeaderCell(context);
                    view4.setBackgroundColor(Theme.getColor("windowBackgroundWhite"));
                    view = view4;
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            boolean z = false;
            switch (holder.getItemViewType()) {
                case 0:
                    ToneCell toneCell = (ToneCell) holder.itemView;
                    Tone tone = null;
                    if (position >= NotificationsSoundActivity.this.systemTonesStartRow && position < NotificationsSoundActivity.this.systemTonesEndRow) {
                        tone = NotificationsSoundActivity.this.systemTones.get(position - NotificationsSoundActivity.this.systemTonesStartRow);
                    }
                    if (position >= NotificationsSoundActivity.this.serverTonesStartRow && position < NotificationsSoundActivity.this.serverTonesEndRow) {
                        tone = NotificationsSoundActivity.this.serverTones.get(position - NotificationsSoundActivity.this.serverTonesStartRow);
                    }
                    if (tone != null) {
                        boolean animated = toneCell.tone == tone;
                        boolean checked = tone == NotificationsSoundActivity.this.selectedTone;
                        boolean selected = NotificationsSoundActivity.this.selectedTones.get(tone.stableId) != null;
                        toneCell.tone = tone;
                        toneCell.textView.setText(tone.title);
                        if (position != NotificationsSoundActivity.this.systemTonesEndRow - 1) {
                            z = true;
                        }
                        boolean unused = toneCell.needDivider = z;
                        toneCell.radioButton.setChecked(checked, animated);
                        toneCell.checkBox.setChecked(selected, animated);
                        return;
                    }
                    return;
                case 1:
                    HeaderCell headerCell = (HeaderCell) holder.itemView;
                    if (position == NotificationsSoundActivity.this.serverTonesHeaderRow) {
                        headerCell.setText(LocaleController.getString("TelegramTones", NUM));
                        return;
                    } else if (position == NotificationsSoundActivity.this.systemTonesHeaderRow) {
                        headerCell.setText(LocaleController.getString("SystemTones", NUM));
                        return;
                    } else {
                        return;
                    }
                case 2:
                    CreationTextCell textCell = (CreationTextCell) holder.itemView;
                    Drawable drawable1 = textCell.getContext().getResources().getDrawable(NUM);
                    Drawable drawable2 = textCell.getContext().getResources().getDrawable(NUM);
                    drawable1.setColorFilter(new PorterDuffColorFilter(Theme.getColor("switchTrackChecked"), PorterDuff.Mode.MULTIPLY));
                    drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor("checkboxCheck"), PorterDuff.Mode.MULTIPLY));
                    textCell.setTextAndIcon(LocaleController.getString("UploadSound", NUM), new CombinedDrawable(drawable1, drawable2), false);
                    return;
                default:
                    return;
            }
        }

        public int getItemViewType(int position) {
            if (position >= NotificationsSoundActivity.this.systemTonesStartRow && position < NotificationsSoundActivity.this.systemTonesEndRow) {
                return 0;
            }
            if (position == NotificationsSoundActivity.this.serverTonesHeaderRow || position == NotificationsSoundActivity.this.systemTonesHeaderRow) {
                return 1;
            }
            if (position == NotificationsSoundActivity.this.uploadRow) {
                return 2;
            }
            if (position == NotificationsSoundActivity.this.dividerRow || position == NotificationsSoundActivity.this.dividerRow2) {
                return 3;
            }
            return super.getItemViewType(position);
        }

        public int getItemCount() {
            return NotificationsSoundActivity.this.rowCount;
        }

        public boolean isEnabled(RecyclerView.ViewHolder holder) {
            return holder.getItemViewType() == 0 || holder.getItemViewType() == 2;
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
        public TextView valueTextView;

        public ToneCell(Context context) {
            super(context);
            RadioButton radioButton2 = new RadioButton(context);
            this.radioButton = radioButton2;
            radioButton2.setSize(AndroidUtilities.dp(20.0f));
            this.radioButton.setColor(Theme.getColor("radioBackground"), Theme.getColor("radioBackgroundChecked"));
            int i = 5;
            addView(this.radioButton, LayoutHelper.createFrame(22, 22.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 20), 0.0f, (float) (!LocaleController.isRTL ? 0 : 20), 0.0f));
            CheckBox2 checkBox2 = new CheckBox2(context, 24);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(26, 26.0f, (LocaleController.isRTL ? 5 : 3) | 16, (float) (LocaleController.isRTL ? 0 : 18), 0.0f, (float) (!LocaleController.isRTL ? 0 : 18), 0.0f));
            this.checkBox.setChecked(true, false);
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, (!LocaleController.isRTL ? 3 : i) | 16, (float) (LocaleController.isRTL ? 23 : 61), 0.0f, (float) (LocaleController.isRTL ? 61 : 23), 0.0f));
        }

        /* access modifiers changed from: protected */
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), NUM));
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

        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
            super.onInitializeAccessibilityNodeInfo(info);
            info.setClassName("android.widget.RadioButton");
            info.setCheckable(true);
            info.setChecked(this.radioButton.isChecked());
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

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.onUserRingtonesUpdated) {
            HashMap<Integer, Tone> currentTones = new HashMap<>();
            for (int i = 0; i < this.serverTones.size(); i++) {
                currentTones.put(Integer.valueOf(this.serverTones.get(i).localId), this.serverTones.get(i));
            }
            this.serverTones.clear();
            for (int i2 = 0; i2 < getMediaDataController().ringtoneDataStore.userRingtones.size(); i2++) {
                RingtoneDataStore.CachedTone cachedTone = getMediaDataController().ringtoneDataStore.userRingtones.get(i2);
                Tone tone = new Tone();
                Tone currentTone = currentTones.get(Integer.valueOf(cachedTone.localId));
                if (currentTone != null) {
                    if (currentTone == this.selectedTone) {
                        this.selectedTone = tone;
                    }
                    tone.stableId = currentTone.stableId;
                } else {
                    int i3 = this.stableIds;
                    this.stableIds = i3 + 1;
                    tone.stableId = i3;
                }
                tone.fromServer = true;
                tone.localId = cachedTone.localId;
                if (cachedTone.document != null) {
                    tone.title = cachedTone.document.file_name_fixed;
                } else {
                    tone.title = new File(cachedTone.localUri).getName();
                }
                tone.document = cachedTone.document;
                trimTitle(tone);
                tone.uri = cachedTone.localUri;
                Tone tone2 = this.startSelectedTone;
                if (!(tone2 == null || tone2.document == null || cachedTone.document == null || this.startSelectedTone.document.id != cachedTone.document.id)) {
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

    public static String trimTitle(TLRPC.Document document, String title) {
        int idx;
        if (!(title == null || (idx = title.lastIndexOf(46)) == -1)) {
            title = title.substring(0, idx);
        }
        if (TextUtils.isEmpty(title) == 0 || document == null) {
            return title;
        }
        return LocaleController.formatString("SoundNameEmpty", NUM, LocaleController.formatDateChat((long) document.date, true));
    }

    public void onFragmentDestroy() {
        String prefDocId;
        String prefPath;
        String prefName;
        super.onFragmentDestroy();
        if (this.selectedTone != null && this.selectedToneChanged) {
            SharedPreferences.Editor editor = getNotificationsSettings().edit();
            if (this.dialogId != 0) {
                prefName = "sound_" + this.dialogId;
                prefPath = "sound_path_" + this.dialogId;
                prefDocId = "sound_document_id_" + this.dialogId;
                editor.putBoolean("sound_enabled_" + this.dialogId, true);
            } else {
                int i = this.currentType;
                if (i == 1) {
                    prefName = "GlobalSound";
                    prefPath = "GlobalSoundPath";
                    prefDocId = "GlobalSoundDocId";
                } else if (i == 0) {
                    prefName = "GroupSound";
                    prefPath = "GroupSoundPath";
                    prefDocId = "GroupSoundDocId";
                } else if (i == 2) {
                    prefName = "ChannelSound";
                    prefPath = "ChannelSoundPath";
                    prefDocId = "ChannelSoundDocId";
                } else {
                    throw new RuntimeException("Unsupported type");
                }
            }
            if (this.selectedTone.fromServer && this.selectedTone.document != null) {
                editor.putLong(prefDocId, this.selectedTone.document.id);
                editor.putString(prefName, this.selectedTone.title);
                editor.putString(prefPath, "NoSound");
            } else if (this.selectedTone.uri != null) {
                editor.putString(prefName, this.selectedTone.title);
                editor.putString(prefPath, this.selectedTone.uri);
                editor.remove(prefDocId);
            } else if (this.selectedTone.isSystemDefault) {
                editor.putString(prefName, "Default");
                editor.putString(prefPath, "Default");
                editor.remove(prefDocId);
            } else {
                editor.putString(prefName, "NoSound");
                editor.putString(prefPath, "NoSound");
                editor.remove(prefDocId);
            }
            editor.apply();
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
            Intent photoPickerIntent = new Intent("android.intent.action.GET_CONTENT");
            if (Build.VERSION.SDK_INT >= 18) {
                photoPickerIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            }
            photoPickerIntent.setType("audio/mpeg");
            startActivityForResult(photoPickerIntent, 21);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    public void onActivityResultFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == 21 && data != null && this.chatAttachAlert != null) {
            boolean apply = false;
            if (data.getData() != null) {
                String path = AndroidUtilities.getPath(data.getData());
                if (path != null) {
                    if (this.chatAttachAlert.getDocumentLayout().isRingtone(new File(path))) {
                        apply = true;
                        getMediaDataController().uploadRingtone(path);
                        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
                    }
                }
            } else if (data.getClipData() != null) {
                ClipData clipData = data.getClipData();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    String path2 = clipData.getItemAt(i).getUri().toString();
                    if (this.chatAttachAlert.getDocumentLayout().isRingtone(new File(path2))) {
                        apply = true;
                        getMediaDataController().uploadRingtone(path2);
                        getNotificationCenter().postNotificationName(NotificationCenter.onUserRingtonesUpdated, new Object[0]);
                    }
                }
            }
            if (apply) {
                this.chatAttachAlert.dismiss();
            }
        }
    }

    private static class Tone {
        TLRPC.Document document;
        public boolean fromServer;
        boolean isSystemDefault;
        boolean isSystemNoSound;
        int localId;
        int stableId;
        String title;
        String uri;

        private Tone() {
        }

        public Uri getUriForShare(int currentAccount) {
            if (!TextUtils.isEmpty(this.uri)) {
                return Uri.fromFile(new File(this.uri));
            }
            TLRPC.Document document2 = this.document;
            if (document2 == null) {
                return null;
            }
            String fileName = document2.file_name_fixed;
            String ext = FileLoader.getDocumentExtension(this.document);
            if (ext == null) {
                return null;
            }
            String ext2 = ext.toLowerCase();
            if (!fileName.endsWith(ext2)) {
                fileName = fileName + "." + ext2;
            }
            File file = new File(AndroidUtilities.getCacheDir(), fileName);
            if (!file.exists()) {
                try {
                    AndroidUtilities.copyFile(FileLoader.getPathToAttach(this.document), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return Uri.fromFile(file);
        }
    }
}

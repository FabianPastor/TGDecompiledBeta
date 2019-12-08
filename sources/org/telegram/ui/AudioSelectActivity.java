package org.telegram.ui;

import android.content.Context;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AudioCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class AudioSelectActivity extends BaseFragment implements NotificationCenterDelegate {
    private ArrayList<AudioEntry> audioEntries = new ArrayList();
    private PickerBottomLayout bottomLayout;
    private AudioSelectActivityDelegate delegate;
    private RecyclerListView listView;
    private ListAdapter listViewAdapter;
    private boolean loadingAudio;
    private ChatActivity parentFragment;
    private MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    private LongSparseArray<AudioEntry> selectedAudios = new LongSparseArray();
    private View shadow;

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList, boolean z, int i);
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        public long getItemId(int i) {
            return (long) i;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public boolean isEnabled(ViewHolder viewHolder) {
            return true;
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public int getItemCount() {
            return AudioSelectActivity.this.audioEntries.size();
        }

        public Object getItem(int i) {
            return AudioSelectActivity.this.audioEntries.get(i);
        }

        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AudioCell audioCell = new AudioCell(this.mContext);
            audioCell.setDelegate(new -$$Lambda$AudioSelectActivity$ListAdapter$_RHioXAj8YlHkv8lhkCbfXzL_JQ(this));
            return new Holder(audioCell);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$0$AudioSelectActivity$ListAdapter(MessageObject messageObject) {
            AudioSelectActivity.this.playingAudio = messageObject;
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            AudioEntry audioEntry = (AudioEntry) AudioSelectActivity.this.audioEntries.get(i);
            AudioCell audioCell = (AudioCell) viewHolder.itemView;
            AudioEntry audioEntry2 = (AudioEntry) AudioSelectActivity.this.audioEntries.get(i);
            boolean z = true;
            boolean z2 = i != AudioSelectActivity.this.audioEntries.size() - 1;
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            audioCell.setAudio(audioEntry2, z2, z);
        }
    }

    public AudioSelectActivity(ChatActivity chatActivity) {
        this.parentFragment = chatActivity;
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        loadAudio();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(NUM);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", NUM));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int i) {
                if (i == -1) {
                    AudioSelectActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.progressView = new EmptyTextProgressView(context);
        this.progressView.setText(LocaleController.getString("NoAudio", NUM));
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.progressView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.listView.setOnItemClickListener(new -$$Lambda$AudioSelectActivity$t7_oOYYhOZXWc6t1x4gHrcrcMM0(this));
        this.bottomLayout = new PickerBottomLayout(context, false);
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.cancelButton.setOnClickListener(new -$$Lambda$AudioSelectActivity$1kNFEVrMBVP4AVP1qEJ7AqtPUlc(this));
        this.bottomLayout.doneButton.setOnClickListener(new -$$Lambda$AudioSelectActivity$oWvKPuPWepjuhHnmQ9yUDjF8FzU(this));
        View view = new View(context);
        view.setBackgroundResource(NUM);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.loadingAudio) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        updateBottomLayoutCount();
        return this.fragmentView;
    }

    public /* synthetic */ void lambda$createView$0$AudioSelectActivity(View view, int i) {
        AudioCell audioCell = (AudioCell) view;
        AudioEntry audioEntry = audioCell.getAudioEntry();
        if (this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
            this.selectedAudios.remove(audioEntry.id);
            audioCell.setChecked(false);
        } else {
            this.selectedAudios.put(audioEntry.id, audioEntry);
            audioCell.setChecked(true);
        }
        updateBottomLayoutCount();
    }

    public /* synthetic */ void lambda$createView$1$AudioSelectActivity(View view) {
        finishFragment();
    }

    public /* synthetic */ void lambda$createView$3$AudioSelectActivity(View view) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.selectedAudios.size(); i++) {
            arrayList.add(((AudioEntry) this.selectedAudios.valueAt(i)).messageObject);
        }
        if (this.parentFragment.isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), UserObject.isUserSelf(this.parentFragment.getCurrentUser()), new -$$Lambda$AudioSelectActivity$BBBkUTfAdpAYMe4sCnxgHzaluKQ(this, arrayList));
            return;
        }
        this.delegate.didSelectAudio(arrayList, true, 0);
        finishFragment();
    }

    public /* synthetic */ void lambda$null$2$AudioSelectActivity(ArrayList arrayList, boolean z, int i) {
        this.delegate.didSelectAudio(arrayList, z, i);
        finishFragment();
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged) {
            ListAdapter listAdapter = this.listViewAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    private void updateBottomLayoutCount() {
        this.bottomLayout.updateSelectedCount(this.selectedAudios.size(), true);
    }

    public void setDelegate(AudioSelectActivityDelegate audioSelectActivityDelegate) {
        this.delegate = audioSelectActivityDelegate;
    }

    private void loadAudio() {
        this.loadingAudio = true;
        EmptyTextProgressView emptyTextProgressView = this.progressView;
        if (emptyTextProgressView != null) {
            emptyTextProgressView.showProgress();
        }
        Utilities.globalQueue.postRunnable(new -$$Lambda$AudioSelectActivity$VxxX_DXPsxIauWOOf7lDE_MpJyA(this));
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:24:0x016a */
    /* JADX WARNING: Missing block: B:19:0x0163, code skipped:
            r0 = move-exception;
     */
    /* JADX WARNING: Missing block: B:20:0x0164, code skipped:
            r3 = r0;
     */
    /* JADX WARNING: Missing block: B:21:0x0165, code skipped:
            if (r2 != null) goto L_0x0167;
     */
    /* JADX WARNING: Missing block: B:23:?, code skipped:
            r2.close();
     */
    public /* synthetic */ void lambda$loadAudio$5$AudioSelectActivity() {
        /*
        r16 = this;
        r1 = r16;
        r0 = 6;
        r4 = new java.lang.String[r0];
        r0 = 0;
        r2 = "_id";
        r4[r0] = r2;
        r8 = 1;
        r2 = "artist";
        r4[r8] = r2;
        r9 = 2;
        r2 = "title";
        r4[r9] = r2;
        r10 = 3;
        r2 = "_data";
        r4[r10] = r2;
        r11 = 4;
        r2 = "duration";
        r4[r11] = r2;
        r12 = 5;
        r2 = "album";
        r4[r12] = r2;
        r13 = new java.util.ArrayList;
        r13.<init>();
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x016b }
        r2 = r2.getContentResolver();	 Catch:{ Exception -> 0x016b }
        r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x016b }
        r5 = "is_music != 0";
        r6 = 0;
        r7 = "title";
        r2 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x016b }
        r3 = -NUM; // 0xfffffffvar_ca6CLASSNAME float:-1.2182823E-33 double:NaN;
    L_0x003c:
        r4 = r2.moveToNext();	 Catch:{ all -> 0x0161 }
        if (r4 == 0) goto L_0x015b;
    L_0x0042:
        r4 = new org.telegram.messenger.MediaController$AudioEntry;	 Catch:{ all -> 0x0161 }
        r4.<init>();	 Catch:{ all -> 0x0161 }
        r5 = r2.getInt(r0);	 Catch:{ all -> 0x0161 }
        r5 = (long) r5;	 Catch:{ all -> 0x0161 }
        r4.id = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r8);	 Catch:{ all -> 0x0161 }
        r4.author = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r9);	 Catch:{ all -> 0x0161 }
        r4.title = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r10);	 Catch:{ all -> 0x0161 }
        r4.path = r5;	 Catch:{ all -> 0x0161 }
        r5 = r2.getLong(r11);	 Catch:{ all -> 0x0161 }
        r14 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;
        r5 = r5 / r14;
        r6 = (int) r5;	 Catch:{ all -> 0x0161 }
        r4.duration = r6;	 Catch:{ all -> 0x0161 }
        r5 = r2.getString(r12);	 Catch:{ all -> 0x0161 }
        r4.genre = r5;	 Catch:{ all -> 0x0161 }
        r5 = new java.io.File;	 Catch:{ all -> 0x0161 }
        r6 = r4.path;	 Catch:{ all -> 0x0161 }
        r5.<init>(r6);	 Catch:{ all -> 0x0161 }
        r6 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ all -> 0x0161 }
        r6.<init>();	 Catch:{ all -> 0x0161 }
        r6.out = r8;	 Catch:{ all -> 0x0161 }
        r6.id = r3;	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r6.to_id = r7;	 Catch:{ all -> 0x0161 }
        r7 = r6.to_id;	 Catch:{ all -> 0x0161 }
        r8 = r1.currentAccount;	 Catch:{ all -> 0x0161 }
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);	 Catch:{ all -> 0x0161 }
        r8 = r8.getClientUserId();	 Catch:{ all -> 0x0161 }
        r6.from_id = r8;	 Catch:{ all -> 0x0161 }
        r7.user_id = r8;	 Catch:{ all -> 0x0161 }
        r7 = java.lang.System.currentTimeMillis();	 Catch:{ all -> 0x0161 }
        r7 = r7 / r14;
        r8 = (int) r7;	 Catch:{ all -> 0x0161 }
        r6.date = r8;	 Catch:{ all -> 0x0161 }
        r7 = "";
        r6.message = r7;	 Catch:{ all -> 0x0161 }
        r7 = r4.path;	 Catch:{ all -> 0x0161 }
        r6.attachPath = r7;	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r6.media = r7;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r7.flags;	 Catch:{ all -> 0x0161 }
        r8 = r8 | r10;
        r7.flags = r8;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = new org.telegram.tgnet.TLRPC$TL_document;	 Catch:{ all -> 0x0161 }
        r8.<init>();	 Catch:{ all -> 0x0161 }
        r7.document = r8;	 Catch:{ all -> 0x0161 }
        r7 = r6.flags;	 Catch:{ all -> 0x0161 }
        r7 = r7 | 768;
        r6.flags = r7;	 Catch:{ all -> 0x0161 }
        r7 = org.telegram.messenger.FileLoader.getFileExtension(r5);	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = 0;
        r8.id = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r8.access_hash = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = new byte[r0];	 Catch:{ all -> 0x0161 }
        r8.file_reference = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = r6.date;	 Catch:{ all -> 0x0161 }
        r8.date = r14;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r14 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0161 }
        r14.<init>();	 Catch:{ all -> 0x0161 }
        r15 = "audio/";
        r14.append(r15);	 Catch:{ all -> 0x0161 }
        r15 = r7.length();	 Catch:{ all -> 0x0161 }
        if (r15 <= 0) goto L_0x00fb;
    L_0x00fa:
        goto L_0x00fd;
    L_0x00fb:
        r7 = "mp3";
    L_0x00fd:
        r14.append(r7);	 Catch:{ all -> 0x0161 }
        r7 = r14.toString();	 Catch:{ all -> 0x0161 }
        r8.mime_type = r7;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r7 = r7.document;	 Catch:{ all -> 0x0161 }
        r14 = r5.length();	 Catch:{ all -> 0x0161 }
        r8 = (int) r14;	 Catch:{ all -> 0x0161 }
        r7.size = r8;	 Catch:{ all -> 0x0161 }
        r7 = r6.media;	 Catch:{ all -> 0x0161 }
        r7 = r7.document;	 Catch:{ all -> 0x0161 }
        r7.dc_id = r0;	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r8 = r4.duration;	 Catch:{ all -> 0x0161 }
        r7.duration = r8;	 Catch:{ all -> 0x0161 }
        r8 = r4.title;	 Catch:{ all -> 0x0161 }
        r7.title = r8;	 Catch:{ all -> 0x0161 }
        r8 = r4.author;	 Catch:{ all -> 0x0161 }
        r7.performer = r8;	 Catch:{ all -> 0x0161 }
        r8 = r7.flags;	 Catch:{ all -> 0x0161 }
        r8 = r8 | r10;
        r7.flags = r8;	 Catch:{ all -> 0x0161 }
        r8 = r6.media;	 Catch:{ all -> 0x0161 }
        r8 = r8.document;	 Catch:{ all -> 0x0161 }
        r8 = r8.attributes;	 Catch:{ all -> 0x0161 }
        r8.add(r7);	 Catch:{ all -> 0x0161 }
        r7 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;	 Catch:{ all -> 0x0161 }
        r7.<init>();	 Catch:{ all -> 0x0161 }
        r5 = r5.getName();	 Catch:{ all -> 0x0161 }
        r7.file_name = r5;	 Catch:{ all -> 0x0161 }
        r5 = r6.media;	 Catch:{ all -> 0x0161 }
        r5 = r5.document;	 Catch:{ all -> 0x0161 }
        r5 = r5.attributes;	 Catch:{ all -> 0x0161 }
        r5.add(r7);	 Catch:{ all -> 0x0161 }
        r5 = new org.telegram.messenger.MessageObject;	 Catch:{ all -> 0x0161 }
        r7 = r1.currentAccount;	 Catch:{ all -> 0x0161 }
        r5.<init>(r7, r6, r0);	 Catch:{ all -> 0x0161 }
        r4.messageObject = r5;	 Catch:{ all -> 0x0161 }
        r13.add(r4);	 Catch:{ all -> 0x0161 }
        r3 = r3 + -1;
        r8 = 1;
        goto L_0x003c;
    L_0x015b:
        if (r2 == 0) goto L_0x016f;
    L_0x015d:
        r2.close();	 Catch:{ Exception -> 0x016b }
        goto L_0x016f;
    L_0x0161:
        r0 = move-exception;
        throw r0;	 Catch:{ all -> 0x0163 }
    L_0x0163:
        r0 = move-exception;
        r3 = r0;
        if (r2 == 0) goto L_0x016a;
    L_0x0167:
        r2.close();	 Catch:{ all -> 0x016a }
    L_0x016a:
        throw r3;	 Catch:{ Exception -> 0x016b }
    L_0x016b:
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);
    L_0x016f:
        r0 = new org.telegram.ui.-$$Lambda$AudioSelectActivity$fjXfeVS7_RmKKYSkd23PAHoDQRM;
        r0.<init>(r1, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AudioSelectActivity.lambda$loadAudio$5$AudioSelectActivity():void");
    }

    public /* synthetic */ void lambda$null$4$AudioSelectActivity(ArrayList arrayList) {
        this.audioEntries = arrayList;
        this.progressView.showTextView();
        this.listViewAdapter.notifyDataSetChanged();
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[24];
        r1[7] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, "divider");
        r1[8] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "emptyListPlaceholder");
        r1[9] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "progressCircle");
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"titleTextView"}, null, null, null, "windowBackgroundWhiteBlackText");
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"genreTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"authorTextView"}, null, null, null, "windowBackgroundWhiteGrayText2");
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"timeTextView"}, null, null, null, "windowBackgroundWhiteGrayText3");
        View view = this.listView;
        int i = ThemeDescription.FLAG_CHECKBOX;
        Class[] clsArr = new Class[]{AudioCell.class};
        String[] strArr = new String[1];
        strArr[0] = "checkBox";
        r1[14] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "musicPicker_checkbox");
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{AudioCell.class}, new String[]{"checkBox"}, null, null, null, "musicPicker_checkboxCheck");
        view = this.listView;
        i = ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE;
        clsArr = new Class[]{AudioCell.class};
        strArr = new String[1];
        strArr[0] = "playButton";
        r1[16] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "musicPicker_buttonIcon");
        r1[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{AudioCell.class}, new String[]{"playButton"}, null, null, null, "musicPicker_buttonBackground");
        r1[18] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "windowBackgroundWhite");
        r1[19] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PickerBottomLayout.class}, new String[]{"cancelButton"}, null, null, null, "picker_enabledButton");
        view = this.bottomLayout;
        i = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        clsArr = new Class[]{PickerBottomLayout.class};
        strArr = new String[1];
        strArr[0] = "doneButtonTextView";
        r1[20] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "picker_enabledButton");
        r1[21] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonTextView"}, null, null, null, "picker_disabledButton");
        view = this.bottomLayout;
        i = ThemeDescription.FLAG_TEXTCOLOR;
        clsArr = new Class[]{PickerBottomLayout.class};
        strArr = new String[1];
        strArr[0] = "doneButtonBadgeTextView";
        r1[22] = new ThemeDescription(view, i, clsArr, strArr, null, null, null, "picker_badgeText");
        r1[23] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonBadgeTextView"}, null, null, null, "picker_badge");
        return r1;
    }
}

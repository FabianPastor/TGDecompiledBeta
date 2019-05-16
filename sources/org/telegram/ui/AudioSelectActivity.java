package org.telegram.ui;

import android.content.Context;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AudioCell;
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
    private MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    private LongSparseArray<AudioEntry> selectedAudios = new LongSparseArray();
    private View shadow;

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList);
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

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:25:0x017c in {7, 8, 10, 12, 18, 19, 21, 23, 24} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public /* synthetic */ void lambda$loadAudio$4$AudioSelectActivity() {
        /*
        r18 = this;
        r1 = r18;
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
        r14 = 0;
        r2 = org.telegram.messenger.ApplicationLoader.applicationContext;	 Catch:{ Exception -> 0x0164 }
        r2 = r2.getContentResolver();	 Catch:{ Exception -> 0x0164 }
        r3 = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;	 Catch:{ Exception -> 0x0164 }
        r5 = "is_music != 0";	 Catch:{ Exception -> 0x0164 }
        r6 = 0;	 Catch:{ Exception -> 0x0164 }
        r7 = "title";	 Catch:{ Exception -> 0x0164 }
        r14 = r2.query(r3, r4, r5, r6, r7);	 Catch:{ Exception -> 0x0164 }
        r2 = -NUM; // 0xfffffffvar_ca6CLASSNAME float:-1.2182823E-33 double:NaN;	 Catch:{ Exception -> 0x0164 }
        r3 = r14.moveToNext();	 Catch:{ Exception -> 0x0164 }
        if (r3 == 0) goto L_0x015f;	 Catch:{ Exception -> 0x0164 }
        r3 = new org.telegram.messenger.MediaController$AudioEntry;	 Catch:{ Exception -> 0x0164 }
        r3.<init>();	 Catch:{ Exception -> 0x0164 }
        r4 = r14.getInt(r0);	 Catch:{ Exception -> 0x0164 }
        r4 = (long) r4;	 Catch:{ Exception -> 0x0164 }
        r3.id = r4;	 Catch:{ Exception -> 0x0164 }
        r4 = r14.getString(r8);	 Catch:{ Exception -> 0x0164 }
        r3.author = r4;	 Catch:{ Exception -> 0x0164 }
        r4 = r14.getString(r9);	 Catch:{ Exception -> 0x0164 }
        r3.title = r4;	 Catch:{ Exception -> 0x0164 }
        r4 = r14.getString(r10);	 Catch:{ Exception -> 0x0164 }
        r3.path = r4;	 Catch:{ Exception -> 0x0164 }
        r4 = r14.getLong(r11);	 Catch:{ Exception -> 0x0164 }
        r6 = 1000; // 0x3e8 float:1.401E-42 double:4.94E-321;	 Catch:{ Exception -> 0x0164 }
        r4 = r4 / r6;	 Catch:{ Exception -> 0x0164 }
        r5 = (int) r4;	 Catch:{ Exception -> 0x0164 }
        r3.duration = r5;	 Catch:{ Exception -> 0x0164 }
        r4 = r14.getString(r12);	 Catch:{ Exception -> 0x0164 }
        r3.genre = r4;	 Catch:{ Exception -> 0x0164 }
        r4 = new java.io.File;	 Catch:{ Exception -> 0x0164 }
        r5 = r3.path;	 Catch:{ Exception -> 0x0164 }
        r4.<init>(r5);	 Catch:{ Exception -> 0x0164 }
        r5 = new org.telegram.tgnet.TLRPC$TL_message;	 Catch:{ Exception -> 0x0164 }
        r5.<init>();	 Catch:{ Exception -> 0x0164 }
        r5.out = r8;	 Catch:{ Exception -> 0x0164 }
        r5.id = r2;	 Catch:{ Exception -> 0x0164 }
        r15 = new org.telegram.tgnet.TLRPC$TL_peerUser;	 Catch:{ Exception -> 0x0164 }
        r15.<init>();	 Catch:{ Exception -> 0x0164 }
        r5.to_id = r15;	 Catch:{ Exception -> 0x0164 }
        r15 = r5.to_id;	 Catch:{ Exception -> 0x0164 }
        r8 = r1.currentAccount;	 Catch:{ Exception -> 0x0164 }
        r8 = org.telegram.messenger.UserConfig.getInstance(r8);	 Catch:{ Exception -> 0x0164 }
        r8 = r8.getClientUserId();	 Catch:{ Exception -> 0x0164 }
        r5.from_id = r8;	 Catch:{ Exception -> 0x0164 }
        r15.user_id = r8;	 Catch:{ Exception -> 0x0164 }
        r16 = java.lang.System.currentTimeMillis();	 Catch:{ Exception -> 0x0164 }
        r6 = r16 / r6;	 Catch:{ Exception -> 0x0164 }
        r7 = (int) r6;	 Catch:{ Exception -> 0x0164 }
        r5.date = r7;	 Catch:{ Exception -> 0x0164 }
        r6 = "";	 Catch:{ Exception -> 0x0164 }
        r5.message = r6;	 Catch:{ Exception -> 0x0164 }
        r6 = r3.path;	 Catch:{ Exception -> 0x0164 }
        r5.attachPath = r6;	 Catch:{ Exception -> 0x0164 }
        r6 = new org.telegram.tgnet.TLRPC$TL_messageMediaDocument;	 Catch:{ Exception -> 0x0164 }
        r6.<init>();	 Catch:{ Exception -> 0x0164 }
        r5.media = r6;	 Catch:{ Exception -> 0x0164 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = r6.flags;	 Catch:{ Exception -> 0x0164 }
        r7 = r7 | r10;	 Catch:{ Exception -> 0x0164 }
        r6.flags = r7;	 Catch:{ Exception -> 0x0164 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = new org.telegram.tgnet.TLRPC$TL_document;	 Catch:{ Exception -> 0x0164 }
        r7.<init>();	 Catch:{ Exception -> 0x0164 }
        r6.document = r7;	 Catch:{ Exception -> 0x0164 }
        r6 = r5.flags;	 Catch:{ Exception -> 0x0164 }
        r6 = r6 | 768;	 Catch:{ Exception -> 0x0164 }
        r5.flags = r6;	 Catch:{ Exception -> 0x0164 }
        r6 = org.telegram.messenger.FileLoader.getFileExtension(r4);	 Catch:{ Exception -> 0x0164 }
        r7 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = r7.document;	 Catch:{ Exception -> 0x0164 }
        r11 = 0;	 Catch:{ Exception -> 0x0164 }
        r7.id = r11;	 Catch:{ Exception -> 0x0164 }
        r7 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = r7.document;	 Catch:{ Exception -> 0x0164 }
        r7.access_hash = r11;	 Catch:{ Exception -> 0x0164 }
        r7 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = r7.document;	 Catch:{ Exception -> 0x0164 }
        r11 = new byte[r0];	 Catch:{ Exception -> 0x0164 }
        r7.file_reference = r11;	 Catch:{ Exception -> 0x0164 }
        r7 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = r7.document;	 Catch:{ Exception -> 0x0164 }
        r11 = r5.date;	 Catch:{ Exception -> 0x0164 }
        r7.date = r11;	 Catch:{ Exception -> 0x0164 }
        r7 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = r7.document;	 Catch:{ Exception -> 0x0164 }
        r11 = new java.lang.StringBuilder;	 Catch:{ Exception -> 0x0164 }
        r11.<init>();	 Catch:{ Exception -> 0x0164 }
        r12 = "audio/";	 Catch:{ Exception -> 0x0164 }
        r11.append(r12);	 Catch:{ Exception -> 0x0164 }
        r12 = r6.length();	 Catch:{ Exception -> 0x0164 }
        if (r12 <= 0) goto L_0x00fd;	 Catch:{ Exception -> 0x0164 }
        goto L_0x00ff;	 Catch:{ Exception -> 0x0164 }
        r6 = "mp3";	 Catch:{ Exception -> 0x0164 }
        r11.append(r6);	 Catch:{ Exception -> 0x0164 }
        r6 = r11.toString();	 Catch:{ Exception -> 0x0164 }
        r7.mime_type = r6;	 Catch:{ Exception -> 0x0164 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r6 = r6.document;	 Catch:{ Exception -> 0x0164 }
        r11 = r4.length();	 Catch:{ Exception -> 0x0164 }
        r7 = (int) r11;	 Catch:{ Exception -> 0x0164 }
        r6.size = r7;	 Catch:{ Exception -> 0x0164 }
        r6 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r6 = r6.document;	 Catch:{ Exception -> 0x0164 }
        r6.dc_id = r0;	 Catch:{ Exception -> 0x0164 }
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeAudio;	 Catch:{ Exception -> 0x0164 }
        r6.<init>();	 Catch:{ Exception -> 0x0164 }
        r7 = r3.duration;	 Catch:{ Exception -> 0x0164 }
        r6.duration = r7;	 Catch:{ Exception -> 0x0164 }
        r7 = r3.title;	 Catch:{ Exception -> 0x0164 }
        r6.title = r7;	 Catch:{ Exception -> 0x0164 }
        r7 = r3.author;	 Catch:{ Exception -> 0x0164 }
        r6.performer = r7;	 Catch:{ Exception -> 0x0164 }
        r7 = r6.flags;	 Catch:{ Exception -> 0x0164 }
        r7 = r7 | r10;	 Catch:{ Exception -> 0x0164 }
        r6.flags = r7;	 Catch:{ Exception -> 0x0164 }
        r7 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r7 = r7.document;	 Catch:{ Exception -> 0x0164 }
        r7 = r7.attributes;	 Catch:{ Exception -> 0x0164 }
        r7.add(r6);	 Catch:{ Exception -> 0x0164 }
        r6 = new org.telegram.tgnet.TLRPC$TL_documentAttributeFilename;	 Catch:{ Exception -> 0x0164 }
        r6.<init>();	 Catch:{ Exception -> 0x0164 }
        r4 = r4.getName();	 Catch:{ Exception -> 0x0164 }
        r6.file_name = r4;	 Catch:{ Exception -> 0x0164 }
        r4 = r5.media;	 Catch:{ Exception -> 0x0164 }
        r4 = r4.document;	 Catch:{ Exception -> 0x0164 }
        r4 = r4.attributes;	 Catch:{ Exception -> 0x0164 }
        r4.add(r6);	 Catch:{ Exception -> 0x0164 }
        r4 = new org.telegram.messenger.MessageObject;	 Catch:{ Exception -> 0x0164 }
        r6 = r1.currentAccount;	 Catch:{ Exception -> 0x0164 }
        r4.<init>(r6, r5, r0);	 Catch:{ Exception -> 0x0164 }
        r3.messageObject = r4;	 Catch:{ Exception -> 0x0164 }
        r13.add(r3);	 Catch:{ Exception -> 0x0164 }
        r2 = r2 + -1;
        r8 = 1;
        r11 = 4;
        r12 = 5;
        goto L_0x003d;
        if (r14 == 0) goto L_0x016d;
        goto L_0x016a;
        r0 = move-exception;
        goto L_0x0176;
        r0 = move-exception;
        org.telegram.messenger.FileLog.e(r0);	 Catch:{ all -> 0x0162 }
        if (r14 == 0) goto L_0x016d;
        r14.close();
        r0 = new org.telegram.ui.-$$Lambda$AudioSelectActivity$QzekR4OrJV2j13f_JDzwyvvM314;
        r0.<init>(r1, r13);
        org.telegram.messenger.AndroidUtilities.runOnUIThread(r0);
        return;
        if (r14 == 0) goto L_0x017b;
        r14.close();
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.AudioSelectActivity.lambda$loadAudio$4$AudioSelectActivity():void");
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
        this.bottomLayout.doneButton.setOnClickListener(new -$$Lambda$AudioSelectActivity$B7w290PyNFItb-f7pzlG0yYvSco(this));
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

    public /* synthetic */ void lambda$createView$2$AudioSelectActivity(View view) {
        if (this.delegate != null) {
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < this.selectedAudios.size(); i++) {
                arrayList.add(((AudioEntry) this.selectedAudios.valueAt(i)).messageObject);
            }
            this.delegate.didSelectAudio(arrayList);
        }
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
        Utilities.globalQueue.postRunnable(new -$$Lambda$AudioSelectActivity$GEv7u1xk_gK3KYDecS-JYS1z0Do(this));
    }

    public /* synthetic */ void lambda$null$3$AudioSelectActivity(ArrayList arrayList) {
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

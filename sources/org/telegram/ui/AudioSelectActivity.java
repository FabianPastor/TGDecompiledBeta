package org.telegram.ui;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
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

    public /* synthetic */ void lambda$loadAudio$4$AudioSelectActivity() {
        Cursor query;
        Throwable th;
        Throwable th2;
        Throwable th3;
        r4 = new String[6];
        int i = 1;
        r4[1] = "artist";
        r4[2] = "title";
        r4[3] = "_data";
        r4[4] = "duration";
        r4[5] = "album";
        ArrayList arrayList = new ArrayList();
        try {
            query = ApplicationLoader.applicationContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, r4, "is_music != 0", null, "title");
            int i2 = -NUM;
            while (query.moveToNext()) {
                try {
                    AudioEntry audioEntry = new AudioEntry();
                    audioEntry.id = (long) query.getInt(0);
                    audioEntry.author = query.getString(i);
                    audioEntry.title = query.getString(2);
                    audioEntry.path = query.getString(3);
                    audioEntry.duration = (int) (query.getLong(4) / 1000);
                    audioEntry.genre = query.getString(5);
                    File file = new File(audioEntry.path);
                    TL_message tL_message = new TL_message();
                    tL_message.out = i;
                    tL_message.id = i2;
                    tL_message.to_id = new TL_peerUser();
                    Peer peer = tL_message.to_id;
                    i = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    tL_message.from_id = i;
                    peer.user_id = i;
                    tL_message.date = (int) (System.currentTimeMillis() / 1000);
                    tL_message.message = "";
                    tL_message.attachPath = audioEntry.path;
                    tL_message.media = new TL_messageMediaDocument();
                    MessageMedia messageMedia = tL_message.media;
                    messageMedia.flags |= 3;
                    tL_message.media.document = new TL_document();
                    tL_message.flags |= 768;
                    String fileExtension = FileLoader.getFileExtension(file);
                    tL_message.media.document.id = 0;
                    tL_message.media.document.access_hash = 0;
                    tL_message.media.document.file_reference = new byte[0];
                    tL_message.media.document.date = tL_message.date;
                    Document document = tL_message.media.document;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("audio/");
                    if (fileExtension.length() <= 0) {
                        fileExtension = "mp3";
                    }
                    stringBuilder.append(fileExtension);
                    document.mime_type = stringBuilder.toString();
                    tL_message.media.document.size = (int) file.length();
                    tL_message.media.document.dc_id = 0;
                    TL_documentAttributeAudio tL_documentAttributeAudio = new TL_documentAttributeAudio();
                    tL_documentAttributeAudio.duration = audioEntry.duration;
                    tL_documentAttributeAudio.title = audioEntry.title;
                    tL_documentAttributeAudio.performer = audioEntry.author;
                    tL_documentAttributeAudio.flags |= 3;
                    tL_message.media.document.attributes.add(tL_documentAttributeAudio);
                    TL_documentAttributeFilename tL_documentAttributeFilename = new TL_documentAttributeFilename();
                    tL_documentAttributeFilename.file_name = file.getName();
                    tL_message.media.document.attributes.add(tL_documentAttributeFilename);
                    audioEntry.messageObject = new MessageObject(this.currentAccount, tL_message, false);
                    arrayList.add(audioEntry);
                    i2--;
                    i = 1;
                } catch (Throwable th4) {
                    th2 = th4;
                    th3 = th;
                }
            }
            if (query != null) {
                query.close();
            }
        } catch (Exception th22) {
            FileLog.e(th22);
        }
        AndroidUtilities.runOnUIThread(new -$$Lambda$AudioSelectActivity$QzekR4OrJV2j13f_JDzwyvvM314(this, arrayList));
        return;
        throw th22;
        if (query != null) {
            if (th3 != null) {
                try {
                    query.close();
                } catch (Throwable unused) {
                }
            } else {
                query.close();
            }
        }
        throw th22;
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

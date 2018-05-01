package org.telegram.ui;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.util.LongSparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import java.io.File;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
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
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_document;
import org.telegram.tgnet.TLRPC.TL_documentAttributeAudio;
import org.telegram.tgnet.TLRPC.TL_documentAttributeFilename;
import org.telegram.tgnet.TLRPC.TL_message;
import org.telegram.tgnet.TLRPC.TL_messageMediaDocument;
import org.telegram.tgnet.TLRPC.TL_peerUser;
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AudioCell;
import org.telegram.ui.Cells.AudioCell.AudioCellDelegate;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
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

    /* renamed from: org.telegram.ui.AudioSelectActivity$3 */
    class C08343 implements OnClickListener {
        C08343() {
        }

        public void onClick(View view) {
            AudioSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$4 */
    class C08354 implements OnClickListener {
        C08354() {
        }

        public void onClick(View view) {
            if (AudioSelectActivity.this.delegate != null) {
                view = new ArrayList();
                for (int i = 0; i < AudioSelectActivity.this.selectedAudios.size(); i++) {
                    view.add(((AudioEntry) AudioSelectActivity.this.selectedAudios.valueAt(i)).messageObject);
                }
                AudioSelectActivity.this.delegate.didSelectAudio(view);
            }
            AudioSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$5 */
    class C08375 implements Runnable {
        C08375() {
        }

        public void run() {
            Throwable e;
            Throwable th;
            C08375 c08375 = this;
            String[] strArr = new String[6];
            strArr[0] = "_id";
            boolean z = true;
            strArr[1] = "artist";
            strArr[2] = "title";
            strArr[3] = "_data";
            int i = 4;
            strArr[4] = "duration";
            int i2 = 5;
            strArr[5] = "album";
            final ArrayList arrayList = new ArrayList();
            Cursor cursor = null;
            Cursor query;
            try {
                query = ApplicationLoader.applicationContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, strArr, "is_music != 0", null, "title");
                int i3 = -NUM;
                while (query.moveToNext()) {
                    try {
                        AudioEntry audioEntry = new AudioEntry();
                        audioEntry.id = (long) query.getInt(0);
                        audioEntry.author = query.getString(z);
                        audioEntry.title = query.getString(2);
                        audioEntry.path = query.getString(3);
                        audioEntry.duration = (int) (query.getLong(i) / 1000);
                        audioEntry.genre = query.getString(i2);
                        File file = new File(audioEntry.path);
                        Message tL_message = new TL_message();
                        tL_message.out = z;
                        tL_message.id = i3;
                        tL_message.to_id = new TL_peerUser();
                        Peer peer = tL_message.to_id;
                        int clientUserId = UserConfig.getInstance(AudioSelectActivity.this.currentAccount).getClientUserId();
                        tL_message.from_id = clientUserId;
                        peer.user_id = clientUserId;
                        tL_message.date = (int) (System.currentTimeMillis() / 1000);
                        tL_message.message = TtmlNode.ANONYMOUS_REGION_ID;
                        tL_message.attachPath = audioEntry.path;
                        tL_message.media = new TL_messageMediaDocument();
                        MessageMedia messageMedia = tL_message.media;
                        messageMedia.flags |= 3;
                        tL_message.media.document = new TL_document();
                        tL_message.flags |= 768;
                        String fileExtension = FileLoader.getFileExtension(file);
                        tL_message.media.document.id = 0;
                        tL_message.media.document.access_hash = 0;
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
                        tL_message.media.document.thumb = new TL_photoSizeEmpty();
                        tL_message.media.document.thumb.type = "s";
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
                        audioEntry.messageObject = new MessageObject(AudioSelectActivity.this.currentAccount, tL_message, false);
                        arrayList.add(audioEntry);
                        i3--;
                        z = true;
                        i = 4;
                        i2 = 5;
                    } catch (Exception e2) {
                        e = e2;
                        cursor = query;
                    } catch (Throwable e3) {
                        th = e3;
                    }
                }
                if (query != null) {
                    query.close();
                }
            } catch (Exception e4) {
                e3 = e4;
                try {
                    FileLog.m3e(e3);
                    if (cursor != null) {
                        cursor.close();
                    }
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        public void run() {
                            AudioSelectActivity.this.audioEntries = arrayList;
                            AudioSelectActivity.this.progressView.showTextView();
                            AudioSelectActivity.this.listViewAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Throwable e32) {
                    th = e32;
                    query = cursor;
                    if (query != null) {
                        query.close();
                    }
                    throw th;
                }
            }
            AndroidUtilities.runOnUIThread(/* anonymous class already generated */);
        }
    }

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList);
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$1 */
    class C19211 extends ActionBarMenuOnItemClick {
        C19211() {
        }

        public void onItemClick(int i) {
            if (i == -1) {
                AudioSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$2 */
    class C19222 implements OnItemClickListener {
        C19222() {
        }

        public void onItemClick(View view, int i) {
            AudioCell audioCell = (AudioCell) view;
            i = audioCell.getAudioEntry();
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(i.id) >= 0) {
                AudioSelectActivity.this.selectedAudios.remove(i.id);
                audioCell.setChecked(0);
            } else {
                AudioSelectActivity.this.selectedAudios.put(i.id, i);
                audioCell.setChecked(1);
            }
            AudioSelectActivity.this.updateBottomLayoutCount();
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.AudioSelectActivity$ListAdapter$1 */
        class C19231 implements AudioCellDelegate {
            C19231() {
            }

            public void startedPlayingAudio(MessageObject messageObject) {
                AudioSelectActivity.this.playingAudio = messageObject;
            }
        }

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
            viewGroup = new AudioCell(this.mContext);
            viewGroup.setDelegate(new C19231());
            return new Holder(viewGroup);
        }

        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            AudioEntry audioEntry = (AudioEntry) AudioSelectActivity.this.audioEntries.get(i);
            AudioCell audioCell = (AudioCell) viewHolder.itemView;
            AudioEntry audioEntry2 = (AudioEntry) AudioSelectActivity.this.audioEntries.get(i);
            boolean z = true;
            i = i != AudioSelectActivity.this.audioEntries.size() - 1 ? 1 : 0;
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            audioCell.setAudio(audioEntry2, i, z);
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        loadAudio();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        if (this.playingAudio != null && MediaController.getInstance().isPlayingMessage(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(C0446R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", C0446R.string.AttachMusic));
        this.actionBar.setActionBarMenuOnItemClick(new C19211());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = (FrameLayout) this.fragmentView;
        this.progressView = new EmptyTextProgressView(context);
        this.progressView.setText(LocaleController.getString("NoAudio", C0446R.string.NoAudio));
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        this.listView = new RecyclerListView(context);
        this.listView.setEmptyView(this.progressView);
        this.listView.setVerticalScrollBarEnabled(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        RecyclerListView recyclerListView = this.listView;
        Adapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        recyclerListView = this.listView;
        if (!LocaleController.isRTL) {
            i = 2;
        }
        recyclerListView.setVerticalScrollbarPosition(i);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        this.listView.setOnItemClickListener(new C19222());
        this.bottomLayout = new PickerBottomLayout(context, false);
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.cancelButton.setOnClickListener(new C08343());
        this.bottomLayout.doneButton.setOnClickListener(new C08354());
        View view = new View(context);
        view.setBackgroundResource(C0446R.drawable.header_shadow_reverse);
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.loadingAudio != null) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        updateBottomLayoutCount();
        return this.fragmentView;
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (i == NotificationCenter.messagePlayingDidReset && this.listViewAdapter != 0) {
            this.listViewAdapter.notifyDataSetChanged();
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
        if (this.progressView != null) {
            this.progressView.showProgress();
        }
        Utilities.globalQueue.postRunnable(new C08375());
    }

    public ThemeDescription[] getThemeDescriptions() {
        r1 = new ThemeDescription[24];
        r1[7] = new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider);
        r1[8] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_emptyListPlaceholder);
        r1[9] = new ThemeDescription(this.progressView, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, Theme.key_progressCircle);
        r1[10] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"titleTextView"}, null, null, null, Theme.key_windowBackgroundWhiteBlackText);
        r1[11] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"genreTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[12] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"authorTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2);
        r1[13] = new ThemeDescription(this.listView, 0, new Class[]{AudioCell.class}, new String[]{"timeTextView"}, null, null, null, Theme.key_windowBackgroundWhiteGrayText3);
        r1[14] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOX, new Class[]{AudioCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_musicPicker_checkbox);
        r1[15] = new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{AudioCell.class}, new String[]{"checkBox"}, null, null, null, Theme.key_musicPicker_checkboxCheck);
        r1[16] = new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{AudioCell.class}, new String[]{"playButton"}, null, null, null, Theme.key_musicPicker_buttonIcon);
        r1[17] = new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{AudioCell.class}, new String[]{"playButton"}, null, null, null, Theme.key_musicPicker_buttonBackground);
        r1[18] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundWhite);
        r1[19] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PickerBottomLayout.class}, new String[]{"cancelButton"}, null, null, null, Theme.key_picker_enabledButton);
        r1[20] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonTextView"}, null, null, null, Theme.key_picker_enabledButton);
        r1[21] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonTextView"}, null, null, null, Theme.key_picker_disabledButton);
        r1[22] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonBadgeTextView"}, null, null, null, Theme.key_picker_badgeText);
        r1[23] = new ThemeDescription(this.bottomLayout, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{PickerBottomLayout.class}, new String[]{"doneButtonBadgeTextView"}, null, null, null, Theme.key_picker_badge);
        return r1;
    }
}

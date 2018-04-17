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
import org.telegram.messenger.beta.R;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Document;
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
    class C08283 implements OnClickListener {
        C08283() {
        }

        public void onClick(View view) {
            AudioSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$4 */
    class C08294 implements OnClickListener {
        C08294() {
        }

        public void onClick(View view) {
            if (AudioSelectActivity.this.delegate != null) {
                ArrayList<MessageObject> audios = new ArrayList();
                for (int a = 0; a < AudioSelectActivity.this.selectedAudios.size(); a++) {
                    audios.add(((AudioEntry) AudioSelectActivity.this.selectedAudios.valueAt(a)).messageObject);
                }
                AudioSelectActivity.this.delegate.didSelectAudio(audios);
            }
            AudioSelectActivity.this.finishFragment();
        }
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$5 */
    class C08315 implements Runnable {
        C08315() {
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            C08315 c08315 = this;
            projection = new String[6];
            boolean z = true;
            projection[1] = "artist";
            int i = 2;
            projection[2] = "title";
            projection[3] = "_data";
            int i2 = 4;
            projection[4] = "duration";
            int i3 = 5;
            projection[5] = "album";
            final ArrayList<AudioEntry> newAudioEntries = new ArrayList();
            Cursor cursor = null;
            try {
                cursor = ApplicationLoader.applicationContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, "is_music != 0", null, "title");
                int id = -NUM;
                while (cursor.moveToNext()) {
                    AudioEntry audioEntry = new AudioEntry();
                    audioEntry.id = (long) cursor.getInt(0);
                    audioEntry.author = cursor.getString(z);
                    audioEntry.title = cursor.getString(i);
                    audioEntry.path = cursor.getString(3);
                    audioEntry.duration = (int) (cursor.getLong(i2) / 1000);
                    audioEntry.genre = cursor.getString(i3);
                    File file = new File(audioEntry.path);
                    TL_message message = new TL_message();
                    message.out = z;
                    message.id = id;
                    message.to_id = new TL_peerUser();
                    Peer peer = message.to_id;
                    int clientUserId = UserConfig.getInstance(AudioSelectActivity.this.currentAccount).getClientUserId();
                    message.from_id = clientUserId;
                    peer.user_id = clientUserId;
                    message.date = (int) (System.currentTimeMillis() / 1000);
                    message.message = TtmlNode.ANONYMOUS_REGION_ID;
                    message.attachPath = audioEntry.path;
                    message.media = new TL_messageMediaDocument();
                    MessageMedia messageMedia = message.media;
                    messageMedia.flags |= 3;
                    message.media.document = new TL_document();
                    message.flags |= 768;
                    String ext = FileLoader.getFileExtension(file);
                    message.media.document.id = 0;
                    message.media.document.access_hash = 0;
                    message.media.document.date = message.date;
                    Document document = message.media.document;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("audio/");
                    stringBuilder.append(ext.length() > 0 ? ext : "mp3");
                    document.mime_type = stringBuilder.toString();
                    message.media.document.size = (int) file.length();
                    message.media.document.thumb = new TL_photoSizeEmpty();
                    message.media.document.thumb.type = "s";
                    message.media.document.dc_id = 0;
                    TL_documentAttributeAudio attributeAudio = new TL_documentAttributeAudio();
                    attributeAudio.duration = audioEntry.duration;
                    attributeAudio.title = audioEntry.title;
                    attributeAudio.performer = audioEntry.author;
                    attributeAudio.flags |= 3;
                    message.media.document.attributes.add(attributeAudio);
                    TL_documentAttributeFilename fileName = new TL_documentAttributeFilename();
                    fileName.file_name = file.getName();
                    message.media.document.attributes.add(fileName);
                    audioEntry.messageObject = new MessageObject(AudioSelectActivity.this.currentAccount, message, false);
                    newAudioEntries.add(audioEntry);
                    id--;
                    z = true;
                    i = 2;
                    i2 = 4;
                    i3 = 5;
                }
            } catch (Throwable e) {
                FileLog.m3e(e);
                if (cursor != null) {
                    cursor.close();
                }
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        AudioSelectActivity.this.audioEntries = newAudioEntries;
                        AudioSelectActivity.this.progressView.showTextView();
                        AudioSelectActivity.this.listViewAdapter.notifyDataSetChanged();
                    }
                });
            } catch (Throwable e2) {
                Throwable th = e2;
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList);
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$1 */
    class C19151 extends ActionBarMenuOnItemClick {
        C19151() {
        }

        public void onItemClick(int id) {
            if (id == -1) {
                AudioSelectActivity.this.finishFragment();
            }
        }
    }

    /* renamed from: org.telegram.ui.AudioSelectActivity$2 */
    class C19162 implements OnItemClickListener {
        C19162() {
        }

        public void onItemClick(View view, int position) {
            AudioCell audioCell = (AudioCell) view;
            AudioEntry audioEntry = audioCell.getAudioEntry();
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) >= 0) {
                AudioSelectActivity.this.selectedAudios.remove(audioEntry.id);
                audioCell.setChecked(false);
            } else {
                AudioSelectActivity.this.selectedAudios.put(audioEntry.id, audioEntry);
                audioCell.setChecked(true);
            }
            AudioSelectActivity.this.updateBottomLayoutCount();
        }
    }

    private class ListAdapter extends SelectionAdapter {
        private Context mContext;

        /* renamed from: org.telegram.ui.AudioSelectActivity$ListAdapter$1 */
        class C19171 implements AudioCellDelegate {
            C19171() {
            }

            public void startedPlayingAudio(MessageObject messageObject) {
                AudioSelectActivity.this.playingAudio = messageObject;
            }
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

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean isEnabled(ViewHolder holder) {
            return true;
        }

        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            AudioCell view = new AudioCell(this.mContext);
            view.setDelegate(new C19171());
            return new Holder(view);
        }

        public void onBindViewHolder(ViewHolder holder, int position) {
            AudioEntry audioEntry = (AudioEntry) AudioSelectActivity.this.audioEntries.get(position);
            AudioCell audioCell = (AudioCell) holder.itemView;
            AudioEntry audioEntry2 = (AudioEntry) AudioSelectActivity.this.audioEntries.get(position);
            boolean z = true;
            boolean z2 = position != AudioSelectActivity.this.audioEntries.size() - 1;
            if (AudioSelectActivity.this.selectedAudios.indexOfKey(audioEntry.id) < 0) {
                z = false;
            }
            audioCell.setAudio(audioEntry2, z2, z);
        }

        public int getItemViewType(int i) {
            return 0;
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
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        int i = 1;
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", R.string.AttachMusic));
        this.actionBar.setActionBarMenuOnItemClick(new C19151());
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.progressView = new EmptyTextProgressView(context);
        this.progressView.setText(LocaleController.getString("NoAudio", R.string.NoAudio));
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
        this.listView.setOnItemClickListener(new C19162());
        this.bottomLayout = new PickerBottomLayout(context, false);
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.cancelButton.setOnClickListener(new C08283());
        this.bottomLayout.doneButton.setOnClickListener(new C08294());
        View shadow = new View(context);
        shadow.setBackgroundResource(R.drawable.header_shadow_reverse);
        frameLayout.addView(shadow, LayoutHelper.createFrame(-1, 3.0f, 83, 0.0f, 0.0f, 0.0f, 48.0f));
        if (this.loadingAudio) {
            this.progressView.showProgress();
        } else {
            this.progressView.showTextView();
        }
        updateBottomLayoutCount();
        return this.fragmentView;
    }

    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.messagePlayingDidReset && this.listViewAdapter != null) {
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
        Utilities.globalQueue.postRunnable(new C08315());
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

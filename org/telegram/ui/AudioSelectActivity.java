package org.telegram.ui;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
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
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.Message;
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
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.AudioCell;
import org.telegram.ui.Cells.AudioCell.AudioCellDelegate;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;

public class AudioSelectActivity extends BaseFragment implements NotificationCenterDelegate {
    private ArrayList<AudioEntry> audioEntries = new ArrayList();
    private PickerBottomLayout bottomLayout;
    private AudioSelectActivityDelegate delegate;
    private ListAdapter listViewAdapter;
    private boolean loadingAudio;
    private MessageObject playingAudio;
    private EmptyTextProgressView progressView;
    private HashMap<Long, AudioEntry> selectedAudios = new HashMap();

    public interface AudioSelectActivityDelegate {
        void didSelectAudio(ArrayList<MessageObject> arrayList);
    }

    private class ListAdapter extends BaseFragmentAdapter {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int i) {
            return true;
        }

        public int getCount() {
            return AudioSelectActivity.this.audioEntries.size();
        }

        public Object getItem(int i) {
            return AudioSelectActivity.this.audioEntries.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public boolean hasStableIds() {
            return false;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            int type = getItemViewType(i);
            if (view == null) {
                view = new AudioCell(this.mContext);
                ((AudioCell) view).setDelegate(new AudioCellDelegate() {
                    public void startedPlayingAudio(MessageObject messageObject) {
                        AudioSelectActivity.this.playingAudio = messageObject;
                    }
                });
            }
            ((AudioCell) view).setAudio((AudioEntry) AudioSelectActivity.this.audioEntries.get(i), i != AudioSelectActivity.this.audioEntries.size() + -1, AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(((AudioEntry) AudioSelectActivity.this.audioEntries.get(i)).id)));
            return view;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return AudioSelectActivity.this.audioEntries.isEmpty();
        }
    }

    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
        loadAudio();
        return true;
    }

    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
        if (this.playingAudio != null && MediaController.getInstance().isPlayingAudio(this.playingAudio)) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
    }

    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString("AttachMusic", R.string.AttachMusic));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBarMenuOnItemClick() {
            public void onItemClick(int id) {
                if (id == -1) {
                    AudioSelectActivity.this.finishFragment();
                }
            }
        });
        this.fragmentView = new FrameLayout(context);
        FrameLayout frameLayout = this.fragmentView;
        this.progressView = new EmptyTextProgressView(context);
        this.progressView.setText(LocaleController.getString("NoAudio", R.string.NoAudio));
        frameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0f));
        ListView listView = new ListView(context);
        listView.setEmptyView(this.progressView);
        listView.setVerticalScrollBarEnabled(false);
        listView.setDivider(null);
        listView.setDividerHeight(0);
        android.widget.ListAdapter listAdapter = new ListAdapter(context);
        this.listViewAdapter = listAdapter;
        listView.setAdapter(listAdapter);
        listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        frameLayout.addView(listView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 48.0f));
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AudioCell audioCell = (AudioCell) view;
                AudioEntry audioEntry = audioCell.getAudioEntry();
                if (AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(audioEntry.id))) {
                    AudioSelectActivity.this.selectedAudios.remove(Long.valueOf(audioEntry.id));
                    audioCell.setChecked(false);
                } else {
                    AudioSelectActivity.this.selectedAudios.put(Long.valueOf(audioEntry.id), audioEntry);
                    audioCell.setChecked(true);
                }
                AudioSelectActivity.this.updateBottomLayoutCount();
            }
        });
        this.bottomLayout = new PickerBottomLayout(context, false);
        frameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
        this.bottomLayout.cancelButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                AudioSelectActivity.this.finishFragment();
            }
        });
        this.bottomLayout.doneButton.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (AudioSelectActivity.this.delegate != null) {
                    ArrayList<MessageObject> audios = new ArrayList();
                    for (Entry<Long, AudioEntry> entry : AudioSelectActivity.this.selectedAudios.entrySet()) {
                        audios.add(((AudioEntry) entry.getValue()).messageObject);
                    }
                    AudioSelectActivity.this.delegate.didSelectAudio(audios);
                }
                AudioSelectActivity.this.finishFragment();
            }
        });
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

    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.closeChats) {
            removeSelfFromStack();
        } else if (id == NotificationCenter.audioDidReset && this.listViewAdapter != null) {
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
        Utilities.globalQueue.postRunnable(new Runnable() {
            public void run() {
                String[] projection = new String[]{"_id", "artist", "title", "_data", "duration", "album"};
                ArrayList<AudioEntry> newAudioEntries = new ArrayList();
                Cursor cursor = null;
                try {
                    cursor = ApplicationLoader.applicationContext.getContentResolver().query(Media.EXTERNAL_CONTENT_URI, projection, "is_music != 0", null, "title");
                    int id = -NUM;
                    while (cursor.moveToNext()) {
                        AudioEntry audioEntry = new AudioEntry();
                        audioEntry.id = (long) cursor.getInt(0);
                        audioEntry.author = cursor.getString(1);
                        audioEntry.title = cursor.getString(2);
                        audioEntry.path = cursor.getString(3);
                        audioEntry.duration = (int) (cursor.getLong(4) / 1000);
                        audioEntry.genre = cursor.getString(5);
                        File file = new File(audioEntry.path);
                        Message message = new TL_message();
                        message.out = true;
                        message.id = id;
                        message.to_id = new TL_peerUser();
                        Peer peer = message.to_id;
                        int clientUserId = UserConfig.getClientUserId();
                        message.from_id = clientUserId;
                        peer.user_id = clientUserId;
                        message.date = (int) (System.currentTimeMillis() / 1000);
                        message.message = "-1";
                        message.attachPath = audioEntry.path;
                        message.media = new TL_messageMediaDocument();
                        message.media.document = new TL_document();
                        message.flags |= 768;
                        String ext = FileLoader.getFileExtension(file);
                        message.media.document.id = 0;
                        message.media.document.access_hash = 0;
                        message.media.document.date = message.date;
                        Document document = message.media.document;
                        StringBuilder append = new StringBuilder().append("audio/");
                        if (ext.length() <= 0) {
                            ext = "mp3";
                        }
                        document.mime_type = append.append(ext).toString();
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
                        audioEntry.messageObject = new MessageObject(message, null, false);
                        newAudioEntries.add(audioEntry);
                        id--;
                    }
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable e) {
                    FileLog.e("tmessages", e);
                    if (cursor != null) {
                        cursor.close();
                    }
                } catch (Throwable th) {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
                final ArrayList<AudioEntry> arrayList = newAudioEntries;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    public void run() {
                        AudioSelectActivity.this.audioEntries = arrayList;
                        AudioSelectActivity.this.progressView.showTextView();
                        AudioSelectActivity.this.listViewAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}

package org.telegram.ui;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DispatchQueue;
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
import org.telegram.tgnet.TLRPC.TL_photoSizeEmpty;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.AudioCell;
import org.telegram.ui.Cells.AudioCell.AudioCellDelegate;
import org.telegram.ui.Components.EmptyTextProgressView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PickerBottomLayout;

public class AudioSelectActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ArrayList<MediaController.AudioEntry> audioEntries = new ArrayList();
  private PickerBottomLayout bottomLayout;
  private AudioSelectActivityDelegate delegate;
  private ListAdapter listViewAdapter;
  private boolean loadingAudio;
  private MessageObject playingAudio;
  private EmptyTextProgressView progressView;
  private HashMap<Long, MediaController.AudioEntry> selectedAudios = new HashMap();
  
  private void loadAudio()
  {
    this.loadingAudio = true;
    if (this.progressView != null) {
      this.progressView.showProgress();
    }
    Utilities.globalQueue.postRunnable(new Runnable()
    {
      public void run()
      {
        final ArrayList localArrayList = new ArrayList();
        localObject3 = null;
        localObject1 = null;
        try
        {
          Cursor localCursor = ApplicationLoader.applicationContext.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, new String[] { "_id", "artist", "title", "_data", "duration", "album" }, "is_music != 0", null, "title");
          int i = -2000000000;
          localObject1 = localCursor;
          localObject3 = localCursor;
          if (localCursor.moveToNext())
          {
            localObject1 = localCursor;
            localObject3 = localCursor;
            MediaController.AudioEntry localAudioEntry = new MediaController.AudioEntry();
            localObject1 = localCursor;
            localObject3 = localCursor;
            localAudioEntry.id = localCursor.getInt(0);
            localObject1 = localCursor;
            localObject3 = localCursor;
            localAudioEntry.author = localCursor.getString(1);
            localObject1 = localCursor;
            localObject3 = localCursor;
            localAudioEntry.title = localCursor.getString(2);
            localObject1 = localCursor;
            localObject3 = localCursor;
            localAudioEntry.path = localCursor.getString(3);
            localObject1 = localCursor;
            localObject3 = localCursor;
            localAudioEntry.duration = ((int)(localCursor.getLong(4) / 1000L));
            localObject1 = localCursor;
            localObject3 = localCursor;
            localAudioEntry.genre = localCursor.getString(5);
            localObject1 = localCursor;
            localObject3 = localCursor;
            File localFile = new File(localAudioEntry.path);
            localObject1 = localCursor;
            localObject3 = localCursor;
            TLRPC.TL_message localTL_message = new TLRPC.TL_message();
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.out = true;
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.id = i;
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.to_id = new TLRPC.TL_peerUser();
            localObject1 = localCursor;
            localObject3 = localCursor;
            Object localObject4 = localTL_message.to_id;
            localObject1 = localCursor;
            localObject3 = localCursor;
            int j = UserConfig.getClientUserId();
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.from_id = j;
            localObject1 = localCursor;
            localObject3 = localCursor;
            ((TLRPC.Peer)localObject4).user_id = j;
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.date = ((int)(System.currentTimeMillis() / 1000L));
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.message = "-1";
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.attachPath = localAudioEntry.path;
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.media = new TLRPC.TL_messageMediaDocument();
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.media.document = new TLRPC.TL_document();
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.flags |= 0x300;
            localObject1 = localCursor;
            localObject3 = localCursor;
            localObject4 = FileLoader.getFileExtension(localFile);
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.media.document.id = 0L;
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.media.document.access_hash = 0L;
            localObject1 = localCursor;
            localObject3 = localCursor;
            localTL_message.media.document.date = localTL_message.date;
            localObject1 = localCursor;
            localObject3 = localCursor;
            TLRPC.Document localDocument = localTL_message.media.document;
            localObject1 = localCursor;
            localObject3 = localCursor;
            StringBuilder localStringBuilder = new StringBuilder().append("audio/");
            localObject1 = localCursor;
            localObject3 = localCursor;
            if (((String)localObject4).length() > 0) {}
            for (;;)
            {
              localObject1 = localCursor;
              localObject3 = localCursor;
              localDocument.mime_type = ((String)localObject4);
              localObject1 = localCursor;
              localObject3 = localCursor;
              localTL_message.media.document.size = ((int)localFile.length());
              localObject1 = localCursor;
              localObject3 = localCursor;
              localTL_message.media.document.thumb = new TLRPC.TL_photoSizeEmpty();
              localObject1 = localCursor;
              localObject3 = localCursor;
              localTL_message.media.document.thumb.type = "s";
              localObject1 = localCursor;
              localObject3 = localCursor;
              localTL_message.media.document.dc_id = 0;
              localObject1 = localCursor;
              localObject3 = localCursor;
              localObject4 = new TLRPC.TL_documentAttributeAudio();
              localObject1 = localCursor;
              localObject3 = localCursor;
              ((TLRPC.TL_documentAttributeAudio)localObject4).duration = localAudioEntry.duration;
              localObject1 = localCursor;
              localObject3 = localCursor;
              ((TLRPC.TL_documentAttributeAudio)localObject4).title = localAudioEntry.title;
              localObject1 = localCursor;
              localObject3 = localCursor;
              ((TLRPC.TL_documentAttributeAudio)localObject4).performer = localAudioEntry.author;
              localObject1 = localCursor;
              localObject3 = localCursor;
              ((TLRPC.TL_documentAttributeAudio)localObject4).flags |= 0x3;
              localObject1 = localCursor;
              localObject3 = localCursor;
              localTL_message.media.document.attributes.add(localObject4);
              localObject1 = localCursor;
              localObject3 = localCursor;
              localObject4 = new TLRPC.TL_documentAttributeFilename();
              localObject1 = localCursor;
              localObject3 = localCursor;
              ((TLRPC.TL_documentAttributeFilename)localObject4).file_name = localFile.getName();
              localObject1 = localCursor;
              localObject3 = localCursor;
              localTL_message.media.document.attributes.add(localObject4);
              localObject1 = localCursor;
              localObject3 = localCursor;
              localAudioEntry.messageObject = new MessageObject(localTL_message, null, false);
              localObject1 = localCursor;
              localObject3 = localCursor;
              localArrayList.add(localAudioEntry);
              i -= 1;
              break;
              localObject4 = "mp3";
            }
          }
          if (localCursor != null) {
            localCursor.close();
          }
        }
        catch (Exception localException)
        {
          for (;;)
          {
            localObject3 = localObject1;
            FileLog.e("tmessages", localException);
            if (localObject1 != null) {
              ((Cursor)localObject1).close();
            }
          }
        }
        finally
        {
          if (localObject3 == null) {
            break label1002;
          }
          ((Cursor)localObject3).close();
        }
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            AudioSelectActivity.access$302(AudioSelectActivity.this, localArrayList);
            AudioSelectActivity.this.progressView.showTextView();
            AudioSelectActivity.this.listViewAdapter.notifyDataSetChanged();
          }
        });
      }
    });
  }
  
  private void updateBottomLayoutCount()
  {
    this.bottomLayout.updateSelectedCount(this.selectedAudios.size(), true);
  }
  
  public View createView(Context paramContext)
  {
    this.actionBar.setBackButtonImage(2130837700);
    this.actionBar.setAllowOverlayTitle(true);
    this.actionBar.setTitle(LocaleController.getString("AttachMusic", 2131165342));
    this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
    {
      public void onItemClick(int paramAnonymousInt)
      {
        if (paramAnonymousInt == -1) {
          AudioSelectActivity.this.finishFragment();
        }
      }
    });
    this.fragmentView = new FrameLayout(paramContext);
    FrameLayout localFrameLayout = (FrameLayout)this.fragmentView;
    this.progressView = new EmptyTextProgressView(paramContext);
    this.progressView.setText(LocaleController.getString("NoAudio", 2131165927));
    localFrameLayout.addView(this.progressView, LayoutHelper.createFrame(-1, -1.0F));
    ListView localListView = new ListView(paramContext);
    localListView.setEmptyView(this.progressView);
    localListView.setVerticalScrollBarEnabled(false);
    localListView.setDivider(null);
    localListView.setDividerHeight(0);
    ListAdapter localListAdapter = new ListAdapter(paramContext);
    this.listViewAdapter = localListAdapter;
    localListView.setAdapter(localListAdapter);
    int i;
    if (LocaleController.isRTL)
    {
      i = 1;
      localListView.setVerticalScrollbarPosition(i);
      localFrameLayout.addView(localListView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 0.0F, 0.0F, 48.0F));
      localListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          paramAnonymousAdapterView = (AudioCell)paramAnonymousView;
          paramAnonymousView = paramAnonymousAdapterView.getAudioEntry();
          if (AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(paramAnonymousView.id)))
          {
            AudioSelectActivity.this.selectedAudios.remove(Long.valueOf(paramAnonymousView.id));
            paramAnonymousAdapterView.setChecked(false);
          }
          for (;;)
          {
            AudioSelectActivity.this.updateBottomLayoutCount();
            return;
            AudioSelectActivity.this.selectedAudios.put(Long.valueOf(paramAnonymousView.id), paramAnonymousView);
            paramAnonymousAdapterView.setChecked(true);
          }
        }
      });
      this.bottomLayout = new PickerBottomLayout(paramContext, false);
      localFrameLayout.addView(this.bottomLayout, LayoutHelper.createFrame(-1, 48, 80));
      this.bottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          AudioSelectActivity.this.finishFragment();
        }
      });
      this.bottomLayout.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (AudioSelectActivity.this.delegate != null)
          {
            paramAnonymousView = new ArrayList();
            Iterator localIterator = AudioSelectActivity.this.selectedAudios.entrySet().iterator();
            while (localIterator.hasNext()) {
              paramAnonymousView.add(((MediaController.AudioEntry)((Map.Entry)localIterator.next()).getValue()).messageObject);
            }
            AudioSelectActivity.this.delegate.didSelectAudio(paramAnonymousView);
          }
          AudioSelectActivity.this.finishFragment();
        }
      });
      paramContext = new View(paramContext);
      paramContext.setBackgroundResource(2130837694);
      localFrameLayout.addView(paramContext, LayoutHelper.createFrame(-1, 3.0F, 83, 0.0F, 0.0F, 0.0F, 48.0F));
      if (!this.loadingAudio) {
        break label339;
      }
      this.progressView.showProgress();
    }
    for (;;)
    {
      updateBottomLayoutCount();
      return this.fragmentView;
      i = 2;
      break;
      label339:
      this.progressView.showTextView();
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
    while ((paramInt != NotificationCenter.audioDidReset) || (this.listViewAdapter == null)) {
      return;
    }
    this.listViewAdapter.notifyDataSetChanged();
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.audioDidReset);
    loadAudio();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.audioDidReset);
    if ((this.playingAudio != null) && (MediaController.getInstance().isPlayingAudio(this.playingAudio))) {
      MediaController.getInstance().cleanupPlayer(true, true);
    }
  }
  
  public void setDelegate(AudioSelectActivityDelegate paramAudioSelectActivityDelegate)
  {
    this.delegate = paramAudioSelectActivityDelegate;
  }
  
  public static abstract interface AudioSelectActivityDelegate
  {
    public abstract void didSelectAudio(ArrayList<MessageObject> paramArrayList);
  }
  
  private class ListAdapter
    extends BaseFragmentAdapter
  {
    private Context mContext;
    
    public ListAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public boolean areAllItemsEnabled()
    {
      return true;
    }
    
    public int getCount()
    {
      return AudioSelectActivity.this.audioEntries.size();
    }
    
    public Object getItem(int paramInt)
    {
      return AudioSelectActivity.this.audioEntries.get(paramInt);
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      getItemViewType(paramInt);
      paramViewGroup = paramView;
      if (paramView == null)
      {
        paramViewGroup = new AudioCell(this.mContext);
        ((AudioCell)paramViewGroup).setDelegate(new AudioCell.AudioCellDelegate()
        {
          public void startedPlayingAudio(MessageObject paramAnonymousMessageObject)
          {
            AudioSelectActivity.access$602(AudioSelectActivity.this, paramAnonymousMessageObject);
          }
        });
      }
      paramView = (MediaController.AudioEntry)AudioSelectActivity.this.audioEntries.get(paramInt);
      AudioCell localAudioCell = (AudioCell)paramViewGroup;
      MediaController.AudioEntry localAudioEntry = (MediaController.AudioEntry)AudioSelectActivity.this.audioEntries.get(paramInt);
      if (paramInt != AudioSelectActivity.this.audioEntries.size() - 1) {}
      for (boolean bool = true;; bool = false)
      {
        localAudioCell.setAudio(localAudioEntry, bool, AudioSelectActivity.this.selectedAudios.containsKey(Long.valueOf(paramView.id)));
        return paramViewGroup;
      }
    }
    
    public int getViewTypeCount()
    {
      return 1;
    }
    
    public boolean hasStableIds()
    {
      return false;
    }
    
    public boolean isEmpty()
    {
      return AudioSelectActivity.this.audioEntries.isEmpty();
    }
    
    public boolean isEnabled(int paramInt)
    {
      return true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/AudioSelectActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
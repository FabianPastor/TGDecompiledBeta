package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.ViewGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeSticker;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_getStickers;
import org.telegram.tgnet.TLRPC.TL_messages_stickers;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.SelectionAdapter;

public class StickersAdapter
  extends RecyclerListView.SelectionAdapter
  implements NotificationCenter.NotificationCenterDelegate
{
  private int currentAccount = UserConfig.selectedAccount;
  private boolean delayLocalResults;
  private StickersAdapterDelegate delegate;
  private int lastReqId;
  private String lastSticker;
  private Context mContext;
  private ArrayList<TLRPC.Document> stickers;
  private HashMap<String, TLRPC.Document> stickersMap;
  private ArrayList<String> stickersToLoad = new ArrayList();
  private boolean visible;
  
  public StickersAdapter(Context paramContext, StickersAdapterDelegate paramStickersAdapterDelegate)
  {
    this.mContext = paramContext;
    this.delegate = paramStickersAdapterDelegate;
    DataQuery.getInstance(this.currentAccount).checkStickers(0);
    DataQuery.getInstance(this.currentAccount).checkStickers(1);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  private void addStickerToResult(TLRPC.Document paramDocument)
  {
    if (paramDocument == null) {}
    for (;;)
    {
      return;
      String str = paramDocument.dc_id + "_" + paramDocument.id;
      if ((this.stickersMap == null) || (!this.stickersMap.containsKey(str)))
      {
        if (this.stickers == null)
        {
          this.stickers = new ArrayList();
          this.stickersMap = new HashMap();
        }
        this.stickers.add(paramDocument);
        this.stickersMap.put(str, paramDocument);
      }
    }
  }
  
  private void addStickersToResult(ArrayList<TLRPC.Document> paramArrayList)
  {
    if ((paramArrayList == null) || (paramArrayList.isEmpty())) {
      return;
    }
    int i = 0;
    int j = paramArrayList.size();
    label19:
    TLRPC.Document localDocument;
    String str;
    if (i < j)
    {
      localDocument = (TLRPC.Document)paramArrayList.get(i);
      str = localDocument.dc_id + "_" + localDocument.id;
      if ((this.stickersMap == null) || (!this.stickersMap.containsKey(str))) {
        break label92;
      }
    }
    for (;;)
    {
      i++;
      break label19;
      break;
      label92:
      if (this.stickers == null)
      {
        this.stickers = new ArrayList();
        this.stickersMap = new HashMap();
      }
      this.stickers.add(localDocument);
      this.stickersMap.put(str, localDocument);
    }
  }
  
  private boolean checkStickerFilesExistAndDownload()
  {
    boolean bool = false;
    if (this.stickers == null) {}
    for (;;)
    {
      return bool;
      this.stickersToLoad.clear();
      int i = Math.min(10, this.stickers.size());
      for (int j = 0; j < i; j++)
      {
        TLRPC.Document localDocument = (TLRPC.Document)this.stickers.get(j);
        if (!FileLoader.getPathToAttach(localDocument.thumb, "webp", true).exists())
        {
          this.stickersToLoad.add(FileLoader.getAttachFileName(localDocument.thumb, "webp"));
          FileLoader.getInstance(this.currentAccount).loadFile(localDocument.thumb.location, "webp", 0, 1);
        }
      }
      bool = this.stickersToLoad.isEmpty();
    }
  }
  
  private boolean isValidSticker(TLRPC.Document paramDocument, String paramString)
  {
    int i = 0;
    int j = paramDocument.attributes.size();
    if (i < j)
    {
      TLRPC.DocumentAttribute localDocumentAttribute = (TLRPC.DocumentAttribute)paramDocument.attributes.get(i);
      if ((localDocumentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
        if ((localDocumentAttribute.alt == null) || (!localDocumentAttribute.alt.contains(paramString))) {
          break label70;
        }
      }
    }
    label70:
    for (boolean bool = true;; bool = false)
    {
      return bool;
      i++;
      break;
    }
  }
  
  private void searchServerStickers(final String paramString)
  {
    if (this.lastReqId != 0) {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
    }
    TLRPC.TL_messages_getStickers localTL_messages_getStickers = new TLRPC.TL_messages_getStickers();
    localTL_messages_getStickers.emoticon = paramString;
    localTL_messages_getStickers.hash = "";
    localTL_messages_getStickers.exclude_featured = false;
    this.lastReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_messages_getStickers, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            boolean bool1 = false;
            StickersAdapter.access$002(StickersAdapter.this, 0);
            if ((!StickersAdapter.2.this.val$emoji.equals(StickersAdapter.this.lastSticker)) || (!(paramAnonymousTLObject instanceof TLRPC.TL_messages_stickers))) {
              return;
            }
            StickersAdapter.access$202(StickersAdapter.this, false);
            Object localObject = (TLRPC.TL_messages_stickers)paramAnonymousTLObject;
            int i;
            if (StickersAdapter.this.stickers != null)
            {
              i = StickersAdapter.this.stickers.size();
              label95:
              StickersAdapter.this.addStickersToResult(((TLRPC.TL_messages_stickers)localObject).stickers);
              if (StickersAdapter.this.stickers == null) {
                break label302;
              }
            }
            label302:
            for (int j = StickersAdapter.this.stickers.size();; j = 0)
            {
              if ((!StickersAdapter.this.visible) && (StickersAdapter.this.stickers != null) && (!StickersAdapter.this.stickers.isEmpty()))
              {
                StickersAdapter.this.checkStickerFilesExistAndDownload();
                localObject = StickersAdapter.this.delegate;
                boolean bool2 = bool1;
                if (StickersAdapter.this.stickers != null)
                {
                  bool2 = bool1;
                  if (!StickersAdapter.this.stickers.isEmpty())
                  {
                    bool2 = bool1;
                    if (StickersAdapter.this.stickersToLoad.isEmpty()) {
                      bool2 = true;
                    }
                  }
                }
                ((StickersAdapter.StickersAdapterDelegate)localObject).needChangePanelVisibility(bool2);
                StickersAdapter.access$502(StickersAdapter.this, true);
              }
              if (i == j) {
                break;
              }
              StickersAdapter.this.notifyDataSetChanged();
              break;
              i = 0;
              break label95;
            }
          }
        });
      }
    });
  }
  
  public void clearStickers()
  {
    this.lastSticker = null;
    this.stickers = null;
    this.stickersMap = null;
    this.stickersToLoad.clear();
    notifyDataSetChanged();
    if (this.lastReqId != 0)
    {
      ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.lastReqId, true);
      this.lastReqId = 0;
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    boolean bool1 = false;
    if (((paramInt1 == NotificationCenter.FileDidLoaded) || (paramInt1 == NotificationCenter.FileDidFailedLoad)) && (this.stickers != null) && (!this.stickers.isEmpty()) && (!this.stickersToLoad.isEmpty()) && (this.visible))
    {
      paramVarArgs = (String)paramVarArgs[0];
      this.stickersToLoad.remove(paramVarArgs);
      if (this.stickersToLoad.isEmpty())
      {
        paramVarArgs = this.delegate;
        boolean bool2 = bool1;
        if (this.stickers != null)
        {
          bool2 = bool1;
          if (!this.stickers.isEmpty())
          {
            bool2 = bool1;
            if (this.stickersToLoad.isEmpty()) {
              bool2 = true;
            }
          }
        }
        paramVarArgs.needChangePanelVisibility(bool2);
      }
    }
  }
  
  public TLRPC.Document getItem(int paramInt)
  {
    if ((this.stickers != null) && (paramInt >= 0) && (paramInt < this.stickers.size())) {}
    for (TLRPC.Document localDocument = (TLRPC.Document)this.stickers.get(paramInt);; localDocument = null) {
      return localDocument;
    }
  }
  
  public int getItemCount()
  {
    if ((!this.delayLocalResults) && (this.stickers != null)) {}
    for (int i = this.stickers.size();; i = 0) {
      return i;
    }
  }
  
  public boolean isEnabled(RecyclerView.ViewHolder paramViewHolder)
  {
    return true;
  }
  
  public void loadStikersForEmoji(CharSequence paramCharSequence)
  {
    if (SharedConfig.suggestStickers == 2) {}
    for (;;)
    {
      return;
      int i;
      int j;
      int k;
      final Object localObject;
      label51:
      int m;
      if ((paramCharSequence != null) && (paramCharSequence.length() > 0) && (paramCharSequence.length() <= 14))
      {
        i = 1;
        if (i == 0) {
          break label768;
        }
        j = paramCharSequence.length();
        k = 0;
        localObject = paramCharSequence;
        if (k >= j) {
          break label313;
        }
        if ((k >= j - 1) || (((((CharSequence)localObject).charAt(k) != 55356) || (((CharSequence)localObject).charAt(k + 1) < 57339) || (((CharSequence)localObject).charAt(k + 1) > 57343)) && ((((CharSequence)localObject).charAt(k) != '‍') || ((((CharSequence)localObject).charAt(k + 1) != '♀') && (((CharSequence)localObject).charAt(k + 1) != '♂'))))) {
          break label234;
        }
        paramCharSequence = TextUtils.concat(new CharSequence[] { ((CharSequence)localObject).subSequence(0, k), ((CharSequence)localObject).subSequence(k + 2, ((CharSequence)localObject).length()) });
        i = j - 2;
        m = k - 1;
      }
      for (;;)
      {
        k = m + 1;
        j = i;
        localObject = paramCharSequence;
        break label51;
        i = 0;
        break;
        label234:
        m = k;
        i = j;
        paramCharSequence = (CharSequence)localObject;
        if (((CharSequence)localObject).charAt(k) == 65039)
        {
          paramCharSequence = TextUtils.concat(new CharSequence[] { ((CharSequence)localObject).subSequence(0, k), ((CharSequence)localObject).subSequence(k + 1, ((CharSequence)localObject).length()) });
          i = j - 1;
          m = k - 1;
        }
      }
      label313:
      this.lastSticker = ((CharSequence)localObject).toString().trim();
      if (!Emoji.isValidEmoji(this.lastSticker))
      {
        if (this.visible)
        {
          this.visible = false;
          this.delegate.needChangePanelVisibility(false);
          notifyDataSetChanged();
        }
      }
      else
      {
        this.stickers = null;
        this.stickersMap = null;
        this.delayLocalResults = false;
        localObject = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(0);
        final ArrayList localArrayList = DataQuery.getInstance(this.currentAccount).getRecentStickersNoCopy(2);
        m = 0;
        k = 0;
        j = ((ArrayList)localObject).size();
        for (;;)
        {
          if (k < j)
          {
            paramCharSequence = (TLRPC.Document)((ArrayList)localObject).get(k);
            i = m;
            if (isValidSticker(paramCharSequence, this.lastSticker))
            {
              addStickerToResult(paramCharSequence);
              m++;
              i = m;
              if (m < 5) {}
            }
          }
          else
          {
            i = 0;
            k = localArrayList.size();
            while (i < k)
            {
              paramCharSequence = (TLRPC.Document)localArrayList.get(i);
              if (isValidSticker(paramCharSequence, this.lastSticker)) {
                addStickerToResult(paramCharSequence);
              }
              i++;
            }
          }
          k++;
          m = i;
        }
        paramCharSequence = DataQuery.getInstance(this.currentAccount).getAllStickers();
        if (paramCharSequence != null) {}
        for (paramCharSequence = (ArrayList)paramCharSequence.get(this.lastSticker);; paramCharSequence = null)
        {
          if ((paramCharSequence != null) && (!paramCharSequence.isEmpty()))
          {
            paramCharSequence = new ArrayList(paramCharSequence);
            if (!((ArrayList)localObject).isEmpty()) {
              Collections.sort(paramCharSequence, new Comparator()
              {
                private int getIndex(long paramAnonymousLong)
                {
                  int i = 0;
                  if (i < localArrayList.size()) {
                    if (((TLRPC.Document)localArrayList.get(i)).id == paramAnonymousLong) {
                      i += 1000;
                    }
                  }
                  for (;;)
                  {
                    return i;
                    i++;
                    break;
                    for (i = 0;; i++)
                    {
                      if (i >= localObject.size()) {
                        break label87;
                      }
                      if (((TLRPC.Document)localObject.get(i)).id == paramAnonymousLong) {
                        break;
                      }
                    }
                    label87:
                    i = -1;
                  }
                }
                
                public int compare(TLRPC.Document paramAnonymousDocument1, TLRPC.Document paramAnonymousDocument2)
                {
                  int i = getIndex(paramAnonymousDocument1.id);
                  int j = getIndex(paramAnonymousDocument2.id);
                  if (i > j) {
                    j = -1;
                  }
                  for (;;)
                  {
                    return j;
                    if (i < j) {
                      j = 1;
                    } else {
                      j = 0;
                    }
                  }
                }
              });
            }
            addStickersToResult(paramCharSequence);
          }
          if (SharedConfig.suggestStickers == 0) {
            searchServerStickers(this.lastSticker);
          }
          if ((this.stickers == null) || (this.stickers.isEmpty())) {
            break label743;
          }
          if ((SharedConfig.suggestStickers != 0) || (this.stickers.size() >= 5)) {
            break label681;
          }
          this.delayLocalResults = true;
          this.delegate.needChangePanelVisibility(false);
          this.visible = false;
          notifyDataSetChanged();
          break;
        }
        label681:
        checkStickerFilesExistAndDownload();
        paramCharSequence = this.delegate;
        if ((this.stickers != null) && (!this.stickers.isEmpty()) && (this.stickersToLoad.isEmpty())) {}
        for (boolean bool = true;; bool = false)
        {
          paramCharSequence.needChangePanelVisibility(bool);
          this.visible = true;
          break;
        }
        label743:
        if (this.visible)
        {
          this.delegate.needChangePanelVisibility(false);
          this.visible = false;
          continue;
          label768:
          this.lastSticker = "";
          if ((this.visible) && (this.stickers != null))
          {
            this.visible = false;
            this.delegate.needChangePanelVisibility(false);
          }
        }
      }
    }
  }
  
  public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
  {
    int i = 0;
    if (paramInt == 0) {
      if (this.stickers.size() == 1) {
        i = 2;
      }
    }
    for (;;)
    {
      ((StickerCell)paramViewHolder.itemView).setSticker((TLRPC.Document)this.stickers.get(paramInt), i);
      return;
      i = -1;
      continue;
      if (paramInt == this.stickers.size() - 1) {
        i = 1;
      }
    }
  }
  
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
  {
    return new RecyclerListView.Holder(new StickerCell(this.mContext));
  }
  
  public void onDestroy()
  {
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  public static abstract interface StickersAdapterDelegate
  {
    public abstract void needChangePanelVisibility(boolean paramBoolean);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Adapters/StickersAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
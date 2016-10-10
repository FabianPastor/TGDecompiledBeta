package org.telegram.ui.Adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.query.StickersQuery;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.ui.Cells.StickerCell;

public class StickersAdapter
  extends RecyclerView.Adapter
  implements NotificationCenter.NotificationCenterDelegate
{
  private StickersAdapterDelegate delegate;
  private String lastSticker;
  private Context mContext;
  private ArrayList<TLRPC.Document> stickers;
  private ArrayList<String> stickersToLoad = new ArrayList();
  private boolean visible;
  
  public StickersAdapter(Context paramContext, StickersAdapterDelegate paramStickersAdapterDelegate)
  {
    this.mContext = paramContext;
    this.delegate = paramStickersAdapterDelegate;
    StickersQuery.checkStickers(0);
    StickersQuery.checkStickers(1);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  private boolean checkStickerFilesExistAndDownload()
  {
    if (this.stickers == null) {
      return false;
    }
    this.stickersToLoad.clear();
    int j = Math.min(10, this.stickers.size());
    int i = 0;
    while (i < j)
    {
      TLRPC.Document localDocument = (TLRPC.Document)this.stickers.get(i);
      if (!FileLoader.getPathToAttach(localDocument.thumb, "webp", true).exists())
      {
        this.stickersToLoad.add(FileLoader.getAttachFileName(localDocument.thumb, "webp"));
        FileLoader.getInstance().loadFile(localDocument.thumb.location, "webp", 0, true);
      }
      i += 1;
    }
    return this.stickersToLoad.isEmpty();
  }
  
  public void clearStickers()
  {
    this.lastSticker = null;
    this.stickers = null;
    this.stickersToLoad.clear();
    notifyDataSetChanged();
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    boolean bool2 = false;
    if (((paramInt == NotificationCenter.FileDidLoaded) || (paramInt == NotificationCenter.FileDidFailedLoad)) && (this.stickers != null) && (!this.stickers.isEmpty()) && (!this.stickersToLoad.isEmpty()) && (this.visible))
    {
      paramVarArgs = (String)paramVarArgs[0];
      this.stickersToLoad.remove(paramVarArgs);
      if (this.stickersToLoad.isEmpty())
      {
        paramVarArgs = this.delegate;
        boolean bool1 = bool2;
        if (this.stickers != null)
        {
          bool1 = bool2;
          if (!this.stickers.isEmpty())
          {
            bool1 = bool2;
            if (this.stickersToLoad.isEmpty()) {
              bool1 = true;
            }
          }
        }
        paramVarArgs.needChangePanelVisibility(bool1);
      }
    }
  }
  
  public TLRPC.Document getItem(int paramInt)
  {
    if ((this.stickers != null) && (paramInt >= 0) && (paramInt < this.stickers.size())) {
      return (TLRPC.Document)this.stickers.get(paramInt);
    }
    return null;
  }
  
  public int getItemCount()
  {
    if (this.stickers != null) {
      return this.stickers.size();
    }
    return 0;
  }
  
  public long getItemId(int paramInt)
  {
    return paramInt;
  }
  
  public void loadStikersForEmoji(final CharSequence paramCharSequence)
  {
    int m;
    int n;
    int i;
    CharSequence localCharSequence;
    label45:
    int j;
    int k;
    if ((paramCharSequence != null) && (paramCharSequence.length() > 0) && (paramCharSequence.length() <= 14))
    {
      m = 1;
      if (m == 0) {
        break label357;
      }
      n = paramCharSequence.length();
      i = 0;
      localCharSequence = paramCharSequence;
      if (i >= n) {
        break label293;
      }
      if ((i >= n - 1) || (((localCharSequence.charAt(i) != 55356) || (localCharSequence.charAt(i + 1) < 57339) || (localCharSequence.charAt(i + 1) > 57343)) && ((localCharSequence.charAt(i) != '‍') || ((localCharSequence.charAt(i + 1) != '♀') && (localCharSequence.charAt(i + 1) != '♂'))))) {
        break label218;
      }
      paramCharSequence = TextUtils.concat(new CharSequence[] { localCharSequence.subSequence(0, i), localCharSequence.subSequence(i + 2, localCharSequence.length()) });
      j = n - 2;
      k = i - 1;
    }
    for (;;)
    {
      i = k + 1;
      n = j;
      localCharSequence = paramCharSequence;
      break label45;
      m = 0;
      break;
      label218:
      k = i;
      j = n;
      paramCharSequence = localCharSequence;
      if (localCharSequence.charAt(i) == 65039)
      {
        paramCharSequence = TextUtils.concat(new CharSequence[] { localCharSequence.subSequence(0, i), localCharSequence.subSequence(i + 1, localCharSequence.length()) });
        j = n - 1;
        k = i - 1;
      }
    }
    label293:
    this.lastSticker = localCharSequence.toString();
    paramCharSequence = StickersQuery.getAllStickers();
    if (paramCharSequence != null)
    {
      paramCharSequence = (ArrayList)paramCharSequence.get(this.lastSticker);
      if ((this.stickers == null) || (paramCharSequence != null)) {
        break label392;
      }
      if (this.visible)
      {
        this.delegate.needChangePanelVisibility(false);
        this.visible = false;
      }
    }
    label357:
    if ((m == 0) && (this.visible) && (this.stickers != null))
    {
      this.visible = false;
      this.delegate.needChangePanelVisibility(false);
    }
    return;
    label392:
    if ((paramCharSequence != null) && (!paramCharSequence.isEmpty()))
    {
      paramCharSequence = new ArrayList(paramCharSequence);
      label412:
      this.stickers = paramCharSequence;
      if (this.stickers != null)
      {
        paramCharSequence = StickersQuery.getRecentStickersNoCopy(0);
        if (!paramCharSequence.isEmpty()) {
          Collections.sort(this.stickers, new Comparator()
          {
            private int getIndex(long paramAnonymousLong)
            {
              int i = 0;
              while (i < paramCharSequence.size())
              {
                if (((TLRPC.Document)paramCharSequence.get(i)).id == paramAnonymousLong) {
                  return i;
                }
                i += 1;
              }
              return -1;
            }
            
            public int compare(TLRPC.Document paramAnonymousDocument1, TLRPC.Document paramAnonymousDocument2)
            {
              int i = getIndex(paramAnonymousDocument1.id);
              int j = getIndex(paramAnonymousDocument2.id);
              if (i > j) {
                return -1;
              }
              if (i < j) {
                return 1;
              }
              return 0;
            }
          });
        }
      }
      checkStickerFilesExistAndDownload();
      paramCharSequence = this.delegate;
      if ((this.stickers == null) || (this.stickers.isEmpty()) || (!this.stickersToLoad.isEmpty())) {
        break label517;
      }
    }
    label517:
    for (boolean bool = true;; bool = false)
    {
      paramCharSequence.needChangePanelVisibility(bool);
      notifyDataSetChanged();
      this.visible = true;
      break;
      paramCharSequence = null;
      break label412;
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
    return new Holder(new StickerCell(this.mContext));
  }
  
  public void onDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidLoaded);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.FileDidFailedLoad);
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
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
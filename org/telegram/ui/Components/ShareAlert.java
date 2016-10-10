package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.support.widget.GridLayoutManager;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.ItemDecoration;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.Peer;
import org.telegram.tgnet.TLRPC.TL_channels_exportMessageLink;
import org.telegram.tgnet.TLRPC.TL_dialog;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_exportedMessageLink;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.DialogsActivity;

public class ShareAlert
  extends BottomSheet
  implements NotificationCenter.NotificationCenterDelegate
{
  private boolean copyLinkOnEnd;
  private LinearLayout doneButton;
  private TextView doneButtonBadgeTextView;
  private TextView doneButtonTextView;
  private TLRPC.TL_exportedMessageLink exportedMessageLink;
  private FrameLayout frameLayout;
  private RecyclerListView gridView;
  private boolean isPublicChannel;
  private GridLayoutManager layoutManager;
  private String linkToCopy;
  private ShareDialogsAdapter listAdapter;
  private boolean loadingLink;
  private EditText nameTextView;
  private int scrollOffsetY;
  private ShareSearchAdapter searchAdapter;
  private EmptyTextProgressView searchEmptyView;
  private HashMap<Long, TLRPC.TL_dialog> selectedDialogs = new HashMap();
  private MessageObject sendingMessageObject;
  private String sendingText;
  private View shadow;
  private Drawable shadowDrawable;
  private int topBeforeSwitch;
  
  public ShareAlert(final Context paramContext, MessageObject paramMessageObject, String paramString1, boolean paramBoolean, String paramString2)
  {
    super(paramContext, true);
    this.shadowDrawable = paramContext.getResources().getDrawable(2130837983);
    this.linkToCopy = paramString2;
    this.sendingMessageObject = paramMessageObject;
    this.searchAdapter = new ShareSearchAdapter(paramContext);
    this.isPublicChannel = paramBoolean;
    this.sendingText = paramString1;
    if (paramBoolean)
    {
      this.loadingLink = true;
      paramString1 = new TLRPC.TL_channels_exportMessageLink();
      paramString1.id = paramMessageObject.getId();
      paramString1.channel = MessagesController.getInputChannel(paramMessageObject.messageOwner.to_id.channel_id);
      ConnectionsManager.getInstance().sendRequest(paramString1, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          AndroidUtilities.runOnUIThread(new Runnable()
          {
            public void run()
            {
              if (paramAnonymousTLObject != null)
              {
                ShareAlert.access$002(ShareAlert.this, (TLRPC.TL_exportedMessageLink)paramAnonymousTLObject);
                if (ShareAlert.this.copyLinkOnEnd) {
                  ShareAlert.this.copyLink(ShareAlert.1.this.val$context);
                }
              }
              ShareAlert.access$302(ShareAlert.this, false);
            }
          });
        }
      });
    }
    this.containerView = new FrameLayout(paramContext)
    {
      private boolean ignoreLayout = false;
      
      protected void onDraw(Canvas paramAnonymousCanvas)
      {
        ShareAlert.this.shadowDrawable.setBounds(0, ShareAlert.this.scrollOffsetY - ShareAlert.backgroundPaddingTop, getMeasuredWidth(), getMeasuredHeight());
        ShareAlert.this.shadowDrawable.draw(paramAnonymousCanvas);
      }
      
      public boolean onInterceptTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        if ((paramAnonymousMotionEvent.getAction() == 0) && (ShareAlert.this.scrollOffsetY != 0) && (paramAnonymousMotionEvent.getY() < ShareAlert.this.scrollOffsetY))
        {
          ShareAlert.this.dismiss();
          return true;
        }
        return super.onInterceptTouchEvent(paramAnonymousMotionEvent);
      }
      
      protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
      {
        super.onLayout(paramAnonymousBoolean, paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
        ShareAlert.this.updateLayout();
      }
      
      protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        int i = View.MeasureSpec.getSize(paramAnonymousInt2);
        paramAnonymousInt2 = i;
        if (Build.VERSION.SDK_INT >= 21) {
          paramAnonymousInt2 = i - AndroidUtilities.statusBarHeight;
        }
        i = Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount());
        int j = AndroidUtilities.dp(48.0F) + Math.max(3, (int)Math.ceil(i / 4.0F)) * AndroidUtilities.dp(100.0F) + ShareAlert.backgroundPaddingTop;
        if (j < paramAnonymousInt2) {}
        for (i = 0;; i = paramAnonymousInt2 - paramAnonymousInt2 / 5 * 3 + AndroidUtilities.dp(8.0F))
        {
          if (ShareAlert.this.gridView.getPaddingTop() != i)
          {
            this.ignoreLayout = true;
            ShareAlert.this.gridView.setPadding(0, i, 0, AndroidUtilities.dp(8.0F));
            this.ignoreLayout = false;
          }
          super.onMeasure(paramAnonymousInt1, View.MeasureSpec.makeMeasureSpec(Math.min(j, paramAnonymousInt2), 1073741824));
          return;
        }
      }
      
      public boolean onTouchEvent(MotionEvent paramAnonymousMotionEvent)
      {
        return (!ShareAlert.this.isDismissed()) && (super.onTouchEvent(paramAnonymousMotionEvent));
      }
      
      public void requestLayout()
      {
        if (this.ignoreLayout) {
          return;
        }
        super.requestLayout();
      }
    };
    this.containerView.setWillNotDraw(false);
    this.containerView.setPadding(backgroundPaddingLeft, 0, backgroundPaddingLeft, 0);
    this.frameLayout = new FrameLayout(paramContext);
    this.frameLayout.setBackgroundColor(-1);
    this.frameLayout.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
      {
        return true;
      }
    });
    this.doneButton = new LinearLayout(paramContext);
    this.doneButton.setOrientation(0);
    this.doneButton.setBackgroundDrawable(Theme.createBarSelectorDrawable(788529152, false));
    this.doneButton.setPadding(AndroidUtilities.dp(21.0F), 0, AndroidUtilities.dp(21.0F), 0);
    this.frameLayout.addView(this.doneButton, LayoutHelper.createFrame(-2, -1, 53));
    this.doneButton.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramAnonymousView)
      {
        if ((ShareAlert.this.selectedDialogs.isEmpty()) && ((ShareAlert.this.isPublicChannel) || (ShareAlert.this.linkToCopy != null)))
        {
          if ((ShareAlert.this.linkToCopy == null) && (ShareAlert.this.loadingLink))
          {
            ShareAlert.access$102(ShareAlert.this, true);
            Toast.makeText(ShareAlert.this.getContext(), LocaleController.getString("Loading", 2131165834), 0).show();
          }
          for (;;)
          {
            ShareAlert.this.dismiss();
            return;
            ShareAlert.this.copyLink(ShareAlert.this.getContext());
          }
        }
        Object localObject;
        if (ShareAlert.this.sendingMessageObject != null)
        {
          paramAnonymousView = new ArrayList();
          paramAnonymousView.add(ShareAlert.this.sendingMessageObject);
          localObject = ShareAlert.this.selectedDialogs.entrySet().iterator();
          while (((Iterator)localObject).hasNext())
          {
            Map.Entry localEntry = (Map.Entry)((Iterator)localObject).next();
            SendMessagesHelper.getInstance().sendMessage(paramAnonymousView, ((Long)localEntry.getKey()).longValue());
          }
        }
        if (ShareAlert.this.sendingText != null)
        {
          paramAnonymousView = ShareAlert.this.selectedDialogs.entrySet().iterator();
          while (paramAnonymousView.hasNext())
          {
            localObject = (Map.Entry)paramAnonymousView.next();
            SendMessagesHelper.getInstance().sendMessage(ShareAlert.this.sendingText, ((Long)((Map.Entry)localObject).getKey()).longValue(), null, null, true, null, null, null);
          }
        }
        ShareAlert.this.dismiss();
      }
    });
    this.doneButtonBadgeTextView = new TextView(paramContext);
    this.doneButtonBadgeTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButtonBadgeTextView.setTextSize(1, 13.0F);
    this.doneButtonBadgeTextView.setTextColor(-1);
    this.doneButtonBadgeTextView.setGravity(17);
    this.doneButtonBadgeTextView.setBackgroundResource(2130837551);
    this.doneButtonBadgeTextView.setMinWidth(AndroidUtilities.dp(23.0F));
    this.doneButtonBadgeTextView.setPadding(AndroidUtilities.dp(8.0F), 0, AndroidUtilities.dp(8.0F), AndroidUtilities.dp(1.0F));
    this.doneButton.addView(this.doneButtonBadgeTextView, LayoutHelper.createLinear(-2, 23, 16, 0, 0, 10, 0));
    this.doneButtonTextView = new TextView(paramContext);
    this.doneButtonTextView.setTextSize(1, 14.0F);
    this.doneButtonTextView.setGravity(17);
    this.doneButtonTextView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0F));
    this.doneButtonTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
    this.doneButton.addView(this.doneButtonTextView, LayoutHelper.createLinear(-2, -2, 16));
    paramMessageObject = new ImageView(paramContext);
    paramMessageObject.setImageResource(2130837975);
    paramMessageObject.setScaleType(ImageView.ScaleType.CENTER);
    paramMessageObject.setPadding(0, AndroidUtilities.dp(2.0F), 0, 0);
    this.frameLayout.addView(paramMessageObject, LayoutHelper.createFrame(48, 48, 19));
    this.nameTextView = new EditText(paramContext);
    this.nameTextView.setHint(LocaleController.getString("ShareSendTo", 2131166278));
    this.nameTextView.setMaxLines(1);
    this.nameTextView.setSingleLine(true);
    this.nameTextView.setGravity(19);
    this.nameTextView.setTextSize(1, 16.0F);
    this.nameTextView.setBackgroundDrawable(null);
    this.nameTextView.setHintTextColor(-6842473);
    this.nameTextView.setImeOptions(268435456);
    this.nameTextView.setInputType(16385);
    AndroidUtilities.clearCursorDrawable(this.nameTextView);
    this.nameTextView.setTextColor(-14606047);
    this.frameLayout.addView(this.nameTextView, LayoutHelper.createFrame(-1, -1.0F, 51, 48.0F, 2.0F, 96.0F, 0.0F));
    this.nameTextView.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        paramAnonymousEditable = ShareAlert.this.nameTextView.getText().toString();
        if (paramAnonymousEditable.length() != 0)
        {
          if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter)
          {
            ShareAlert.access$1802(ShareAlert.this, ShareAlert.this.getCurrentTop());
            ShareAlert.this.gridView.setAdapter(ShareAlert.this.searchAdapter);
            ShareAlert.this.searchAdapter.notifyDataSetChanged();
          }
          if (ShareAlert.this.searchEmptyView != null) {
            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoResult", 2131165949));
          }
        }
        for (;;)
        {
          if (ShareAlert.this.searchAdapter != null) {
            ShareAlert.this.searchAdapter.searchDialogs(paramAnonymousEditable);
          }
          return;
          if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter)
          {
            int i = ShareAlert.this.getCurrentTop();
            ShareAlert.this.searchEmptyView.setText(LocaleController.getString("NoChats", 2131165930));
            ShareAlert.this.gridView.setAdapter(ShareAlert.this.listAdapter);
            ShareAlert.this.listAdapter.notifyDataSetChanged();
            if (i > 0) {
              ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -i);
            }
          }
        }
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    this.gridView = new RecyclerListView(paramContext);
    this.gridView.setTag(Integer.valueOf(13));
    this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(8.0F));
    this.gridView.setClipToPadding(false);
    paramMessageObject = this.gridView;
    paramString1 = new GridLayoutManager(getContext(), 4);
    this.layoutManager = paramString1;
    paramMessageObject.setLayoutManager(paramString1);
    this.gridView.setHorizontalScrollBarEnabled(false);
    this.gridView.setVerticalScrollBarEnabled(false);
    this.gridView.addItemDecoration(new RecyclerView.ItemDecoration()
    {
      public void getItemOffsets(Rect paramAnonymousRect, View paramAnonymousView, RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState)
      {
        int j = 0;
        paramAnonymousView = (ShareAlert.Holder)paramAnonymousRecyclerView.getChildViewHolder(paramAnonymousView);
        if (paramAnonymousView != null)
        {
          int k = paramAnonymousView.getAdapterPosition();
          if (k % 4 == 0)
          {
            i = 0;
            paramAnonymousRect.left = i;
            if (k % 4 != 3) {
              break label67;
            }
          }
          label67:
          for (int i = j;; i = AndroidUtilities.dp(4.0F))
          {
            paramAnonymousRect.right = i;
            return;
            i = AndroidUtilities.dp(4.0F);
            break;
          }
        }
        paramAnonymousRect.left = AndroidUtilities.dp(4.0F);
        paramAnonymousRect.right = AndroidUtilities.dp(4.0F);
      }
    });
    this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
    paramMessageObject = this.gridView;
    paramString1 = new ShareDialogsAdapter(paramContext);
    this.listAdapter = paramString1;
    paramMessageObject.setAdapter(paramString1);
    this.gridView.setGlowColor(-657673);
    this.gridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
    {
      public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
      {
        if (paramAnonymousInt < 0) {
          return;
        }
        TLRPC.TL_dialog localTL_dialog;
        if (ShareAlert.this.gridView.getAdapter() == ShareAlert.this.listAdapter)
        {
          localTL_dialog = ShareAlert.this.listAdapter.getItem(paramAnonymousInt);
          label37:
          if (localTL_dialog == null) {
            break label111;
          }
          paramAnonymousView = (ShareDialogCell)paramAnonymousView;
          if (!ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localTL_dialog.id))) {
            break label113;
          }
          ShareAlert.this.selectedDialogs.remove(Long.valueOf(localTL_dialog.id));
          paramAnonymousView.setChecked(false, true);
        }
        for (;;)
        {
          ShareAlert.this.updateSelectedCount();
          return;
          localTL_dialog = ShareAlert.this.searchAdapter.getItem(paramAnonymousInt);
          break label37;
          label111:
          break;
          label113:
          ShareAlert.this.selectedDialogs.put(Long.valueOf(localTL_dialog.id), localTL_dialog);
          paramAnonymousView.setChecked(true, true);
        }
      }
    });
    this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener()
    {
      public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
      {
        ShareAlert.this.updateLayout();
      }
    });
    this.searchEmptyView = new EmptyTextProgressView(paramContext);
    this.searchEmptyView.setShowAtCenter(true);
    this.searchEmptyView.showTextView();
    this.searchEmptyView.setText(LocaleController.getString("NoChats", 2131165930));
    this.gridView.setEmptyView(this.searchEmptyView);
    this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
    this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, 48, 51));
    this.shadow = new View(paramContext);
    this.shadow.setBackgroundResource(2130837693);
    this.containerView.addView(this.shadow, LayoutHelper.createFrame(-1, 3.0F, 51, 0.0F, 48.0F, 0.0F, 0.0F));
    updateSelectedCount();
    if (!DialogsActivity.dialogsLoaded)
    {
      MessagesController.getInstance().loadDialogs(0, 100, true);
      ContactsController.getInstance().checkInviteText();
      DialogsActivity.dialogsLoaded = true;
    }
    if (this.listAdapter.dialogs.isEmpty()) {
      NotificationCenter.getInstance().addObserver(this, NotificationCenter.dialogsNeedReload);
    }
  }
  
  private void copyLink(Context paramContext)
  {
    if ((this.exportedMessageLink == null) && (this.linkToCopy == null)) {
      return;
    }
    for (;;)
    {
      try
      {
        ClipboardManager localClipboardManager = (ClipboardManager)ApplicationLoader.applicationContext.getSystemService("clipboard");
        if (this.linkToCopy != null)
        {
          str = this.linkToCopy;
          localClipboardManager.setPrimaryClip(ClipData.newPlainText("label", str));
          Toast.makeText(paramContext, LocaleController.getString("LinkCopied", 2131165823), 0).show();
          return;
        }
      }
      catch (Exception paramContext)
      {
        FileLog.e("tmessages", paramContext);
        return;
      }
      String str = this.exportedMessageLink.link;
    }
  }
  
  private int getCurrentTop()
  {
    int j = 0;
    if (this.gridView.getChildCount() != 0)
    {
      View localView = this.gridView.getChildAt(0);
      Holder localHolder = (Holder)this.gridView.findContainingViewHolder(localView);
      if (localHolder != null)
      {
        int k = this.gridView.getPaddingTop();
        int i = j;
        if (localHolder.getAdapterPosition() == 0)
        {
          i = j;
          if (localView.getTop() >= 0) {
            i = localView.getTop();
          }
        }
        return k - i;
      }
    }
    return 64536;
  }
  
  @SuppressLint({"NewApi"})
  private void updateLayout()
  {
    int j = 0;
    if (this.gridView.getChildCount() <= 0) {}
    int i;
    do
    {
      return;
      localObject = this.gridView.getChildAt(0);
      Holder localHolder = (Holder)this.gridView.findContainingViewHolder((View)localObject);
      int k = ((View)localObject).getTop() - AndroidUtilities.dp(8.0F);
      i = j;
      if (k > 0)
      {
        i = j;
        if (localHolder != null)
        {
          i = j;
          if (localHolder.getAdapterPosition() == 0) {
            i = k;
          }
        }
      }
    } while (this.scrollOffsetY == i);
    Object localObject = this.gridView;
    this.scrollOffsetY = i;
    ((RecyclerListView)localObject).setTopGlowOffset(i);
    this.frameLayout.setTranslationY(this.scrollOffsetY);
    this.shadow.setTranslationY(this.scrollOffsetY);
    this.searchEmptyView.setTranslationY(this.scrollOffsetY);
    this.containerView.invalidate();
  }
  
  protected boolean canDismissWithSwipe()
  {
    return false;
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.dialogsNeedReload)
    {
      if (this.listAdapter != null) {
        this.listAdapter.fetchDialogs();
      }
      NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
    }
  }
  
  public void dismiss()
  {
    super.dismiss();
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.dialogsNeedReload);
  }
  
  public void updateSelectedCount()
  {
    if (this.selectedDialogs.isEmpty())
    {
      this.doneButtonBadgeTextView.setVisibility(8);
      if ((!this.isPublicChannel) && (this.linkToCopy == null))
      {
        this.doneButtonTextView.setTextColor(-5000269);
        this.doneButton.setEnabled(false);
        this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166233).toUpperCase());
        return;
      }
      this.doneButtonTextView.setTextColor(-12940081);
      this.doneButton.setEnabled(true);
      this.doneButtonTextView.setText(LocaleController.getString("CopyLink", 2131165532).toUpperCase());
      return;
    }
    this.doneButtonTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    this.doneButtonBadgeTextView.setVisibility(0);
    this.doneButtonBadgeTextView.setText(String.format("%d", new Object[] { Integer.valueOf(this.selectedDialogs.size()) }));
    this.doneButtonTextView.setTextColor(-12664327);
    this.doneButton.setEnabled(true);
    this.doneButtonTextView.setText(LocaleController.getString("Send", 2131166233).toUpperCase());
  }
  
  private class Holder
    extends RecyclerView.ViewHolder
  {
    public Holder(View paramView)
    {
      super();
    }
  }
  
  private class ShareDialogsAdapter
    extends RecyclerView.Adapter
  {
    private Context context;
    private int currentCount;
    private ArrayList<TLRPC.TL_dialog> dialogs = new ArrayList();
    
    public ShareDialogsAdapter(Context paramContext)
    {
      this.context = paramContext;
      fetchDialogs();
    }
    
    public void fetchDialogs()
    {
      this.dialogs.clear();
      int i = 0;
      if (i < MessagesController.getInstance().dialogsServerOnly.size())
      {
        TLRPC.TL_dialog localTL_dialog = (TLRPC.TL_dialog)MessagesController.getInstance().dialogsServerOnly.get(i);
        int j = (int)localTL_dialog.id;
        int k = (int)(localTL_dialog.id >> 32);
        if ((j != 0) && (k != 1))
        {
          if (j <= 0) {
            break label84;
          }
          this.dialogs.add(localTL_dialog);
        }
        for (;;)
        {
          i += 1;
          break;
          label84:
          TLRPC.Chat localChat = MessagesController.getInstance().getChat(Integer.valueOf(-j));
          if ((localChat != null) && (!ChatObject.isNotInChat(localChat)) && ((!ChatObject.isChannel(localChat)) || (localChat.creator) || (localChat.editor) || (localChat.megagroup))) {
            this.dialogs.add(localTL_dialog);
          }
        }
      }
      notifyDataSetChanged();
    }
    
    public TLRPC.TL_dialog getItem(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= this.dialogs.size())) {
        return null;
      }
      return (TLRPC.TL_dialog)this.dialogs.get(paramInt);
    }
    
    public int getItemCount()
    {
      return this.dialogs.size();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (ShareDialogCell)paramViewHolder.itemView;
      TLRPC.TL_dialog localTL_dialog = getItem(paramInt);
      paramViewHolder.setDialog((int)localTL_dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localTL_dialog.id)), null);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ShareDialogCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      return new ShareAlert.Holder(ShareAlert.this, paramViewGroup);
    }
  }
  
  public class ShareSearchAdapter
    extends RecyclerView.Adapter
  {
    private Context context;
    private int lastReqId;
    private int lastSearchId = 0;
    private String lastSearchText;
    private int reqId = 0;
    private ArrayList<DialogSearchResult> searchResult = new ArrayList();
    private Timer searchTimer;
    
    public ShareSearchAdapter(Context paramContext)
    {
      this.context = paramContext;
    }
    
    private void searchDialogsInternal(final String paramString, final int paramInt)
    {
      MessagesStorage.getInstance().getStorageQueue().postRunnable(new Runnable()
      {
        public void run()
        {
          int k;
          try
          {
            localObject4 = paramString.trim().toLowerCase();
            if (((String)localObject4).length() == 0)
            {
              ShareAlert.ShareSearchAdapter.access$2302(ShareAlert.ShareSearchAdapter.this, -1);
              ShareAlert.ShareSearchAdapter.this.updateSearchResults(new ArrayList(), ShareAlert.ShareSearchAdapter.this.lastSearchId);
              return;
            }
            localObject3 = LocaleController.getInstance().getTranslitString((String)localObject4);
            if (((String)localObject4).equals(localObject3)) {
              break label1615;
            }
            localObject1 = localObject3;
            if (((String)localObject3).length() != 0) {
              break label1618;
            }
          }
          catch (Exception localException)
          {
            Object localObject1;
            FileLog.e("tmessages", localException);
            return;
          }
          String[] arrayOfString = new String[i + 1];
          arrayOfString[0] = localObject4;
          if (localObject1 != null) {
            arrayOfString[1] = localObject1;
          }
          localObject1 = new ArrayList();
          Object localObject5 = new ArrayList();
          int i = 0;
          int j = 0;
          Object localObject4 = new HashMap();
          Object localObject3 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT did, date FROM dialogs ORDER BY date DESC LIMIT 400", new Object[0]);
          int m;
          for (;;)
          {
            if (!((SQLiteCursor)localObject3).next()) {
              break label310;
            }
            long l = ((SQLiteCursor)localObject3).longValue(0);
            localObject6 = new ShareAlert.ShareSearchAdapter.DialogSearchResult(ShareAlert.ShareSearchAdapter.this, null);
            ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).date = ((SQLiteCursor)localObject3).intValue(1);
            ((HashMap)localObject4).put(Long.valueOf(l), localObject6);
            k = (int)l;
            m = (int)(l >> 32);
            if ((k != 0) && (m != 1))
            {
              if (k > 0)
              {
                if (((ArrayList)localObject1).contains(Integer.valueOf(k))) {
                  continue;
                }
                ((ArrayList)localObject1).add(Integer.valueOf(k));
                continue;
                label274:
                i = 0;
                break;
              }
              m = -k;
              if (!((ArrayList)localObject5).contains(Integer.valueOf(m))) {
                ((ArrayList)localObject5).add(Integer.valueOf(-k));
              }
            }
          }
          label310:
          ((SQLiteCursor)localObject3).dispose();
          label362:
          String str1;
          label441:
          String str2;
          if (!localException.isEmpty())
          {
            localObject6 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, status, name FROM users WHERE uid IN(%s)", new Object[] { TextUtils.join(",", localException) }), new Object[0]);
            i = j;
            while (((SQLiteCursor)localObject6).next())
            {
              str1 = ((SQLiteCursor)localObject6).stringValue(2);
              localObject3 = LocaleController.getInstance().getTranslitString(str1);
              localObject2 = localObject3;
              if (str1.equals(localObject3)) {
                localObject2 = null;
              }
              localObject3 = null;
              j = str1.lastIndexOf(";;;");
              if (j != -1) {
                localObject3 = str1.substring(j + 3);
              }
              m = 0;
              int n = arrayOfString.length;
              k = 0;
              if (k >= n) {
                break label1641;
              }
              str2 = arrayOfString[k];
              if ((str1.startsWith(str2)) || (str1.contains(" " + str2))) {
                break label1628;
              }
              if (localObject2 != null)
              {
                if (((String)localObject2).startsWith(str2)) {
                  break label1628;
                }
                if (((String)localObject2).contains(" " + str2))
                {
                  break label1628;
                  label537:
                  if (j == 0) {
                    break label1633;
                  }
                  localObject3 = ((SQLiteCursor)localObject6).byteBufferValue(0);
                  if (localObject3 == null) {
                    continue;
                  }
                  localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                  ((NativeByteBuffer)localObject3).reuse();
                  localObject3 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)((HashMap)localObject4).get(Long.valueOf(((TLRPC.User)localObject2).id));
                  if (((TLRPC.User)localObject2).status != null) {
                    ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject6).intValue(1);
                  }
                  if (j != 1) {
                    break label693;
                  }
                }
              }
              label693:
              for (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2);; ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
              {
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).dialog.id = ((TLRPC.User)localObject2).id;
                i += 1;
                break;
                j = m;
                if (localObject3 == null) {
                  break label537;
                }
                j = m;
                if (!((String)localObject3).startsWith(str2)) {
                  break label537;
                }
                j = 2;
                break label537;
              }
            }
            ((SQLiteCursor)localObject6).dispose();
          }
          j = i;
          if (!((ArrayList)localObject5).isEmpty())
          {
            localObject5 = MessagesStorage.getInstance().getDatabase().queryFinalized(String.format(Locale.US, "SELECT data, name FROM chats WHERE uid IN(%s)", new Object[] { TextUtils.join(",", (Iterable)localObject5) }), new Object[0]);
            label803:
            while (((SQLiteCursor)localObject5).next())
            {
              localObject6 = ((SQLiteCursor)localObject5).stringValue(1);
              localObject3 = LocaleController.getInstance().getTranslitString((String)localObject6);
              localObject2 = localObject3;
              if (!((String)localObject6).equals(localObject3)) {
                break label1643;
              }
              localObject2 = null;
              break label1643;
              label849:
              if (j >= arrayOfString.length) {
                break label1653;
              }
              localObject3 = arrayOfString[j];
              if ((!((String)localObject6).startsWith((String)localObject3)) && (!((String)localObject6).contains(" " + (String)localObject3)) && ((localObject2 == null) || ((!((String)localObject2).startsWith((String)localObject3)) && (!((String)localObject2).contains(" " + (String)localObject3))))) {
                break label1648;
              }
              localObject6 = ((SQLiteCursor)localObject5).byteBufferValue(0);
              if (localObject6 != null)
              {
                localObject2 = TLRPC.Chat.TLdeserialize((AbstractSerializedData)localObject6, ((NativeByteBuffer)localObject6).readInt32(false), false);
                ((NativeByteBuffer)localObject6).reuse();
                if ((localObject2 != null) && (!ChatObject.isNotInChat((TLRPC.Chat)localObject2)) && ((!ChatObject.isChannel((TLRPC.Chat)localObject2)) || (((TLRPC.Chat)localObject2).creator) || (((TLRPC.Chat)localObject2).editor) || (((TLRPC.Chat)localObject2).megagroup)))
                {
                  localObject6 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)((HashMap)localObject4).get(Long.valueOf(-((TLRPC.Chat)localObject2).id));
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).name = AndroidUtilities.generateSearchName(((TLRPC.Chat)localObject2).title, null, (String)localObject3);
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).object = ((TLObject)localObject2);
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject6).dialog.id = (-((TLRPC.Chat)localObject2).id);
                  i += 1;
                }
              }
            }
            ((SQLiteCursor)localObject5).dispose();
            j = i;
          }
          localObject5 = new ArrayList(j);
          Object localObject2 = ((HashMap)localObject4).values().iterator();
          while (((Iterator)localObject2).hasNext())
          {
            localObject3 = (ShareAlert.ShareSearchAdapter.DialogSearchResult)((Iterator)localObject2).next();
            if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object != null) && (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name != null)) {
              ((ArrayList)localObject5).add(localObject3);
            }
          }
          Object localObject6 = MessagesStorage.getInstance().getDatabase().queryFinalized("SELECT u.data, u.status, u.name, u.uid FROM users as u INNER JOIN contacts as c ON u.uid = c.uid", new Object[0]);
          label1181:
          label1373:
          label1615:
          label1618:
          label1628:
          label1633:
          label1641:
          label1643:
          label1648:
          label1653:
          label1655:
          label1660:
          label1667:
          for (;;)
          {
            if (((SQLiteCursor)localObject6).next())
            {
              if (!((HashMap)localObject4).containsKey(Long.valueOf(((SQLiteCursor)localObject6).intValue(3))))
              {
                str1 = ((SQLiteCursor)localObject6).stringValue(2);
                localObject3 = LocaleController.getInstance().getTranslitString(str1);
                localObject2 = localObject3;
                if (str1.equals(localObject3)) {
                  localObject2 = null;
                }
                localObject3 = null;
                i = str1.lastIndexOf(";;;");
                if (i != -1) {
                  localObject3 = str1.substring(i + 3);
                }
                k = 0;
                m = arrayOfString.length;
                j = 0;
              }
            }
            else {
              for (;;)
              {
                if (j >= m) {
                  break label1667;
                }
                str2 = arrayOfString[j];
                if ((!str1.startsWith(str2)) && (!str1.contains(" " + str2))) {
                  if (localObject2 != null)
                  {
                    if (((String)localObject2).startsWith(str2)) {
                      break label1655;
                    }
                    if (((String)localObject2).contains(" " + str2)) {
                      break label1655;
                    }
                  }
                }
                for (;;)
                {
                  if (i == 0) {
                    break label1660;
                  }
                  localObject3 = ((SQLiteCursor)localObject6).byteBufferValue(0);
                  if (localObject3 == null) {
                    break label1181;
                  }
                  localObject2 = TLRPC.User.TLdeserialize((AbstractSerializedData)localObject3, ((NativeByteBuffer)localObject3).readInt32(false), false);
                  ((NativeByteBuffer)localObject3).reuse();
                  localObject3 = new ShareAlert.ShareSearchAdapter.DialogSearchResult(ShareAlert.ShareSearchAdapter.this, null);
                  if (((TLRPC.User)localObject2).status != null) {
                    ((TLRPC.User)localObject2).status.expires = ((SQLiteCursor)localObject6).intValue(1);
                  }
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).dialog.id = ((TLRPC.User)localObject2).id;
                  ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).object = ((TLObject)localObject2);
                  if (i == 1) {}
                  for (((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName(((TLRPC.User)localObject2).first_name, ((TLRPC.User)localObject2).last_name, str2);; ((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject3).name = AndroidUtilities.generateSearchName("@" + ((TLRPC.User)localObject2).username, null, "@" + str2))
                  {
                    ((ArrayList)localObject5).add(localObject3);
                    break;
                    i = k;
                    if (localObject3 == null) {
                      break label1373;
                    }
                    i = k;
                    if (!((String)localObject3).startsWith(str2)) {
                      break label1373;
                    }
                    i = 2;
                    break label1373;
                  }
                  ((SQLiteCursor)localObject6).dispose();
                  Collections.sort((List)localObject5, new Comparator()
                  {
                    public int compare(ShareAlert.ShareSearchAdapter.DialogSearchResult paramAnonymous2DialogSearchResult1, ShareAlert.ShareSearchAdapter.DialogSearchResult paramAnonymous2DialogSearchResult2)
                    {
                      if (paramAnonymous2DialogSearchResult1.date < paramAnonymous2DialogSearchResult2.date) {
                        return 1;
                      }
                      if (paramAnonymous2DialogSearchResult1.date > paramAnonymous2DialogSearchResult2.date) {
                        return -1;
                      }
                      return 0;
                    }
                  });
                  ShareAlert.ShareSearchAdapter.this.updateSearchResults((ArrayList)localObject5, paramInt);
                  return;
                  localObject2 = null;
                  if (localObject2 == null) {
                    break label274;
                  }
                  i = 1;
                  break;
                  j = 1;
                  break label537;
                  k += 1;
                  m = j;
                  break label441;
                  break label362;
                  j = 0;
                  break label849;
                  j += 1;
                  break label849;
                  break label803;
                  i = 1;
                }
                j += 1;
                k = i;
              }
            }
          }
        }
      });
    }
    
    private void updateSearchResults(final ArrayList<DialogSearchResult> paramArrayList, final int paramInt)
    {
      AndroidUtilities.runOnUIThread(new Runnable()
      {
        public void run()
        {
          if (paramInt != ShareAlert.ShareSearchAdapter.this.lastSearchId) {
            return;
          }
          int i = 0;
          if (i < paramArrayList.size())
          {
            Object localObject = (ShareAlert.ShareSearchAdapter.DialogSearchResult)paramArrayList.get(i);
            if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object instanceof TLRPC.User))
            {
              localObject = (TLRPC.User)((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object;
              MessagesController.getInstance().putUser((TLRPC.User)localObject, true);
            }
            for (;;)
            {
              i += 1;
              break;
              if ((((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object instanceof TLRPC.Chat))
              {
                localObject = (TLRPC.Chat)((ShareAlert.ShareSearchAdapter.DialogSearchResult)localObject).object;
                MessagesController.getInstance().putChat((TLRPC.Chat)localObject, true);
              }
            }
          }
          if ((!ShareAlert.ShareSearchAdapter.this.searchResult.isEmpty()) && (paramArrayList.isEmpty()))
          {
            i = 1;
            label128:
            if ((!ShareAlert.ShareSearchAdapter.this.searchResult.isEmpty()) || (!paramArrayList.isEmpty())) {
              break label263;
            }
          }
          label263:
          for (int j = 1;; j = 0)
          {
            if (i != 0) {
              ShareAlert.access$1802(ShareAlert.this, ShareAlert.this.getCurrentTop());
            }
            ShareAlert.ShareSearchAdapter.access$2602(ShareAlert.ShareSearchAdapter.this, paramArrayList);
            ShareAlert.ShareSearchAdapter.this.notifyDataSetChanged();
            if ((j != 0) || (i != 0) || (ShareAlert.this.topBeforeSwitch <= 0)) {
              break;
            }
            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -ShareAlert.this.topBeforeSwitch);
            ShareAlert.access$1802(ShareAlert.this, 64536);
            return;
            i = 0;
            break label128;
          }
        }
      });
    }
    
    public TLRPC.TL_dialog getItem(int paramInt)
    {
      return ((DialogSearchResult)this.searchResult.get(paramInt)).dialog;
    }
    
    public int getItemCount()
    {
      return this.searchResult.size();
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      paramViewHolder = (ShareDialogCell)paramViewHolder.itemView;
      DialogSearchResult localDialogSearchResult = (DialogSearchResult)this.searchResult.get(paramInt);
      paramViewHolder.setDialog((int)localDialogSearchResult.dialog.id, ShareAlert.this.selectedDialogs.containsKey(Long.valueOf(localDialogSearchResult.dialog.id)), localDialogSearchResult.name);
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = new ShareDialogCell(this.context);
      paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0F)));
      return new ShareAlert.Holder(ShareAlert.this, paramViewGroup);
    }
    
    public void searchDialogs(final String paramString)
    {
      if ((paramString != null) && (this.lastSearchText != null) && (paramString.equals(this.lastSearchText))) {
        return;
      }
      this.lastSearchText = paramString;
      try
      {
        if (this.searchTimer != null)
        {
          this.searchTimer.cancel();
          this.searchTimer = null;
        }
        if ((paramString == null) || (paramString.length() == 0))
        {
          this.searchResult.clear();
          ShareAlert.access$1802(ShareAlert.this, ShareAlert.this.getCurrentTop());
          notifyDataSetChanged();
          return;
        }
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e("tmessages", localException);
        }
        final int i = this.lastSearchId + 1;
        this.lastSearchId = i;
        this.searchTimer = new Timer();
        this.searchTimer.schedule(new TimerTask()
        {
          public void run()
          {
            try
            {
              cancel();
              ShareAlert.ShareSearchAdapter.this.searchTimer.cancel();
              ShareAlert.ShareSearchAdapter.access$2702(ShareAlert.ShareSearchAdapter.this, null);
              ShareAlert.ShareSearchAdapter.this.searchDialogsInternal(paramString, i);
              return;
            }
            catch (Exception localException)
            {
              for (;;)
              {
                FileLog.e("tmessages", localException);
              }
            }
          }
        }, 200L, 300L);
      }
    }
    
    private class DialogSearchResult
    {
      public int date;
      public TLRPC.TL_dialog dialog = new TLRPC.TL_dialog();
      public CharSequence name;
      public TLObject object;
      
      private DialogSearchResult() {}
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Components/ShareAlert.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
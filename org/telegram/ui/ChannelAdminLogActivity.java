package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DataQuery;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.exoplayer2.ui.AspectRatioFrameLayout;
import org.telegram.messenger.support.widget.LinearLayoutManager;
import org.telegram.messenger.support.widget.LinearSmoothScrollerMiddle;
import org.telegram.messenger.support.widget.RecyclerView;
import org.telegram.messenger.support.widget.RecyclerView.Adapter;
import org.telegram.messenger.support.widget.RecyclerView.LayoutParams;
import org.telegram.messenger.support.widget.RecyclerView.OnScrollListener;
import org.telegram.messenger.support.widget.RecyclerView.State;
import org.telegram.messenger.support.widget.RecyclerView.ViewHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.ChannelAdminLogEventAction;
import org.telegram.tgnet.TLRPC.ChannelParticipant;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.InputStickerSet;
import org.telegram.tgnet.TLRPC.KeyboardButton;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.tgnet.TLRPC.MessageMedia;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventActionChangeStickerSet;
import org.telegram.tgnet.TLRPC.TL_channelAdminLogEventsFilter;
import org.telegram.tgnet.TLRPC.TL_channelParticipantsAdmins;
import org.telegram.tgnet.TLRPC.TL_channels_adminLogResults;
import org.telegram.tgnet.TLRPC.TL_channels_channelParticipants;
import org.telegram.tgnet.TLRPC.TL_channels_getAdminLog;
import org.telegram.tgnet.TLRPC.TL_channels_getParticipants;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetEmpty;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetID;
import org.telegram.tgnet.TLRPC.TL_inputStickerSetShortName;
import org.telegram.tgnet.TLRPC.TL_messageMediaPhoto;
import org.telegram.tgnet.TLRPC.TL_messageMediaWebPage;
import org.telegram.tgnet.TLRPC.TL_replyInlineMarkup;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.tgnet.TLRPC.WebPage;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet.Builder;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Components.AdminLogFilterAlert;
import org.telegram.ui.Components.AdminLogFilterAlert.AdminLogFilterAlertDelegate;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.RecyclerListView.Holder;
import org.telegram.ui.Components.RecyclerListView.OnItemClickListener;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;

public class ChannelAdminLogActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate
{
  private ArrayList<TLRPC.ChannelParticipant> admins;
  private Paint aspectPaint;
  private Path aspectPath;
  private AspectRatioFrameLayout aspectRatioFrameLayout;
  private ChatAvatarContainer avatarContainer;
  private FrameLayout bottomOverlayChat;
  private TextView bottomOverlayChatText;
  private ImageView bottomOverlayImage;
  private ChatActivityAdapter chatAdapter;
  private LinearLayoutManager chatLayoutManager;
  private RecyclerListView chatListView;
  private ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList();
  private boolean checkTextureViewPosition;
  private SizeNotifierFrameLayout contentView;
  protected TLRPC.Chat currentChat;
  private TLRPC.TL_channelAdminLogEventsFilter currentFilter = null;
  private boolean currentFloatingDateOnScreen;
  private boolean currentFloatingTopIsNotMessage;
  private TextView emptyView;
  private FrameLayout emptyViewContainer;
  private boolean endReached;
  private AnimatorSet floatingDateAnimation;
  private ChatActionCell floatingDateView;
  private boolean loading;
  private int loadsCount;
  protected ArrayList<MessageObject> messages = new ArrayList();
  private HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap();
  private LongSparseArray<MessageObject> messagesDict = new LongSparseArray();
  private int[] mid = { 2 };
  private int minDate;
  private long minEventId;
  private boolean openAnimationEnded;
  private boolean paused = true;
  private RadialProgressView progressBar;
  private FrameLayout progressView;
  private View progressView2;
  private PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider()
  {
    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramAnonymousMessageObject, TLRPC.FileLocation paramAnonymousFileLocation, int paramAnonymousInt)
    {
      int i = ChannelAdminLogActivity.this.chatListView.getChildCount();
      paramAnonymousInt = 0;
      Object localObject1;
      View localView;
      Object localObject2;
      Object localObject3;
      MessageObject localMessageObject;
      label98:
      int j;
      if (paramAnonymousInt < i)
      {
        localObject1 = null;
        localView = ChannelAdminLogActivity.this.chatListView.getChildAt(paramAnonymousInt);
        if ((localView instanceof ChatMessageCell))
        {
          localObject2 = localObject1;
          if (paramAnonymousMessageObject != null)
          {
            localObject3 = (ChatMessageCell)localView;
            localMessageObject = ((ChatMessageCell)localObject3).getMessageObject();
            localObject2 = localObject1;
            if (localMessageObject != null)
            {
              localObject2 = localObject1;
              if (localMessageObject.getId() == paramAnonymousMessageObject.getId()) {
                localObject2 = ((ChatMessageCell)localObject3).getPhotoImage();
              }
            }
          }
          if (localObject2 == null) {
            break label367;
          }
          paramAnonymousFileLocation = new int[2];
          localView.getLocationInWindow(paramAnonymousFileLocation);
          paramAnonymousMessageObject = new PhotoViewer.PlaceProviderObject();
          paramAnonymousMessageObject.viewX = paramAnonymousFileLocation[0];
          j = paramAnonymousFileLocation[1];
          if (Build.VERSION.SDK_INT < 21) {
            break label360;
          }
          paramAnonymousInt = 0;
          label143:
          paramAnonymousMessageObject.viewY = (j - paramAnonymousInt);
          paramAnonymousMessageObject.parentView = ChannelAdminLogActivity.this.chatListView;
          paramAnonymousMessageObject.imageReceiver = ((ImageReceiver)localObject2);
          paramAnonymousMessageObject.thumb = ((ImageReceiver)localObject2).getBitmapSafe();
          paramAnonymousMessageObject.radius = ((ImageReceiver)localObject2).getRoundRadius();
          paramAnonymousMessageObject.isEvent = true;
        }
      }
      for (;;)
      {
        return paramAnonymousMessageObject;
        localObject2 = localObject1;
        if (!(localView instanceof ChatActionCell)) {
          break label98;
        }
        localObject3 = (ChatActionCell)localView;
        localMessageObject = ((ChatActionCell)localObject3).getMessageObject();
        localObject2 = localObject1;
        if (localMessageObject == null) {
          break label98;
        }
        if (paramAnonymousMessageObject != null)
        {
          localObject2 = localObject1;
          if (localMessageObject.getId() != paramAnonymousMessageObject.getId()) {
            break label98;
          }
          localObject2 = ((ChatActionCell)localObject3).getPhotoImage();
          break label98;
        }
        localObject2 = localObject1;
        if (paramAnonymousFileLocation == null) {
          break label98;
        }
        localObject2 = localObject1;
        if (localMessageObject.photoThumbs == null) {
          break label98;
        }
        for (j = 0;; j++)
        {
          localObject2 = localObject1;
          if (j >= localMessageObject.photoThumbs.size()) {
            break;
          }
          localObject2 = (TLRPC.PhotoSize)localMessageObject.photoThumbs.get(j);
          if ((((TLRPC.PhotoSize)localObject2).location.volume_id == paramAnonymousFileLocation.volume_id) && (((TLRPC.PhotoSize)localObject2).location.local_id == paramAnonymousFileLocation.local_id))
          {
            localObject2 = ((ChatActionCell)localObject3).getPhotoImage();
            break;
          }
        }
        label360:
        paramAnonymousInt = AndroidUtilities.statusBarHeight;
        break label143;
        label367:
        paramAnonymousInt++;
        break;
        paramAnonymousMessageObject = null;
      }
    }
  };
  private FrameLayout roundVideoContainer;
  private MessageObject scrollToMessage;
  private int scrollToOffsetOnRecreate = 0;
  private int scrollToPositionOnRecreate = -1;
  private boolean scrollingFloatingDate;
  private ImageView searchCalendarButton;
  private FrameLayout searchContainer;
  private SimpleTextView searchCountText;
  private ImageView searchDownButton;
  private ActionBarMenuItem searchItem;
  private String searchQuery = "";
  private ImageView searchUpButton;
  private boolean searchWas;
  private SparseArray<TLRPC.User> selectedAdmins;
  private MessageObject selectedObject;
  private TextureView videoTextureView;
  private boolean wasPaused = false;
  
  public ChannelAdminLogActivity(TLRPC.Chat paramChat)
  {
    this.currentChat = paramChat;
  }
  
  private void addCanBanUser(Bundle paramBundle, int paramInt)
  {
    if ((!this.currentChat.megagroup) || (this.admins == null) || (!ChatObject.canBlockUsers(this.currentChat))) {
      return;
    }
    for (int i = 0;; i++)
    {
      if (i < this.admins.size())
      {
        TLRPC.ChannelParticipant localChannelParticipant = (TLRPC.ChannelParticipant)this.admins.get(i);
        if (localChannelParticipant.user_id != paramInt) {
          continue;
        }
        if (!localChannelParticipant.can_edit) {
          break;
        }
      }
      paramBundle.putInt("ban_chat_id", this.currentChat.id);
      break;
    }
  }
  
  private void alertUserOpenError(MessageObject paramMessageObject)
  {
    if (getParentActivity() == null) {
      return;
    }
    AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
    localBuilder.setTitle(LocaleController.getString("AppName", NUM));
    localBuilder.setPositiveButton(LocaleController.getString("OK", NUM), null);
    if (paramMessageObject.type == 3) {
      localBuilder.setMessage(LocaleController.getString("NoPlayerInstalled", NUM));
    }
    for (;;)
    {
      showDialog(localBuilder.create());
      break;
      localBuilder.setMessage(LocaleController.formatString("NoHandleAppInstalled", NUM, new Object[] { paramMessageObject.getDocument().mime_type }));
    }
  }
  
  private void checkScrollForLoad(boolean paramBoolean)
  {
    if ((this.chatLayoutManager == null) || (this.paused)) {}
    label30:
    label92:
    label94:
    label97:
    for (;;)
    {
      return;
      int i = this.chatLayoutManager.findFirstVisibleItemPosition();
      if (i == -1)
      {
        j = 0;
        if (j <= 0) {
          break label92;
        }
        this.chatAdapter.getItemCount();
        if (!paramBoolean) {
          break label94;
        }
      }
      for (int j = 25;; j = 5)
      {
        if ((i > j) || (this.loading) || (this.endReached)) {
          break label97;
        }
        loadMessages(false);
        break;
        j = Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - i) + 1;
        break label30;
        break;
      }
    }
  }
  
  private void createMenu(View paramView)
  {
    Object localObject = null;
    if ((paramView instanceof ChatMessageCell))
    {
      localObject = ((ChatMessageCell)paramView).getMessageObject();
      if (localObject != null) {
        break label40;
      }
    }
    label21:
    label40:
    label370:
    label1148:
    for (;;)
    {
      return;
      int i;
      AlertDialog.Builder localBuilder;
      ArrayList localArrayList1;
      final ArrayList localArrayList2;
      if ((paramView instanceof ChatActionCell))
      {
        localObject = ((ChatActionCell)paramView).getMessageObject();
        break;
        i = getMessageType((MessageObject)localObject);
        this.selectedObject = ((MessageObject)localObject);
        if (getParentActivity() == null) {
          continue;
        }
        localBuilder = new AlertDialog.Builder(getParentActivity());
        localArrayList1 = new ArrayList();
        localArrayList2 = new ArrayList();
        if ((this.selectedObject.type == 0) || (this.selectedObject.caption != null))
        {
          localArrayList1.add(LocaleController.getString("Copy", NUM));
          localArrayList2.add(Integer.valueOf(3));
        }
        if (i == 1)
        {
          if ((this.selectedObject.currentEvent != null) && ((this.selectedObject.currentEvent.action instanceof TLRPC.TL_channelAdminLogEventActionChangeStickerSet)))
          {
            localObject = this.selectedObject.currentEvent.action.new_stickerset;
            if (localObject != null)
            {
              paramView = (View)localObject;
              if (!(localObject instanceof TLRPC.TL_inputStickerSetEmpty)) {}
            }
            else
            {
              paramView = this.selectedObject.currentEvent.action.prev_stickerset;
            }
            if (paramView != null) {
              showDialog(new StickersAlert(getParentActivity(), this, paramView, null, null));
            }
          }
        }
        else
        {
          if (i != 3) {
            break label370;
          }
          if (((this.selectedObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (MessageObject.isNewGifDocument(this.selectedObject.messageOwner.media.webpage.document)))
          {
            localArrayList1.add(LocaleController.getString("SaveToGIFs", NUM));
            localArrayList2.add(Integer.valueOf(11));
          }
        }
      }
      for (;;)
      {
        if (localArrayList2.isEmpty()) {
          break label1148;
        }
        localBuilder.setItems((CharSequence[])localArrayList1.toArray(new CharSequence[localArrayList1.size()]), new DialogInterface.OnClickListener()
        {
          public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
          {
            if ((ChannelAdminLogActivity.this.selectedObject == null) || (paramAnonymousInt < 0) || (paramAnonymousInt >= localArrayList2.size())) {}
            for (;;)
            {
              return;
              ChannelAdminLogActivity.this.processSelectedOption(((Integer)localArrayList2.get(paramAnonymousInt)).intValue());
            }
          }
        });
        localBuilder.setTitle(LocaleController.getString("Message", NUM));
        showDialog(localBuilder.create());
        break label21;
        break;
        if (i == 4)
        {
          if (this.selectedObject.isVideo())
          {
            localArrayList1.add(LocaleController.getString("SaveToGallery", NUM));
            localArrayList2.add(Integer.valueOf(4));
            localArrayList1.add(LocaleController.getString("ShareFile", NUM));
            localArrayList2.add(Integer.valueOf(6));
          }
          else if (this.selectedObject.isMusic())
          {
            localArrayList1.add(LocaleController.getString("SaveToMusic", NUM));
            localArrayList2.add(Integer.valueOf(10));
            localArrayList1.add(LocaleController.getString("ShareFile", NUM));
            localArrayList2.add(Integer.valueOf(6));
          }
          else if (this.selectedObject.getDocument() != null)
          {
            if (MessageObject.isNewGifDocument(this.selectedObject.getDocument()))
            {
              localArrayList1.add(LocaleController.getString("SaveToGIFs", NUM));
              localArrayList2.add(Integer.valueOf(11));
            }
            localArrayList1.add(LocaleController.getString("SaveToDownloads", NUM));
            localArrayList2.add(Integer.valueOf(10));
            localArrayList1.add(LocaleController.getString("ShareFile", NUM));
            localArrayList2.add(Integer.valueOf(6));
          }
          else
          {
            localArrayList1.add(LocaleController.getString("SaveToGallery", NUM));
            localArrayList2.add(Integer.valueOf(4));
          }
        }
        else if (i == 5)
        {
          localArrayList1.add(LocaleController.getString("ApplyLocalizationFile", NUM));
          localArrayList2.add(Integer.valueOf(5));
          localArrayList1.add(LocaleController.getString("SaveToDownloads", NUM));
          localArrayList2.add(Integer.valueOf(10));
          localArrayList1.add(LocaleController.getString("ShareFile", NUM));
          localArrayList2.add(Integer.valueOf(6));
        }
        else if (i == 10)
        {
          localArrayList1.add(LocaleController.getString("ApplyThemeFile", NUM));
          localArrayList2.add(Integer.valueOf(5));
          localArrayList1.add(LocaleController.getString("SaveToDownloads", NUM));
          localArrayList2.add(Integer.valueOf(10));
          localArrayList1.add(LocaleController.getString("ShareFile", NUM));
          localArrayList2.add(Integer.valueOf(6));
        }
        else if (i == 6)
        {
          localArrayList1.add(LocaleController.getString("SaveToGallery", NUM));
          localArrayList2.add(Integer.valueOf(7));
          localArrayList1.add(LocaleController.getString("SaveToDownloads", NUM));
          localArrayList2.add(Integer.valueOf(10));
          localArrayList1.add(LocaleController.getString("ShareFile", NUM));
          localArrayList2.add(Integer.valueOf(6));
        }
        else
        {
          if (i == 7)
          {
            if (this.selectedObject.isMask()) {
              localArrayList1.add(LocaleController.getString("AddToMasks", NUM));
            }
            for (;;)
            {
              localArrayList2.add(Integer.valueOf(9));
              break;
              localArrayList1.add(LocaleController.getString("AddToStickers", NUM));
            }
          }
          if (i == 8)
          {
            paramView = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.selectedObject.messageOwner.media.user_id));
            if ((paramView != null) && (paramView.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) && (ContactsController.getInstance(this.currentAccount).contactsDict.get(Integer.valueOf(paramView.id)) == null))
            {
              localArrayList1.add(LocaleController.getString("AddContactTitle", NUM));
              localArrayList2.add(Integer.valueOf(15));
            }
            if ((this.selectedObject.messageOwner.media.phone_number != null) || (this.selectedObject.messageOwner.media.phone_number.length() != 0))
            {
              localArrayList1.add(LocaleController.getString("Copy", NUM));
              localArrayList2.add(Integer.valueOf(16));
              localArrayList1.add(LocaleController.getString("Call", NUM));
              localArrayList2.add(Integer.valueOf(17));
            }
          }
        }
      }
    }
  }
  
  private TextureView createTextureView(boolean paramBoolean)
  {
    TextureView localTextureView;
    if (this.parentLayout == null)
    {
      localTextureView = null;
      return localTextureView;
    }
    if (this.roundVideoContainer == null)
    {
      if (Build.VERSION.SDK_INT < 21) {
        break label226;
      }
      this.roundVideoContainer = new FrameLayout(getParentActivity())
      {
        public void setTranslationY(float paramAnonymousFloat)
        {
          super.setTranslationY(paramAnonymousFloat);
          ChannelAdminLogActivity.this.contentView.invalidate();
        }
      };
      this.roundVideoContainer.setOutlineProvider(new ViewOutlineProvider()
      {
        @TargetApi(21)
        public void getOutline(View paramAnonymousView, Outline paramAnonymousOutline)
        {
          paramAnonymousOutline.setOval(0, 0, AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize);
        }
      });
      this.roundVideoContainer.setClipToOutline(true);
    }
    for (;;)
    {
      this.roundVideoContainer.setWillNotDraw(false);
      this.roundVideoContainer.setVisibility(4);
      this.aspectRatioFrameLayout = new AspectRatioFrameLayout(getParentActivity());
      this.aspectRatioFrameLayout.setBackgroundColor(0);
      if (paramBoolean) {
        this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0F));
      }
      this.videoTextureView = new TextureView(getParentActivity());
      this.videoTextureView.setOpaque(false);
      this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0F));
      if (this.roundVideoContainer.getParent() == null) {
        this.contentView.addView(this.roundVideoContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundMessageSize, AndroidUtilities.roundMessageSize));
      }
      this.roundVideoContainer.setVisibility(4);
      this.aspectRatioFrameLayout.setDrawingReady(false);
      localTextureView = this.videoTextureView;
      break;
      label226:
      this.roundVideoContainer = new FrameLayout(getParentActivity())
      {
        protected void dispatchDraw(Canvas paramAnonymousCanvas)
        {
          super.dispatchDraw(paramAnonymousCanvas);
          paramAnonymousCanvas.drawPath(ChannelAdminLogActivity.this.aspectPath, ChannelAdminLogActivity.this.aspectPaint);
        }
        
        protected void onSizeChanged(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          super.onSizeChanged(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousInt3, paramAnonymousInt4);
          ChannelAdminLogActivity.this.aspectPath.reset();
          ChannelAdminLogActivity.this.aspectPath.addCircle(paramAnonymousInt1 / 2, paramAnonymousInt2 / 2, paramAnonymousInt1 / 2, Path.Direction.CW);
          ChannelAdminLogActivity.this.aspectPath.toggleInverseFillType();
        }
        
        public void setTranslationY(float paramAnonymousFloat)
        {
          super.setTranslationY(paramAnonymousFloat);
          ChannelAdminLogActivity.this.contentView.invalidate();
        }
        
        public void setVisibility(int paramAnonymousInt)
        {
          super.setVisibility(paramAnonymousInt);
          if (paramAnonymousInt == 0) {
            setLayerType(2, null);
          }
        }
      };
      this.aspectPath = new Path();
      this.aspectPaint = new Paint(1);
      this.aspectPaint.setColor(-16777216);
      this.aspectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }
  }
  
  private void destroyTextureView()
  {
    if ((this.roundVideoContainer == null) || (this.roundVideoContainer.getParent() == null)) {}
    for (;;)
    {
      return;
      this.contentView.removeView(this.roundVideoContainer);
      this.aspectRatioFrameLayout.setDrawingReady(false);
      this.roundVideoContainer.setVisibility(4);
      if (Build.VERSION.SDK_INT < 21) {
        this.roundVideoContainer.setLayerType(0, null);
      }
    }
  }
  
  private void fixLayout()
  {
    if (this.avatarContainer != null) {
      this.avatarContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          if (ChannelAdminLogActivity.this.avatarContainer != null) {
            ChannelAdminLogActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
          }
          return true;
        }
      });
    }
  }
  
  private String getMessageContent(MessageObject paramMessageObject, int paramInt, boolean paramBoolean)
  {
    String str1 = "";
    String str2 = str1;
    Object localObject;
    if (paramBoolean)
    {
      str2 = str1;
      if (paramInt != paramMessageObject.messageOwner.from_id)
      {
        if (paramMessageObject.messageOwner.from_id <= 0) {
          break label147;
        }
        localObject = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(paramMessageObject.messageOwner.from_id));
        str2 = str1;
        if (localObject != null) {
          str2 = ContactsController.formatName(((TLRPC.User)localObject).first_name, ((TLRPC.User)localObject).last_name) + ":\n";
        }
      }
    }
    if ((paramMessageObject.type == 0) && (paramMessageObject.messageOwner.message != null)) {
      paramMessageObject = str2 + paramMessageObject.messageOwner.message;
    }
    for (;;)
    {
      return paramMessageObject;
      label147:
      str2 = str1;
      if (paramMessageObject.messageOwner.from_id >= 0) {
        break;
      }
      localObject = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-paramMessageObject.messageOwner.from_id));
      str2 = str1;
      if (localObject == null) {
        break;
      }
      str2 = ((TLRPC.Chat)localObject).title + ":\n";
      break;
      if ((paramMessageObject.messageOwner.media != null) && (paramMessageObject.messageOwner.message != null)) {
        paramMessageObject = str2 + paramMessageObject.messageOwner.message;
      } else {
        paramMessageObject = str2 + paramMessageObject.messageText;
      }
    }
  }
  
  private int getMessageType(MessageObject paramMessageObject)
  {
    int i = -1;
    int j;
    if (paramMessageObject == null) {
      j = i;
    }
    for (;;)
    {
      return j;
      j = i;
      if (paramMessageObject.type != 6) {
        if ((paramMessageObject.type == 10) || (paramMessageObject.type == 11) || (paramMessageObject.type == 16))
        {
          j = i;
          if (paramMessageObject.getId() != 0) {
            j = 1;
          }
        }
        else if (paramMessageObject.isVoice())
        {
          j = 2;
        }
        else
        {
          if (paramMessageObject.isSticker())
          {
            paramMessageObject = paramMessageObject.getInputStickerSet();
            if ((paramMessageObject instanceof TLRPC.TL_inputStickerSetID))
            {
              if (!DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(paramMessageObject.id)) {
                j = 7;
              }
            }
            else if (((paramMessageObject instanceof TLRPC.TL_inputStickerSetShortName)) && (!DataQuery.getInstance(this.currentAccount).isStickerPackInstalled(paramMessageObject.short_name))) {
              j = 7;
            }
          }
          else if (((!paramMessageObject.isRoundVideo()) || ((paramMessageObject.isRoundVideo()) && (BuildVars.DEBUG_VERSION))) && (((paramMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto)) || (paramMessageObject.getDocument() != null) || (paramMessageObject.isMusic()) || (paramMessageObject.isVideo())))
          {
            i = 0;
            j = i;
            if (paramMessageObject.messageOwner.attachPath != null)
            {
              j = i;
              if (paramMessageObject.messageOwner.attachPath.length() != 0)
              {
                j = i;
                if (new File(paramMessageObject.messageOwner.attachPath).exists()) {
                  j = 1;
                }
              }
            }
            i = j;
            if (j == 0)
            {
              i = j;
              if (FileLoader.getPathToMessage(paramMessageObject.messageOwner).exists()) {
                i = 1;
              }
            }
            if (i != 0)
            {
              if (paramMessageObject.getDocument() != null)
              {
                String str = paramMessageObject.getDocument().mime_type;
                if (str != null)
                {
                  if (paramMessageObject.getDocumentName().toLowerCase().endsWith("attheme"))
                  {
                    j = 10;
                    continue;
                  }
                  if (str.endsWith("/xml"))
                  {
                    j = 5;
                    continue;
                  }
                  if ((str.endsWith("/png")) || (str.endsWith("/jpg")) || (str.endsWith("/jpeg")))
                  {
                    j = 6;
                    continue;
                  }
                }
              }
              j = 4;
            }
          }
          else
          {
            if (paramMessageObject.type == 12)
            {
              j = 8;
              continue;
            }
            if (paramMessageObject.isMediaEmpty())
            {
              j = 3;
              continue;
            }
          }
          j = 2;
        }
      }
    }
  }
  
  private void hideFloatingDateView(boolean paramBoolean)
  {
    if ((this.floatingDateView.getTag() != null) && (!this.currentFloatingDateOnScreen) && ((!this.scrollingFloatingDate) || (this.currentFloatingTopIsNotMessage)))
    {
      this.floatingDateView.setTag(null);
      if (!paramBoolean) {
        break label129;
      }
      this.floatingDateAnimation = new AnimatorSet();
      this.floatingDateAnimation.setDuration(150L);
      this.floatingDateAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(this.floatingDateView, "alpha", new float[] { 0.0F }) });
      this.floatingDateAnimation.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (paramAnonymousAnimator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
            ChannelAdminLogActivity.access$4502(ChannelAdminLogActivity.this, null);
          }
        }
      });
      this.floatingDateAnimation.setStartDelay(500L);
      this.floatingDateAnimation.start();
    }
    for (;;)
    {
      return;
      label129:
      if (this.floatingDateAnimation != null)
      {
        this.floatingDateAnimation.cancel();
        this.floatingDateAnimation = null;
      }
      this.floatingDateView.setAlpha(0.0F);
    }
  }
  
  private void loadAdmins()
  {
    TLRPC.TL_channels_getParticipants localTL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
    localTL_channels_getParticipants.channel = MessagesController.getInputChannel(this.currentChat);
    localTL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
    localTL_channels_getParticipants.offset = 0;
    localTL_channels_getParticipants.limit = 200;
    int i = ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_getParticipants, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, final TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            if (paramAnonymousTL_error == null)
            {
              TLRPC.TL_channels_channelParticipants localTL_channels_channelParticipants = (TLRPC.TL_channels_channelParticipants)paramAnonymousTLObject;
              MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putUsers(localTL_channels_channelParticipants.users, false);
              ChannelAdminLogActivity.access$4902(ChannelAdminLogActivity.this, localTL_channels_channelParticipants.participants);
              if ((ChannelAdminLogActivity.this.visibleDialog instanceof AdminLogFilterAlert)) {
                ((AdminLogFilterAlert)ChannelAdminLogActivity.this.visibleDialog).setCurrentAdmins(ChannelAdminLogActivity.this.admins);
              }
            }
          }
        });
      }
    });
    ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(i, this.classGuid);
  }
  
  private void loadMessages(boolean paramBoolean)
  {
    if (this.loading) {}
    for (;;)
    {
      return;
      if (paramBoolean)
      {
        this.minEventId = Long.MAX_VALUE;
        if (this.progressView != null)
        {
          this.progressView.setVisibility(0);
          this.emptyViewContainer.setVisibility(4);
          this.chatListView.setEmptyView(null);
        }
        this.messagesDict.clear();
        this.messages.clear();
        this.messagesByDays.clear();
      }
      this.loading = true;
      TLRPC.TL_channels_getAdminLog localTL_channels_getAdminLog = new TLRPC.TL_channels_getAdminLog();
      localTL_channels_getAdminLog.channel = MessagesController.getInputChannel(this.currentChat);
      localTL_channels_getAdminLog.q = this.searchQuery;
      localTL_channels_getAdminLog.limit = 50;
      if ((!paramBoolean) && (!this.messages.isEmpty())) {}
      for (localTL_channels_getAdminLog.max_id = this.minEventId;; localTL_channels_getAdminLog.max_id = 0L)
      {
        localTL_channels_getAdminLog.min_id = 0L;
        if (this.currentFilter != null)
        {
          localTL_channels_getAdminLog.flags |= 0x1;
          localTL_channels_getAdminLog.events_filter = this.currentFilter;
        }
        if (this.selectedAdmins == null) {
          break;
        }
        localTL_channels_getAdminLog.flags |= 0x2;
        for (int i = 0; i < this.selectedAdmins.size(); i++) {
          localTL_channels_getAdminLog.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser((TLRPC.User)this.selectedAdmins.valueAt(i)));
        }
      }
      updateEmptyPlaceholder();
      ConnectionsManager.getInstance(this.currentAccount).sendRequest(localTL_channels_getAdminLog, new RequestDelegate()
      {
        public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
        {
          if (paramAnonymousTLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable()
            {
              public void run()
              {
                MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putUsers(paramAnonymousTLObject.users, false);
                MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).putChats(paramAnonymousTLObject.chats, false);
                int i = 0;
                int j = ChannelAdminLogActivity.this.messages.size();
                int k = 0;
                Object localObject;
                int m;
                if (k < paramAnonymousTLObject.events.size())
                {
                  localObject = (TLRPC.TL_channelAdminLogEvent)paramAnonymousTLObject.events.get(k);
                  if (ChannelAdminLogActivity.this.messagesDict.indexOfKey(((TLRPC.TL_channelAdminLogEvent)localObject).id) >= 0) {}
                  for (;;)
                  {
                    k++;
                    break;
                    ChannelAdminLogActivity.access$402(ChannelAdminLogActivity.this, Math.min(ChannelAdminLogActivity.this.minEventId, ((TLRPC.TL_channelAdminLogEvent)localObject).id));
                    m = 1;
                    MessageObject localMessageObject = new MessageObject(ChannelAdminLogActivity.this.currentAccount, (TLRPC.TL_channelAdminLogEvent)localObject, ChannelAdminLogActivity.this.messages, ChannelAdminLogActivity.this.messagesByDays, ChannelAdminLogActivity.this.currentChat, ChannelAdminLogActivity.this.mid);
                    i = m;
                    if (localMessageObject.contentType >= 0)
                    {
                      ChannelAdminLogActivity.this.messagesDict.put(((TLRPC.TL_channelAdminLogEvent)localObject).id, localMessageObject);
                      i = m;
                    }
                  }
                }
                j = ChannelAdminLogActivity.this.messages.size() - j;
                ChannelAdminLogActivity.access$802(ChannelAdminLogActivity.this, false);
                if (i == 0) {
                  ChannelAdminLogActivity.access$902(ChannelAdminLogActivity.this, true);
                }
                ChannelAdminLogActivity.this.progressView.setVisibility(4);
                ChannelAdminLogActivity.this.chatListView.setEmptyView(ChannelAdminLogActivity.this.emptyViewContainer);
                if (j != 0)
                {
                  k = 0;
                  if (ChannelAdminLogActivity.this.endReached)
                  {
                    k = 1;
                    ChannelAdminLogActivity.this.chatAdapter.notifyItemRangeChanged(0, 2);
                  }
                  int n = ChannelAdminLogActivity.this.chatLayoutManager.findLastVisibleItemPosition();
                  localObject = ChannelAdminLogActivity.this.chatLayoutManager.findViewByPosition(n);
                  if (localObject == null)
                  {
                    i = 0;
                    int i1 = ChannelAdminLogActivity.this.chatListView.getPaddingTop();
                    if (k == 0) {
                      break label545;
                    }
                    m = 1;
                    label431:
                    if (j - m > 0)
                    {
                      if (k == 0) {
                        break label551;
                      }
                      m = 0;
                      label445:
                      int i2 = m + 1;
                      ChannelAdminLogActivity.this.chatAdapter.notifyItemChanged(i2);
                      localObject = ChannelAdminLogActivity.this.chatAdapter;
                      if (k == 0) {
                        break label557;
                      }
                      m = 1;
                      label485:
                      ((ChannelAdminLogActivity.ChatActivityAdapter)localObject).notifyItemRangeInserted(i2, j - m);
                    }
                    if (n != -1)
                    {
                      localObject = ChannelAdminLogActivity.this.chatLayoutManager;
                      if (k == 0) {
                        break label563;
                      }
                      k = 1;
                      label520:
                      ((LinearLayoutManager)localObject).scrollToPositionWithOffset(n + j - k, i - i1);
                    }
                  }
                }
                for (;;)
                {
                  return;
                  i = ((View)localObject).getTop();
                  break;
                  label545:
                  m = 0;
                  break label431;
                  label551:
                  m = 1;
                  break label445;
                  label557:
                  m = 0;
                  break label485;
                  label563:
                  k = 0;
                  break label520;
                  if (ChannelAdminLogActivity.this.endReached) {
                    ChannelAdminLogActivity.this.chatAdapter.notifyItemRemoved(0);
                  }
                }
              }
            });
          }
        }
      });
      if ((paramBoolean) && (this.chatAdapter != null)) {
        this.chatAdapter.notifyDataSetChanged();
      }
    }
  }
  
  private void moveScrollToLastMessage()
  {
    if ((this.chatListView != null) && (!this.messages.isEmpty())) {
      this.chatLayoutManager.scrollToPositionWithOffset(this.messages.size() - 1, -100000 - this.chatListView.getPaddingTop());
    }
  }
  
  private void processSelectedOption(int paramInt)
  {
    if (this.selectedObject == null) {
      return;
    }
    switch (paramInt)
    {
    }
    for (;;)
    {
      this.selectedObject = null;
      break;
      AndroidUtilities.addToClipboard(getMessageContent(this.selectedObject, 0, true));
      continue;
      Object localObject1 = this.selectedObject.messageOwner.attachPath;
      Object localObject2 = localObject1;
      if (localObject1 != null)
      {
        localObject2 = localObject1;
        if (((String)localObject1).length() > 0)
        {
          localObject2 = localObject1;
          if (!new File((String)localObject1).exists()) {
            localObject2 = null;
          }
        }
      }
      if (localObject2 != null)
      {
        localObject1 = localObject2;
        if (((String)localObject2).length() != 0) {}
      }
      else
      {
        localObject1 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
      }
      if ((this.selectedObject.type == 3) || (this.selectedObject.type == 1))
      {
        if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
        {
          getParentActivity().requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
          this.selectedObject = null;
          break;
        }
        localObject2 = getParentActivity();
        if (this.selectedObject.type == 3) {}
        for (paramInt = 1;; paramInt = 0)
        {
          MediaController.saveFile((String)localObject1, (Context)localObject2, paramInt, null, null);
          break;
        }
        Object localObject3 = null;
        localObject2 = localObject3;
        if (this.selectedObject.messageOwner.attachPath != null)
        {
          localObject2 = localObject3;
          if (this.selectedObject.messageOwner.attachPath.length() != 0)
          {
            localObject1 = new File(this.selectedObject.messageOwner.attachPath);
            localObject2 = localObject3;
            if (((File)localObject1).exists()) {
              localObject2 = localObject1;
            }
          }
        }
        localObject1 = localObject2;
        if (localObject2 == null)
        {
          localObject3 = FileLoader.getPathToMessage(this.selectedObject.messageOwner);
          localObject1 = localObject2;
          if (((File)localObject3).exists()) {
            localObject1 = localObject3;
          }
        }
        if (localObject1 != null)
        {
          if (((File)localObject1).getName().toLowerCase().endsWith("attheme"))
          {
            if (this.chatLayoutManager != null)
            {
              if (this.chatLayoutManager.findLastVisibleItemPosition() >= this.chatLayoutManager.getItemCount() - 1) {
                break label510;
              }
              this.scrollToPositionOnRecreate = this.chatLayoutManager.findFirstVisibleItemPosition();
              localObject2 = (RecyclerListView.Holder)this.chatListView.findViewHolderForAdapterPosition(this.scrollToPositionOnRecreate);
              if (localObject2 == null) {
                break label502;
              }
              this.scrollToOffsetOnRecreate = ((RecyclerListView.Holder)localObject2).itemView.getTop();
            }
            for (;;)
            {
              localObject2 = Theme.applyThemeFile((File)localObject1, this.selectedObject.getDocumentName(), true);
              if (localObject2 == null) {
                break label518;
              }
              presentFragment(new ThemePreviewActivity((File)localObject1, (Theme.ThemeInfo)localObject2));
              break;
              label502:
              this.scrollToPositionOnRecreate = -1;
              continue;
              label510:
              this.scrollToPositionOnRecreate = -1;
            }
            label518:
            this.scrollToPositionOnRecreate = -1;
            if (getParentActivity() == null)
            {
              this.selectedObject = null;
              break;
            }
            localObject2 = new AlertDialog.Builder(getParentActivity());
            ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", NUM));
            ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("IncorrectTheme", NUM));
            ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(((AlertDialog.Builder)localObject2).create());
            continue;
          }
          if (LocaleController.getInstance().applyLanguageFile((File)localObject1, this.currentAccount))
          {
            presentFragment(new LanguageSelectActivity());
          }
          else
          {
            if (getParentActivity() == null)
            {
              this.selectedObject = null;
              break;
            }
            localObject2 = new AlertDialog.Builder(getParentActivity());
            ((AlertDialog.Builder)localObject2).setTitle(LocaleController.getString("AppName", NUM));
            ((AlertDialog.Builder)localObject2).setMessage(LocaleController.getString("IncorrectLocalization", NUM));
            ((AlertDialog.Builder)localObject2).setPositiveButton(LocaleController.getString("OK", NUM), null);
            showDialog(((AlertDialog.Builder)localObject2).create());
            continue;
            localObject1 = this.selectedObject.messageOwner.attachPath;
            localObject2 = localObject1;
            if (localObject1 != null)
            {
              localObject2 = localObject1;
              if (((String)localObject1).length() > 0)
              {
                localObject2 = localObject1;
                if (!new File((String)localObject1).exists()) {
                  localObject2 = null;
                }
              }
            }
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (((String)localObject2).length() != 0) {}
            }
            else
            {
              localObject1 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
            }
            localObject2 = new Intent("android.intent.action.SEND");
            ((Intent)localObject2).setType(this.selectedObject.getDocument().mime_type);
            if (Build.VERSION.SDK_INT >= 24) {}
            for (;;)
            {
              try
              {
                localObject3 = getParentActivity();
                localObject5 = new java/io/File;
                ((File)localObject5).<init>((String)localObject1);
                ((Intent)localObject2).putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile((Context)localObject3, "org.telegram.messenger.beta.provider", (File)localObject5));
                ((Intent)localObject2).setFlags(1);
                getParentActivity().startActivityForResult(Intent.createChooser((Intent)localObject2, LocaleController.getString("ShareFile", NUM)), 500);
              }
              catch (Exception localException2)
              {
                ((Intent)localObject2).putExtra("android.intent.extra.STREAM", Uri.fromFile(new File((String)localObject1)));
                continue;
              }
              ((Intent)localObject2).putExtra("android.intent.extra.STREAM", Uri.fromFile(new File((String)localObject1)));
            }
            localObject1 = this.selectedObject.messageOwner.attachPath;
            localObject2 = localObject1;
            if (localObject1 != null)
            {
              localObject2 = localObject1;
              if (((String)localObject1).length() > 0)
              {
                localObject2 = localObject1;
                if (!new File((String)localObject1).exists()) {
                  localObject2 = null;
                }
              }
            }
            if (localObject2 != null)
            {
              localObject1 = localObject2;
              if (((String)localObject2).length() != 0) {}
            }
            else
            {
              localObject1 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
            }
            if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
            {
              getParentActivity().requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
              this.selectedObject = null;
              break;
            }
            MediaController.saveFile((String)localObject1, getParentActivity(), 0, null, null);
            continue;
            showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), null, null));
            continue;
            if ((Build.VERSION.SDK_INT >= 23) && (getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0))
            {
              getParentActivity().requestPermissions(new String[] { "android.permission.WRITE_EXTERNAL_STORAGE" }, 4);
              this.selectedObject = null;
              break;
            }
            localObject2 = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
            localObject1 = localObject2;
            if (TextUtils.isEmpty((CharSequence)localObject2)) {
              localObject1 = this.selectedObject.getFileName();
            }
            Object localObject4 = this.selectedObject.messageOwner.attachPath;
            localObject2 = localObject4;
            if (localObject4 != null)
            {
              localObject2 = localObject4;
              if (((String)localObject4).length() > 0)
              {
                localObject2 = localObject4;
                if (!new File((String)localObject4).exists()) {
                  localObject2 = null;
                }
              }
            }
            if (localObject2 != null)
            {
              localObject4 = localObject2;
              if (((String)localObject2).length() != 0) {}
            }
            else
            {
              localObject4 = FileLoader.getPathToMessage(this.selectedObject.messageOwner).toString();
            }
            Object localObject5 = getParentActivity();
            if (this.selectedObject.isMusic())
            {
              paramInt = 3;
              label1268:
              if (this.selectedObject.getDocument() == null) {
                break label1307;
              }
            }
            label1307:
            for (localObject2 = this.selectedObject.getDocument().mime_type;; localObject2 = "")
            {
              MediaController.saveFile((String)localObject4, (Context)localObject5, paramInt, (String)localObject1, (String)localObject2);
              break;
              paramInt = 2;
              break label1268;
            }
            localObject2 = this.selectedObject.getDocument();
            MessagesController.getInstance(this.currentAccount).saveGif((TLRPC.Document)localObject2);
            continue;
            localObject2 = new Bundle();
            ((Bundle)localObject2).putInt("user_id", this.selectedObject.messageOwner.media.user_id);
            ((Bundle)localObject2).putString("phone", this.selectedObject.messageOwner.media.phone_number);
            ((Bundle)localObject2).putBoolean("addContact", true);
            presentFragment(new ContactAddActivity((Bundle)localObject2));
            continue;
            AndroidUtilities.addToClipboard(this.selectedObject.messageOwner.media.phone_number);
            continue;
            try
            {
              localObject2 = new android/content/Intent;
              localObject1 = new java/lang/StringBuilder;
              ((StringBuilder)localObject1).<init>();
              ((Intent)localObject2).<init>("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
              ((Intent)localObject2).addFlags(268435456);
              getParentActivity().startActivityForResult((Intent)localObject2, 500);
            }
            catch (Exception localException1)
            {
              FileLog.e(localException1);
            }
          }
        }
      }
    }
  }
  
  private void removeMessageObject(MessageObject paramMessageObject)
  {
    int i = this.messages.indexOf(paramMessageObject);
    if (i == -1) {}
    for (;;)
    {
      return;
      this.messages.remove(i);
      if (this.chatAdapter != null) {
        this.chatAdapter.notifyItemRemoved(this.chatAdapter.messagesStartRow + this.messages.size() - i - 1);
      }
    }
  }
  
  private void updateBottomOverlay() {}
  
  private void updateEmptyPlaceholder()
  {
    if (this.emptyView == null) {}
    for (;;)
    {
      return;
      if (!TextUtils.isEmpty(this.searchQuery))
      {
        this.emptyView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(5.0F));
        this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("EventLogEmptyTextSearch", NUM, new Object[] { this.searchQuery })));
      }
      else if ((this.selectedAdmins != null) || (this.currentFilter != null))
      {
        this.emptyView.setPadding(AndroidUtilities.dp(8.0F), AndroidUtilities.dp(5.0F), AndroidUtilities.dp(8.0F), AndroidUtilities.dp(5.0F));
        this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptySearch", NUM)));
      }
      else
      {
        this.emptyView.setPadding(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F));
        if (this.currentChat.megagroup) {
          this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmpty", NUM)));
        } else {
          this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString("EventLogEmptyChannel", NUM)));
        }
      }
    }
  }
  
  private void updateMessagesVisisblePart()
  {
    if (this.chatListView == null) {}
    for (;;)
    {
      return;
      int i = this.chatListView.getChildCount();
      int j = this.chatListView.getMeasuredHeight();
      int k = Integer.MAX_VALUE;
      int m = Integer.MAX_VALUE;
      Object localObject1 = null;
      Object localObject2 = null;
      Object localObject3 = null;
      int n = 0;
      int i1 = 0;
      Object localObject4;
      if (i1 < i)
      {
        localObject4 = this.chatListView.getChildAt(i1);
        int i2 = n;
        Object localObject5;
        int i3;
        label105:
        int i4;
        int i5;
        Object localObject6;
        if ((localObject4 instanceof ChatMessageCell))
        {
          localObject5 = (ChatMessageCell)localObject4;
          i3 = ((ChatMessageCell)localObject5).getTop();
          ((ChatMessageCell)localObject5).getBottom();
          if (i3 < 0) {
            break label295;
          }
          i2 = 0;
          i4 = ((ChatMessageCell)localObject5).getMeasuredHeight();
          i5 = i4;
          if (i4 > j) {
            i5 = i2 + j;
          }
          ((ChatMessageCell)localObject5).setVisiblePart(i2, i5 - i2);
          localObject6 = ((ChatMessageCell)localObject5).getMessageObject();
          i2 = n;
          if (this.roundVideoContainer != null)
          {
            i2 = n;
            if (((MessageObject)localObject6).isRoundVideo())
            {
              i2 = n;
              if (MediaController.getInstance().isPlayingMessage((MessageObject)localObject6))
              {
                localObject5 = ((ChatMessageCell)localObject5).getPhotoImage();
                this.roundVideoContainer.setTranslationX(((ImageReceiver)localObject5).getImageX());
                this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + i3 + ((ImageReceiver)localObject5).getImageY());
                this.fragmentView.invalidate();
                this.roundVideoContainer.invalidate();
                i2 = 1;
              }
            }
          }
        }
        Object localObject7;
        if (((View)localObject4).getBottom() <= this.chatListView.getPaddingTop())
        {
          i4 = m;
          localObject6 = localObject1;
          localObject7 = localObject2;
        }
        for (;;)
        {
          i1++;
          n = i2;
          localObject2 = localObject7;
          localObject1 = localObject6;
          m = i4;
          break;
          label295:
          i2 = -i3;
          break label105;
          i5 = ((View)localObject4).getBottom();
          localObject5 = localObject2;
          localObject2 = localObject3;
          n = k;
          if (i5 < k)
          {
            n = i5;
            if (((localObject4 instanceof ChatMessageCell)) || ((localObject4 instanceof ChatActionCell))) {
              localObject3 = localObject4;
            }
            localObject5 = localObject4;
            localObject2 = localObject3;
          }
          localObject7 = localObject5;
          localObject6 = localObject1;
          localObject3 = localObject2;
          i4 = m;
          k = n;
          if ((localObject4 instanceof ChatActionCell))
          {
            localObject7 = localObject5;
            localObject6 = localObject1;
            localObject3 = localObject2;
            i4 = m;
            k = n;
            if (((ChatActionCell)localObject4).getMessageObject().isDateObject)
            {
              if (((View)localObject4).getAlpha() != 1.0F) {
                ((View)localObject4).setAlpha(1.0F);
              }
              localObject7 = localObject5;
              localObject6 = localObject1;
              localObject3 = localObject2;
              i4 = m;
              k = n;
              if (i5 < m)
              {
                localObject7 = localObject5;
                localObject6 = localObject4;
                localObject3 = localObject2;
                i4 = i5;
                k = n;
              }
            }
          }
        }
      }
      if (this.roundVideoContainer != null)
      {
        if (n != 0) {
          break label740;
        }
        this.roundVideoContainer.setTranslationY(-AndroidUtilities.roundMessageSize - 100);
        this.fragmentView.invalidate();
        localObject4 = MediaController.getInstance().getPlayingMessageObject();
        if ((localObject4 != null) && (((MessageObject)localObject4).isRoundVideo()) && (this.checkTextureViewPosition)) {
          MediaController.getInstance().setCurrentRoundVisible(false);
        }
      }
      label552:
      label575:
      boolean bool;
      if (localObject3 != null)
      {
        if ((localObject3 instanceof ChatMessageCell))
        {
          localObject3 = ((ChatMessageCell)localObject3).getMessageObject();
          this.floatingDateView.setCustomDate(((MessageObject)localObject3).messageOwner.date);
        }
      }
      else
      {
        this.currentFloatingDateOnScreen = false;
        if (((localObject2 instanceof ChatMessageCell)) || ((localObject2 instanceof ChatActionCell))) {
          break label763;
        }
        bool = true;
        label614:
        this.currentFloatingTopIsNotMessage = bool;
        if (localObject1 == null) {
          break label870;
        }
        if ((((View)localObject1).getTop() <= this.chatListView.getPaddingTop()) && (!this.currentFloatingTopIsNotMessage)) {
          break label775;
        }
        if (((View)localObject1).getAlpha() != 1.0F) {
          ((View)localObject1).setAlpha(1.0F);
        }
        if (this.currentFloatingTopIsNotMessage) {
          break label769;
        }
        bool = true;
        label673:
        hideFloatingDateView(bool);
      }
      for (;;)
      {
        k = ((View)localObject1).getBottom() - this.chatListView.getPaddingTop();
        if ((k <= this.floatingDateView.getMeasuredHeight()) || (k >= this.floatingDateView.getMeasuredHeight() * 2)) {
          break label859;
        }
        this.floatingDateView.setTranslationY(-this.floatingDateView.getMeasuredHeight() * 2 + k);
        break;
        label740:
        MediaController.getInstance().setCurrentRoundVisible(true);
        break label552;
        localObject3 = ((ChatActionCell)localObject3).getMessageObject();
        break label575;
        label763:
        bool = false;
        break label614;
        label769:
        bool = false;
        break label673;
        label775:
        if (((View)localObject1).getAlpha() != 0.0F) {
          ((View)localObject1).setAlpha(0.0F);
        }
        if (this.floatingDateAnimation != null)
        {
          this.floatingDateAnimation.cancel();
          this.floatingDateAnimation = null;
        }
        if (this.floatingDateView.getTag() == null) {
          this.floatingDateView.setTag(Integer.valueOf(1));
        }
        if (this.floatingDateView.getAlpha() != 1.0F) {
          this.floatingDateView.setAlpha(1.0F);
        }
        this.currentFloatingDateOnScreen = true;
      }
      label859:
      this.floatingDateView.setTranslationY(0.0F);
      continue;
      label870:
      hideFloatingDateView(true);
      this.floatingDateView.setTranslationY(0.0F);
    }
  }
  
  private void updateTextureViewPosition()
  {
    int i = 0;
    int j = this.chatListView.getChildCount();
    int k = 0;
    int m = i;
    Object localObject1;
    if (k < j)
    {
      localObject1 = this.chatListView.getChildAt(k);
      if ((localObject1 instanceof ChatMessageCell))
      {
        localObject1 = (ChatMessageCell)localObject1;
        Object localObject2 = ((ChatMessageCell)localObject1).getMessageObject();
        if ((this.roundVideoContainer != null) && (((MessageObject)localObject2).isRoundVideo()) && (MediaController.getInstance().isPlayingMessage((MessageObject)localObject2)))
        {
          localObject2 = ((ChatMessageCell)localObject1).getPhotoImage();
          this.roundVideoContainer.setTranslationX(((ImageReceiver)localObject2).getImageX());
          this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + ((ChatMessageCell)localObject1).getTop() + ((ImageReceiver)localObject2).getImageY());
          this.fragmentView.invalidate();
          this.roundVideoContainer.invalidate();
          m = 1;
        }
      }
    }
    else if (this.roundVideoContainer != null)
    {
      localObject1 = MediaController.getInstance().getPlayingMessageObject();
      if (m != 0) {
        break label224;
      }
      this.roundVideoContainer.setTranslationY(-AndroidUtilities.roundMessageSize - 100);
      this.fragmentView.invalidate();
      if ((localObject1 != null) && (((MessageObject)localObject1).isRoundVideo()) && ((this.checkTextureViewPosition) || (PipRoundVideoView.getInstance() != null))) {
        MediaController.getInstance().setCurrentRoundVisible(false);
      }
    }
    for (;;)
    {
      return;
      k++;
      break;
      label224:
      MediaController.getInstance().setCurrentRoundVisible(true);
    }
  }
  
  public View createView(Context paramContext)
  {
    if (this.chatMessageCellsCache.isEmpty()) {
      for (int i = 0; i < 8; i++) {
        this.chatMessageCellsCache.add(new ChatMessageCell(paramContext));
      }
    }
    this.searchWas = false;
    this.hasOwnBackground = true;
    Theme.createChatResources(paramContext, false);
    this.actionBar.setAddToContainer(false);
    Object localObject = this.actionBar;
    boolean bool;
    if ((Build.VERSION.SDK_INT >= 21) && (!AndroidUtilities.isTablet()))
    {
      bool = true;
      ((ActionBar)localObject).setOccupyStatusBar(bool);
      this.actionBar.setBackButtonDrawable(new BackDrawable(false));
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            ChannelAdminLogActivity.this.finishFragment();
          }
        }
      });
      this.avatarContainer = new ChatAvatarContainer(paramContext, null, false);
      localObject = this.avatarContainer;
      if (AndroidUtilities.isTablet()) {
        break label1510;
      }
      bool = true;
      label149:
      ((ChatAvatarContainer)localObject).setOccupyStatusBar(bool);
      this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0F, 51, 56.0F, 0.0F, 40.0F, 0.0F));
      this.searchItem = this.actionBar.createMenu().addItem(0, NUM).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
      {
        public void onSearchCollapse()
        {
          ChannelAdminLogActivity.access$1402(ChannelAdminLogActivity.this, "");
          ChannelAdminLogActivity.this.avatarContainer.setVisibility(0);
          if (ChannelAdminLogActivity.this.searchWas)
          {
            ChannelAdminLogActivity.access$1602(ChannelAdminLogActivity.this, false);
            ChannelAdminLogActivity.this.loadMessages(true);
          }
          ChannelAdminLogActivity.this.updateBottomOverlay();
        }
        
        public void onSearchExpand()
        {
          ChannelAdminLogActivity.this.avatarContainer.setVisibility(8);
          ChannelAdminLogActivity.this.updateBottomOverlay();
        }
        
        public void onSearchPressed(EditText paramAnonymousEditText)
        {
          ChannelAdminLogActivity.access$1602(ChannelAdminLogActivity.this, true);
          ChannelAdminLogActivity.access$1402(ChannelAdminLogActivity.this, paramAnonymousEditText.getText().toString());
          ChannelAdminLogActivity.this.loadMessages(true);
        }
      });
      this.searchItem.getSearchField().setHint(LocaleController.getString("Search", NUM));
      this.avatarContainer.setEnabled(false);
      this.avatarContainer.setTitle(this.currentChat.title);
      this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
      this.avatarContainer.setChatAvatar(this.currentChat);
      this.fragmentView = new SizeNotifierFrameLayout(paramContext)
      {
        protected boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
        {
          boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
          if ((paramAnonymousView == ChannelAdminLogActivity.this.actionBar) && (ChannelAdminLogActivity.this.parentLayout != null))
          {
            paramAnonymousView = ChannelAdminLogActivity.this.parentLayout;
            if (ChannelAdminLogActivity.this.actionBar.getVisibility() != 0) {
              break label73;
            }
          }
          label73:
          for (int i = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();; i = 0)
          {
            paramAnonymousView.drawHeaderShadow(paramAnonymousCanvas, i);
            return bool;
          }
        }
        
        protected boolean isActionBarVisible()
        {
          if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {}
          for (boolean bool = true;; bool = false) {
            return bool;
          }
        }
        
        protected void onAttachedToWindow()
        {
          super.onAttachedToWindow();
          MessageObject localMessageObject = MediaController.getInstance().getPlayingMessageObject();
          if ((localMessageObject != null) && (localMessageObject.isRoundVideo()) && (localMessageObject.eventId != 0L) && (localMessageObject.getDialogId() == -ChannelAdminLogActivity.this.currentChat.id)) {
            MediaController.getInstance().setTextureView(ChannelAdminLogActivity.this.createTextureView(false), ChannelAdminLogActivity.this.aspectRatioFrameLayout, ChannelAdminLogActivity.this.roundVideoContainer, true);
          }
        }
        
        protected void onLayout(boolean paramAnonymousBoolean, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
        {
          int i = getChildCount();
          int j = 0;
          while (j < i)
          {
            View localView = getChildAt(j);
            if (localView.getVisibility() == 8)
            {
              j++;
            }
            else
            {
              FrameLayout.LayoutParams localLayoutParams = (FrameLayout.LayoutParams)localView.getLayoutParams();
              int k = localView.getMeasuredWidth();
              int m = localView.getMeasuredHeight();
              int n = localLayoutParams.gravity;
              int i1 = n;
              if (n == -1) {
                i1 = 51;
              }
              label127:
              label175:
              int i3;
              switch (i1 & 0x7 & 0x7)
              {
              default: 
                n = localLayoutParams.leftMargin;
                switch (i1 & 0x70)
                {
                default: 
                  i1 = localLayoutParams.topMargin;
                  if (localView == ChannelAdminLogActivity.this.emptyViewContainer)
                  {
                    int i2 = AndroidUtilities.dp(24.0F);
                    if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0)
                    {
                      i3 = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight() / 2;
                      label221:
                      i3 = i1 - (i2 - i3);
                    }
                  }
                  break;
                }
                break;
              }
              for (;;)
              {
                localView.layout(n, i3, n + k, i3 + m);
                break;
                n = (paramAnonymousInt3 - paramAnonymousInt1 - k) / 2 + localLayoutParams.leftMargin - localLayoutParams.rightMargin;
                break label127;
                n = paramAnonymousInt3 - k - localLayoutParams.rightMargin;
                break label127;
                i3 = localLayoutParams.topMargin + getPaddingTop();
                i1 = i3;
                if (localView == ChannelAdminLogActivity.this.actionBar) {
                  break label175;
                }
                i1 = i3;
                if (ChannelAdminLogActivity.this.actionBar.getVisibility() != 0) {
                  break label175;
                }
                i1 = i3 + ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
                break label175;
                i1 = (paramAnonymousInt4 - paramAnonymousInt2 - m) / 2 + localLayoutParams.topMargin - localLayoutParams.bottomMargin;
                break label175;
                i1 = paramAnonymousInt4 - paramAnonymousInt2 - m - localLayoutParams.bottomMargin;
                break label175;
                i3 = 0;
                break label221;
                i3 = i1;
                if (localView == ChannelAdminLogActivity.this.actionBar) {
                  i3 = i1 - getPaddingTop();
                }
              }
            }
          }
          ChannelAdminLogActivity.this.updateMessagesVisisblePart();
          notifyHeightChanged();
        }
        
        protected void onMeasure(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          int i = View.MeasureSpec.getSize(paramAnonymousInt1);
          int j = View.MeasureSpec.getSize(paramAnonymousInt2);
          setMeasuredDimension(i, j);
          int k = j - getPaddingTop();
          measureChildWithMargins(ChannelAdminLogActivity.this.actionBar, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
          int m = ChannelAdminLogActivity.this.actionBar.getMeasuredHeight();
          j = k;
          if (ChannelAdminLogActivity.this.actionBar.getVisibility() == 0) {
            j = k - m;
          }
          getKeyboardHeight();
          m = getChildCount();
          k = 0;
          if (k < m)
          {
            View localView = getChildAt(k);
            if ((localView == null) || (localView.getVisibility() == 8) || (localView == ChannelAdminLogActivity.this.actionBar)) {}
            for (;;)
            {
              k++;
              break;
              if ((localView == ChannelAdminLogActivity.this.chatListView) || (localView == ChannelAdminLogActivity.this.progressView)) {
                localView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0F), j - AndroidUtilities.dp(50.0F)), NUM));
              } else if (localView == ChannelAdminLogActivity.this.emptyViewContainer) {
                localView.measure(View.MeasureSpec.makeMeasureSpec(i, NUM), View.MeasureSpec.makeMeasureSpec(j, NUM));
              } else {
                measureChildWithMargins(localView, paramAnonymousInt1, 0, paramAnonymousInt2, 0);
              }
            }
          }
        }
      };
      this.contentView = ((SizeNotifierFrameLayout)this.fragmentView);
      localObject = this.contentView;
      if (AndroidUtilities.isTablet()) {
        break label1516;
      }
      bool = true;
      label324:
      ((SizeNotifierFrameLayout)localObject).setOccupyStatusBar(bool);
      this.contentView.setBackgroundImage(Theme.getCachedWallpaper());
      this.emptyViewContainer = new FrameLayout(paramContext);
      this.emptyViewContainer.setVisibility(4);
      this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
      this.emptyViewContainer.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      this.emptyView = new TextView(paramContext);
      this.emptyView.setTextSize(1, 14.0F);
      this.emptyView.setGravity(17);
      this.emptyView.setTextColor(Theme.getColor("chat_serviceText"));
      this.emptyView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0F), Theme.getServiceMessageColor()));
      this.emptyView.setPadding(AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F), AndroidUtilities.dp(16.0F));
      this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0F, 17, 16.0F, 0.0F, 16.0F, 0.0F));
      this.chatListView = new RecyclerListView(paramContext)
      {
        public boolean drawChild(Canvas paramAnonymousCanvas, View paramAnonymousView, long paramAnonymousLong)
        {
          boolean bool = super.drawChild(paramAnonymousCanvas, paramAnonymousView, paramAnonymousLong);
          ChatMessageCell localChatMessageCell;
          ImageReceiver localImageReceiver;
          int i;
          Object localObject;
          if ((paramAnonymousView instanceof ChatMessageCell))
          {
            localChatMessageCell = (ChatMessageCell)paramAnonymousView;
            localImageReceiver = localChatMessageCell.getAvatarImage();
            if (localImageReceiver != null)
            {
              i = paramAnonymousView.getTop();
              if (!localChatMessageCell.isPinnedBottom()) {
                break label107;
              }
              localObject = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(paramAnonymousView);
              if ((localObject == null) || (ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(((RecyclerView.ViewHolder)localObject).getAdapterPosition() + 1) == null)) {
                break label107;
              }
              localImageReceiver.setImageY(-AndroidUtilities.dp(1000.0F));
              localImageReceiver.draw(paramAnonymousCanvas);
            }
          }
          for (;;)
          {
            return bool;
            label107:
            int j = i;
            if (localChatMessageCell.isPinnedTop())
            {
              localObject = ChannelAdminLogActivity.this.chatListView.getChildViewHolder(paramAnonymousView);
              j = i;
              if (localObject != null)
              {
                j = i;
                RecyclerView.ViewHolder localViewHolder;
                do
                {
                  localViewHolder = ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(((RecyclerView.ViewHolder)localObject).getAdapterPosition() - 1);
                  if (localViewHolder == null) {
                    break;
                  }
                  i = localViewHolder.itemView.getTop();
                  j = i;
                  if (!(localViewHolder.itemView instanceof ChatMessageCell)) {
                    break;
                  }
                  localObject = localViewHolder;
                  j = i;
                } while (((ChatMessageCell)localViewHolder.itemView).isPinnedTop());
                j = i;
              }
            }
            int k = paramAnonymousView.getTop() + localChatMessageCell.getLayoutHeight();
            int m = ChannelAdminLogActivity.this.chatListView.getHeight() - ChannelAdminLogActivity.this.chatListView.getPaddingBottom();
            i = k;
            if (k > m) {
              i = m;
            }
            m = i;
            if (i - AndroidUtilities.dp(48.0F) < j) {
              m = j + AndroidUtilities.dp(48.0F);
            }
            localImageReceiver.setImageY(m - AndroidUtilities.dp(44.0F));
            localImageReceiver.draw(paramAnonymousCanvas);
          }
        }
      };
      this.chatListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener()
      {
        public void onItemClick(View paramAnonymousView, int paramAnonymousInt)
        {
          ChannelAdminLogActivity.this.createMenu(paramAnonymousView);
        }
      });
      this.chatListView.setTag(Integer.valueOf(1));
      this.chatListView.setVerticalScrollBarEnabled(true);
      localObject = this.chatListView;
      ChatActivityAdapter localChatActivityAdapter = new ChatActivityAdapter(paramContext);
      this.chatAdapter = localChatActivityAdapter;
      ((RecyclerListView)localObject).setAdapter(localChatActivityAdapter);
      this.chatListView.setClipToPadding(false);
      this.chatListView.setPadding(0, AndroidUtilities.dp(4.0F), 0, AndroidUtilities.dp(3.0F));
      this.chatListView.setItemAnimator(null);
      this.chatListView.setLayoutAnimation(null);
      this.chatLayoutManager = new LinearLayoutManager(paramContext)
      {
        public void smoothScrollToPosition(RecyclerView paramAnonymousRecyclerView, RecyclerView.State paramAnonymousState, int paramAnonymousInt)
        {
          paramAnonymousRecyclerView = new LinearSmoothScrollerMiddle(paramAnonymousRecyclerView.getContext());
          paramAnonymousRecyclerView.setTargetPosition(paramAnonymousInt);
          startSmoothScroll(paramAnonymousRecyclerView);
        }
        
        public boolean supportsPredictiveItemAnimations()
        {
          return false;
        }
      };
      this.chatLayoutManager.setOrientation(1);
      this.chatLayoutManager.setStackFromEnd(true);
      this.chatListView.setLayoutManager(this.chatLayoutManager);
      this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0F));
      this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener()
      {
        private final int scrollValue = AndroidUtilities.dp(100.0F);
        private float totalDy = 0.0F;
        
        public void onScrollStateChanged(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt)
        {
          if (paramAnonymousInt == 1)
          {
            ChannelAdminLogActivity.access$4002(ChannelAdminLogActivity.this, true);
            ChannelAdminLogActivity.access$4102(ChannelAdminLogActivity.this, true);
          }
          for (;;)
          {
            return;
            if (paramAnonymousInt == 0)
            {
              ChannelAdminLogActivity.access$4002(ChannelAdminLogActivity.this, false);
              ChannelAdminLogActivity.access$4102(ChannelAdminLogActivity.this, false);
              ChannelAdminLogActivity.this.hideFloatingDateView(true);
            }
          }
        }
        
        public void onScrolled(RecyclerView paramAnonymousRecyclerView, int paramAnonymousInt1, int paramAnonymousInt2)
        {
          ChannelAdminLogActivity.this.chatListView.invalidate();
          if ((paramAnonymousInt2 != 0) && (ChannelAdminLogActivity.this.scrollingFloatingDate) && (!ChannelAdminLogActivity.this.currentFloatingTopIsNotMessage) && (ChannelAdminLogActivity.this.floatingDateView.getTag() == null))
          {
            if (ChannelAdminLogActivity.this.floatingDateAnimation != null) {
              ChannelAdminLogActivity.this.floatingDateAnimation.cancel();
            }
            ChannelAdminLogActivity.this.floatingDateView.setTag(Integer.valueOf(1));
            ChannelAdminLogActivity.access$4502(ChannelAdminLogActivity.this, new AnimatorSet());
            ChannelAdminLogActivity.this.floatingDateAnimation.setDuration(150L);
            ChannelAdminLogActivity.this.floatingDateAnimation.playTogether(new Animator[] { ObjectAnimator.ofFloat(ChannelAdminLogActivity.this.floatingDateView, "alpha", new float[] { 1.0F }) });
            ChannelAdminLogActivity.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymous2Animator)
              {
                if (paramAnonymous2Animator.equals(ChannelAdminLogActivity.this.floatingDateAnimation)) {
                  ChannelAdminLogActivity.access$4502(ChannelAdminLogActivity.this, null);
                }
              }
            });
            ChannelAdminLogActivity.this.floatingDateAnimation.start();
          }
          ChannelAdminLogActivity.this.checkScrollForLoad(true);
          ChannelAdminLogActivity.this.updateMessagesVisisblePart();
        }
      });
      if (this.scrollToPositionOnRecreate != -1)
      {
        this.chatLayoutManager.scrollToPositionWithOffset(this.scrollToPositionOnRecreate, this.scrollToOffsetOnRecreate);
        this.scrollToPositionOnRecreate = -1;
      }
      this.progressView = new FrameLayout(paramContext);
      this.progressView.setVisibility(4);
      this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
      this.progressView2 = new View(paramContext);
      this.progressView2.setBackgroundResource(NUM);
      this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
      this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
      this.progressBar = new RadialProgressView(paramContext);
      this.progressBar.setSize(AndroidUtilities.dp(28.0F));
      this.progressBar.setProgressColor(Theme.getColor("chat_serviceText"));
      this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
      this.floatingDateView = new ChatActionCell(paramContext);
      this.floatingDateView.setAlpha(0.0F);
      this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0F, 49, 0.0F, 4.0F, 0.0F, 0.0F));
      this.contentView.addView(this.actionBar);
      this.bottomOverlayChat = new FrameLayout(paramContext)
      {
        public void onDraw(Canvas paramAnonymousCanvas)
        {
          int i = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
          Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), i);
          Theme.chat_composeShadowDrawable.draw(paramAnonymousCanvas);
          paramAnonymousCanvas.drawRect(0.0F, i, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
        }
      };
      this.bottomOverlayChat.setWillNotDraw(false);
      this.bottomOverlayChat.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
      this.contentView.addView(this.bottomOverlayChat, LayoutHelper.createFrame(-1, 51, 80));
      this.bottomOverlayChat.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelAdminLogActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            paramAnonymousView = new AdminLogFilterAlert(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this.currentFilter, ChannelAdminLogActivity.this.selectedAdmins, ChannelAdminLogActivity.this.currentChat.megagroup);
            paramAnonymousView.setCurrentAdmins(ChannelAdminLogActivity.this.admins);
            paramAnonymousView.setAdminLogFilterAlertDelegate(new AdminLogFilterAlert.AdminLogFilterAlertDelegate()
            {
              public void didSelectRights(TLRPC.TL_channelAdminLogEventsFilter paramAnonymous2TL_channelAdminLogEventsFilter, SparseArray<TLRPC.User> paramAnonymous2SparseArray)
              {
                ChannelAdminLogActivity.access$4702(ChannelAdminLogActivity.this, paramAnonymous2TL_channelAdminLogEventsFilter);
                ChannelAdminLogActivity.access$4802(ChannelAdminLogActivity.this, paramAnonymous2SparseArray);
                if ((ChannelAdminLogActivity.this.currentFilter != null) || (ChannelAdminLogActivity.this.selectedAdmins != null)) {
                  ChannelAdminLogActivity.this.avatarContainer.setSubtitle(LocaleController.getString("EventLogSelectedEvents", NUM));
                }
                for (;;)
                {
                  ChannelAdminLogActivity.this.loadMessages(true);
                  return;
                  ChannelAdminLogActivity.this.avatarContainer.setSubtitle(LocaleController.getString("EventLogAllEvents", NUM));
                }
              }
            });
            ChannelAdminLogActivity.this.showDialog(paramAnonymousView);
          }
        }
      });
      this.bottomOverlayChatText = new TextView(paramContext);
      this.bottomOverlayChatText.setTextSize(1, 15.0F);
      this.bottomOverlayChatText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.bottomOverlayChatText.setTextColor(Theme.getColor("chat_fieldOverlayText"));
      this.bottomOverlayChatText.setText(LocaleController.getString("SETTINGS", NUM).toUpperCase());
      this.bottomOverlayChat.addView(this.bottomOverlayChatText, LayoutHelper.createFrame(-2, -2, 17));
      this.bottomOverlayImage = new ImageView(paramContext);
      this.bottomOverlayImage.setImageResource(NUM);
      this.bottomOverlayImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_fieldOverlayText"), PorterDuff.Mode.MULTIPLY));
      this.bottomOverlayImage.setScaleType(ImageView.ScaleType.CENTER);
      this.bottomOverlayChat.addView(this.bottomOverlayImage, LayoutHelper.createFrame(48, 48.0F, 53, 3.0F, 0.0F, 0.0F, 0.0F));
      this.bottomOverlayImage.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          paramAnonymousView = new AlertDialog.Builder(ChannelAdminLogActivity.this.getParentActivity());
          if (ChannelAdminLogActivity.this.currentChat.megagroup) {
            paramAnonymousView.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetail", NUM)));
          }
          for (;;)
          {
            paramAnonymousView.setPositiveButton(LocaleController.getString("OK", NUM), null);
            paramAnonymousView.setTitle(LocaleController.getString("EventLogInfoTitle", NUM));
            ChannelAdminLogActivity.this.showDialog(paramAnonymousView.create());
            return;
            paramAnonymousView.setMessage(AndroidUtilities.replaceTags(LocaleController.getString("EventLogInfoDetailChannel", NUM)));
          }
        }
      });
      this.searchContainer = new FrameLayout(paramContext)
      {
        public void onDraw(Canvas paramAnonymousCanvas)
        {
          int i = Theme.chat_composeShadowDrawable.getIntrinsicHeight();
          Theme.chat_composeShadowDrawable.setBounds(0, 0, getMeasuredWidth(), i);
          Theme.chat_composeShadowDrawable.draw(paramAnonymousCanvas);
          paramAnonymousCanvas.drawRect(0.0F, i, getMeasuredWidth(), getMeasuredHeight(), Theme.chat_composeBackgroundPaint);
        }
      };
      this.searchContainer.setWillNotDraw(false);
      this.searchContainer.setVisibility(4);
      this.searchContainer.setFocusable(true);
      this.searchContainer.setFocusableInTouchMode(true);
      this.searchContainer.setClickable(true);
      this.searchContainer.setPadding(0, AndroidUtilities.dp(3.0F), 0, 0);
      this.contentView.addView(this.searchContainer, LayoutHelper.createFrame(-1, 51, 80));
      this.searchCalendarButton = new ImageView(paramContext);
      this.searchCalendarButton.setScaleType(ImageView.ScaleType.CENTER);
      this.searchCalendarButton.setImageResource(NUM);
      this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("chat_searchPanelIcons"), PorterDuff.Mode.MULTIPLY));
      this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48, 53));
      this.searchCalendarButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          if (ChannelAdminLogActivity.this.getParentActivity() == null) {}
          for (;;)
          {
            return;
            AndroidUtilities.hideKeyboard(ChannelAdminLogActivity.this.searchItem.getSearchField());
            paramAnonymousView = Calendar.getInstance();
            int i = paramAnonymousView.get(1);
            int j = paramAnonymousView.get(2);
            int k = paramAnonymousView.get(5);
            try
            {
              paramAnonymousView = new android/app/DatePickerDialog;
              Object localObject1 = ChannelAdminLogActivity.this.getParentActivity();
              Object localObject2 = new org/telegram/ui/ChannelAdminLogActivity$15$1;
              ((1)localObject2).<init>(this);
              paramAnonymousView.<init>((Context)localObject1, (DatePickerDialog.OnDateSetListener)localObject2, i, j, k);
              localObject1 = paramAnonymousView.getDatePicker();
              ((DatePicker)localObject1).setMinDate(1375315200000L);
              ((DatePicker)localObject1).setMaxDate(System.currentTimeMillis());
              paramAnonymousView.setButton(-1, LocaleController.getString("JumpToDate", NUM), paramAnonymousView);
              String str = LocaleController.getString("Cancel", NUM);
              localObject2 = new org/telegram/ui/ChannelAdminLogActivity$15$2;
              ((2)localObject2).<init>(this);
              paramAnonymousView.setButton(-2, str, (DialogInterface.OnClickListener)localObject2);
              if (Build.VERSION.SDK_INT >= 21)
              {
                localObject2 = new org/telegram/ui/ChannelAdminLogActivity$15$3;
                ((3)localObject2).<init>(this, (DatePicker)localObject1);
                paramAnonymousView.setOnShowListener((DialogInterface.OnShowListener)localObject2);
              }
              ChannelAdminLogActivity.this.showDialog(paramAnonymousView);
            }
            catch (Exception paramAnonymousView)
            {
              FileLog.e(paramAnonymousView);
            }
          }
        }
      });
      this.searchCountText = new SimpleTextView(paramContext);
      this.searchCountText.setTextColor(Theme.getColor("chat_searchPanelText"));
      this.searchCountText.setTextSize(15);
      this.searchCountText.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
      this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-1, -2.0F, 19, 108.0F, 0.0F, 0.0F, 0.0F));
      this.chatAdapter.updateRows();
      if ((!this.loading) || (!this.messages.isEmpty())) {
        break label1522;
      }
      this.progressView.setVisibility(0);
      this.chatListView.setEmptyView(null);
    }
    for (;;)
    {
      updateEmptyPlaceholder();
      return this.fragmentView;
      bool = false;
      break;
      label1510:
      bool = false;
      break label149;
      label1516:
      bool = false;
      break label324;
      label1522:
      this.progressView.setVisibility(4);
      this.chatListView.setEmptyView(this.emptyViewContainer);
    }
  }
  
  public void didReceivedNotification(int paramInt1, int paramInt2, Object... paramVarArgs)
  {
    if (paramInt1 == NotificationCenter.emojiDidLoaded) {
      if (this.chatListView != null) {
        this.chatListView.invalidateViews();
      }
    }
    for (;;)
    {
      return;
      label82:
      Object localObject1;
      if (paramInt1 == NotificationCenter.messagePlayingDidStarted)
      {
        if (((MessageObject)paramVarArgs[0]).isRoundVideo())
        {
          MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
          updateTextureViewPosition();
        }
        if (this.chatListView != null)
        {
          paramInt2 = this.chatListView.getChildCount();
          paramInt1 = 0;
          if (paramInt1 < paramInt2)
          {
            paramVarArgs = this.chatListView.getChildAt(paramInt1);
            if ((paramVarArgs instanceof ChatMessageCell))
            {
              paramVarArgs = (ChatMessageCell)paramVarArgs;
              localObject1 = paramVarArgs.getMessageObject();
              if (localObject1 != null)
              {
                if ((!((MessageObject)localObject1).isVoice()) && (!((MessageObject)localObject1).isMusic())) {
                  break label146;
                }
                paramVarArgs.updateButtonState(false);
              }
            }
          }
          for (;;)
          {
            paramInt1++;
            break label82;
            break;
            label146:
            if (((MessageObject)localObject1).isRoundVideo())
            {
              paramVarArgs.checkRoundVideoPlayback(false);
              if ((!MediaController.getInstance().isPlayingMessage((MessageObject)localObject1)) && (((MessageObject)localObject1).audioProgress != 0.0F))
              {
                ((MessageObject)localObject1).resetPlayingProgress();
                paramVarArgs.invalidate();
              }
            }
          }
        }
      }
      else if ((paramInt1 == NotificationCenter.messagePlayingDidReset) || (paramInt1 == NotificationCenter.messagePlayingPlayStateChanged))
      {
        if (this.chatListView != null)
        {
          paramInt2 = this.chatListView.getChildCount();
          paramInt1 = 0;
          label223:
          if (paramInt1 < paramInt2)
          {
            paramVarArgs = this.chatListView.getChildAt(paramInt1);
            if ((paramVarArgs instanceof ChatMessageCell))
            {
              localObject1 = (ChatMessageCell)paramVarArgs;
              paramVarArgs = ((ChatMessageCell)localObject1).getMessageObject();
              if (paramVarArgs != null)
              {
                if ((!paramVarArgs.isVoice()) && (!paramVarArgs.isMusic())) {
                  break label286;
                }
                ((ChatMessageCell)localObject1).updateButtonState(false);
              }
            }
          }
          for (;;)
          {
            paramInt1++;
            break label223;
            break;
            label286:
            if ((paramVarArgs.isRoundVideo()) && (!MediaController.getInstance().isPlayingMessage(paramVarArgs))) {
              ((ChatMessageCell)localObject1).checkRoundVideoPlayback(true);
            }
          }
        }
      }
      else if (paramInt1 == NotificationCenter.messagePlayingProgressDidChanged)
      {
        Object localObject2 = (Integer)paramVarArgs[0];
        if (this.chatListView != null)
        {
          paramInt2 = this.chatListView.getChildCount();
          for (paramInt1 = 0;; paramInt1++)
          {
            if (paramInt1 >= paramInt2) {
              break label445;
            }
            paramVarArgs = this.chatListView.getChildAt(paramInt1);
            if ((paramVarArgs instanceof ChatMessageCell))
            {
              localObject1 = (ChatMessageCell)paramVarArgs;
              paramVarArgs = ((ChatMessageCell)localObject1).getMessageObject();
              if ((paramVarArgs != null) && (paramVarArgs.getId() == ((Integer)localObject2).intValue()))
              {
                localObject2 = MediaController.getInstance().getPlayingMessageObject();
                if (localObject2 == null) {
                  break;
                }
                paramVarArgs.audioProgress = ((MessageObject)localObject2).audioProgress;
                paramVarArgs.audioProgressSec = ((MessageObject)localObject2).audioProgressSec;
                paramVarArgs.audioPlayerDuration = ((MessageObject)localObject2).audioPlayerDuration;
                ((ChatMessageCell)localObject1).updatePlayingMessageProgress();
                break;
              }
            }
          }
        }
      }
      else
      {
        label445:
        if ((paramInt1 == NotificationCenter.didSetNewWallpapper) && (this.fragmentView != null))
        {
          ((SizeNotifierFrameLayout)this.fragmentView).setBackgroundImage(Theme.getCachedWallpaper());
          this.progressView2.getBackground().setColorFilter(Theme.colorFilter);
          if (this.emptyView != null) {
            this.emptyView.getBackground().setColorFilter(Theme.colorFilter);
          }
          this.chatListView.invalidateViews();
        }
      }
    }
  }
  
  public TLRPC.Chat getCurrentChat()
  {
    return this.currentChat;
  }
  
  public ThemeDescription[] getThemeDescriptions()
  {
    ThemeDescription localThemeDescription1 = new ThemeDescription(this.fragmentView, 0, null, null, null, null, "chat_wallpaper");
    ThemeDescription localThemeDescription2 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription3 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription4 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription5 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    ThemeDescription localThemeDescription6 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, null, "actionBarDefaultSubmenuBackground");
    ThemeDescription localThemeDescription7 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, null, "actionBarDefaultSubmenuItem");
    ThemeDescription localThemeDescription8 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription9 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, "actionBarDefault");
    ThemeDescription localThemeDescription10 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, "actionBarDefaultIcon");
    ThemeDescription localThemeDescription11 = new ThemeDescription(this.avatarContainer.getTitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "actionBarDefaultTitle");
    ThemeDescription localThemeDescription12 = new ThemeDescription(this.avatarContainer.getSubtitleTextView(), ThemeDescription.FLAG_TEXTCOLOR, null, new Paint[] { Theme.chat_statusPaint, Theme.chat_statusRecordPaint }, null, null, "actionBarDefaultSubtitle", null);
    ThemeDescription localThemeDescription13 = new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, "actionBarDefaultSelector");
    Object localObject1 = this.chatListView;
    Object localObject2 = Theme.avatar_photoDrawable;
    Object localObject3 = Theme.avatar_broadcastDrawable;
    Object localObject4 = Theme.avatar_savedDrawable;
    ThemeDescription localThemeDescription14 = new ThemeDescription((View)localObject1, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject2, localObject3, localObject4 }, null, "avatar_text");
    ThemeDescription localThemeDescription15 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_backgroundRed");
    ThemeDescription localThemeDescription16 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_backgroundOrange");
    ThemeDescription localThemeDescription17 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_backgroundViolet");
    localObject2 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_backgroundGreen");
    ThemeDescription localThemeDescription18 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_backgroundCyan");
    ThemeDescription localThemeDescription19 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_backgroundBlue");
    ThemeDescription localThemeDescription20 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_backgroundPink");
    ThemeDescription localThemeDescription21 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_nameInMessageRed");
    ThemeDescription localThemeDescription22 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_nameInMessageOrange");
    ThemeDescription localThemeDescription23 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_nameInMessageViolet");
    localObject3 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_nameInMessageGreen");
    ThemeDescription localThemeDescription24 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_nameInMessageCyan");
    localObject1 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_nameInMessageBlue");
    ThemeDescription localThemeDescription25 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "avatar_nameInMessagePink");
    Object localObject5 = this.chatListView;
    localObject4 = Theme.chat_msgInDrawable;
    Object localObject6 = Theme.chat_msgInMediaDrawable;
    localObject5 = new ThemeDescription((View)localObject5, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject6 }, null, "chat_inBubble");
    Object localObject7 = this.chatListView;
    localObject4 = Theme.chat_msgInSelectedDrawable;
    localObject6 = Theme.chat_msgInMediaSelectedDrawable;
    localObject6 = new ThemeDescription((View)localObject7, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject6 }, null, "chat_inBubbleSelected");
    localObject4 = this.chatListView;
    localObject7 = Theme.chat_msgInShadowDrawable;
    Object localObject8 = Theme.chat_msgInMediaShadowDrawable;
    localObject7 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject7, localObject8 }, null, "chat_inBubbleShadow");
    localObject4 = this.chatListView;
    localObject8 = Theme.chat_msgOutDrawable;
    Object localObject9 = Theme.chat_msgOutMediaDrawable;
    localObject8 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject8, localObject9 }, null, "chat_outBubble");
    localObject9 = this.chatListView;
    localObject4 = Theme.chat_msgOutSelectedDrawable;
    Object localObject10 = Theme.chat_msgOutMediaSelectedDrawable;
    localObject9 = new ThemeDescription((View)localObject9, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject10 }, null, "chat_outBubbleSelected");
    localObject4 = this.chatListView;
    localObject10 = Theme.chat_msgOutShadowDrawable;
    Object localObject11 = Theme.chat_msgOutMediaShadowDrawable;
    localObject10 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject10, localObject11 }, null, "chat_outBubbleShadow");
    localObject11 = this.chatListView;
    int i = ThemeDescription.FLAG_TEXTCOLOR;
    localObject4 = Theme.chat_actionTextPaint;
    localObject11 = new ThemeDescription((View)localObject11, i, new Class[] { ChatActionCell.class }, (Paint)localObject4, null, null, "chat_serviceText");
    localObject4 = this.chatListView;
    i = ThemeDescription.FLAG_LINKCOLOR;
    Object localObject12 = Theme.chat_actionTextPaint;
    localObject12 = new ThemeDescription((View)localObject4, i, new Class[] { ChatActionCell.class }, (Paint)localObject12, null, null, "chat_serviceLink");
    Object localObject13 = this.chatListView;
    Object localObject14 = Theme.chat_shareIconDrawable;
    Object localObject15 = Theme.chat_botInlineDrawable;
    Object localObject16 = Theme.chat_botLinkDrawalbe;
    localObject4 = Theme.chat_goIconDrawable;
    localObject14 = new ThemeDescription((View)localObject13, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject14, localObject15, localObject16, localObject4 }, null, "chat_serviceIcon");
    localObject13 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class, ChatActionCell.class }, null, null, null, "chat_serviceBackground");
    localObject16 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class, ChatActionCell.class }, null, null, null, "chat_serviceBackgroundSelected");
    ThemeDescription localThemeDescription26 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_messageTextIn");
    ThemeDescription localThemeDescription27 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_messageTextOut");
    ThemeDescription localThemeDescription28 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[] { ChatMessageCell.class }, null, null, null, "chat_messageLinkIn", null);
    localObject15 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[] { ChatMessageCell.class }, null, null, null, "chat_messageLinkOut", null);
    localObject4 = this.chatListView;
    Object localObject17 = Theme.chat_msgOutCheckDrawable;
    Object localObject18 = Theme.chat_msgOutHalfCheckDrawable;
    localObject17 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject17, localObject18 }, null, "chat_outSentCheck");
    Object localObject19 = this.chatListView;
    localObject18 = Theme.chat_msgOutCheckSelectedDrawable;
    localObject4 = Theme.chat_msgOutHalfCheckSelectedDrawable;
    localObject18 = new ThemeDescription((View)localObject19, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject18, localObject4 }, null, "chat_outSentCheckSelected");
    localObject19 = this.chatListView;
    localObject4 = Theme.chat_msgOutClockDrawable;
    localObject19 = new ThemeDescription((View)localObject19, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outSentClock");
    Object localObject20 = this.chatListView;
    localObject4 = Theme.chat_msgOutSelectedClockDrawable;
    localObject20 = new ThemeDescription((View)localObject20, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outSentClockSelected");
    Object localObject21 = this.chatListView;
    localObject4 = Theme.chat_msgInClockDrawable;
    localObject21 = new ThemeDescription((View)localObject21, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inSentClock");
    localObject4 = this.chatListView;
    Object localObject22 = Theme.chat_msgInSelectedClockDrawable;
    localObject22 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject22 }, null, "chat_inSentClockSelected");
    localObject4 = this.chatListView;
    Object localObject23 = Theme.chat_msgMediaCheckDrawable;
    Object localObject24 = Theme.chat_msgMediaHalfCheckDrawable;
    localObject24 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject23, localObject24 }, null, "chat_mediaSentCheck");
    Object localObject25 = this.chatListView;
    Object localObject26 = Theme.chat_msgStickerHalfCheckDrawable;
    localObject4 = Theme.chat_msgStickerCheckDrawable;
    Object localObject27 = Theme.chat_msgStickerClockDrawable;
    localObject23 = Theme.chat_msgStickerViewsDrawable;
    localObject23 = new ThemeDescription((View)localObject25, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject26, localObject4, localObject27, localObject23 }, null, "chat_serviceText");
    localObject27 = this.chatListView;
    localObject4 = Theme.chat_msgMediaClockDrawable;
    localObject27 = new ThemeDescription((View)localObject27, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_mediaSentClock");
    localObject25 = this.chatListView;
    localObject4 = Theme.chat_msgOutViewsDrawable;
    localObject25 = new ThemeDescription((View)localObject25, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outViews");
    localObject26 = this.chatListView;
    localObject4 = Theme.chat_msgOutViewsSelectedDrawable;
    localObject26 = new ThemeDescription((View)localObject26, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outViewsSelected");
    Object localObject28 = this.chatListView;
    localObject4 = Theme.chat_msgInViewsDrawable;
    localObject28 = new ThemeDescription((View)localObject28, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inViews");
    Object localObject29 = this.chatListView;
    localObject4 = Theme.chat_msgInViewsSelectedDrawable;
    localObject29 = new ThemeDescription((View)localObject29, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inViewsSelected");
    localObject4 = this.chatListView;
    Object localObject30 = Theme.chat_msgMediaViewsDrawable;
    localObject30 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject30 }, null, "chat_mediaViews");
    Object localObject31 = this.chatListView;
    localObject4 = Theme.chat_msgOutMenuDrawable;
    localObject31 = new ThemeDescription((View)localObject31, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outMenu");
    localObject4 = this.chatListView;
    Object localObject32 = Theme.chat_msgOutMenuSelectedDrawable;
    localObject32 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject32 }, null, "chat_outMenuSelected");
    Object localObject33 = this.chatListView;
    localObject4 = Theme.chat_msgInMenuDrawable;
    localObject33 = new ThemeDescription((View)localObject33, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inMenu");
    Object localObject34 = this.chatListView;
    localObject4 = Theme.chat_msgInMenuSelectedDrawable;
    localObject34 = new ThemeDescription((View)localObject34, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inMenuSelected");
    Object localObject35 = this.chatListView;
    localObject4 = Theme.chat_msgMediaMenuDrawable;
    localObject35 = new ThemeDescription((View)localObject35, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_mediaMenu");
    localObject4 = this.chatListView;
    Object localObject36 = Theme.chat_msgOutInstantDrawable;
    Object localObject37 = Theme.chat_msgOutCallDrawable;
    localObject37 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject36, localObject37 }, null, "chat_outInstant");
    localObject4 = this.chatListView;
    localObject36 = Theme.chat_msgOutCallSelectedDrawable;
    localObject36 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject36 }, null, "chat_outInstantSelected");
    Object localObject38 = this.chatListView;
    localObject4 = Theme.chat_msgInInstantDrawable;
    Object localObject39 = Theme.chat_msgInCallDrawable;
    localObject38 = new ThemeDescription((View)localObject38, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject39 }, null, "chat_inInstant");
    localObject4 = this.chatListView;
    localObject39 = Theme.chat_msgInCallSelectedDrawable;
    localObject39 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject39 }, null, "chat_inInstantSelected");
    localObject4 = this.chatListView;
    Object localObject40 = Theme.chat_msgCallUpRedDrawable;
    Object localObject41 = Theme.chat_msgCallDownRedDrawable;
    localObject40 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject40, localObject41 }, null, "calls_callReceivedRedIcon");
    localObject41 = this.chatListView;
    Object localObject42 = Theme.chat_msgCallUpGreenDrawable;
    localObject4 = Theme.chat_msgCallDownGreenDrawable;
    localObject41 = new ThemeDescription((View)localObject41, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject42, localObject4 }, null, "calls_callReceivedGreenIcon");
    localObject42 = this.chatListView;
    localObject4 = Theme.chat_msgErrorPaint;
    localObject42 = new ThemeDescription((View)localObject42, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject4, null, null, "chat_sentError");
    Object localObject43 = this.chatListView;
    localObject4 = Theme.chat_msgErrorDrawable;
    localObject43 = new ThemeDescription((View)localObject43, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_sentErrorIcon");
    localObject4 = this.chatListView;
    Object localObject44 = Theme.chat_durationPaint;
    localObject44 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject44, null, null, "chat_previewDurationText");
    Object localObject45 = this.chatListView;
    localObject4 = Theme.chat_gamePaint;
    ThemeDescription localThemeDescription29 = new ThemeDescription((View)localObject45, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject4, null, null, "chat_previewGameText");
    ThemeDescription localThemeDescription30 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inPreviewInstantText");
    ThemeDescription localThemeDescription31 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outPreviewInstantText");
    localObject45 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inPreviewInstantSelectedText");
    ThemeDescription localThemeDescription32 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outPreviewInstantSelectedText");
    localObject4 = this.chatListView;
    Object localObject46 = Theme.chat_deleteProgressPaint;
    ThemeDescription localThemeDescription33 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject46, null, null, "chat_secretTimeText");
    localObject46 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_stickerNameText");
    localObject4 = this.chatListView;
    Object localObject47 = Theme.chat_botButtonPaint;
    localObject47 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject47, null, null, "chat_botButtonText");
    Object localObject48 = this.chatListView;
    localObject4 = Theme.chat_botProgressPaint;
    ThemeDescription localThemeDescription34 = new ThemeDescription((View)localObject48, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject4, null, null, "chat_botProgress");
    ThemeDescription localThemeDescription35 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inForwardedNameText");
    ThemeDescription localThemeDescription36 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outForwardedNameText");
    ThemeDescription localThemeDescription37 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inViaBotNameText");
    ThemeDescription localThemeDescription38 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outViaBotNameText");
    ThemeDescription localThemeDescription39 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_stickerViaBotNameText");
    ThemeDescription localThemeDescription40 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inReplyLine");
    ThemeDescription localThemeDescription41 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outReplyLine");
    ThemeDescription localThemeDescription42 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_stickerReplyLine");
    ThemeDescription localThemeDescription43 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inReplyNameText");
    ThemeDescription localThemeDescription44 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outReplyNameText");
    ThemeDescription localThemeDescription45 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_stickerReplyNameText");
    ThemeDescription localThemeDescription46 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inReplyMessageText");
    ThemeDescription localThemeDescription47 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outReplyMessageText");
    ThemeDescription localThemeDescription48 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inReplyMediaMessageText");
    ThemeDescription localThemeDescription49 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outReplyMediaMessageText");
    ThemeDescription localThemeDescription50 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inReplyMediaMessageSelectedText");
    ThemeDescription localThemeDescription51 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outReplyMediaMessageSelectedText");
    ThemeDescription localThemeDescription52 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_stickerReplyMessageText");
    ThemeDescription localThemeDescription53 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inPreviewLine");
    ThemeDescription localThemeDescription54 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outPreviewLine");
    ThemeDescription localThemeDescription55 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inSiteNameText");
    ThemeDescription localThemeDescription56 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outSiteNameText");
    ThemeDescription localThemeDescription57 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inContactNameText");
    ThemeDescription localThemeDescription58 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outContactNameText");
    ThemeDescription localThemeDescription59 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inContactPhoneText");
    ThemeDescription localThemeDescription60 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outContactPhoneText");
    ThemeDescription localThemeDescription61 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_mediaProgress");
    ThemeDescription localThemeDescription62 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioProgress");
    ThemeDescription localThemeDescription63 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioProgress");
    ThemeDescription localThemeDescription64 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioSelectedProgress");
    ThemeDescription localThemeDescription65 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioSelectedProgress");
    ThemeDescription localThemeDescription66 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_mediaTimeText");
    ThemeDescription localThemeDescription67 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inTimeText");
    ThemeDescription localThemeDescription68 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outTimeText");
    ThemeDescription localThemeDescription69 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inTimeSelectedText");
    ThemeDescription localThemeDescription70 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outTimeSelectedText");
    ThemeDescription localThemeDescription71 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioPerfomerText");
    ThemeDescription localThemeDescription72 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioPerfomerText");
    localObject48 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioTitleText");
    ThemeDescription localThemeDescription73 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioTitleText");
    ThemeDescription localThemeDescription74 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioDurationText");
    ThemeDescription localThemeDescription75 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioDurationText");
    ThemeDescription localThemeDescription76 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioDurationSelectedText");
    ThemeDescription localThemeDescription77 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioDurationSelectedText");
    ThemeDescription localThemeDescription78 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioSeekbar");
    ThemeDescription localThemeDescription79 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioSeekbar");
    ThemeDescription localThemeDescription80 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioSeekbarSelected");
    ThemeDescription localThemeDescription81 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioSeekbarSelected");
    ThemeDescription localThemeDescription82 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioSeekbarFill");
    ThemeDescription localThemeDescription83 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inAudioCacheSeekbar");
    ThemeDescription localThemeDescription84 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioSeekbarFill");
    ThemeDescription localThemeDescription85 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outAudioCacheSeekbar");
    ThemeDescription localThemeDescription86 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inVoiceSeekbar");
    ThemeDescription localThemeDescription87 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outVoiceSeekbar");
    ThemeDescription localThemeDescription88 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inVoiceSeekbarSelected");
    ThemeDescription localThemeDescription89 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outVoiceSeekbarSelected");
    ThemeDescription localThemeDescription90 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inVoiceSeekbarFill");
    ThemeDescription localThemeDescription91 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outVoiceSeekbarFill");
    ThemeDescription localThemeDescription92 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inFileProgress");
    ThemeDescription localThemeDescription93 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outFileProgress");
    ThemeDescription localThemeDescription94 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inFileProgressSelected");
    ThemeDescription localThemeDescription95 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outFileProgressSelected");
    ThemeDescription localThemeDescription96 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inFileNameText");
    ThemeDescription localThemeDescription97 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outFileNameText");
    ThemeDescription localThemeDescription98 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inFileInfoText");
    ThemeDescription localThemeDescription99 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outFileInfoText");
    ThemeDescription localThemeDescription100 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inFileInfoSelectedText");
    ThemeDescription localThemeDescription101 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outFileInfoSelectedText");
    ThemeDescription localThemeDescription102 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inFileBackground");
    ThemeDescription localThemeDescription103 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outFileBackground");
    ThemeDescription localThemeDescription104 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inFileBackgroundSelected");
    ThemeDescription localThemeDescription105 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outFileBackgroundSelected");
    ThemeDescription localThemeDescription106 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inVenueNameText");
    ThemeDescription localThemeDescription107 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outVenueNameText");
    ThemeDescription localThemeDescription108 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inVenueInfoText");
    ThemeDescription localThemeDescription109 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outVenueInfoText");
    ThemeDescription localThemeDescription110 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_inVenueInfoSelectedText");
    ThemeDescription localThemeDescription111 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_outVenueInfoSelectedText");
    ThemeDescription localThemeDescription112 = new ThemeDescription(this.chatListView, 0, new Class[] { ChatMessageCell.class }, null, null, null, "chat_mediaInfoText");
    localObject4 = this.chatListView;
    Object localObject49 = Theme.chat_urlPaint;
    localObject49 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject49, null, null, "chat_linkSelectBackground");
    localObject4 = this.chatListView;
    Object localObject50 = Theme.chat_textSearchSelectionPaint;
    localObject50 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, (Paint)localObject50, null, null, "chat_textSelectBackground");
    Object localObject51 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject4 = Theme.chat_fileStatesDrawable[0][0];
    Object localObject52 = Theme.chat_fileStatesDrawable[1][0];
    Object localObject53 = Theme.chat_fileStatesDrawable[2][0];
    Object localObject54 = Theme.chat_fileStatesDrawable[3][0];
    Object localObject55 = Theme.chat_fileStatesDrawable[4][0];
    localObject53 = new ThemeDescription((View)localObject51, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject52, localObject53, localObject54, localObject55 }, null, "chat_outLoader");
    localObject54 = this.chatListView;
    localObject51 = Theme.chat_fileStatesDrawable[0][0];
    localObject55 = Theme.chat_fileStatesDrawable[1][0];
    localObject4 = Theme.chat_fileStatesDrawable[2][0];
    localObject52 = Theme.chat_fileStatesDrawable[3][0];
    Object localObject56 = Theme.chat_fileStatesDrawable[4][0];
    localObject55 = new ThemeDescription((View)localObject54, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject51, localObject55, localObject4, localObject52, localObject56 }, null, "chat_outBubble");
    localObject54 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    Object localObject57 = Theme.chat_fileStatesDrawable[0][1];
    localObject4 = Theme.chat_fileStatesDrawable[1][1];
    localObject52 = Theme.chat_fileStatesDrawable[2][1];
    localObject51 = Theme.chat_fileStatesDrawable[3][1];
    localObject56 = Theme.chat_fileStatesDrawable[4][1];
    localObject51 = new ThemeDescription((View)localObject54, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject57, localObject4, localObject52, localObject51, localObject56 }, null, "chat_outLoaderSelected");
    localObject54 = this.chatListView;
    localObject52 = Theme.chat_fileStatesDrawable[0][1];
    localObject56 = Theme.chat_fileStatesDrawable[1][1];
    localObject4 = Theme.chat_fileStatesDrawable[2][1];
    localObject57 = Theme.chat_fileStatesDrawable[3][1];
    Object localObject58 = Theme.chat_fileStatesDrawable[4][1];
    localObject52 = new ThemeDescription((View)localObject54, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject52, localObject56, localObject4, localObject57, localObject58 }, null, "chat_outBubbleSelected");
    Object localObject59 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject56 = Theme.chat_fileStatesDrawable[5][0];
    localObject58 = Theme.chat_fileStatesDrawable[6][0];
    localObject57 = Theme.chat_fileStatesDrawable[7][0];
    localObject4 = Theme.chat_fileStatesDrawable[8][0];
    localObject54 = Theme.chat_fileStatesDrawable[9][0];
    localObject54 = new ThemeDescription((View)localObject59, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject56, localObject58, localObject57, localObject4, localObject54 }, null, "chat_inLoader");
    localObject58 = this.chatListView;
    localObject59 = Theme.chat_fileStatesDrawable[5][0];
    localObject4 = Theme.chat_fileStatesDrawable[6][0];
    localObject57 = Theme.chat_fileStatesDrawable[7][0];
    Object localObject60 = Theme.chat_fileStatesDrawable[8][0];
    localObject56 = Theme.chat_fileStatesDrawable[9][0];
    localObject56 = new ThemeDescription((View)localObject58, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject59, localObject4, localObject57, localObject60, localObject56 }, null, "chat_inBubble");
    Object localObject61 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject59 = Theme.chat_fileStatesDrawable[5][1];
    localObject4 = Theme.chat_fileStatesDrawable[6][1];
    localObject57 = Theme.chat_fileStatesDrawable[7][1];
    localObject58 = Theme.chat_fileStatesDrawable[8][1];
    localObject60 = Theme.chat_fileStatesDrawable[9][1];
    localObject57 = new ThemeDescription((View)localObject61, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject59, localObject4, localObject57, localObject58, localObject60 }, null, "chat_inLoaderSelected");
    Object localObject62 = this.chatListView;
    localObject61 = Theme.chat_fileStatesDrawable[5][1];
    localObject58 = Theme.chat_fileStatesDrawable[6][1];
    localObject59 = Theme.chat_fileStatesDrawable[7][1];
    localObject60 = Theme.chat_fileStatesDrawable[8][1];
    localObject4 = Theme.chat_fileStatesDrawable[9][1];
    localObject58 = new ThemeDescription((View)localObject62, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject61, localObject58, localObject59, localObject60, localObject4 }, null, "chat_inBubbleSelected");
    localObject4 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject61 = Theme.chat_photoStatesDrawables[0][0];
    localObject62 = Theme.chat_photoStatesDrawables[1][0];
    localObject60 = Theme.chat_photoStatesDrawables[2][0];
    localObject59 = Theme.chat_photoStatesDrawables[3][0];
    localObject59 = new ThemeDescription((View)localObject4, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject61, localObject62, localObject60, localObject59 }, null, "chat_mediaLoaderPhoto");
    localObject62 = this.chatListView;
    localObject61 = Theme.chat_photoStatesDrawables[0][0];
    localObject4 = Theme.chat_photoStatesDrawables[1][0];
    localObject60 = Theme.chat_photoStatesDrawables[2][0];
    Object localObject63 = Theme.chat_photoStatesDrawables[3][0];
    localObject60 = new ThemeDescription((View)localObject62, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject61, localObject4, localObject60, localObject63 }, null, "chat_mediaLoaderPhotoIcon");
    Object localObject64 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject4 = Theme.chat_photoStatesDrawables[0][1];
    localObject61 = Theme.chat_photoStatesDrawables[1][1];
    localObject63 = Theme.chat_photoStatesDrawables[2][1];
    localObject62 = Theme.chat_photoStatesDrawables[3][1];
    localObject61 = new ThemeDescription((View)localObject64, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject61, localObject63, localObject62 }, null, "chat_mediaLoaderPhotoSelected");
    localObject64 = this.chatListView;
    Object localObject65 = Theme.chat_photoStatesDrawables[0][1];
    localObject62 = Theme.chat_photoStatesDrawables[1][1];
    localObject63 = Theme.chat_photoStatesDrawables[2][1];
    localObject4 = Theme.chat_photoStatesDrawables[3][1];
    localObject62 = new ThemeDescription((View)localObject64, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject65, localObject62, localObject63, localObject4 }, null, "chat_mediaLoaderPhotoIconSelected");
    localObject63 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject4 = Theme.chat_photoStatesDrawables[7][0];
    localObject64 = Theme.chat_photoStatesDrawables[8][0];
    localObject63 = new ThemeDescription((View)localObject63, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject64 }, null, "chat_outLoaderPhoto");
    localObject4 = this.chatListView;
    localObject64 = Theme.chat_photoStatesDrawables[7][0];
    localObject65 = Theme.chat_photoStatesDrawables[8][0];
    localObject64 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject64, localObject65 }, null, "chat_outLoaderPhotoIcon");
    Object localObject66 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject4 = Theme.chat_photoStatesDrawables[7][1];
    localObject65 = Theme.chat_photoStatesDrawables[8][1];
    localObject65 = new ThemeDescription((View)localObject66, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject65 }, null, "chat_outLoaderPhotoSelected");
    localObject4 = this.chatListView;
    localObject66 = Theme.chat_photoStatesDrawables[7][1];
    Object localObject67 = Theme.chat_photoStatesDrawables[8][1];
    localObject66 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject66, localObject67 }, null, "chat_outLoaderPhotoIconSelected");
    localObject67 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject4 = Theme.chat_photoStatesDrawables[10][0];
    Object localObject68 = Theme.chat_photoStatesDrawables[11][0];
    localObject67 = new ThemeDescription((View)localObject67, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4, localObject68 }, null, "chat_inLoaderPhoto");
    localObject4 = this.chatListView;
    localObject68 = Theme.chat_photoStatesDrawables[10][0];
    Object localObject69 = Theme.chat_photoStatesDrawables[11][0];
    localObject68 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject68, localObject69 }, null, "chat_inLoaderPhotoIcon");
    localObject69 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    Object localObject70 = Theme.chat_photoStatesDrawables[10][1];
    localObject4 = Theme.chat_photoStatesDrawables[11][1];
    localObject69 = new ThemeDescription((View)localObject69, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject70, localObject4 }, null, "chat_inLoaderPhotoSelected");
    Object localObject71 = this.chatListView;
    localObject70 = Theme.chat_photoStatesDrawables[10][1];
    localObject4 = Theme.chat_photoStatesDrawables[11][1];
    localObject70 = new ThemeDescription((View)localObject71, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject70, localObject4 }, null, "chat_inLoaderPhotoIconSelected");
    localObject71 = this.chatListView;
    localObject4 = Theme.chat_photoStatesDrawables[9][0];
    localObject71 = new ThemeDescription((View)localObject71, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outFileIcon");
    Object localObject72 = this.chatListView;
    localObject4 = Theme.chat_photoStatesDrawables[9][1];
    localObject72 = new ThemeDescription((View)localObject72, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outFileSelectedIcon");
    Object localObject73 = this.chatListView;
    localObject4 = Theme.chat_photoStatesDrawables[12][0];
    localObject73 = new ThemeDescription((View)localObject73, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inFileIcon");
    Object localObject74 = this.chatListView;
    localObject4 = Theme.chat_photoStatesDrawables[12][1];
    localObject74 = new ThemeDescription((View)localObject74, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inFileSelectedIcon");
    localObject4 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    Object localObject75 = Theme.chat_contactDrawable[0];
    localObject75 = new ThemeDescription((View)localObject4, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject75 }, null, "chat_inContactBackground");
    localObject4 = this.chatListView;
    Object localObject76 = Theme.chat_contactDrawable[0];
    localObject76 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject76 }, null, "chat_inContactIcon");
    localObject4 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    Object localObject77 = Theme.chat_contactDrawable[1];
    localObject77 = new ThemeDescription((View)localObject4, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject77 }, null, "chat_outContactBackground");
    localObject4 = this.chatListView;
    Object localObject78 = Theme.chat_contactDrawable[1];
    localObject78 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject78 }, null, "chat_outContactIcon");
    Object localObject79 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject4 = Theme.chat_locationDrawable[0];
    localObject79 = new ThemeDescription((View)localObject79, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_inLocationBackground");
    localObject4 = this.chatListView;
    Object localObject80 = Theme.chat_locationDrawable[0];
    localObject80 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject80 }, null, "chat_inLocationIcon");
    Object localObject81 = this.chatListView;
    i = ThemeDescription.FLAG_BACKGROUNDFILTER;
    localObject4 = Theme.chat_locationDrawable[1];
    localObject81 = new ThemeDescription((View)localObject81, i, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject4 }, null, "chat_outLocationBackground");
    localObject4 = this.chatListView;
    Object localObject82 = Theme.chat_locationDrawable[1];
    ThemeDescription localThemeDescription113 = new ThemeDescription((View)localObject4, 0, new Class[] { ChatMessageCell.class }, null, new Drawable[] { localObject82 }, null, "chat_outLocationIcon");
    ThemeDescription localThemeDescription114 = new ThemeDescription(this.bottomOverlayChat, 0, null, Theme.chat_composeBackgroundPaint, null, null, "chat_messagePanelBackground");
    ThemeDescription localThemeDescription115 = new ThemeDescription(this.bottomOverlayChat, 0, null, null, new Drawable[] { Theme.chat_composeShadowDrawable }, null, "chat_messagePanelShadow");
    ThemeDescription localThemeDescription116 = new ThemeDescription(this.bottomOverlayChatText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_fieldOverlayText");
    ThemeDescription localThemeDescription117 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, "chat_serviceText");
    ThemeDescription localThemeDescription118 = new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, "chat_serviceText");
    ThemeDescription localThemeDescription119 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[] { ChatUnreadCell.class }, new String[] { "backgroundLayout" }, null, null, null, "chat_unreadMessagesStartBackground");
    ThemeDescription localThemeDescription120 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[] { ChatUnreadCell.class }, new String[] { "imageView" }, null, null, null, "chat_unreadMessagesStartArrowIcon");
    localObject82 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[] { ChatUnreadCell.class }, new String[] { "textView" }, null, null, null, "chat_unreadMessagesStartText");
    ThemeDescription localThemeDescription121 = new ThemeDescription(this.progressView2, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground");
    ThemeDescription localThemeDescription122 = new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, "chat_serviceBackground");
    ThemeDescription localThemeDescription123 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_SERVICEBACKGROUND, new Class[] { ChatLoadingCell.class }, new String[] { "textView" }, null, null, null, "chat_serviceBackground");
    ThemeDescription localThemeDescription124 = new ThemeDescription(this.chatListView, ThemeDescription.FLAG_PROGRESSBAR, new Class[] { ChatLoadingCell.class }, new String[] { "textView" }, null, null, null, "chat_serviceText");
    ThemeDescription localThemeDescription125;
    if (this.avatarContainer != null)
    {
      localObject4 = this.avatarContainer.getTimeItem();
      localThemeDescription125 = new ThemeDescription((View)localObject4, 0, null, null, null, null, "chat_secretTimerBackground");
      if (this.avatarContainer == null) {
        break label9907;
      }
    }
    label9907:
    for (localObject4 = this.avatarContainer.getTimeItem();; localObject4 = null)
    {
      return new ThemeDescription[] { localThemeDescription1, localThemeDescription2, localThemeDescription3, localThemeDescription4, localThemeDescription5, localThemeDescription6, localThemeDescription7, localThemeDescription8, localThemeDescription9, localThemeDescription10, localThemeDescription11, localThemeDescription12, localThemeDescription13, localThemeDescription14, localThemeDescription15, localThemeDescription16, localThemeDescription17, localObject2, localThemeDescription18, localThemeDescription19, localThemeDescription20, localThemeDescription21, localThemeDescription22, localThemeDescription23, localObject3, localThemeDescription24, localObject1, localThemeDescription25, localObject5, localObject6, localObject7, localObject8, localObject9, localObject10, localObject11, localObject12, localObject14, localObject13, localObject16, localThemeDescription26, localThemeDescription27, localThemeDescription28, localObject15, localObject17, localObject18, localObject19, localObject20, localObject21, localObject22, localObject24, localObject23, localObject27, localObject25, localObject26, localObject28, localObject29, localObject30, localObject31, localObject32, localObject33, localObject34, localObject35, localObject37, localObject36, localObject38, localObject39, localObject40, localObject41, localObject42, localObject43, localObject44, localThemeDescription29, localThemeDescription30, localThemeDescription31, localObject45, localThemeDescription32, localThemeDescription33, localObject46, localObject47, localThemeDescription34, localThemeDescription35, localThemeDescription36, localThemeDescription37, localThemeDescription38, localThemeDescription39, localThemeDescription40, localThemeDescription41, localThemeDescription42, localThemeDescription43, localThemeDescription44, localThemeDescription45, localThemeDescription46, localThemeDescription47, localThemeDescription48, localThemeDescription49, localThemeDescription50, localThemeDescription51, localThemeDescription52, localThemeDescription53, localThemeDescription54, localThemeDescription55, localThemeDescription56, localThemeDescription57, localThemeDescription58, localThemeDescription59, localThemeDescription60, localThemeDescription61, localThemeDescription62, localThemeDescription63, localThemeDescription64, localThemeDescription65, localThemeDescription66, localThemeDescription67, localThemeDescription68, localThemeDescription69, localThemeDescription70, localThemeDescription71, localThemeDescription72, localObject48, localThemeDescription73, localThemeDescription74, localThemeDescription75, localThemeDescription76, localThemeDescription77, localThemeDescription78, localThemeDescription79, localThemeDescription80, localThemeDescription81, localThemeDescription82, localThemeDescription83, localThemeDescription84, localThemeDescription85, localThemeDescription86, localThemeDescription87, localThemeDescription88, localThemeDescription89, localThemeDescription90, localThemeDescription91, localThemeDescription92, localThemeDescription93, localThemeDescription94, localThemeDescription95, localThemeDescription96, localThemeDescription97, localThemeDescription98, localThemeDescription99, localThemeDescription100, localThemeDescription101, localThemeDescription102, localThemeDescription103, localThemeDescription104, localThemeDescription105, localThemeDescription106, localThemeDescription107, localThemeDescription108, localThemeDescription109, localThemeDescription110, localThemeDescription111, localThemeDescription112, localObject49, localObject50, localObject53, localObject55, localObject51, localObject52, localObject54, localObject56, localObject57, localObject58, localObject59, localObject60, localObject61, localObject62, localObject63, localObject64, localObject65, localObject66, localObject67, localObject68, localObject69, localObject70, localObject71, localObject72, localObject73, localObject74, localObject75, localObject76, localObject77, localObject78, localObject79, localObject80, localObject81, localThemeDescription113, localThemeDescription114, localThemeDescription115, localThemeDescription116, localThemeDescription117, localThemeDescription118, localThemeDescription119, localThemeDescription120, localObject82, localThemeDescription121, localThemeDescription122, localThemeDescription123, localThemeDescription124, localThemeDescription125, new ThemeDescription((View)localObject4, 0, null, null, null, null, "chat_secretTimerText") };
      localObject4 = null;
      break;
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    fixLayout();
    if ((this.visibleDialog instanceof DatePickerDialog)) {
      this.visibleDialog.dismiss();
    }
  }
  
  public boolean onFragmentCreate()
  {
    super.onFragmentCreate();
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStarted);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
    NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
    loadMessages(true);
    loadAdmins();
    return true;
  }
  
  public void onFragmentDestroy()
  {
    super.onFragmentDestroy();
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStarted);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
    NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
    NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
  }
  
  public void onPause()
  {
    super.onPause();
    this.paused = true;
    this.wasPaused = true;
  }
  
  protected void onRemoveFromParent()
  {
    MediaController.getInstance().setTextureView(this.videoTextureView, null, null, false);
  }
  
  public void onResume()
  {
    super.onResume();
    this.paused = false;
    checkScrollForLoad(false);
    if (this.wasPaused)
    {
      this.wasPaused = false;
      if (this.chatAdapter != null) {
        this.chatAdapter.notifyDataSetChanged();
      }
    }
    fixLayout();
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(false);
    if (paramBoolean1) {
      this.openAnimationEnded = true;
    }
  }
  
  public void onTransitionAnimationStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    NotificationCenter.getInstance(this.currentAccount).setAllowedNotificationsDutingAnimation(new int[] { NotificationCenter.chatInfoDidLoaded, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoaded, NotificationCenter.botKeyboardDidLoaded });
    NotificationCenter.getInstance(this.currentAccount).setAnimationInProgress(true);
    if (paramBoolean1) {
      this.openAnimationEnded = false;
    }
  }
  
  public void showOpenUrlAlert(final String paramString, boolean paramBoolean)
  {
    if ((Browser.isInternalUrl(paramString, null)) || (!paramBoolean)) {
      Browser.openUrl(getParentActivity(), paramString, true);
    }
    for (;;)
    {
      return;
      AlertDialog.Builder localBuilder = new AlertDialog.Builder(getParentActivity());
      localBuilder.setTitle(LocaleController.getString("AppName", NUM));
      localBuilder.setMessage(LocaleController.formatString("OpenUrlAlert", NUM, new Object[] { paramString }));
      localBuilder.setPositiveButton(LocaleController.getString("Open", NUM), new DialogInterface.OnClickListener()
      {
        public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt)
        {
          Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), paramString, true);
        }
      });
      localBuilder.setNegativeButton(LocaleController.getString("Cancel", NUM), null);
      showDialog(localBuilder.create());
    }
  }
  
  public class ChatActivityAdapter
    extends RecyclerView.Adapter
  {
    private int loadingUpRow;
    private Context mContext;
    private int messagesEndRow;
    private int messagesStartRow;
    private int rowCount;
    
    public ChatActivityAdapter(Context paramContext)
    {
      this.mContext = paramContext;
    }
    
    public int getItemCount()
    {
      return this.rowCount;
    }
    
    public long getItemId(int paramInt)
    {
      return -1L;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((paramInt >= this.messagesStartRow) && (paramInt < this.messagesEndRow)) {}
      for (paramInt = ((MessageObject)ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (paramInt - this.messagesStartRow) - 1)).contentType;; paramInt = 4) {
        return paramInt;
      }
    }
    
    public void notifyDataSetChanged()
    {
      updateRows();
      try
      {
        super.notifyDataSetChanged();
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void notifyItemChanged(int paramInt)
    {
      updateRows();
      try
      {
        super.notifyItemChanged(paramInt);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void notifyItemInserted(int paramInt)
    {
      updateRows();
      try
      {
        super.notifyItemInserted(paramInt);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemMoved(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemRangeChanged(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemRangeInserted(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      updateRows();
      try
      {
        super.notifyItemRangeRemoved(paramInt1, paramInt2);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void notifyItemRemoved(int paramInt)
    {
      updateRows();
      try
      {
        super.notifyItemRemoved(paramInt);
        return;
      }
      catch (Exception localException)
      {
        for (;;)
        {
          FileLog.e(localException);
        }
      }
    }
    
    public void onBindViewHolder(RecyclerView.ViewHolder paramViewHolder, int paramInt)
    {
      boolean bool1;
      if (paramInt == this.loadingUpRow)
      {
        paramViewHolder = (ChatLoadingCell)paramViewHolder.itemView;
        if (ChannelAdminLogActivity.this.loadsCount > 1)
        {
          bool1 = true;
          paramViewHolder.setProgressVisible(bool1);
        }
      }
      for (;;)
      {
        return;
        bool1 = false;
        break;
        if ((paramInt >= this.messagesStartRow) && (paramInt < this.messagesEndRow))
        {
          MessageObject localMessageObject1 = (MessageObject)ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (paramInt - this.messagesStartRow) - 1);
          View localView = paramViewHolder.itemView;
          if ((localView instanceof ChatMessageCell))
          {
            ChatMessageCell localChatMessageCell = (ChatMessageCell)localView;
            localChatMessageCell.isChat = true;
            int i = getItemViewType(paramInt + 1);
            int j = getItemViewType(paramInt - 1);
            label254:
            boolean bool2;
            if ((!(localMessageObject1.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) && (i == paramViewHolder.getItemViewType()))
            {
              MessageObject localMessageObject2 = (MessageObject)ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (paramInt + 1 - this.messagesStartRow) - 1);
              if ((localMessageObject2.isOutOwner() == localMessageObject1.isOutOwner()) && (localMessageObject2.messageOwner.from_id == localMessageObject1.messageOwner.from_id) && (Math.abs(localMessageObject2.messageOwner.date - localMessageObject1.messageOwner.date) <= 300))
              {
                bool1 = true;
                if (j != paramViewHolder.getItemViewType()) {
                  break label441;
                }
                paramViewHolder = (MessageObject)ChannelAdminLogActivity.this.messages.get(ChannelAdminLogActivity.this.messages.size() - (paramInt - this.messagesStartRow));
                if (((paramViewHolder.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup)) || (paramViewHolder.isOutOwner() != localMessageObject1.isOutOwner()) || (paramViewHolder.messageOwner.from_id != localMessageObject1.messageOwner.from_id) || (Math.abs(paramViewHolder.messageOwner.date - localMessageObject1.messageOwner.date) > 300)) {
                  break label435;
                }
                bool2 = true;
              }
            }
            for (;;)
            {
              localChatMessageCell.setMessageObject(localMessageObject1, null, bool1, bool2);
              if (((localView instanceof ChatMessageCell)) && (DownloadController.getInstance(ChannelAdminLogActivity.this.currentAccount).canDownloadMedia(localMessageObject1))) {
                ((ChatMessageCell)localView).downloadAudioIfNeed();
              }
              localChatMessageCell.setHighlighted(false);
              localChatMessageCell.setHighlightedText(null);
              break;
              bool1 = false;
              break label254;
              bool1 = false;
              break label254;
              label435:
              bool2 = false;
              continue;
              label441:
              bool2 = false;
            }
          }
          if ((localView instanceof ChatActionCell))
          {
            paramViewHolder = (ChatActionCell)localView;
            paramViewHolder.setMessageObject(localMessageObject1);
            paramViewHolder.setAlpha(1.0F);
          }
        }
      }
    }
    
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      paramViewGroup = null;
      if (paramInt == 0) {
        if (!ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty())
        {
          paramViewGroup = (View)ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
          ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
          ChatMessageCell localChatMessageCell = (ChatMessageCell)paramViewGroup;
          localChatMessageCell.setDelegate(new ChatMessageCell.ChatMessageCellDelegate()
          {
            public boolean canPerformActions()
            {
              return true;
            }
            
            public void didLongPressed(ChatMessageCell paramAnonymousChatMessageCell)
            {
              ChannelAdminLogActivity.this.createMenu(paramAnonymousChatMessageCell);
            }
            
            public void didPressedBotButton(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.KeyboardButton paramAnonymousKeyboardButton) {}
            
            public void didPressedCancelSendButton(ChatMessageCell paramAnonymousChatMessageCell) {}
            
            public void didPressedChannelAvatar(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.Chat paramAnonymousChat, int paramAnonymousInt)
            {
              if ((paramAnonymousChat != null) && (paramAnonymousChat != ChannelAdminLogActivity.this.currentChat))
              {
                paramAnonymousChatMessageCell = new Bundle();
                paramAnonymousChatMessageCell.putInt("chat_id", paramAnonymousChat.id);
                if (paramAnonymousInt != 0) {
                  paramAnonymousChatMessageCell.putInt("message_id", paramAnonymousInt);
                }
                if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat(paramAnonymousChatMessageCell, ChannelAdminLogActivity.this)) {
                  ChannelAdminLogActivity.this.presentFragment(new ChatActivity(paramAnonymousChatMessageCell), true);
                }
              }
            }
            
            public void didPressedImage(ChatMessageCell paramAnonymousChatMessageCell)
            {
              MessageObject localMessageObject = paramAnonymousChatMessageCell.getMessageObject();
              if (localMessageObject.type == 13) {
                ChannelAdminLogActivity.this.showDialog(new StickersAlert(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this, localMessageObject.getInputStickerSet(), null, null));
              }
              for (;;)
              {
                return;
                if ((localMessageObject.isVideo()) || (localMessageObject.type == 1) || ((localMessageObject.type == 0) && (!localMessageObject.isWebpageDocument())) || (localMessageObject.isGif()))
                {
                  PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
                  PhotoViewer.getInstance().openPhoto(localMessageObject, 0L, 0L, ChannelAdminLogActivity.this.provider);
                }
                else
                {
                  Object localObject;
                  if (localMessageObject.type == 3)
                  {
                    localObject = null;
                    paramAnonymousChatMessageCell = (ChatMessageCell)localObject;
                    for (;;)
                    {
                      try
                      {
                        if (localMessageObject.messageOwner.attachPath != null)
                        {
                          paramAnonymousChatMessageCell = (ChatMessageCell)localObject;
                          if (localMessageObject.messageOwner.attachPath.length() != 0)
                          {
                            paramAnonymousChatMessageCell = new java/io/File;
                            paramAnonymousChatMessageCell.<init>(localMessageObject.messageOwner.attachPath);
                          }
                        }
                        if (paramAnonymousChatMessageCell != null)
                        {
                          localObject = paramAnonymousChatMessageCell;
                          if (paramAnonymousChatMessageCell.exists()) {}
                        }
                        else
                        {
                          localObject = FileLoader.getPathToMessage(localMessageObject.messageOwner);
                        }
                        paramAnonymousChatMessageCell = new android/content/Intent;
                        paramAnonymousChatMessageCell.<init>("android.intent.action.VIEW");
                        if (Build.VERSION.SDK_INT < 24) {
                          break label286;
                        }
                        paramAnonymousChatMessageCell.setFlags(1);
                        paramAnonymousChatMessageCell.setDataAndType(FileProvider.getUriForFile(ChannelAdminLogActivity.this.getParentActivity(), "org.telegram.messenger.beta.provider", (File)localObject), "video/mp4");
                        ChannelAdminLogActivity.this.getParentActivity().startActivityForResult(paramAnonymousChatMessageCell, 500);
                      }
                      catch (Exception paramAnonymousChatMessageCell)
                      {
                        ChannelAdminLogActivity.this.alertUserOpenError(localMessageObject);
                      }
                      break;
                      label286:
                      paramAnonymousChatMessageCell.setDataAndType(Uri.fromFile((File)localObject), "video/mp4");
                    }
                  }
                  if (localMessageObject.type == 4)
                  {
                    if (AndroidUtilities.isGoogleMapsInstalled(ChannelAdminLogActivity.this))
                    {
                      paramAnonymousChatMessageCell = new LocationActivity(0);
                      paramAnonymousChatMessageCell.setMessageObject(localMessageObject);
                      ChannelAdminLogActivity.this.presentFragment(paramAnonymousChatMessageCell);
                    }
                  }
                  else if ((localMessageObject.type == 9) || (localMessageObject.type == 0))
                  {
                    if (localMessageObject.getDocumentName().toLowerCase().endsWith("attheme"))
                    {
                      File localFile = null;
                      paramAnonymousChatMessageCell = localFile;
                      if (localMessageObject.messageOwner.attachPath != null)
                      {
                        paramAnonymousChatMessageCell = localFile;
                        if (localMessageObject.messageOwner.attachPath.length() != 0)
                        {
                          localObject = new File(localMessageObject.messageOwner.attachPath);
                          paramAnonymousChatMessageCell = localFile;
                          if (((File)localObject).exists()) {
                            paramAnonymousChatMessageCell = (ChatMessageCell)localObject;
                          }
                        }
                      }
                      localObject = paramAnonymousChatMessageCell;
                      if (paramAnonymousChatMessageCell == null)
                      {
                        localFile = FileLoader.getPathToMessage(localMessageObject.messageOwner);
                        localObject = paramAnonymousChatMessageCell;
                        if (localFile.exists()) {
                          localObject = localFile;
                        }
                      }
                      if (ChannelAdminLogActivity.this.chatLayoutManager != null)
                      {
                        if (ChannelAdminLogActivity.this.chatLayoutManager.findLastVisibleItemPosition() >= ChannelAdminLogActivity.this.chatLayoutManager.getItemCount() - 1) {
                          break label637;
                        }
                        ChannelAdminLogActivity.access$7002(ChannelAdminLogActivity.this, ChannelAdminLogActivity.this.chatLayoutManager.findFirstVisibleItemPosition());
                        paramAnonymousChatMessageCell = (RecyclerListView.Holder)ChannelAdminLogActivity.this.chatListView.findViewHolderForAdapterPosition(ChannelAdminLogActivity.this.scrollToPositionOnRecreate);
                        if (paramAnonymousChatMessageCell == null) {
                          break label622;
                        }
                        ChannelAdminLogActivity.access$7102(ChannelAdminLogActivity.this, paramAnonymousChatMessageCell.itemView.getTop());
                      }
                      for (;;)
                      {
                        paramAnonymousChatMessageCell = Theme.applyThemeFile((File)localObject, localMessageObject.getDocumentName(), true);
                        if (paramAnonymousChatMessageCell == null) {
                          break label652;
                        }
                        ChannelAdminLogActivity.this.presentFragment(new ThemePreviewActivity((File)localObject, paramAnonymousChatMessageCell));
                        break;
                        label622:
                        ChannelAdminLogActivity.access$7002(ChannelAdminLogActivity.this, -1);
                        continue;
                        label637:
                        ChannelAdminLogActivity.access$7002(ChannelAdminLogActivity.this, -1);
                      }
                      label652:
                      ChannelAdminLogActivity.access$7002(ChannelAdminLogActivity.this, -1);
                    }
                    try
                    {
                      AndroidUtilities.openForView(localMessageObject, ChannelAdminLogActivity.this.getParentActivity());
                    }
                    catch (Exception paramAnonymousChatMessageCell)
                    {
                      ChannelAdminLogActivity.this.alertUserOpenError(localMessageObject);
                    }
                  }
                }
              }
            }
            
            public void didPressedInstantButton(ChatMessageCell paramAnonymousChatMessageCell, int paramAnonymousInt)
            {
              paramAnonymousChatMessageCell = paramAnonymousChatMessageCell.getMessageObject();
              if (paramAnonymousInt == 0) {
                if ((paramAnonymousChatMessageCell.messageOwner.media != null) && (paramAnonymousChatMessageCell.messageOwner.media.webpage != null) && (paramAnonymousChatMessageCell.messageOwner.media.webpage.cached_page != null))
                {
                  ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                  ArticleViewer.getInstance().open(paramAnonymousChatMessageCell);
                }
              }
              for (;;)
              {
                return;
                Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), paramAnonymousChatMessageCell.messageOwner.media.webpage.url);
              }
            }
            
            public void didPressedOther(ChatMessageCell paramAnonymousChatMessageCell)
            {
              ChannelAdminLogActivity.this.createMenu(paramAnonymousChatMessageCell);
            }
            
            public void didPressedReplyMessage(ChatMessageCell paramAnonymousChatMessageCell, int paramAnonymousInt) {}
            
            public void didPressedShare(ChatMessageCell paramAnonymousChatMessageCell)
            {
              if (ChannelAdminLogActivity.this.getParentActivity() == null) {
                return;
              }
              ChannelAdminLogActivity localChannelAdminLogActivity = ChannelAdminLogActivity.this;
              Context localContext = ChannelAdminLogActivity.ChatActivityAdapter.this.mContext;
              paramAnonymousChatMessageCell = paramAnonymousChatMessageCell.getMessageObject();
              if ((ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat)) && (!ChannelAdminLogActivity.this.currentChat.megagroup) && (ChannelAdminLogActivity.this.currentChat.username != null) && (ChannelAdminLogActivity.this.currentChat.username.length() > 0)) {}
              for (boolean bool = true;; bool = false)
              {
                localChannelAdminLogActivity.showDialog(ShareAlert.createShareAlert(localContext, paramAnonymousChatMessageCell, null, bool, null, false));
                break;
              }
            }
            
            public void didPressedUrl(MessageObject paramAnonymousMessageObject, CharacterStyle paramAnonymousCharacterStyle, boolean paramAnonymousBoolean)
            {
              if (paramAnonymousCharacterStyle == null) {}
              for (;;)
              {
                return;
                if ((paramAnonymousCharacterStyle instanceof URLSpanMono))
                {
                  ((URLSpanMono)paramAnonymousCharacterStyle).copyToClipboard();
                  Toast.makeText(ChannelAdminLogActivity.this.getParentActivity(), LocaleController.getString("TextCopied", NUM), 0).show();
                }
                else if ((paramAnonymousCharacterStyle instanceof URLSpanUserMention))
                {
                  paramAnonymousMessageObject = MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).getUser(Utilities.parseInt(((URLSpanUserMention)paramAnonymousCharacterStyle).getURL()));
                  if (paramAnonymousMessageObject != null) {
                    MessagesController.openChatOrProfileWith(paramAnonymousMessageObject, null, ChannelAdminLogActivity.this, 0, false);
                  }
                }
                else if ((paramAnonymousCharacterStyle instanceof URLSpanNoUnderline))
                {
                  paramAnonymousCharacterStyle = ((URLSpanNoUnderline)paramAnonymousCharacterStyle).getURL();
                  if (paramAnonymousCharacterStyle.startsWith("@"))
                  {
                    MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(paramAnonymousCharacterStyle.substring(1), ChannelAdminLogActivity.this, 0);
                  }
                  else if (paramAnonymousCharacterStyle.startsWith("#"))
                  {
                    paramAnonymousMessageObject = new DialogsActivity(null);
                    paramAnonymousMessageObject.setSearchString(paramAnonymousCharacterStyle);
                    ChannelAdminLogActivity.this.presentFragment(paramAnonymousMessageObject);
                  }
                }
                else
                {
                  final Object localObject1 = ((URLSpan)paramAnonymousCharacterStyle).getURL();
                  Object localObject2;
                  if (paramAnonymousBoolean)
                  {
                    localObject2 = new BottomSheet.Builder(ChannelAdminLogActivity.this.getParentActivity());
                    ((BottomSheet.Builder)localObject2).setTitle((CharSequence)localObject1);
                    paramAnonymousCharacterStyle = LocaleController.getString("Open", NUM);
                    paramAnonymousMessageObject = LocaleController.getString("Copy", NUM);
                    localObject1 = new DialogInterface.OnClickListener()
                    {
                      public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                      {
                        if (paramAnonymous2Int == 0)
                        {
                          Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), localObject1, true);
                          return;
                        }
                        String str;
                        if (paramAnonymous2Int == 1)
                        {
                          str = localObject1;
                          if (!str.startsWith("mailto:")) {
                            break label59;
                          }
                          paramAnonymous2DialogInterface = str.substring(7);
                        }
                        for (;;)
                        {
                          AndroidUtilities.addToClipboard(paramAnonymous2DialogInterface);
                          break;
                          break;
                          label59:
                          paramAnonymous2DialogInterface = str;
                          if (str.startsWith("tel:")) {
                            paramAnonymous2DialogInterface = str.substring(4);
                          }
                        }
                      }
                    };
                    ((BottomSheet.Builder)localObject2).setItems(new CharSequence[] { paramAnonymousCharacterStyle, paramAnonymousMessageObject }, (DialogInterface.OnClickListener)localObject1);
                    ChannelAdminLogActivity.this.showDialog(((BottomSheet.Builder)localObject2).create());
                  }
                  else if ((paramAnonymousCharacterStyle instanceof URLSpanReplacement))
                  {
                    ChannelAdminLogActivity.this.showOpenUrlAlert(((URLSpanReplacement)paramAnonymousCharacterStyle).getURL(), true);
                  }
                  else if ((paramAnonymousCharacterStyle instanceof URLSpan))
                  {
                    if (((paramAnonymousMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage)) && (paramAnonymousMessageObject.messageOwner.media.webpage != null) && (paramAnonymousMessageObject.messageOwner.media.webpage.cached_page != null))
                    {
                      localObject2 = ((String)localObject1).toLowerCase();
                      paramAnonymousCharacterStyle = paramAnonymousMessageObject.messageOwner.media.webpage.url.toLowerCase();
                      if (((((String)localObject2).contains("telegra.ph")) || (((String)localObject2).contains("t.me/iv"))) && ((((String)localObject2).contains(paramAnonymousCharacterStyle)) || (paramAnonymousCharacterStyle.contains((CharSequence)localObject2))))
                      {
                        ArticleViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity(), ChannelAdminLogActivity.this);
                        ArticleViewer.getInstance().open(paramAnonymousMessageObject);
                        continue;
                      }
                    }
                    Browser.openUrl(ChannelAdminLogActivity.this.getParentActivity(), (String)localObject1, true);
                  }
                  else if ((paramAnonymousCharacterStyle instanceof ClickableSpan))
                  {
                    ((ClickableSpan)paramAnonymousCharacterStyle).onClick(ChannelAdminLogActivity.this.fragmentView);
                  }
                }
              }
            }
            
            public void didPressedUserAvatar(ChatMessageCell paramAnonymousChatMessageCell, TLRPC.User paramAnonymousUser)
            {
              if ((paramAnonymousUser != null) && (paramAnonymousUser.id != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId()))
              {
                paramAnonymousChatMessageCell = new Bundle();
                paramAnonymousChatMessageCell.putInt("user_id", paramAnonymousUser.id);
                ChannelAdminLogActivity.this.addCanBanUser(paramAnonymousChatMessageCell, paramAnonymousUser.id);
                paramAnonymousChatMessageCell = new ProfileActivity(paramAnonymousChatMessageCell);
                paramAnonymousChatMessageCell.setPlayProfileAnimation(false);
                ChannelAdminLogActivity.this.presentFragment(paramAnonymousChatMessageCell);
              }
            }
            
            public void didPressedViaBot(ChatMessageCell paramAnonymousChatMessageCell, String paramAnonymousString) {}
            
            public boolean isChatAdminCell(int paramAnonymousInt)
            {
              return false;
            }
            
            public void needOpenWebView(String paramAnonymousString1, String paramAnonymousString2, String paramAnonymousString3, String paramAnonymousString4, int paramAnonymousInt1, int paramAnonymousInt2)
            {
              EmbedBottomSheet.show(ChannelAdminLogActivity.ChatActivityAdapter.this.mContext, paramAnonymousString2, paramAnonymousString3, paramAnonymousString4, paramAnonymousString1, paramAnonymousInt1, paramAnonymousInt2);
            }
            
            public boolean needPlayMessage(MessageObject paramAnonymousMessageObject)
            {
              boolean bool;
              if ((paramAnonymousMessageObject.isVoice()) || (paramAnonymousMessageObject.isRoundVideo()))
              {
                bool = MediaController.getInstance().playMessage(paramAnonymousMessageObject);
                MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
              }
              for (;;)
              {
                return bool;
                if (paramAnonymousMessageObject.isMusic()) {
                  bool = MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.messages, paramAnonymousMessageObject);
                } else {
                  bool = false;
                }
              }
            }
          });
          localChatMessageCell.setAllowAssistant(true);
        }
      }
      for (;;)
      {
        paramViewGroup.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(paramViewGroup);
        paramViewGroup = new ChatMessageCell(this.mContext);
        break;
        if (paramInt == 1)
        {
          paramViewGroup = new ChatActionCell(this.mContext);
          ((ChatActionCell)paramViewGroup).setDelegate(new ChatActionCell.ChatActionCellDelegate()
          {
            public void didClickedImage(ChatActionCell paramAnonymousChatActionCell)
            {
              MessageObject localMessageObject = paramAnonymousChatActionCell.getMessageObject();
              PhotoViewer.getInstance().setParentActivity(ChannelAdminLogActivity.this.getParentActivity());
              paramAnonymousChatActionCell = FileLoader.getClosestPhotoSizeWithSize(localMessageObject.photoThumbs, 640);
              if (paramAnonymousChatActionCell != null) {
                PhotoViewer.getInstance().openPhoto(paramAnonymousChatActionCell.location, ChannelAdminLogActivity.this.provider);
              }
              for (;;)
              {
                return;
                PhotoViewer.getInstance().openPhoto(localMessageObject, 0L, 0L, ChannelAdminLogActivity.this.provider);
              }
            }
            
            public void didLongPressed(ChatActionCell paramAnonymousChatActionCell)
            {
              ChannelAdminLogActivity.this.createMenu(paramAnonymousChatActionCell);
            }
            
            public void didPressedBotButton(MessageObject paramAnonymousMessageObject, TLRPC.KeyboardButton paramAnonymousKeyboardButton) {}
            
            public void didPressedReplyMessage(ChatActionCell paramAnonymousChatActionCell, int paramAnonymousInt) {}
            
            public void needOpenUserProfile(int paramAnonymousInt)
            {
              Object localObject;
              if (paramAnonymousInt < 0)
              {
                localObject = new Bundle();
                ((Bundle)localObject).putInt("chat_id", -paramAnonymousInt);
                if (MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).checkCanOpenChat((Bundle)localObject, ChannelAdminLogActivity.this)) {
                  ChannelAdminLogActivity.this.presentFragment(new ChatActivity((Bundle)localObject), true);
                }
              }
              for (;;)
              {
                return;
                if (paramAnonymousInt != UserConfig.getInstance(ChannelAdminLogActivity.this.currentAccount).getClientUserId())
                {
                  localObject = new Bundle();
                  ((Bundle)localObject).putInt("user_id", paramAnonymousInt);
                  ChannelAdminLogActivity.this.addCanBanUser((Bundle)localObject, paramAnonymousInt);
                  localObject = new ProfileActivity((Bundle)localObject);
                  ((ProfileActivity)localObject).setPlayProfileAnimation(false);
                  ChannelAdminLogActivity.this.presentFragment((BaseFragment)localObject);
                }
              }
            }
          });
        }
        else if (paramInt == 2)
        {
          paramViewGroup = new ChatUnreadCell(this.mContext);
        }
        else if (paramInt == 3)
        {
          paramViewGroup = new BotHelpCell(this.mContext);
          ((BotHelpCell)paramViewGroup).setDelegate(new BotHelpCell.BotHelpCellDelegate()
          {
            public void didPressUrl(String paramAnonymousString)
            {
              if (paramAnonymousString.startsWith("@")) {
                MessagesController.getInstance(ChannelAdminLogActivity.this.currentAccount).openByUserName(paramAnonymousString.substring(1), ChannelAdminLogActivity.this, 0);
              }
              for (;;)
              {
                return;
                if (paramAnonymousString.startsWith("#"))
                {
                  DialogsActivity localDialogsActivity = new DialogsActivity(null);
                  localDialogsActivity.setSearchString(paramAnonymousString);
                  ChannelAdminLogActivity.this.presentFragment(localDialogsActivity);
                }
              }
            }
          });
        }
        else if (paramInt == 4)
        {
          paramViewGroup = new ChatLoadingCell(this.mContext);
        }
      }
    }
    
    public void onViewAttachedToWindow(final RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool1 = true;
      boolean bool2;
      if ((paramViewHolder.itemView instanceof ChatMessageCell))
      {
        paramViewHolder = (ChatMessageCell)paramViewHolder.itemView;
        paramViewHolder.getMessageObject();
        paramViewHolder.setBackgroundDrawable(null);
        if (0 != 0) {
          break label72;
        }
        bool2 = true;
        if ((0 == 0) || (0 == 0)) {
          break label77;
        }
      }
      for (;;)
      {
        paramViewHolder.setCheckPressed(bool2, bool1);
        paramViewHolder.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
        {
          public boolean onPreDraw()
          {
            paramViewHolder.getViewTreeObserver().removeOnPreDrawListener(this);
            int i = ChannelAdminLogActivity.this.chatListView.getMeasuredHeight();
            int j = paramViewHolder.getTop();
            paramViewHolder.getBottom();
            if (j >= 0) {}
            for (j = 0;; j = -j)
            {
              int k = paramViewHolder.getMeasuredHeight();
              int m = k;
              if (k > i) {
                m = j + i;
              }
              paramViewHolder.setVisiblePart(j, m - j);
              return true;
            }
          }
        });
        paramViewHolder.setHighlighted(false);
        return;
        label72:
        bool2 = false;
        break;
        label77:
        bool1 = false;
      }
    }
    
    public void updateRowWithMessageObject(MessageObject paramMessageObject)
    {
      int i = ChannelAdminLogActivity.this.messages.indexOf(paramMessageObject);
      if (i == -1) {}
      for (;;)
      {
        return;
        notifyItemChanged(this.messagesStartRow + ChannelAdminLogActivity.this.messages.size() - i - 1);
      }
    }
    
    public void updateRows()
    {
      this.rowCount = 0;
      if (!ChannelAdminLogActivity.this.messages.isEmpty()) {
        if (!ChannelAdminLogActivity.this.endReached)
        {
          int i = this.rowCount;
          this.rowCount = (i + 1);
          this.loadingUpRow = i;
          this.messagesStartRow = this.rowCount;
          this.rowCount += ChannelAdminLogActivity.this.messages.size();
        }
      }
      for (this.messagesEndRow = this.rowCount;; this.messagesEndRow = -1)
      {
        return;
        this.loadingUpRow = -1;
        break;
        this.loadingUpRow = -1;
        this.messagesStartRow = -1;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/ChannelAdminLogActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
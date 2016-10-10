package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.text.Editable;
import android.util.Base64;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController.AlbumEntry;
import org.telegram.messenger.MediaController.PhotoEntry;
import org.telegram.messenger.MediaController.SearchImage;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationCenter.NotificationCenterDelegate;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.volley.AuthFailureError;
import org.telegram.messenger.volley.RequestQueue;
import org.telegram.messenger.volley.Response.ErrorListener;
import org.telegram.messenger.volley.Response.Listener;
import org.telegram.messenger.volley.VolleyError;
import org.telegram.messenger.volley.toolbox.JsonObjectRequest;
import org.telegram.messenger.volley.toolbox.Volley;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Document;
import org.telegram.tgnet.TLRPC.DocumentAttribute;
import org.telegram.tgnet.TLRPC.FileLocation;
import org.telegram.tgnet.TLRPC.FoundGif;
import org.telegram.tgnet.TLRPC.Photo;
import org.telegram.tgnet.TLRPC.PhotoSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC.TL_documentAttributeVideo;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messages_foundGifs;
import org.telegram.tgnet.TLRPC.TL_messages_searchGifs;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Adapters.BaseFragmentAdapter;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.PickerBottomLayout;

public class PhotoPickerActivity
  extends BaseFragment
  implements NotificationCenter.NotificationCenterDelegate, PhotoViewer.PhotoViewerProvider
{
  private boolean allowCaption = true;
  private ChatActivity chatActivity;
  private PhotoPickerActivityDelegate delegate;
  private TextView emptyView;
  private int giphyReqId;
  private boolean giphySearchEndReached = true;
  private int itemWidth = 100;
  private String lastSearchString;
  private int lastSearchToken;
  private ListAdapter listAdapter;
  private GridView listView;
  private boolean loadingRecent;
  private int nextGiphySearchOffset;
  private String nextSearchBingString;
  private PickerBottomLayout pickerBottomLayout;
  private FrameLayout progressView;
  private ArrayList<MediaController.SearchImage> recentImages;
  private RequestQueue requestQueue;
  private ActionBarMenuItem searchItem;
  private ArrayList<MediaController.SearchImage> searchResult = new ArrayList();
  private HashMap<String, MediaController.SearchImage> searchResultKeys = new HashMap();
  private HashMap<String, MediaController.SearchImage> searchResultUrls = new HashMap();
  private boolean searching;
  private MediaController.AlbumEntry selectedAlbum;
  private HashMap<Integer, MediaController.PhotoEntry> selectedPhotos;
  private HashMap<String, MediaController.SearchImage> selectedWebPhotos;
  private boolean sendPressed;
  private boolean singlePhoto;
  private int type;
  
  public PhotoPickerActivity(int paramInt, MediaController.AlbumEntry paramAlbumEntry, HashMap<Integer, MediaController.PhotoEntry> paramHashMap, HashMap<String, MediaController.SearchImage> paramHashMap1, ArrayList<MediaController.SearchImage> paramArrayList, boolean paramBoolean1, boolean paramBoolean2, ChatActivity paramChatActivity)
  {
    this.selectedAlbum = paramAlbumEntry;
    this.selectedPhotos = paramHashMap;
    this.selectedWebPhotos = paramHashMap1;
    this.type = paramInt;
    this.recentImages = paramArrayList;
    this.singlePhoto = paramBoolean1;
    this.chatActivity = paramChatActivity;
    this.allowCaption = paramBoolean2;
    if ((paramAlbumEntry != null) && (paramAlbumEntry.isVideo)) {
      this.singlePhoto = true;
    }
  }
  
  private void fixLayout()
  {
    if (this.listView != null) {
      this.listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
      {
        public boolean onPreDraw()
        {
          PhotoPickerActivity.this.fixLayoutInternal();
          if (PhotoPickerActivity.this.listView != null) {
            PhotoPickerActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
          }
          return true;
        }
      });
    }
  }
  
  private void fixLayoutInternal()
  {
    if (getParentActivity() == null) {
      return;
    }
    int j = this.listView.getFirstVisiblePosition();
    int i = ((WindowManager)ApplicationLoader.applicationContext.getSystemService("window")).getDefaultDisplay().getRotation();
    if (AndroidUtilities.isTablet())
    {
      i = 3;
      label45:
      this.listView.setNumColumns(i);
      if (!AndroidUtilities.isTablet()) {
        break label162;
      }
    }
    label162:
    for (this.itemWidth = ((AndroidUtilities.dp(490.0F) - (i + 1) * AndroidUtilities.dp(4.0F)) / i);; this.itemWidth = ((AndroidUtilities.displaySize.x - (i + 1) * AndroidUtilities.dp(4.0F)) / i))
    {
      this.listView.setColumnWidth(this.itemWidth);
      this.listAdapter.notifyDataSetChanged();
      this.listView.setSelection(j);
      if (this.selectedAlbum != null) {
        break;
      }
      this.emptyView.setPadding(0, 0, 0, (int)((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) * 0.4F));
      return;
      if ((i == 3) || (i == 1))
      {
        i = 5;
        break label45;
      }
      i = 3;
      break label45;
    }
  }
  
  private PhotoPickerPhotoCell getCellForIndex(int paramInt)
  {
    int j = this.listView.getChildCount();
    int i = 0;
    if (i < j)
    {
      Object localObject = this.listView.getChildAt(i);
      PhotoPickerPhotoCell localPhotoPickerPhotoCell;
      int k;
      if ((localObject instanceof PhotoPickerPhotoCell))
      {
        localPhotoPickerPhotoCell = (PhotoPickerPhotoCell)localObject;
        k = ((Integer)localPhotoPickerPhotoCell.photoImage.getTag()).intValue();
        if (this.selectedAlbum == null) {
          break label90;
        }
        if ((k >= 0) && (k < this.selectedAlbum.photos.size())) {
          break label128;
        }
      }
      label90:
      label128:
      label144:
      for (;;)
      {
        i += 1;
        break;
        if ((this.searchResult.isEmpty()) && (this.lastSearchString == null)) {}
        for (localObject = this.recentImages;; localObject = this.searchResult)
        {
          if ((k < 0) || (k >= ((ArrayList)localObject).size())) {
            break label144;
          }
          if (k != paramInt) {
            break;
          }
          return localPhotoPickerPhotoCell;
        }
      }
    }
    return null;
  }
  
  private void searchBingImages(String paramString, int paramInt1, int paramInt2)
  {
    int j = 1;
    if (this.searching)
    {
      this.searching = false;
      if (this.giphyReqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
        this.giphyReqId = 0;
      }
      this.requestQueue.cancelAll("search");
    }
    label292:
    for (;;)
    {
      try
      {
        this.searching = true;
        if (this.nextSearchBingString != null)
        {
          paramString = this.nextSearchBingString;
          paramString = new JsonObjectRequest(0, paramString, null, new Response.Listener()new Response.ErrorListener
          {
            /* Error */
            public void onResponse(JSONObject paramAnonymousJSONObject)
            {
              // Byte code:
              //   0: aload_0
              //   1: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   4: aconst_null
              //   5: invokestatic 35	org/telegram/ui/PhotoPickerActivity:access$302	(Lorg/telegram/ui/PhotoPickerActivity;Ljava/lang/String;)Ljava/lang/String;
              //   8: pop
              //   9: aload_1
              //   10: ldc 37
              //   12: invokevirtual 41	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
              //   15: astore 5
              //   17: aload 5
              //   19: ldc 43
              //   21: invokevirtual 47	org/json/JSONObject:getJSONArray	(Ljava/lang/String;)Lorg/json/JSONArray;
              //   24: astore_1
              //   25: aload_0
              //   26: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   29: aload 5
              //   31: ldc 49
              //   33: invokevirtual 53	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
              //   36: invokestatic 35	org/telegram/ui/PhotoPickerActivity:access$302	(Lorg/telegram/ui/PhotoPickerActivity;Ljava/lang/String;)Ljava/lang/String;
              //   39: pop
              //   40: iconst_0
              //   41: istore_2
              //   42: aload_1
              //   43: invokevirtual 59	org/json/JSONArray:length	()I
              //   46: istore_3
              //   47: iload_2
              //   48: iload_3
              //   49: if_icmpge +76 -> 125
              //   52: aload_1
              //   53: iload_2
              //   54: invokevirtual 62	org/json/JSONArray:getJSONObject	(I)Lorg/json/JSONObject;
              //   57: astore 5
              //   59: aload 5
              //   61: ldc 64
              //   63: invokevirtual 53	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
              //   66: invokestatic 69	org/telegram/messenger/Utilities:MD5	(Ljava/lang/String;)Ljava/lang/String;
              //   69: astore 6
              //   71: aload_0
              //   72: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   75: invokestatic 73	org/telegram/ui/PhotoPickerActivity:access$100	(Lorg/telegram/ui/PhotoPickerActivity;)Ljava/util/HashMap;
              //   78: aload 6
              //   80: invokevirtual 79	java/util/HashMap:containsKey	(Ljava/lang/Object;)Z
              //   83: istore 4
              //   85: iload 4
              //   87: ifeq +113 -> 200
              //   90: iload_2
              //   91: iconst_1
              //   92: iadd
              //   93: istore_2
              //   94: goto -52 -> 42
              //   97: astore 5
              //   99: aload_0
              //   100: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   103: aconst_null
              //   104: invokestatic 35	org/telegram/ui/PhotoPickerActivity:access$302	(Lorg/telegram/ui/PhotoPickerActivity;Ljava/lang/String;)Ljava/lang/String;
              //   107: pop
              //   108: ldc 81
              //   110: aload 5
              //   112: invokestatic 87	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
              //   115: goto -75 -> 40
              //   118: astore_1
              //   119: ldc 81
              //   121: aload_1
              //   122: invokestatic 87	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
              //   125: aload_0
              //   126: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   129: iconst_0
              //   130: invokestatic 91	org/telegram/ui/PhotoPickerActivity:access$502	(Lorg/telegram/ui/PhotoPickerActivity;Z)Z
              //   133: pop
              //   134: aload_0
              //   135: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   138: invokestatic 95	org/telegram/ui/PhotoPickerActivity:access$300	(Lorg/telegram/ui/PhotoPickerActivity;)Ljava/lang/String;
              //   141: ifnull +51 -> 192
              //   144: aload_0
              //   145: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   148: invokestatic 95	org/telegram/ui/PhotoPickerActivity:access$300	(Lorg/telegram/ui/PhotoPickerActivity;)Ljava/lang/String;
              //   151: ldc 97
              //   153: invokevirtual 103	java/lang/String:contains	(Ljava/lang/CharSequence;)Z
              //   156: ifne +36 -> 192
              //   159: aload_0
              //   160: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   163: new 105	java/lang/StringBuilder
              //   166: dup
              //   167: invokespecial 106	java/lang/StringBuilder:<init>	()V
              //   170: aload_0
              //   171: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   174: invokestatic 95	org/telegram/ui/PhotoPickerActivity:access$300	(Lorg/telegram/ui/PhotoPickerActivity;)Ljava/lang/String;
              //   177: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   180: ldc 112
              //   182: invokevirtual 110	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
              //   185: invokevirtual 116	java/lang/StringBuilder:toString	()Ljava/lang/String;
              //   188: invokestatic 35	org/telegram/ui/PhotoPickerActivity:access$302	(Lorg/telegram/ui/PhotoPickerActivity;Ljava/lang/String;)Ljava/lang/String;
              //   191: pop
              //   192: aload_0
              //   193: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   196: invokestatic 119	org/telegram/ui/PhotoPickerActivity:access$900	(Lorg/telegram/ui/PhotoPickerActivity;)V
              //   199: return
              //   200: new 121	org/telegram/messenger/MediaController$SearchImage
              //   203: dup
              //   204: invokespecial 122	org/telegram/messenger/MediaController$SearchImage:<init>	()V
              //   207: astore 7
              //   209: aload 7
              //   211: aload 6
              //   213: putfield 126	org/telegram/messenger/MediaController$SearchImage:id	Ljava/lang/String;
              //   216: aload 7
              //   218: aload 5
              //   220: ldc -128
              //   222: invokevirtual 132	org/json/JSONObject:getInt	(Ljava/lang/String;)I
              //   225: putfield 136	org/telegram/messenger/MediaController$SearchImage:width	I
              //   228: aload 7
              //   230: aload 5
              //   232: ldc -118
              //   234: invokevirtual 132	org/json/JSONObject:getInt	(Ljava/lang/String;)I
              //   237: putfield 141	org/telegram/messenger/MediaController$SearchImage:height	I
              //   240: aload 7
              //   242: aload 5
              //   244: ldc -113
              //   246: invokevirtual 132	org/json/JSONObject:getInt	(Ljava/lang/String;)I
              //   249: putfield 146	org/telegram/messenger/MediaController$SearchImage:size	I
              //   252: aload 7
              //   254: aload 5
              //   256: ldc 64
              //   258: invokevirtual 53	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
              //   261: putfield 149	org/telegram/messenger/MediaController$SearchImage:imageUrl	Ljava/lang/String;
              //   264: aload 7
              //   266: aload 5
              //   268: ldc -105
              //   270: invokevirtual 41	org/json/JSONObject:getJSONObject	(Ljava/lang/String;)Lorg/json/JSONObject;
              //   273: ldc 64
              //   275: invokevirtual 53	org/json/JSONObject:getString	(Ljava/lang/String;)Ljava/lang/String;
              //   278: putfield 154	org/telegram/messenger/MediaController$SearchImage:thumbUrl	Ljava/lang/String;
              //   281: aload_0
              //   282: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   285: invokestatic 158	org/telegram/ui/PhotoPickerActivity:access$000	(Lorg/telegram/ui/PhotoPickerActivity;)Ljava/util/ArrayList;
              //   288: aload 7
              //   290: invokevirtual 163	java/util/ArrayList:add	(Ljava/lang/Object;)Z
              //   293: pop
              //   294: aload_0
              //   295: getfield 18	org/telegram/ui/PhotoPickerActivity$10:this$0	Lorg/telegram/ui/PhotoPickerActivity;
              //   298: invokestatic 73	org/telegram/ui/PhotoPickerActivity:access$100	(Lorg/telegram/ui/PhotoPickerActivity;)Ljava/util/HashMap;
              //   301: aload 6
              //   303: aload 7
              //   305: invokevirtual 167	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
              //   308: pop
              //   309: goto -219 -> 90
              //   312: astore 5
              //   314: ldc 81
              //   316: aload 5
              //   318: invokestatic 87	org/telegram/messenger/FileLog:e	(Ljava/lang/String;Ljava/lang/Throwable;)V
              //   321: goto -231 -> 90
              // Local variable table:
              //   start	length	slot	name	signature
              //   0	324	0	this	10
              //   0	324	1	paramAnonymousJSONObject	JSONObject
              //   41	53	2	i	int
              //   46	4	3	j	int
              //   83	3	4	bool	boolean
              //   15	45	5	localJSONObject	JSONObject
              //   97	170	5	localException1	Exception
              //   312	5	5	localException2	Exception
              //   69	233	6	str	String
              //   207	97	7	localSearchImage	MediaController.SearchImage
              // Exception table:
              //   from	to	target	type
              //   25	40	97	java/lang/Exception
              //   9	25	118	java/lang/Exception
              //   42	47	118	java/lang/Exception
              //   99	115	118	java/lang/Exception
              //   314	321	118	java/lang/Exception
              //   52	85	312	java/lang/Exception
              //   200	309	312	java/lang/Exception
            }
          }, new Response.ErrorListener()
          {
            public void onErrorResponse(VolleyError paramAnonymousVolleyError)
            {
              FileLog.e("tmessages", "Error: " + paramAnonymousVolleyError.getMessage());
              PhotoPickerActivity.access$302(PhotoPickerActivity.this, null);
              PhotoPickerActivity.access$502(PhotoPickerActivity.this, false);
              PhotoPickerActivity.this.updateSearchInterface();
            }
          })
          {
            public Map<String, String> getHeaders()
              throws AuthFailureError
            {
              HashMap localHashMap = new HashMap();
              localHashMap.put("Authorization", "Basic " + Base64.encodeToString(new StringBuilder().append(BuildVars.BING_SEARCH_KEY).append(":").append(BuildVars.BING_SEARCH_KEY).toString().getBytes(), 2));
              return localHashMap;
            }
          };
          paramString.setShouldCache(false);
          paramString.setTag("search");
          this.requestQueue.add(paramString);
          return;
        }
        Object localObject = UserConfig.getCurrentUser().phone;
        int i = j;
        if (!((String)localObject).startsWith("44"))
        {
          i = j;
          if (!((String)localObject).startsWith("49"))
          {
            i = j;
            if (!((String)localObject).startsWith("43"))
            {
              i = j;
              if (!((String)localObject).startsWith("31"))
              {
                if (!((String)localObject).startsWith("1")) {
                  break label292;
                }
                i = j;
              }
            }
          }
        }
        localObject = Locale.US;
        String str = URLEncoder.encode(paramString, "UTF-8");
        if (i != 0)
        {
          paramString = "";
          paramString = String.format((Locale)localObject, "https://api.datamarket.azure.com/Bing/Search/v1/Image?Query='%s'&$skip=%d&$top=%d&$format=json%s", new Object[] { str, Integer.valueOf(paramInt1), Integer.valueOf(paramInt2), paramString });
        }
        else
        {
          paramString = "&Adult='Off'";
          continue;
          i = 0;
        }
      }
      catch (Exception paramString)
      {
        FileLog.e("tmessages", paramString);
        this.nextSearchBingString = null;
        this.searching = false;
        updateSearchInterface();
        return;
      }
    }
  }
  
  private void searchGiphyImages(final String paramString, final int paramInt)
  {
    if (this.searching)
    {
      this.searching = false;
      if (this.giphyReqId != 0)
      {
        ConnectionsManager.getInstance().cancelRequest(this.giphyReqId, true);
        this.giphyReqId = 0;
      }
      this.requestQueue.cancelAll("search");
    }
    this.searching = true;
    TLRPC.TL_messages_searchGifs localTL_messages_searchGifs = new TLRPC.TL_messages_searchGifs();
    localTL_messages_searchGifs.q = paramString;
    localTL_messages_searchGifs.offset = paramInt;
    paramInt = this.lastSearchToken + 1;
    this.lastSearchToken = paramInt;
    this.giphyReqId = ConnectionsManager.getInstance().sendRequest(localTL_messages_searchGifs, new RequestDelegate()
    {
      public void run(final TLObject paramAnonymousTLObject, TLRPC.TL_error paramAnonymousTL_error)
      {
        AndroidUtilities.runOnUIThread(new Runnable()
        {
          public void run()
          {
            boolean bool = true;
            if (PhotoPickerActivity.9.this.val$token != PhotoPickerActivity.this.lastSearchToken) {
              return;
            }
            Object localObject1;
            if (paramAnonymousTLObject != null)
            {
              int j = 0;
              localObject1 = (TLRPC.TL_messages_foundGifs)paramAnonymousTLObject;
              PhotoPickerActivity.access$1102(PhotoPickerActivity.this, ((TLRPC.TL_messages_foundGifs)localObject1).next_offset);
              int i = 0;
              while (i < ((TLRPC.TL_messages_foundGifs)localObject1).results.size())
              {
                TLRPC.FoundGif localFoundGif = (TLRPC.FoundGif)((TLRPC.TL_messages_foundGifs)localObject1).results.get(i);
                if (PhotoPickerActivity.this.searchResultKeys.containsKey(localFoundGif.url))
                {
                  i += 1;
                }
                else
                {
                  int k = 1;
                  MediaController.SearchImage localSearchImage = new MediaController.SearchImage();
                  localSearchImage.id = localFoundGif.url;
                  label145:
                  Object localObject2;
                  if (localFoundGif.document != null)
                  {
                    j = 0;
                    if (j < localFoundGif.document.attributes.size())
                    {
                      localObject2 = (TLRPC.DocumentAttribute)localFoundGif.document.attributes.get(j);
                      if ((!(localObject2 instanceof TLRPC.TL_documentAttributeImageSize)) && (!(localObject2 instanceof TLRPC.TL_documentAttributeVideo))) {
                        break label390;
                      }
                      localSearchImage.width = ((TLRPC.DocumentAttribute)localObject2).w;
                    }
                  }
                  for (localSearchImage.height = ((TLRPC.DocumentAttribute)localObject2).h;; localSearchImage.height = localFoundGif.h)
                  {
                    localSearchImage.size = 0;
                    localSearchImage.imageUrl = localFoundGif.content_url;
                    localSearchImage.thumbUrl = localFoundGif.thumb_url;
                    localSearchImage.localUrl = (localFoundGif.url + "|" + PhotoPickerActivity.9.this.val$query);
                    localSearchImage.document = localFoundGif.document;
                    if ((localFoundGif.photo != null) && (localFoundGif.document != null))
                    {
                      localObject2 = FileLoader.getClosestPhotoSizeWithSize(localFoundGif.photo.sizes, PhotoPickerActivity.this.itemWidth, true);
                      if (localObject2 != null) {
                        localFoundGif.document.thumb = ((TLRPC.PhotoSize)localObject2);
                      }
                    }
                    localSearchImage.type = 1;
                    PhotoPickerActivity.this.searchResult.add(localSearchImage);
                    PhotoPickerActivity.this.searchResultKeys.put(localSearchImage.id, localSearchImage);
                    j = k;
                    break;
                    label390:
                    j += 1;
                    break label145;
                    localSearchImage.width = localFoundGif.w;
                  }
                }
              }
              localObject1 = PhotoPickerActivity.this;
              if (j != 0) {
                break label464;
              }
            }
            for (;;)
            {
              PhotoPickerActivity.access$402((PhotoPickerActivity)localObject1, bool);
              PhotoPickerActivity.access$502(PhotoPickerActivity.this, false);
              PhotoPickerActivity.this.updateSearchInterface();
              return;
              label464:
              bool = false;
            }
          }
        });
      }
    });
    ConnectionsManager.getInstance().bindRequestToGuid(this.giphyReqId, this.classGuid);
  }
  
  private void sendSelectedPhotos()
  {
    if (((this.selectedPhotos.isEmpty()) && (this.selectedWebPhotos.isEmpty())) || (this.delegate == null) || (this.sendPressed)) {
      return;
    }
    this.sendPressed = true;
    this.delegate.actionButtonPressed(false);
    finishFragment();
  }
  
  private void updateSearchInterface()
  {
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    if (((this.searching) && (this.searchResult.isEmpty())) || ((this.loadingRecent) && (this.lastSearchString == null)))
    {
      this.progressView.setVisibility(0);
      this.listView.setEmptyView(null);
      this.emptyView.setVisibility(8);
      return;
    }
    this.progressView.setVisibility(8);
    this.emptyView.setVisibility(0);
    this.listView.setEmptyView(this.emptyView);
  }
  
  public boolean allowCaption()
  {
    return this.allowCaption;
  }
  
  public boolean cancelButtonPressed()
  {
    this.delegate.actionButtonPressed(true);
    finishFragment();
    return true;
  }
  
  public View createView(Context paramContext)
  {
    int j = 0;
    this.actionBar.setBackgroundColor(-13421773);
    this.actionBar.setItemsBackgroundColor(-12763843);
    this.actionBar.setBackButtonImage(2130837700);
    label141:
    FrameLayout localFrameLayout;
    Object localObject1;
    label339:
    Object localObject2;
    if (this.selectedAlbum != null)
    {
      this.actionBar.setTitle(this.selectedAlbum.bucketName);
      this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick()
      {
        public void onItemClick(int paramAnonymousInt)
        {
          if (paramAnonymousInt == -1) {
            PhotoPickerActivity.this.finishFragment();
          }
        }
      });
      if (this.selectedAlbum == null) {
        this.searchItem = this.actionBar.createMenu().addItem(0, 2130837711).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener()
        {
          public boolean canCollapseSearch()
          {
            PhotoPickerActivity.this.finishFragment();
            return false;
          }
          
          public void onSearchExpand() {}
          
          public void onSearchPressed(EditText paramAnonymousEditText)
          {
            if (paramAnonymousEditText.getText().toString().length() == 0) {
              return;
            }
            PhotoPickerActivity.this.searchResult.clear();
            PhotoPickerActivity.this.searchResultKeys.clear();
            PhotoPickerActivity.access$302(PhotoPickerActivity.this, null);
            PhotoPickerActivity.access$402(PhotoPickerActivity.this, true);
            if (PhotoPickerActivity.this.type == 0)
            {
              PhotoPickerActivity.this.searchBingImages(paramAnonymousEditText.getText().toString(), 0, 53);
              PhotoPickerActivity.access$202(PhotoPickerActivity.this, paramAnonymousEditText.getText().toString());
              if (PhotoPickerActivity.this.lastSearchString.length() != 0) {
                break label220;
              }
              PhotoPickerActivity.access$202(PhotoPickerActivity.this, null);
              if (PhotoPickerActivity.this.type != 0) {
                break label189;
              }
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", 2131165948));
            }
            for (;;)
            {
              PhotoPickerActivity.this.updateSearchInterface();
              return;
              if (PhotoPickerActivity.this.type != 1) {
                break;
              }
              PhotoPickerActivity.access$1102(PhotoPickerActivity.this, 0);
              PhotoPickerActivity.this.searchGiphyImages(paramAnonymousEditText.getText().toString(), 0);
              break;
              label189:
              if (PhotoPickerActivity.this.type == 1)
              {
                PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", 2131165947));
                continue;
                label220:
                PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoResult", 2131165949));
              }
            }
          }
          
          public void onTextChanged(EditText paramAnonymousEditText)
          {
            if (paramAnonymousEditText.getText().length() == 0)
            {
              PhotoPickerActivity.this.searchResult.clear();
              PhotoPickerActivity.this.searchResultKeys.clear();
              PhotoPickerActivity.access$202(PhotoPickerActivity.this, null);
              PhotoPickerActivity.access$302(PhotoPickerActivity.this, null);
              PhotoPickerActivity.access$402(PhotoPickerActivity.this, true);
              PhotoPickerActivity.access$502(PhotoPickerActivity.this, false);
              PhotoPickerActivity.this.requestQueue.cancelAll("search");
              if (PhotoPickerActivity.this.type != 0) {
                break label115;
              }
              PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentPhotos", 2131165948));
            }
            for (;;)
            {
              PhotoPickerActivity.this.updateSearchInterface();
              return;
              label115:
              if (PhotoPickerActivity.this.type == 1) {
                PhotoPickerActivity.this.emptyView.setText(LocaleController.getString("NoRecentGIFs", 2131165947));
              }
            }
          }
        });
      }
      if (this.selectedAlbum == null)
      {
        if (this.type != 0) {
          break label942;
        }
        this.searchItem.getSearchField().setHint(LocaleController.getString("SearchImagesTitle", 2131166211));
      }
      this.fragmentView = new FrameLayout(paramContext);
      localFrameLayout = (FrameLayout)this.fragmentView;
      localFrameLayout.setBackgroundColor(-16777216);
      this.listView = new GridView(paramContext);
      this.listView.setPadding(AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F), AndroidUtilities.dp(4.0F));
      this.listView.setClipToPadding(false);
      this.listView.setDrawSelectorOnTop(true);
      this.listView.setStretchMode(2);
      this.listView.setHorizontalScrollBarEnabled(false);
      this.listView.setVerticalScrollBarEnabled(false);
      this.listView.setNumColumns(-1);
      this.listView.setVerticalSpacing(AndroidUtilities.dp(4.0F));
      this.listView.setHorizontalSpacing(AndroidUtilities.dp(4.0F));
      this.listView.setSelector(2130837796);
      localFrameLayout.addView(this.listView);
      localObject1 = (FrameLayout.LayoutParams)this.listView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -1;
      if (!this.singlePhoto) {
        break label972;
      }
      i = 0;
      ((FrameLayout.LayoutParams)localObject1).bottomMargin = i;
      this.listView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      localObject1 = this.listView;
      localObject2 = new ListAdapter(paramContext);
      this.listAdapter = ((ListAdapter)localObject2);
      ((GridView)localObject1).setAdapter((ListAdapter)localObject2);
      AndroidUtilities.setListViewEdgeEffectColor(this.listView, -13421773);
      this.listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
      {
        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
        {
          if ((PhotoPickerActivity.this.selectedAlbum != null) && (PhotoPickerActivity.this.selectedAlbum.isVideo))
          {
            if ((paramAnonymousInt < 0) || (paramAnonymousInt >= PhotoPickerActivity.this.selectedAlbum.photos.size())) {}
            while (!PhotoPickerActivity.this.delegate.didSelectVideo(((MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramAnonymousInt)).path)) {
              return;
            }
            PhotoPickerActivity.this.finishFragment();
            return;
          }
          if (PhotoPickerActivity.this.selectedAlbum != null)
          {
            paramAnonymousAdapterView = PhotoPickerActivity.this.selectedAlbum.photos;
            label109:
            if ((paramAnonymousInt < 0) || (paramAnonymousInt >= paramAnonymousAdapterView.size())) {
              break label238;
            }
            if (PhotoPickerActivity.this.searchItem != null) {
              AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.searchItem.getSearchField());
            }
            PhotoViewer.getInstance().setParentActivity(PhotoPickerActivity.this.getParentActivity());
            paramAnonymousView = PhotoViewer.getInstance();
            if (!PhotoPickerActivity.this.singlePhoto) {
              break label240;
            }
          }
          label238:
          label240:
          for (int i = 1;; i = 0)
          {
            paramAnonymousView.openPhotoForSelect(paramAnonymousAdapterView, paramAnonymousInt, i, PhotoPickerActivity.this, PhotoPickerActivity.this.chatActivity);
            return;
            if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
            {
              paramAnonymousAdapterView = PhotoPickerActivity.this.recentImages;
              break label109;
            }
            paramAnonymousAdapterView = PhotoPickerActivity.this.searchResult;
            break label109;
            break;
          }
        }
      });
      if (this.selectedAlbum == null) {
        this.listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
          public boolean onItemLongClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
          {
            if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
            {
              paramAnonymousAdapterView = new AlertDialog.Builder(PhotoPickerActivity.this.getParentActivity());
              paramAnonymousAdapterView.setTitle(LocaleController.getString("AppName", 2131165299));
              paramAnonymousAdapterView.setMessage(LocaleController.getString("ClearSearch", 2131165514));
              paramAnonymousAdapterView.setPositiveButton(LocaleController.getString("ClearButton", 2131165508).toUpperCase(), new DialogInterface.OnClickListener()
              {
                public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int)
                {
                  PhotoPickerActivity.this.recentImages.clear();
                  if (PhotoPickerActivity.this.listAdapter != null) {
                    PhotoPickerActivity.this.listAdapter.notifyDataSetChanged();
                  }
                  MessagesStorage.getInstance().clearWebRecent(PhotoPickerActivity.this.type);
                }
              });
              paramAnonymousAdapterView.setNegativeButton(LocaleController.getString("Cancel", 2131165386), null);
              PhotoPickerActivity.this.showDialog(paramAnonymousAdapterView.create());
              return true;
            }
            return false;
          }
        });
      }
      this.emptyView = new TextView(paramContext);
      this.emptyView.setTextColor(-8355712);
      this.emptyView.setTextSize(20.0F);
      this.emptyView.setGravity(17);
      this.emptyView.setVisibility(8);
      if (this.selectedAlbum == null) {
        break label982;
      }
      this.emptyView.setText(LocaleController.getString("NoPhotos", 2131165943));
      label504:
      localFrameLayout.addView(this.emptyView);
      localObject1 = (FrameLayout.LayoutParams)this.emptyView.getLayoutParams();
      ((FrameLayout.LayoutParams)localObject1).width = -1;
      ((FrameLayout.LayoutParams)localObject1).height = -1;
      if (!this.singlePhoto) {
        break label1035;
      }
      i = 0;
      label546:
      ((FrameLayout.LayoutParams)localObject1).bottomMargin = i;
      this.emptyView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      this.emptyView.setOnTouchListener(new View.OnTouchListener()
      {
        public boolean onTouch(View paramAnonymousView, MotionEvent paramAnonymousMotionEvent)
        {
          return true;
        }
      });
      if (this.selectedAlbum == null)
      {
        this.listView.setOnScrollListener(new AbsListView.OnScrollListener()
        {
          public void onScroll(AbsListView paramAnonymousAbsListView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3)
          {
            if ((paramAnonymousInt2 != 0) && (paramAnonymousInt1 + paramAnonymousInt2 > paramAnonymousInt3 - 2) && (!PhotoPickerActivity.this.searching))
            {
              if ((PhotoPickerActivity.this.type != 0) || (PhotoPickerActivity.this.nextSearchBingString == null)) {
                break label71;
              }
              PhotoPickerActivity.this.searchBingImages(PhotoPickerActivity.this.lastSearchString, PhotoPickerActivity.this.searchResult.size(), 54);
            }
            label71:
            while ((PhotoPickerActivity.this.type != 1) || (PhotoPickerActivity.this.giphySearchEndReached)) {
              return;
            }
            PhotoPickerActivity.this.searchGiphyImages(PhotoPickerActivity.this.searchItem.getSearchField().getText().toString(), PhotoPickerActivity.this.nextGiphySearchOffset);
          }
          
          public void onScrollStateChanged(AbsListView paramAnonymousAbsListView, int paramAnonymousInt)
          {
            if (paramAnonymousInt == 1) {
              AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
            }
          }
        });
        this.progressView = new FrameLayout(paramContext);
        this.progressView.setVisibility(8);
        localFrameLayout.addView(this.progressView);
        localObject1 = (FrameLayout.LayoutParams)this.progressView.getLayoutParams();
        ((FrameLayout.LayoutParams)localObject1).width = -1;
        ((FrameLayout.LayoutParams)localObject1).height = -1;
        if (!this.singlePhoto) {
          break label1045;
        }
      }
    }
    label942:
    label972:
    label982:
    label1035:
    label1045:
    for (int i = j;; i = AndroidUtilities.dp(48.0F))
    {
      ((FrameLayout.LayoutParams)localObject1).bottomMargin = i;
      this.progressView.setLayoutParams((ViewGroup.LayoutParams)localObject1);
      localObject1 = new ProgressBar(paramContext);
      this.progressView.addView((View)localObject1);
      localObject2 = (FrameLayout.LayoutParams)((ProgressBar)localObject1).getLayoutParams();
      ((FrameLayout.LayoutParams)localObject2).width = -2;
      ((FrameLayout.LayoutParams)localObject2).height = -2;
      ((FrameLayout.LayoutParams)localObject2).gravity = 17;
      ((ProgressBar)localObject1).setLayoutParams((ViewGroup.LayoutParams)localObject2);
      updateSearchInterface();
      this.pickerBottomLayout = new PickerBottomLayout(paramContext);
      localFrameLayout.addView(this.pickerBottomLayout);
      paramContext = (FrameLayout.LayoutParams)this.pickerBottomLayout.getLayoutParams();
      paramContext.width = -1;
      paramContext.height = AndroidUtilities.dp(48.0F);
      paramContext.gravity = 80;
      this.pickerBottomLayout.setLayoutParams(paramContext);
      this.pickerBottomLayout.cancelButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PhotoPickerActivity.this.delegate.actionButtonPressed(true);
          PhotoPickerActivity.this.finishFragment();
        }
      });
      this.pickerBottomLayout.doneButton.setOnClickListener(new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          PhotoPickerActivity.this.sendSelectedPhotos();
        }
      });
      if (this.singlePhoto) {
        this.pickerBottomLayout.setVisibility(8);
      }
      this.listView.setEmptyView(this.emptyView);
      this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
      return this.fragmentView;
      if (this.type == 0)
      {
        this.actionBar.setTitle(LocaleController.getString("SearchImagesTitle", 2131166211));
        break;
      }
      if (this.type != 1) {
        break;
      }
      this.actionBar.setTitle(LocaleController.getString("SearchGifsTitle", 2131166208));
      break;
      if (this.type != 1) {
        break label141;
      }
      this.searchItem.getSearchField().setHint(LocaleController.getString("SearchGifsTitle", 2131166208));
      break label141;
      i = AndroidUtilities.dp(48.0F);
      break label339;
      if (this.type == 0)
      {
        this.emptyView.setText(LocaleController.getString("NoRecentPhotos", 2131165948));
        break label504;
      }
      if (this.type != 1) {
        break label504;
      }
      this.emptyView.setText(LocaleController.getString("NoRecentGIFs", 2131165947));
      break label504;
      i = AndroidUtilities.dp(48.0F);
      break label546;
    }
  }
  
  public void didReceivedNotification(int paramInt, Object... paramVarArgs)
  {
    if (paramInt == NotificationCenter.closeChats) {
      removeSelfFromStack();
    }
    while ((paramInt != NotificationCenter.recentImagesDidLoaded) || (this.selectedAlbum != null) || (this.type != ((Integer)paramVarArgs[0]).intValue())) {
      return;
    }
    this.recentImages = ((ArrayList)paramVarArgs[1]);
    this.loadingRecent = false;
    updateSearchInterface();
  }
  
  public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null)
    {
      paramFileLocation = new int[2];
      paramMessageObject.photoImage.getLocationInWindow(paramFileLocation);
      PhotoViewer.PlaceProviderObject localPlaceProviderObject = new PhotoViewer.PlaceProviderObject();
      localPlaceProviderObject.viewX = paramFileLocation[0];
      localPlaceProviderObject.viewY = (paramFileLocation[1] - AndroidUtilities.statusBarHeight);
      localPlaceProviderObject.parentView = this.listView;
      localPlaceProviderObject.imageReceiver = paramMessageObject.photoImage.getImageReceiver();
      localPlaceProviderObject.thumb = localPlaceProviderObject.imageReceiver.getBitmap();
      localPlaceProviderObject.scale = paramMessageObject.photoImage.getScaleX();
      paramMessageObject.checkBox.setVisibility(8);
      return localPlaceProviderObject;
    }
    return null;
  }
  
  public int getSelectedCount()
  {
    return this.selectedPhotos.size() + this.selectedWebPhotos.size();
  }
  
  public Bitmap getThumbForPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    paramMessageObject = getCellForIndex(paramInt);
    if (paramMessageObject != null) {
      return paramMessageObject.photoImage.getImageReceiver().getBitmap();
    }
    return null;
  }
  
  public boolean isPhotoChecked(int paramInt)
  {
    boolean bool = true;
    if (this.selectedAlbum != null) {
      return (paramInt >= 0) && (paramInt < this.selectedAlbum.photos.size()) && (this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt)).imageId)));
    }
    ArrayList localArrayList;
    if ((this.searchResult.isEmpty()) && (this.lastSearchString == null))
    {
      localArrayList = this.recentImages;
      if ((paramInt < 0) || (paramInt >= localArrayList.size()) || (!this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localArrayList.get(paramInt)).id))) {
        break label126;
      }
    }
    for (;;)
    {
      return bool;
      localArrayList = this.searchResult;
      break;
      label126:
      bool = false;
    }
  }
  
  public void onConfigurationChanged(Configuration paramConfiguration)
  {
    super.onConfigurationChanged(paramConfiguration);
    fixLayout();
  }
  
  public boolean onFragmentCreate()
  {
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().addObserver(this, NotificationCenter.recentImagesDidLoaded);
    if (this.selectedAlbum == null)
    {
      this.requestQueue = Volley.newRequestQueue(ApplicationLoader.applicationContext);
      if (this.recentImages.isEmpty())
      {
        MessagesStorage.getInstance().loadWebRecent(this.type);
        this.loadingRecent = true;
      }
    }
    return super.onFragmentCreate();
  }
  
  public void onFragmentDestroy()
  {
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.closeChats);
    NotificationCenter.getInstance().removeObserver(this, NotificationCenter.recentImagesDidLoaded);
    if (this.requestQueue != null)
    {
      this.requestQueue.cancelAll("search");
      this.requestQueue.stop();
    }
    super.onFragmentDestroy();
  }
  
  public void onResume()
  {
    super.onResume();
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
    if (this.searchItem != null)
    {
      this.searchItem.openSearch(true);
      getParentActivity().getWindow().setSoftInputMode(32);
    }
    fixLayout();
  }
  
  public void onTransitionAnimationEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if ((paramBoolean1) && (this.searchItem != null)) {
      AndroidUtilities.showKeyboard(this.searchItem.getSearchField());
    }
  }
  
  public void sendButtonPressed(int paramInt)
  {
    if (this.selectedAlbum != null) {
      if (this.selectedPhotos.isEmpty()) {
        if ((paramInt >= 0) && (paramInt < this.selectedAlbum.photos.size())) {}
      }
    }
    label147:
    for (;;)
    {
      return;
      Object localObject = (MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt);
      this.selectedPhotos.put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
      do
      {
        sendSelectedPhotos();
        return;
      } while (!this.selectedPhotos.isEmpty());
      if ((this.searchResult.isEmpty()) && (this.lastSearchString == null)) {}
      for (localObject = this.recentImages;; localObject = this.searchResult)
      {
        if ((paramInt < 0) || (paramInt >= ((ArrayList)localObject).size())) {
          break label147;
        }
        localObject = (MediaController.SearchImage)((ArrayList)localObject).get(paramInt);
        this.selectedWebPhotos.put(((MediaController.SearchImage)localObject).id, localObject);
        break;
      }
    }
  }
  
  public void setDelegate(PhotoPickerActivityDelegate paramPhotoPickerActivityDelegate)
  {
    this.delegate = paramPhotoPickerActivityDelegate;
  }
  
  public void setPhotoChecked(int paramInt)
  {
    boolean bool = true;
    Object localObject;
    label82:
    int j;
    int i;
    if (this.selectedAlbum != null)
    {
      if ((paramInt < 0) || (paramInt >= this.selectedAlbum.photos.size())) {
        return;
      }
      localObject = (MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt);
      if (this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)))
      {
        this.selectedPhotos.remove(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));
        bool = false;
        j = this.listView.getChildCount();
        i = 0;
      }
    }
    for (;;)
    {
      if (i < j)
      {
        localObject = this.listView.getChildAt(i);
        if (((Integer)((View)localObject).getTag()).intValue() == paramInt) {
          ((PhotoPickerPhotoCell)localObject).setChecked(bool, false);
        }
      }
      else
      {
        this.pickerBottomLayout.updateSelectedCount(this.selectedPhotos.size() + this.selectedWebPhotos.size(), true);
        this.delegate.selectedPhotosChanged();
        return;
        this.selectedPhotos.put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
        break label82;
        if ((this.searchResult.isEmpty()) && (this.lastSearchString == null)) {}
        for (localObject = this.recentImages;; localObject = this.searchResult)
        {
          if ((paramInt < 0) || (paramInt >= ((ArrayList)localObject).size())) {
            break label275;
          }
          localObject = (MediaController.SearchImage)((ArrayList)localObject).get(paramInt);
          if (!this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localObject).id)) {
            break label277;
          }
          this.selectedWebPhotos.remove(((MediaController.SearchImage)localObject).id);
          bool = false;
          break;
        }
        label275:
        break;
        label277:
        this.selectedWebPhotos.put(((MediaController.SearchImage)localObject).id, localObject);
        break label82;
      }
      i += 1;
    }
  }
  
  public void updatePhotoAtIndex(int paramInt)
  {
    PhotoPickerPhotoCell localPhotoPickerPhotoCell = getCellForIndex(paramInt);
    if (localPhotoPickerPhotoCell != null)
    {
      if (this.selectedAlbum == null) {
        break label227;
      }
      localPhotoPickerPhotoCell.photoImage.setOrientation(0, true);
      localObject = (MediaController.PhotoEntry)this.selectedAlbum.photos.get(paramInt);
      if (((MediaController.PhotoEntry)localObject).thumbPath != null) {
        localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.PhotoEntry)localObject).thumbPath, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837858));
      }
    }
    else
    {
      return;
    }
    if (((MediaController.PhotoEntry)localObject).path != null)
    {
      localPhotoPickerPhotoCell.photoImage.setOrientation(((MediaController.PhotoEntry)localObject).orientation, true);
      if (((MediaController.PhotoEntry)localObject).isVideo)
      {
        localPhotoPickerPhotoCell.photoImage.setImage("vthumb://" + ((MediaController.PhotoEntry)localObject).imageId + ":" + ((MediaController.PhotoEntry)localObject).path, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837858));
        return;
      }
      localPhotoPickerPhotoCell.photoImage.setImage("thumb://" + ((MediaController.PhotoEntry)localObject).imageId + ":" + ((MediaController.PhotoEntry)localObject).path, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837858));
      return;
    }
    localPhotoPickerPhotoCell.photoImage.setImageResource(2130837858);
    return;
    label227:
    if ((this.searchResult.isEmpty()) && (this.lastSearchString == null)) {}
    for (Object localObject = this.recentImages;; localObject = this.searchResult)
    {
      localObject = (MediaController.SearchImage)((ArrayList)localObject).get(paramInt);
      if ((((MediaController.SearchImage)localObject).document == null) || (((MediaController.SearchImage)localObject).document.thumb == null)) {
        break;
      }
      localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).document.thumb.location, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837858));
      return;
    }
    if (((MediaController.SearchImage)localObject).thumbPath != null)
    {
      localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).thumbPath, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837858));
      return;
    }
    if ((((MediaController.SearchImage)localObject).thumbUrl != null) && (((MediaController.SearchImage)localObject).thumbUrl.length() > 0))
    {
      localPhotoPickerPhotoCell.photoImage.setImage(((MediaController.SearchImage)localObject).thumbUrl, null, localPhotoPickerPhotoCell.getContext().getResources().getDrawable(2130837858));
      return;
    }
    localPhotoPickerPhotoCell.photoImage.setImageResource(2130837858);
  }
  
  public void willHidePhotoViewer()
  {
    if (this.listAdapter != null) {
      this.listAdapter.notifyDataSetChanged();
    }
  }
  
  public void willSwitchFromPhoto(MessageObject paramMessageObject, TLRPC.FileLocation paramFileLocation, int paramInt)
  {
    int j = this.listView.getChildCount();
    int i = 0;
    if (i < j)
    {
      paramMessageObject = this.listView.getChildAt(i);
      if (paramMessageObject.getTag() == null) {
        break label89;
      }
    }
    label36:
    label89:
    label149:
    for (;;)
    {
      i += 1;
      break;
      paramFileLocation = (PhotoPickerPhotoCell)paramMessageObject;
      int k = ((Integer)paramMessageObject.getTag()).intValue();
      if (this.selectedAlbum != null)
      {
        if ((k >= 0) && (k < this.selectedAlbum.photos.size())) {
          if (k == paramInt) {
            paramFileLocation.checkBox.setVisibility(0);
          }
        }
      }
      else
      {
        if ((this.searchResult.isEmpty()) && (this.lastSearchString == null)) {}
        for (paramMessageObject = this.recentImages;; paramMessageObject = this.searchResult)
        {
          if (k < 0) {
            break label149;
          }
          if (k < paramMessageObject.size()) {
            break;
          }
          break label36;
        }
      }
    }
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
      return PhotoPickerActivity.this.selectedAlbum != null;
    }
    
    public int getCount()
    {
      int j = 0;
      int i = 0;
      if (PhotoPickerActivity.this.selectedAlbum == null)
      {
        if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {
          return PhotoPickerActivity.this.recentImages.size();
        }
        if (PhotoPickerActivity.this.type == 0)
        {
          j = PhotoPickerActivity.this.searchResult.size();
          if (PhotoPickerActivity.this.nextSearchBingString == null) {}
          for (;;)
          {
            return i + j;
            i = 1;
          }
        }
        if (PhotoPickerActivity.this.type == 1)
        {
          int k = PhotoPickerActivity.this.searchResult.size();
          if (PhotoPickerActivity.this.giphySearchEndReached) {}
          for (i = j;; i = 1) {
            return i + k;
          }
        }
      }
      return PhotoPickerActivity.this.selectedAlbum.photos.size();
    }
    
    public Object getItem(int paramInt)
    {
      return null;
    }
    
    public long getItemId(int paramInt)
    {
      return paramInt;
    }
    
    public int getItemViewType(int paramInt)
    {
      if ((PhotoPickerActivity.this.selectedAlbum != null) || ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null) && (paramInt < PhotoPickerActivity.this.recentImages.size())) || (paramInt < PhotoPickerActivity.this.searchResult.size())) {
        return 0;
      }
      return 1;
    }
    
    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
    {
      int i = getItemViewType(paramInt);
      BackupImageView localBackupImageView;
      label186:
      boolean bool1;
      label221:
      boolean bool2;
      if (i == 0)
      {
        localObject = (PhotoPickerPhotoCell)paramView;
        paramViewGroup = paramView;
        if (paramView == null)
        {
          paramViewGroup = new PhotoPickerPhotoCell(this.mContext);
          localObject = (PhotoPickerPhotoCell)paramViewGroup;
          ((PhotoPickerPhotoCell)localObject).checkFrame.setOnClickListener(new View.OnClickListener()
          {
            public void onClick(View paramAnonymousView)
            {
              int i = ((Integer)((View)paramAnonymousView.getParent()).getTag()).intValue();
              Object localObject;
              if (PhotoPickerActivity.this.selectedAlbum != null)
              {
                localObject = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(i);
                if (PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)))
                {
                  PhotoPickerActivity.this.selectedPhotos.remove(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId));
                  ((MediaController.PhotoEntry)localObject).imagePath = null;
                  ((MediaController.PhotoEntry)localObject).thumbPath = null;
                  ((MediaController.PhotoEntry)localObject).stickers.clear();
                  PhotoPickerActivity.this.updatePhotoAtIndex(i);
                }
                for (;;)
                {
                  ((PhotoPickerPhotoCell)paramAnonymousView.getParent()).setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId)), true);
                  PhotoPickerActivity.this.pickerBottomLayout.updateSelectedCount(PhotoPickerActivity.this.selectedPhotos.size() + PhotoPickerActivity.this.selectedWebPhotos.size(), true);
                  PhotoPickerActivity.this.delegate.selectedPhotosChanged();
                  return;
                  PhotoPickerActivity.this.selectedPhotos.put(Integer.valueOf(((MediaController.PhotoEntry)localObject).imageId), localObject);
                }
              }
              AndroidUtilities.hideKeyboard(PhotoPickerActivity.this.getParentActivity().getCurrentFocus());
              if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
              {
                localObject = (MediaController.SearchImage)PhotoPickerActivity.this.recentImages.get(((Integer)((View)paramAnonymousView.getParent()).getTag()).intValue());
                label314:
                if (!PhotoPickerActivity.this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localObject).id)) {
                  break label440;
                }
                PhotoPickerActivity.this.selectedWebPhotos.remove(((MediaController.SearchImage)localObject).id);
                ((MediaController.SearchImage)localObject).imagePath = null;
                ((MediaController.SearchImage)localObject).thumbPath = null;
                PhotoPickerActivity.this.updatePhotoAtIndex(i);
              }
              for (;;)
              {
                ((PhotoPickerPhotoCell)paramAnonymousView.getParent()).setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(((MediaController.SearchImage)localObject).id), true);
                break;
                localObject = (MediaController.SearchImage)PhotoPickerActivity.this.searchResult.get(((Integer)((View)paramAnonymousView.getParent()).getTag()).intValue());
                break label314;
                label440:
                PhotoPickerActivity.this.selectedWebPhotos.put(((MediaController.SearchImage)localObject).id, localObject);
              }
            }
          });
          paramView = ((PhotoPickerPhotoCell)localObject).checkFrame;
          if (PhotoPickerActivity.this.singlePhoto)
          {
            i = 8;
            paramView.setVisibility(i);
          }
        }
        else
        {
          ((PhotoPickerPhotoCell)localObject).itemWidth = PhotoPickerActivity.this.itemWidth;
          localBackupImageView = ((PhotoPickerPhotoCell)paramViewGroup).photoImage;
          localBackupImageView.setTag(Integer.valueOf(paramInt));
          paramViewGroup.setTag(Integer.valueOf(paramInt));
          localBackupImageView.setOrientation(0, true);
          if (PhotoPickerActivity.this.selectedAlbum == null) {
            break label427;
          }
          paramView = (MediaController.PhotoEntry)PhotoPickerActivity.this.selectedAlbum.photos.get(paramInt);
          if (paramView.thumbPath == null) {
            break label283;
          }
          localBackupImageView.setImage(paramView.thumbPath, null, this.mContext.getResources().getDrawable(2130837858));
          ((PhotoPickerPhotoCell)localObject).setChecked(PhotoPickerActivity.this.selectedPhotos.containsKey(Integer.valueOf(paramView.imageId)), false);
          bool1 = PhotoViewer.getInstance().isShowingImage(paramView.path);
          paramView = localBackupImageView.getImageReceiver();
          if (bool1) {
            break label676;
          }
          bool2 = true;
          label235:
          paramView.setVisible(bool2, true);
          paramView = ((PhotoPickerPhotoCell)localObject).checkBox;
          if ((!PhotoPickerActivity.this.singlePhoto) && (!bool1)) {
            break label682;
          }
          paramInt = 8;
          label266:
          paramView.setVisibility(paramInt);
          localObject = paramViewGroup;
        }
      }
      label283:
      label427:
      label465:
      label561:
      label661:
      label676:
      label682:
      do
      {
        return (View)localObject;
        i = 0;
        break;
        if (paramView.path != null)
        {
          localBackupImageView.setOrientation(paramView.orientation, true);
          if (paramView.isVideo)
          {
            localBackupImageView.setImage("vthumb://" + paramView.imageId + ":" + paramView.path, null, this.mContext.getResources().getDrawable(2130837858));
            break label186;
          }
          localBackupImageView.setImage("thumb://" + paramView.imageId + ":" + paramView.path, null, this.mContext.getResources().getDrawable(2130837858));
          break label186;
        }
        localBackupImageView.setImageResource(2130837858);
        break label186;
        if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null))
        {
          paramView = (MediaController.SearchImage)PhotoPickerActivity.this.recentImages.get(paramInt);
          if (paramView.thumbPath == null) {
            break label561;
          }
          localBackupImageView.setImage(paramView.thumbPath, null, this.mContext.getResources().getDrawable(2130837858));
        }
        for (;;)
        {
          ((PhotoPickerPhotoCell)localObject).setChecked(PhotoPickerActivity.this.selectedWebPhotos.containsKey(paramView.id), false);
          if (paramView.document == null) {
            break label661;
          }
          bool1 = PhotoViewer.getInstance().isShowingImage(FileLoader.getPathToAttach(paramView.document, true).getAbsolutePath());
          break;
          paramView = (MediaController.SearchImage)PhotoPickerActivity.this.searchResult.get(paramInt);
          break label465;
          if ((paramView.thumbUrl != null) && (paramView.thumbUrl.length() > 0)) {
            localBackupImageView.setImage(paramView.thumbUrl, null, this.mContext.getResources().getDrawable(2130837858));
          } else if ((paramView.document != null) && (paramView.document.thumb != null)) {
            localBackupImageView.setImage(paramView.document.thumb.location, null, this.mContext.getResources().getDrawable(2130837858));
          } else {
            localBackupImageView.setImageResource(2130837858);
          }
        }
        bool1 = PhotoViewer.getInstance().isShowingImage(paramView.imageUrl);
        break label221;
        bool2 = false;
        break label235;
        paramInt = 0;
        break label266;
        localObject = paramView;
      } while (i != 1);
      Object localObject = paramView;
      if (paramView == null) {
        localObject = ((LayoutInflater)this.mContext.getSystemService("layout_inflater")).inflate(2130903051, paramViewGroup, false);
      }
      paramView = ((View)localObject).getLayoutParams();
      paramView.width = PhotoPickerActivity.this.itemWidth;
      paramView.height = PhotoPickerActivity.this.itemWidth;
      ((View)localObject).setLayoutParams(paramView);
      return (View)localObject;
    }
    
    public int getViewTypeCount()
    {
      return 2;
    }
    
    public boolean hasStableIds()
    {
      return true;
    }
    
    public boolean isEmpty()
    {
      if (PhotoPickerActivity.this.selectedAlbum != null) {
        return PhotoPickerActivity.this.selectedAlbum.photos.isEmpty();
      }
      if ((PhotoPickerActivity.this.searchResult.isEmpty()) && (PhotoPickerActivity.this.lastSearchString == null)) {
        return PhotoPickerActivity.this.recentImages.isEmpty();
      }
      return PhotoPickerActivity.this.searchResult.isEmpty();
    }
    
    public boolean isEnabled(int paramInt)
    {
      if (PhotoPickerActivity.this.selectedAlbum == null)
      {
        if ((!PhotoPickerActivity.this.searchResult.isEmpty()) || (PhotoPickerActivity.this.lastSearchString != null)) {
          break label51;
        }
        if (paramInt >= PhotoPickerActivity.this.recentImages.size()) {
          break label49;
        }
      }
      label49:
      label51:
      while (paramInt < PhotoPickerActivity.this.searchResult.size())
      {
        return true;
        return false;
      }
      return false;
    }
  }
  
  public static abstract interface PhotoPickerActivityDelegate
  {
    public abstract void actionButtonPressed(boolean paramBoolean);
    
    public abstract boolean didSelectVideo(String paramString);
    
    public abstract void selectedPhotosChanged();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/PhotoPickerActivity.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.Theme.ThemeInfo;
import org.telegram.ui.Components.LayoutHelper;

public class ThemeCell
  extends FrameLayout
{
  private static byte[] bytes = new byte['Ѐ'];
  private ImageView checkImage;
  private Theme.ThemeInfo currentThemeInfo;
  private boolean isNightTheme;
  private boolean needDivider;
  private ImageView optionsButton;
  private Paint paint;
  private TextView textView;
  
  public ThemeCell(Context paramContext, boolean paramBoolean)
  {
    super(paramContext);
    setWillNotDraw(false);
    this.isNightTheme = paramBoolean;
    this.paint = new Paint(1);
    this.textView = new TextView(paramContext);
    this.textView.setTextSize(1, 16.0F);
    this.textView.setLines(1);
    this.textView.setMaxLines(1);
    this.textView.setSingleLine(true);
    this.textView.setPadding(0, 0, 0, AndroidUtilities.dp(1.0F));
    this.textView.setEllipsize(TextUtils.TruncateAt.END);
    Object localObject = this.textView;
    int j;
    label139:
    float f1;
    label149:
    float f2;
    if (LocaleController.isRTL)
    {
      j = 5;
      ((TextView)localObject).setGravity(j | 0x10);
      localObject = this.textView;
      if (!LocaleController.isRTL) {
        break label381;
      }
      j = 5;
      if (!LocaleController.isRTL) {
        break label387;
      }
      f1 = 101.0F;
      if (!LocaleController.isRTL) {
        break label394;
      }
      f2 = 60.0F;
      label159:
      addView((View)localObject, LayoutHelper.createFrame(-1, -1.0F, j | 0x30, f1, 0.0F, f2, 0.0F));
      this.checkImage = new ImageView(paramContext);
      this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
      this.checkImage.setImageResource(NUM);
      if (this.isNightTheme) {
        break label412;
      }
      localObject = this.checkImage;
      if (!LocaleController.isRTL) {
        break label401;
      }
      j = 3;
      label247:
      addView((View)localObject, LayoutHelper.createFrame(19, 14.0F, j | 0x10, 55.0F, 0.0F, 55.0F, 0.0F));
      this.optionsButton = new ImageView(paramContext);
      this.optionsButton.setFocusable(false);
      this.optionsButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor("stickers_menuSelector")));
      this.optionsButton.setImageResource(NUM);
      this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("stickers_menu"), PorterDuff.Mode.MULTIPLY));
      this.optionsButton.setScaleType(ImageView.ScaleType.CENTER);
      paramContext = this.optionsButton;
      if (!LocaleController.isRTL) {
        break label407;
      }
    }
    for (;;)
    {
      addView(paramContext, LayoutHelper.createFrame(48, 48, i | 0x30));
      return;
      j = 3;
      break;
      label381:
      j = 3;
      break label139;
      label387:
      f1 = 60.0F;
      break label149;
      label394:
      f2 = 101.0F;
      break label159;
      label401:
      j = 5;
      break label247;
      label407:
      i = 5;
    }
    label412:
    paramContext = this.checkImage;
    if (LocaleController.isRTL) {}
    for (;;)
    {
      addView(paramContext, LayoutHelper.createFrame(19, 14.0F, i | 0x10, 17.0F, 0.0F, 17.0F, 0.0F));
      break;
      i = 5;
    }
  }
  
  public Theme.ThemeInfo getCurrentThemeInfo()
  {
    return this.currentThemeInfo;
  }
  
  public TextView getTextView()
  {
    return this.textView;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    this.checkImage.setColorFilter(new PorterDuffColorFilter(Theme.getColor("featuredStickers_addedIcon"), PorterDuff.Mode.MULTIPLY));
    this.textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
  }
  
  protected void onDraw(Canvas paramCanvas)
  {
    if (this.needDivider) {
      paramCanvas.drawLine(getPaddingLeft(), getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
    }
    int i = AndroidUtilities.dp(27.0F);
    int j = i;
    if (LocaleController.isRTL) {
      j = getWidth() - i;
    }
    paramCanvas.drawCircle(j, AndroidUtilities.dp(24.0F), AndroidUtilities.dp(11.0F), this.paint);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    paramInt2 = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(paramInt1), NUM);
    int i = AndroidUtilities.dp(48.0F);
    if (this.needDivider) {}
    for (paramInt1 = 1;; paramInt1 = 0)
    {
      super.onMeasure(paramInt2, View.MeasureSpec.makeMeasureSpec(paramInt1 + i, NUM));
      return;
    }
  }
  
  public void setOnOptionsClick(View.OnClickListener paramOnClickListener)
  {
    this.optionsButton.setOnClickListener(paramOnClickListener);
  }
  
  public void setTextColor(int paramInt)
  {
    this.textView.setTextColor(paramInt);
  }
  
  /* Error */
  public void setTheme(Theme.ThemeInfo paramThemeInfo, boolean paramBoolean)
  {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: putfield 171	org/telegram/ui/Cells/ThemeCell:currentThemeInfo	Lorg/telegram/ui/ActionBar/Theme$ThemeInfo;
    //   5: aload_1
    //   6: invokevirtual 247	org/telegram/ui/ActionBar/Theme$ThemeInfo:getName	()Ljava/lang/String;
    //   9: astore_3
    //   10: aload_3
    //   11: astore 4
    //   13: aload_3
    //   14: ldc -7
    //   16: invokevirtual 255	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   19: ifeq +16 -> 35
    //   22: aload_3
    //   23: iconst_0
    //   24: aload_3
    //   25: bipush 46
    //   27: invokevirtual 258	java/lang/String:lastIndexOf	(I)I
    //   30: invokevirtual 262	java/lang/String:substring	(II)Ljava/lang/String;
    //   33: astore 4
    //   35: aload_0
    //   36: getfield 46	org/telegram/ui/Cells/ThemeCell:textView	Landroid/widget/TextView;
    //   39: aload 4
    //   41: invokevirtual 266	android/widget/TextView:setText	(Ljava/lang/CharSequence;)V
    //   44: aload_0
    //   45: iload_2
    //   46: putfield 185	org/telegram/ui/Cells/ThemeCell:needDivider	Z
    //   49: aload_0
    //   50: invokevirtual 269	org/telegram/ui/Cells/ThemeCell:updateCurrentThemeCheck	()V
    //   53: iconst_0
    //   54: istore 5
    //   56: iconst_0
    //   57: istore 6
    //   59: iconst_0
    //   60: istore 7
    //   62: aload_1
    //   63: getfield 273	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   66: ifnonnull +10 -> 76
    //   69: aload_1
    //   70: getfield 276	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   73: ifnull +247 -> 320
    //   76: aconst_null
    //   77: astore 8
    //   79: aconst_null
    //   80: astore 9
    //   82: iconst_0
    //   83: istore 10
    //   85: aload 8
    //   87: astore 4
    //   89: aload_1
    //   90: getfield 276	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   93: ifnull +246 -> 339
    //   96: aload 8
    //   98: astore 4
    //   100: aload_1
    //   101: getfield 276	org/telegram/ui/ActionBar/Theme$ThemeInfo:assetName	Ljava/lang/String;
    //   104: invokestatic 280	org/telegram/ui/ActionBar/Theme:getAssetFile	(Ljava/lang/String;)Ljava/io/File;
    //   107: astore_3
    //   108: aload 8
    //   110: astore 4
    //   112: new 282	java/io/FileInputStream
    //   115: astore_1
    //   116: aload 8
    //   118: astore 4
    //   120: aload_1
    //   121: aload_3
    //   122: invokespecial 285	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   125: iconst_0
    //   126: istore 11
    //   128: iload 7
    //   130: istore 5
    //   132: iload 10
    //   134: istore 12
    //   136: iload 5
    //   138: istore 7
    //   140: aload_1
    //   141: getstatic 22	org/telegram/ui/Cells/ThemeCell:bytes	[B
    //   144: invokevirtual 289	java/io/FileInputStream:read	([B)I
    //   147: istore 13
    //   149: iload 5
    //   151: istore 7
    //   153: iload 13
    //   155: iconst_m1
    //   156: if_icmpeq +148 -> 304
    //   159: iconst_0
    //   160: istore 14
    //   162: iconst_0
    //   163: istore 15
    //   165: iload 12
    //   167: istore 6
    //   169: iload 6
    //   171: istore 10
    //   173: iload 5
    //   175: istore 6
    //   177: iload 11
    //   179: istore 16
    //   181: iload 15
    //   183: iload 13
    //   185: if_icmpge +96 -> 281
    //   188: iload 5
    //   190: istore 7
    //   192: iload 10
    //   194: istore 6
    //   196: iload 11
    //   198: istore 16
    //   200: iload 14
    //   202: istore 17
    //   204: getstatic 22	org/telegram/ui/Cells/ThemeCell:bytes	[B
    //   207: iload 15
    //   209: baload
    //   210: bipush 10
    //   212: if_icmpne +357 -> 569
    //   215: iload 11
    //   217: iconst_1
    //   218: iadd
    //   219: istore 16
    //   221: iload 15
    //   223: iload 14
    //   225: isub
    //   226: iconst_1
    //   227: iadd
    //   228: istore 6
    //   230: iload 5
    //   232: istore 7
    //   234: new 251	java/lang/String
    //   237: astore 4
    //   239: iload 5
    //   241: istore 7
    //   243: aload 4
    //   245: getstatic 22	org/telegram/ui/Cells/ThemeCell:bytes	[B
    //   248: iload 14
    //   250: iload 6
    //   252: iconst_1
    //   253: isub
    //   254: ldc_w 291
    //   257: invokespecial 294	java/lang/String:<init>	([BIILjava/lang/String;)V
    //   260: iload 5
    //   262: istore 7
    //   264: aload 4
    //   266: ldc_w 296
    //   269: invokevirtual 299	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   272: istore_2
    //   273: iload_2
    //   274: ifeq +84 -> 358
    //   277: iload 5
    //   279: istore 6
    //   281: iload 6
    //   283: istore 7
    //   285: iload 12
    //   287: iload 10
    //   289: if_icmpeq +15 -> 304
    //   292: iload 16
    //   294: sipush 500
    //   297: if_icmplt +286 -> 583
    //   300: iload 6
    //   302: istore 7
    //   304: iload 7
    //   306: istore 5
    //   308: aload_1
    //   309: ifnull +11 -> 320
    //   312: aload_1
    //   313: invokevirtual 302	java/io/FileInputStream:close	()V
    //   316: iload 7
    //   318: istore 5
    //   320: iload 5
    //   322: ifne +16 -> 338
    //   325: aload_0
    //   326: getfield 41	org/telegram/ui/Cells/ThemeCell:paint	Landroid/graphics/Paint;
    //   329: ldc_w 304
    //   332: invokestatic 307	org/telegram/ui/ActionBar/Theme:getDefaultColor	(Ljava/lang/String;)I
    //   335: invokevirtual 310	android/graphics/Paint:setColor	(I)V
    //   338: return
    //   339: aload 8
    //   341: astore 4
    //   343: new 312	java/io/File
    //   346: dup
    //   347: aload_1
    //   348: getfield 273	org/telegram/ui/ActionBar/Theme$ThemeInfo:pathToFile	Ljava/lang/String;
    //   351: invokespecial 315	java/io/File:<init>	(Ljava/lang/String;)V
    //   354: astore_3
    //   355: goto -247 -> 108
    //   358: iload 5
    //   360: istore 7
    //   362: aload 4
    //   364: bipush 61
    //   366: invokevirtual 318	java/lang/String:indexOf	(I)I
    //   369: istore 11
    //   371: iload 11
    //   373: iconst_m1
    //   374: if_icmpeq +181 -> 555
    //   377: iload 5
    //   379: istore 7
    //   381: aload 4
    //   383: iconst_0
    //   384: iload 11
    //   386: invokevirtual 262	java/lang/String:substring	(II)Ljava/lang/String;
    //   389: ldc_w 304
    //   392: invokevirtual 322	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   395: ifeq +160 -> 555
    //   398: iload 5
    //   400: istore 7
    //   402: aload 4
    //   404: iload 11
    //   406: iconst_1
    //   407: iadd
    //   408: invokevirtual 325	java/lang/String:substring	(I)Ljava/lang/String;
    //   411: astore 4
    //   413: iload 5
    //   415: istore 7
    //   417: aload 4
    //   419: invokevirtual 328	java/lang/String:length	()I
    //   422: ifle +116 -> 538
    //   425: iload 5
    //   427: istore 7
    //   429: aload 4
    //   431: iconst_0
    //   432: invokevirtual 332	java/lang/String:charAt	(I)C
    //   435: istore 6
    //   437: iload 6
    //   439: bipush 35
    //   441: if_icmpne +97 -> 538
    //   444: iload 5
    //   446: istore 7
    //   448: aload 4
    //   450: invokestatic 337	android/graphics/Color:parseColor	(Ljava/lang/String;)I
    //   453: istore 6
    //   455: iload 6
    //   457: istore 5
    //   459: iconst_1
    //   460: istore 7
    //   462: iconst_1
    //   463: istore 6
    //   465: aload_0
    //   466: getfield 41	org/telegram/ui/Cells/ThemeCell:paint	Landroid/graphics/Paint;
    //   469: iload 5
    //   471: invokevirtual 310	android/graphics/Paint:setColor	(I)V
    //   474: goto -193 -> 281
    //   477: astore 4
    //   479: aload 4
    //   481: astore_3
    //   482: aload_1
    //   483: astore 4
    //   485: aload_3
    //   486: invokestatic 343	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   489: iload 7
    //   491: istore 5
    //   493: aload_1
    //   494: ifnull -174 -> 320
    //   497: aload_1
    //   498: invokevirtual 302	java/io/FileInputStream:close	()V
    //   501: iload 7
    //   503: istore 5
    //   505: goto -185 -> 320
    //   508: astore_1
    //   509: aload_1
    //   510: invokestatic 343	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   513: iload 7
    //   515: istore 5
    //   517: goto -197 -> 320
    //   520: astore_3
    //   521: iload 5
    //   523: istore 7
    //   525: aload 4
    //   527: invokestatic 349	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   530: invokevirtual 354	java/lang/Integer:intValue	()I
    //   533: istore 5
    //   535: goto -76 -> 459
    //   538: iload 5
    //   540: istore 7
    //   542: aload 4
    //   544: invokestatic 349	org/telegram/messenger/Utilities:parseInt	(Ljava/lang/String;)Ljava/lang/Integer;
    //   547: invokevirtual 354	java/lang/Integer:intValue	()I
    //   550: istore 5
    //   552: goto -93 -> 459
    //   555: iload 14
    //   557: iload 6
    //   559: iadd
    //   560: istore 17
    //   562: iload 10
    //   564: iload 6
    //   566: iadd
    //   567: istore 6
    //   569: iinc 15 1
    //   572: iload 16
    //   574: istore 11
    //   576: iload 17
    //   578: istore 14
    //   580: goto -411 -> 169
    //   583: iload 6
    //   585: istore 7
    //   587: aload_1
    //   588: invokevirtual 358	java/io/FileInputStream:getChannel	()Ljava/nio/channels/FileChannel;
    //   591: iload 10
    //   593: i2l
    //   594: invokevirtual 364	java/nio/channels/FileChannel:position	(J)Ljava/nio/channels/FileChannel;
    //   597: pop
    //   598: iload 6
    //   600: istore 5
    //   602: iload 16
    //   604: istore 11
    //   606: iload 6
    //   608: ifeq -476 -> 132
    //   611: iload 6
    //   613: istore 7
    //   615: goto -311 -> 304
    //   618: astore_1
    //   619: aload_1
    //   620: invokestatic 343	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   623: iload 7
    //   625: istore 5
    //   627: goto -307 -> 320
    //   630: astore_1
    //   631: aload 4
    //   633: astore_3
    //   634: aload_3
    //   635: ifnull +7 -> 642
    //   638: aload_3
    //   639: invokevirtual 302	java/io/FileInputStream:close	()V
    //   642: aload_1
    //   643: athrow
    //   644: astore 4
    //   646: aload 4
    //   648: invokestatic 343	org/telegram/messenger/FileLog:e	(Ljava/lang/Throwable;)V
    //   651: goto -9 -> 642
    //   654: astore 4
    //   656: aload_1
    //   657: astore_3
    //   658: aload 4
    //   660: astore_1
    //   661: goto -27 -> 634
    //   664: astore_3
    //   665: iload 6
    //   667: istore 7
    //   669: aload 9
    //   671: astore_1
    //   672: goto -190 -> 482
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	675	0	this	ThemeCell
    //   0	675	1	paramThemeInfo	Theme.ThemeInfo
    //   0	675	2	paramBoolean	boolean
    //   9	477	3	localObject1	Object
    //   520	1	3	localException1	Exception
    //   633	25	3	localObject2	Object
    //   664	1	3	localThrowable1	Throwable
    //   11	438	4	localObject3	Object
    //   477	3	4	localThrowable2	Throwable
    //   483	149	4	localThemeInfo	Theme.ThemeInfo
    //   644	3	4	localException2	Exception
    //   654	5	4	localObject4	Object
    //   54	572	5	i	int
    //   57	609	6	j	int
    //   60	608	7	k	int
    //   77	263	8	localObject5	Object
    //   80	590	9	localObject6	Object
    //   83	509	10	m	int
    //   126	479	11	n	int
    //   134	156	12	i1	int
    //   147	39	13	i2	int
    //   160	419	14	i3	int
    //   163	407	15	i4	int
    //   179	424	16	i5	int
    //   202	375	17	i6	int
    // Exception table:
    //   from	to	target	type
    //   140	149	477	java/lang/Throwable
    //   204	215	477	java/lang/Throwable
    //   234	239	477	java/lang/Throwable
    //   243	260	477	java/lang/Throwable
    //   264	273	477	java/lang/Throwable
    //   362	371	477	java/lang/Throwable
    //   381	398	477	java/lang/Throwable
    //   402	413	477	java/lang/Throwable
    //   417	425	477	java/lang/Throwable
    //   429	437	477	java/lang/Throwable
    //   448	455	477	java/lang/Throwable
    //   465	474	477	java/lang/Throwable
    //   525	535	477	java/lang/Throwable
    //   542	552	477	java/lang/Throwable
    //   587	598	477	java/lang/Throwable
    //   497	501	508	java/lang/Exception
    //   448	455	520	java/lang/Exception
    //   312	316	618	java/lang/Exception
    //   89	96	630	finally
    //   100	108	630	finally
    //   112	116	630	finally
    //   120	125	630	finally
    //   343	355	630	finally
    //   485	489	630	finally
    //   638	642	644	java/lang/Exception
    //   140	149	654	finally
    //   204	215	654	finally
    //   234	239	654	finally
    //   243	260	654	finally
    //   264	273	654	finally
    //   362	371	654	finally
    //   381	398	654	finally
    //   402	413	654	finally
    //   417	425	654	finally
    //   429	437	654	finally
    //   448	455	654	finally
    //   465	474	654	finally
    //   525	535	654	finally
    //   542	552	654	finally
    //   587	598	654	finally
    //   89	96	664	java/lang/Throwable
    //   100	108	664	java/lang/Throwable
    //   112	116	664	java/lang/Throwable
    //   120	125	664	java/lang/Throwable
    //   343	355	664	java/lang/Throwable
  }
  
  public void updateCurrentThemeCheck()
  {
    Theme.ThemeInfo localThemeInfo;
    if (this.isNightTheme)
    {
      localThemeInfo = Theme.getCurrentNightTheme();
      if (this.currentThemeInfo != localThemeInfo) {
        break label48;
      }
    }
    label48:
    for (int i = 0;; i = 4)
    {
      if (this.checkImage.getVisibility() != i) {
        this.checkImage.setVisibility(i);
      }
      return;
      localThemeInfo = Theme.getCurrentTheme();
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/ui/Cells/ThemeCell.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
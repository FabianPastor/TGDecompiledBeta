package org.telegram.messenger.voip;

public class EncryptionKeyEmojifier
{
  private static final String[] emojis = { "😉", "😍", "😛", "😭", "😱", "😡", "😎", "😴", "😵", "😈", "😬", "😇", "😏", "👮", "👷", "💂", "👶", "👨", "👩", "👴", "👵", "😻", "😽", "🙀", "👺", "🙈", "🙉", "🙊", "💀", "👽", "💩", "🔥", "💥", "💤", "👂", "👀", "👃", "👅", "👄", "👍", "👎", "👌", "👊", "✌", "✋", "👐", "👆", "👇", "👉", "👈", "🙏", "👏", "💪", "🚶", "🏃", "💃", "👫", "👪", "👬", "👭", "💅", "🎩", "👑", "👒", "👟", "👞", "👠", "👕", "👗", "👖", "👙", "👜", "👓", "🎀", "💄", "💛", "💙", "💜", "💚", "💍", "💎", "🐶", "🐺", "🐱", "🐭", "🐹", "🐰", "🐸", "🐯", "🐨", "🐻", "🐷", "🐮", "🐗", "🐴", "🐑", "🐘", "🐼", "🐧", "🐥", "🐔", "🐍", "🐢", "🐛", "🐝", "🐜", "🐞", "🐌", "🐙", "🐚", "🐟", "🐬", "🐋", "🐐", "🐊", "🐫", "🍀", "🌹", "🌻", "🍁", "🌾", "🍄", "🌵", "🌴", "🌳", "🌞", "🌚", "🌙", "🌎", "🌋", "⚡", "☔", "❄", "⛄", "🌀", "🌈", "🌊", "🎓", "🎆", "🎃", "👻", "🎅", "🎄", "🎁", "🎈", "🔮", "🎥", "📷", "💿", "💻", "☎", "📡", "📺", "📻", "🔉", "🔔", "⏳", "⏰", "⌚", "🔒", "🔑", "🔎", "💡", "🔦", "🔌", "🔋", "🚿", "🚽", "🔧", "🔨", "🚪", "🚬", "💣", "🔫", "🔪", "💊", "💉", "💰", "💵", "💳", "✉", "📫", "📦", "📅", "📁", "✂", "📌", "📎", "✒", "✏", "📐", "📚", "🔬", "🔭", "🎨", "🎬", "🎤", "🎧", "🎵", "🎹", "🎻", "🎺", "🎸", "👾", "🎮", "🃏", "🎲", "🎯", "🏈", "🏀", "⚽", "⚾", "🎾", "🎱", "🏉", "🎳", "🏁", "🏇", "🏆", "🏊", "🏄", "☕", "🍼", "🍺", "🍷", "🍴", "🍕", "🍔", "🍟", "🍗", "🍱", "🍚", "🍜", "🍡", "🍳", "🍞", "🍩", "🍦", "🎂", "🍰", "🍪", "🍫", "🍭", "🍯", "🍎", "🍏", "🍊", "🍋", "🍒", "🍇", "🍉", "🍓", "🍑", "🍌", "🍐", "🍍", "🍆", "🍅", "🌽", "🏡", "🏥", "🏦", "⛪", "🏰", "⛺", "🏭", "🗻", "🗽", "🎠", "🎡", "⛲", "🎢", "🚢", "🚤", "⚓", "🚀", "✈", "🚁", "🚂", "🚋", "🚎", "🚌", "🚙", "🚗", "🚕", "🚛", "🚨", "🚔", "🚒", "🚑", "🚲", "🚠", "🚜", "🚦", "⚠", "🚧", "⛽", "🎰", "🗿", "🎪", "🎭", "🇯🇵", "🇰🇷", "🇩🇪", "🇨🇳", "🇺🇸", "🇫🇷", "🇪🇸", "🇮🇹", "🇷🇺", "🇬🇧", "1⃣", "2⃣", "3⃣", "4⃣", "5⃣", "6⃣", "7⃣", "8⃣", "9⃣", "0⃣", "🔟", "❗", "❓", "♥", "♦", "💯", "🔗", "🔱", "🔴", "🔵", "🔶", "🔷" };
  private static final int[] offsets = { 0, 4, 8, 12, 16 };
  
  private static int bytesToInt(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0x7F) << 24 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 3)] & 0xFF;
  }
  
  private static long bytesToLong(byte[] paramArrayOfByte, int paramInt)
  {
    return (paramArrayOfByte[paramInt] & 0x7F) << 56 | (paramArrayOfByte[(paramInt + 1)] & 0xFF) << 48 | (paramArrayOfByte[(paramInt + 2)] & 0xFF) << 40 | (paramArrayOfByte[(paramInt + 3)] & 0xFF) << 32 | (paramArrayOfByte[(paramInt + 4)] & 0xFF) << 24 | (paramArrayOfByte[(paramInt + 5)] & 0xFF) << 16 | (paramArrayOfByte[(paramInt + 6)] & 0xFF) << 8 | paramArrayOfByte[(paramInt + 7)] & 0xFF;
  }
  
  public static String[] emojify(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte.length != 32) {
      throw new IllegalArgumentException("sha256 needs to be exactly 32 bytes");
    }
    String[] arrayOfString = new String[5];
    for (int i = 0; i < 5; i++) {
      arrayOfString[i] = emojis[(bytesToInt(paramArrayOfByte, offsets[i]) % emojis.length)];
    }
    return arrayOfString;
  }
  
  public static String[] emojifyForCall(byte[] paramArrayOfByte)
  {
    String[] arrayOfString = new String[4];
    for (int i = 0; i < 4; i++) {
      arrayOfString[i] = emojis[((int)(bytesToLong(paramArrayOfByte, i * 8) % emojis.length))];
    }
    return arrayOfString;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/voip/EncryptionKeyEmojifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */
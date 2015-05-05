/*   1:    */ package mindhelix.com.myapplication;
/*   2:    */ 
/*   3:    */ import android.util.Log;

import java.util.ArrayList;

/*   4:    */
/*   5:    */ public class SmartConfig20
/*   6:    */ {
/*   7:    */   private ArrayList<Integer> mData;
/*   8:    */   private byte[] mfreeData;
/*   9:    */   private String mSsid;
/*  10:    */   private String mToken;
/*  11:    */   private byte[] mKey;
/*  12:    */   private boolean hasEncryption;
/*  13:    */   
/*  14:    */   public void encodePackets()
/*  15:    */     throws Exception
/*  16:    */   {
/*  17: 32 */     this.mData = new ArrayList();
/*  18: 33 */     int T_START = 1099;
/*  19: 34 */     int T_MID_NO_ENCRYPTION = 1199;
/*  20: 35 */     int T_MID_ENCRYPTION = 1200;
/*  21: 36 */     int T_FREE = 1149;
/*  22:    */     
/*  23: 38 */     this.mData = new ArrayList();
/*  24: 39 */     this.mData.add(Integer.valueOf(1099));
/*  25: 40 */     constructSsid();
/*  26: 41 */     if (this.hasEncryption) {
/*  27: 42 */       this.mData.add(Integer.valueOf(1200));
/*  28:    */     } else {
/*  29: 44 */       this.mData.add(Integer.valueOf(1199));
/*  30:    */     }
/*  31: 46 */     constructKey();
/*  32: 47 */     if (this.mfreeData.length > 1)
/*  33:    */     {
/*  34: 48 */       this.mData.add(Integer.valueOf(1149));
/*  35: 49 */       constructFreeData();
/*  36:    */     }
/*  37:    */   }
/*  38:    */   
/*  39:    */   private void constructSsid()
/*  40:    */     throws Exception
/*  41:    */   {
/*  42: 55 */     int ConstOffset_1 = 1;
/*  43: 56 */     int ConstOffset_2 = 27;
/*  44:    */     
/*  45: 58 */     int ssidLength = this.mSsid.length();
    Log.i("ssidLength",""+ssidLength);
/*  46: 59 */     int ssidL = ssidLength + 1 + 27;
/*  47:    */     
/*  48: 61 */     this.mData.add(Integer.valueOf(ssidL));
/*  49:    */     
/*  50: 63 */     encodeSsidString(this.mSsid);
/*  51:    */   }
/*  52:    */   
/*  53:    */   private void encodeSsidString(String ssid)
/*  54:    */     throws Exception
/*  55:    */   {

                   Log.i("encodeSsidString", ssid);
/*  56: 68 */     int DataOffset = 593;
/*  57: 69 */     byte prevNibble = 0;
/*  58: 70 */     int currentIndex = 0;
/*  59: 71 */     byte[] stringBuffer = new byte[ssid.length()];
/*  60:    */     
/*  61: 73 */     stringBuffer = convertStringToBytes(ssid);
    Log.i("ssid in bytes",""+stringBuffer);
/*  62: 75 */     for (int i = 0; i < ssid.length(); i++)
/*  63:    */     {
/*  64: 76 */       byte currentChar = stringBuffer[i];
/*  65:    */       
/*  66: 78 */       int lowNibble = currentChar & 0xF;
/*  67: 79 */       int highNibble = currentChar >> 4;
/*  68:    */       Log.i("lowNibble",""+lowNibble);
        Log.i("highNibble",""+highNibble);
/*  69: 81 */       this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
/*  70: 82 */       prevNibble = (byte)highNibble;
/*  71: 83 */       this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
/*  72: 84 */       prevNibble = (byte)lowNibble;
/*  73:    */       
/*  74: 86 */       currentIndex &= 0xF;
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   private void constructKey()
/*  79:    */     throws Exception
/*  80:    */   {
/*  81: 93 */     int ConstOffset_1 = 1;
/*  82: 94 */     int ConstOffset_2 = 27;
/*  83:    */     
/*  84: 96 */     int keyLength = this.mKey.length;
/*  85:    */     
/*  86:    */ 
/*  87: 99 */     int keyL = keyLength + 1 + 27;
/*  88:    */     
/*  89:101 */     this.mData.add(Integer.valueOf(keyL));
/*  90:    */     
/*  91:103 */     encodeKeyString(this.mKey);
/*  92:    */   }
/*  93:    */   
/*  94:    */   private void encodeKeyString(byte[] key)
/*  95:    */     throws Exception
/*  96:    */   {
/*  97:108 */     int DataOffset = 593;
/*  98:109 */     byte prevNibble = 0;
/*  99:110 */     int currentIndex = 0;
/* 100:112 */     for (int i = 0; i < key.length; i++)
/* 101:    */     {
/* 102:113 */       int currentChar = intToUint8(key[i]);
/* 103:114 */       int lowNibble = currentChar & 0xF;
/* 104:115 */       int highNibble = currentChar >> 4;
/* 105:    */       
/* 106:117 */       this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
/* 107:118 */       prevNibble = (byte)highNibble;
/* 108:119 */       this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
/* 109:120 */       prevNibble = (byte)lowNibble;
/* 110:    */       
/* 111:122 */       currentIndex &= 0xF;
/* 112:    */     }
/* 113:    */   }
/* 114:    */   
/* 115:    */   private void constructFreeData()
/* 116:    */     throws Exception
/* 117:    */   {
/* 118:128 */     int ConstOffset_1 = 1;
/* 119:129 */     int ConstOffset_2 = 27;
/* 120:    */     
/* 121:131 */     int freeDataLength = this.mfreeData.length;
/* 122:    */     
/* 123:133 */     int freeDataL = freeDataLength + 1 + 27;
/* 124:    */     
/* 125:135 */     this.mData.add(Integer.valueOf(freeDataL));
/* 126:    */     
/* 127:137 */     encodeFreeData(this.mfreeData);
/* 128:    */   }
/* 129:    */   
/* 130:    */   private void encodeFreeData(byte[] freeData)
/* 131:    */     throws Exception
/* 132:    */   {
/* 133:141 */     int DataOffset = 593;
/* 134:142 */     byte prevNibble = 0;
/* 135:143 */     int currentIndex = 0;
/* 136:145 */     for (int i = 0; i < freeData.length; i++)
/* 137:    */     {
/* 138:146 */       int currentChar = intToUint8(freeData[i]);
/* 139:147 */       int lowNibble = currentChar & 0xF;
/* 140:148 */       int highNibble = currentChar >> 4;
/* 141:    */       
/* 142:150 */       this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | highNibble) + 593));
/* 143:151 */       prevNibble = (byte)highNibble;
/* 144:152 */       this.mData.add(Integer.valueOf(((prevNibble ^ currentIndex++) << 4 | lowNibble) + 593));
/* 145:153 */       prevNibble = (byte)lowNibble;
/* 146:    */       
/* 147:155 */       currentIndex &= 0xF;
/* 148:    */     }
/* 149:    */   }
/* 150:    */   
/* 151:    */   private byte[] convertStringToBytes(String string)
/* 152:    */     throws Exception
/* 153:    */   {
/* 154:161 */     return string.getBytes();
/* 155:    */   }
/* 156:    */   
/* 157:    */   private int intToUint8(int number)
/* 158:    */     throws Exception
/* 159:    */   {
/* 160:166 */     return number & 0xFF;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public ArrayList<Integer> getmData()
/* 164:    */     throws Exception
/* 165:    */   {
/* 166:172 */     return this.mData;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String getmSsid()
/* 170:    */     throws Exception
/* 171:    */   {
/* 172:177 */     return this.mSsid;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void setmSsid(String mSsid)
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:182 */     this.mSsid = mSsid;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public byte[] getmKey()
/* 182:    */     throws Exception
/* 183:    */   {
/* 184:187 */     return this.mKey;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setmKey(byte[] mKey)
/* 188:    */     throws Exception
/* 189:    */   {
/* 190:192 */     this.mKey = mKey;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public byte[] getmFreeData()
/* 194:    */     throws Exception
/* 195:    */   {
/* 196:196 */     return this.mfreeData;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setmFreeData(byte[] mFreeData)
/* 200:    */     throws Exception
/* 201:    */   {
/* 202:201 */     this.mfreeData = mFreeData;
/* 203:    */   }
/* 204:    */   
/* 205:    */   public String getmToken()
/* 206:    */     throws Exception
/* 207:    */   {
/* 208:205 */     return this.mToken;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public void setmToken(String mToken)
/* 212:    */     throws Exception
/* 213:    */   {
/* 214:209 */     this.mToken = mToken;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public void setHasEncryption(boolean hasEncryption)
/* 218:    */     throws Exception
/* 219:    */   {
/* 220:213 */     this.hasEncryption = hasEncryption;
/* 221:    */   }
/* 222:    */ }



/* Location:           C:\Users\Jain\Desktop\smartconfiglib.jar(1)\smartconfiglib.jar

 * Qualified Name:     com.integrity_project.smartconfiglib.SmartConfig20

 * JD-Core Version:    0.7.0.1

 */
/*  1:   */ package mindhelix.com.myapplication;
/*  2:   */ 
/*  3:   */

import android.util.Log;

import java.util.ArrayList;

/*  4:   */
/*  5:   */ public class SmartConfigEncode
/*  6:   */ {
/*  7:   */   private ArrayList<Integer> mData;
/*  8:   */   
/*  9:   */   public SmartConfigEncode(String ssid, byte[] key, byte[] freeData, String token, boolean hasEncryption)
/* 10:   */     throws Exception
/* 11:   */   {
/* 12: 9 */     SmartConfig20 sc = new SmartConfig20();
/* 13:   */     
/* 14:11 */     sc.setmSsid(ssid);
/* 15:   */     
/* 16:13 */     sc.setmKey(key);
/* 17:   */     
/* 18:15 */     sc.setmFreeData(freeData);
/* 19:   */     
/* 20:17 */     sc.setmToken(token);
/* 21:   */     
/* 22:19 */     sc.setHasEncryption(hasEncryption);
/* 23:   */     
/* 24:21 */     sc.encodePackets();
/* 25:   */     
/* 26:23 */     this.mData = sc.getmData();
    Log.i("SmartConfigEncode", "mData" + this.mData);
/* 27:   */   }
/* 28:   */   
/* 29:   */   public ArrayList<Integer> getmData()
/* 30:   */     throws Exception
/* 31:   */   {
/* 32:27 */     return this.mData;
/* 33:   */   }
/* 34:   */ }



/* Location:           C:\Users\Jain\Desktop\smartconfiglib.jar(1)\smartconfiglib.jar

 * Qualified Name:     com.integrity_project.smartconfiglib.SmartConfigEncode

 * JD-Core Version:    0.7.0.1

 */
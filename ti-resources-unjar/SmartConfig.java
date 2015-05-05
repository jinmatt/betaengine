package mindhelix.com.myapplication;

import android.util.Log;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class SmartConfig{

    private static final int defaultNumberOfSetups = 4;
    private static final int defaultNumberOfSyncs = 10;
    private static final java.lang.String defaultSyncLString = "abc";
    private static final java.lang.String defaultSyncHString = "abcdefghijklmnopqrstuvw";
    private static final java.lang.String defaultmDnsAckString = "CC3000";
    private static final int mDnsListenPort = 5353;
    private static final int LOCAL_PORT = 15000;
    private static final int FIVE_MINUTE_TIMEOUT_MS = 300000;
    private boolean stopSending;
    private java.lang.String ip;
    private java.lang.String ssid;
    private byte group;
    private byte[] freeData;
    private java.lang.String key;
    private java.lang.String token;
    private java.lang.String mDnsAckString;
    private int nSetup;
    private java.lang.String syncLString;
    private java.lang.String syncHString;
    private byte[] encryptionKey;
    private SmartConfigEncode ftcData;
    java.net.InetSocketAddress sockAddr;
    private java.net.MulticastSocket listenSocket;
    int localPort;
    int listenPort;
    int waitForAckSocketTimeout;
    private SmartConfigListener m_listener;
    java.lang.Thread sendingThread;
    java.lang.Thread ackWaitThread;
    private boolean isListenSocketGracefullyClosed;
   boolean hasEncryption = false;
    public SmartConfig(SmartConfigListener listener, byte[] FreeData,
                       java.lang.String Key, byte[] EncryptionKey, java.lang.String Ip,
                       java.lang.String Ssid, byte Group, java.lang.String Token) throws java.lang.Exception {


/*  84:164 */     m_listener = listener;
/*  85:    */
/*  86:166 */     this.freeData = FreeData;
/*  87:168 */     if ((EncryptionKey != null) &&
/*  88:169 */       (EncryptionKey.length != 0) && (EncryptionKey.length != 16)) {
/*  89:171 */       throw new Exception("Encryption key must have 16 characters!");
/*  90:    */     }
/*  91:174 */     if (Key.length() > 32) {
/*  92:175 */       throw new Exception("Password is too long! Maximum length is 32 characters.");
/*  93:    */     }
/*  94:178 */     if (Ssid.length() > 32) {
/*  95:179 */       throw new Exception("Network name (SSID) is too long! Maximum length is 32 characters.");
/*  96:    */     }
/*  97:182 */     if (Token.length() > 32) {
/*  98:183 */       throw new Exception("Token is too long! Maximum length is 32 characters.");
/*  99:    */     }
/* 100:186 */     this.group = Group;
/* 101:    */
/* 102:    */
/* 103:189 */     this.stopSending = true;
/* 104:    */
/* 105:191 */     this.isListenSocketGracefullyClosed = false;
/* 106:    */
/* 107:193 */     this.listenSocket = null;
/* 108:    */
/* 109:195 */     this.freeData = FreeData;
/* 110:    */
        Log.i("freedata",""+freeData);
/* 111:197 */     this.ip = Ip;
/* 112:198 */     this.ssid = Ssid;
/* 113:199 */     this.key = Key;
/* 114:200 */     this.token = Token;
/* 115:201 */     this.nSetup = 0;
/* 116:    */
/* 117:    */
/* 118:204 */     this.syncLString = "";
/* 119:205 */     this.syncHString = "";
/* 120:    */
/* 121:207 */     this.encryptionKey = EncryptionKey;
/* 122:    */
/* 123:    */
/* 124:    */
/* 125:211 */     this.mDnsAckString = "";
/* 126:    */
/* 127:213 */     createBroadcastUDPSocket(0);
/* 128:    */
/* 129:215 */     this.localPort = LOCAL_PORT;
/* 130:216 */     this.listenPort = 5353;
/* 131:217 */     this.waitForAckSocketTimeout = FIVE_MINUTE_TIMEOUT_MS;
/* 132:    */
/* 133:219 */     this.sockAddr = new InetSocketAddress(this.ip, this.localPort);
/* 134:    */
/* 135:    */
/* 136:    */
/* 137:    */
/* 138:    */
/* 139:    */
/* 140:    */
/* 141:    */
/* 142:    */
/* 143:    */
/* 144:    */
/* 145:    */
/* 146:    */
/* 147:233 */     byte[] keyData = new byte[this.key.length()];
/* 148:234 */     keyData = this.key.getBytes("UTF-8");
/* 149:235 */     if (this.encryptionKey != null)
/* 150:    */     {
/* 151:236 */       keyData = encryptData(keyData);
/* 152:237 */       hasEncryption = true;
/* 153:    */     }
/* 154:240 */     this.ftcData = new SmartConfigEncode(this.ssid, keyData, this.freeData, this.token, hasEncryption);
        Log.i("ftcdata",""+ftcData);
        transmitSettings();
/* 155:    */   }
/* 156:    */
/* 157:    */   private void createBroadcastUDPSocket(int port)
/* 158:    */     throws Exception
/* 159:    */   {
/* 160:249 */     InetAddress wildcardAddr = null;
/* 161:250 */     InetSocketAddress localAddr = null;
/* 162:    */
/* 163:252 */     localAddr = new InetSocketAddress(wildcardAddr, port);
/* 164:    */
/* 165:    */
/* 166:255 */     this.listenSocket = new MulticastSocket(null);
/* 167:    */
/* 168:    */
/* 169:258 */     this.listenSocket.setReuseAddress(true);
/* 170:    */
/* 171:    */
/* 172:261 */     this.listenSocket.bind(localAddr);
/* 173:    */
/* 174:    */
/* 175:264 */     this.listenSocket.setTimeToLive(255);
/* 176:    */
/* 177:    */
/* 178:267 */     this.listenSocket.joinGroup(InetAddress.getByName("224.0.0.251"));
/* 179:    */
/* 180:    */
/* 181:270 */     this.listenSocket.setBroadcast(true);
/* 182:    */   }
/* 183:    */
/* 184:    */   private void send()
/* 185:    */     throws Exception
/* 186:    */   {
/* 187:279 */     int numberOfPackets = this.ftcData.getmData().size();
/* 188:    */
/* 189:281 */     ArrayList<Integer> packets = new ArrayList();
/* 190:282 */     byte[] ftcBuffer = new byte[1600];
/* 191:283 */     byte[] syncLBuffer = this.syncLString.getBytes();
/* 192:284 */     byte[] syncHBuffer = this.syncHString.getBytes();
/* 193:    */
/* 194:286 */     ftcBuffer = makePaddedByteArray(ftcBuffer.length);
/* 195:    */
/* 196:    */
/* 197:289 */     packets = this.ftcData.getmData();
/* 198:291 */     while (!this.stopSending)
/* 199:    */     {
/* 200:295 */       for (int i = 0; i < this.nSetup; i++) {
/* 201:296 */         for (int j = 0; j < numberOfPackets; j++)
/* 202:    */         {
/* 203:297 */           int packsize = ((Integer)packets.get(j)).intValue();
/* 204:311 */           if (i % 2 == 0) {
/* 205:311 */             sendData(new DatagramPacket(syncLBuffer, syncLBuffer.length, this.sockAddr), this.localPort);
/* 206:    */           } else {
/* 207:312 */             sendData(new DatagramPacket(syncHBuffer, syncHBuffer.length - this.group, this.sockAddr), this.localPort);
/* 208:    */           }
/* 209:314 */           sendData(new DatagramPacket(ftcBuffer, packsize, this.sockAddr), this.localPort);
/* 210:    */         }
/* 211:    */       }
/* 212:318 */       Thread.sleep(100L);
/* 213:    */     }
/* 214:    */   }
/* 215:    */
/* 216:    */   private void sendData(DatagramPacket packet, int localSendingPort)
/* 217:    */     throws Exception
/* 218:    */   {
/* 219:326 */     DatagramSocket sock = null;
/* 220:327 */     sock = new DatagramSocket(localSendingPort);
/* 221:    */
/* 222:329 */     sock.send(packet);
/* 223:    */
/* 224:331 */     sock.close();
/* 225:    */   }
/* 226:    */
/* 227:    */   private class NotifyThread
/* 228:    */     implements Runnable
/* 229:    */   {
/* 230:    */     private SmartConfigListener m_listener;
/* 231:    */     private SmartConfigListener.SmtCfgEvent t_event;
/* 232:    */     private Exception t_ex;
/* 233:    */
/* 234:    */     public NotifyThread(SmartConfigListener listener, SmartConfigListener.SmtCfgEvent event)
/* 235:    */     {
/* 236:337 */       this.m_listener = listener;
/* 237:338 */       this.t_event = event;
/* 238:339 */       this.t_ex = null;
/* 239:340 */       Thread t = new Thread(this);
/* 240:341 */       t.start();
/* 241:    */     }
/* 242:    */
/* 243:    */     public NotifyThread(SmartConfigListener listener, Exception ex)
/* 244:    */     {
/* 245:345 */       this.m_listener = listener;
/* 246:346 */       this.t_event = SmartConfigListener.SmtCfgEvent.FTC_ERROR;
/* 247:347 */       this.t_ex = ex;
/* 248:348 */       Thread t = new Thread(this);
/* 249:349 */       t.start();
/* 250:    */     }
/* 251:    */
/* 252:    */     public void run()
/* 253:    */     {
/* 254:    */       try
/* 255:    */       {
/* 256:357 */         if (this.m_listener != null) {
/* 257:358 */           this.m_listener.onSmartConfigEvent(this.t_event, this.t_ex);
/* 258:    */         }
/* 259:    */       }
/* 260:    */       catch (Exception localException) {}
/* 261:    */     }
/* 262:    */   }
/* 263:    */
/* 264:    */   public void transmitSettings()
/* 265:    */     throws Exception
/* 266:    */   {
/* 267:370 */     this.stopSending = false;
/* 268:371 */     this.sendingThread = new Thread(new Runnable()
/* 269:    */     {
/* 270:    */       public void run()
/* 271:    */       {
/* 272:    */         try
/* 273:    */         {
/* 274:374 */           SmartConfig.this.send();
/* 275:    */         }
/* 276:    */         catch (Exception e)
/* 277:    */         {
/* 278:376 */           //new SmartConfig.NotifyThread(SmartConfig.this, SmartConfig.this.m_listener, e);
/* 279:    */         }
/* 280:    */       }
/* 281:380 */     });
/* 282:381 */     this.sendingThread.start();
/* 283:    */
/* 284:383 */     this.ackWaitThread = new Thread(new Runnable()
/* 285:    */     {
/* 286:    */       public void run()
/* 287:    */       {
/* 288:    */         try
/* 289:    */         {
/* 290:386 */           SmartConfig.this.waitForAck();
/* 291:    */         }
/* 292:    */         catch (Exception e)
/* 293:    */         {
/* 294:388 */           //new SmartConfig.NotifyThread(SmartConfig.this, SmartConfig.this.m_listener, e);
/* 295:    */         }
/* 296:    */       }
/* 297:392 */     });
/* 298:393 */     this.ackWaitThread.start();
/* 299:    */   }
/* 300:    */
/* 301:    */   public void stopTransmitting()
/* 302:    */     throws Exception
/* 303:    */   {
/* 304:403 */     this.isListenSocketGracefullyClosed = true;
/* 305:404 */     this.listenSocket.close();
/* 306:    */
/* 307:406 */     this.stopSending = true;
/* 308:407 */     if (Thread.currentThread() != this.sendingThread) {
/* 309:408 */       this.sendingThread.join();
/* 310:    */     }
/* 311:409 */     if (Thread.currentThread() != this.ackWaitThread) {
/* 312:410 */       this.ackWaitThread.join();
/* 313:    */     }
/* 314:    */   }
/* 315:    */
/* 316:    */   private void waitForAck()
/* 317:    */     throws Exception
/* 318:    */   {
/* 319:419 */     int RECV_BUFFER_LENGTH = 16384;
/* 320:    */
/* 321:421 */     byte[] recvBuffer = new byte[16384];
/* 322:    */     Exception ee;
/* 323:423 */     DatagramPacket listenPacket = new DatagramPacket(recvBuffer, 16384);
/* 324:424 */     int timeout = this.waitForAckSocketTimeout;
/* 325:    */     label97:
/* 326:426 */     while (!this.stopSending)
/* 327:    */     {
/* 328:428 */       long start = System.nanoTime();
/* 329:429 */       this.listenSocket.setSoTimeout(timeout);
/* 330:    */       try
/* 331:    */       {
/* 332:431 */         this.listenSocket.receive(listenPacket);
/* 333:    */       }
/* 334:    */       catch (InterruptedIOException e)
/* 335:    */       {
/* 336:433 */         if (this.isListenSocketGracefullyClosed) {
/* 337:    */           break;
/* 338:    */         }
/* 339:435 */         new NotifyThread(this.m_listener, SmartConfigListener.SmtCfgEvent.FTC_TIMEOUT);
/* 340:436 */         break;
/* 341:    */       }
/* 342:    */       catch (Exception e)
/* 343:    */       {
/* 344:438 */         if (!this.isListenSocketGracefullyClosed) {
/* 345:    */           break label97;
/* 346:    */         }
/* 347:    */       }
/* 348:439 */
/* 349:440 */
/* 350:443 */       if (parseMDns(listenPacket.getData()))
/* 351:    */       {
/* 352:444 */         stopTransmitting();
/* 353:445 */         new NotifyThread(this.m_listener, SmartConfigListener.SmtCfgEvent.FTC_SUCCESS);
/* 354:446 */         break;
/* 355:    */       }
/* 356:448 */       timeout = (int)(timeout - (System.nanoTime() - start) / 1000000L);
/* 357:449 */       if (timeout <= 0)
/* 358:    */       {
/* 359:451 */         new NotifyThread(this.m_listener, SmartConfigListener.SmtCfgEvent.FTC_TIMEOUT);
/* 360:452 */         break;
/* 361:    */       }
/* 362:    */     }
/* 363:    */   }
/* 364:    */
/* 365:    */   private boolean parseMDns(byte[] data)
/* 366:    */     throws Exception
/* 367:    */   {
/* 368:463 */     int MDNS_HEADER_SIZE = 12;
/* 369:464 */     int MDNS_HEADER_SIZE2 = 10;
/* 370:    */
/* 371:    */
/* 372:467 */     int pos = 12;
/* 373:470 */     if (data.length < pos + 1) {
/* 374:471 */       return false;
/* 375:    */     }
/* 376:472 */     int len = data[pos] & 0xFF;
/* 377:473 */     pos++;
/* 378:475 */     while (len > 0)
/* 379:    */     {
/* 380:478 */       if (data.length < pos + len) {
/* 381:479 */         return false;
/* 382:    */       }
/* 383:481 */       pos += len;
/* 384:484 */       if (data.length < pos + 1) {
/* 385:485 */         return false;
/* 386:    */       }
/* 387:486 */       len = data[pos] & 0xFF;
/* 388:487 */       pos++;
/* 389:    */     }
/* 390:491 */     pos += 10;
/* 391:494 */     if (data.length < pos + 1) {
/* 392:495 */       return false;
/* 393:    */     }
/* 394:496 */     len = data[pos] & 0xFF;
/* 395:497 */     pos++;
/* 396:500 */     if (data.length < pos + len) {
/* 397:501 */       return false;
/* 398:    */     }
/* 399:502 */     String name = new String(data, pos, len);
/* 400:    */       Log.i("parsemdns",name);
/* 401:    */
/* 402:505 */     boolean bRet = name.equals(this.mDnsAckString);
/* 403:506 */     return bRet;
/* 404:    */   }
/* 405:    */
/* 406:    */   private byte[] encryptData(byte[] data)
/* 407:    */     throws Exception
/* 408:    */   {
/* 409:514 */     byte[] InitializationVector1 = { 1, 3, 25, -46, -79, 81, -14, 9, 112, 97, -61, -53, 48, 125, 0, 1 };
/* 410:515 */     byte[] InitializationVector2 = { 1, 3, 25, -46, -79, 81, -14, 9, 112, 97, -61, -53, 48, 125, 0, 2 };
/* 411:517 */     if ((this.encryptionKey == null) || (this.encryptionKey.length == 0)) {
/* 412:518 */       return data;
/* 413:    */     }
/* 414:520 */     int ZERO_OFFSET = 0;
/* 415:521 */     int AES_LENGTH = 16;
/* 416:522 */     int DATA_LENGTH = 32;
/* 417:    */
/* 418:524 */     Cipher c = null;
/* 419:525 */     byte[] encryptedData = null;
/* 420:526 */     byte[] encryptedData1 = null;
/* 421:527 */     byte[] encryptedData2 = null;
/* 422:528 */     byte[] paddedData = new byte[32];
/* 423:529 */     byte[] paddedData1 = new byte[16];
/* 424:530 */     byte[] paddedData2 = new byte[16];
/* 425:531 */     byte[] aesKey = new byte[16];
/* 426:533 */     for (int x = 0; x < 16; x++) {
/* 427:534 */       if (x < this.encryptionKey.length) {
/* 428:535 */         aesKey[x] = this.encryptionKey[x];
/* 429:    */       } else {
/* 430:538 */         aesKey[x] = 0;
/* 431:    */       }
/* 432:    */     }
/* 433:543 */     System.arraycopy(this.encryptionKey, 0, aesKey, 0, 16);
/* 434:    */
/* 435:    */
/* 436:546 */     int dataOffset = 0;
/* 437:547 */     if (data.length < 32)
/* 438:    */     {
/* 439:549 */       paddedData[dataOffset] = ((byte)data.length);
/* 440:550 */       dataOffset++;
/* 441:    */     }
/* 442:554 */     System.arraycopy(data, 0, paddedData, dataOffset, data.length);
/* 443:555 */     dataOffset += data.length;
/* 444:558 */     while (dataOffset < 32)
/* 445:    */     {
/* 446:560 */       paddedData[dataOffset] = 0;
/* 447:561 */       dataOffset++;
/* 448:    */     }
/* 449:564 */     for (int i = 0; i < 16; i++)
/* 450:    */     {
/* 451:565 */       paddedData1[i] = paddedData[i];
/* 452:566 */       paddedData2[i] = paddedData[(i + 16)];
/* 453:    */     }
/* 454:569 */     c = Cipher.getInstance("AES/OFB/NoPadding");
/* 455:    */
/* 456:571 */     SecretKeySpec k = new SecretKeySpec(aesKey, "AES");
/* 457:    */
/* 458:573 */     encryptedData = new byte[32];
/* 459:    */
/* 460:575 */     IvParameterSpec ivspec1 = new IvParameterSpec(InitializationVector1);
/* 461:576 */     IvParameterSpec ivspec2 = new IvParameterSpec(InitializationVector2);
/* 462:    */
/* 463:578 */     c.init(1, k, ivspec1);
/* 464:579 */     encryptedData1 = c.doFinal(paddedData1);
/* 465:    */
/* 466:581 */     c.init(1, k, ivspec2);
/* 467:582 */     encryptedData2 = c.doFinal(paddedData2);
/* 468:    */
/* 469:584 */     System.arraycopy(encryptedData1, 0, encryptedData, 0, 16);
/* 470:585 */     System.arraycopy(encryptedData2, 0, encryptedData, 16, 16);
/* 471:    */
/* 472:587 */     return encryptedData;
/* 473:    */   }
/* 474:    */
/* 475:    */   private byte[] makePaddedByteArray(int length)
/* 476:    */     throws Exception
/* 477:    */   {
/* 478:594 */     byte[] paddedArray = new byte[length];
/* 479:596 */     for (int x = 0; x < length; x++) {
/* 480:597 */       paddedArray[x] = ((byte)"1".charAt(0));
/* 481:    */     }
/* 482:600 */     return paddedArray;
/* 483:    */   }
/* 484:    */ }



/* Location:           C:\Users\Jain\Desktop\smartconfiglib.jar(1)\smartconfiglib.jar

 * Qualified Name:     com.integrity_project.smartconfiglib.SmartConfig

 * JD-Core Version:    0.7.0.1

 */
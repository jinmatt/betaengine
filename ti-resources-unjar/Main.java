package mindhelix.com.myapplication;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by hari on 30/04/15.
 */
public class Main extends Activity{

    byte[] freeData;
    static SmartConfig smartConfig;
    static SmartConfigListener smartConfigListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        final String passwordKey = "LetMeIn";
        final byte[] paddedEncryptionKey = null;
        final String SSID = "rara";
        final String gateway = NetworkUtil.getGateway(getApplicationContext());
        Log.i("gateway",gateway);
        String deviceName = "SOC105";
        if (deviceName.length() > 0) { // device name isn't empty
            byte[] freeDataChars = new byte[deviceName.length() + 2];
            freeDataChars[0] = 0x03;
            freeDataChars[1] = (byte) deviceName.length();
            for (int i=0; i<deviceName.length(); i++) {
                freeDataChars[i+2] = (byte) deviceName.charAt(i);
            }
            freeData = freeDataChars;
        } else {
            freeData = new byte[1];
            freeData[0] = 0x03;
        }

        smartConfig = null;
        smartConfigListener = new SmartConfigListener() {
            @Override
            public void onSmartConfigEvent(SmtCfgEvent event, Exception e) {}
        };
        try{
        smartConfig = new SmartConfig(smartConfigListener, freeData, passwordKey, paddedEncryptionKey, gateway, SSID, (byte) 0, deviceName);}
        catch (Exception e){
            e.printStackTrace();
        }
    }






}

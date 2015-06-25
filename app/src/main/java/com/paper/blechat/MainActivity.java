package com.paper.blechat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends Activity {
    public static  final String TAG = "hshs";

    @InjectView(R.id.btn1)
    Button btn1;
    @InjectView(R.id.btn2)
    Button btn2;
    private BluetoothGattCharacteristic character;
    private BluetoothGattService service;
    private BluetoothManager manager;
    private BluetoothAdapter bleAdapter;
    private BluetoothLeAdvertiser advertiser;
    private String serviceUUID;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        mContext = this;
        serviceUUID =  UUID.randomUUID().toString();
    }

    @OnClick(R.id.btn1)
    public void onClickBtn1(){
        createGattServer();
    }


    public void createGattServer(){
//        character = new BluetoothGattCharacteristic(
//                UUID.fromString(serviceUUID),
//                BluetoothGattCharacteristic.PROPERTY_NOTIFY,
//                BluetoothGattCharacteristic.PERMISSION_READ);
//        service = new BluetoothGattService(UUID.fromString(serviceUUID),
//                BluetoothGattService.SERVICE_TYPE_PRIMARY);
//        service.addCharacteristic(character);
        manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bleAdapter = manager.getAdapter();
//        BluetoothGattServer server = manager.openGattServer(this,
//                new BluetoothGattServerCallback(){
//
//                });
//        server.addService(service);
        advertiser =bleAdapter.getBluetoothLeAdvertiser();
        if (advertiser == null){
            getActionBar().setTitle("您的设备不支持ble周边");
        }else {
            advertiser.startAdvertising(createAdvSettings(true,5000),createAdvertiseData(),mAdvertiseCallback);
        }

    }

    /** create AdvertiseSettings */
    public static AdvertiseSettings createAdvSettings(boolean connectable, int timeoutMillis) {
        AdvertiseSettings.Builder mSettingsbuilder = new AdvertiseSettings.Builder();
        mSettingsbuilder.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);
        mSettingsbuilder.setConnectable(connectable);
        mSettingsbuilder.setTimeout(timeoutMillis);
        mSettingsbuilder.setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_HIGH);
        AdvertiseSettings mAdvertiseSettings = mSettingsbuilder.build();
        if(mAdvertiseSettings == null){

                Log.e(TAG, "mAdvertiseSettings == null");

        }
        return mAdvertiseSettings;
    }

    public AdvertiseData createAdvertiseData(){
        AdvertiseData.Builder    mDataBuilder = new AdvertiseData.Builder();
        mDataBuilder.addServiceUuid(ParcelUuid.fromString(serviceUUID));
        AdvertiseData mAdvertiseData = mDataBuilder.build();
        if(mAdvertiseData==null){
                Log.e(TAG,"mAdvertiseSettings == null");
        }

        return mAdvertiseData;
    }
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            super.onStartSuccess(settingsInEffect);
            if (settingsInEffect != null) {
                Log.d(TAG, "onStartSuccess TxPowerLv=" + settingsInEffect.getTxPowerLevel()	 + " mode=" + settingsInEffect.getMode()
                        + " timeout=" + settingsInEffect.getTimeout());
            } else {
                Log.e(TAG, "onStartSuccess, settingInEffect is null");
            }
            Log.e(TAG,"onStartSuccess settingsInEffect" + settingsInEffect);

        }

        boolean D = true;

        @Override
        public void onStartFailure(int errorCode) {
            super.onStartFailure(errorCode);
            if(D) 	Log.e(TAG,"onStartFailure errorCode" + errorCode);

            if(errorCode == ADVERTISE_FAILED_DATA_TOO_LARGE){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_data_too_large, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Failed to start advertising as the advertise data to be broadcasted is larger than 31 bytes.");
                }
            }else if(errorCode == ADVERTISE_FAILED_TOO_MANY_ADVERTISERS){
                if(D){
                    Log.e(TAG, "Failed to start advertising because no advertising instance is available.");
                }
            }else if(errorCode == ADVERTISE_FAILED_ALREADY_STARTED){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_already_started, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Failed to start advertising as the advertising is already started");
                }
            }else if(errorCode == ADVERTISE_FAILED_INTERNAL_ERROR){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_internal_error, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"Operation failed due to an internal error");
                }
            }else if(errorCode == ADVERTISE_FAILED_FEATURE_UNSUPPORTED){
                if(D){
                    Toast.makeText(mContext, R.string.advertise_failed_feature_unsupported, Toast.LENGTH_LONG).show();
                    Log.e(TAG,"This feature is not supported on this platform");
                }
            }
        }
    };

    private void stopAdvertise() {
        if (advertiser != null) {
            advertiser.stopAdvertising(mAdvertiseCallback);
            advertiser = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopAdvertise();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

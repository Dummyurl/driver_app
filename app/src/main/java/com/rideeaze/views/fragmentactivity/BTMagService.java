package com.rideeaze.views.fragmentactivity;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

import com.idtech.bluetooth.IDTechOpenHelper;
import com.idtech.bluetooth.OnReceiveListener;
import com.util.Const;


public class BTMagService extends Service implements OnReceiveListener{

	public static boolean isBTMagServiceAlived=false;
	IDTechOpenHelper mIDTechHelper = null;
	public BluetoothAdapter mBluetoothAdapter = null;
	Timer sendAliveSignalTimer;
	
	public static boolean bIsSwiped=false;
	public static boolean bIsConnected=false;
	
	ConnectBTMagReceiver receiver;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		
		mIDTechHelper = new IDTechOpenHelper(this);
		mIDTechHelper.setOnReceiveListener(this);
		ConnectBTMag();
		
		receiver=new ConnectBTMagReceiver();
		registerReceiver(receiver, new IntentFilter(Const.CONNECT_BT_MAG_ACTION));
		
		sendAliveSignalTimer=new Timer();
		TimerTask task=new TimerTask() {
			
			@Override
			public void run()  {
				// TODO Auto-generated method stub
				mIDTechHelper.isAvailable();
				byte[] bte = new byte[] { 1,1};
				mIDTechHelper.write(bte);
			}
		};
		
		sendAliveSignalTimer.schedule(task, 0, 1000);
		
		return super.onStartCommand(intent, flags, startId);
	}
	
	
	private void ConnectBTMag(){
		
		if(EnableBluetooth())
		{
			Set<BluetoothDevice> pairedDevices=mBluetoothAdapter.getBondedDevices();
			
			 for (BluetoothDevice device : pairedDevices)
			 {
				 if(device.getName().contains("ID_TECH"))
				 {
					 mIDTechHelper.connect(device);
					 mIDTechHelper.setDefault();
					 
					 Intent intent=new Intent(Const.BT_MAG_CONNECTING_ACTION);
					 sendBroadcast(intent);
					 
					 return;
				 }
			 }
			
		}
		
		
		
	}

	
	public boolean EnableBluetooth()
    {
	      this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	      if (this.mBluetoothAdapter != null)
	      {
	        if (this.mBluetoothAdapter.isEnabled()) {
	          return true;
	        }
	        this.mBluetoothAdapter.enable();
	
	        if (!this.mBluetoothAdapter.isEnabled()) {
	          
	          Toast.makeText(this, "Bluetooth is turned off", Toast.LENGTH_LONG).show();
	          return false;
	        }
	      }
	      
	      else {
	    	  Toast.makeText(this, "Bluetooth is turned off", Toast.LENGTH_LONG).show();
	    	  return false;
	      }
	      
	      return false;
    }
	
	
	@Override
	public void onConnected() {
		// TODO Auto-generated method stub
		isBTMagServiceAlived=true;
		
		Intent intent=new Intent(Const.BT_MAG_CONNECTED_ACTION);
		sendBroadcast(intent);
	}

	@Override
	public void onConnectedError(int arg0, String arg1) {
		// TODO Auto-generated method stub
		Intent intent=new Intent(Const.BT_MAG_CONNECT_FAILURE_ACTION);
		intent.putExtra("error", arg1);
		sendBroadcast(intent);
	}

	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		
		unregisterReceiver(receiver);
		
		super.onDestroy();
	}
	
	
	@Override
	public void onConnecting() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(Const.BT_MAG_DISCONNECTED_ACTION);
		sendBroadcast(intent);
	}

	@Override
	public void onReceivedData(int arg0, byte[] arg1) {
		// TODO Auto-generated method stub
		String data=new String(arg1);
		
		Intent intent=new Intent(Const.BT_MAG_RECEIVED_ACTION);
		intent.putExtra("data", data);
		sendBroadcast(intent);
	}

	@Override
	public void onReceivedFailed(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceivedSuccess(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	private class ConnectBTMagReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			if(intent.getAction().equals(Const.CONNECT_BT_MAG_ACTION))
			{
				ConnectBTMag();
			}
			
		}
		
	}

}

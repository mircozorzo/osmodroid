package com.OsMoDroid;

import java.text.SimpleDateFormat;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class DeviceChatFragment extends SherlockFragment implements ResultsListener {
	ListView lv2;
	Button sendButton;
	EditText input;
	 int deviceU=-1;
	final private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
	
	Device getDeviceByU (int u){
		for (Device dev : LocalService.deviceList){
			if (dev.u==u){
				return dev;
			}
		}
		
		return null;
		
	}
	String getMyApp (){
		for (Device dev:LocalService.deviceList ){
			if (dev.u==Integer.parseInt(LocalService.settings.getString("device", "-1"))){
				return dev.app;
			}
		}
			return "0";
	}
	
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	
	private GPSLocalServiceClient globalActivity;
	
	
	 @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setHasOptionsMenu(true);
         setRetainInstance(true);
         super.onCreate(savedInstanceState);
     }
	
	@Override
	public void onAttach(Activity activity) {
		globalActivity = (GPSLocalServiceClient)activity;
		super.onAttach(activity);
	}
	@Override
	public void onDetach() {
		LocalService.chatmessagelist.clear();
		   
		 LocalService.currentDevice=null;
		super.onDetach();
	}
	@Override
	public void onResume() {
		globalActivity.devicesTab.setText(getString(R.string.chatwith)+getDeviceByU(deviceU).name);
		super.onResume();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		globalActivity = (GPSLocalServiceClient) getSherlockActivity();
		super.onActivityCreated(savedInstanceState);
	}
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		
		MenuItem refresh = menu.add(0, 2, 0, R.string.refresh);
		refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		refresh.setIcon(android.R.drawable.ic_menu_rotate);
		super.onCreateOptionsMenu(menu, inflater);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onPrepareOptionsMenu(com.actionbarsherlock.view.Menu)
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		super.onPrepareOptionsMenu(menu);
	}

	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onOptionsItemSelected(com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId()==2){
			netutil.newapicommand((ResultsListener)DeviceChatFragment.this,globalActivity, "om_device_message_get:"+globalActivity.settings.getString("device", "")+","+getDeviceByU(deviceU).u);
		}
		
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.chat, container, false);  
		 
		Bundle bundle = getArguments();
		   if(bundle != null){
		      deviceU = bundle.getInt("deviceU", -1);
		   
		   }
		    
		   //LocalService.currentchanneldeviceList= LocalService.channelList.get(getIntent().getIntExtra("CHANNELPOS", -1)).deviceList;
		   
		   LocalService.currentDevice = getDeviceByU(deviceU);
		    
	LocalService.chatmessagesAdapter = new DeviceChatAdapter(globalActivity.getApplicationContext(), R.layout.devicechatitem, LocalService.chatmessagelist);

		    lv2 = (ListView) view.findViewById(R.id.chatmessages);

	LocalService.chatmessagesAdapter.registerDataSetObserver(new DataSetObserver() {
	    @Override
	    public void onChanged() {
	        super.onChanged();
	        lv2.setSelection(LocalService.chatmessagesAdapter.getCount());    
	    }
	});
		    input =(EditText) view.findViewById(R.id.chateditText);
		    input.requestFocus();
		    
	sendButton= (Button) view.findViewById(R.id.chatsendButton);
	sendButton.setOnClickListener(new OnClickListener() {

		public void onClick(View v) {

			if (!(input.getText().toString().equals(""))) {

				JSONObject postjson = new JSONObject();
			try {

				postjson.put("from", globalActivity.settings.getString("device", ""));
				postjson.put("to", Integer.toString((getDeviceByU(deviceU)).u));
				postjson.put("text", input.getText().toString());
				netutil.newapicommand((ResultsListener) DeviceChatFragment.this, "om_device_message_send","json="+postjson.toString());

				} 
			catch (JSONException e) {

					// TODO Auto-generated catch block

					e.printStackTrace();

				}
	input.setText("");
			}


		}});

		      
		       lv2.setAdapter(LocalService.chatmessagesAdapter);
		       if (LocalService.chatmessagesAdapter!=null) {LocalService.chatmessagesAdapter.notifyDataSetChanged();}
		       Log.d(this.getClass().getSimpleName(),"getSherlockActivity:"+getSherlockActivity()+" deviceU="+deviceU+" getdevicebyU="+getDeviceByU(deviceU).u);
		    netutil.newapicommand((ResultsListener)DeviceChatFragment.this,getSherlockActivity(), "om_device_message_get:"+OsMoDroid.settings.getString("device", "")+","+getDeviceByU(deviceU).u);

		


		
		return view;

	}
	
	static public void getDevices(ResultsListener listener){

		netutil.newapicommand(listener, "om_device");



	}


	@Override
	public void onResultsSucceeded(APIComResult result) {

		Log.d(this.getClass().getSimpleName(),"OnResultListener Command:"+result.Command+",Jo="+result.Jo);
		if (!(result.Jo==null)  ) {

			Toast.makeText(getActivity(),result.Jo.optString("state")+" "+ result.Jo.optString("error_description"),5).show();
			
		
		
		
		if (result.Jo.has("om_device_message_get:"+globalActivity.settings.getString("device", "")+","+getDeviceByU(deviceU).u)){
			
			LocalService.chatmessagelist.clear();

			// {"im_get_dialog:173,176":{"33374":{"u":"33374","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0430\u043f\u0440\u0430\u043f\u0440\u0430\u043f\u0440","time":"2013-03-14 21:41:38","readed":"2013-03-15 00:00:04"},"33375":{"u":"33375","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0438\u043c\u0442\u043c\u0438\u0442\u043c\u0438\u0442","time":"2013-03-14 21:41:58","readed":"2013-03-15 00:00:04"},"33376":{"u":"33376","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u044f","time":"2013-03-14 21:42:13","readed":"2013-03-15 00:00:04"},"33377":{"u":"33377","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0430\u043f\u0440\u0430\u043f\u0440\u0430\u043f\u0440","time":"2013-03-14 21:52:46","readed":"2013-03-15 00:00:04"},"33378":{"u":"33378","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"zxcvxzcv","time":"2013-03-14 22:29:15","readed":"2013-03-15 00:00:04"},"33379":{"u":"33379","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"xcvbxcvbxcvbcxvb","time":"2013-03-14 22:32:44","readed":"2013-03-15 00:00:04"},"33385":{"u":"33385","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"bnbvnbvnvbn","time":"2013-03-15 14:39:30","readed":"2013-03-15 14:40:11"},"33386":{"u":"33386","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"567567567567","time":"2013-03-15 14:39:42","readed":"2013-03-15 14:40:11"},"33392":{"u":"33392","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438","time":"2013-03-15 15:53:07","readed":"0000-00-00 00:00:00"},"33396":{"u":"33396","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0410\u0438\u043b\u043b","time":"2013-03-15 20:54:58","readed":"0000-00-00 00:00:00"},"33397":{"u":"33397","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0440\u0440\u043e\u043b\u043f\u043f","time":"2013-03-16 14:29:48","readed":"0000-00-00 00:00:00"}},"state":"ok","working":0.0014}
			
			
			try {
				  JSONArray a = result.Jo.getJSONArray("om_device_message_get:"+globalActivity.settings.getString("device", "")+","+getDeviceByU(deviceU).u );
				  for (int i = 0; i < a.length(); i++) {
			 			JSONObject jsonObject = a.getJSONObject(i);
	          	{
			    	
			    	LocalService.chatmessagelist.add( new MyMessage(jsonObject));
			    	Collections.sort(LocalService.chatmessagelist);
			    	if (LocalService.chatmessagesAdapter!=null) {LocalService.chatmessagesAdapter.notifyDataSetChanged();}
	          	}	
		
				  }
				} catch (Exception e) {
					Log.d(this.getClass().getSimpleName(), "om_device_chat эксепшн"+e.getMessage());
					e.printStackTrace();
				}


			
		}
		
		}
	}

}

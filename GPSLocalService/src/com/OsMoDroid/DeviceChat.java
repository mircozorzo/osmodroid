package com.OsMoDroid;import java.text.SimpleDateFormat;import java.util.ArrayList;import java.util.Collections;import java.util.HashMap;import java.util.Arrays;import java.util.Iterator;import org.json.JSONArray;import org.json.JSONException;import org.json.JSONObject;import com.OsMoDroid.GPSLocalServiceClient.RequestCommandTask;import android.app.Activity;import android.app.AlertDialog;import android.app.ListActivity;import android.content.Context;import android.content.DialogInterface;import android.content.Intent;import android.content.SharedPreferences;import android.os.Bundle;import android.preference.PreferenceManager;import android.text.ClipboardManager;import android.text.format.Time;import android.text.util.Linkify;import android.util.Log;import android.view.ContextMenu;import android.view.Menu;import android.view.ContextMenu.ContextMenuInfo;import android.view.MenuItem;import android.view.View;import android.view.View.OnClickListener;import android.widget.AdapterView;import android.widget.CheckBox;import android.widget.CompoundButton;import android.widget.AdapterView.AdapterContextMenuInfo;import android.widget.AdapterView.OnItemClickListener;import android.widget.CompoundButton.OnCheckedChangeListener;import android.widget.ArrayAdapter;import android.widget.Button;import android.widget.EditText;import android.widget.LinearLayout;import android.widget.ListView;import android.widget.TextView;import android.widget.Toast;public class DeviceChat extends Activity implements ResultsListener{		 ListView lv2;	 SharedPreferences settings;	Button sendButton;	EditText input;		 final private static SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");	 //	public DeviceChat() {		// TODO Auto-generated constructor stub	}Device getDeviceByU (int u){	for (Device dev : LocalService.deviceList){		if (dev.u==u){			return dev;		}	}		return null;	}String getMyApp (){	for (Device dev:LocalService.deviceList ){		if (dev.u==Integer.parseInt(LocalService.settings.getString("device", "-1"))){			return dev.app;		}	}		return "0";		}		/** Called when the activity is first created. */	@Override	public void onCreate(Bundle savedInstanceState) {	    super.onCreate(savedInstanceState);	    setContentView(R.layout.chat);	    settings  = PreferenceManager.getDefaultSharedPreferences(this);	    	   //LocalService.currentchanneldeviceList= LocalService.channelList.get(getIntent().getIntExtra("CHANNELPOS", -1)).deviceList;	   	   LocalService.currentDevice = getDeviceByU(getIntent().getIntExtra("deviceU", 0));	    LocalService.chatmessagesAdapter = new DeviceChatAdapter(getApplicationContext(), R.layout.devicechatitem, LocalService.chatmessagelist);	  	    lv2 = (ListView) findViewById(R.id.chatmessages);	    	    input =(EditText) findViewById(R.id.chateditText);	    input.requestFocus();	    sendButton= (Button) findViewById(R.id.chatsendButton);sendButton.setOnClickListener(new OnClickListener() {	public void onClick(View v) {		if (!(input.getText().toString().equals(""))) {			JSONObject postjson = new JSONObject();		try {			postjson.put("from", settings.getString("device", ""));			postjson.put("to", Integer.toString(getDeviceByU(getIntent().getIntExtra("deviceU", 0)).u));			postjson.put("text", input.getText().toString());			netutil.newapicommand((ResultsListener) DeviceChat.this, "om_device_message_send","json="+postjson.toString());			} 		catch (JSONException e) {				// TODO Auto-generated catch block				e.printStackTrace();			}input.setText("");		}	}});	      	       lv2.setAdapter(LocalService.chatmessagesAdapter);	       if (LocalService.chatmessagesAdapter!=null) {LocalService.chatmessagesAdapter.notifyDataSetChanged();}	      	    Button refsimlinkbutton = (Button) findViewById(R.id.refreshchat);	    refsimlinkbutton.setOnClickListener(new OnClickListener() {			public void onClick(View v) {				netutil.newapicommand((Context)DeviceChat.this, "om_device_message_get:"+getDeviceByU(getIntent().getIntExtra("deviceU", 0)).u);			}});		    netutil.newapicommand((Context)DeviceChat.this, "om_device_message_get:"+getDeviceByU(getIntent().getIntExtra("deviceU", 0)).u);	}	 @Override	protected void onDestroy() {		 LocalService.chatmessagelist.clear();		   		 LocalService.currentDevice=null;		 		super.onDestroy();	}	@Override	  public void onCreateContextMenu(ContextMenu menu, View v,	      ContextMenuInfo menuInfo) {	    super.onCreateContextMenu(menu, v, menuInfo);	    menu.add(0, 1, 0, "Исключить из канала").setIcon(android.R.drawable.ic_menu_share);;	  //  menu.add(0, 2, 0, "Проверить связь").setIcon(android.R.drawable.ic_menu_delete);;	  //  menu.add(0, 3, 0, "Проверить батарею").setIcon(android.R.drawable.ic_menu_edit);;	  }	  @Override	  public boolean onContextItemSelected(MenuItem item) {		  if (item.getItemId() == 1) {					    return super.onContextItemSelected(item);		  }		  return super.onContextItemSelected(item);	  }	public void onResultsSucceeded(APIComResult result) {				Log.d(getClass().getSimpleName(),"OnResultListener Command:"+result.Command+",Jo="+result.Jo);		if (!(result.Jo==null)  ) {			Toast.makeText(this,result.Jo.optString("state")+" "+ result.Jo.optString("error_description"),5).show();					}						if (result.Jo.has("om_device_message_get:"+getDeviceByU(getIntent().getIntExtra("deviceU", 0)).u)){						LocalService.chatmessagelist.clear();			// {"im_get_dialog:173,176":{"33374":{"u":"33374","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0430\u043f\u0440\u0430\u043f\u0440\u0430\u043f\u0440","time":"2013-03-14 21:41:38","readed":"2013-03-15 00:00:04"},"33375":{"u":"33375","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0438\u043c\u0442\u043c\u0438\u0442\u043c\u0438\u0442","time":"2013-03-14 21:41:58","readed":"2013-03-15 00:00:04"},"33376":{"u":"33376","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u044f","time":"2013-03-14 21:42:13","readed":"2013-03-15 00:00:04"},"33377":{"u":"33377","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0430\u043f\u0440\u0430\u043f\u0440\u0430\u043f\u0440","time":"2013-03-14 21:52:46","readed":"2013-03-15 00:00:04"},"33378":{"u":"33378","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"zxcvxzcv","time":"2013-03-14 22:29:15","readed":"2013-03-15 00:00:04"},"33379":{"u":"33379","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"xcvbxcvbxcvbcxvb","time":"2013-03-14 22:32:44","readed":"2013-03-15 00:00:04"},"33385":{"u":"33385","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"bnbvnbvnvbn","time":"2013-03-15 14:39:30","readed":"2013-03-15 14:40:11"},"33386":{"u":"33386","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"567567567567","time":"2013-03-15 14:39:42","readed":"2013-03-15 14:40:11"},"33392":{"u":"33392","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438\u0438","time":"2013-03-15 15:53:07","readed":"0000-00-00 00:00:00"},"33396":{"u":"33396","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0410\u0438\u043b\u043b","time":"2013-03-15 20:54:58","readed":"0000-00-00 00:00:00"},"33397":{"u":"33397","from":"173","from_app":"0","for":"173","for_app":"176","trig":"173-173","trig_app":"0-176","text":"\u0440\u0440\u043e\u043b\u043f\u043f","time":"2013-03-16 14:29:48","readed":"0000-00-00 00:00:00"}},"state":"ok","working":0.0014}									try {				  JSONArray a = result.Jo.getJSONArray("om_device_message_get:"+getDeviceByU(getIntent().getIntExtra("deviceU", 0)).u );				  for (int i = 0; i < a.length(); i++) {			 			JSONObject jsonObject = a.getJSONObject(i);	          	{			    				    	LocalService.chatmessagelist.add( new MyMessage(jsonObject));			    	Collections.sort(LocalService.chatmessagelist);			    	if (LocalService.chatmessagesAdapter!=null) {LocalService.chatmessagesAdapter.notifyDataSetChanged();}	          	}							  }				} catch (Exception e) {					Log.d(getClass().getSimpleName(), "om_device_chat эксепшн"+e.getMessage());					e.printStackTrace();				}					}								}		}
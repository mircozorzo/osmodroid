package com.OsMoDroid;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

public class DevicesFragment extends SherlockFragment implements ResultsListener {

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		registerForContextMenu(lv1);
		globalActivity=(GPSLocalServiceClient) getSherlockActivity();
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		globalActivity.devicesTab.setText("Устройства");
		if(globalActivity.NeedIntent!=null){
			if(globalActivity.NeedIntent.getAction().equals("devicechat")){
			globalActivity.openTabByIntent(globalActivity.NeedIntent);
			globalActivity.NeedIntent=null;
			}
		}
		super.onResume();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(android.view.MenuItem item) {
		  final AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();

		  if (item.getItemId() == 1) {

				//final AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) item.getMenuInfo();

				LocalService.deviceAdapter.getItem(acmi.position);

                                LinearLayout layout = new LinearLayout(globalActivity);

				layout.setOrientation(LinearLayout.VERTICAL);

				final TextView txv = new TextView(globalActivity);

				txv.setText("Ваше сообщение:");

				layout.addView(txv);

				final EditText input = new EditText(globalActivity);

				layout.addView(input);


				AlertDialog alertdialog3 = new AlertDialog.Builder(

						globalActivity)

						.setTitle("Отправка сообщения")

						.setView(layout)

						.setPositiveButton(R.string.send,

								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,

											int whichButton) {



										if (!(input.getText().toString().equals(""))) {

											JSONObject postjson = new JSONObject();



											try {

											//postjson.put("text", input.getText().toString());

											//netutil.newapicommand((ResultsListener) MyDevices.this, "im_send:"+LocalService.deviceList.get((int) acmi.id).uid+","+LocalService.deviceList.get((int) acmi.id).app,"json="+postjson.toString());
										
											postjson.put("from", globalActivity.settings.getString("device", ""));
											postjson.put("to", Integer.toString(LocalService.deviceList.get((int) acmi.id).u));
											postjson.put("text", input.getText().toString());
											netutil.newapicommand((ResultsListener) DevicesFragment.this, "om_device_message_send","json="+postjson.toString());
											} catch (JSONException e) {

												// TODO Auto-generated catch block

												e.printStackTrace();

											}

										}





									}

								})

						.setNegativeButton(R.string.cancel,

								new DialogInterface.OnClickListener() {

									public void onClick(DialogInterface dialog,

											int whichButton) {





									}

								}).create();



				alertdialog3.show();













		      return true;

		    }



		  if (item.getItemId() == 2) {
			  	DeviceChatFragment myDetailFragment = new DeviceChatFragment();
			    Bundle bundle = new Bundle();
			    bundle.putInt("deviceU", LocalService.deviceList.get((int) acmi.id).u);
			    myDetailFragment.setArguments(bundle);
			    globalActivity.addFragment(myDetailFragment);
			   return true;

	    }

		 
 if (item.getItemId() == 3) {
			  
			  ClipboardManager clipboard = (ClipboardManager) globalActivity.getSystemService(Context.CLIPBOARD_SERVICE);
              if (LocalService.deviceList.get((int) acmi.id).url != null)
              clipboard.setText(LocalService.deviceList.get((int) acmi.id).url);
			  
			  return true;

		  }
		  
		  if (item.getItemId() == 4) {
			  
			  Intent sendIntent = new Intent(Intent.ACTION_SEND);
              sendIntent.setType("text/plain");
              sendIntent.putExtra(android.content.Intent.EXTRA_TEXT, LocalService.deviceList.get((int) acmi.id).url != null);
              startActivity(Intent.createChooser(sendIntent, "Поделиться ссылкой"));
			  
			  return true;

		  }
		  
 if (item.getItemId() == 5) {
			  
	 Intent browseIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(LocalService.deviceList.get((int) acmi.id).url));
	 startActivity(browseIntent);
             			  
			  return true;

		  }
		  
		  

	    
		return super.onContextItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		 menu.add(0, 1, 1, "Написать сообщение").setIcon(android.R.drawable.ic_menu_share);

		    menu.add(0, 2, 2, "Сообщения").setIcon(android.R.drawable.ic_menu_delete);
		   
		    menu.add(0, 3, 3, "Скопировать ссылку").setIcon(android.R.drawable.ic_menu_edit);
		    menu.add(0, 4, 4, "Поделиться ссылкой").setIcon(android.R.drawable.ic_menu_edit);
		    menu.add(0, 5, 5, "Открыть в браузере").setIcon(android.R.drawable.ic_menu_edit);
		super.onCreateContextMenu(menu, v, menuInfo);
	}


	private GPSLocalServiceClient globalActivity;
	private ListView lv1;
	
	 @Override
     public void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setHasOptionsMenu(true);
         setRetainInstance(true);
         super.onCreate(savedInstanceState);
     }
	
	@Override
	public void onAttach(Activity activity) {
		globalActivity = (GPSLocalServiceClient)activity;// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	/* (non-Javadoc)
	 * @see com.actionbarsherlock.app.SherlockFragment#onCreateOptionsMenu(com.actionbarsherlock.view.Menu, com.actionbarsherlock.view.MenuInflater)
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem bind = menu.add(0, 1, 0, "Привязать устройство");
		bind.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		bind.setIcon(android.R.drawable.ic_menu_add);
		MenuItem refresh = menu.add(0, 2, 0, "Обновить");
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
		if (item.getItemId() == 1) {

			// final View textEntryView = factory.inflate(R.layout.dialog,
			// null);
			LinearLayout layout = new LinearLayout(getSherlockActivity());
			layout.setOrientation(LinearLayout.VERTICAL);
			final TextView txv5 = new TextView(getSherlockActivity());
			txv5.setText("Хэш:");
			layout.addView(txv5);
			final EditText inputhash = new EditText(getSherlockActivity());
			
			layout.addView(inputhash);

			final TextView txv3 = new TextView(getSherlockActivity());
			txv3.setText("Контрольное число:");
			layout.addView(txv3);

			final EditText inputN = new EditText(getSherlockActivity());
			//input2.setText("Ваше имя");
			layout.addView(inputN);
		//	final EditText input = new EditText(this);
			

			AlertDialog alertdialog3 = new AlertDialog.Builder(
					getSherlockActivity())
					.setTitle("Подключение приложения")
					.setView(layout)
					.setPositiveButton(R.string.yes,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									 
									if (!( inputhash.getText().toString().equals(""))&&!( inputN.getText().toString().equals(""))) {
										netutil.newapicommand(DevicesFragment.this,getSherlockActivity() , "om_device_bind:"+ inputhash.getText().toString()+","+ inputN.getText().toString());
									} 
								}
							})
					.setNegativeButton(R.string.No,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {

									/* User clicked cancel so do some stuff */
								}
							}).create();

			alertdialog3.show();

			// 6564 2638 7281 2680

		}
		if (item.getItemId()==2){
			getDevices(DevicesFragment.this, getSherlockActivity());
		}
		
		return super.onOptionsItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.mydevices, container, false);
		lv1 = (ListView) view.findViewById(R.id.mydeviceslistView);



		   //if (LocalService.deviceAdapter==null){    LocalService.deviceAdapter = new DeviceAdapter(getApplicationContext(),R.layout.deviceitem, LocalService.deviceList);}

		       lv1.setAdapter(LocalService.deviceAdapter);

		       registerForContextMenu(lv1);

		  lv1.setOnItemClickListener(new OnItemClickListener() {



			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,

					long arg3) {

							arg0.showContextMenuForChild(arg1);

							}

		});

		    getDevices(DevicesFragment.this, getSherlockActivity());

		
		return view;

	}
	
	static public void getDevices(ResultsListener listener , Context ctx){

		netutil.newapicommand(listener,ctx, "om_device");



	}


	@Override
	public void onResultsSucceeded(APIComResult result) {
		JSONArray a = null;

		Log.d(getClass().getSimpleName(),"OnResultListener Command:"+result.Command+",Jo="+result.Jo);

		if (result.Jo==null&&result.ja==null)

		{



			Log.d(getClass().getSimpleName(),"notifwar1 Команда:"+result.Command+" Ответ сервера:"+result.rawresponse+ " Запрос:"+result.url);

		//		notifywarnactivity("Команда:"+result.Command+" Ответ сервера:"+result.rawresponse+ " Запрос:"+result.url);
			Toast.makeText(LocalService.serContext, "Esya.ru не отвечает. Проверьте работу интернета." , Toast.LENGTH_LONG).show();

			}
		
		if (!(result.Jo==null)  ) {

			Toast.makeText(getSherlockActivity(),result.Jo.optString("state")+" "+ result.Jo.optString("error_description"),5).show();
		}		
		if (result.Command.equals("APIM")&& !(result.Jo==null))

		{

			Log.d(getClass().getSimpleName(),"APIM Response:"+result.Jo);

			if (result.Jo.has("om_device")){
				LocalService.deviceList.clear();
				LocalService.deviceList.add(new Device("0","Зрители","0"
						,"",
						"",
						"",
						"",
						"",
						"",
						"", globalActivity.settings.getString("uid", "0")
						));
				try {
					  a =	result.Jo.getJSONArray("om_device");
			 		  Log.d(getClass().getSimpleName(), a.toString());
			 		 for (int i = 0; i < a.length(); i++) {
			 			JSONObject jsonObject = a.getJSONObject(i);
			Device devitem = new Device(jsonObject.getString("u"), jsonObject.getString("name"),jsonObject.getString("app")
				,jsonObject.getString("last"),
				"http://m.esya.ru/"+jsonObject.getString("url"),
				jsonObject.getString("where"),
				jsonObject.getString("lat"),
				jsonObject.getString("lon"),
				jsonObject.getString("online"),
				jsonObject.getString("state"),jsonObject.getString("uid")
				);

		LocalService.deviceList.add(devitem);



			 		 }
					} catch (Exception e) {

						 Log.d(getClass().getSimpleName(), "эксепшн");
						//e.printStackTrace();
					}


				 Log.d(getClass().getSimpleName(),LocalService.deviceList.toString());
				// lv1.setAdapter(LocalService.deviceAdapter);
				 LocalService.deviceAdapter.notifyDataSetChanged();
			}

		}

		
	}

}

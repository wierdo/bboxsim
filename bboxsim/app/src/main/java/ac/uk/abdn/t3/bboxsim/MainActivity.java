package ac.uk.abdn.t3.bboxsim;

import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

 public class MainActivity extends Activity implements OnInitListener {
static TextView output;
 
long count;
private TextToSpeech myTTS;
private int MY_DATA_CHECK_CODE = 0;
Location previousLocation=null;

float distanceTravelled=0;

GraphViewSeries seriesCos;
GraphViewSeries seriesSin;
GraphViewSeries seriesRnd;
LinearLayout graphLayout;
LinearLayout graphLayout1;
LinearLayout graphLayout2;
private final String SERVER_URL="http://t3.abdn.ac.uk:8080/bboxserver/upload";

Button start;
Button set;
LocationManager locationManager;
SensorManager sensorManager;
TextView distance;
TextView speed;
Button speakButton;
String id="";
private SensorEventListener sensorListener;


private static String PROVIDER=LocationManager.GPS_PROVIDER ;
String deviceid="bboxSimulatorV1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Memory.previousSend=System.currentTimeMillis();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		output=(TextView)findViewById(R.id.output);
		start=(Button)findViewById(R.id.button_sim);
		set=(Button)findViewById(R.id.button_tre);
		Memory.locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager=Memory.locationManager;
		sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
		distance=(TextView)findViewById(R.id.distance);
		speed=(TextView)findViewById(R.id.speed);
		speakButton=(Button)findViewById(R.id.speakButton);
		//check tts
		 Intent checkTTSIntent = new Intent();
         checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
         startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
		
         speakButton.setOnClickListener(new OnClickListener(){
        	 public void onClick (View v){
        		 if(Memory.isSpeaking){
        			 Memory.isSpeaking=false;
        			 speakButton.setText("Speech OFF");
        			 
        		 }
        		 else{
        			 Memory.isSpeaking=true;
        			 speakButton.setText("Speech ON");
        		 }
        	 }
         });
		sensorListener=new SensorEventListener() {
		    @Override
		    public void onAccuracyChanged(Sensor arg0, int arg1) {
		    }
		    
		    
    
		    @Override
		    public void onSensorChanged(SensorEvent event) {
		        Sensor sensor = event.sensor;
		        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
		        	
		        float i=sensor.getMaximumRange();
		           Memory.getACCReadLoop(event.values[0], event.values[1], event.values[2],i);
		        seriesSin.appendData(new GraphViewData(count++,event.values[0]), true, 1000);
		        seriesCos.appendData(new GraphViewData(count++,event.values[1]), true, 1000);
		        seriesRnd.appendData(new GraphViewData(count++,event.values[2]), true, 1000);
		           
		        if(System.currentTimeMillis()-Memory.previousSend > Memory.LOOP_TIME &&!Memory.sending){
		        	   Log.e("LOG","GETTING JSON DATA AFTER 20 seconds");
		        	  //sendData
		        	 
		        	   
		        	    
		        	   try {
						Memory.jsonBody.put("batt", getBatteryLevel());
					    checkDriving();
					    Memory.jsonBody.put("distance", distanceTravelled);
					    id="genid"+new Date().getTime();
					    Memory.jsonBody.put("provid", id);
					    //shared id for both servers to identify entities
				
					  distanceTravelled=0;
					 String jsonData=Memory.getJsonData();
					 
						 // output.setText(jsonData);
						  Memory.sending=true;
						  speakWords("Sending data to server");
						
			        	  new SendTask(jsonData,SERVER_URL).execute();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        }
		      
		        }
		        else if (sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE) {
		           Memory.temp=event.values[0];
		           Log.e("TEMP", ""+event.values[0]);
		        	
		        }
		        
		    }
		};
		
		Memory.gpsListener=new LocationListener() { public void onLocationChanged(Location location) {
		 if(location.hasSpeed()){
speed.setText(""+location.getSpeed());
		 }
		 if(previousLocation!=null){
		 float dis=location.distanceTo(previousLocation);
		 distanceTravelled+=dis;
		 distance.setText(""+distanceTravelled);
		 previousLocation=location;
		 }
		 previousLocation=location;
		 
		 
			
	        	 
	        //
	        	   
	        	   
	        	
	           
			
			
		}
		public void onProviderDisabled(String provider) { // required for interface, not used
		}
		public void onProviderEnabled(String provider) {
			Toast.makeText(MainActivity.this, "Provider ACTIVATED!"+provider, Toast.LENGTH_LONG).show();// required for interface, not used
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {
		    // required for interface, not used
		}};
		
		start.setOnClickListener(new OnClickListener(){
			@SuppressLint("InlinedApi")
			public void onClick(View v){
				
			//register event
				
				//start getting accelerometer data
				Log.e("LOG", "Activating GPS signals");
				locationManager.requestLocationUpdates(PROVIDER, 500, 10.0f, Memory.gpsListener);
				sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
				sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE), SensorManager.SENSOR_DELAY_NORMAL);
			
				
				
				
			}
		});
		set.setOnClickListener(new OnClickListener(){
			@SuppressLint("InlinedApi")
			public void onClick(View v){
				
			Intent i=new Intent(MainActivity.this,TresholdActivity.class);
			startActivity(i);
				
				
				
			}
		});
		
		//graph
		int num = 1;
		GraphViewData[] data = new GraphViewData[num];
		double v=0;
		for (int i=0; i<num; i++) {
		  v += 0.2;
		  data[i] = new GraphViewData(i, Math.sin(v));
		}
		seriesSin = new GraphViewSeries("X",null, data);
		seriesSin.getStyle().color=Color.RED;
		seriesCos = new GraphViewSeries("Y",null, data);
		seriesCos.getStyle().color=Color.GREEN;
		seriesRnd = new GraphViewSeries("Z",null, data);
		seriesRnd.getStyle().color=Color.YELLOW;
			GraphView graphView = new LineGraphView(
			    this // context
			    , " X" // heading
			);
			
			graphView.addSeries(seriesSin); // data
			graphView.addSeries(seriesCos);
			graphView.addSeries(seriesRnd);
			//graphView.setHorizontalLabels(new String[] {"2 days ago", "today", "tomorrow"});
			graphView.getGraphViewStyle().setGridColor(Color.BLACK);
		
			graphView.setViewPort(2, 1000);
			graphView.setScrollable(true);
			// optional - activate scaling / zooming
			graphView.setScalable(true);
			graphView.setShowLegend(true);
			
			graphLayout = (LinearLayout) findViewById(R.id.graph);
			
			graphLayout.addView(graphView);
		

		
	}
		
		public void onPause(){
			super.onPause();
			locationManager.removeUpdates(Memory.gpsListener);
			sensorManager.unregisterListener(sensorListener);
			count=0;
			distanceTravelled=0;
			previousLocation=null;
		}
		public void onResume(){
			super.onResume();
			
		}
	

		

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	public float getBatteryLevel(){
	   Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	    int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
	    int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

	    // Error checking that probably isn't needed but I added just in case.
	    if(level == -1 || scale == -1) {
	        return 50.0f;
	    }

	    return ((float)level / (float)scale) * 100.0f; 
	}
	
	
	public class SendTask extends AsyncTask<String, String, String>{

	    private String body;
	    private String url;
	

	    /**
	     * Creates a new instance of GetTask with the specified URL and callback.
	     * 
	     * @param restUrl The URL for the REST API.
	     * @param callback The callback to be invoked when the HTTP request
	     *            completes.
	     * 
	     */
	    public SendTask(String json,String url){
	     body=json;
	     this.url=url;
	    }

	    
	   
	    
	    
	    
	    
	    
	    @Override
	    protected String doInBackground(String... params) {
	
	       
	      try{
	    	  HttpPost httpPost = new HttpPost(url);
	          httpPost.setEntity(new StringEntity(body));
	       
	          httpPost.setHeader("Content-type", "application/json");
	         HttpResponse responseHttp= new DefaultHttpClient().execute(httpPost);
	    	
	    	return EntityUtils.toString(responseHttp.getEntity());
	    
	      }
	      catch(Exception e){
	    	  e.printStackTrace();
	    	  return "exception:"+e.getMessage();
	      }
	    }

	    @Override
	    protected void onPostExecute(String result) {
	    	try {
				JSONObject json=new JSONObject(result);
				
				if(json.getBoolean("collected")){
					
					genProvDataSend(json.getString("agent"),id);	
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	
	    	
	    	
	    	
	  	//  speakWords("Data sent!Reading server message:"+result);
	    	Memory.sending=false;
	    	//output.setText(result);
	    	  Memory.previousSend=System.currentTimeMillis();
	    	
	}
	    
	    
	    public void genProvDataSend(String agent, String id){
	    	ProvTrack track=new ProvTrack();
	    String act=ProvTrack.bbox_ns+"ExSimActivity"+new Date().getTime();
	 
	    String accEntity=ProvTrack.bbox_ns+"Acc"+id;
	    String speedEntity=ProvTrack.bbox_ns+"Speed"+id;
	    String locationEntity=ProvTrack.bbox_ns+"Location"+id;
	    
	    String genAct=ProvTrack.bbox_ns+"SimboxGenActivity"+new Date().getTime();
	    String sensorACC=ProvTrack.bbox_ns+"AccelerometerSensor";
	    String sensorGPS=ProvTrack.bbox_ns+"GPSSensor";
	    
	    
	    track.addStatement(genAct+" "+ProvTrack.type+ProvTrack.Activity);
		track.addStatement(accEntity+" "+ProvTrack.wasGeneratedBy+genAct);
		track.addStatement(speedEntity+" "+ProvTrack.wasGeneratedBy+genAct);
		track.addStatement(locationEntity+" "+ProvTrack.wasGeneratedBy+genAct);
		track.addStatement(genAct+" "+ProvTrack.used+sensorACC);
		track.addStatement(genAct+" "+ProvTrack.used+sensorGPS);
	    
		track.addStatement(sensorGPS+" "+ProvTrack.type +ProvTrack.Entity);
		track.addStatement(sensorACC+" "+ProvTrack.type +ProvTrack.Entity);
	    
	    track.addStatement(act+" "+ProvTrack.type+ProvTrack.Activity);
		track.addStatement(act+" "+ProvTrack.used+accEntity);
		track.addStatement(act+" "+ProvTrack.used+speedEntity);
		track.addStatement(act+" "+ProvTrack.used+locationEntity);
		
		track.addStatement(accEntity+" "+ProvTrack.type +ProvTrack.Accelerometer);
		track.addStatement(speedEntity+" "+ProvTrack.type +ProvTrack.Speed);
		track.addStatement(locationEntity+" "+ProvTrack.type +ProvTrack.Location);
		
		track.addStatement(accEntity+" "+ProvTrack.type +ProvTrack.PersonalData);
		track.addStatement(speedEntity+" "+ProvTrack.type +ProvTrack.PersonalData);
		track.addStatement(locationEntity+" "+ProvTrack.type +ProvTrack.PersonalData);
		
		track.addStatement(accEntity+" "+ProvTrack.type +ProvTrack.Entity);
		track.addStatement(speedEntity+" "+ProvTrack.type +ProvTrack.Entity);
		track.addStatement(locationEntity+" "+ProvTrack.type +ProvTrack.Entity);
		
		track.addStatement(accEntity+" "+ProvTrack.description+"\\\"Accelerometer values\\\"^^xsd:string");
		track.addStatement(speedEntity+" "+ProvTrack.description+"\\\"Speed in km/h\\\"^^xsd:string");
		track.addStatement(locationEntity+" "+ProvTrack.description+"\\\"Latitude and Longtitude of location\\\"^^xsd:string");
		

		
		
		
		// ADD GENERATION MAYBE 
		
		track.addStatement(act+" "+ProvTrack.wasAssociatedWith + agent);
		track.addStatement(genAct+" "+ProvTrack.wasAssociatedWith + ProvTrack.agent_resource);
	    track.send();
	    
	    
	    }
	    
	    
	}
	

	public void checkDriving() throws JSONException{
		double x_diff=Math.abs(Memory.ax_min)+Math.abs(Memory.ax_max);
		double y_diff=Math.abs(Memory.ay_min)+Math.abs(Memory.ay_max);
		double z_diff=Math.abs(Memory.az_min)+Math.abs(Memory.az_max);
		output.setText("TURNS:"+x_diff+"BRAKING"+y_diff);
		
		int cornering_level=-1;
		int braking_level=-1;
		
		if(x_diff<Memory.X_LOW_TRESHOLD){
			speakWords("PERFECT CORNERING");
			cornering_level=1;
		}
		else if(x_diff>=Memory.X_LOW_TRESHOLD && x_diff<Memory.X_MEDIUM_TRESHOLD){
			speakWords("GOOD CORNERING");
			cornering_level=2;
		}
		else if(x_diff>=Memory.X_MEDIUM_TRESHOLD && x_diff<Memory.X_HIGH_TRESHOLD){
			speakWords("HIGH CORNERING");
			cornering_level=3;
		}
		else if(x_diff>=Memory.X_HIGH_TRESHOLD){
			//Toast.makeText(this, "Left and Right Turns are EXTREME:"+(int)x_diff, Toast.LENGTH_LONG).show();
	      speakWords("DANGEROUS TURNING!");
	      cornering_level=4;
		}
		
		if(y_diff<Memory.Y_LOW_TRESHOLD){
		speakWords("GREAT BRAKING");
		braking_level=1;
		}
		else if(y_diff>=Memory.Y_LOW_TRESHOLD && y_diff<Memory.Y_MEDIUM_TRESHOLD){
			braking_level=2;
			speakWords("GOOD BRAKING");//	Toast.makeText(this, "Acceleration Braking are LOW:"+(int)y_diff, Toast.LENGTH_LONG).show();
		}
		else if(y_diff>=Memory.Y_MEDIUM_TRESHOLD && y_diff<Memory.Y_HIGH_TRESHOLD){
			braking_level=3;
			speakWords("HIGH BRAKING");//	Toast.makeText(this, "Acceleration Braking  are MEDIUM:"+(int)y_diff, Toast.LENGTH_LONG).show();
		}
		else if(y_diff>=Memory.Y_HIGH_TRESHOLD){
			braking_level=4;
			speakWords("EXTREME BRAKING");	//Toast.makeText(this, "Acceleration Braking are EXTREME:"+(int)y_diff, Toast.LENGTH_LONG).show();
		}
		 
		 Memory.jsonBody.put("braking_level", braking_level);
		 Memory.jsonBody.put("cornering_level", cornering_level);
		 
		 if(previousLocation!=null){
		 speakWords("Your current speed is "+(int)previousLocation.getSpeed()*2+" kilometres per hour.");
		 }
		 else{
			 speakWords("No GPS data available...");
		 }
	}

	@Override
	public void onInit(int initStatus) {
        //check for successful instantiation
       if (initStatus == TextToSpeech.SUCCESS) {
           if(myTTS.isLanguageAvailable(Locale.UK)==TextToSpeech.LANG_AVAILABLE)
               myTTS.setLanguage(Locale.UK);
       }
       else if (initStatus == TextToSpeech.ERROR) {
           Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
       }
		
	}
	
	  private void speakWords(String speech) {
		  if(Memory.isSpeaking){
          //speak straight away
          myTTS.speak(speech, TextToSpeech.QUEUE_ADD, null);
		  }
  }
	  
	 
	  
	  
	  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		     
	        if (requestCode == MY_DATA_CHECK_CODE) {
	            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	                //the user has the necessary data - create the TTS
	            myTTS = new TextToSpeech(this, this);
	            }
	            else {
	                    //no data - install it now
	                Intent installTTSIntent = new Intent();
	                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	                startActivity(installTTSIntent);
	            }
	        }
	  }
 }

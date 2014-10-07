package ac.uk.abdn.t3.bboxsim;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class Memory {
	static LocationListener gpsListener;
	static LocationManager locationManager;
	static JSONObject jsonBody=new JSONObject();
	
	static String deviceid="simbbox001";
	static boolean sending=false;
	
	static boolean isSpeaking= true;
	
	
	float speed;
	double latitude;
	double longitude;
	double altitude;
	double course;
	long timeTaken;
	static long LOOP_TIME=20000;
	static long previousSend;
	
	static float temp=-99;
	static double ax_min;
	static double ax_max;
	static double ay_min;
	static double ay_max;
	static double az_min;
	static double az_max;


	static long ax_average;
	static long az_average;
	static long ay_average;

	static long acc_size;

	static long x_total;
	static long y_total;
	static long z_total;
	
	static	double X_LOW_TRESHOLD=5;
	static	double X_MEDIUM_TRESHOLD=9;
	static	double X_HIGH_TRESHOLD=14;
	static	double Y_LOW_TRESHOLD=3.5;
	static	double Y_MEDIUM_TRESHOLD=5.5;
	static	double Y_HIGH_TRESHOLD=6.5;
	
	
	
	public static void getACCReadLoop(float xf,float yf, float zf,float range){
		
		double x=xf;
		double y=yf;
		double z=zf;
		
		
		
		acc_size++;
		 x_total+=x;
		 y_total+=y;
		 z_total+=z;
		 
		 if(x< ax_min){
		   ax_min=x;
		 }
		 if(x>ax_max){
		   ax_max=x;
		 }
		 
		 if(y<ay_min){
		   ay_min=y;
		 }
		 
		 if(y>ay_max){
		   ay_max=y;
		 }
		 if(z<az_min){
		   az_min=z;
		 }
		 if(z>az_max){
		   az_max=z;
		 }

		
//		MainActivity.x.setText("X: \t"+x+"\t"+ax_min+"\t"+ax_max+"\t"+x_total+"\t"+(double)(x_total/acc_size)+"\t"+temp);
//		MainActivity.y.setText("Y: \t"+y+"\t"+ay_min+"\t"+ay_max+"\t"+y_total+"\t"+(double)(y_total/acc_size)+"\t"+range);
//		MainActivity.z.setText("Z: \t"+z+"\t"+az_min+"\t"+az_max+"\t"+z_total+"\t"+(double)(z_total/acc_size)+"\t"+range);
		
	}
	
		
		
	
	
	public static String getJsonData(){
		Log.e("getData", "Waiting for previous sending to finish");
		while(sending){}
		Log.e("getData", "Waiting for previous sending to finish");
		
		
		 ax_average=x_total/acc_size;
		 az_average=z_total/acc_size;
		 ay_average=y_total/acc_size;
		 
		 try{
		 jsonBody.put("ax_min", ax_min);
		 jsonBody.put("ax_max", ax_max);
		 jsonBody.put("ax_avg", ax_average);
		 jsonBody.put("ay_min", ay_min);
		 jsonBody.put("ay_max", ay_max);
		 jsonBody.put("ay_avg", ay_average);
		 jsonBody.put("az_min", az_min);
		 jsonBody.put("az_max", az_max);
		 jsonBody.put("az_avg", az_average);
		
	
		Location l= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if(l!=null){
		 jsonBody.put("lt", l.getLatitude());
		 jsonBody.put("ln", l.getLongitude());
		 jsonBody.put("al", l.getAltitude());
		 jsonBody.put("cs", l.getBearing());
		 jsonBody.put("sp", l.getSpeed());
		 jsonBody.put("temp", temp);
	
		 long time=l.getTime();
		 Date d=new Date(time);
		 String formattedDate = new SimpleDateFormat("dd-MM-yy hh:mm:ss").format(d);
		 jsonBody.put("tm",formattedDate);
		 }
		 Date sent=new Date();
		 String formattedsent = new SimpleDateFormat("dd-MM-yy hh:mm:ss").format(sent);
		 jsonBody.put("time",formattedsent);
		 jsonBody.put("device_id", deviceid);
		 
		 return jsonBody.toString();
		 }
		 catch(Exception e){
			 e.printStackTrace();	
				return "Something went wrong";
		 }
		 finally{
		 
			 //clear ax and start again
			 ax_min=0;
			   ax_max=0;
			  
			     ay_min=0;
			   ay_max=0;

			     az_min=0;
			   az_max=0;
			   acc_size=0;
			   x_total=0;
			   y_total=0;
			   z_total=0; 
			 
			 
		 }
		
	
			
			
			
			
			
			
		}

		
	
private static long convertACC(float ACC){
	long finaloutput;
double round=(double)Math.round(ACC * 1000) / 1000;
int i=0;
double fraction;
if(round<0){
	i=(int)Math.ceil(round);
}
else{
	i=(int)Math.floor(round);
}
fraction=round-i;
long frl=(long) (fraction*1000);
if(round<0){
finaloutput=i*1000-frl;
}
else{
	finaloutput=i*1000+frl;
}

return finaloutput;
	//BigDecimal round = new BigDecimal(ACC).setScale(5,BigDecimal.ROUND_HALF_UP);
	
//	int i= (round < 0 ? Math.ceil(round) : Math.floor(round)));
	
}
	

	 
	
}

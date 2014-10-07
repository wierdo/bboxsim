package ac.uk.abdn.t3.bboxsim;


import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;


public class ProvTrack {
	
	static String TTT_DEV_ID="simbbox001";
	
	 ArrayList<String> provTrack=new ArrayList<String>();
	static String type="<http://www.w3.org/1999/02/22-rdf-syntax-ns#type> "; //space
	static String ttt_ns="ttt:";static String ttt_prefix="http://t3.abdn.ac.uk/ontologies/t3.owl#";
	static String prov_ns="prov:";static String prov_prefix="http://www.w3.org/ns/prov#";
	static String bbox_ns="bbox:"; static String bbox_prefix="http://t3.abdn.ac.uk/t3v2/1/device/"+TTT_DEV_ID+"/";
	static String agent_resource=bbox_ns+"SimBBoxController";
	
	static String wasAssociatedWith=prov_ns+"wasAssociatedWith ";
	static String wasGeneratedBy=prov_ns+"wasGeneratedBy ";
	static String used=prov_ns+"used ";
	static String entity=prov_ns+"entity ";
	static String atTime=prov_ns+"atTime ";
	static String Activity=prov_ns+"Activity ";
	static String Agent=prov_ns+"Agent ";
    static String Entity=prov_ns+"Entity ";
    static String Usage=prov_ns+"Usage ";
    static String PersonalData=ttt_ns+"PersonalData";
    static String purpose=ttt_ns+"purpose ";
    static String description=ttt_ns+"description ";
    static String Accelerometer=ttt_ns+"Accelerometer";
    static String Location=ttt_ns+"Location";
    static String Performance=ttt_ns+"Performance";
    static String Speed=ttt_ns+"Speed";
   static String SP=" ";
   static String DT=".";
   
   
   public void getTrack(){

	   
	   
   }
	public void send(){
		SendProvTask task=new SendProvTask(provTrack);
		task.execute(new String[]{""});
	}
	public  void addStatement(String statement){
		provTrack.add(statement);		
	}
	
	
	private static class SendProvTask extends AsyncTask<String, String, String>{

	    private String body;
	    private String url;
	ArrayList<String> provTrack;

	    /**
	     * Creates a new instance of GetTask with the specified URL and callback.
	     * 
	     * @param restUrl The URL for the REST API.
	     * @param callback The callback to be invoked when the HTTP request
	     *            completes.
	     * 
	     */
	    public SendProvTask(ArrayList<String> prov){
	 this.provTrack=prov;
	 
	    }

	    
	    
	    public String sendProv(){
			
		
			String body="{\"body\":\"@prefix bbox: <"+bbox_prefix+"> ."+"@prefix prov: <"+prov_prefix+"> ."+"@prefix ttt: <"+ttt_prefix+"> ."+"@prefix xsd:<http://www.w3.org/2001/XMLSchema>.";
					
					
					for (int i=0; i<provTrack.size();i++){
						String line=provTrack.get(i);
						body+=line+" .";		
						
					}
			
			provTrack.clear();
			
		 body+="\"}";
	Log.e("Sending prov:",body);
			DefaultHttpClient client=new DefaultHttpClient();

			try {
			    HttpPost request = new HttpPost("http://t3.abdn.ac.uk:8080/t3v2/1/device/upload/"+TTT_DEV_ID+"/prov");
			    StringEntity params = new StringEntity(body);
			    request.addHeader("content-type", "application/json");
			    request.setEntity(params);
			   HttpResponse resp= client.execute(request);
			   if(resp.getEntity()!=null){
				   Log.e("Entity",EntityUtils.toString(resp.getEntity()));
			   }
			  return "StatusCode: "+ resp.getStatusLine().getStatusCode();
		
			} catch (Exception ex) {
			   ex.printStackTrace();
			   return ex.getMessage();
			} finally {
			 // client.close();
			}
			
			
			
		}
	    
	    
	    
	    @Override
	    protected String doInBackground(String... params) {
	  String s=sendProv();
	  Log.e("STATUS OF SEND PROV", s);
	  return s ;    
	    
	}
	    public void onPostExecute(String result){
	    	Log.e("STATUS of SENT Prov", result);
	    }
	}
	
	
	
	
}


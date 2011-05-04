package edu.asu.eas.snac.cloudvpn.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import edu.asu.eas.snac.cloudvpn.Configuration;
import edu.asu.eas.snac.cloudvpn.activities.R;
import edu.asu.eas.snac.cloudvpn.activities.R.layout;
import edu.asu.eas.snac.cloudvpn.messages.FeedbackMessage;
import edu.asu.eas.snac.cloudvpn.messages.FeedbackType;
import edu.asu.eas.snac.cloudvpn.messages.Message;
import edu.asu.eas.snac.cloudvpn.messages.MessageType;
import edu.asu.eas.snac.cloudvpn.messages.MessageUtility;
import edu.asu.eas.snac.cloudvpn.messages.RegisterMessage;
import edu.asu.eas.snac.cloudvpn.messages.TransmittableMessage;
import edu.asu.eas.snac.cloudvpn.networking.TCPClient;


public class RegisterActivity extends Activity {

	OnClickListener listenSubmit = null;
	Button btnRegSubmit;	
	Message userInfo;
	TCPClient client = null;
	TextView userName,passWord,repassWord,email,phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.register);
		
		client = new TCPClient();
		
		userName = (TextView) findViewById(R.id.userReg);
		passWord = (TextView) findViewById(R.id.passReg);
		repassWord = (TextView) findViewById(R.id.repassReg);
		email = (TextView) findViewById(R.id.emailReg);
		phone = (TextView) findViewById(R.id.phoneReg);
		
		listenSubmit = new OnClickListener() {
			public void onClick(View v) {
				int retVal = 0;	
				if(!passWord.getText().toString().equals(repassWord.getText().toString()))
				{
				      Toast.makeText(getApplicationContext(), "Passwords don't match!",
					          Toast.LENGTH_LONG).show();	
				      return;
				}
				String IMEI = ((TelephonyManager)getSystemService(TELEPHONY_SERVICE)).getDeviceId();
				retVal = sendRegRequest(userName.getText().toString(), passWord.getText().toString(), IMEI, email.getText().toString(), phone.getText().toString());			
				if(retVal == FeedbackType.SUCCESS)
				{
					Toast.makeText(getApplicationContext(), "Congratulations: you're VPN account has been created successfully!",
					          Toast.LENGTH_LONG).show();	
					Intent intent0 = new Intent(RegisterActivity.this, LoginActivity.class);
					setTitle("Main Page");
					startActivity(intent0);
				}
				else if(retVal == FeedbackType.USER_EXIST)
				{
				      Toast.makeText(getApplicationContext(), "Error Code 2: User name already exists.",
					          Toast.LENGTH_LONG).show();						
				}
				else
				{
				      Toast.makeText(getApplicationContext(), "Error Code X: Unknown Error.",
					          Toast.LENGTH_LONG).show();						
				}
				finish();
			}
		};
		
		btnRegSubmit = (Button) findViewById(R.id.btnRegSubmit);
		btnRegSubmit.setOnClickListener(listenSubmit);
	}
	
	public int sendRegRequest(String userName, String passWord, String IMEI, String email, String phone)
	{	//this is the function to communicate with the server!
		
		
		
		if (!client.connect(Configuration.registererIP, Configuration.registererPort))
		{
			Log.e("!!!", "");
			return -1;
		}  //this part facing problems.
		Log.e("Success", "I'm connected to Registerer");
		
		RegisterMessage userInfo = new RegisterMessage();
		FeedbackMessage returnMsg = new FeedbackMessage();
		userInfo.setUsername(userName);
		userInfo.setPassword(passWord);
		userInfo.setIMEI(IMEI);
		userInfo.setEmail(email);
		userInfo.setPhone(phone);
		userInfo.setSeqNo(1);
		userInfo.setMsgType(MessageType.REGISTER_MSG);
		//send complete
		client.send(MessageUtility.convertMessageToTransmittableMessage(userInfo));
		TransmittableMessage message = client.recieve();
		returnMsg = (FeedbackMessage)MessageUtility.convertTranssmitableMessageToMessage(message);
		//recv complete
		
		client.disconnect();
		
		return returnMsg.getReturnNo();
		//return 0;
						
	}
}
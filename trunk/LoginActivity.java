package edu.asu.eas.snac.cloudvpn.activities;

import edu.asu.eas.snac.cloudvpn.Configuration;
import edu.asu.eas.snac.cloudvpn.activities.R;
import edu.asu.eas.snac.cloudvpn.activities.R.id;
import edu.asu.eas.snac.cloudvpn.activities.R.layout;
import edu.asu.eas.snac.cloudvpn.messages.FeedbackMessage;
import edu.asu.eas.snac.cloudvpn.messages.FeedbackType;
import edu.asu.eas.snac.cloudvpn.messages.LoginMessage;
import edu.asu.eas.snac.cloudvpn.messages.MessageType;
import edu.asu.eas.snac.cloudvpn.messages.MessageUtility;
import edu.asu.eas.snac.cloudvpn.messages.TransmittableMessage;
import edu.asu.eas.snac.cloudvpn.networking.TCPClient;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.vpn.IVpnService;
import android.net.vpn.KeyStore;
import android.net.vpn.L2tpIpsecPskProfile;
import android.net.vpn.L2tpProfile;
import android.net.vpn.VpnManager;
import android.net.vpn.VpnProfile;
import android.net.vpn.VpnState;
import android.net.vpn.VpnType;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;



public class LoginActivity extends Activity {
    /** Called when the activity is first created. */
	OnClickListener listenLogin = null;
	OnClickListener listenerRegister = null;
	OnClickListener listenButton1 = null;
	OnClickListener listenReconnCheckBox = null;
	Button btnLogin;
	Button btnReg;
	TextView userName;
	TextView userPassword;
	Button button1;
	CheckBox reconn;
	private Context mContext;
    //private L2tpIpsecPskProfile mProfile;
    private L2tpProfile mProfile;
    private VpnManager mVpnManager;
    private KeyStore mKeyStore;
    private boolean reconnChk; 
    private static boolean connected;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        connected = false;
        reconn = (CheckBox) findViewById(R.id.checkBox2);
        reconnChk = reconn.isChecked(); 
        System.out.println("^^^^Checked = " + reconnChk );
        
        mVpnManager = new VpnManager(this);
        
       
		//reconn = (CheckBox) findViewById(R.id.checkBox2);
        reconn.setOnClickListener(listenReconnCheckBox);
        mKeyStore = KeyStore.getInstance();
        
        listenReconnCheckBox = new OnClickListener() {
        	public void onClick(View v) {
        		reconnChk = !reconnChk;
        	}
        };
        listenButton1 = new OnClickListener() {
			public void onClick(View v) {
				//startActivity(new Intent("android.net.vpn.SETTINGS"));
				if (!connected) {
					Context context = null;
					mContext = context;
					//Object obj = mContext.getSystemService("VPN_SERVICE");
					//System.out.println("obj = " + obj);
					//mVpnManager = (VpnManager) getSystemService("VPN_SERVICE");

					System.out.println("mVpnManager = " + mVpnManager );
					//mProfile = (L2tpIpsecPskProfile) mVpnManager.createVpnProfile(VpnType.L2TP_IPSEC_PSK);
					mProfile = (L2tpProfile) mVpnManager.createVpnProfile(VpnType.L2TP);
					//context.getApplicationContext();
					VpnType vpnTypes[] = VpnManager.getSupportedVpnTypes();
					for (int i=0; i< vpnTypes.length; i++)
						System.out.println(vpnTypes[i].getDisplayName());

					//IVpnService service;
					// = context.getSystemService("android.net.vpn.VPN_SERVICE");
					String prId = Integer.toString((int)(Math.random()));			
					//mKeyStore.put("VPN_i"+prId, "pskpass");					
					mProfile.setId(prId);
					mProfile.setName("CloudVpn");
					System.out.println("******" + mKeyStore.get("VPN_i"+prId));
					//mKeyStore.get("VPN_i"+prId)
					//mProfile.setPresharedKey("pskpass");
					mProfile.setSavedUsername("test");
					mProfile.setSecretEnabled(false);
					mProfile.setServerName("10.211.21.70");
					//mProfile.setRouteList("10.211.21.70/255.255.248.0");
					//mProfile.setState(VpnState.IDLE);

					mVpnManager.startVpnService();

					ServiceConnection c = new ServiceConnection() {
						public void onServiceConnected(ComponentName className,
								IBinder service) {
							try {
								//.connect(mProfile, userName.toString(), userPassword.toString());

								boolean success = IVpnService.Stub.asInterface(service)
								.connect(mProfile, "test", "testpass");
								if (!success) {
									Log.d("TAG", "~~~~~~ connect() failed!");
								} else {
									Toast.makeText(getApplicationContext(), "Connecting...",
											Toast.LENGTH_LONG).show();
									connected = true;
									button1.setText("Disconnect from VPN");
									Log.d("TAG", "~~~~~~ connect() succeeded!");
									broadcastConnectivity(VpnState.CONNECTED);
								}
							} catch (Throwable e) {
								Log.e("TAG", "connect()", e);
								broadcastConnectivity(VpnState.IDLE,
										VpnManager.VPN_ERROR_CONNECTION_FAILED);
							} finally {
								//mContext.unbindService(this);
							}
						}

						public void onServiceDisconnected(ComponentName className) {
							System.out.println("&&&&&&& Disconnected");
							checkStatus();
							if (reconnChk)
								connect();
						}
					};
					if (!bindService(c)) {
						broadcastConnectivity(VpnState.IDLE,
								VpnManager.VPN_ERROR_CONNECTION_FAILED);
					}




				}
				else {
					connected = false;
					Toast.makeText(getApplicationContext(), "Disconnecting...",
							Toast.LENGTH_LONG).show();
					button1.setText("Connect to the Cloud VPN");
					disconnect();
				}
			}
		};
        
        listenLogin = new OnClickListener() {
			public void onClick(View v) {
				startActivity(new Intent("android.net.vpn.SETTINGS"));
			}
		};
		
		listenerRegister = new OnClickListener() {
			public void onClick(View v) {
				Intent intent0 = new Intent(LoginActivity.this, RegisterActivity.class);
				//intent0.putExtra("TCP", client);
				setTitle("VPN User Registration");
				startActivity(intent0);
			}
		};
		userName = (TextView) findViewById(R.id.nameLogin);
        userPassword = (TextView) findViewById(R.id.pwLogin);
		button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(listenButton1);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(listenLogin);
		btnReg = (Button) findViewById(R.id.btnReg);
		btnReg.setOnClickListener(listenerRegister);
		
    }
    
   
    
    private void broadcastConnectivity(VpnState s) {
        mVpnManager.broadcastConnectivity(mProfile.getName(), s);
    }
    
    private void broadcastConnectivity(VpnState s, int errorCode) {
        mVpnManager.broadcastConnectivity(mProfile.getName(), s, errorCode);
    }
    private boolean bindService(ServiceConnection c) {
        return mVpnManager.bindVpnService(c);
    }
    
    public void checkStatus() {
        final ConditionVariable cv = new ConditionVariable();
        cv.close();
        ServiceConnection c = new ServiceConnection() {
            public synchronized void onServiceConnected(ComponentName className,
                    IBinder service) {
                cv.open();
                try {
                    IVpnService.Stub.asInterface(service).checkStatus(mProfile);
                } catch (RemoteException e) {
                    Log.e("TAG", "checkStatus()", e);
                    broadcastConnectivity(VpnState.IDLE);
                } finally {
                    //mContext.unbindService(this);
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                cv.open();
                broadcastConnectivity(VpnState.IDLE);
                //mContext.unbindService(this);
            }
        };
        if (bindService(c)) {
            // wait for a second, let status propagate
            if (!cv.block(1000)) broadcastConnectivity(VpnState.IDLE);
        }
    }
    
    
    public void connect() {
    	if (reconnChk) {
    		Log.e("XXX", "I'm trying to reconnect");
    		mVpnManager.startVpnService();
    		ServiceConnection c = new ServiceConnection() {
    			public void onServiceConnected(ComponentName className,
    					IBinder service) {
    				try {
    					//.connect(mProfile, userName.toString(), userPassword.toString());
    					Log.e("XXX", "inside the try");
    					boolean success = IVpnService.Stub.asInterface(service)
    					.connect(mProfile, "test", "testpass");
    					if (!success) {
    						Log.d("TAG", "~~~~~~ connect() failed!");
    					} else {
    						Toast.makeText(getApplicationContext(), "Connected!",
    								Toast.LENGTH_LONG).show();
    						Log.d("TAG", "~~~~~~ connect() succeeded!");
    						broadcastConnectivity(VpnState.CONNECTED);
    					}
    				} catch (Throwable e) {
    					Log.e("TAG", "connect()", e);
    					broadcastConnectivity(VpnState.IDLE,
    							VpnManager.VPN_ERROR_CONNECTION_FAILED);
    				} finally {
    					//mContext.unbindService(this);
    				}
    			}
    			public void onServiceDisconnected(ComponentName className) {
    				System.out.println("%%%%%%%%%%%%%%%%%% I'm in disconnected");
    				checkStatus();
    				connect();
    			}
    		};

    	}

    }
    
    
    public void disconnect() {
        ServiceConnection c = new ServiceConnection() {
            public void onServiceConnected(ComponentName className,
                    IBinder service) {
                try {
                    IVpnService.Stub.asInterface(service).disconnect();
                } catch (RemoteException e) {
                    Log.e("TAG", "disconnect()", e);
                    checkStatus();
                } finally {
                    //mContext.unbindService(this);
                }
            }

            public void onServiceDisconnected(ComponentName className) {
                checkStatus();
            }
        };
        if (!bindService(c)) {
            checkStatus();
        }
    }


}
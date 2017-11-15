package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class SerialPortFinder {
	private static String[] serials = null;
	private static Hashtable<String,String> htSerialToPath = null;
	
	

	public class Driver {
		public Driver(String name, String root) {
			mDriverName = name;
			mDeviceRoot = root;
		}
		private String mDriverName;
		private String mDeviceRoot;
		
		public Vector<File> getDevices() {
			Vector<File> mDevices = null;
			if (mDevices == null) {
				mDevices = new Vector<File>();
				File dev = new File("/dev");
				File[] files = dev.listFiles();
				int i;
				for (i=0; i<files.length; i++) {
					if (files[i].getAbsolutePath().startsWith(mDeviceRoot)) {
						Log.d(TAG, "Found new device: " + files[i]);
						mDevices.add(files[i]);
					}
				}
			}
			return mDevices;
		}
		
		public String getName() {
			return mDriverName;
		}
	}

	private static final String TAG = "SerialPort";
	Vector<Driver> getDrivers() throws IOException {
		Vector<Driver> mDrivers = null;
		if (mDrivers == null) {
			mDrivers = new Vector<Driver>();
			LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
			String l;
			while((l = r.readLine()) != null) {
				// Issue 3:
				// Since driver name may contain spaces, we do not extract driver name with split()
				String drivername = l.substring(0, 0x15).trim();
				String[] w = l.split(" +");
				if ((w.length >= 5) && (w[w.length-1].equals("serial"))) {
					Log.d(TAG, "Found new driver " + drivername + " on " + w[w.length-4]);
					mDrivers.add(new Driver(drivername, w[w.length-4]));
				}
			}
			r.close();
		}
		return mDrivers;
	}
	
	public String[] getAllDevices() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Iterator<File> itdev = driver.getDevices().iterator();
				while(itdev.hasNext()) {
					String device = itdev.next().getName();
					String value = String.format("%s (%s)", device, driver.getName());
					if(value.startsWith("tty")){
						devices.add(value);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}

	public String[] getAllDevicesPath() {
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Iterator<File> itdev = driver.getDevices().iterator();
				while(itdev.hasNext()) {
					String device = itdev.next().getAbsolutePath();
					if(device.startsWith("tty")){
						devices.add(device);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return devices.toArray(new String[devices.size()]);
	}
	
	public Hashtable<String,String> getSerialDrivers() throws IOException {
		Hashtable<String,String> mDrivers = new Hashtable<String,String>();
		Vector<String> devices = new Vector<String>();
		// Parse each driver
		Iterator<Driver> itdriv;
		try {
			itdriv = getDrivers().iterator();
			while(itdriv.hasNext()) {
				Driver driver = itdriv.next();
				Iterator<File> itdev = driver.getDevices().iterator();
				while(itdev.hasNext()) {
					File file = itdev.next();
					String device = file.getName();
					String path = file.getAbsolutePath();
					String value = String.format("%s (%s)", device, driver.getName());
					if(value.startsWith("tty")){
						devices.add(value);
						mDrivers.put(value,path);
					}
				}
			}
			if(htSerialToPath!=null){
				htSerialToPath.clear();
			}
			htSerialToPath = mDrivers;			
			serials = devices.toArray(new String[devices.size()]);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mDrivers;
	}
	
	public String[] getSerials(){
		try{
			getSerialDrivers();
		}catch(Exception ep){
			Log.d(TAG, "serial finder error" + ep.getMessage() + "\n");
		}
		return serials;
	}
	
	public Hashtable<String, String> getSerialsToPath(){
		return htSerialToPath;
	}
}

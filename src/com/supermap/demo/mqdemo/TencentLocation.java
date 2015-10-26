/**
 * 
 */
package com.supermap.demo.mqdemo;

import java.util.Calendar;

import android.text.format.Time;
import android.view.View;

import com.supermap.data.Point2D;
import com.supermap.plugin.LocationManagePlugin.GPSData;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;

/**
 * @author zhengyl
 *
 */
public class TencentLocation  implements TencentLocationListener{

	private TencentLocationManager mLocationManager;
	
	private GPSData mGPSData = null;

	private Point2D mPoint = null;
	
	private MainActivity m_MainActivity = null;
	
	TencentLocation(MainActivity mainActivity) {
		m_MainActivity = mainActivity;
		
		mLocationManager = TencentLocationManager.getInstance(mainActivity);
		
		mLocationManager.removeUpdates(null);
		mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_WGS84);
		
		mGPSData = new GPSData();
		mPoint = new Point2D();
		
		startLocation();
	}
	
	public void dispose() {
		stopLocation(null);
	}
	
	public void stopLocation(View view) {
		mLocationManager.removeUpdates(this);
		
	}
	
	public void startLocation() {
		TencentLocationRequest request = TencentLocationRequest.create();
		request.setInterval(1000);
		int errorNo = mLocationManager.requestLocationUpdates(request, this);
		if (errorNo == 2) {
			System.out.println("Key不正确，请重新配置！");
		}
	}
	@Override
	public void onLocationChanged(
			com.tencent.map.geolocation.TencentLocation location, int error,
			String reason) {
		if (error == com.tencent.map.geolocation.TencentLocation.ERROR_OK) {
			// 定位成功
			setLocation(location);
		}
	}

	public GPSData getLocation() {
		Calendar cal = Calendar.getInstance();
		mGPSData.lTime = cal.getTimeInMillis();
		
		mGPSData.nYear = cal.get(Calendar.YEAR);
		mGPSData.nMonth = cal.get(Calendar.MONTH);
		mGPSData.nDay = cal.get(Calendar.DAY_OF_MONTH);
		mGPSData.nHour = cal.get(Calendar.HOUR_OF_DAY);
		mGPSData.nMinute = cal.get(Calendar.MINUTE);
		mGPSData.nSecond = cal.get(Calendar.SECOND);
		return mGPSData;
	}
	
	public Point2D getGPSPoint() {
		return mPoint;
	}
	public double getAccuracy() {
		return mGPSData.dAccuracy;
	}
	
	private void setLocation(com.tencent.map.geolocation.TencentLocation location) {
		mGPSData = new GPSData();
		mGPSData.dAltitude = location.getAltitude();
		mGPSData.dLatitude = location.getLatitude();
		mGPSData.dAccuracy = location.getAccuracy();
		mGPSData.dBearing = location.getBearing();
		mGPSData.dLongitude = location.getLongitude();
		mGPSData.dSpeed = location.getSpeed();
		mGPSData.lTime = location.getTime();
		
		Calendar cal = Calendar.getInstance();
//		mGPSData.lTime = cal.getTimeInMillis();
		mGPSData.nYear = cal.get(Calendar.YEAR);
		mGPSData.nMonth = cal.get(Calendar.MONTH);
		mGPSData.nDay = cal.get(Calendar.DAY_OF_MONTH);
		mGPSData.nHour = cal.get(Calendar.HOUR_OF_DAY);
		mGPSData.nMinute = cal.get(Calendar.MINUTE);
		mGPSData.nSecond = cal.get(Calendar.SECOND);
		
		mPoint.setX(location.getLongitude());
		mPoint.setY(location.getLatitude());
		
		if (location.getLatitude() == 0 || location.getLongitude() == 0) {
			// 定位失效
			m_MainActivity.m_StartPoint.setX(116.498778);
			m_MainActivity.m_StartPoint.setY(39.984743);
		} else {
			m_MainActivity.m_StartPoint.setX(location.getLongitude());
			m_MainActivity.m_StartPoint.setY(location.getLatitude());
		}
	}
	
	@Override
	public void onStatusUpdate(String arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		
	}

}

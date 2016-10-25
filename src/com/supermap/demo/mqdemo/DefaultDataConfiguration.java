package com.supermap.demo.mqdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipException;

import com.supermap.demo.util.ZipFileUtil;

public class DefaultDataConfiguration {
	
	private final       String assetsDataDir     = "Data";
	public static final String MapDataPath = MyApplication.SDCARD + "SuperMap/Data/mqdemodata/";
	
	private final       String LicenseName = "Trial License.slm";
	public static final String LicensePath = MyApplication.SDCARD + "SuperMap/License/" ;
	public static final String DefaultWorkspace = MapDataPath + "mqdemo.smwu";
	
	public static final String WebCachePath = MyApplication.SDCARD + "SuperMap/WebCache/";
	
	public static final String MultiMediaDataSources = MapDataPath + "multimedia.udb";
	public static final String MultiMediaDataset = "MQDemo_MediaDataset";
	public static final String CADDataset = "CAD";
	
	public static final String MultiMediaDs = "multimedia";
	
	public DefaultDataConfiguration () 
	{
		
	}
	
	/**
	 * ��������
	 */
	public void autoConfig () 
	{
		
		File licenseDir = new File (LicensePath);
		File mapDataDir = new File (MapDataPath);
		
		if(!licenseDir.exists()){
			FileManager.getInstance().mkdirs(LicensePath);
			configLicense();
		}else {
			boolean isLicenseExists = FileManager.getInstance().isFileExist(LicensePath + LicenseName);
			if(isLicenseExists == false)
			{
				configLicense();
			}
		}

		if (!mapDataDir.exists())
		{
			FileManager.getInstance().mkdirs(MapDataPath);
			configMapData();
			copyPlotData();
		}else {
//			boolean isWorkspaceFileExists = FileManager.getInstance().isFileExist(DefaultWorkspace);
//			
//			if(isWorkspaceFileExists == false)
//			{
//				configMapData();
//			}

			{
				// ���¿�������Դ�빤���ռ�
//				File[] files = mapDataDir.listFiles();
//				for(File f:files)
//				{
//					if (f.isFile())
//					{
//						f.delete();
//					}
//				}

				configMapData();
			}

				
				
			String dir = MapDataPath + "Symbol";
			boolean isPlotDataExists = FileManager.getInstance().isDirExsit(dir);
			
			if (isPlotDataExists == false) {
				copyPlotData();
			}
			
		}
	}
	
    /**
     * ��������ļ�
     */
	private void configLicense ()
	{
		InputStream is = MyAssetManager.getInstance().open(LicenseName);
		if(is != null){
		    FileManager.getInstance().copy(is, LicensePath + LicenseName);
		}
	}
	
	/**
	 * ���õ�ͼ����
	 */
	private void configMapData () 
	{
		String[] datas = MyAssetManager.getInstance().openDir(assetsDataDir);
//		for (String data : datas)
		{
			String data = "mqdemodata.zip";
			InputStream is = MyAssetManager.getInstance().open(assetsDataDir + "/" + data);        // data is a zip file under DataDir
			if(is != null){
				String zip = MapDataPath + "/" + data;

				boolean result = FileManager.getInstance().copy(is, zip);
				if (result) {
					try {
						ZipFileUtil.unZipFile(zip, MapDataPath);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ZipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				File ziFile = new File(zip);
				ziFile.delete();
			}
		}
	}

	public void copyPlotData() {
		String[] datas = MyAssetManager.getInstance().openDir(assetsDataDir);
//		for (String data : datas)
		{
			String data = "PlotData.zip";
			InputStream is = MyAssetManager.getInstance().open(assetsDataDir + "/" + data);        // data is a zip file under DataDir
			if(is != null){
				String zip = MapDataPath + "/" + data;

				boolean result = FileManager.getInstance().copy(is, zip);
				if (result) {
					try {
						ZipFileUtil.unZipFile(zip, MapDataPath);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ZipException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				File ziFile = new File(zip);
				ziFile.delete();
			}
		}

	}
}


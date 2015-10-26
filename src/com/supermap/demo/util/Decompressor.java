package com.supermap.demo.util;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Decompressor {

	public static boolean isUnZiped = false;
	/** 
     * 瑙ｅ帇涓�釜鍘嬬缉鏂囨。 鍒版寚瀹氫綅缃�
     * @param zipFileString 鍘嬬缉鍖呯殑鍚嶅瓧 
     * @param outPathString 鎸囧畾鐨勮矾寰�
     * @throws Exception 
     */  
    public static void UnZipFolder(String zipFile, String targetDir){  
        android.util.Log.v("XZip", "UnZipFolder(String, String)");  
        java.util.zip.ZipInputStream inZip;
		try {
			
			inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFile));
		
	        java.util.zip.ZipEntry zipEntry;  
	        String szName = "";  
	          
				while ((zipEntry = inZip.getNextEntry()) != null) {  
				    szName = zipEntry.getName();  
				  
				    if (zipEntry.isDirectory()) {  
				   
				        java.io.File folder = new java.io.File(targetDir + java.io.File.separator + szName);  
				        folder.mkdirs();  
				  
				    } else {  
				  
				        java.io.File file = new java.io.File(targetDir + java.io.File.separator + szName);  
				        file.createNewFile();  
				        // get the output stream of the file  
				        java.io.FileOutputStream out = new java.io.FileOutputStream(file);  
				        int len;  
				        byte[] buffer = new byte[1024];  
				        while ((len = inZip.read(buffer)) != -1) {  
				            out.write(buffer, 0, len);  
				            out.flush();  
				        }  
				        out.close();  
				    }  
				}
			inZip.close(); 
			
			isUnZiped = true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}              
    } 
      
}

package com.zy.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {
	
	public static byte[] readFile(String filePath){
		File file = new File(filePath);
		byte[] data =null;
		if (file.exists()){
			FileInputStream fio =null;
			try {
				fio = new FileInputStream(file);
				int length = fio.available();
				data = new byte[length];
				fio.read(data);
			} catch (FileNotFoundException e) {
	        	e.printStackTrace();
				throw new RuntimeException(e);
			} catch (IOException e) {
	        	e.printStackTrace();
				throw new RuntimeException(e);
			}finally{
				if (fio!=null){
					try {
						fio.close();
					} catch (IOException e) {
					}
				}
			}
		}
		return data;
	}
	
	public static void writeFile(String filePath, byte[] data) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(data);
			fos.flush();
		} catch (FileNotFoundException e) {
        	e.printStackTrace();
			throw new RuntimeException(e);
		} catch (IOException e) {
        	e.printStackTrace();
			throw new RuntimeException(e);
		} finally {

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
				}
			}

		}
	}

}

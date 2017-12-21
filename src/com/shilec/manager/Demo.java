package com.shilec.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

public class Demo {

	public static void main(String[] args) throws IOException {
//		final String FILE_NAME = "D://DownloadManager/test.txt";
//		String fileMD5 = Utils.getFileMD5(new File(FILE_NAME));
//		System.out.println(fileMD5);
		
//		File file = new File("D://DownloadManager/test.txt");
//		final long TARGET_SIZE = 10 * 1024 * 1024;
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		
//		int n = 0;
//		while(baos.size() < TARGET_SIZE) {
//			for(int i = 0; i < 100; i++) {
//				baos.write(new String("-" + n).getBytes());
//			}
//			baos.write('\n');
//			baos.write('\n');
//			n++;
//		}
//		
//		System.out.println("finished -------------");
//		FileOutputStream fos = new FileOutputStream(file);
//		fos.write(baos.toByteArray());
//		fos.flush();
//		fos.close();
		
		RandomAccessFile rFile = new RandomAccessFile(new File("D://DownloadManager/test.zip"), "rw");
		System.err.println(rFile.length());
	}
	

}

package com.shilec.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.shilec.moudle.FileInfo;

public class FileUtils {
	
	public static List<FileInfo> getFileList(String path) {
		if(path == null) {
			return null;
		}
		File file = new File(path);
		if(!file.exists()) {
			return null;
		}
		
		if(!file.isDirectory()) {
			return null;
		}
		
		List<FileInfo> fileList = new ArrayList<>();
		File[] files = file.listFiles();
		for(File file2 : files) {
			FileInfo fileInfo = new FileInfo();
			fileInfo.name = file2.getName();
			fileInfo.length = file2.length();
			fileInfo.type = file2.isDirectory() ? 0 : 1;
			fileInfo.path = path + "/" + file2.getName();
			//System.out.println(file2.getPath());
			fileInfo.date = file2.lastModified();
			
			fileList.add(fileInfo);
		}
		return fileList;
	}
}

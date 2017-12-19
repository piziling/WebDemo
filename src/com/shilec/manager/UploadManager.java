package com.shilec.manager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class UploadManager
 */
@WebServlet("/UploadManager")
public class UploadManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static final String LOCAL_SAVE_BASE_PATH = "D://UploadManager";
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadManager() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(400, "禁止GET");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String fileName = request.getHeader("Content-Disposition");
		fileName = fileName.split("=")[1];
		if(fileName == null || fileName.equals("")) {
			response.sendError(500, "少Header Content-Disposition");
			return;
		}
		try {
			fileName = fileName.substring(fileName.indexOf("filename=") + 1,fileName.length());
		}catch(Exception e) {
			if(fileName == null || fileName.equals("")) {
				response.sendError(500, "少Header Content-Disposition");
				return;
			}
		}
		
		String fileSize = request.getHeader("Content-Length");
		String sizeRange = request.getHeader("Content-Range");
		sizeRange = sizeRange.replace("bytes", "");
		sizeRange = sizeRange.trim();
		String seesionId = request.getHeader("Session-ID");
		long offset = Long.parseLong(sizeRange.split("-")[0]);
		long length = Long.parseLong(sizeRange.split("/")[1]);
		long end = Long.parseLong(sizeRange.split("-")[1].split("/")[0]);
		
		if(length == 0 || offset > length) {
			response.sendError(400, "参数:offset,length错误");
			return;
		}
		
		if(seesionId == null || seesionId.equals("")) {
			response.sendError(400,"seessionId 不能为空!");
			return;
		}
		
		System.out.println("文件名称:" + fileName);
		System.out.println("文件长度:" + length);
		System.out.println("文件偏移量:" + offset);
		System.out.println("结束位置:" + end);
		ServletInputStream is = request.getInputStream();
		ServletOutputStream os = response.getOutputStream();
		try {
			String ret = saveFile(fileName, offset, end, length, seesionId, is);
			if(!ret.equals("0")) {
				response.sendError(500,ret);
				return;
			}
		}catch(IOException e) {
			response.sendError(500,"文件保存失败");
		}
		
		if(end > (length - 1)) {
			response.setStatus(200);
			os.flush();
			os.close();
			return;
		}
		os.write(("{\"code\":0,\"range\":\"" + offset + "-" + length + "\"}").getBytes());
		os.flush();
		os.close();
	}
	

	private String saveFile(String fileName,long offset,Long end,long length,String sessionId,InputStream is) throws IOException {
		File file = new File(LOCAL_SAVE_BASE_PATH + File.separator + "Temp" + File.separator + sessionId);
		
		if(!file.exists()) {
			boolean ret = file.mkdirs();
			if(!ret) {
				return "创建文件夹失败!";
			}
		}
	
		file = new File(file.getAbsolutePath() + File.separator + fileName + ".temp");
		RandomAccessFile rFile = new RandomAccessFile(file, "rw");
		if(rFile.length() == 0 && offset != 0) {
			return "offset 错误!";
		}
		
		if(rFile.length() == 0 && offset == 0 && length != 0) {
			rFile.setLength(length);
		}
		
		rFile.skipBytes((int) offset);
		writeFile(is, rFile);
		System.out.println("保存成功:" + fileName);
		if(end == length - 1) {
			File f  = new File(LOCAL_SAVE_BASE_PATH + File.separator + fileName);
			file.renameTo(new File(getOnlyOnePath(f)));
			file.delete();
			file = new File(LOCAL_SAVE_BASE_PATH + File.separator + sessionId);
			file.delete();
			System.out.println("删除成功:" + file.getAbsolutePath());
		}
		return "0";
	}
	
	private String getOnlyOnePath(File file) {
		File[] files = new File(file.getParent()).listFiles();
		for(int i = 0; i < Integer.MAX_VALUE; i++) {
			String path = file.getAbsolutePath();
			boolean isContain = false;
			for(File f : files) {
				if(path.equals(f.getAbsolutePath())) {
					isContain = true;
				}
			}
			if(!isContain) {
				return path;
			}
			String ext = path.substring(path.lastIndexOf("."), path.length());
			path = path.substring(0,path.length() - ext.length());
			path += "(" + i + ")" + ext;
		}
		return file.getAbsolutePath();
	} 
	
	private void writeFile(InputStream is,RandomAccessFile rFile) throws IOException {
		int len = 0;
		byte[] buf = new byte[1024 * 1024 * 1024];
		
		while((len = is.read(buf)) != -1) {
			rFile.write(buf,0,len);
		}
		rFile.close();
	}
}

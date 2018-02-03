package com.shilec.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadManager
 */
@WebServlet("/DownloadManager")
public class DownloadManager extends HttpServlet {
	
	private final String FILE_HOME = "D://DownloadManager";
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadManager() {
        super();
        File file = new File(FILE_HOME);
        if(!file.exists()) {
        	file.mkdirs();
        }
    }
    
    private void sendFile(String path,String root,long start, long end,OutputStream os) throws Exception {
    	
    	String basePath = FILE_HOME + "/";
    	if(root != null && root != "") {
    		if(!root.endsWith("/")) {
    			root += "/";
    		}
    		basePath = "D://" + root;
    	}
    	File file = new File(basePath + path);
    	System.out.println(basePath + path);
    	if(!file.exists()) {
    		 System.out.println("FILE_HOME 没有设置，请将要下载的文件放到" + FILE_HOME + "下"); 
    		 return;
    	}
    	
    	RandomAccessFile rFile = new RandomAccessFile(file, "rw");
    	if(start != 0) {
    		rFile.seek(start);
    	}
    	int len = 0;
    	byte[] buf = new byte[1 * 1024 * 1024];
    	
    	while((len = rFile.read(buf)) != -1) {
    		os.write(buf,0,len);
    		os.flush();
    	}
    	
    	rFile.close();
    	os.close();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String path = request.getParameter("path");
		String root = request.getParameter("root");
		String headerRange = request.getHeader("Range");
		System.out.println(1233213123 + "");
//		if(headerRange == null || headerRange.isEmpty()) {
//			response.sendError(405, "缺少Range");
//			return;
//		}
		
		long start = 0;
		long end = 0;
		try {
			System.out.println("client request range:" + headerRange);
			headerRange = headerRange.replace("bytes=", "");
			String[] nSize = headerRange.split("-");
			start = Long.parseLong(nSize[0]);
			end = Long.parseLong(nSize[1]);
		} catch (NumberFormatException | NullPointerException e) {
			//response.sendError(405, "缺少Range");
			//return;
		}
		
		try {
			sendFile(path,root,start,end,response.getOutputStream());
		} catch (Exception e) {
			//response.sendError(500, e.getMessage());
		}
	}
	
	@Override
	protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = request.getParameter("path");
		String root = request.getParameter("root");
		String basePath = FILE_HOME;
		if(root != null && root != "") {
			basePath = root;
		}
		
		System.out.println("client request head:" + basePath + File.separator + path);
		File file = new File(basePath + File.separator + path);
		if(!file.exists()) {
			response.setContentLength(0);
			response.sendError(404);
			return;
		}
		long length = file.length();
		response.setContentLength((int) length);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendError(404, "禁止POST");
	}

	
}

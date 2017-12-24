package com.shilec.manager;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;

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
		response.sendError(400, "��ֹGET");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String fileName = request.getHeader("Content-Disposition");
		fileName = fileName.split("=")[1];
		if(fileName == null || fileName.equals("")) {
			response.sendError(500, "��Header Content-Disposition");
			return;
		}
		try {
			fileName = fileName.substring(fileName.indexOf("filename=") + 1,fileName.length());
		}catch(Exception e) {
			if(fileName == null || fileName.equals("")) {
				response.sendError(500, "��Header Content-Disposition");
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
			response.sendError(400, "����:offset,length����");
			return;
		}
		
		if(seesionId == null || seesionId.equals("")) {
			response.sendError(400,"seessionId ����Ϊ��!");
			return;
		}
		
		System.out.println("�ļ�����:" + fileName);
		System.out.println("�ļ�����:" + length);
		System.out.println("�ļ�ƫ����:" + offset);
		System.out.println("����λ��:" + end);
		ServletInputStream is = request.getInputStream();
		ServletOutputStream os = response.getOutputStream();
		try {
			String ret = saveFile(fileName, offset, end, length, seesionId, is);
			if(!ret.equals("0")) {
				response.sendError(500,ret);
				return;
			}
			System.err.println("ret ===== " + ret);
		}catch(IOException e) {
			response.sendError(500,"�ļ�����ʧ��");
			e.printStackTrace();
		}
		
		if(end > (length - 1)) {
			response.setStatus(200);
			os.write("0".getBytes());
			System.out.println("=============0");
			os.flush();
			os.close();
			return;
		}
		os.write(("{\"code\":0,\"range\":\"" + offset + "-" + length + "\"}").getBytes());
		System.out.println("{\"code\":0,\"range\":\"" + offset + "-" + length + "\"}");
		os.flush();
	}
	

	private String saveFile(String fileName,long offset,Long end,long length,String sessionId,InputStream is) throws IOException {
		File file = new File(LOCAL_SAVE_BASE_PATH + File.separator + "Temp" + File.separator + sessionId);
		
		if(!file.exists()) {
			boolean ret = file.mkdirs();
			if(!ret) {
				return "�����ļ���ʧ��!";
			}
		}
	
		file = new File(file.getAbsolutePath() + File.separator + fileName + ".temp");
		RandomAccessFile rFile = new RandomAccessFile(file, "rw");
		if(rFile.length() == 0 && offset != 0) {
			return "offset ����!";
		}
		
		if(rFile.length() == 0 && offset == 0 && length != 0) {
			rFile.setLength(length);
		}
		
		rFile.skipBytes((int) offset);
		writeFile(is, rFile);
		System.out.println("����ɹ�:" + fileName);
		System.err.println("edn = " + end + ",length = " + length);
		if(end == length - 1) {
			System.err.println("ɾ���ɹ�1:" + file.getAbsolutePath());
			File f  = new File(LOCAL_SAVE_BASE_PATH + File.separator + fileName);
			System.err.println("f = " + f.getAbsolutePath());
			String path = getOnlyOnePath(f);
			System.err.println("only path = " + path);
			file.renameTo(new File(path));
			file.delete();
			file = new File(LOCAL_SAVE_BASE_PATH + File.separator + sessionId);
			file.delete();
			System.err.println("ɾ���ɹ�:" + file.getAbsolutePath());
		}
		return "0";
	}
	
	private String getOnlyOnePath(File file) {
		File[] files = new File(file.getParent()).listFiles();
		System.out.println("files = " + Arrays.asList(files));
		int count = 0;
		for(File f : files) {
			if(f.getName().equals(file.getName())) {
				count ++;
			}
		}
		
		String name = file.getName().split("\\.")[0];
		String ext = file.getName().split("\\.")[1];
		System.out.println("name = " + name + ",ext = " + ext + ",count = " + count);
		String path = file.getParent() + File.separator + 
				(count == 0 ? file.getName() : name + "(" +
						( count) + ")." + ext);
		return path;
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

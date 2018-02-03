package com.shilec.manager;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.shilec.moudle.FileInfo;
import com.shilec.utils.FileUtils;


@WebServlet("/FileListGet")
public class FileListManager extends HttpServlet{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	final String BASE_PATH = "D://FileListGet/";
	public FileListManager() {
		File file = new File(BASE_PATH);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getParameter("path");
		path = path.replaceAll("/WebDemo/FileListGet/", "");
		File file = new File(BASE_PATH + path);
		System.out.println("path = " + BASE_PATH + path);
		if(!file.exists()) {
			resp.sendError(404);
			return;
		}
		
		if(file.isFile()) {
			String dirctUrl = req.getRequestURI();
			dirctUrl = dirctUrl.replaceAll("FileListGet/", "DownloadManager");
			dirctUrl = dirctUrl.replaceAll(path, "");
			dirctUrl = "http://" + req.getRemoteHost() + ":8080" + dirctUrl;
			dirctUrl += "?path=" + path + "&root=FileListGet";
			System.out.println("redirect = " + dirctUrl);
			resp.sendRedirect(dirctUrl);
		} else {
			List<FileInfo> fileList = FileUtils.getFileList(BASE_PATH + path);
			JSONArray jsonArray = new JSONArray();
			for(FileInfo info : fileList) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("name", info.name);
				jsonObject.put("path", info.path.replaceAll(BASE_PATH, ""));
				jsonObject.put("length", info.length);
				jsonObject.put("type", info.type);
				jsonObject.put("date", info.date);
				jsonArray.add(jsonObject);
			}
			resp.getWriter().write(jsonArray.toJSONString());
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doPost(req, resp);
	}
}

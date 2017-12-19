package com.shilec.manager;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoginManager
 */
@WebServlet("/LoginManager")
public class LoginManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginManager() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = request.getParameter("name");
		String psw = request.getParameter("psw");
		System.out.println("name:" + name + ",psw:" + psw);
		
		ServletOutputStream os = response.getOutputStream();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		sb.append("\"code\":");
		sb.append("\"0\"");
		sb.append(",");
		sb.append("\"name\":");
		sb.append("\"" + name + "\"");
		sb.append(",");
		sb.append("\"psw\":");
		sb.append("\"" + psw + "\"");
		sb.append("}");
		
		System.out.println("Ïß³Ì:" + Thread.currentThread().getName());
		bw.write(sb.toString());
		bw.flush();
		bw.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

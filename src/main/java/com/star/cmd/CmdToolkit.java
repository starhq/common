package com.star.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 这类有点价值的，可以把一些脚本之类的用web形式来做
 * 
 * 初步意向可以配合rapid，用web形式来生成项目骨架
 * 
 * @author starhq
 *
 */
public final class CmdToolkit {

	private CmdToolkit() {
		// TODO Auto-generated constructor stub
	}

	public static String readConsole(String cmd, Boolean isPrettify) throws IOException {
		StringBuffer cmdout = new StringBuffer();
		Process process = Runtime.getRuntime().exec("cmd /c " + cmd); // 执行一个系统命令
		InputStream fis = process.getInputStream();
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		String line = null;
		if (isPrettify == null || isPrettify) {
			while ((line = br.readLine()) != null) {
				cmdout.append(line);
			}
		} else {
			while ((line = br.readLine()) != null) {
				cmdout.append(line).append(System.getProperty("line.separator"));
			}
		}
		return cmdout.toString().trim();
	}
}

package com.star.io.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;

import javax.servlet.http.HttpServletResponse;

import com.star.exception.pojo.ToolException;
import com.star.io.CharsetUtil;
import com.star.io.IoUtil;
import com.star.lang.Assert;
import com.star.lang.ReaderHandler;
import com.star.string.StringUtil;

/**
 * 文件读写工具类
 * 
 * @author starhq
 *
 */
public final class FileIoUtil {

	private FileIoUtil() {
	}

	/**
	 * 获得输入流
	 * 
	 */
	public static BufferedInputStream getInputStream(final Path path) {
		Assert.isTrue(PathUtil.exist(path), "get path's inputstream failure,the path is not exists");
		try {
			return new BufferedInputStream(Files.newInputStream(path));
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("get {}'s inputstream failure,the reason is: {}", path, e.getMessage()), e);
		}
	}

	/**
	 * 拷贝文件流用nio
	 * 
	 * 这个肯定是最快的，tps等测下来又有点纠结，复制文件主用这个
	 */
	public static long copy(final FileInputStream src, final FileOutputStream dst) {
		Assert.notNull(src, "copy file failure,the fileInputStream is null");
		Assert.notNull(dst, "copy file failure,the fileOutputStream is null");
		final FileChannel inChannel = src.getChannel();
		final FileChannel outChannel = dst.getChannel();

		try {
			return inChannel.transferTo(0, inChannel.size(), outChannel);
		} catch (IOException e) {
			throw new ToolException(StringUtil.format("copy file failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 从filechannel中读取内容
	 * 
	 * 对于MappedByteBuffer只听说快，还没深入过，对文件内容的读取优先用其他的方法
	 */
	public static String read(final FileChannel fileChannel, final String charset) {
		Assert.notNull(fileChannel, "get string from file channel failure,the input filechannel is null");
		try {
			final MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size())
					.load();
			return StringUtil.byteBuffer2String(buffer, CharsetUtil.charset(charset));
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("get string from file channel failure,the reason is : {}", e.getMessage()), e);
		}
	}

	/**
	 * 读取文件按 readerHandler 处理
	 */
	public static <T> T load(final ReaderHandler<T> readerHandler, final Path path, final String charset) {
		Assert.notNull(readerHandler, "read path and handle it failure,the readerhandler is null");
		Assert.isTrue(PathUtil.exist(path), "read path and handle it failure,the path is not exists");
		BufferedReader reader = null;
		T result = null;
		try {
			reader = IoUtil.getReader(Files.newInputStream(path), CharsetUtil.charset(charset));
			result = readerHandler.handle(reader);
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("read file {} and handle failure,the reasone is: {}", path, e.getMessage()), e);
		} finally {
			IoUtil.close(reader);
		}
		return result;
	}

	/**
	 * 获得一个输出流对象
	 */
	public static BufferedOutputStream getOutputStream(final Path path) {
		if (!Files.exists(path)) {
			PathUtil.touch(path);
		}
		try {
			return new BufferedOutputStream(Files.newOutputStream(path));
		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("get {}'s outputstream failure,the reason is: {}", path, e.getMessage()), e);
		}
	}

	/**
	 * 文件大小可读
	 */
	public static String readableFileSize(final long size) {
		String result;
		if (size > 0) {
			final String[] units = new String[] { "B", "kB", "MB", "GB", "TB", "EB" };
			final int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
			result = new DecimalFormat("#,##0.##").format(size / Math.pow(1024, digitGroups)) + " "
					+ units[digitGroups];
		} else {
			result = "0";
		}
		return result;
	}

	/**
	 * 生成文件的下载流
	 */
	public static void makeStreamFile(final Path path, final HttpServletResponse response, final String contentType) {
		Assert.isTrue(PathUtil.exist(path), "make file's download stream failure,the file not exists");
		Assert.notNull(response, "make file's download stream failure,the response is null");

		BufferedOutputStream output = null;
		BufferedInputStream input = null;
		try {
			response.reset();
			response.setCharacterEncoding(CharsetUtil.UTF_8);

			response.setHeader("Content-disposition", "attachment; filename=" + path.getFileName());
			response.setContentType(contentType);
			response.setContentLength((int) Files.size(path));

			output = new BufferedOutputStream(response.getOutputStream());
			input = getInputStream(path);

			IoUtil.copy(input, output, 4096);

		} catch (IOException e) {
			throw new ToolException(
					StringUtil.format("make file's download stream failure,the reason is: {}", e.getMessage()), e);
		} finally {
			IoUtil.close(input);
			IoUtil.close(output);
		}
	}
}

package com.star.beans;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import com.star.exception.pojo.ToolException;
import com.star.io.IoUtil;
import com.star.string.StringUtil;

/**
 * 对象xml的持久化
 * 
 * 感觉这类没什么大用处，偷懒可以用用
 * 
 * @author starhq
 *
 */
public final class XMLReaderWriter {

	private XMLReaderWriter() {
		super();
	}

	/**
	 * 单个对象转储xml，写进文本
	 */
	public static <T> void writeSingle(final String path, final T instance, final boolean append) {
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(path);
		} catch (FileNotFoundException e) {
			throw new ToolException(StringUtil.format("object convert to xml,write into file failure,the reason is: {}",
					e.getMessage()), e);
		}
		final XMLEncoder encoder = new XMLEncoder(fileOutputStream, "UTF-8", true, 0);
		encoder.writeObject(instance);
		IoUtil.close(encoder);
	}

	/**
	 * 从文本中读取对象
	 */
	public static <T> T readSingle(final String path) {
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			throw new ToolException(StringUtil
					.format("read xml from path and convert to object failure,the reason is: {}", e.getMessage()), e);
		}
		final XMLDecoder decoder = new XMLDecoder(fileInput);
		@SuppressWarnings("unchecked")
		final T instance = (T) decoder.readObject();
		IoUtil.close(decoder);
		return instance;
	}

	/**
	 * 多个对象转储xml，写进文本
	 */
	public static <T> void writeMulit(final String path, final List<T> instances, final boolean append) {
		FileOutputStream fileOutput;
		try {
			fileOutput = new FileOutputStream(path, append);
		} catch (FileNotFoundException e) {
			throw new ToolException(StringUtil.format(
					"collection convert to xml then write into xml failure,the reason is: {}", e.getMessage()), e);
		}
		final XMLEncoder encoder = new XMLEncoder(fileOutput, "UTF-8", true, 0);
		encoder.writeObject(instances);
		IoUtil.close(encoder);
	}

	/**
	 * 从文本中读取对象
	 */
	public static <T> List<T> readMulti(final String path) {
		FileInputStream fileInput;
		try {
			fileInput = new FileInputStream(path);
		} catch (FileNotFoundException e) {
			throw new ToolException(StringUtil.format(
					"read xml from file and conver to collectoin failure,the reason is: {}", e.getMessage()), e);
		}
		final XMLDecoder decoder = new XMLDecoder(fileInput);
		@SuppressWarnings("unchecked")
		final List<T> instances = (List<T>) decoder.readObject();
		IoUtil.close(decoder);
		return instances;
	}

}

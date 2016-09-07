package com.star.config;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

import com.star.clazz.ClassUtil;
import com.star.exception.pojo.ToolException;
import com.star.io.IoUtil;
import com.star.io.file.FileIoUtil;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * 读取属性文件的工具类
 * 
 * @author starhq
 *
 */
public class Config {

	/**
	 * properties读到的值
	 */
	private static Properties props;

	/**
	 * 
	 * 构造，使用相对于Class文件根目录的相对路径
	 * 
	 */
	public Config(final String path) {
		Assert.notBlank(path, "the config file is null,plz check");
		final URL url = ClassUtil.getURL(path);
		this.init(url);
	}

	/**
	 * 初始化配置文件
	 * 
	 */
	public Config(final File configFile) {
		Assert.notNull(configFile, "the config file is null,plz check");
		URL url = null;
		try {
			url = configFile.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new ToolException(StringUtil.format("init config file failue,the reason is: {}", e.getMessage()), e);
		}
		this.init(url);
	}

	/**
	 * 初始化配置文件
	 */
	private void init(final URL configUrl) {
		Assert.notNull(configUrl, "the config url is null,plz check");
		if (Objects.isNull(props) || props.isEmpty()) {
			InputStream input = null;
			props = new Properties();
			try {
				input = configUrl.openStream();
				props.load(input);
			} catch (IOException e) {
				throw new ToolException(StringUtil.format("init config file failue,the reason is: {}", e.getMessage()),
						e);
			} finally {
				IoUtil.close(input);
			}
		}
	}

	/**
	 * 获取字符型型属性值
	 */
	public String getString(final String key, final String defaultValue) {
		final String result = props.getProperty(key);
		return StringUtil.isBlank(result) ? defaultValue : result;
	}

	/**
	 * 获取数字型型属性值
	 */
	public int getInt(final String key, final int defaultValue) {
		final String result = props.getProperty(key);
		return StringUtil.isBlank(result) ? defaultValue : Integer.parseInt(result);
	}

	/**
	 * 获取布尔型型属性值
	 */
	public boolean getBool(final String key, final boolean defaultValue) {
		final String result = props.getProperty(key);
		return StringUtil.isBlank(result) ? defaultValue : Boolean.parseBoolean(result);
	}

	/**
	 * 获取long型型属性值
	 */
	public long getLong(final String key, final long defaultValue) {
		final String result = props.getProperty(key);
		return StringUtil.isBlank(result) ? defaultValue : Long.parseLong(result);
	}

	/**
	 * 
	 * 设置值，无给定键创建之。设置后未持久化
	 * 
	 */
	public void setProperty(final String key, final Object value) {
		props.setProperty(key, value.toString());
	}

	/**
	 * 
	 * 持久化当前设置，会覆盖掉之前的设置
	 */
	public void store(final String absolutePath) {
		BufferedOutputStream output = null;
		try {
			final URL url = ClassUtil.getURL(absolutePath);
			final Path path = Paths.get(url.toURI());
			output = FileIoUtil.getOutputStream(path);
			props.store(output, null);
		} catch (IOException | URISyntaxException e) {
			throw new ToolException(StringUtil.format("store properties failure,the reason is: {}", e.getMessage()), e);
		} finally {
			IoUtil.close(output);
		}
	}

}

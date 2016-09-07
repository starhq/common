package com.star.clazz;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.star.collection.ArrayUtil;
import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.string.StringUtil;

/**
 * Class工具类
 * 
 * @author starhq
 *
 */
public final class ClassUtil {

	private ClassUtil() {
		super();
	}

	/**
	 * 获得对象数组的类数组
	 */
	public static Class<?>[] getClasses(final Object... objects) {
		Assert.notEmpty(objects, "get instance's type array failure,the object's array is empty");
		Class<?>[] clazzs = new Class<?>[objects.length];
		for (int i = 0; i < objects.length; i++) {
			clazzs[i] = objects[i].getClass();
		}
		return clazzs;
	}

	// ======================实例化======================
	/**
	 * 实例化对象,要注意基本类型int的话要改成integer
	 */
	public static <T> T newInstance(final Class<T> clazz, final Object... params) {
		Assert.notNull(clazz, "newinstance failue,the clazz is null");
		try {
			T instance;
			if (ArrayUtil.isEmpty(params)) {
				instance = (T) clazz.newInstance();
			} else {
				instance = clazz.getDeclaredConstructor(getClasses(params)).newInstance(params);
			}
			return instance;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new ToolException(
					String.format("instance class {} failure,the reason is: {}", clazz.getSimpleName(), e.getMessage()),
					e);
		}
	}
	// ======================实例化结束======================

	// =====================classloader==============================
	/**
	 * 获取当前线程的classloader
	 */
	public static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

	/**
	 * 获取当前线程的classloader,若不存在获取类的classLoader
	 */
	public static ClassLoader getClassLoader() {
		final ClassLoader classLoader = getContextClassLoader();
		return Objects.isNull(classLoader) ? ClassUtil.class.getClassLoader() : classLoader;
	}

	/**
	 * 加载类
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> loadClass(final String className, final boolean isInitialized) {
		Assert.notBlank(className, "load class failure,the classname is blank");
		try {
			return (Class<T>) Class.forName(className, isInitialized, getClassLoader());
		} catch (ClassNotFoundException e) {
			throw new ToolException(
					StringUtil.format(" {} load class failure,the reasone is: {}", className, e.getMessage()), e);
		}
	}

	/**
	 * 加载类并初始化
	 */
	public static <T> Class<T> loadClass(final String className) {
		return loadClass(className, true);
	}
	// =====================classloader结束==============================

	// ==============================路径==========================

	/**
	 * 
	 * 获得ClassPath
	 * 
	 */
	public static Set<String> getClassPaths(final String packageName) {
		final String packagePath = packageName.replace(StringUtil.DOT, StringUtil.SLASH);
		Enumeration<URL> resources;
		try {
			resources = getClassLoader().getResources(packagePath);
			final Set<String> paths = new HashSet<String>();
			while (resources.hasMoreElements()) {
				paths.add(resources.nextElement().getPath());
			}
			return paths;
		} catch (IOException e) {
			throw new ToolException(StringUtil.format("get classpath failure,the reason is: {}", e.getMessage()), e);
		}
	}

	/**
	 * 获得Java ClassPath路径
	 * 
	 * 这个作用不明啊
	 */
	public static String[] getJavaClassPaths() {
		return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
	}

	/**
	 * 
	 * 获得资源的URL
	 * 
	 * classpath直接传给空，要路径再加个getPath()
	 * 
	 */
	public static URL getURL(final String resource) {
		return getClassLoader().getResource(resource);
	}

	// ==============================路径结束========================

}

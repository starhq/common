package com.star.beans;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

import com.star.clazz.ClassUtil;
import com.star.collection.ArrayUtil;
import com.star.collection.CollectionUtil;
import com.star.exception.pojo.ToolException;
import com.star.lang.Assert;
import com.star.reflect.MethodUtil;
import com.star.string.StringUtil;

/**
 * bean工具类
 * 
 * @author starhq
 *
 */
public final class BeanUtils {

	private BeanUtils() {
		super();
	}

	/**
	 * 获得bean的PropertyDescriptor
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> clazz) {
		Assert.notNull(clazz, "get class's propertyDescriptor array failure,the clazz is null");
		try {
			return Introspector.getBeanInfo(clazz).getPropertyDescriptors();
		} catch (IntrospectionException e) {
			throw new ToolException(
					StringUtil.format("get clazz {}'s PropertyDescriptor array failue,the reasone is: {}",
							clazz.getClass().getName(), e.getMessage()),
					e);
		}
	}

	/**
	 * 对象转map
	 */
	public static <T> Map<String, Object> beanToMap(final T instance) {
		Assert.notNull(instance, "instance to map failure,the instance is null");
		final PropertyDescriptor[] descriptors = getPropertyDescriptors(instance.getClass());
		final Map<String, Object> maps = CollectionUtil.getMap(descriptors.length);
		for (final PropertyDescriptor descriptor : descriptors) {
			final String name = descriptor.getName();
			if (!StringUtil.CLASS.equals(name)) {
				try {
					final Object value = getSimpleProperty(instance, name, descriptor);
					if (!Objects.isNull(value)) {
						maps.put(name, value);
					}
				} catch (IllegalArgumentException e) {
					throw new ToolException(
							StringUtil.format("instance to map failure,the reasone is: {}", e.getMessage()), e);
				}

			}
		}
		return maps;
	}

	/**
	 * map转对象
	 */
	public static <T> T mapToBean(final Map<String, Object> maps, final Class<T> beanClass) {
		Assert.notEmpty(maps, "map to instance failue,the map is empty");
		final T instance = ClassUtil.newInstance(beanClass);
		final PropertyDescriptor[] descriptors = BeanUtils.getPropertyDescriptors(beanClass);
		for (final PropertyDescriptor descriptor : descriptors) {
			final String name = descriptor.getName();
			if (!StringUtil.CLASS.equals(name)) {
				setSimpleProperty(instance, name, maps.get(name), descriptor);
			}
		}
		return instance;
	}

	/**
	 * 为对象设置简单属性
	 */
	public static <T> void setSimpleProperty(final T instance, final String name, final Object value,
			final PropertyDescriptor desc) {
		Assert.notNull(instance, StringUtil.format("filed {} set value {} failure:the instance is null ", name, value));
		Assert.notBlank(name, StringUtil.format("filed {} set value {} failure:the field name is blank ", name, value));
		PropertyDescriptor descriptor;
		if (Objects.isNull(desc)) {
			try {
				descriptor = new PropertyDescriptor(name, instance.getClass());
			} catch (IntrospectionException e) {
				throw new ToolException(StringUtil.format("filed {} set value {} failure,the reason is: {}", name,
						value, e.getMessage()), e);
			}
		} else {
			descriptor = desc;
		}
		final Method method = descriptor.getWriteMethod();
		MethodUtil.invoke(instance, method, value);
	}

	/**
	 * 获取对象的简单属性
	 */
	public static <T> Object getSimpleProperty(final T instance, final String name, final PropertyDescriptor desc) {
		Assert.notNull(instance, StringUtil.format("get filed {}'s value failure:the instance is null ", name));
		Assert.notBlank(name, StringUtil.format("get filed {}'s value failure:the field name is blank ", name));
		PropertyDescriptor descriptor;
		if (Objects.isNull(desc)) {
			try {
				descriptor = new PropertyDescriptor(name, instance.getClass());
			} catch (IntrospectionException e) {
				throw new ToolException(
						StringUtil.format("get files {}'s value failure,the reason is: {}", name, e.getMessage()), e);
			}
		} else {
			descriptor = desc;
		}
		final Method method = descriptor.getReadMethod();
		return MethodUtil.invoke(instance, method);
	}

	/**
	 * 复制Bean对象属性<br>
	 */
	public static void copyProperties(final Object source, final Object target, final String... ignoreProperties) {
		copyProperties(source, target, null, ignoreProperties);
	}

	/**
	 * 
	 * 复制Bean对象属性<br>
	 * 
	 * 限制类用于限制拷贝的属性，例如一个类我只想复制其父类的一些属性，就可以将editable设置为父类
	 * 
	 * ignoreProperties不拷贝的属性
	 */
	public static void copyProperties(final Object source, final Object target, final Class<?> editable,
			final String... ignoreProperties) {
		final Class<?> actualEditable = !Objects.isNull(editable) && editable.isInstance(target) ? editable
				: target.getClass();

		final PropertyDescriptor[] targetDescriptors = getPropertyDescriptors(actualEditable);
		final PropertyDescriptor[] sourceDescriptors = getPropertyDescriptors(source.getClass());
		for (final PropertyDescriptor targetDescriptor : targetDescriptors) {
			final String name = targetDescriptor.getName();
			if (StringUtil.CLASS.equals(name) || ArrayUtil.contains(ignoreProperties, name)) {
				continue;
			}

			for (final PropertyDescriptor sourceDescriptor : sourceDescriptors) {
				if (name.equals(sourceDescriptor.getName())) {
					final Object value = getSimpleProperty(source, name, sourceDescriptor);
					if (!Objects.isNull(value)) {
						setSimpleProperty(target, name, value, targetDescriptor);
						break;
					}
				}
			}
		}
	}

}

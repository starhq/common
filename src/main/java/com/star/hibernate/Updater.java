package com.star.hibernate;

import java.util.Objects;
import java.util.Set;

import com.star.collection.CollectionUtil;

/**
 * 更新对象类 提供三种更新模式：MAX, MIN, MIDDLE
 * <ul>
 * <li>MIDDLE：默认模式。除了null外，都更新。exclude和include例外。</li>
 * <li>MAX：最大化更新模式。所有字段都更新（包括null）。exclude例外。</li>
 * <li>MIN：最小化更新模式。所有字段都不更新。include例外。</li>
 * </ul>
 */
public class Updater {

	/**
	 * 要设置的实例
	 */
	private final Object bean;

	/**
	 * 要更新的属性
	 */
	private final Set<String> includeProperties = CollectionUtil.getHashSet(null);

	/**
	 * 不需要跟新的属性
	 */
	private final Set<String> excludeProperties = CollectionUtil.getHashSet(null);

	/**
	 * 默认更新模式
	 */
	private transient UpdateMode mode = UpdateMode.MIDDLE;

	/**
	 * 更新的模式
	 * 
	 * @author starhq
	 *
	 */
	public static enum UpdateMode {
		MAX, MIN, MIDDLE
	}

	/**
	 * 构造方法
	 */
	protected Updater(final Object bean) {
		this.bean = bean;
	}

	/**
	 * 创建更新对象
	 */
	public static Updater create(final Object bean) {
		return new Updater(bean);
	}

	/**
	 * 创建更新对象，并设置更新模式
	 */
	public static Updater create(final Object bean, final UpdateMode mode) {
		final Updater updater = new Updater(bean);
		updater.setUpdateMode(mode);
		return updater;
	}

	/**
	 * 
	 * 必须更新的字段
	 * 
	 */
	public Updater include(final String property) {
		includeProperties.add(property);
		return this;
	}

	/**
	 * 
	 * 不更新的字段
	 * 
	 */
	public Updater exclude(final String property) {
		excludeProperties.add(property);
		return this;
	}

	/**
	 * 某一字段是否更新
	 */
	public boolean isUpdate(final String name, final Object value) {
		boolean result;
		switch (mode) {
		case MAX:
			result = !excludeProperties.contains(name);
			break;
		case MIN:
			result = includeProperties.contains(name);
			break;
		default:
			result = Objects.isNull(value) ? includeProperties.contains(name) : !excludeProperties.contains(name);
			break;
		}
		return result;
	}

	/**
	 * 设置更新模式
	 */
	public Updater setUpdateMode(final UpdateMode mode) {
		this.mode = mode;
		return this;
	}

	/**
	 * 获得设置的对象
	 * 
	 */
	public Object getBean() {
		return bean;
	}

	/**
	 * 获得不更新的属性
	 */
	public Set<String> getExcludeProperties() {
		return excludeProperties;
	}

	/**
	 * 获得更新的属性
	 */
	public Set<String> getIncludeProperties() {
		return includeProperties;
	}
}

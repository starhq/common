package com.star.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.star.lang.Filter;

/**
 * 集合工具类
 * 
 * @author http://git.oschina.net/loolly/hutool
 *
 */
public final class CollectionUtil {

	/**
	 * 构造方法
	 */
	private CollectionUtil() {
		// 防止初始化
	}

	/**
	 * 以 conjunction 为分隔符将集合转换为字符串
	 * 
	 * jdk1.8用stringjoiner
	 */
	public static <T> String join(final Iterable<T> collection, final char conjunction) {
		final StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (final T item : collection) {
			if (isFirst) {
				isFirst = false;
			} else {
				builder.append(conjunction);
			}
			builder.append(item);
		}
		return builder.toString();
	}

	/**
	 * 
	 * 集合是否为空
	 * 
	 */
	public static boolean isEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * 
	 * Map是否为空
	 * 
	 */
	public static boolean isEmpty(final Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	/**
	 * 初始化hashmap
	 * 
	 * initialCapacity=0,默认initialCapacity>0,采用initialCapacity大小初始化
	 */
	public static <K, V> Map<K, V> getMap(final Integer initialCapacity) {
		return Objects.isNull(initialCapacity) ? new HashMap<>() : new HashMap<>(initialCapacity);
	}

	/**
	 * 初始化ConcurrentHashMap
	 * 
	 * initialCapacity=0,默认initialCapacity>0,采用initialCapacity大小初始化
	 */
	public static <K, V> Map<K, V> getConcurrentMap(final Integer initialCapacity) {
		return Objects.isNull(initialCapacity) ? new ConcurrentHashMap<K, V>()
				: new ConcurrentHashMap<K, V>(initialCapacity);
	}

	/**
	 * 初始化LinkedHashMap
	 * 
	 * initialCapacity=0,默认initialCapacity>0,采用initialCapacity大小初始化
	 */
	public static <K, V> Map<K, V> getLinkedMap(final Integer initialCapacity) {
		return Objects.isNull(initialCapacity) ? new LinkedHashMap<K, V>() : new LinkedHashMap<K, V>(initialCapacity);
	}

	/**
	 * 初始化TreeMap
	 */
	public static <K, V> Map<K, V> getTreeMap() {
		return new TreeMap<K, V>();
	}

	/**
	 * 初始化ArrayList
	 * 
	 * initialCapacity=0,默认initialCapacity>0,采用initialCapacity大小初始化
	 */
	public static <T> List<T> getList(final Integer initialCapacity) {
		return Objects.isNull(initialCapacity) ? new ArrayList<T>() : new ArrayList<T>(initialCapacity);
	}

	/**
	 * 初始化LinkedList
	 */
	public static <T> List<T> getLinkedList() {
		return new LinkedList<T>();
	}

	/**
	 * 初始化HashSet
	 * 
	 * initialCapacity=0,默认initialCapacity>0,采用initialCapacity大小初始化
	 */
	public static <T> Set<T> getHashSet(final Integer initialCapacity) {
		return Objects.isNull(initialCapacity) ? new HashSet<T>() : new HashSet<T>(initialCapacity);
	}

	/**
	 * 集合转成TreeSet
	 */
	public static <T> Set<T> getTreeSet() {
		return new TreeSet<T>();
	}

	/**
	 * 集合转成treeset
	 */
	public static <T> Set<T> getTreeSet(final Collection<? extends T> collection) {
		return isEmpty(collection) ? new TreeSet<>() : new TreeSet<>(collection);
	}

	/**
	 * 用该方法来代替 {@code new LinkedList<E>()} 方式获得新的 {@code java.util.Queue} 的实例对象。
	 */
	public static <E> Queue<E> getQueue() {
		return new LinkedList<E>();
	}

	/**
	 * 合并set
	 */
	public static <T> Set<T> unionHashSet(final Set<T> setA, final Set<T> setB) {
		Set<T> result;
		if (isEmpty(setA) && isEmpty(setB)) {
			result = Collections.emptySet();
		} else {
			result = wrapHashSet(setA);
			result.addAll(setB);
		}
		return result;
	}

	/**
	 * 两个set取交集
	 */
	public static <T> Set<T> intersectHashSet(final Set<T> setA, final Set<T> setB) {
		Set<T> result;
		if (isEmpty(setA) && isEmpty(setB)) {
			result = Collections.emptySet();
		} else {
			result = wrapHashSet(setA);
			result.retainAll(setB);
		}
		return result;
	}

	/**
	 * 在seta中删除和setb的交集
	 */
	public static <T> Set<T> differenceHashSet(final Set<T> setA, final Set<T> setB) {
		Set<T> result;
		if (isEmpty(setA)) {
			result = Collections.emptySet();
		} else if (isEmpty(setB)) {
			result = setA;
		} else {
			result = wrapHashSet(setA);
			result.removeAll(setB);
		}
		return result;
	}

	/**
	 * 取seta和setb的补集
	 */
	public static <T> Set<T> complementHashSet(final Set<T> setA, final Set<T> setB) {
		return differenceHashSet(unionHashSet(setA, setB), intersectHashSet(setA, setB));
	}

	/**
	 * 集合转成arraylist
	 */
	public static <T> List<T> wrapList(final Collection<? extends T> collection) {
		return isEmpty(collection) ? new ArrayList<T>() : new ArrayList<T>(collection);
	}

	/**
	 * 集合转成hashset
	 */
	public static <T> Set<T> wrapHashSet(final Collection<? extends T> collection) {
		return isEmpty(collection) ? new HashSet<>() : new HashSet<>(collection);
	}

	/**
	 * 
	 * 新建一个HashSet
	 * 
	 */
	@SafeVarargs
	public static <T> Set<T> wrapHashSet(final T... instances) {
		Set<T> set;
		if (ArrayUtil.isEmpty(instances)) {
			set = Collections.emptySet();
		} else {
			set = new HashSet<>(instances.length);
			for (final T instance : instances) {
				set.add(instance);
			}
		}
		return set;
	}

	/**
	 * 
	 * 新建一个list
	 * 
	 */
	@SafeVarargs
	public static <T> List<T> wrapList(final T... instances) {
		List<T> list;
		if (ArrayUtil.isEmpty(instances)) {
			list = Collections.emptyList();
		} else {
			list = new ArrayList<>(instances.length);
			for (final T instance : instances) {
				list.add(instance);
			}
		}
		return list;
	}

	/**
	 * 过滤集合
	 * 
	 * java8考虑用流来处理
	 * 
	 * 出于性能考虑,调用前确保collection是拷贝
	 */
	public static <T> Collection<T> filter(final Collection<T> collection, final Filter<T> filter) {
		final Collection<T> result = collection;
		if (!Objects.isNull(filter)) {
			result.clear();
			for (final T instance : collection) {
				if (filter.accept(instance)) {
					result.add(instance);
				}
			}
		}
		return result;
	}

	/**
	 * 过滤map
	 * 
	 * java8考虑用流来处理
	 * 
	 * 出于性能考虑,调用前确保map是拷贝
	 */
	public static <K, V> Map<K, V> filter(final Map<K, V> map, final Filter<Entry<K, V>> filter) {
		final Map<K, V> result = map;
		if (!Objects.isNull(filter)) {
			result.clear();
			for (final Entry<K, V> entry : map.entrySet()) {
				if (filter.accept(entry)) {
					result.put(entry.getKey(), entry.getValue());
				}
			}
		}
		return result;
	}
}

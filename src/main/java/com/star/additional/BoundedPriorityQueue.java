package com.star.additional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;

/**
 * 有界优先队列<br>
 * 
 * 应用在啥场景没直观印象
 * 
 * @author xiaoleilu
 *
 */
public class BoundedPriorityQueue<E> extends PriorityQueue<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7611383337878251833L;

	/**
	 * 容量
	 */
	private transient int capacity;
	/**
	 * 比较器
	 */
	private transient Comparator<? super E> comparator;

	/**
	 * 构造方法
	 * 
	 * @param capacity
	 *            队列大小
	 */
	public BoundedPriorityQueue(final int capacity) {
		this(capacity, null);
	}

	/**
	 * 构造方法
	 * 
	 * @param capacity
	 *            队列大小
	 * @param comparator
	 *            比较器
	 */
	public BoundedPriorityQueue(final int capacity, final Comparator<? super E> comparator) {
		super(capacity, new Comparator<E>() {

			/**
			 * 比较
			 */
			@Override
			public int compare(final E obj1, final E obj2) {
				int cResult = 0;
				if (Objects.isNull(comparator)) {
					@SuppressWarnings("unchecked")
					final Comparable<E> o1c = (Comparable<E>) obj1;
					cResult = o1c.compareTo(obj2);
				} else {
					cResult = comparator.compare(obj1, obj2);
				}

				return -cResult;
			}

		});
		this.capacity = capacity;
		this.comparator = comparator;
	}

	/**
	 * 入队列，当队列满时，淘汰末尾元素
	 * 
	 * @param ele
	 *            元素
	 * 
	 * @return 成功与否
	 */
	@Override
	public boolean offer(final E ele) {
		boolean result = false;
		if (size() >= capacity) {
			final E head = peek();
			if (this.comparator().compare(ele, head) <= 0) {
				result = true;
			}
			// 当队列满时，就要淘汰顶端队列

			poll();
		}
		return result ? result : super.offer(ele);
	}

	/**
	 * 多个元素如队列
	 * 
	 * @param array
	 *            多个元素
	 * @return 成功与否
	 */
	@SuppressWarnings("unchecked")
	public boolean addAll(final E... array) {
		return this.addAll(Arrays.asList(array));
	}

	/**
	 * 
	 * @return 返回排序后的列表
	 */
	public List<E> toList() {
		final ArrayList<E> list = new ArrayList<E>(this);
		Collections.sort(list, comparator);
		return list;
	}

	/**
	 * @return 返回迭代器
	 */
	@Override
	public Iterator<E> iterator() {
		return toList().iterator();
	}
}

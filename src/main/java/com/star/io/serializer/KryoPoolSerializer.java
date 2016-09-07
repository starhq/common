package com.star.io.serializer;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.star.collection.ArrayUtil;
import com.star.lang.Assert;

/**
 * kryopool实现 kryo不是线程安全的，这类用在并发的情况下用
 * 
 * @author starhq
 *
 */
public class KryoPoolSerializer implements Serializer {

	/**
	 * 持有kryo
	 * 
	 * @author starhq
	 *
	 */
	private static class KryoHolder {
		/**
		 * kryo整个类中时刻要用的
		 */
		private transient final Kryo kryo;
		/**
		 * 缓冲区大小
		 */
		private static final int BUFFER_SIZE = 1024;
		/**
		 * 写
		 */
		private transient final Output output = new Output(BUFFER_SIZE, -1);
		/**
		 * 读
		 */
		private transient final Input input = new Input();

		KryoHolder(final Kryo kryo) {
			this.kryo = kryo;
		}

	}

	/**
	 * kryo_pool_ser实现
	 */
	@Override
	public String name() {
		return "kryo_pool_ser";
	}

	/**
	 * kryo_pool_ser实现序列化对象
	 */
	@Override
	public byte[] serialize(final Object obj) {
		Assert.notNull(obj, "kryo serialize obj failure,the input object is null");
		KryoHolder kryoHolder = null;
		try {
			kryoHolder = KryoPoolImpl.getInstance().get();
			kryoHolder.output.clear();
			kryoHolder.kryo.writeClassAndObject(kryoHolder.output, obj);
			return kryoHolder.output.toBytes();
		} finally {
			KryoPoolImpl.getInstance().offer(kryoHolder);
		}
	}

	/**
	 * kryo_pool_ser实现反序列化对象
	 */
	@Override
	public Object deserialize(final byte[] bytes) {
		Assert.isTrue(!ArrayUtil.isEmpty(bytes), "kryo deserialize obj failure,the input object is null");
		final byte[] copy = bytes.clone();
		KryoHolder kryoHolder = null;
		try {
			kryoHolder = KryoPoolImpl.getInstance().get();
			kryoHolder.input.setBuffer(copy, 0, copy.length);
			return kryoHolder.kryo.readClassAndObject(kryoHolder.input);
		} finally {
			KryoPoolImpl.getInstance().offer(kryoHolder);
		}
	}

	/**
	 * kryopool接口
	 * 
	 * @author starhq
	 *
	 */
	interface KryoPool {
		/**
		 * 从池中获取kryoholder
		 * 
		 * @return
		 */
		KryoHolder get();

		/**
		 * 将kryoholder放回池中
		 * 
		 * @param kryo
		 */
		void offer(KryoHolder kryo);
	}

	/**
	 * kryopool吃接口的实现
	 * 
	 * @author starhq
	 *
	 */
	public static class KryoPoolImpl implements KryoPool {

		/**
		 * KryoHolder队列
		 */
		private final transient Deque<KryoHolder> kryoHolderDeque = new ConcurrentLinkedDeque<>();

		private KryoPoolImpl() {
		}

		/**
		 * 获得kryopool就是个单例类
		 * 
		 * @return
		 */
		public static KryoPool getInstance() {
			return Singleton.POOL;
		}

		/**
		 * 获得kryoholder
		 */
		@Override
		public KryoHolder get() {
			final KryoHolder kryoHolder = kryoHolderDeque.pollFirst();
			return kryoHolder == null ? createInstance() : kryoHolder;
		}

		/**
		 * 创建kryoholder
		 * 
		 * @return
		 */
		private KryoHolder createInstance() {
			final Kryo kryo = new Kryo();
			kryo.setReferences(false);
			return new KryoHolder(kryo);
		}

		/**
		 * 将KryoHolder推回池中
		 */
		@Override
		public void offer(final KryoHolder kryo) {
			kryoHolderDeque.addLast(kryo);
		}

		/**
		 * 持有对kryopool的引用，单利
		 * 
		 * @author starhq
		 *
		 */
		private static class Singleton {
			private static final KryoPool POOL = new KryoPoolImpl();
		}

	}

}

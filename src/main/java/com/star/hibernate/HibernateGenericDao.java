package com.star.hibernate;

import static org.hibernate.EntityMode.POJO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.star.beans.BeanUtils;
import com.star.collection.CollectionUtil;
import com.star.lang.Assert;
import com.star.reflect.GenericUtils;

/**
 * hibernate泛型类
 * 
 * @author starhq
 */
public class HibernateGenericDao<T> extends HibernateDaoSupport {

	/**
	 * 子类的实体对象
	 */
	protected final Class<T> entityClass;

	/**
	 * 构造方法，获得子类的实体对象
	 */
	@SuppressWarnings("unchecked")
	protected HibernateGenericDao() {
		super();
		entityClass = (Class<T>) GenericUtils.getSuperClassGenricType(getClass(), 0);
	}

	/**
	 * 获得实体对象
	 */
	protected Class<T> getEntityClass() {
		Assert.notNull(entityClass, "entity class can not be null");
		return entityClass;
	}

	// ################### 获取 ######################
	/**
	 * 按id找对象
	 */
	public T get(final Serializable id) {
		return get(id, false);
	}

	/**
	 * 按id找对象
	 * 
	 */
	@SuppressWarnings("unchecked")
	public T get(final Serializable id, final boolean lock) {
		return lock ? (T) getSession().get(getEntityClass(), id, LockMode.UPGRADE)
				: (T) getSession().get(getEntityClass(), id);
	}

	/**
	 * 按id查找对象
	 */
	public T load(final Serializable id) {
		return load(id, false);
	}

	/**
	 * 按id查找对象
	 */
	@SuppressWarnings("unchecked")
	public T load(final Serializable id, final boolean lock) {
		return lock ? (T) getSession().load(getEntityClass(), id, LockMode.UPGRADE)
				: (T) getSession().load(getEntityClass(), id);
	}

	// ################### 添加/更新 ######################
	/**
	 * 保存对象
	 * 
	 */
	public void save(final T instance) {
		getHibernateTemplate().save(instance);
	}

	/**
	 * 保存或更新
	 */
	public void saveOrUpdate(final T instance) {
		getHibernateTemplate().saveOrUpdate(instance);
	}

	/**
	 * 好像也是更新，有啥特性忘记了
	 * 
	 */
	public void merage(final T instance) {
		getHibernateTemplate().merge(instance);
	}

	/**
	 * 更新对象
	 * 
	 */
	public void update(final T instance) {
		getHibernateTemplate().update(instance);
	}

	/**
	 * 有条件的更新
	 */
	@SuppressWarnings("unchecked")
	public void updateByUpdater(final Updater updater) {
		final ClassMetadata metada = getSessionFactory().getClassMetadata(entityClass);
		final T bean = (T) updater.getBean();
		final T instance = (T) getSession().get(entityClass, metada.getIdentifier(bean, POJO));
		updaterCopyToPersistentObject(updater, instance);
	}

	// ################### 删除 ######################
	/**
	 * 按对象删除
	 */
	public void delete(final T instance) {
		getHibernateTemplate().delete(instance);
	}

	/**
	 * 按id删除
	 */
	public void deleteById(final Serializable id) {
		getHibernateTemplate().delete(load(id));
	}

	/**
	 * 按条件删除
	 * 
	 */
	public void deleteByParams(final Map<Object, Object> params) {
		deleteByParams(params, SQL.EQUAL.toString(), SQL.AND.toString());
	}

	/**
	 * 按条件删除
	 */
	public void deleteByParams(final Map<Object, Object> params, final String eqOrNo, final String andOrOr) {
		Assert.notEmpty(params, "params can not be empty");
		final StringBuilder builder = getHqlHead(SQL.DELETE.toString());
		builder.append(getHql(params, eqOrNo, andOrOr));
		final Session session = this.getSession();
		final Query query = createNonPageSimpleQuery(session, builder.toString(), params);
		query.executeUpdate();
	}

	/**
	 * 批删除，条件都是and和=
	 */
	public void batchDeleteByHql(final Map<Object, Object> params) {
		batchDeleteByHql(params, SQL.EQUAL.toString(), SQL.AND.toString());
	}

	/**
	 * 批删除
	 */
	public void batchDeleteByHql(final Map<Object, Object> params, final String eqOrNo, final String andOrOr) {
		Assert.notEmpty(params, "params can not be empty");
		final StringBuilder builder = getHqlHead(SQL.DELETE.toString());
		builder.append(getHql(params, eqOrNo, andOrOr));
		final Session session = this.getSession();
		final Query query = createBatchQuery(session, builder.toString(), params);
		query.executeUpdate();
	}

	// ################# 统计 ###################
	/**
	 * 根据hql统计
	 */
	@SuppressWarnings("unchecked")
	public long countByHql(final String hql, final Object... params) {
		Assert.notBlank(hql, "hql can not be blank");
		Assert.notEmpty(params, "params can not be null");
		Assert.notNull(params, "参数为空");
		final List<Long> list = getHibernateTemplate().find(hql, params);
		return CollectionUtil.isEmpty(list) ? 0 : list.get(0);
	}

	/**
	 * 按条件统计
	 */
	@SuppressWarnings("unchecked")
	public long countByHql(final String hql) {
		Assert.notBlank(hql, "hql can not be blank");
		final List<Long> list = getHibernateTemplate().find(hql);
		return CollectionUtil.isEmpty(list) ? 0 : list.get(0);
	}

	/**
	 * 统计
	 */
	public Long count() {
		return count(null, null, null);
	}

	/**
	 * 按条件统计，条件都是=和and
	 */
	public Long count(final Map<Object, Object> params) {
		return count(params, SQL.EQUAL.toString(), SQL.AND.toString());
	}

	/**
	 * 按条件统计
	 */
	public Long count(final Map<Object, Object> params, final String eqOrNo, final String andOrOr) {
		final StringBuilder builder = getHqlHead(SQL.COUNT.toString());
		builder.append(getHql(params, eqOrNo, andOrOr));
		final Session session = this.getSession();
		final Query query = createNonPageSimpleQuery(session, builder.toString(), params);
		return (Long) query.uniqueResult();
	}

	// ################# 查询 ###################
	/**
	 * 查询所有数据
	 */
	public List<T> findAll() {
		return findAll(null, null, null, null);
	}

	/**
	 * 查询所有数据有排序的
	 */
	public List<T> findAll(final Map<String, String> order) {
		return findAll(null, null, null, order);
	}

	/**
	 * 查询所有，条件都是and和等于
	 */
	public List<T> findByParams(final Map<Object, Object> params) {
		Assert.notEmpty(params, "params can not be empty");
		return findAll(params, SQL.EQUAL.toString(), SQL.AND.toString(), null);
	}

	/**
	 * 查询所有，没有排序
	 */
	public List<T> findByParams(final Map<Object, Object> params, final String eqOrNo, final String andOrOr) {
		Assert.notEmpty(params, "params can not be empty");
		return findAll(params, eqOrNo, andOrOr, null);
	}

	/**
	 * 查询所有
	 */
	@SuppressWarnings("unchecked")
	public List<T> findAll(final Map<Object, Object> params, final String eqOrNo, final String andOrOr,
			final Map<String, String> order) {
		final StringBuilder builder = getHqlHead(SQL.FROM.toString());
		if (!CollectionUtil.isEmpty(order)) {
			builder.append(getOrderHql(params, eqOrNo, andOrOr, order));
		}
		if (!CollectionUtil.isEmpty(params) && CollectionUtil.isEmpty(order)) {
			builder.append(getHql(params, eqOrNo, andOrOr));
		}
		final Session session = this.getSession();
		final Query query = createNonPageSimpleQuery(session, builder.toString(), params);
		return query.list();
	}

	/**
	 * 更具hql查找
	 */
	@SuppressWarnings("unchecked")
	public List<T> findByHql(final String hql, final Object... objects) {
		Assert.notBlank(hql, "hql can't be null");
		Assert.notEmpty(objects, "params can't be null");
		return getHibernateTemplate().find(hql, objects);
	}

	/**
	 * 根据条件查找对象
	 */
	public Object getObjectByHql(final String hql, final Object... objects) {
		Assert.notBlank(hql, "hql can't be null");
		Assert.notEmpty(objects, "params can't be null");
		final Session session = this.getSession();
		final Query query = createSimpleQuery(session, hql);
		for (int i = 0; i < objects.length; i++) {
			query.setParameter(i, objects[i]);
		}
		query.setMaxResults(1);
		return query.uniqueResult();
	}

	/**
	 * 根据条件查找对象
	 */
	public Object getObjectByParams(final Map<Object, Object> params) {
		Assert.notNull(params, "params can not  be null");
		final StringBuilder builder = getHqlHead(SQL.FROM.toString());
		builder.append(getHql(params));
		final Session session = this.getSession();
		final Query query = createSimpleQuery(session, builder.toString());
		query.setMaxResults(1);
		return query.uniqueResult();
	}

	// ################### 创建query，查询用################
	/**
	 * 创建query
	 */
	private Query createNonPageSimpleQuery(final Session session, final String hql, final Map<Object, Object> params) {
		return createQuery(session, hql, params, null);
	}

	/**
	 * 创建query
	 */
	private Query createSimpleQuery(final Session session, final String hql) {
		return createQuery(session, hql, null, null);
	}

	/**
	 * 创建query，多条件的还有分页
	 * 
	 */
	private Query createQuery(final Session session, final String hql, final Map<Object, Object> params,
			final Page page) {
		final Query query = session.createQuery(hql);
		if (!CollectionUtil.isEmpty(params)) {
			query.setProperties(params);
		}
		if (!Objects.isNull(page)) {
			query.setFirstResult(page.getBeginIndex()).setFetchSize(page.getPageSize())
					.setMaxResults(page.getPageSize());
		}
		return query;
	}

	/**
	 * 创建批处理的query，像批删除
	 * 
	 */
	private Query createBatchQuery(final Session session, final String hql, final Map<Object, Object> params) {
		final Query query = session.createQuery(hql);
		if (!CollectionUtil.isEmpty(params)) {
			for (final Entry<Object, Object> entry : params.entrySet()) {
				final String name = entry.getKey().toString();
				final List<?> value = (List<?>) entry.getValue();
				query.setParameterList(name, value);
			}
		}
		return query;
	}

	// ################# 分页 ###################
	/**
	 * 分页查询
	 */
	@SuppressWarnings("unchecked")
	public Page findPage(final String hql, final int pageNum, final int pageSize) {
		final Page page = new Page();
		if (pageNum <= 0) {
			page.setPageNum(1);
		} else {
			page.setPageNum(pageNum);
		}
		if (pageSize <= 0) {
			page.setPageSize(Page.DEFAUTSIZE);
		} else {
			page.setPageSize(pageSize);
		}
		final long total = countByHql(hql);
		if (total < 1) {
			page.setTotalCount((long) 0);
			page.setData(Collections.emptyList());
		} else {
			page.setTotalCount(total);
			final Session session = this.getSession();
			final Query query = createQuery(session, hql, null, page);
			final List<T> datas = query.list();
			page.setData(datas);
		}
		return page;
	}

	/**
	 * 分页查询，重载
	 * 
	 */
	public Page findPage(final String hql, final int pageNum) {
		return findPage(hql, pageNum, Page.DEFAUTSIZE);
	}

	/**
	 * 分页查询，重载
	 * 
	 */
	public Page findPage(final String hql, final int pageNum, final Object... objects) {
		return findPage(hql, pageNum, Page.DEFAUTSIZE, objects);
	}

	/**
	 * 分页查询，重载
	 * 
	 */
	@SuppressWarnings("unchecked")
	public Page findPage(final String hql, final int pageNum, final int pageSize, final Object... objects) {
		final Page page = new Page();
		if (pageNum <= 0) {
			page.setPageNum(1);
		} else {
			page.setPageNum(pageNum);
		}
		if (pageSize <= 0) {
			page.setPageSize(Page.DEFAUTSIZE);
		} else {
			page.setPageSize(pageSize);
		}
		final long total = countByHql(hql, objects);
		if (total < 1) {
			page.setTotalCount((long) 0);
			page.setData(new ArrayList<T>(0));
		} else {
			page.setTotalCount(total);
			final Session session = this.getSession();
			final Query query = createQuery(session, hql, null, page);
			for (int i = 0; i < objects.length; i++) {
				query.setParameter(i, objects[i]);
			}
			final List<T> datas = query.list();
			page.setData(datas);
		}
		return page;
	}

	// ################# 更新或删除 ###################
	/**
	 * 更新或删除
	 */
	public void deleteOrUpdate(final String hql, final Object... objects) {
		getHibernateTemplate().bulkUpdate(hql, objects);
	}

	// ################### 用map生成hql，用于比较复杂的hql拼接################
	/**
	 * 用带有字段名和值的一个map创建hql where开始的部分
	 */
	private StringBuilder getHql(final Map<Object, Object> params) {
		return getOrderHql(params, SQL.EQUAL.toString(), SQL.AND.toString(), null);
	}

	/**
	 * 用带有字段名和值的一个map创建hql where开始的部分
	 */
	private StringBuilder getHql(final Map<Object, Object> params, final String eqOrNo, final String andOrOr) {
		return getOrderHql(params, eqOrNo, andOrOr, null);
	}

	/**
	 * 用带有字段名和值的一个map创建hql where开始的部分，带排序的
	 */
	private StringBuilder getOrderHql(final Map<Object, Object> params, final String eqOrNo, final String andOrOr,
			final Map<String, String> order) {
		final StringBuilder builder = new StringBuilder();
		int index = 0;
		if (!CollectionUtil.isEmpty(params)) {
			Assert.notBlank(eqOrNo, "where params is not empty,hql must has = or !=");
			builder.append(" WHERE ");
			for (final Object name : params.keySet()) {
				index++;
				builder.append(name).append(eqOrNo).append(':').append(name).append(' ');
				if (index != params.size()) {
					Assert.notBlank(andOrOr, "where param is not empty,hql must has and or or");
					builder.append(andOrOr).append(' ');
				}
			}
		}
		if (!CollectionUtil.isEmpty(order)) {
			index = 0;
			builder.append(SQL.ORDER).append(' ');
			for (final Map.Entry<String, String> entry : order.entrySet()) {
				final String orderField = entry.getKey();
				final String sort = entry.getValue();
				index++;
				builder.append(orderField).append(' ').append(sort);
				if (index != order.size()) {
					builder.append(',');
				}
			}
		}
		return builder;
	}

	/**
	 * 将更新对象拷贝至实体对象，并处理many-to-one的更新。
	 */
	private void updaterCopyToPersistentObject(final Updater updater, final T instance) {
		final Map<String, Object> fieldValue = BeanUtils.beanToMap(instance);
		for (final Entry<String, Object> entry : fieldValue.entrySet()) {
			final String name = entry.getKey();
			Object value = entry.getValue();
			if (!updater.isUpdate(name, value)) {
				continue;
			}
			if (!Objects.isNull(value)) {
				final Class<?> valueClass = value.getClass();
				final ClassMetadata metadata = getSessionFactory().getClassMetadata(valueClass);
				if (!Objects.isNull(metadata)) {
					final Serializable vid = metadata.getIdentifier(valueClass, POJO);
					if (Objects.isNull(vid)) {
						value = null;
					} else {
						value = getSession().load(valueClass, vid);
					}
				}
				BeanUtils.setSimpleProperty(instance, name, value, null);
			}
		}
	}

	private StringBuilder getHqlHead(final String operator) {
		final StringBuilder builder = new StringBuilder();
		builder.append(operator).append(entityClass.getSimpleName());
		return builder;
	}
}

package net.web.base.mongodb;

import java.util.List;

import net.web.base.entity.Page;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public abstract class MongodbBaseDao<T> {

	/**
	 * spring mongodb　集成操作类　
	 */
	protected MongoTemplate mongoTemplate;

	/**
	 * 获取需要操作的实体类class
	 */
	protected abstract Class<T> getEntityClass();

	/**
	 * 注入mongodbTemplate
	 * 
	 * @param mongoTemplate
	 */
	protected abstract void setMongoTemplate(MongoTemplate mongoTemplate);

	/**
	 * 通过条件查询,查询分页结果
	 */
	public Page getPage(Query query, int pageNumber, int pageSize) {
		long total = this.mongoTemplate.count(query, this.getEntityClass());
		Page page = new Page();
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		page.setTotal((int) total);
		query.skip((page.getPageNumber() - 1) * page.getPageSize());// skip相当于从那条记录开始
		query.limit(pageSize);// 从skip开始,取多少条记录
		List<T> dataList = this.find(query);
		page.setRows(dataList);
		return page;
	}

	/**
	 * 通过条件查询,查询分页结果
	 */
	public Page getPage(Query query, int pageNumber, int pageSize, String collectionName) {
		long total = this.mongoTemplate.count(query, this.getEntityClass(), collectionName);
		Page page = new Page();
		page.setPageNumber(pageNumber);
		page.setPageSize(pageSize);
		page.setTotal((int) total);
		query.skip((page.getPageNumber() - 1) * page.getPageSize());// skip相当于从那条记录开始
		query.limit(pageSize);// 从skip开始,取多少条记录
		List<T> dataList = this.find(query, collectionName);
		page.setRows(dataList);
		return page;
	}

	/**
	 * 通过条件查询实体(集合)
	 */
	public List<T> find(Query query) {
		return mongoTemplate.find(query, this.getEntityClass());
	}

	/**
	 * 通过条件查询实体(集合)
	 */
	public List<T> find(Query query, String collectionName) {
		return mongoTemplate.find(query, this.getEntityClass(), collectionName);
	}

	/**
	 * 查询出所有数据
	 */
	public List<T> findAll() {
		return this.mongoTemplate.findAll(getEntityClass());
	}

	/**
	 * 查询出所有数据
	 */
	public List<T> findAll(String collectionName) {
		return this.mongoTemplate.findAll(getEntityClass(), collectionName);
	}

	/**
	 * 通过一定的条件查询一个实体
	 */
	public T findOne(Query query) {
		return mongoTemplate.findOne(query, this.getEntityClass());
	}

	/**
	 * 通过一定的条件查询一个实体
	 */
	public T findOne(Query query, String collectionName) {
		return mongoTemplate.findOne(query, this.getEntityClass(), collectionName);
	}

	/**
	 * 查询并且修改记录
	 */
	public T findAndModify(Query query, Update update) {
		return this.mongoTemplate.findAndModify(query, update, this.getEntityClass());
	}

	/**
	 * 查询并且修改记录
	 */
	public T findAndModify(Query query, Update update, String collectionName) {
		return this.mongoTemplate.findAndModify(query, update, this.getEntityClass(), collectionName);
	}

	/**
	 * 按条件查询,并且删除记录
	 */
	public T findAndRemove(Query query) {
		return this.mongoTemplate.findAndRemove(query, this.getEntityClass());
	}

	/**
	 * 按条件查询,并且删除记录
	 */
	public T findAndRemove(Query query, String collectionName) {
		return this.mongoTemplate.findAndRemove(query, this.getEntityClass(), collectionName);
	}

	/**
	 * 通过条件查询更新数据
	 */
	public void updateFirst(Query query, Update update) {
		mongoTemplate.updateFirst(query, update, this.getEntityClass());
	}

	/**
	 * 通过条件查询更新数据
	 */
	public void updateFirst(Query query, Update update, String collectionName) {
		mongoTemplate.updateFirst(query, update, this.getEntityClass(), collectionName);
	}

	/**
	 * 保存一个对象到mongodb
	 */
	public T save(T bean) {
		mongoTemplate.save(bean);
		return bean;
	}

	/**
	 * 保存一个对象到mongodb
	 */
	public T save(T bean, String collectionName) {
		mongoTemplate.save(bean, collectionName);
		return bean;
	}

	/**
	 * 新增一个对象到mongodb
	 */
	public T insert(T bean) {
		mongoTemplate.insert(bean);
		return bean;
	}

	/**
	 * 新增一个对象到mongodb
	 */
	public T insert(T bean, String collectionName) {
		mongoTemplate.insert(bean, collectionName);
		return bean;
	}

	/**
	 * 新增一个对象集合到mongodb
	 */
	public List<T> insertAll(List<T> list) {
		mongoTemplate.insertAll(list);
		return list;
	}

	/**
	 * 新增一个对象集合到mongodb
	 */
	public List<T> insert(List<T> list) {
		mongoTemplate.insert(list, this.getEntityClass());
		return list;
	}

	/**
	 * 新增一个对象集合到mongodb
	 */
	public List<T> insert(List<T> list, String collectionName) {
		mongoTemplate.insert(list, collectionName);
		return list;
	}

	/**
	 * 通过ID获取记录
	 */
	public T findById(String id) {
		return mongoTemplate.findById(id, this.getEntityClass());
	}

	/**
	 * 通过ID获取记录,并且指定了集合名(表的意思)
	 */
	public T findById(String id, String collectionName) {
		return mongoTemplate.findById(id, this.getEntityClass(), collectionName);
	}

}

package net.web.base.dao;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;

import net.web.base.entity.Page;
import net.web.base.util.DialectUtils;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

public class BaseDaoImpl extends NamedParameterJdbcDaoSupport {

	@Resource(name = "dataSource")
	protected DataSource dataSource;

	@PostConstruct
	void init() {
		setDataSource(this.dataSource);
	}

	protected Map<String, Object> queryForMap(String sql) {
		return this.getJdbcTemplate().queryForMap(sql);
	}

	protected Map<String, Object> queryForMap(String sql, Object javaBean) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(javaBean);
		return this.getNamedParameterJdbcTemplate().queryForMap(sql, paramSource);
	}

	protected Map<String, Object> queryForMap(String sql, Map<String, ?> params) {
		return this.getNamedParameterJdbcTemplate().queryForMap(sql, params);
	}

	protected List<Map<String, Object>> queryForList(String sql) {
		return this.getJdbcTemplate().queryForList(sql);
	}

	protected List<Map<String, Object>> queryForList(String sql, Object javaBean) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(javaBean);
		return this.getNamedParameterJdbcTemplate().queryForList(sql, paramSource);
	}

	protected List<Map<String, Object>> queryForList(String sql, Map<String, ?> params) {
		return this.getNamedParameterJdbcTemplate().queryForList(sql, params);
	}

	protected int update(String sql) {
		return this.getJdbcTemplate().update(sql);
	}

	protected int update(String sql, Object javaBean) {
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(javaBean);
		return this.getNamedParameterJdbcTemplate().update(sql, paramSource);
	}

	protected int update(String sql, Map<String, ?> params) {
		return this.getNamedParameterJdbcTemplate().update(sql, params);
	}

	protected int batchUpdate(String sql, List<Object> paramsList) {
		SqlParameterSource[] batchArgs = new SqlParameterSource[paramsList.size()];
		for (int i = 0; i < paramsList.size(); i++) {
			batchArgs[i] = new BeanPropertySqlParameterSource(paramsList.get(i));
		}
		int[] result = this.getNamedParameterJdbcTemplate().batchUpdate(sql, batchArgs);
		int count = 0;
		for (int i : result) {
			if (i == 1) {
				count++;
			}
		}
		return count;
	}

	protected void executeProc(String procName, final Object[] params) {
		StringBuffer sql = new StringBuffer();
		sql.append("{call ");
		sql.append(procName);
		if ((params != null) && (params.length > 0)) {
			sql.append("(");
			for (int i = 0; i < params.length; i++) {
				if (i == params.length - 1) {
					sql.append("?");
				} else {
					sql.append("?,");
				}
			}
			sql.append(")");
		}
		sql.append("}");
		this.getJdbcTemplate().execute(sql.toString(), new CallableStatementCallback<Object>() {
			@Override
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				if ((params != null) && (params.length > 0)) {
					for (int i = 1; i <= params.length; i++) {
						cs.setObject(i, params[(i - 1)]);
					}
				}
				cs.execute();
				return null;
			}
		});
	}

	protected Page selectPaginatedBySql(String sql, Map<String, Object> params, int pageNumber, int pageSize) {
		DialectUtils dialectUtils = DialectUtils.getInstance();
		Page page = new Page();
		page.setPageSize(pageSize);
		page.setPageNumber(pageNumber);

		String countSQL = dialectUtils.generateCountSql(sql);
		String pageSQL = dialectUtils.generatePageSql(sql, getDataSource());

		int recordSum = 0;
		List<Map<String, Object>> countList = queryForList(countSQL, params);
		if ((countList != null) && (countList.size() == 1)) {
			Map<String, Object> map = (Map<String, Object>) countList.get(0);
			recordSum = ((BigDecimal) map.get("count(1)")).intValue();
		}
		page.setTotal(recordSum);

		dialectUtils.generatePageParams(params, pageNumber, pageSize, getDataSource());
		List<Map<String, Object>> pageList = queryForList(pageSQL, params);
		page.setRows(pageList);

		return page;
	}

}

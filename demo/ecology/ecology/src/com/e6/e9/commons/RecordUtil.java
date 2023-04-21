package com.e6.e9.commons;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.e6.common.utils.CustomUtil;

import weaver.conn.ConnectionPool;
import weaver.conn.RecordSet;
import weaver.conn.WeaverConnection;
import weaver.general.GCONST;

/**
 * 
 * 描述:数据库操作工具
 * 
 * @author HM
 * @date 2022年5月3日
 *
 */
public class RecordUtil {

	private final static Logger log = Logger.getLogger(RecordUtil.class);

	public static <T> T findById(Class<T> clazz, Object id) {
		String idField = getIdFieldName(clazz);
		Map<String, Field> queryFields = getQueryFields(clazz);
		String queryFieldsSql = getQueryFiledsSql(queryFields);
		Table table = clazz.getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("SELECT ").append(queryFieldsSql);
		sql.append(" FROM ").append(table.name());
		sql.append(" WHERE ").append(idField).append("=?");
		log.debug("findById(), sql = " + sql + ", " + idField + " = " + id);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			setParameter(stmt, 1, id);
			rs = stmt.executeQuery();
			if (!rs.next()) {
				throw new Exception("查询无结果！");
			}
			return toObject(rs, clazz, queryFields);
		} catch (Exception e) {
			log.error(e);
			return null;
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
	}

	public static <T> T find(Class<T> clazz, Map<String, Object> values) {
		T obj = single(clazz, values);
		if (obj == null) {
			log.debug("find(), 查询无结果！");
		}
		return obj;
	}

	public static <T> T find(Class<T> clazz, Object[] values) {
		Map<String, Object> mapValuses = new HashMap<String, Object>();
		for (int i = 0; i < values.length; i += 2) {
			mapValuses.put("" + values[i], values[i + 1]);
		}
		T obj = single(clazz, mapValuses);
		if (obj == null) {
			log.debug("find(), 查询无结果！");
		}
		return obj;
	}

	public static <T> T find(Class<T> clazz, String whereSql, Object... whereParams) {
		T obj = single(clazz, whereSql, whereParams);
		if (obj == null) {
			log.debug("find(), 查询无结果！");
		}
		return obj;
	}

	public static <T> T find2(Class<T> clazz, String whereSql) {
		T obj = single2(clazz, whereSql);
		if (obj == null) {
			log.debug("find(), 查询无结果！");
		}
		return obj;
	}

	public static <T> List<T> search(Class<T> clazz, Map<String, Object> values) {
		List<T> list = query(clazz, values);
		if (list == null || list.isEmpty()) {
			log.debug("search(), 查询无结果！");
		}
		return list;
	}

	public static <T> List<T> search(Class<T> clazz, String whereSql, Object... whereParams) {
		List<T> list = query(clazz, whereSql, whereParams);
		if (list == null || list.isEmpty()) {
			log.debug("search(), 查询无结果！");
		}
		return list;
	}

	public static <T> List<T> searchAll(Class<T> clazz) {
		List<T> list = query(clazz, new HashMap<String, Object>());
		if (list == null || list.isEmpty()) {
			log.debug("search(), 查询无结果！");
		}
		return list;
	}

	public static <T> List<T> searchBySql(Class<T> clazz, String sql, Object... whereParams) {
		Map<String, Field> queryFields = getQueryFields(clazz);
		Table table = clazz.getAnnotation(Table.class);
		log.debug("query(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		List<T> retObj = null;
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql);
			int index = 1;
			for (Object value : whereParams) {
				setParameter(stmt, index++, value);
			}
			rs = stmt.executeQuery();
			retObj = toObjectList(rs, clazz, queryFields);
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
		return retObj;
	}

	public static <T> List<T> search(Class<T> clazz, Object[] values) {
		Map<String, Object> mapValuses = new HashMap<String, Object>();
		for (int i = 0; i < values.length; i += 2) {
			mapValuses.put("" + values[i], values[i + 1]);
		}
		List<T> list = query(clazz, mapValuses);
		if (list == null || list.isEmpty()) {
			log.debug("search(), 查询无结果！");
		}
		return list;
	}

	private static <T> List<T> query(Class<T> clazz, Map<String, Object> values) {
		Map<String, Field> queryFields = getQueryFields(clazz);
		String queryFieldsSql = getQueryFiledsSql(queryFields);
		Table table = clazz.getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("SELECT ").append(queryFieldsSql);
		sql.append(" FROM ").append(table.name());
		Set<String> fieldNames = values.keySet();
		StringBuilder whereSql = new StringBuilder();
		for (String fieldName : fieldNames) {
			whereSql.append(" AND ").append(fieldName).append("=?");
		}
		if (!values.isEmpty()) {
			sql.append(" WHERE").append(whereSql.substring(4));
		}
		log.debug("query(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		List<T> retObj = null;
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			for (String fieldName : fieldNames) {
				setParameter(stmt, index++, values.get(fieldName));
			}
			rs = stmt.executeQuery();
			retObj = toObjectList(rs, clazz, queryFields);
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
		return retObj;
	}

	public static <T> List<T> query(Class<T> clazz, String whereSql, Object[] whereParams) {
		Map<String, Field> queryFields = getQueryFields(clazz);
		String queryFieldsSql = getQueryFiledsSql(queryFields);
		Table table = clazz.getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("SELECT ").append(queryFieldsSql);
		sql.append(" FROM ").append(table.name() + " T1");
		if (whereSql != null && !whereSql.isEmpty()) {
			sql.append(" WHERE ").append(whereSql);
		}
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		List<T> retObj = null;
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			StringBuilder sbWhereValue = new StringBuilder();
			if (whereParams != null && whereParams.length > 0) {
				for (Object whereValue : whereParams) {
					setParameter(stmt, index++, whereValue);
					sbWhereValue.append(", " + whereValue);
				}
			}
			log.debug("query(), sql = " + sql + sbWhereValue);
			rs = stmt.executeQuery();
			retObj = toObjectList(rs, clazz, queryFields);
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
		return retObj;
	}

	private static <T> T single(Class<T> clazz, Map<String, Object> values) {
		Map<String, Field> queryFields = getQueryFields(clazz);
		String queryFieldsSql = getQueryFiledsSql(queryFields);
		Table table = clazz.getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("SELECT ").append(queryFieldsSql);
		sql.append(" FROM ").append(table.name());
		Set<String> fieldNames = values.keySet();
		StringBuilder whereSql = new StringBuilder();
		for (String fieldName : fieldNames) {
			whereSql.append(" AND ").append(fieldName).append("=?");
		}
		sql.append(" WHERE").append(whereSql.substring(4));
		log.debug("findById(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		T retObj = null;
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			for (String fieldName : fieldNames) {
				setParameter(stmt, index++, values.get(fieldName));
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				retObj = toObject(rs, clazz, queryFields);
			}
		} catch (Exception e) {
			log.error(e);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
		return retObj;
	}

	private static <T> T single2(Class<T> clazz, String whereSql) {
		Map<String, Field> queryFields = getQueryFields(clazz);
		String queryFieldsSql = getQueryFiledsSql(queryFields);
		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			throw new RuntimeException(Table.class.getName() + " 没有设置 Table 注解。");
		}
		StringBuilder sql = new StringBuilder("SELECT ").append(queryFieldsSql);
		sql.append(" FROM ").append(table.name());
		if (whereSql != null && !whereSql.isEmpty()) {
			sql.append(" WHERE ").append(whereSql);
		}
		log.debug("single(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		T retObj = null;
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());

			rs = stmt.executeQuery();
			if (rs.next()) {
				retObj = toObject(rs, clazz, queryFields);
			}
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
		return retObj;
	}

	private static <T> T single(Class<T> clazz, String whereSql, Object... whereParams) {
		Map<String, Field> queryFields = getQueryFields(clazz);
		String queryFieldsSql = getQueryFiledsSql(queryFields);
		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			throw new RuntimeException(Table.class.getName() + " 没有设置 Table 注解。");
		}
		StringBuilder sql = new StringBuilder("SELECT ").append(queryFieldsSql);
		sql.append(" FROM ").append(table.name());
		if (whereSql != null && !whereSql.isEmpty()) {
			sql.append(" WHERE ").append(whereSql);
		}
		log.debug("single(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		T retObj = null;
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			for (Object fieldValue : whereParams) {
				setParameter(stmt, index++, fieldValue);
			}
			rs = stmt.executeQuery();
			if (rs.next()) {
				retObj = toObject(rs, clazz, queryFields);
			}
		} catch (Exception e) {
			log.error(e.toString());
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
		return retObj;
	}

	public static int save(Object o) throws Exception {
		Map<String, Object> saveFields = getSaveFields(o);
		Set<String> fieldNames = saveFields.keySet();
		Table table = o.getClass().getAnnotation(Table.class);
		GenerationType gvType = getIdGenerationType(o.getClass());
		String idField = getIdFieldName(o.getClass());
		StringBuilder sql = new StringBuilder("INSERT INTO ").append(table.name()).append("(");
		StringBuilder fieldNameStr = new StringBuilder();
		StringBuilder fieldValueStr = new StringBuilder();
		if (gvType == GenerationType.IDENTITY) {
			fieldNames.remove(idField.toLowerCase());
		}
		for (String fieldName : fieldNames) {
			fieldNameStr.append(", ").append(fieldName);
			fieldValueStr.append(", ?");
		}
		sql.append(fieldNameStr.substring(2));
		sql.append(") VALUES(").append(fieldValueStr.substring(2)).append(")");
		log.debug("save(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			for (String fieldName : fieldNames) {
				setParameter(stmt, index++, saveFields.get(fieldName));
			}
			return stmt.executeUpdate();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
	}

	/**
	 * 根据主键更新数据库记录。
	 * 
	 * @param o       数据对象
	 * @param idValue 主键值
	 * @return
	 * @throws Exception
	 */
	public static int update(Object o, Object idValue) throws Exception {
		return update(o, idValue, null);
	}

	/**
	 * 更新指定字段。
	 * 
	 * @param o
	 * @param idValue
	 * @param fieldNames 指定字段
	 * @return
	 * @throws Exception
	 */
	public static int updateExact(Object o, Object idValue, String fieldNameStr) throws Exception {
		Map<String, Object> saveFields = getSaveFields(o);
		String idField = getIdFieldName(o.getClass());
		Table table = o.getClass().getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("UPDATE ").append(table.name()).append(" SET ");
		StringBuilder fieldsStr = new StringBuilder();
		String[] fieldNames = fieldNameStr.toLowerCase().split(",");
		for (String fieldName : fieldNames) {
			fieldsStr.append(", ").append(fieldName.toUpperCase()).append("=?");
		}
		sql.append(fieldsStr.substring(2));
		sql.append(" WHERE ").append(idField).append("=?");
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			StringBuilder parameterStr = new StringBuilder();
			for (String fieldName : fieldNames) {
				setParameter(stmt, index++, saveFields.get(fieldName));
				parameterStr.append(", " + fieldName + "=" + saveFields.get(fieldName));
			}
			setParameter(stmt, index++, idValue);
			log.debug("update(), sql = " + sql + parameterStr + ", keyValue=" + idValue);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
	}

	/**
	 * 根据主键更新数据库记录。
	 * 
	 * @param o            数据对象
	 * @param idValue      主键值
	 * @param ignoreFields 更新时需要忽略的字段
	 * @return
	 * @throws Exception
	 */
	public static int update(Object o, Object idValue, String[] ignoreFields) throws Exception {
		Map<String, Object> saveFields = getSaveFields(o);
		String idField = getIdFieldName(o.getClass());
		Set<String> fieldNames = saveFields.keySet();
		Table table = o.getClass().getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("UPDATE ").append(table.name()).append(" SET ");
		StringBuilder fieldsStr = new StringBuilder();
		// 移除主键
		fieldNames.remove(idField.toLowerCase());
		// 移除不需要更新的字段
		if (ignoreFields != null && ignoreFields.length > 0) {
			for (String fieldName : ignoreFields) {
				fieldNames.remove(fieldName.toLowerCase());
			}
		}
		for (String fieldName : fieldNames) {
			fieldsStr.append(", ").append(fieldName).append("=?");
		}
		sql.append(fieldsStr.substring(2));
		sql.append(" WHERE ").append(idField).append("=?");
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			StringBuilder parameterStr = new StringBuilder();
			for (String fieldName : fieldNames) {
				setParameter(stmt, index++, saveFields.get(fieldName));
				parameterStr.append(", " + fieldName + "=" + saveFields.get(fieldName));
			}
			log.debug("update(), sql = " + sql + ", " + parameterStr);
			setParameter(stmt, index++, idValue);
			return stmt.executeUpdate();
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
	}

	/***** 自定义修改 ********/
	public static int updateSql(String sql) {
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection();
			stmt = conn.prepareStatement(sql.toString());
			stmt.executeUpdate();
			return stmt.executeUpdate();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				conn.close();
			}
		}
		return 0;
	}

	public static int deleteAll(Class<?> clazz, String whereSql, Object... whereParams) {
		Table table = clazz.getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(table.name());
		sql.append(" WHERE ").append(whereSql);
		log.debug("delete(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			StringBuilder sbWhereValue = new StringBuilder();
			if (whereParams != null && whereParams.length > 0) {
				int index = 1;
				for (Object whereValue : whereParams) {
					setParameter(stmt, index++, whereValue);
					sbWhereValue.append(", " + whereValue);
				}
			}
			log.debug("query(), sql = " + sql + sbWhereValue);
			return stmt.executeUpdate();
		} catch (Exception e) {
			log.error(e);
			return -1;
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
	}

	public static int delete(Class<?> clazz, Object id) {
		String idField = getIdFieldName(clazz);
		Table table = clazz.getAnnotation(Table.class);
		StringBuilder sql = new StringBuilder("DELETE FROM ").append(table.name());
		sql.append(" WHERE ").append(idField).append("=?");
		log.debug("delete(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(table.catalog());
			stmt = conn.prepareStatement(sql.toString());
			setParameter(stmt, 1, id);
			return stmt.executeUpdate();
		} catch (Exception e) {
			log.error(e);
			return -1;
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(table.catalog(), conn);
			}
		}
	}

	public static String getIdFieldName(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			Column column = field.getAnnotation(Column.class);
			if (id != null) {
				return column == null ? field.getName().toUpperCase() : column.name();
			}
		}
		throw new RuntimeException("没有定义主键字段，entity class = " + clazz.getName());
	}

	public static GenerationType getIdGenerationType(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Id id = field.getAnnotation(Id.class);
			if (id != null) {
				GeneratedValue gv = field.getAnnotation(GeneratedValue.class);
				if (gv == null) {
					throw new RuntimeException("主键策略未定义，entity class =  " + clazz.getName());
				}
				return gv.strategy();
			}
		}
		throw new RuntimeException("没有定义主键字段，entity class = " + clazz.getName());
	}

	public static Map<String, Field> getQueryFields(Class<?> clazz) {
		Map<String, Field> queryFields = new HashMap<String, Field>();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			Transient t = field.getAnnotation(Transient.class);
			if (t != null) {
				continue;
			}
			if (column == null) {
				queryFields.put(field.getName().toLowerCase(), field);
			} else {
				queryFields.put(column.name().toLowerCase(), field);
			}
		}
		return queryFields;
	}

	public static Map<String, Object> getSaveFields(Object o) throws Exception {
		Map<String, Object> saveFields = new HashMap<String, Object>();

		Field[] fields = o.getClass().getDeclaredFields();
		for (Field field : fields) {
			Column column = field.getAnnotation(Column.class);
			Transient t = field.getAnnotation(Transient.class);
			if (t != null) {
				continue;
			}
			String getMethodName = "get" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
			Object value = o.getClass().getMethod(getMethodName).invoke(o);
			if (column == null) {
				saveFields.put(field.getName().toLowerCase(), value);
			} else {
				saveFields.put(column.name().toLowerCase(), value);
			}
		}
		return saveFields;
	}

	public static String getTableName(Class<?> clazz) {
		Table table = clazz.getAnnotation(Table.class);
		if (table == null) {
			log.error("Entry name " + clazz.getName() + " 没有指定表名");
			throw new RuntimeException("Entry name " + clazz.getName() + " 没有指定表名");
		}
		return table.name();
	}

	public static String getQueryFiledsSql(Map<String, Field> queryFields) {
		StringBuilder sql = new StringBuilder();
		for (String fieldName : queryFields.keySet()) {
			sql.append(", ").append(fieldName);
		}
		return sql.toString().substring(2);
	}

	public static String getDBValue(Object val) {
		if (val instanceof Integer || val instanceof Double) {
			return val + "";
		}
		return "'" + val + "'";
	}

	public static <T> T toObject(RecordSet rs, Class<T> clazz) {
		try {
			T o = clazz.newInstance();
			Method[] methods = clazz.getMethods();
			for (Method m : methods) {
				if (m.getName().startsWith("set")) {
					if (m.getParameterTypes()[0].equals(Integer.class)) {
						m.invoke(o, CustomUtil.getInteger(rs.getString(m.getName().substring(3)), null));
					} else if (m.getParameterTypes()[0].equals(Double.class)) {
						m.invoke(o, CustomUtil.getDouble(rs.getString(m.getName().substring(3)), null));
					} else if (m.getParameterTypes()[0].equals(String.class)) {
						m.invoke(o, rs.getString(m.getName().substring(3)));
					}
				}
			}
			return o;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static <T> List<T> toObjectList(RecordSet rs, Class<T> clazz) {
		List<T> list = new ArrayList<T>();
		while (rs.next()) {
			list.add(toObject(rs, clazz));
		}
		return list;
	}

	public static <T> T toObject(ResultSet rs, Class<T> clazz, Map<String, Field> queryFields) {
		try {
			T o = clazz.newInstance();
			int colCount = rs.getMetaData().getColumnCount();
			for (int i = 0; i < colCount; i++) {
				String colName = rs.getMetaData().getColumnName(i + 1);
				Field field = queryFields.get(colName.toLowerCase());
				String setMethodName = "set" + field.getName().substring(0, 1).toUpperCase()
						+ field.getName().substring(1);
				if (field.getType().equals(Integer.class)) {
					clazz.getMethod(setMethodName, field.getType()).invoke(o,
							CustomUtil.getInteger(rs.getString(colName), null));
				} else if (field.getType().equals(Double.class)) {
					clazz.getMethod(setMethodName, field.getType()).invoke(o,
							CustomUtil.getDouble(rs.getString(colName), null));
				} else if (field.getType().equals(String.class)) {
					clazz.getMethod(setMethodName, field.getType()).invoke(o, rs.getString(colName));
				}
			}
			return o;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}

	public static <T> List<T> toObjectList(ResultSet rs, Class<T> clazz, Map<String, Field> queryFields) {
		List<T> list = new ArrayList<T>();
		try {
			while (rs.next()) {
				list.add(toObject(rs, clazz, queryFields));
			}
			return list;
		} catch (Exception e) {
			log.error(e);
		}
		list.clear();
		return list;
	}

	private static void setParameter(PreparedStatement stmt, int index, Object value) throws SQLException {
		if (value == null) {
			stmt.setNull(index, Types.NULL);
		} else if (value instanceof String) {
			stmt.setString(index, (String) value);
		} else if (value instanceof Integer) {
			stmt.setInt(index, (Integer) value);
		} else if (value instanceof Double) {
			stmt.setDouble(index, (Double) value);
		} else if (value instanceof byte[]) {
			stmt.setBytes(index, (byte[]) value);
		} else if (value instanceof Boolean) {
			stmt.setBoolean(index, (Boolean) value);
		} else {
			throw new SQLException("使用了没有处理过的字段类型 - " + value.getClass().getName());
		}
	}

	public static Map<String, String> selectOne(String sql) {
		RecordSet rs = new RecordSet();
		Map<String, String> row = new HashMap<String, String>();
		if (rs.execute(sql) && rs.next()) {
			String[] names = rs.getColumnName();
			for (String name : names) {
				row.put(name, rs.getString(name));
			}
		}
		return row;
	}

	public static List<Map<String, String>> selectList(String sql) {
		RecordSet rs = new RecordSet();
		List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
		if (rs.execute(sql)) {
			String[] names = rs.getColumnName();
			while (rs.next()) {
				Map<String, String> row = new HashMap<String, String>();
				for (String name : names) {
					row.put(name, rs.getString(name));
				}
				rows.add(row);
			}
		}
		return rows;
	}

	public static int executeUpdate(String sql, Object... params) throws SQLException {
		log.debug("executeUpdate(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		int i = 0;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(GCONST.getServerName());
			stmt = conn.prepareStatement(sql.toString());
			int index = 1;
			for (Object value : params) {
				setParameter(stmt, index++, value);
			}
			i = stmt.executeUpdate();
			log.info(">>>" + i);
			return i;
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(GCONST.getServerName(), conn);
			}
		}

	}

	public static int getInt(String sql) throws Exception {
		return getInt(sql, "ecology");
	}

	public static int getIntStarts(String sql) throws Exception {
		log.debug("getInteger(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection("ecology");
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return 1;
			}
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection("ecology", conn);
			}
		}
		return 0;
	}

	public static int getInt(String sql, String dsName) throws Exception {
		log.debug("getInteger(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(dsName);
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
			throw new RuntimeException("取值失败，sql = " + sql);
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(dsName, conn);
			}
		}
	}

	public static List<Map<String, String>> executeQuery(String listSql, String[] fieldArr) throws Exception {
		return executeQuery(listSql, fieldArr, "ecology");
	}

	public static List<Map<String, String>> executeQuery(String listSql, String[] fieldArr, String dsName)
			throws Exception {
		log.debug("executeQuery(), sql = " + listSql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection(dsName);
			stmt = conn.prepareStatement(listSql);
			ResultSet rs = stmt.executeQuery();
			if (fieldArr == null || fieldArr.length == 0) {
				ResultSetMetaData rsmd = rs.getMetaData();
				int colCount = rsmd.getColumnCount();
				fieldArr = new String[colCount];
				for (int i = 0; i < colCount; i++) {
					// 第一列是 1，第二个列是 2
					fieldArr[i] = rs.getMetaData().getColumnName(i + 1);
				}
			}
			List<Map<String, String>> records = new ArrayList<Map<String, String>>();
			while (rs.next()) {
				Map<String, String> record = new HashMap<String, String>();
				for (String name : fieldArr) {
					record.put(name, rs.getString(name));
				}
				records.add(record);
			}
			return records;
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection(dsName, conn);
			}
		}
	}

	public static String getString(String tableName, String fieldName, String whereSql, Object... whereValues)
			throws SQLException {
		String sql = String.format("SELECT %s FROM %s", fieldName, tableName);
		if (CustomUtil.isNotBlank(whereSql)) {
			sql += " WHERE " + whereSql;
		}
		log.debug("getString(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection("ecology");
			stmt = conn.prepareStatement(sql);
			if (whereValues != null && whereValues.length > 0) {
				for (int i = 0; i < whereValues.length; i++) {
					setParameter(stmt, (i + 1), whereValues[i]);
				}
			}
			ResultSet rs = stmt.executeQuery();
			return rs.next() ? rs.getString(1) : "";
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection("ecology", conn);
			}
		}
	}

	/**
	 * 查询某个表某个字段的值，返回整型
	 * 
	 * @param tableName   表名
	 * @param fieldName   要返回值的字段名
	 * @param whereSql    查询条件
	 * @param whereValues 查询条件值
	 * @return
	 * @throws SQLException
	 */
	public static Integer getInteger(String tableName, String fieldName, String whereSql, Object... whereValues)
			throws SQLException {
		String sql = String.format("SELECT %s FROM %s", fieldName, tableName);
		if (CustomUtil.isNotBlank(whereSql)) {
			sql += " WHERE " + whereSql;
		}
		log.debug("getString(), sql = " + sql);
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection("ecology");
			stmt = conn.prepareStatement(sql);
			if (whereValues != null && whereValues.length > 0) {
				for (int i = 0; i < whereValues.length; i++) {
					setParameter(stmt, (i + 1), whereValues[i]);
				}
			}
			ResultSet rs = stmt.executeQuery();
			return rs.next() ? CustomUtil.getInteger(rs.getString(1)) : null;
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection("ecology", conn);
			}
		}
	}

	public static void insert(String sql) {
		WeaverConnection conn = null;
		PreparedStatement stmt = null;
		ConnectionPool pool = ConnectionPool.getInstance();
		try {
			conn = pool.getConnection("ecology");
			stmt = conn.prepareStatement(sql);
			ResultSet res = stmt.executeQuery();
			log.info(res);
		} catch (Exception e) {
			log.info(e);
		} finally {
			if (stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (conn != null) {
				pool.returnConnection("ecology", conn);
			}
		}
	}

}
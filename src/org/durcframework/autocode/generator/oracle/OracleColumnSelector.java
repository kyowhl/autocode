package org.durcframework.autocode.generator.oracle;

import java.util.Map;
import java.util.Set;

import org.durcframework.autocode.generator.ColumnDefinition;
import org.durcframework.autocode.generator.ColumnSelector;
import org.durcframework.autocode.generator.DataBaseConfig;
import org.springframework.util.StringUtils;

/**
 * mysql表信息查询
 * 
 */
public class OracleColumnSelector extends ColumnSelector {

	public OracleColumnSelector(DataBaseConfig dataBaseConfig) {
		super(dataBaseConfig);
	}

	/**
	 * SHOW FULL COLUMNS FROM 表名
	 */
	@Override
	protected String getColumnInfoSQL(String tableName) {
		return "SELECT utc.TABLE_NAME,utc.COLUMN_NAME,utc.DATA_TYPE,utc.DATA_LENGTH,utc.DATA_PRECISION,utc.DATA_SCALE,utc.NULLABLE,ucc.COMMENTS,b.CONSTRAINT_TYPE FROM USER_TAB_COLUMNS utc,USER_COL_COMMENTS ucc,(select ucco.TABLE_NAME,ucco.COLUMN_NAME,uc.CONSTRAINT_TYPE from USER_CONS_COLUMNS ucco,USER_CONSTRAINTS uc where uc.TABLE_NAME='"
				+ tableName
				+ "' and uc.CONSTRAINT_TYPE='P' and uc.TABLE_NAME=ucco.TABLE_NAME and uc.CONSTRAINT_NAME=ucco.CONSTRAINT_NAME ) b WHERE utc.TABLE_NAME='"
				+ tableName
				+ "' AND utc.TABLE_NAME=ucc.TABLE_NAME AND utc.COLUMN_NAME=ucc.COLUMN_NAME AND utc.TABLE_NAME=b.TABLE_NAME(+) AND utc.COLUMN_NAME=b.COLUMN_NAME(+) ORDER BY utc.COLUMN_ID";
	}

	/*
	 * {FIELD=username, EXTRA=, COMMENT=用户名, COLLATION=utf8_general_ci,
	 * PRIVILEGES=select,insert,update,references, KEY=PRI, NULL=NO,
	 * DEFAULT=null, TYPE=varchar(20)}
	 */
	protected ColumnDefinition buildColumnDefinition(Map<String, Object> rowMap) {
		Set<String> columnSet = rowMap.keySet();

		for (String columnInfo : columnSet) {
			rowMap.put(columnInfo.toUpperCase(), rowMap.get(columnInfo));
		}

		ColumnDefinition columnDefinition = new ColumnDefinition();

		columnDefinition.setColumnName((String) rowMap.get("COLUMN_NAME"));

		columnDefinition.setIsIdentity(false);
		boolean isPk = "P".equalsIgnoreCase((String) rowMap
				.get("CONSTRAINT_TYPE"));
		columnDefinition.setIsPk(isPk);
		String type = (String) rowMap.get("DATA_TYPE");
		columnDefinition.setType(type);
		String length =rowMap.get("DATA_LENGTH").toString();
		String precision = rowMap.get("DATA_PRECISION")==null?"0":rowMap.get("DATA_PRECISION").toString();
		String scale = rowMap.get("DATA_SCALE")==null?"0":rowMap.get("DATA_SCALE").toString();
		columnDefinition.setCheckType(buildCheckType(type));
		columnDefinition.setLength(length);
		columnDefinition.setPrecision(precision);
		columnDefinition.setScale(scale);
		String comments = (String) rowMap.get("COMMENTS");
		columnDefinition.setComment(buildCOMMENTS(comments));
		String isNull = (String) rowMap.get("NULLABLE");
		columnDefinition.setIsNull(buildIsNull(isNull));
		
		return columnDefinition;
	}

	// 将‘地区编号(DQBH)’转换成‘地区编号’
	private String buildCOMMENTS(String comments) {
		if (StringUtils.hasText(comments)) {
			int index = comments.indexOf("(");
			if (index > 0) {
				return comments.substring(0, index);
			}
			return comments;
		}
		return comments;
	}

	// 将数据库类型另存为检查类型
	private String buildCheckType(String type) {
		String checkType = "C";
		if (StringUtils.hasText(type)) {
			if ("NUMBER".equals(type)) {
				checkType = "N";
			} else if ("DATE".equals(type)) {
				checkType = "D";
			}
			return checkType;
		}
		return checkType;
	}

	// 将字段是否为空转换为boolean
	private String buildIsNull(String isNull) {
		if (StringUtils.hasText(isNull)) {
			if ("N".equals(isNull)) {
				return "1";
			}
			return "2";
		}
		return "2";
	}
}

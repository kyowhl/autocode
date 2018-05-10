package org.durcframework.autocode.generator.oracle;

import java.util.Map;

import org.durcframework.autocode.generator.ColumnSelector;
import org.durcframework.autocode.generator.DataBaseConfig;
import org.durcframework.autocode.generator.TableDefinition;
import org.durcframework.autocode.generator.TableSelector;
import org.springframework.util.StringUtils;

/**
 * 查询mysql数据库表
 */
public class OracleTableSelector extends TableSelector {

	public OracleTableSelector(ColumnSelector columnSelector,
			DataBaseConfig dataBaseConfig) {
		super(columnSelector, dataBaseConfig);
	}

	@Override
	protected String getShowTablesSQL(String dbName) {
		String sql = "SELECT TABLE_NAME,COMMENTS from USER_TAB_COMMENTS ";
		if (this.getSchTableNames() != null
				&& this.getSchTableNames().size() > 0) {
			StringBuilder tables = new StringBuilder();
			for (String table : this.getSchTableNames()) {
				tables.append(",'").append(table).append("'");
			}
			sql += " WHERE TABLE_NAME IN (" + tables.substring(1) + ")";
		}
		return sql;
	}

	@Override
	protected TableDefinition buildTableDefinition(Map<String, Object> tableMap) {
		TableDefinition tableDefinition = new TableDefinition();
		tableDefinition.setTableName((String) tableMap.get("TABLE_NAME"));
		tableDefinition.setComment(buildCOMMENTS((String) tableMap.get("COMMENTS")));
		return tableDefinition;
	}

	// 将‘放款明细表(F_BL_FKMX)’转换成‘放款明细’
	private String buildCOMMENTS(String comments) {
		if (StringUtils.hasText(comments)) {
			int index = comments.indexOf("表");
			if (index > 0) {
				return comments.substring(0, index);
			}
			index = comments.indexOf("(");
			if (index > 0) {
				return comments.substring(0, index);
			}
			return comments;
		}
		return comments;
	}
}

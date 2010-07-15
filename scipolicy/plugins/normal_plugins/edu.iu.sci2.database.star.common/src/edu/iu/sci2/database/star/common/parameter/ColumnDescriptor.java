package edu.iu.sci2.database.star.common.parameter;

import edu.iu.cns.database.load.framework.DerbyFieldType;

public class ColumnDescriptor {
	private int columnIndex;
	private String name;
	private String databaseName;
	private DerbyFieldType type;
	private boolean isCoreColumn;
	private boolean isMultiValued;
	private boolean shouldMergeIdenticalValues;
	private String separator;

	public ColumnDescriptor(int columnIndex, String name) {
		this(columnIndex, name, "", DerbyFieldType.VARCHAR, true, false, false, "");
	}

	public ColumnDescriptor(
			int columnIndex,
			String name,
			String databaseName,
			DerbyFieldType type,
			boolean isCoreColumn,
			boolean isMultiValued,
			boolean shouldMergeIdenticalValues,
			String separator) {
		this.columnIndex = columnIndex;
		this.name = name;
		this.databaseName = databaseName;
		this.type = type;
		this.isCoreColumn = isCoreColumn;
		this.isMultiValued = isMultiValued;
		this.shouldMergeIdenticalValues = shouldMergeIdenticalValues;
		this.separator = separator;
	}

	public int getColumnIndex() {
		return this.columnIndex;
	}

	public String getName() {
		return this.name;
	}

	public String getNameForDatabase() {
		return this.databaseName;
	}

	public DerbyFieldType getType() {
		return this.type;
	}

	public boolean isCoreColumn() {
		return this.isCoreColumn;
	}

	public boolean isMultiValued() {
		return this.isMultiValued;
	}

	public boolean shouldMergeIdenticalValues() {
		return this.shouldMergeIdenticalValues;
	}

	public String getSeparator() {
		return this.separator;
	}
}
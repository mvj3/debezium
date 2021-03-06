/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache Software License version 2.0, available at http://www.apache.org/licenses/LICENSE-2.0
 */
package io.debezium.relational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class TableImpl implements Table {

    private final TableId id;
    private final List<Column> columnDefs;
    private final List<String> pkColumnNames;
    private final List<String> columnNames;
    private final Map<String, Column> columnsByLowercaseName;

    protected TableImpl(Table table) {
        this(table.id(), table.columns(), table.primaryKeyColumnNames());
    }

    protected TableImpl(TableId id, List<Column> sortedColumns, List<String> pkColumnNames) {
        this.id = id;
        this.columnDefs = Collections.unmodifiableList(sortedColumns);
        this.pkColumnNames = pkColumnNames == null ? Collections.emptyList() : Collections.unmodifiableList(pkColumnNames);
        Map<String, Column> defsByLowercaseName = new LinkedHashMap<>();
        List<String> columnNames = new ArrayList<>();
        for (Column def : this.columnDefs) {
            defsByLowercaseName.put(def.name().toLowerCase(), def);
            columnNames.add(def.name());
        }
        this.columnsByLowercaseName = Collections.unmodifiableMap(defsByLowercaseName);
        this.columnNames = Collections.unmodifiableList(columnNames);
    }
    
    @Override
    public TableId id() {
        return id;
    }

    @Override
    public List<String> primaryKeyColumnNames() {
        return pkColumnNames;
    }

    @Override
    public List<String> columnNames() {
        return columnNames;
    }

    @Override
    public List<Column> columns() {
        return columnDefs;
    }

    @Override
    public Column columnWithName(String name) {
        return columnsByLowercaseName.get(name.toLowerCase());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toString(sb, "");
        return sb.toString();
    }

    protected void toString(StringBuilder sb, String prefix) {
        if (prefix == null) prefix = "";
        sb.append(prefix).append("columns: {").append(System.lineSeparator());
        for (Column defn : columnDefs) {
            sb.append(prefix).append("  ").append(defn).append(System.lineSeparator());
        }
        sb.append(prefix).append("}").append(System.lineSeparator());
        sb.append(prefix).append("primary key: ").append(primaryKeyColumnNames()).append(System.lineSeparator());
    }

    @Override
    public TableEditor edit() {
        return new TableEditorImpl().tableId(id).setColumns(columnDefs).setPrimaryKeyNames(pkColumnNames);
    }
}
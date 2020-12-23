package org.sqlcomponents.core.model.relational;

import lombok.Getter;
import lombok.Setter;
import org.sqlcomponents.core.model.relational.enumeration.Flag;
import org.sqlcomponents.core.model.relational.enumeration.TableType;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Getter
public class Table {

    private final Database database;
    private String tableName;
    private String sequenceName;
    private String categoryName;
    private String schemaName;
    private TableType tableType;
    private String remarks;
    private String categoryType;
    private String schemaType;
    private String nameType;
    private String selfReferencingColumnName;
    private String referenceGeneration;
    private List<Column> columns;
    private List<Index> indices;

    public Table(final Database database) {
        this.database = database;
    }


    public boolean getHasPrimaryKey() {
        return this.getColumns().stream()
                .filter(column -> column.getPrimaryKeyIndex() != 0)
                .findFirst().isPresent();
    }

    public boolean getHasAutoGeneratedPrimaryKey() {
        return this.getColumns().stream()
                .filter(column -> column.getPrimaryKeyIndex() != 0 && column.getAutoIncrement() == Flag.YES)
                .findFirst().isPresent();
    }

    public String getEscapedName() {
        return this.database.escapedName(this.getTableName());
    }

    public int getHighestPKIndex() {
        int highestPKIndex = 0;
        for (Column column : columns) {
            if (highestPKIndex < column.getPrimaryKeyIndex()) {
                highestPKIndex = column.getPrimaryKeyIndex();
            }
        }
        return highestPKIndex;
    }

    public List<String> getUniqueConstraintGroupNames() {
        List<String> uniqueConstraintGroupNames = new ArrayList<String>();
        String prevUniqueConstraintGroupName = null;
        String uniqueConstraintGroupName = null;
        for (Column column : columns) {
            uniqueConstraintGroupName = column.getUniqueConstraintName();
            if (uniqueConstraintGroupName != null
                    && !uniqueConstraintGroupName.equals(prevUniqueConstraintGroupName)) {
                uniqueConstraintGroupNames.add(uniqueConstraintGroupName);
                prevUniqueConstraintGroupName = uniqueConstraintGroupName;
            }
        }
        return uniqueConstraintGroupNames;
    }

    public SortedSet<String> getDistinctCustomColumnTypeNames() {
        SortedSet<String> distinctColumnTypeNames = new TreeSet<>();

        distinctColumnTypeNames.addAll(columns
                .stream()
                .filter(column -> Table.class.getResource("/template/java/custom-object/"+column.getTypeName().toLowerCase()+".ftl") != null )
                .map(column -> column.getTypeName()).distinct()
                .collect(Collectors.toList()));


        return distinctColumnTypeNames;
    }

    public SortedSet<String> getDistinctColumnTypeNames() {
        SortedSet<String> distinctColumnTypeNames = new TreeSet<>();

        distinctColumnTypeNames.addAll(columns
                .stream()
                .map(column -> column.getTypeName()).distinct()
                .collect(Collectors.toList()));


        return distinctColumnTypeNames;
    }

    @Override
    public String toString() {
        return tableName + "::" + tableType;
    }
}

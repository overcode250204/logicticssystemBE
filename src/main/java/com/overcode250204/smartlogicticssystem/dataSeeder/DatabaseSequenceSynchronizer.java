package com.overcode250204.smartlogicticssystem.dataSeeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseSequenceSynchronizer {

    private static final String SEQUENCE_COLUMNS_SQL = """
            SELECT
                c.table_schema,
                c.table_name,
                c.column_name,
                pg_get_serial_sequence(format('%I.%I', c.table_schema, c.table_name), c.column_name) AS sequence_name
            FROM information_schema.columns c
            WHERE c.table_schema = current_schema()
              AND pg_get_serial_sequence(format('%I.%I', c.table_schema, c.table_name), c.column_name) IS NOT NULL
            """;

    private final JdbcTemplate jdbcTemplate;

    public void synchronize() {
        List<SequenceColumn> sequenceColumns = jdbcTemplate.query(
                SEQUENCE_COLUMNS_SQL,
                (rs, rowNum) -> new SequenceColumn(
                        rs.getString("table_schema"),
                        rs.getString("table_name"),
                        rs.getString("column_name"),
                        rs.getString("sequence_name")
                )
        );

        sequenceColumns.forEach(this::synchronizeSequence);
        log.info("Synchronized {} database sequences before seed data.", sequenceColumns.size());
    }

    private void synchronizeSequence(SequenceColumn sequenceColumn) {
        String tableReference = "%s.%s".formatted(
                quoteIdentifier(sequenceColumn.tableSchema()),
                quoteIdentifier(sequenceColumn.tableName())
        );
        String columnReference = quoteIdentifier(sequenceColumn.columnName());
        String sql = """
                SELECT setval(
                    ?::regclass,
                    GREATEST(COALESCE((SELECT MAX(%s) FROM %s), 0) + 1, 1),
                    false
                )
                """.formatted(columnReference, tableReference);

        try {
            Long nextValue = jdbcTemplate.queryForObject(sql, Long.class, sequenceColumn.sequenceName());
            log.debug(
                    "Synchronized sequence {} for {}.{} to next value {}.",
                    sequenceColumn.sequenceName(),
                    sequenceColumn.tableName(),
                    sequenceColumn.columnName(),
                    nextValue
            );
        } catch (Exception exception) {
            log.warn(
                    "Unable to synchronize sequence {} for {}.{}: {}",
                    sequenceColumn.sequenceName(),
                    sequenceColumn.tableName(),
                    sequenceColumn.columnName(),
                    exception.getMessage()
            );
        }
    }

    private String quoteIdentifier(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private record SequenceColumn(
            String tableSchema,
            String tableName,
            String columnName,
            String sequenceName
    ) {
    }
}

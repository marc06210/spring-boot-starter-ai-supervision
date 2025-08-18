package dev.mgu.ai.supervision.impl;

import dev.mgu.ai.supervision.TokenCounter;
import dev.mgu.ai.supervision.TokenCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

public class DataSourceCounter implements TokenCounterService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final JdbcTemplate jdbcTemplate;
    private final String SQL_SELECT_ALL = """
            SELECT ai_model, prompt_tokens, generation_tokens, total_tokens
            FROM token_table
            """;
    private final String SQL_SELECT_MODEL = """
            SELECT prompt_tokens, generation_tokens, total_tokens
            FROM token_table
            WHERE ai_model = ?
            """;

    private final String SQL_CREATE = """
            INSERT INTO token_table (ai_model, prompt_tokens, generation_tokens, total_tokens)
            VALUES (?, ?, ?, ?)
            """;

    private final String SQL_UPDATE = """
            UPDATE token_table 
            SET prompt_tokens = ?, generation_tokens = ?, total_tokens = ?
            WHERE ai_model = ?
            """;

    public DataSourceCounter(final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    @Override
    @Transactional
    public void incrementTokenCounters(TokenCounter tokenCounter) {
        TokenCounter dbData = getTokenCountersForModel(tokenCounter.model());
        if (dbData == null) {
            dbData  = new TokenCounter(tokenCounter.model(),
                    tokenCounter.promptTokens(),
                    tokenCounter.generationTokens(),
                    tokenCounter.totalTokens()
            );
            this.create(dbData);
        } else {
            dbData  = new TokenCounter(tokenCounter.model(),
                    dbData.promptTokens() + tokenCounter.promptTokens(),
                    dbData.generationTokens() + tokenCounter.generationTokens(),
                    dbData.totalTokens() + tokenCounter.totalTokens()
            );
            this.update(dbData);
        }
    }

    private void update(TokenCounter dbData) {
        int update = jdbcTemplate.update(SQL_UPDATE,
                dbData.promptTokens(),
                dbData.generationTokens(),
                dbData.totalTokens(),
                dbData.model());
        System.out.println("MGU >>> " + update + " rows updated");
    }

    private void create(TokenCounter dbData) {
        int update = jdbcTemplate.update(SQL_CREATE,
                dbData.model(),
                dbData.promptTokens(),
                dbData.generationTokens(),
                dbData.totalTokens());
        System.out.println("MGU >>> " + update + " rows created");
    }

    @Override
    public List<TokenCounter> getTokenCounters() {
        try {
            return loadTable();
        } catch(Exception e) {
            logger.error("Error while getting token counters", e);
            return Collections.emptyList();
        }
    }

    private List<TokenCounter> loadTable() throws DataAccessException {
        return jdbcTemplate.query(SQL_SELECT_ALL, (rs, row) ->
                new TokenCounter(rs.getString("ai_model"),
                        rs.getInt("prompt_tokens"),
                        rs.getInt("generation_tokens"),
                        rs.getInt("total_tokens")
                ));
    }

    @Override
    public void reset() {
        logger.debug("Resetting data");
        String createSQL = """
                delete from token_table;
                """;
        this.jdbcTemplate.execute(createSQL);
    }

    private TokenCounter getTokenCountersForModel(final String model) {
        try {
            return jdbcTemplate.queryForObject(SQL_SELECT_MODEL, (rs, rowNum) ->
                            new TokenCounter(model,
                                    rs.getInt("prompt_tokens"),
                                    rs.getInt("generation_tokens"),
                                    rs.getInt("total_tokens")
                            ),
                    model
            );
        } catch (EmptyResultDataAccessException eERDA) {
            return null;
        }
    }

    public void createSchema() {

        try {
            loadTable();
            return;
        } catch (DataAccessException e) {
            System.err.println(e);
        }
        logger.debug("Creating table if not exists");
        String createSQL = """
                create table token_table (
                    ai_model VARCHAR(255) NOT NULL UNIQUE,
                    prompt_tokens INT,
                    generation_tokens INT,
                    total_tokens INT
                );
                """;
        this.jdbcTemplate.execute(createSQL);
    }
}

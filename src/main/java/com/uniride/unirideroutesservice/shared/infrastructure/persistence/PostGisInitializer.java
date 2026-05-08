package com.uniride.unirideroutesservice.shared.infrastructure.persistence;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostGisInitializer {
    private final JdbcTemplate jdbcTemplate;

    public PostGisInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        // Habilita PostGIS automáticamente en la base de datos PostgreSQL
        jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS postgis;");
    }
}
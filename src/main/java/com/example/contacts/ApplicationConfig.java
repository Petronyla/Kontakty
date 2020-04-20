package com.example.contacts;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.mariadb.jdbc.MariaDbDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import com.example.contacts.entity.Contact;

@Configuration
public class ApplicationConfig {

    @Bean
    public RowMapper<Contact> contactRowMapper() {
        return BeanPropertyRowMapper.newInstance(Contact.class);
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        MariaDbDataSource dataSource = new MariaDbDataSource();
        dataSource.setUserName("student");
        dataSource.setPassword("password");
        dataSource.setUrl("jdbc:mysql://localhost:3306/SeznamKontaktu");
        return dataSource;
    }
}

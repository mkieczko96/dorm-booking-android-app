package com.dorm.booker.api;

import net.kaczmarzyk.spring.data.jpa.web.SpecificationArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
@ComponentScan({
        "com.dorm.booker.api.data.controllers",
        "com.dorm.booker.api.data.repositories",
        "com.dorm.booker.api.security.jwt",
        "com.dorm.booker.api.security",
        "com.dorm.booker.api"})
public class DormBookerApiApplication implements WebMvcConfigurer {

    private final String jdbcURL;

    public DormBookerApiApplication(@Value("${spring.datasource.url:url}") String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    public static void main(String[] args) {

        SpringApplication.run(DormBookerApiApplication.class, args);
    }

    @Bean(value = "datasource")
    @ConfigurationProperties("spring.datasource")
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url(jdbcURL)
                .build();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SpecificationArgumentResolver());
    }
}

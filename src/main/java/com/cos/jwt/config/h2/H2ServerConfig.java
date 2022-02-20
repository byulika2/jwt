package com.cos.jwt.config.h2;

import java.sql.SQLException;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class H2ServerConfig {

  @Bean
  public Server h2TcpServer() throws SQLException {
    return Server.createTcpServer().start();
  }
}

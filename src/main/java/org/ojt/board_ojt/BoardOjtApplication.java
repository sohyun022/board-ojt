package org.ojt.board_ojt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BoardOjtApplication {

    public static void main(String[] args) {
        SpringApplication.run(BoardOjtApplication.class, args);
    }

}

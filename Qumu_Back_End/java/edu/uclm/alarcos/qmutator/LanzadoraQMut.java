package edu.uclm.alarcos.qmutator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import edu.uclm.alarcos.qmutator.model.Manager;

@SpringBootApplication
public class LanzadoraQMut extends SpringBootServletInitializer {

	@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		Manager.get().loadOperators();
        return builder.sources(LanzadoraQMut.class);
    }

	public static void main(String[] args) throws Exception {
		Manager.get().loadOperators();
		SpringApplication.run(LanzadoraQMut.class, args);
	}

}

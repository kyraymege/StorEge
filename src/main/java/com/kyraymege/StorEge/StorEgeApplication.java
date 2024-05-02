package com.kyraymege.StorEge;

import com.kyraymege.StorEge.domain.RequestContext;
import com.kyraymege.StorEge.entity.Role;
import com.kyraymege.StorEge.enums.Authority;
import com.kyraymege.StorEge.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class StorEgeApplication {

	public static void main(String[] args) {
		SpringApplication.run(StorEgeApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(RoleRepository roleRepository){
		return args -> {
//			RequestContext.setUserId(0L);
//			var userRole = new Role();
//			userRole.setName(Authority.USER.name());
//			userRole.setAuthorities(Authority.USER);
//			roleRepository.save(userRole);
//
//			var adminRole = new Role();
//			adminRole.setName(Authority.ADMIN.name());
//			adminRole.setAuthorities(Authority.ADMIN);
//			roleRepository.save(adminRole);
//			RequestContext.start();
		};
	}

}

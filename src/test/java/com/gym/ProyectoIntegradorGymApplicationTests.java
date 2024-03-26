package com.gym;

import com.gym.entities.Rank;
import com.gym.enums.ERank;
import com.gym.enums.ERole;
import com.gym.repositories.RankRepository;
import com.gym.security.entities.RoleEntity;
import com.gym.security.entities.UserEntity;
import com.gym.security.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootTest
class ProyectoIntegradorGymApplicationTests {

//	@Test
//	void contextLoads() {
//	}
//
//	@Autowired
//	PasswordEncoder passwordEncoder;
//	@Autowired
//	UserRepository userRepository;
//
//	@Autowired
//	RankRepository rankRepository;
//
//	@Bean
//	CommandLineRunner init(){
//		return args -> {
//			UserEntity userEntity = UserEntity.builder()
//					.email("admin@mail.com")
//					.username("admin")
//					.password(passwordEncoder.encode("123456"))
//					.roles(Set.of(RoleEntity.builder()
//							.name(ERole.valueOf(ERole.ADMIN.name()))
//							.build()))
//					.build();
//
//			UserEntity userEntity2 = UserEntity.builder()
//					.email("user@mail.com")
//					.username("user")
//					.password(passwordEncoder.encode("123456"))
//					.roles(Set.of(RoleEntity.builder()
//							.name(ERole.valueOf(ERole.USER.name()))
//							.build()))
//					.build();
//
//			Rank rankEntity = Rank.builder()
//					.name(ERank.BRONZE)
//					.build();
//
//			Rank rankEntity2 = Rank.builder()
//					.name(ERank.GOLD)
//					.build();
//
//			Rank rankEntity3 = Rank.builder()
//					.name(ERank.PLATINUM)
//					.build();
//
//			Rank rankEntity4 = Rank.builder()
//					.name(ERank.SILVER)
//					.build();
//
//			rankRepository.save(rankEntity);
//			rankRepository.save(rankEntity2);
//			rankRepository.save(rankEntity3);
//			rankRepository.save(rankEntity4);
//
//
//			userRepository.save(userEntity);
//			userRepository.save(userEntity2);
//		};
//	}
}

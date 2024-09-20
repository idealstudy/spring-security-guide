package com.idealstudy.eureka.client.auth.repository;

import com.idealstudy.eureka.client.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}

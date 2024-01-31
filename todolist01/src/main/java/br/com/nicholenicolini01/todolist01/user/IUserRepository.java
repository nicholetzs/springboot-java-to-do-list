package br.com.nicholenicolini01.todolist01.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;


public interface IUserRepository extends JpaRepository<MinhaUserModel, UUID> {
    MinhaUserModel findByUsername(String name);

}
    


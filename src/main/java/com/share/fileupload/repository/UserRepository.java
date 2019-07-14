package com.share.fileupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.share.fileupload.entity.UserEntity;

@Repository("userRepository")
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    
    UserEntity findByEmail(String email);

    //  @Query("SELECT t FROM user t WHERE t.id = ?1 AND t.bar = ?2")
    //  @Query("SELECT t FROM user t WHERE t.id = ?1")
    
    UserEntity findById(int id);
}
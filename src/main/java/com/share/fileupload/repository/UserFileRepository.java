package com.share.fileupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.share.fileupload.entity.UserFileEntity;

@Repository("userFileRepository")
public interface UserFileRepository extends JpaRepository<UserFileEntity, Integer> {}
package com.share.fileupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.share.fileupload.entity.FileEntity;

@Repository("fileRepository")
public interface FileRepository extends JpaRepository<FileEntity, Integer> {}
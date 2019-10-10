package com.share.fileupload.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.share.fileupload.entity.ShareFileEntity;

@Repository("shareFileRepository")
public interface ShareFileRepository extends JpaRepository<ShareFileEntity, Integer> {

    @Query("SELECT f FROM ShareFileEntity f WHERE f.hashValue = :uuid")
    public ShareFileEntity findbyUUID(@Param("uuid") String uuid);
}

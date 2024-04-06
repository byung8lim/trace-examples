package com.lbh.openapi.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lbh.openapi.auth.entity.AppKeyEntity;

@Repository
public interface AppKeyRepository  extends JpaRepository<AppKeyEntity, String> {
	AppKeyEntity findByAppKey(String appKey);
	List<AppKeyEntity> findByType(String type);
	List<AppKeyEntity> findByOrgNo(String orgNo);
}

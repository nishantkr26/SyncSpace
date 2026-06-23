package com.nishant.workspace_service.repository;

import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nishant.workspace_service.entity.Workspace;

@ReadingConverter
public interface WorkSpaceRepository extends JpaRepository<Workspace, Long> {
    boolean existsById(Long id);
}

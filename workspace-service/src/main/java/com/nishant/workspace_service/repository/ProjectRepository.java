package com.nishant.workspace_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import com.nishant.workspace_service.entity.Project;

public interface ProjectRepository extends JpaRepository<Project ,Long> {
    
    List<Project> findByWorkspaceId(Long id);
}

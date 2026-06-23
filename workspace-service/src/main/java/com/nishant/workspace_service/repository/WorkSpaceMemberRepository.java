package com.nishant.workspace_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

import com.nishant.workspace_service.entity.Workspace;
import com.nishant.workspace_service.entity.WorkspaceMember;

@Repository
public interface WorkSpaceMemberRepository extends JpaRepository<WorkspaceMember,Long>{

    @Query("Select wm.workspace from WorkspaceMember wm where wm.userId = :id")
    List<Workspace> findByUserId (@Param("id") Long userId);

    boolean existsByWorkspaceIdAndUserId(Long workspaceId,Long userId);
}

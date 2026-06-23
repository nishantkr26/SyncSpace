package com.nishant.workspace_service.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.nishant.workspace_service.dto.UserProfileResponse;
import com.nishant.workspace_service.dto.WorkSpaceRequest.CreateWorkSpaceRequest;
import com.nishant.workspace_service.dto.WorkSpaceRequest.addMemberWorkspace;
import com.nishant.workspace_service.repository.ProjectRepository;
import com.nishant.workspace_service.repository.WorkSpaceMemberRepository;
import com.nishant.workspace_service.repository.WorkSpaceRepository;
import com.nishant.workspace_service.entity.Project;
import com.nishant.workspace_service.entity.Workspace;
import com.nishant.workspace_service.entity.WorkspaceMember;
import com.nishant.workspace_service.enums.Role;
import com.nishant.workspace_service.client.AuthClient;
import java.util.List;
import java.util.Optional;
import com.nishant.workspace_service.dto.WorkSpaceRequest.addProject;
import javax.management.RuntimeErrorException;

@Service
@RequiredArgsConstructor
public class WorkSpaceService {

    private final WorkSpaceRepository workSpaceRepository;
    private final WorkSpaceMemberRepository workSpaceMemberRepository;
    private final ProjectRepository projectRepository;
    private final AuthClient authClient;

    public void createWorkSpace(CreateWorkSpaceRequest request, String email) {

        System.out.println(request);
        UserProfileResponse userProfileResponse = authClient.getUserProfile(email);
        System.out.println(userProfileResponse);
        Workspace workSpace = Workspace.builder()
                .name(request.name())
                .description(request.description())
                .ownerId(Long.parseLong(userProfileResponse.userId()))
                .createdAt(LocalDateTime.now())
                .build();

        workSpaceRepository.save(workSpace);

        WorkspaceMember workspaceMember = WorkspaceMember.builder()
                .userId(Long.parseLong(userProfileResponse.userId()))
                .role(Role.OWNER)
                .workspace(workSpace)
                .build();

        workSpaceMemberRepository.save(workspaceMember);

    }

    public List<Workspace> getWorkspaces(String email) {
        UserProfileResponse userProfileResponse = authClient.getUserProfile(email);
        Long targetUserId = Long.parseLong(userProfileResponse.userId());
        List<Workspace> memberships = workSpaceMemberRepository.findByUserId(targetUserId);

        if (memberships.isEmpty()) {
            throw new RuntimeException("No workspaces found for user ID: " + targetUserId);
        }

        return memberships;
    }

    public Workspace getWorkspaceById(String email, Long id) {
        UserProfileResponse userProfileResponse = authClient.getUserProfile(email);
        Long targetUserId = Long.parseLong(userProfileResponse.userId());

        if (workSpaceMemberRepository.existsByWorkspaceIdAndUserId(id, targetUserId)) {
            return workSpaceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Workspace data structure missing for ID: " + id));
        } else {
            throw new RuntimeException("No workservice with this id or the person is not in this workspace");
        }
    }

    public void addMember(addMemberWorkspace request) {
        UserProfileResponse userProfileResponse = authClient.getUserProfile(request.email());
        Long targetUserId = Long.parseLong(userProfileResponse.userId());

        if (workSpaceRepository.existsById(request.workspaceId())) {
            // Append .orElseThrow() to handle the Optional safely
            Workspace workspace = workSpaceRepository.findById(request.workspaceId())
                    .orElseThrow(() -> new RuntimeException("Workspace not found with ID: " + request.workspaceId()));

            WorkspaceMember member = WorkspaceMember.builder().userId(targetUserId).role(Role.MEMBER)
                    .workspace(workspace).build();

            workSpaceMemberRepository.save(member);
        } else {
            throw new RuntimeException("Wrong user or wrong workspace");
        }
    }

    public void addProject(addProject request) {
        if (workSpaceRepository.existsById(request.workspaceId())) {
            Workspace workspace = workSpaceRepository.findById(request.workspaceId())
                    .orElseThrow(() -> new RuntimeException("Workspace not found with id: " + request.workspaceId()));

            Project project = Project.builder().name(request.name()).description(request.description())
                    .workspace(workspace).createdAt(LocalDateTime.now()).build();

            projectRepository.save(project);
        } else {
            throw new RuntimeException("No workspace with this id");
        }
    }

    public List<Project> getProjectByWorkspace(Long workspaceId) {
        if (!workSpaceRepository.existsById(workspaceId)) {
            throw new RuntimeException("Workspace not found with ID: " + workspaceId);
        }

        List<Project> projects = projectRepository.findByWorkspaceId(workspaceId);

        if (projects.isEmpty()) {
            throw new RuntimeException("No projects found inside this workspace.");
        }

        return projects;
    }

    public Project getProjectById(Long id){
        if(projectRepository.existsById(id)){
            return projectRepository.findById(id).
                orElseThrow(() -> new RuntimeException("No projects with this id"));
        }

        else{
            throw new RuntimeException("No project availaible with this id");
        }
    }

}

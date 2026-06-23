package com.nishant.workspace_service.dto;

public class WorkSpaceRequest {
    

    public record CreateWorkSpaceRequest(String name, String description) {
    }    
    
    public record addMemberWorkspace(String email,Long workspaceId){

    }

    public record addProject(String name, String description,Long workspaceId){

    }

}

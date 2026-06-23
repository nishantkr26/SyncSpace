package com.nishant.workspace_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nishant.workspace_service.client.AuthClient;
import com.nishant.workspace_service.repository.WorkSpaceRepository;
import com.nishant.workspace_service.service.WorkSpaceService;

import feign.Response;
import lombok.RequiredArgsConstructor;
import com.nishant.workspace_service.dto.WorkSpaceRequest.CreateWorkSpaceRequest;
import com.nishant.workspace_service.dto.WorkSpaceRequest.addMemberWorkspace;
import com.nishant.workspace_service.dto.WorkSpaceRequest.addProject;
import com.nishant.workspace_service.entity.Workspace;

@RestController
@RequestMapping("/workspace")
@RequiredArgsConstructor
public class WorkSpaceController {

    private final WorkSpaceService workspaceService;

    @PostMapping("/create")
    public ResponseEntity<?> createWorkspace(@RequestHeader("X-User-Email") String email,
            @RequestBody CreateWorkSpaceRequest request) {
        try {
            workspaceService.createWorkSpace(request, email);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("WorkSpace " + request.name() + " is successfully created");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Workspace>> getWorkSpace(@RequestHeader("X-User-Email") String email) {
        try {
            return ResponseEntity.status(200).body(workspaceService.getWorkspaces(email));
        } catch (RuntimeException ex) {
            throw new RuntimeException("Failed to fetch workspaces: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getWorkspaceById(@RequestHeader("X-User-Email") String email,
            @PathVariable("id") Long id) {
        try {
            return ResponseEntity.status(200).body(workspaceService.getWorkspaceById(email, id));
        } catch (RuntimeException ex) {
            // CORRECT: Returns a clean 403 Forbidden status with the security message
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

   @PostMapping("/addMember")
    public ResponseEntity<?> addMember(@RequestBody addMemberWorkspace request) {
        try {
            workspaceService.addMember(request);
            // Fix 1: Using ResponseEntity.ok() with a clean success string body
            return ResponseEntity.ok("Member successfully added to the workspace.");
        } catch (RuntimeException ex) {
            // Fix 2: Corrected parenthesis layout so .body() chains properly
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @PostMapping("/project")
    public ResponseEntity<?> addProject(@RequestBody addProject request){
        try{
            workspaceService.addProject(request);

            return ResponseEntity.ok("Project successfully added to the workspace");
        }catch(RuntimeException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/{id}/project")
    public ResponseEntity<?> getProjectByWorkspace(@PathVariable("id") Long id){
        try{
            return ResponseEntity.ok(workspaceService.getProjectByWorkspace(id));
        }catch(RuntimeException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }

    @GetMapping("/project/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable("id") Long id){
        try{
            return ResponseEntity.ok(workspaceService.getProjectById(id));
        }catch(RuntimeException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }
    }
}

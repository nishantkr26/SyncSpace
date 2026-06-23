package com.nishant.workspace_service.entity;

import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import com.nishant.workspace_service.enums.Role;
import jakarta.persistence.EnumType;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "workspace_member")
public class WorkspaceMember {
    
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "workspace_id")
    private Workspace workspace;
}


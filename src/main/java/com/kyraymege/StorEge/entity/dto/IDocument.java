package com.kyraymege.StorEge.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public interface IDocument {
    Long getId();
    void setId(Long id);
    @JsonProperty("documentId")
    String getDocument_Id();
    void setDocumentId(String documentId);
    String getName();
    void setName(String name);
    String getDescription();
    void setDescription(String description);
    String getUri();
    void setUri(String uri);
    String getIcon();
    void setIcon(String icon);
    long getSize();
    void setSize(long size);
    @JsonProperty("formattedSize")
    String getFormatted_Size();
    void setFormattedSize(String formattedSize);
    String getExtension();
    void setExtension(String extension);
    @JsonProperty("referenceId")
    String getReference_Id();
    void setReferenceId(String referenceId);
    @JsonProperty("createdAt")
    LocalDateTime getCreated_At();
    void setCreatedAt(LocalDateTime createdAt);
    @JsonProperty("updatedAt")
    LocalDateTime getUpdated_At();
    void setUpdatedAt(LocalDateTime updatedAt);
    @JsonProperty("ownerName")
    String getOwner_Name();
    void setOwnerName(String ownerName);
    @JsonProperty("ownerEmail")
    String getOwner_Email();
    void setOwnerEmail(String ownerEmail);
    @JsonProperty("ownerPhone")
    String getOwner_Phone();
    void setOwnerPhone(String ownerPhone);
    @JsonProperty("ownerLastLogin")
    LocalDateTime getOwner_Last_Login();
    void setOwnerLastLogin(LocalDateTime ownerLastLogin);
    @JsonProperty("updaterName")
    String getUpdater_Name();
    void setUpdaterName(String updaterName);
}

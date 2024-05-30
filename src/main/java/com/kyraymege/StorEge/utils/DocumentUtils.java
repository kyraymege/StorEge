package com.kyraymege.StorEge.utils;

import com.kyraymege.StorEge.entity.concretes.Document;
import com.kyraymege.StorEge.entity.dto.DocumentDto;
import com.kyraymege.StorEge.entity.dto.UserDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

public class DocumentUtils {
    public static String getDocumentUri(String fileName) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(String.format("/documents/%s", fileName))
                .toUriString();
    }

    public static String setIcon(String fileExtension){
        String extension = StringUtils.trim(fileExtension);
        return switch (extension) {
            case "pdf" -> "https://cdn-icons-png.flaticon.com/128/7359/7359453.png";
            case "doc", "docx" -> "https://cdn-icons-png.flaticon.com/128/5719/5719745.png";
            case "xls", "xlsx" -> "https://cdn-icons-png.flaticon.com/128/15267/15267115.png";
            case "ppt", "pptx" -> "https://cdn-icons-png.flaticon.com/128/9034/9034417.png";
            case "jpg", "jpeg", "png", "gif" -> "https://cdn-icons-png.flaticon.com/128/1221/1221385.png";
            case "zip", "rar" -> "https://cdn-icons-png.flaticon.com/128/12075/12075662.png";
            default -> "https://cdn-icons-png.flaticon.com/128/9890/9890119.png";
        };

    }

    public static DocumentDto documentEntityToDto(Document document, UserDto createdBy, UserDto updatedBy) {
        var documentDto = new DocumentDto();
        BeanUtils.copyProperties(document, documentDto);
        documentDto.setOwnerName(createdBy.getFirstName() + " " + createdBy.getLastName());
        documentDto.setOwnerEmail(createdBy.getEmail());
        documentDto.setOwnerPhone(createdBy.getPhone());
        documentDto.setOwnerLastLogin(createdBy.getLastLogin());
        documentDto.setUpdaterName(updatedBy.getFirstName() + " " + updatedBy.getLastName());
        return documentDto;
    }
}
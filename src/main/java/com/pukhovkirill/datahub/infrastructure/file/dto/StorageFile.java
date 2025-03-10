package com.pukhovkirill.datahub.infrastructure.file.dto;

import java.sql.Timestamp;
import java.util.Objects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFilename;
import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFilepath;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StorageFile implements StorageEntityDto {

    @NotNull(message = "name is null")
    @ValidFilename
    private String name;

    @NotNull(message = "path is null")
    @ValidFilepath
    private String path;

    @NotNull(message = "contentType is null")
    @NotEmpty(message = "contentType is empty")
    @NotBlank(message = "contentType is empty")
    private String contentType;

    private Timestamp lastModified;

    @Size
    private long size;

    private String location;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageFile that = (StorageFile) o;
        return size == that.size                                &&
               Objects.equals(name, that.name)                  &&
               Objects.equals(path, that.path)                  &&
               Objects.equals(contentType, that.contentType)    &&
               Objects.equals(lastModified, that.lastModified)  &&
               Objects.equals(location, that.location);

    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (int) size;
        hash = 31 * hash + (name == null ? 0 : name.hashCode());
        hash = 31 * hash + (path == null ? 0 : path.hashCode());
        hash = 31 * hash + (contentType == null ? 0 : contentType.hashCode());
        hash = 31 * hash + (lastModified == null ? 0 : lastModified.hashCode());
        hash = 31 * hash + (location == null ? 0 : location.hashCode());
        return hash;
    }

    @Override
    public StorageEntityDto clone() {
        return StorageFile.builder()
                .name(this.name)
                .path(this.path)
                .contentType(this.contentType)
                .lastModified(this.lastModified != null ? (Timestamp) this.lastModified.clone() : null)
                .size(this.size)
                .location(this.location)
                .build();
    }
}

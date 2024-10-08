package com.pukhovkirill.datahub.infrastructure.file.dto;

import java.sql.Timestamp;
import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFileSize;
import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFilename;
import com.pukhovkirill.datahub.infrastructure.file.validator.valid.ValidFilepath;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StorageFile implements StorageEntityDto {

    @ValidFilename
    private String name;

    @ValidFilepath
    private String path;

    private String contentType;

    private Timestamp lastModified;

    @ValidFileSize
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
        try {
            return (StorageEntityDto) super.clone();
        }
        catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}

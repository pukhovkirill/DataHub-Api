package com.pukhovkirill.datahub.infrastructure.file.dto;

import java.sql.Timestamp;

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
    public StorageEntityDto clone() {
        try {
            return (StorageEntityDto) super.clone();
        }
        catch( CloneNotSupportedException ex ) {
            throw new InternalError();
        }
    }
}

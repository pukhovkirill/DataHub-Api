package com.pukhovkirill.datahub.infrastructure.dto;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import com.pukhovkirill.datahub.usecase.dto.StorageEntityDto;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class StorageEntityDtoImpl implements StorageEntityDto {

    private long id;

    private String name;

    private String path;

    private String contentType;

    private Timestamp lastModified;

    private long size;

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

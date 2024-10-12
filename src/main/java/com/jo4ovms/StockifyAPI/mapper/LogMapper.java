package com.jo4ovms.StockifyAPI.mapper;

import com.jo4ovms.StockifyAPI.model.DTO.LogDTO;
import com.jo4ovms.StockifyAPI.model.Log;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LogMapper {

    LogDTO toLogDTO(Log log);

    Log toLog(LogDTO logDTO);
}

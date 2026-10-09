package com.yuvraj.driverservice.service.mapper;

import com.yuvraj.driverservice.dto.DriverDto;
import com.yuvraj.driverservice.entity.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverMapperImpl implements DriverMapper {

    @Override
    public DriverDto driverToDriverDto(Driver driver) {
        DriverDto driverDto = new DriverDto();
        driverDto.setId(driver.getId());
        driverDto.setName(driver.getName());
        driverDto.setLicenseNumber(driver.getLicenseNumber());
        driverDto.setLicenseCategory(driver.getLicenseCategory());
        driverDto.setLicenseExpiryDate(driver.getLicenseExpiryDate());
        driverDto.setContactNumber(String.valueOf(driver.getContactNumber()));
        driverDto.setSafetyScore(driver.getSafetyScore());
        driverDto.setStatus(driver.getStatus());
        return driverDto;
    }
}

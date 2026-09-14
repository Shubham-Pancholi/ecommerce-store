package com.ecommerce.store.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.ecommerce.store.dto.CategoryResponse;
import com.ecommerce.store.entity.Category;

@Mapper (componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {

    public CategoryResponse toResponse(Category category);
}
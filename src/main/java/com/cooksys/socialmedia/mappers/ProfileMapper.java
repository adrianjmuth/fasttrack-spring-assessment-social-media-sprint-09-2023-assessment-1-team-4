package com.cooksys.socialmedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.ProfileDto;
import com.cooksys.socialmedia.embeds.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

	Profile DtoToEntity(ProfileDto ProfileRequestDto);

	ProfileDto entityToDto(Profile entity);

	List<ProfileDto> entitiesToDtos(List<Profile> entities);
}

package com.cooksys.socialmedia.mappers;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.CredentialsDto;
import com.cooksys.socialmedia.embeds.Credentials;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {

	Credentials DtoToEntity(CredentialsDto credentialsRequestDto);

	CredentialsDto entityToDto(CredentialsDto credentials);
}

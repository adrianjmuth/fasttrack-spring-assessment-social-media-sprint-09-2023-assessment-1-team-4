package com.cooksys.socialmedia.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.socialmedia.dtos.HashtagRequestDto;
import com.cooksys.socialmedia.dtos.HashtagResponseDto;
import com.cooksys.socialmedia.entities.Hashtag;

@Mapper(componentModel = "spring", uses = { TweetMapper.class })
public interface HashtagMapper {

	Hashtag DtoToEntity(HashtagRequestDto hashtagRequestDto);

	HashtagResponseDto entityToDto(Hashtag entity);

	List<HashtagResponseDto> entitiesToDtos(List<Hashtag> entities);
}

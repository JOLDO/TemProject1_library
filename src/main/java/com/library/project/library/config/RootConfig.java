package com.library.project.library.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }


    //api 사용할때 json을 객첼로 변경할때 사용
    /*@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Java 8 날짜/시간 타입(LocalDate, LocalDateTime 등) 지원 모듈 등록
        mapper.registerModule(new JavaTimeModule());

        // 날짜를 타임스탬프(숫자) 대신 "yyyy-MM-dd" 같은 문자열 형식으로 직렬화
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }*/

    @Bean
    public KoreanDecomposer koreanDecomposer() {
        return new KoreanDecomposer();
    }
}
package com.example.demo.model.dto.response;

import com.example.demo.model.dto.request.CarInfoRequest;
import com.example.demo.model.dto.request.UserInfoRequest;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CarInfoResponse extends CarInfoRequest {

    Long id;
    UserInfoResponse user;
}

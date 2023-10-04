package com.example.demo.remote.clients;


import com.example.demo.model.dto.response.UserInfoResponse;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;


//это имитация удаленного клиента (типа Postman), кот.стучится в наше приложение
//@FeignClient(path = "/endpointPath", url = "clientUrl.com")
//public interface RemoteClient {
//
//    //это контроллер(только без тела метода)
//    @GetMapping
//    UserInfoResponse getRemoteSource(@RequestParam Long id, @RequestHeader("api-key") String apiKey); //здесь задаем параметры,по которым будем что-то искать в сервисе на стороне
//}

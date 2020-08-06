package com.shop.ItemServiceRest.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/")
public class RootController {
    @GetMapping("/")
    public void toSwagger(HttpServletResponse response) throws IOException {
        response.sendRedirect("/items-rest-swagger/swagger-ui.html");
    }
}

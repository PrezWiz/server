package prezwiz.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping
    @Operation(summary = "heath check")
    public String healthCheck() {
        return "healthy";
    }
}

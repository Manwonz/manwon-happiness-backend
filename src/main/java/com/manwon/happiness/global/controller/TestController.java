package com.manwon.happiness.global.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * TestController
 * - Debug/Test endpoint for development and testing purposes
 * - Should be removed or secured in production environment
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * Test error endpoint
     * GET /test/error
     * This endpoint was causing ClassCastException due to unsafe type casting.
     * Fixed to use proper type conversion with error handling.
     */
    @GetMapping("/error")
    public ResponseEntity<Map<String, Object>> testError(@RequestParam(value = "value", defaultValue = "123") String stringValue) {
        try {
            // Previously this was causing ClassCastException with unsafe casting: (Integer) stringValue
            // Fixed to use proper Integer conversion with error handling
            Integer integerValue = Integer.valueOf(stringValue);
            
            return ResponseEntity.ok(Map.of(
                "message", "Test endpoint executed successfully",
                "originalValue", stringValue,
                "convertedValue", integerValue,
                "type", "Integer"
            ));
        } catch (NumberFormatException e) {
            // Handle case where string cannot be converted to integer
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Invalid number format",
                "message", "The provided value '" + stringValue + "' cannot be converted to Integer",
                "originalValue", stringValue,
                "suggestion", "Please provide a valid integer value"
            ));
        }
    }
}
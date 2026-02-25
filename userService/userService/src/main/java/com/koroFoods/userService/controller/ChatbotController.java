package com.koroFoods.userService.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koroFoods.userService.dto.request.PromptRequest;
import com.koroFoods.userService.service.GeminiService;


@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/conversacion")
    public String conversar(@RequestBody PromptRequest prompt) {
        return geminiService.getFormattedChatResponse(prompt);
    }
}
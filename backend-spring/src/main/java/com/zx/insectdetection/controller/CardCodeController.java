package com.zx.insectdetection.controller;

import com.zx.insectdetection.service.Impl.CardCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CardCodeController {
    @Autowired
    private CardCodeService cardCodeService;

    @GetMapping("/generateCardCodes")
    public List<String> generateCardCodes(@RequestParam int count) {
        return cardCodeService.generateCardCodes(count);
    }
}
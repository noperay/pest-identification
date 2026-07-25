package com.zx.insectdetection.service.Impl;


import com.zx.insectdetection.entity.others.CardCode;
import com.zx.insectdetection.mapper.CardCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Service
public class CardCodeService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 10;

    @Autowired
    private CardCodeRepository cardCodeRepository;

    public List<String> generateCardCodes(int count) {
        List<String> codes = new ArrayList<>();
        Random random = new Random();

        while (codes.size() < count) {
            StringBuilder codeBuilder = new StringBuilder();
            for (int i = 0; i < CODE_LENGTH; i++) {
                int index = random.nextInt(CHARACTERS.length());
                codeBuilder.append(CHARACTERS.charAt(index));
            }
            String code = codeBuilder.toString();

            if (!cardCodeRepository.findByCode(code).isPresent()) {
                CardCode cardCode = new CardCode();
                cardCode.setCode(code);
                cardCode.setCreateDate(new Timestamp(System.currentTimeMillis()));
                cardCodeRepository.save(cardCode);
                codes.add(code);
            }
        }
        return codes;
    }
}
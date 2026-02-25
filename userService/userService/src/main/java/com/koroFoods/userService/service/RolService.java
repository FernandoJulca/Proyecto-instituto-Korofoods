package com.koroFoods.userService.service;

import com.koroFoods.userService.repository.IRolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RolService {

    private final IRolRepository rolRepository;
}

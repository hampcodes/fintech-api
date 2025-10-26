package com.fintech.service;

import com.fintech.dto.request.SettingRequest;
import com.fintech.dto.response.SettingResponse;
import com.fintech.exception.DuplicateAccountException;
import com.fintech.model.SystemSetting;
import com.fintech.repository.SettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SettingsService {

    private final SettingsRepository settingsRepository;

    @Transactional
    public SettingResponse createSetting(SettingRequest request) {
        if (settingsRepository.existsBySettingKey(request.settingKey())) {
            throw new DuplicateAccountException("Setting with key " + request.settingKey() + " already exists");
        }

        SystemSetting setting = new SystemSetting();
        setting.setSettingKey(request.settingKey());
        setting.setSettingValue(request.settingValue());
        setting.setDescription(request.description());

        return mapToResponse(settingsRepository.save(setting));
    }

    @Transactional(readOnly = true)
    public List<SettingResponse> getAllSettings() {
        return settingsRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SettingResponse getSettingByKey(String key) {
        SystemSetting setting = settingsRepository.findBySettingKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found: " + key));
        return mapToResponse(setting);
    }

    @Transactional
    public SettingResponse updateSetting(String key, SettingRequest request) {
        SystemSetting setting = settingsRepository.findBySettingKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found: " + key));

        setting.setSettingValue(request.settingValue());
        setting.setDescription(request.description());

        return mapToResponse(settingsRepository.save(setting));
    }

    @Transactional
    public void deleteSetting(String key) {
        SystemSetting setting = settingsRepository.findBySettingKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found: " + key));
        settingsRepository.delete(setting);
    }

    private SettingResponse mapToResponse(SystemSetting setting) {
        return new SettingResponse(
                setting.getId(),
                setting.getSettingKey(),
                setting.getSettingValue(),
                setting.getDescription(),
                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }
}

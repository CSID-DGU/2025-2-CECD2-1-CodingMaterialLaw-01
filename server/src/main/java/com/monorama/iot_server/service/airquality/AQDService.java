package com.monorama.iot_server.service.airquality;

import com.monorama.iot_server.domain.User;
import com.monorama.iot_server.dto.request.airquality.AQDRequestDto;
import com.monorama.iot_server.dto.request.airquality.AQDSyncRequestDto;
import com.monorama.iot_server.exception.CommonException;
import com.monorama.iot_server.exception.ErrorCode;
import com.monorama.iot_server.repository.AQDRepository;
import com.monorama.iot_server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AQDService {

    private final UserRepository userRepo;
    private final AQDRepository airRepo;

    @Transactional
    public String saveRealtime(Long userId, AQDRequestDto dto) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));

        List<String> missingFields = dto.getMissingFields(user);
        airRepo.save(dto.toEntity(user));

        return missingFields.isEmpty() ?
                "모든 필드가 정상적으로 저장되었습니다." :
                "누락된 필드: " + missingFields;
    }



    @Transactional
    public void saveSync(Long userId, AQDSyncRequestDto request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new CommonException(ErrorCode.NOT_FOUND_USER));
        airRepo.saveAll(
                request.airQualityDataList().stream()
                        .map(dto -> dto.toEntity(user))
                        .toList()
        );
    }
}

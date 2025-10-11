package com.manwon.happiness.place.service;

import com.manwon.happiness.place.entity.Place;
import com.manwon.happiness.place.repository.PlaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {

    private final PlaceRepository placeRepository;

    /**
     * CSV 파일을 읽어서 DB에 Place 데이터 저장 또는 업데이트
     * - 필수 컬럼: 사업장명, 지번주소, 우편번호
     * - 이미 존재하면 우편번호만 업데이트
     * - 새 데이터면 새 row 추가
     * - 실패한 행은 별도 CSV로 백업
     */
    public void importFromCsv(String filePath) {
        int totalCount = 0;
        int successCount = 0;
        int duplicateCount = 0;
        int failCount = 0;

        List<String> failedRows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8))) {
            // 1. CSV 헤더 읽기
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV 파일이 비어 있습니다.");
            }

            // 2. 헤더 파싱
            String[] headers = headerLine.split(",");
            Map<String, Integer> headerIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                headerIndex.put(headers[i].trim(), i);
            }

            // 3. 필수 헤더 존재 확인
            if (!headerIndex.containsKey("사업장명") || !headerIndex.containsKey("지번주소")) {
                throw new IllegalArgumentException("CSV 파일에 '사업장명' 또는 '지번주소' 컬럼이 없습니다.");
            }

            // 4. 선택 컬럼 (우편번호)
            boolean hasPostalCode = headerIndex.containsKey("우편번호");

            String line;
            while ((line = br.readLine()) != null) {
                totalCount++;
                try {
                    String[] tokens = line.split(",");

                    String name = tokens[headerIndex.get("사업장명")].trim();
                    String address = tokens[headerIndex.get("지번주소")].trim();
                    String postalCode = hasPostalCode ? tokens[headerIndex.get("우편번호")].trim() : null;

                    if (name.isEmpty() || address.isEmpty()) {
                        throw new IllegalArgumentException("이름 또는 주소 누락");
                    }

                    // 중복 확인 (사업장명 + 주소 기준)
                    Optional<Place> existing = placeRepository.findByNameAndAddress(name, address);
                    if (existing.isPresent()) {
                        Place place = existing.get();
                        // 기존 데이터의 우편번호가 다르면 업데이트
                        if (postalCode != null &&
                                (place.getPostalCode() == null || !place.getPostalCode().equals(postalCode))) {
                            place.updatePostalCode(postalCode);
                            placeRepository.save(place);
                            log.info("♻️ 기존 데이터 우편번호 업데이트: {}", name);
                        } else {
                            duplicateCount++;
                            log.warn("⚠️ 이미 존재 (변경 없음): {}", name);
                        }
                        continue;
                    }

                    // 새로운 가게 저장
                    Place newPlace = Place.builder()
                            .name(name)
                            .address(address)
                            .postalCode(postalCode)
                            .isClosed(false)
                            .build();

                    placeRepository.save(newPlace);
                    successCount++;
                    log.info("✅ 저장 완료: {}", name);

                } catch (Exception e) {
                    failCount++;
                    failedRows.add(line + ",에러:" + e.getMessage());
                    log.error("❌ 저장 실패 (라인): {}, 이유: {}", line, e.getMessage());
                    log.error("   ↳ 이유: {}", e.getMessage());
                }
            }

            // 실패 항목 저장
            if (!failedRows.isEmpty()) {
                String failFilePath = filePath.replace(".csv", "_failed.csv");
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(failFilePath, StandardCharsets.UTF_8))) {
                    bw.write("원본데이터,error\n");
                    for (String failLine : failedRows) {
                        bw.write(failLine + "\n");
                    }
                    log.warn("📁 실패 항목 저장 완료 → {}", failFilePath);
                }
            }

            // 요약 로그
            log.info("\n===== 📊 CSV Import 결과 요약 =====");
            log.info("총 라인 수: {}", totalCount);
            log.info("✅ 성공: {}", successCount);
            log.info("⚠️ 중복: {}", duplicateCount);
            log.info("❌ 실패: {}", failCount);
            log.info("=================================\n");

        } catch (Exception e) {
            log.error("❌ CSV 파일 처리 중 오류 발생: {}", e.getMessage(), e);
        }
    }
}
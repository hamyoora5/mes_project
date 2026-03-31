package com.example.mes.entity;

import com.example.mes.common.QualityTestResult;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * 품질 검사 대상과 최종 검사 결과를 저장하는 엔티티입니다.
 *
 * <p>왜 필요한가:
 * 생산 종료 후 바로 PASS/DEFECT를 확정하지 않고, 품질 검사라는 중간 단계를 통해
 * 결과를 반영해야 하기 때문입니다. 특히 이 프로젝트는 AI 판정 결과를 후속으로
 * 받아 반영하는 흐름을 강조하므로, QT 번호 단위의 별도 테이블이 필요합니다.
 *
 * <p>어떻게 쓰는가:
 * <ul>
 *   <li>DP 코드 기준으로 품질 검사 대상 생성</li>
 *   <li>처음에는 `WAIT` 상태</li>
 *   <li>AI 결과 수신 후 `PASS` 또는 `FAIL` 반영</li>
 *   <li>동시에 생산 결과 상태도 함께 갱신</li>
 * </ul>
 */
@Entity
@Table(name = "quality_test")
public class QualityTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "test_no", nullable = false, unique = true, length = 50)
    private String testNo;

    @Column(name = "daily_prod_code", nullable = false, length = 50)
    private String dailyProdCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private QualityTestResult result;

    @Column(name = "tested_at")
    private LocalDateTime testedAt;

    @Column(name = "image_path", length = 255)
    private String imagePath;

    protected QualityTest() {
    }

    public QualityTest(String testNo, String dailyProdCode, String imagePath) {
        this.testNo = testNo;
        this.dailyProdCode = dailyProdCode;
        this.imagePath = imagePath;
        this.result = QualityTestResult.WAIT;
    }

    @PrePersist
    void prePersist() {
        if (result == null) {
            result = QualityTestResult.WAIT;
        }
    }

    /**
     * AI 판정 결과를 품질 검사에 반영하고 검사 시각을 기록합니다.
     *
     * @param result 반영할 품질 결과
     */
    public void applyAiResult(QualityTestResult result) {
        this.result = result;
        this.testedAt = LocalDateTime.now();
    }

    /**
     * 샘플 데이터 적재용으로 결과와 검사 시각을 직접 설정합니다.
     *
     * @param result 품질 결과
     * @param testedAt 검사 시각
     */
    public void applySeedResult(QualityTestResult result, LocalDateTime testedAt) {
        this.result = result;
        this.testedAt = testedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTestNo() {
        return testNo;
    }

    public String getDailyProdCode() {
        return dailyProdCode;
    }

    public QualityTestResult getResult() {
        return result;
    }

    public LocalDateTime getTestedAt() {
        return testedAt;
    }

    public String getImagePath() {
        return imagePath;
    }
}

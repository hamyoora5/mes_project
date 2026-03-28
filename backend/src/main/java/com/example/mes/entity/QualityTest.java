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

    public void applyAiResult(QualityTestResult result) {
        this.result = result;
        this.testedAt = LocalDateTime.now();
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

# FinLedger
Backend architecture study project implementing a concurrency-safe banking transfer system
Spring Boot 기반 동시성(Concurrency)을 고려한 금융 이체 시스템 구현 프로젝트

📌 프로젝트 소개
FinLedger는 금융 시스템에 가장 중요한 이체 기능을 중심으로 동시 요청 환경에서도 데이터 정합성을 보장하는 백엔드 시스템을 구현하는 프로젝트이다.

- 트랜잭션 경계 설정
- 동시성 문제 해결
- 백엔드 아키텍처 이해
- 실제 금융 시스템 동작 방식 학습

🛠 기술 스택
- Java 17
- Spring Boot
- Spring Data JPA
- PostgreSQL
- Gradle
- JUnit5

🚧 구현 예정 기능
- 계좌 생성
- 잔액 조회
- 이체 처리
- 거래 원장(Ledger) 기록
- 동시성 테스트
- Lock 전략 적용 및 비교

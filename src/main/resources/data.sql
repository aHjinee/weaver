-- data.sql
-- 기준:
-- - 테스트 목적: 목록 조회, 단건 조회, 필터/검색, 정렬, 상태별 조회, 변경 이력, 백업 성공/실패/건너뜀 확인
-- - employee_change_logs는 employees FK 없음
-- - employee_change_diffs만 employee_change_logs를 FK로 참조
-- - employees.profile_image_id, backup_histories.file_id는 files를 참조
-- - profile_image_id/file_id는 nullable
-- - UNIQUE 제약:
--   - departments.name
--   - files.storage_path
--   - employees.email
--   - employees.employee_number
--   - employees.profile_image_id
--   - backup_histories.file_id
--
-- 주의:
-- - 아래 TRUNCATE는 기존 데이터를 삭제합니다.
-- - 파일 다운로드까지 테스트하려면 storage_path에 해당하는 실제 파일을 로컬 스토리지에 별도로 준비해야 합니다.
-- - DB/API 목록/상세/필터/정렬/상태/백업/변경이력 테스트용 데이터입니다.


--Postgresql
-- TRUNCATE TABLE
--     employee_change_diffs,
--     employee_change_logs,
--     backup_histories,
--     employees,
--     files,
--     departments
-- RESTART IDENTITY CASCADE;

--H2 DB
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE employee_change_diffs;
TRUNCATE TABLE employee_change_logs;
TRUNCATE TABLE backup_histories;
TRUNCATE TABLE employees;
TRUNCATE TABLE files;
TRUNCATE TABLE departments;

SET REFERENTIAL_INTEGRITY TRUE;
-- =========================================================
-- 1. departments
-- 테스트 포인트:
-- - 전체 조회
-- - 이름 부분 검색: 개발, 인사, 마케팅 등
-- - established_date 범위 조회
-- - name 정렬
-- - 직원이 많은 부서/적은 부서/직원이 없는 부서
-- =========================================================

INSERT INTO departments (
    id, name, description, established_date, created_at, updated_at
) VALUES
      (
          '018f6b7a-1000-7000-8000-000000000001',
          '개발팀',
          '백엔드, 프론트엔드, 인프라 개발을 담당하는 부서',
          DATE '2019-06-15',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:00:00+09:00'
      ),
      (
          '018f6b7a-1000-7000-8000-000000000002',
          '인사팀',
          '채용, 평가, 보상, 직원 관리를 담당하는 부서',
          DATE '2018-03-01',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:05:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:05:00+09:00'
      ),
      (
          '018f6b7a-1000-7000-8000-000000000003',
          '마케팅팀',
          '브랜드, 콘텐츠, 퍼포먼스 마케팅을 담당하는 부서',
          DATE '2021-04-20',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:10:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:10:00+09:00'
      ),
      (
          '018f6b7a-1000-7000-8000-000000000004',
          '디자인팀',
          '서비스 UI/UX와 브랜드 디자인을 담당하는 부서',
          DATE '2020-01-10',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:15:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:15:00+09:00'
      ),
      (
          '018f6b7a-1000-7000-8000-000000000005',
          '운영팀',
          '서비스 운영과 고객 대응을 담당하는 부서',
          DATE '2022-09-01',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:20:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:20:00+09:00'
      ),
      (
          '018f6b7a-1000-7000-8000-000000000006',
          '재무팀',
          '회계, 정산, 예산 관리를 담당하는 부서',
          DATE '2017-11-01',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:25:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:25:00+09:00'
      ),
      (
          '018f6b7a-1000-7000-8000-000000000007',
          '법무팀',
          '계약, 컴플라이언스, 법률 검토를 담당하는 부서. 직원 없음 테스트용',
          DATE '2024-01-01',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:30:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 09:30:00+09:00'
      );

-- =========================================================
-- 2. files
-- 테스트 포인트:
-- - 직원 프로필 이미지 파일
-- - 백업 성공 CSV 파일
-- - 백업 실패 log 파일
-- - 참조되지 않는 일반 파일
-- - storage_path unique 충족
-- =========================================================

INSERT INTO files (
    id, original_name, content_type, size, storage_path, created_at
) VALUES
-- profile images
(
    '018f6b7a-2000-7000-8000-000000000001',
    'kim-minjun-profile.png',
    'image/png',
    152034,
    'storage/profiles/018f6b7a-2000-7000-8000-000000000001_kim-minjun-profile.png',
    TIMESTAMP WITH TIME ZONE '2026-05-01 09:40:00+09:00'
),
(
    '018f6b7a-2000-7000-8000-000000000002',
    'lee-seoyeon-profile.jpg',
    'image/jpeg',
    204812,
    'storage/profiles/018f6b7a-2000-7000-8000-000000000002_lee-seoyeon-profile.jpg',
    TIMESTAMP WITH TIME ZONE '2026-05-01 09:41:00+09:00'
),
(
    '018f6b7a-2000-7000-8000-000000000003',
    'park-jihoon-profile.png',
    'image/png',
    180330,
    'storage/profiles/018f6b7a-2000-7000-8000-000000000003_park-jihoon-profile.png',
    TIMESTAMP WITH TIME ZONE '2026-05-01 09:42:00+09:00'
),
(
    '018f6b7a-2000-7000-8000-000000000004',
    'choi-yujin-profile.webp',
    'image/webp',
    98012,
    'storage/profiles/018f6b7a-2000-7000-8000-000000000004_choi-yujin-profile.webp',
    TIMESTAMP WITH TIME ZONE '2026-05-01 09:43:00+09:00'
),
(
    '018f6b7a-2000-7000-8000-000000000005',
    'han-jimin-profile.png',
    'image/png',
    123456,
    'storage/profiles/018f6b7a-2000-7000-8000-000000000005_han-jimin-profile.png',
    TIMESTAMP WITH TIME ZONE '2026-05-01 09:44:00+09:00'
),

-- backup csv files
(
    '018f6b7a-2000-7000-8000-000000000101',
    'employees-backup-20260515-090000.csv',
    'text/csv',
    4096,
    'storage/backups/employees-backup-20260515-090000.csv',
    TIMESTAMP WITH TIME ZONE '2026-05-15 09:00:30+09:00'
),
(
    '018f6b7a-2000-7000-8000-000000000102',
    'employees-backup-20260516-090000.csv',
    'text/csv',
    5120,
    'storage/backups/employees-backup-20260516-090000.csv',
    TIMESTAMP WITH TIME ZONE '2026-05-16 09:00:35+09:00'
),

-- failed backup log files
(
    '018f6b7a-2000-7000-8000-000000000201',
    'backup-error-20260515-100000.log',
    'text/plain',
    1024,
    'storage/backups/backup-error-20260515-100000.log',
    TIMESTAMP WITH TIME ZONE '2026-05-15 10:00:30+09:00'
),
(
    '018f6b7a-2000-7000-8000-000000000202',
    'backup-error-20260517-030000.log',
    'text/plain',
    1536,
    'storage/backups/backup-error-20260517-030000.log',
    TIMESTAMP WITH TIME ZONE '2026-05-17 03:00:40+09:00'
),

-- unreferenced file for file download / orphan metadata check
(
    '018f6b7a-2000-7000-8000-000000000301',
    'sample-policy.pdf',
    'application/pdf',
    300000,
    'storage/files/sample-policy.pdf',
    TIMESTAMP WITH TIME ZONE '2026-05-11 11:00:00+09:00'
);

-- =========================================================
-- 3. employees
-- 테스트 포인트:
-- - 상태별 조회: ACTIVE, ON_LEAVE, RESIGNED
-- - 부서별 조회
-- - 입사일 범위 조회
-- - 프로필 이미지 있음/없음
-- - email, employee_number unique
-- - profile_image_id unique
-- =========================================================

INSERT INTO employees (
    id, name, email, employee_number, department_id, profile_image_id,
    position, hire_date, status, created_at, updated_at
) VALUES
      (
          '018f6b7a-3000-7000-8000-000000000001',
          '김민준',
          'minjun.kim@hrbank.com',
          'EMP-2026-0001',
          '018f6b7a-1000-7000-8000-000000000001',
          NULL,
          '백엔드 개발자',
          DATE '2023-03-01',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-10 11:00:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000002',
          '이서연',
          'seoyeon.lee@hrbank.com',
          'EMP-2026-0002',
          '018f6b7a-1000-7000-8000-000000000002',
          NULL,
          '인사 매니저',
          DATE '2022-07-15',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:01:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:01:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000003',
          '박지훈',
          'jihoon.park@hrbank.com',
          'EMP-2026-0003',
          '018f6b7a-1000-7000-8000-000000000004',
          NULL,
          'UX 디자이너',
          DATE '2024-01-10',
          'ON_LEAVE',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:02:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-12 14:30:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000004',
          '최유진',
          'yujin.choi@hrbank.com',
          'EMP-2026-0004',
          '018f6b7a-1000-7000-8000-000000000003',
          NULL,
          '마케팅 담당자',
          DATE '2021-11-05',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:03:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:03:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000005',
          '정도현',
          'dohyun.jung@hrbank.com',
          'EMP-2026-0005',
          '018f6b7a-1000-7000-8000-000000000005',
          NULL,
          '운영 매니저',
          DATE '2020-05-20',
          'RESIGNED',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:04:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-13 17:00:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000006',
          '한지민',
          'jimin.han@hrbank.com',
          'EMP-2026-0006',
          '018f6b7a-1000-7000-8000-000000000001',
          NULL,
          '프론트엔드 개발자',
          DATE '2025-02-03',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:05:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:05:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000007',
          '오세훈',
          'sehun.oh@hrbank.com',
          'EMP-2026-0007',
          '018f6b7a-1000-7000-8000-000000000001',
          NULL,
          'DevOps 엔지니어',
          DATE '2024-08-12',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:06:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:06:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000008',
          '윤하늘',
          'haneul.yoon@hrbank.com',
          'EMP-2026-0008',
          '018f6b7a-1000-7000-8000-000000000002',
          NULL,
          '채용 담당자',
          DATE '2026-05-02',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:00:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000009',
          '강도윤',
          'doyoon.kang@hrbank.com',
          'EMP-2026-0009',
          '018f6b7a-1000-7000-8000-000000000006',
          NULL,
          '재무 분석가',
          DATE '2022-02-14',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:05:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:05:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000010',
          '송예린',
          'yerin.song@hrbank.com',
          'EMP-2026-0010',
          '018f6b7a-1000-7000-8000-000000000003',
          NULL,
          '콘텐츠 마케터',
          DATE '2023-09-18',
          'ON_LEAVE',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:10:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-14 16:20:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000011',
          '문태오',
          'taeo.moon@hrbank.com',
          'EMP-2026-0011',
          '018f6b7a-1000-7000-8000-000000000001',
          NULL,
          '백엔드 개발자',
          DATE '2026-01-05',
          'ACTIVE',
          TIMESTAMP WITH TIME ZONE '2026-05-03 09:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-03 09:00:00+09:00'
      ),
      (
          '018f6b7a-3000-7000-8000-000000000012',
          '서아린',
          'arin.seo@hrbank.com',
          'EMP-2026-0012',
          '018f6b7a-1000-7000-8000-000000000004',
          NULL,
          '프로덕트 디자이너',
          DATE '2021-04-01',
          'RESIGNED',
          TIMESTAMP WITH TIME ZONE '2026-05-03 09:10:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-15 18:00:00+09:00'
      );

-- =========================================================
-- 4. employee_change_logs
-- 테스트 포인트:
-- - type별 조회: CREATED, UPDATED, DELETED
-- - employee_number 검색
-- - at 기간 조회
-- - IP 저장 확인
-- - employees FK 없음: 삭제된 직원도 이력 보존 가능
-- =========================================================

INSERT INTO employee_change_logs (
    id, type, employee_number, memo, ip_address, at
) VALUES
      (
          '018f6b7a-4000-7000-8000-000000000001',
          'CREATED',
          'EMP-2026-0001',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:00:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000002',
          'CREATED',
          'EMP-2026-0002',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:01:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000003',
          'CREATED',
          'EMP-2026-0003',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:02:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000004',
          'UPDATED',
          'EMP-2026-0001',
          '직함 변경',
          '192.168.0.10',
          TIMESTAMP WITH TIME ZONE '2026-05-10 11:00:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000005',
          'UPDATED',
          'EMP-2026-0003',
          '휴직 처리',
          '192.168.0.11',
          TIMESTAMP WITH TIME ZONE '2026-05-12 14:30:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000006',
          'DELETED',
          'EMP-2026-0005',
          '퇴사자 삭제 처리',
          '192.168.0.12',
          TIMESTAMP WITH TIME ZONE '2026-05-13 17:00:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000007',
          'CREATED',
          'EMP-2026-0008',
          '신규 입사자 등록',
          '192.168.0.13',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:00:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000008',
          'UPDATED',
          'EMP-2026-0010',
          '휴직 처리',
          '192.168.0.20',
          TIMESTAMP WITH TIME ZONE '2026-05-14 16:20:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000009',
          'DELETED',
          'EMP-2026-0012',
          '퇴사자 삭제 처리',
          '192.168.0.21',
          TIMESTAMP WITH TIME ZONE '2026-05-15 18:00:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000010',
          'CREATED',
          'EMP-2026-0011',
          '개발팀 충원',
          '192.168.0.22',
          TIMESTAMP WITH TIME ZONE '2026-05-03 09:00:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000011',
          'CREATED',
          'EMP-2026-0004',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:03:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000012',
          'CREATED',
          'EMP-2026-0005',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:04:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000013',
          'CREATED',
          'EMP-2026-0006',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:05:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000014',
          'CREATED',
          'EMP-2026-0007',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-01 10:06:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000015',
          'CREATED',
          'EMP-2026-0009',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:05:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000016',
          'CREATED',
          'EMP-2026-0010',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-02 10:10:00+09:00'
      ),
      (
          '018f6b7a-4000-7000-8000-000000000017',
          'CREATED',
          'EMP-2026-0012',
          '초기 직원 등록',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-03 09:10:00+09:00'
      );

-- =========================================================
-- 5. employee_change_diffs
-- 테스트 포인트:
-- - 변경 이력 상세 조회
-- - 한 change_log에 diff 여러 개
-- - 추가/삭제 이력은 before_value 또는 after_value가 null일 수 있음
-- =========================================================

INSERT INTO employee_change_diffs (
    id, change_log_id, property_name, before_value, after_value
) VALUES
      (
          '018f6b7a-5000-7000-8000-000000000001',
          '018f6b7a-4000-7000-8000-000000000004',
          'position',
          '주니어 백엔드 개발자',
          '백엔드 개발자'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000002',
          '018f6b7a-4000-7000-8000-000000000005',
          'status',
          '재직중',
          '휴직중'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000003',
          '018f6b7a-4000-7000-8000-000000000006',
          'status',
          '재직중',
          '퇴사'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000004',
          '018f6b7a-4000-7000-8000-000000000006',
          'deleted',
          'false',
          'true'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000005',
          '018f6b7a-4000-7000-8000-000000000008',
          'status',
          '재직중',
          '휴직중'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000006',
          '018f6b7a-4000-7000-8000-000000000009',
          'status',
          '재직중',
          '퇴사'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000007',
          '018f6b7a-4000-7000-8000-000000000009',
          'deleted',
          'false',
          'true'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000008',
          '018f6b7a-4000-7000-8000-000000000010',
          'department',
          NULL,
          '개발팀'
      ),
      (
          '018f6b7a-5000-7000-8000-000000000009',
          '018f6b7a-4000-7000-8000-000000000010',
          'position',
          NULL,
          '백엔드 개발자'
      );

-- =========================================================
-- 6. backup_histories
-- 테스트 포인트:
-- - status별 조회:
--   COMPLETED, FAILED, SKIPPED
-- - 최근 완료 백업 조회
-- - worker별 조회: system, IP
-- - started_at/ended_at 정렬
-- - file_id nullable:
--   SKIPPED는 file_id null 가능
-- - file_id unique:
--   COMPLETED/FAILED는 서로 다른 files.id 참조
-- =========================================================

INSERT INTO backup_histories (
    id, worker, started_at, ended_at, status, file_id
) VALUES
      (
          '018f6b7a-6000-7000-8000-000000000001',
          'system',
          TIMESTAMP WITH TIME ZONE '2026-05-15 09:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-15 09:00:30+09:00',
          'COMPLETED',
          NULL
      ),
      (
          '018f6b7a-6000-7000-8000-000000000002',
          '127.0.0.1',
          TIMESTAMP WITH TIME ZONE '2026-05-15 10:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-15 10:00:30+09:00',
          'FAILED',
          NULL
      ),
      (
          '018f6b7a-6000-7000-8000-000000000003',
          'system',
          TIMESTAMP WITH TIME ZONE '2026-05-15 11:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-15 11:00:01+09:00',
          'SKIPPED',
          NULL
      ),
      (
          '018f6b7a-6000-7000-8000-000000000004',
          'system',
          TIMESTAMP WITH TIME ZONE '2026-05-16 09:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-16 09:00:35+09:00',
          'COMPLETED',
          NULL
      ),
      (
          '018f6b7a-6000-7000-8000-000000000005',
          '192.168.0.50',
          TIMESTAMP WITH TIME ZONE '2026-05-17 03:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-17 03:00:40+09:00',
          'FAILED',
          NULL
      ),
      (
          '018f6b7a-6000-7000-8000-000000000006',
          'system',
          TIMESTAMP WITH TIME ZONE '2026-05-17 04:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-17 04:00:01+09:00',
          'SKIPPED',
          NULL
      ),
      (
          '018f6b7a-6000-7000-8000-000000000007',
          'system',
          TIMESTAMP WITH TIME ZONE '2026-05-18 09:00:00+09:00',
          TIMESTAMP WITH TIME ZONE '2026-05-18 09:00:01+09:00',
          'SKIPPED',
          NULL
      );
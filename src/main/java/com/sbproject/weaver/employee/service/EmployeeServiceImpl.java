package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.changelog.entity.ChangeLogType;
import com.sbproject.weaver.changelog.service.ChangeLogService;
import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.entity.Department;
import com.sbproject.weaver.department.repository.DepartmentRepository;
import com.sbproject.weaver.employee.dto.EmployeeCountCondition;
import com.sbproject.weaver.employee.dto.EmployeeCreateRequest;
import com.sbproject.weaver.employee.dto.EmployeeDistributionDto;
import com.sbproject.weaver.employee.dto.EmployeeDto;
import com.sbproject.weaver.employee.dto.EmployeeSearchCondition;
import com.sbproject.weaver.employee.dto.EmployeeSearchDistribution;
import com.sbproject.weaver.employee.dto.EmployeeTrendCondition;
import com.sbproject.weaver.employee.dto.EmployeeTrendDto;
import com.sbproject.weaver.employee.dto.EmployeeUpdateRequest;
import com.sbproject.weaver.employee.entity.Employee;
import com.sbproject.weaver.employee.entity.EmployeeStatus;
import com.sbproject.weaver.employee.mapper.EmployeeMapper;
import com.sbproject.weaver.employee.repository.EmployeeRepository;
import com.sbproject.weaver.file.entity.FileEntity;
import com.sbproject.weaver.file.service.FileService;
import com.sbproject.weaver.file.type.FilePurpose;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final FileService fileService;
    private final ChangeLogService changeLogService;

    @Override
    public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile, HttpServletRequest httpRequest) {
        validateCreateRequest(request);

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다. email = " + request.getEmail());
        }

        Department department = getDepartmentOrThrow(request.getDepartmentId());

        FileEntity profileImage = null;
        if (profile != null && !profile.isEmpty()) {
            profileImage = fileService.saveMultipartFile(profile, FilePurpose.PROFILE);
        }

        Employee employee = Employee.builder()
                .name(request.getName())
                .email(request.getEmail())
                .employeeNumber(generateEmployeeNumber())
                .department(department)
                .profileImage(profileImage)
                .position(request.getPosition())
                .hireDate(request.getHireDate())
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        changeLogService.save(
                ChangeLogType.CREATED,
                null,
                savedEmployee,
                request.getMemo(),
                httpRequest
        );

        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto findById(UUID id) {
        Employee employee = getEmployeeOrThrow(id);
        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto update(UUID id, EmployeeUpdateRequest request, MultipartFile profile, HttpServletRequest httpRequest) {
        Employee employee = getEmployeeOrThrow(id);

        Employee beforeEmployee = Employee.builder()
                .name(employee.getName())
                .email(employee.getEmail())
                .position(employee.getPosition())
                .hireDate(employee.getHireDate())
                .department(employee.getDepartment())
                .status(employee.getStatus())
                .employeeNumber(employee.getEmployeeNumber())
                .profileImage(employee.getProfileImage())
                .build();

        if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다. email = " + request.getEmail());
            }
        }

        Department department = null;
        if (request.getDepartmentId() != null) {
            department = getDepartmentOrThrow(request.getDepartmentId());
        }

        employee.updateInfo(
                request.getName(),
                request.getEmail(),
                request.getPosition(),
                request.getHireDate(),
                department,
                request.getStatus()
        );

        if (profile != null && !profile.isEmpty()) {
            FileEntity previousProfileImage = employee.getProfileImage();

            FileEntity newProfileImage = fileService.saveMultipartFile(profile, FilePurpose.PROFILE);
            employee.updateProfileImage(newProfileImage);

            employeeRepository.flush();

            if (previousProfileImage != null) {
                fileService.delete(previousProfileImage.getId());
            }
        }

        changeLogService.save(
                ChangeLogType.UPDATED,
                beforeEmployee,
                employee,
                request.getMemo(),
                httpRequest
        );

        return employeeMapper.toDto(employee);
    }

    @Override
    public void delete(UUID id, HttpServletRequest httpRequest) {
        Employee employee = getEmployeeOrThrow(id);
        FileEntity profileImage = employee.getProfileImage();

        changeLogService.save(
                ChangeLogType.DELETED,
                employee,
                null,
                "직원 삭제",
                httpRequest
        );

        employeeRepository.delete(employee);
        employeeRepository.flush();

        if (profileImage != null) {
            fileService.delete(profileImage.getId());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<EmployeeDto> findAll(EmployeeSearchCondition condition) {
        return employeeRepository.search(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeTrendDto> getTrend(EmployeeTrendCondition condition) {
        return employeeRepository.getTrend(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public Long count(EmployeeCountCondition condition) {
        return employeeRepository.countEmployees(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeDistributionDto> getDistribution(EmployeeSearchDistribution searchDistribution) {
        return employeeRepository.distribution(searchDistribution);
    }

    private void validateCreateRequest(EmployeeCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("직원 생성 요청은 필수입니다.");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("직원 이름은 필수입니다.");
        }

        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        if (request.getDepartmentId() == null) {
            throw new IllegalArgumentException("부서 ID는 필수입니다.");
        }

        if (request.getPosition() == null || request.getPosition().isBlank()) {
            throw new IllegalArgumentException("직함은 필수입니다.");
        }

        if (request.getHireDate() == null) {
            throw new IllegalArgumentException("입사일은 필수입니다.");
        }
    }

    private Employee getEmployeeOrThrow(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("직원을 찾을 수 없습니다. id = " + id));
    }

    private Department getDepartmentOrThrow(UUID departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다. id = " + departmentId));
    }

    private String generateEmployeeNumber() {
        String prefix = "EMP-" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        long sequence = employeeRepository.count() + 1;

        String employeeNumber = "%s-%03d".formatted(prefix, sequence);

        while (employeeRepository.existsByEmployeeNumber(employeeNumber)) {
            sequence++;
            employeeNumber = "%s-%03d".formatted(prefix, sequence);
        }

        return employeeNumber;
    }
}

package com.sbproject.weaver.employee.service;

import com.sbproject.weaver.common.dto.CursorPageResponse;
import com.sbproject.weaver.department.entity.Department;
import com.sbproject.weaver.department.repository.DepartmentRepository;
import com.sbproject.weaver.employee.dto.*;
import com.sbproject.weaver.employee.entity.Employee;
import com.sbproject.weaver.employee.entity.EmployeeStatus;
import com.sbproject.weaver.employee.mapper.EmployeeMapper;
import com.sbproject.weaver.employee.repository.EmployeeRepository;
import com.sbproject.weaver.file.entity.FileEntity;
import com.sbproject.weaver.file.service.FileService;
import com.sbproject.weaver.file.type.FilePurpose;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final FileService fileService;

    @Override
    @Transactional
    public EmployeeDto create(EmployeeCreateRequest request, MultipartFile profile) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다."));

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

        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeDto findById(UUID id) {
        Employee employee = getEmployeeOrThrow(id);
        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<EmployeeDto> findAll(EmployeeSearchCondition condition) {
        return employeeRepository.search(condition);
    }

    @Override
    @Transactional
    public EmployeeDto update(UUID id, EmployeeUpdateRequest request, MultipartFile profile) {
        Employee employee = getEmployeeOrThrow(id);

        if (request.getEmail() != null &&
                employeeRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Department department = null;

        if (request.getDepartmentId() != null) {
            department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new NoSuchElementException("부서를 찾을 수 없습니다. id=" + request.getDepartmentId()));
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
            FileEntity oldProfileImage = employee.getProfileImage();
            employee.updateProfileImage(fileService.saveMultipartFile(profile, FilePurpose.PROFILE));

            if (oldProfileImage != null) {
                fileService.delete(oldProfileImage.getId());
            }
        }

        return employeeMapper.toDto(employee);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Employee employee = getEmployeeOrThrow(id);
        FileEntity profileImage = employee.getProfileImage();
        employeeRepository.delete(employee);

        if (profileImage != null) {
            fileService.delete(profileImage.getId());
        }
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

    private String generateEmployeeNumber() {
        int year = LocalDate.now().getYear();
        long sequence = employeeRepository.count() + 1;

        while (employeeRepository.existsByEmployeeNumber(formatEmployeeNumber(year, sequence))) {
            sequence++;
        }

        return formatEmployeeNumber(year, sequence);
    }

    private String formatEmployeeNumber(int year, long sequence) {
        return String.format("EMP-%d-%04d", year, sequence);
    }

    private Employee getEmployeeOrThrow(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("직원을 찾을 수 없습니다. id=" + id));
    }
}

package com.blz.payrolljdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class EmployeePayRollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	private List<EmployeePayRollData> employeePayrollList;
	private static EmployeePayrollDBService employeePayrollDBService;

	public EmployeePayRollService() {
		employeePayrollDBService = EmployeePayrollDBService.getInstance();
	}

	public EmployeePayRollService(List<EmployeePayRollData> employeePayrollList) {
		this();
		this.employeePayrollList = employeePayrollList;
	}

	public List<EmployeePayRollData> readEmployeePayrollData(IOService ioservice) throws EmployeePayrollException {
		if (ioservice.equals(IOService.DB_IO))
			this.employeePayrollList = employeePayrollDBService.readData();
		return this.employeePayrollList;
	}

	public List<EmployeePayRollData> readEmployeePayrollForDateRange(IOService ioService, LocalDate startDate,
			LocalDate endDate) throws EmployeePayrollException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getEmployeeForDateRange(startDate, endDate);
		return null;
	}

	public Map<String, Double> readAverageSalaryByGender(IOService ioService) throws EmployeePayrollException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getAverageSalaryByGender();
		return null;
	}

	public Map<String, Double> readSumOfSalaryByGender(IOService ioService) throws EmployeePayrollException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getSumOfSalaryByGender();
		return null;
	}

	public Map<String, Double> readMinOfSalaryByGender(IOService ioService) throws EmployeePayrollException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getMinOfSalaryByGender();
		return null;
	}

	public Map<String, Double> readMaxOfSalaryByGender(IOService ioService) throws EmployeePayrollException {
		if (ioService.equals(IOService.DB_IO))
			return employeePayrollDBService.getMaxOfSalaryByGender();
		return null;
	}

	public void updateEmployeeSalary(String name, double salary) throws EmployeePayrollException {
		int result = employeePayrollDBService.updateEmployeeData(name, salary);
		if (result == 0)
			return;
		EmployeePayRollData employeePayrollData = this.getEmployeePayrollData(name);
		if (employeePayrollData != null)
			employeePayrollData.salary = salary;
	}

	private EmployeePayRollData getEmployeePayrollData(String name) {
		return this.employeePayrollList.stream()
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.equals(name)).findFirst().orElse(null);
	}

	public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate)
			throws SQLException {
		employeePayrollList.add(employeePayrollDBService.addEmployeePayroll(name, gender, salary, startDate));
	}
	
	public void addEmployeeWithPayrollDetails(String name, String gender, double salary, LocalDate startDate)
			throws SQLException {
		employeePayrollList.add(employeePayrollDBService.addEmployeePayrollInBothTables(name, gender, salary, startDate));
	}
	
	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeePayrollException {
		List<EmployeePayRollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}
}

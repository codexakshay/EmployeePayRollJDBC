package com.blz.payrolljdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayRollService {

	public enum IOService {
		DB_IO, FILE_IO
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
		return employeePayrollList;
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
				.filter(employeePayrollDataItem -> employeePayrollDataItem.name.endsWith(name)).findFirst()
				.orElse(null);
	}

	public void addEmployeeToPayroll(String name, String gender, double salary, LocalDate startDate)
			throws SQLException {
		employeePayrollList.add(employeePayrollDBService.addEmployeePayroll(name, gender, salary, startDate));
	}

	public void addEmployeeWithPayrollDetails(String name, String gender, double salary, LocalDate startDate)
			throws SQLException {
		employeePayrollList
				.add(employeePayrollDBService.addEmployeePayrollInBothTables(name, gender, salary, startDate));
	}

	public void addEmployee(String name, String gender, double salary, LocalDate startDate, String department,
			String company) throws SQLException, EmployeePayrollException {
		employeePayrollList.add(employeePayrollDBService.addEmployeePayrollInAllTables(name, gender, salary, startDate,
				department, company));
	}

	public void deleteEmployeeToPayroll(String name) throws EmployeePayrollException {
		this.employeePayrollList = this.employeePayrollDBService.deleteEmployeeFromDatabase(name);
	}

	public boolean checkEmployeePayrollInSyncWithDB(String name) throws EmployeePayrollException {
		List<EmployeePayRollData> employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
		return employeePayrollDataList.get(0).equals(getEmployeePayrollData(name));
	}

	public void addEmployeePayrollData_MultiThread(List<EmployeePayRollData> employeePayrollList) {
		employeePayrollList.forEach(employeePayrollData -> {
			try {
				employeePayrollDBService.addEmployeePayroll(employeePayrollData.name, employeePayrollData.gender,
						employeePayrollData.salary, employeePayrollData.startDate);

			} catch (SQLException e) {

				e.printStackTrace();
			}

		});
	}

	public long countEnteries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayRollService().countEnteries(ioService);
		return employeePayrollList.size();
	}

	public void addEmployeeToPayRollWIthThreads(List<EmployeePayRollData> employeePayRollList) {
		Map<Integer, Boolean> employeeAditionStatus = new HashMap<Integer, Boolean>();
		employeePayRollList.forEach(employeePayRollData -> {
			Runnable task = () -> {
				employeeAditionStatus.put(employeePayRollData.hashCode(), false);
				System.out.println("Employee Added:" + Thread.currentThread().getName());
				try {
					employeePayrollDBService.addEmployeePayroll(employeePayRollData.name, employeePayRollData.gender,
							employeePayRollData.salary, employeePayRollData.startDate);
				} catch (SQLException e) {
					e.printStackTrace();
				}
				employeeAditionStatus.put(employeePayRollData.hashCode(), true);
				System.out.println("Employee Added: " + Thread.currentThread().getName());

			};
			Thread thread = new Thread(task, employeePayRollData.name);
			thread.start();
		});
		while (employeeAditionStatus.containsValue(false)) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}

	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			new EmployeePayRollService().printData(ioService.DB_IO);
		else
			System.out.println(employeePayrollList);
	}
}

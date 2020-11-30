package com.blz.payrolljdbc;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.blz.payrolljdbc.EmployeePayRollService.IOService;

public class EmployeePayRollServiceTest {

	static EmployeePayRollService employeePayrollService;

	@BeforeClass
	public static void EmpPayRollServiceObj() {
		employeePayrollService = new EmployeePayRollService();
	}

	@Test
	public void givenEmployeePayRollInDB_WhenRetrieved_ShouldMatchEmployeeCount() throws EmployeePayrollException {
		List<EmployeePayRollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(5, employeePayrollData.size());
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() throws EmployeePayrollException {
		employeePayrollService.updateEmployeeSalary("Terisa", 300000.00);
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Terisa");
		Assert.assertTrue(result);
	}

	@Test
	public void givenDateRange_WhenRetrieved_ShouldmatchEmployeeCount() throws EmployeePayrollException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayRollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(5, employeePayrollData.size());
	}

	@Test
	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue()
			throws EmployeePayrollException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				averageSalaryByGender.get("M").equals(300000.00) && averageSalaryByGender.get("F").equals(450000.00));
	}

	@Test
	public void givenPayrollData_WhenSumSalaryRetrievedByGender_ShouldReturnProperValue()
			throws EmployeePayrollException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> sumOfSalaryByGender = employeePayrollService.readSumOfSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				sumOfSalaryByGender.get("M").equals(900000.00) && sumOfSalaryByGender.get("F").equals(900000.00));
	}

	@Test
	public void givenPayrollData_FindMinSalaryRetrievedByGender_ShouldReturnProperValue()
			throws EmployeePayrollException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> minOfSalaryByGender = employeePayrollService.readMinOfSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				minOfSalaryByGender.get("M").equals(100000.00) && minOfSalaryByGender.get("F").equals(300000.00));
	}

	@Test
	public void givenPayrollData_FindMaxSalaryRetrievedByGender_ShouldReturnProperValue()
			throws EmployeePayrollException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> maxOfSalaryByGender = employeePayrollService.readMaxOfSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				maxOfSalaryByGender.get("M").equals(500000.00) && maxOfSalaryByGender.get("F").equals(600000.00));
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws EmployeePayrollException, SQLException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark", "M", 500000.00, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
	}
	
	@Test
	public void givenNewEmployee_WhenAddedToEmployeePayrollAndPayrollDetails_ShouldSyncWithDB() throws EmployeePayrollException, SQLException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeWithPayrollDetails("Lisa", "F", 600000.00, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Lisa");
		Assert.assertTrue(result);
	}
}

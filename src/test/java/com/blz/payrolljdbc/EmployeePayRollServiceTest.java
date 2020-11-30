package com.blz.payrolljdbc;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
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
		Assert.assertEquals(11, employeePayrollData.size());
	}

	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_ShouldMatch() throws EmployeePayrollException {
		employeePayrollService.updateEmployeeSalary("Terisa", 400000.00);
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
		Assert.assertEquals(11, employeePayrollData.size());
	}

	@Test
	public void givenPayrollData_WhenAverageSalaryRetrievedByGender_ShouldReturnProperValue()
			throws EmployeePayrollException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				averageSalaryByGender.get("M").equals(200000.00) && averageSalaryByGender.get("F").equals(300000.00));
	}

	@Test
	public void givenPayrollData_WhenSumSalaryRetrievedByGender_ShouldReturnProperValue()
			throws EmployeePayrollException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> sumOfSalaryByGender = employeePayrollService.readSumOfSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(
				sumOfSalaryByGender.get("M").equals(400000.00) && sumOfSalaryByGender.get("F").equals(300000.00));
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
				maxOfSalaryByGender.get("M").equals(300000.00) && maxOfSalaryByGender.get("F").equals(300000.00));
	}

	@Test
	public void givenNewEmployee_WhenAdded_ShouldSyncWithDB() throws EmployeePayrollException, SQLException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark", "M", 500000.00, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
	}

	@Test
	public void givenNewEmployee_WhenAddedToEmployeePayrollAndPayrollDetails_ShouldSyncWithDB()
			throws EmployeePayrollException, SQLException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeWithPayrollDetails("Lisa", "F", 600000.00, LocalDate.now());
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Lisa");
		Assert.assertTrue(result);
	}

	@Test
	public void givenEmployeePayroll_WhenAddNewRecord_ShouldeReturnUpdatedValue()
			throws EmployeePayrollException, SQLException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployee("Vamsi", "M", 4000000.00, LocalDate.now(), "Marketing", "Infosys");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Vamsi");
		Assert.assertTrue(result);
	}

	@Test
	public void givenEmployeePayroll_WhenDeleteRecord_ShouldeSyncWithDB()
			throws EmployeePayrollException, SQLException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.deleteEmployeeToPayroll("Bill");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Bill");
		Assert.assertTrue(result);
	}

	@Test
	public void given6Employees_WhenAddedDataToDB_ShouldMatchEmployeesEnteries()
			throws EmployeePayrollException, SQLException {
		EmployeePayRollData[] arrayOfEmps = { new EmployeePayRollData(0, "Abhi", "M", 1000000, LocalDate.now()),
				new EmployeePayRollData(0, "Badree", "M", 2000000, LocalDate.now()),
				new EmployeePayRollData(0, "Anjali", "F", 3000000, LocalDate.now()),
				new EmployeePayRollData(0, "Sai", "M", 4000000, LocalDate.now()),
				new EmployeePayRollData(0, "Prasanna", "F", 5000000, LocalDate.now()),
				new EmployeePayRollData(0, "Banti", "M", 6000000, LocalDate.now()), };

		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeePayrollData_MultiThread(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without thread: " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeeToPayRollWIthThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		System.out.println("Duration With Thread : " + Duration.between(threadStart, threadEnd));
		employeePayrollService.printData(IOService.DB_IO);
		Assert.assertEquals(49, employeePayrollService.countEnteries(IOService.DB_IO));
	}
}
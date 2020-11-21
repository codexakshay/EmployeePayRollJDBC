package com.blz.payrolljdbc;

import java.time.LocalDate;
import java.util.List;

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
		Assert.assertEquals(3, employeePayrollData.size());
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
		Assert.assertEquals(2, employeePayrollData.size());
	}
}

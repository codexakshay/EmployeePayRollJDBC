package com.blz.payrolljdbc;

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
	public void givenEmployeePayRollInDB_WhenRetrieved_ShouldMatchEmployeeCount() {
		List<EmployeePayRollData> employeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(3, employeePayrollData.size());
	}
}

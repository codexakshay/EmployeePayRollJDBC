package com.blz.payrolljdbc;

import java.util.List;
import java.util.Scanner;

public class EmployeePayRollService {

	public enum IOService {
		CONSOLE_IO, FILE_IO, DB_IO, REST_IO
	};

	private List<EmployeePayRollData> employeePayrollList;

	public EmployeePayRollService() {}

	public EmployeePayRollService(List<EmployeePayRollData> employeePayrollList) {
		this.employeePayrollList = employeePayrollList;
	}

	private void readEmployeePayrollData(Scanner consoleInputReader) {
		System.out.println("Enter Employee ID: ");
		int id = consoleInputReader.nextInt();
		System.out.println("Enter Employee Name: ");
		String name = consoleInputReader.next();
		System.out.println("Enter Employee Salary: ");
		double salary = consoleInputReader.nextDouble();

		EmployeePayRollData employee = new EmployeePayRollData(id, name, salary);
		employeePayrollList.add(employee);
	}

	public List<EmployeePayRollData> readEmployeePayrollData(IOService ioservice) {
		if(ioservice.equals(IOService.DB_IO))
			this.employeePayrollList = new EmployeePayrollDBService().readData();
		return this.employeePayrollList;
	}

	public void writeEmpPayRollData(IOService ioService) {
		if (ioService.equals(EmployeePayRollService.IOService.CONSOLE_IO))
			System.out.println("Employee Payroll to Details : " + employeePayrollList);
		if (ioService.equals(EmployeePayRollService.IOService.FILE_IO))
			new EmployeePayRollFileIOService().writeData(employeePayrollList);
	}

	public void printData(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			new EmployeePayRollFileIOService().printData();
	}

	public long countEntries(IOService ioService) {
		if (ioService.equals(IOService.FILE_IO))
			return new EmployeePayRollFileIOService().countEntries();
		return 0;
	}

}

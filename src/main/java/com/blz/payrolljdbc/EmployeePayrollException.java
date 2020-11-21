package com.blz.payrolljdbc;

public class EmployeePayrollException extends Exception {

	enum ExceptionType {
		DATABASE_EXCEPTION, NO_SUCH_CLASS
	}

	public ExceptionType type;

	public EmployeePayrollException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}
}

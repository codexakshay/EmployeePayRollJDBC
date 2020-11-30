package com.blz.payrolljdbc;

public class EmployeePayrollException extends Exception {

	enum ExceptionType {
		DATABASE_EXCEPTION, NO_SUCH_CLASS, COMMIT_FAILED, RESOURCES_NOT_CLOSED_EXCEPTION
	}

	public ExceptionType type;

	public EmployeePayrollException(String message, ExceptionType type) {
		super(message);
		this.type = type;
	}
}

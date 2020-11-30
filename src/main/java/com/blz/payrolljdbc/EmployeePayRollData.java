package com.blz.payrolljdbc;

import java.time.LocalDate;

public class EmployeePayRollData {
	public int id;
	public String name;
	public double salary;
	public LocalDate startDate;
	public String department;
	public String company;

	public EmployeePayRollData(int id, String name, double salary) {
		this.id = id;
		this.name = name;
		this.salary = salary;
	}

	public EmployeePayRollData(int id, String name, double salary, LocalDate startDate) {
		this(id, name, salary);
		this.startDate = startDate;
	}

	public EmployeePayRollData(int id, String name, double salary, LocalDate startDate, String department,
			String company) {
		this(id, name, salary, startDate);
		this.department = department;
		this.company = company;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmployeePayRollData other = (EmployeePayRollData) obj;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (Double.doubleToLongBits(salary) != Double.doubleToLongBits(other.salary))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Id: " + id + " Name: " + name + " salary: " + salary + "startDate: " + startDate + "department: "
				+ department + "company: " + company;
	}
}

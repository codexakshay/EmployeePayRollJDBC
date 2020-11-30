package com.blz.payrolljdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeePayrollDBService {

	private static EmployeePayrollDBService employeePayrollDBService;
	private PreparedStatement employeePayrollDataStatement;

	private EmployeePayrollDBService() {
	}

	public static EmployeePayrollDBService getInstance() {
		if (employeePayrollDBService == null)
			employeePayrollDBService = new EmployeePayrollDBService();
		return employeePayrollDBService;
	}

	private Connection getConnection() throws SQLException {
		String jdbcURL = "jdbc:mysql://localhost:3306/payroll_service?useSSL=false";
		String userName = "root";
		String password = "1234";
		Connection connection;
		System.out.println("Connecting to database:" + jdbcURL);
		connection = DriverManager.getConnection(jdbcURL, userName, password);
		System.out.println("Connection is successfull " + connection);
		return connection;
	}

	public List<EmployeePayRollData> readData() throws EmployeePayrollException {
		String sql = "SELECT * FROM employee_payroll; ";
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	public List<EmployeePayRollData> getEmployeePayrollData(String name) throws EmployeePayrollException {
		List<EmployeePayRollData> employeePayrollList = null;
		if (this.employeePayrollDataStatement == null)
			this.prepareStatementForEmployeeData();
		try {
			employeePayrollDataStatement.setString(1, name);
			ResultSet resultSet = employeePayrollDataStatement.executeQuery();
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollList;
	}

	private List<EmployeePayRollData> getEmployeePayrollData(ResultSet resultSet) throws EmployeePayrollException {
		List<EmployeePayRollData> employeePayrollList = new ArrayList<>();
		try {
			while (resultSet.next()) {
				int id = resultSet.getInt("id");
				String name = resultSet.getString("name");
				double salary = resultSet.getDouble("salary");
				LocalDate startDate = resultSet.getDate("start").toLocalDate();
				employeePayrollList.add(new EmployeePayRollData(id, name, salary, startDate));
			}
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollList;
	}

	public List<EmployeePayRollData> getEmployeeForDateRange(LocalDate startDate, LocalDate endDate)
			throws EmployeePayrollException {
		String sql = String.format("SELECT * FROM employee_payroll WHERE start BETWEEN '%s' AND '%s';",
				Date.valueOf(startDate), Date.valueOf(endDate));
		return this.getEmployeePayrollDataUsingDB(sql);
	}

	private List<EmployeePayRollData> getEmployeePayrollDataUsingDB(String sql) throws EmployeePayrollException {
		List<EmployeePayRollData> employeePayrollList = new ArrayList<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			employeePayrollList = this.getEmployeePayrollData(resultSet);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return employeePayrollList;
	}

	private Map<String, Double> getSalaryByGender(String sql) throws EmployeePayrollException {
		Map<String, Double> genderToSalaryMap = new HashMap<>();
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				String gender = resultSet.getString("gender");
				double salary = resultSet.getDouble("salary");
				genderToSalaryMap.put(gender, salary);
			}
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		return genderToSalaryMap;
	}

	public Map<String, Double> getAverageSalaryByGender() throws EmployeePayrollException {
		String sql = "SELECT gender,AVG(salary) as salary FROM employee_payroll GROUP BY gender;";
		return this.getSalaryByGender(sql);
	}

	public Map<String, Double> getSumOfSalaryByGender() throws EmployeePayrollException {
		String sql = "SELECT gender,SUM(salary) as salary FROM employee_payroll GROUP BY gender;";
		return this.getSalaryByGender(sql);
	}

	public Map<String, Double> getMinOfSalaryByGender() throws EmployeePayrollException {
		String sql = "SELECT gender,MIN(salary) as salary FROM employee_payroll GROUP BY gender;";
		return this.getSalaryByGender(sql);
	}

	public Map<String, Double> getMaxOfSalaryByGender() throws EmployeePayrollException {
		String sql = "SELECT gender,MAX(salary) as salary FROM employee_payroll GROUP BY gender;";
		return this.getSalaryByGender(sql);
	}

	private void prepareStatementForEmployeeData() throws EmployeePayrollException {
		try {
			Connection connection = this.getConnection();
			String sql = "SELECT * FROM employee_payroll WHERE name = ?";
			employeePayrollDataStatement = connection.prepareStatement(sql);
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
	}

	public int updateEmployeeData(String name, double salary) throws EmployeePayrollException {
		return this.updateEmployeeDataUsingStatement(name, salary);
	}

	private int updateEmployeeDataUsingStatement(String name, double salary) {
		String sql = String.format("update employee_payroll set salary = %.2f where name = '%s'", salary, name);
		try (Connection connection = this.getConnection();) {
			Statement statement = connection.createStatement();
			return statement.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws EmployeePayrollException {
		try (Connection connection = this.getConnection();) {
			String sql = "update employee_payroll set salary = ? where name = ?";
			PreparedStatement preparedStatement = connection.prepareStatement(sql);
			preparedStatement.setDouble(1, salary);
			preparedStatement.setString(2, name);
			int status = preparedStatement.executeUpdate();
			return status;
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
	}

	public EmployeePayRollData addEmployeePayroll(String name, String gender, double salary, LocalDate startDate)
			throws SQLException {
		int employeeId = -1;
		EmployeePayRollData employeePayrollData = null;
		String sql = String.format(
				"INSERT INTO employee_payroll (name,gender,salary,start)" + " VALUES ('%s','%s','%s','%s')", name,
				gender, salary, Date.valueOf(startDate));
		try (Connection connection = this.getConnection()) {
			Statement statement = connection.createStatement();
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayRollData(employeeId, name, salary, startDate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return employeePayrollData;
	}

	public EmployeePayRollData addEmployeePayrollInBothTables(String name, String gender, double salary,
			LocalDate startDate) throws SQLException {
		int employeeId = -1;
		Connection connection = null;
		EmployeePayRollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try (Statement statement = connection.createStatement()) {
			String sql = String.format(
					"INSERT INTO employee_payroll (name,gender,salary,start)" + " VALUES ('%s','%s','%s','%s')", name,
					gender, salary, Date.valueOf(startDate));
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeId = resultSet.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		}

		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String sql = String.format(
					"INSERT INTO payroll_details(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES (%s,%s,%s,%s,%s,%s)",
					employeeId, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(sql, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayRollData(employeeId, name, salary, startDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			connection.rollback();
		}
		try {
			connection.commit();
		} finally {
			if (connection != null)
				connection.close();
		}
		return employeePayrollData;
	}

	public EmployeePayRollData addEmployeePayrollInAllTables(String name, String gender, double salary, LocalDate start,
			String department, String company) throws EmployeePayrollException {
		int employeeID = -1;
		Connection connection = null;
		EmployeePayRollData employeePayrollData = null;
		try {
			connection = this.getConnection();
			connection.setAutoCommit(false);
		} catch (SQLException sqlException) {
			throw new EmployeePayrollException(sqlException.getMessage(),
					EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
		}
		try (Statement statement = connection.createStatement()) {
			String query = String.format(
					"INSERT INTO employee_payroll (name,gender,salary,start,department,company)"
							+ " VALUES ('%s','%s','%s','%s','%s','%s')",
					name, salary, start, gender, department, company);
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeID = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayRollData(employeeID, name, salary, start, department, company);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException(e1.getMessage(),
						EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
			}
		}
		try (Statement statement = connection.createStatement()) {
			List<String> departmentList = new ArrayList<>(Arrays.asList("sales", "marketing"));
			String query = String.format("insert into department(emp_id,dept_name) values ('%s', '%s')", employeeID,
					department);
			int rowAffected = statement.executeUpdate(query, statement.RETURN_GENERATED_KEYS);
			if (rowAffected == 1) {
				ResultSet resultSet = statement.getGeneratedKeys();
				if (resultSet.next())
					employeeID = resultSet.getInt(1);
			}
			employeePayrollData = new EmployeePayRollData(employeeID, name, salary, start, department, company);
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException(e1.getMessage(),
						EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
			}
		}
		try (Statement statement = connection.createStatement()) {
			double deductions = salary * 0.2;
			double taxablePay = salary - deductions;
			double tax = taxablePay * 0.1;
			double netPay = salary - tax;
			String query = String.format(
					"INSERT INTO payroll_details(employee_id,basic_pay,deductions,taxable_pay,tax,net_pay) VALUES (%s,%s,%s,%s,%s,%s)",
					employeeID, salary, deductions, taxablePay, tax, netPay);
			int rowAffected = statement.executeUpdate(query);
			if (rowAffected == 1) {
				employeePayrollData = new EmployeePayRollData(employeeID, name, salary, start, department, company);
			}
		} catch (SQLException e) {
			try {
				connection.rollback();
			} catch (SQLException e1) {
				throw new EmployeePayrollException(e1.getMessage(),
						EmployeePayrollException.ExceptionType.DATABASE_EXCEPTION);
			}
		}
		try {
			connection.commit();
		} catch (SQLException e) {
			throw new EmployeePayrollException(e.getMessage(), EmployeePayrollException.ExceptionType.COMMIT_FAILED);
		} finally {
			if (connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					throw new EmployeePayrollException(e.getMessage(),
							EmployeePayrollException.ExceptionType.RESOURCES_NOT_CLOSED_EXCEPTION);
				}
		}
		return employeePayrollData;
	}
}

package com.bridgelabz.employeeservice.EmployeePayroll;

import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

//import com.bridgelabz.employeepayroll.EmployeePayrollData;
//import com.bridgelabz.employeepayroll.PayrollServiceException;
import com.bridgelabz.employeeservice.EmployeePayroll.EmployeePayrollService.IOService;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

public class EmployeePayrollServiceTest {
	static EmployeePayrollService employeePayrollService;
	public static boolean finalResult = true;

	@BeforeClass
	public static void initializeConstructor() {
		employeePayrollService = new EmployeePayrollService();
	}

	@Test
	public void printWelcomeMessage() {

		employeePayrollService.printWelcomeMessage();
	}

	@Test
	public void given3EmployeesWhenWrittenToFileShouldMatchEmployeeEntries() {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
				new EmployeePayrollData(2, "Bill Gates", 200000.0),
				new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0) };
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		employeePayrollService.writeEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
		employeePayrollService.printData(EmployeePayrollService.IOService.FILE_IO);
		long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
		Assert.assertEquals(3, entries);
	}

	@Test
	public void givenFileOnReadingFileShouldMatchEmployeeCount() {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		List<EmployeePayrollData> entries = employeePayrollService
				.readPayrollData(EmployeePayrollService.IOService.FILE_IO);

	}

	@Test
	public void givenEmployeePayrollinDB_whenRetrieved_ShouldMatch_Employee_Count() throws PayrollServiceException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
		Assert.assertEquals(1, employeePayrollData.size());
	}

	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_shouldSynchronizewithDataBase() throws PayrollServiceException {
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
		employeePayrollService.updateEmployeeSalary("Bill Gates", 3000000.00,IOService.DB_IO );
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Bill Gates");
		Assert.assertTrue(result);
	}
	

	@Test
	public void givenDateRange_WhenRetrieved_ShouldMatchEmployeeCount() throws PayrollServiceException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		LocalDate startDate = LocalDate.of(2018, 01, 01);
		LocalDate endDate = LocalDate.now();
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readEmployeePayrollForDateRange(IOService.DB_IO, startDate, endDate);
		Assert.assertEquals(1, employeePayrollData.size());
	}

	@Test
	public void givenPayrollData_whenAverageSalaryRetrievedByGender_shouldReturnProperValue()
			throws PayrollServiceException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
		Assert.assertTrue(averageSalaryByGender.get("M").equals(5000000.00));
	}

	@Test
	public void givenNewEmployee_whenAddedShouldSyncWithTheDatabase() throws PayrollServiceException {
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		employeePayrollService.addEmployeeToPayroll("Mark", 5000000.00, LocalDate.now(), "M");
		boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB("Mark");
		Assert.assertTrue(result);
	}

	@Test
	public void givenEmployeeWhenRemoved_ShouldRemainInDatabase() throws PayrollServiceException {
		EmployeePayrollService employeePayrollService = new EmployeePayrollService();
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		int countOfEmployeeRemoved = employeePayrollService.removeEmployeeFromPayroll("Mark", IOService.DB_IO);
		Assert.assertEquals(2, countOfEmployeeRemoved);
		List<EmployeePayrollData> employeePayrollData = employeePayrollService
				.readActiveEmployeePayrollData(IOService.DB_IO);
		Assert.assertEquals(4, employeePayrollData.size());
	}

	@Test
	public void given6Employees_whenAddedToDB_shouldMatchEmployeeEntries() throws PayrollServiceException {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData(0, "Jeff Bezos", 600000.0, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "Bill Gates", 500000.0, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "Mark Zuckerberg", 400000.0, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "Sunder Pichai", 300000.0, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "Mukesh Ambani", 200000.0, LocalDate.now(), "M"),
				new EmployeePayrollData(0, "Anil Ambani", 100000.0, LocalDate.now(), "M") };
		employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.addEmployeesToPayroll(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without Thread; " + Duration.between(start, end));
		Instant threadStart = Instant.now();
		employeePayrollService.addEmployeesToPayrollWithThreads(Arrays.asList(arrayOfEmps));
		Instant threadEnd = Instant.now();
		System.out.println("Duration with Thread; " + Duration.between(threadStart, threadEnd));
		Assert.assertEquals(13, employeePayrollService.countEntries(IOService.DB_IO));
	}
	
	@Test
	public void givenNewSalaryForEmployee_WhenUpdated_MultipleEmployeeSalary_shouldSynchronizewithDataBase() throws PayrollServiceException {
		EmployeePayrollData[] arrayOfEmps = { new EmployeePayrollData("Bill Gates", 9200000.00),
				new EmployeePayrollData("Mukesh Ambani", 3100000.00), 
				new EmployeePayrollData("Mark Zuckerberg", 4000000.00)
				 };
		employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.DB_IO);
		Instant start = Instant.now();
		employeePayrollService.updateEmployeeSalary(Arrays.asList(arrayOfEmps));
		Instant end = Instant.now();
		System.out.println("Duration without thread: " + Duration.between(start, end));
		List<EmployeePayrollData> employeePayrollDataList = Arrays.asList(arrayOfEmps);
		employeePayrollDataList.forEach(employeePayrollData -> {
			Runnable task = () -> {
				boolean result = employeePayrollService.checkEmployeePayrollInSyncWithDB(employeePayrollData.name);
				finalResult ^= result;
			};
			Thread thread = new Thread(task, employeePayrollData.name);
			thread.start();
		});
		Assert.assertTrue(finalResult);
	}
}
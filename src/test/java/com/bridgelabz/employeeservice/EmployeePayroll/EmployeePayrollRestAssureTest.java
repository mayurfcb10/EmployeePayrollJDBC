package com.bridgelabz.employeeservice.EmployeePayroll;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.bridgelabz.employeeservice.EmployeePayroll.EmployeePayrollService.IOService;

public class EmployeePayrollRestAssureTest {

	static EmployeePayrollService employeePayrollService;
	
	@Before
	public void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = 3000;
	}

	@BeforeClass
	public static void initializeConstructor() {
		employeePayrollService = new EmployeePayrollService();
	}
	
	/* get Employee Payroll List Employee Payroll Json */
	public EmployeePayrollData[] getEmployeeList(){
        Response response = RestAssured.get("/employees");
        System.out.println("Employee Payroll entires in JSON Server: \n"+response.asString());
        EmployeePayrollData[] arrayOfEmps = new Gson().fromJson(response.asString(),EmployeePayrollData[].class);
        return arrayOfEmps;
    }

    @Test
    public void givenEmployeeDataInJSONServer_whenRetrieved_shouldMatchTheCount(){
        EmployeePayrollData[] arrayOfEmps = getEmployeeList();
        employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
        Assert.assertEquals(2,entries);
    }
    
    /* Add  Employees in Employee Payroll Json */
    @Test
    public void givenNeEmployeeWhenAddedShouldMatch201ResponseAndCount() throws PayrollServiceException {
    	EmployeePayrollData[] arrayOfEmps = getEmployeeList();
    	employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
    	EmployeePayrollData employeePayrollData =null;
    	employeePayrollData = new EmployeePayrollData(0, "Mukesh Ambani", 8000000.00, LocalDate.now(),"M");
    	Response response = addEmployeeToJsonServer(employeePayrollData);
    	int statusCode = response.getStatusCode();
    	Assert.assertEquals(201, statusCode);
    	employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
    	employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
    	  long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
    	  System.out.println("Employee Payroll entires in JSON Server: \n"+response.asString());
          Assert.assertEquals(3,entries);
    }

	private Response addEmployeeToJsonServer(EmployeePayrollData employeePayrollData) {
		String employeeJson = new Gson().toJson(employeePayrollData);
		RequestSpecification request = RestAssured.given();
		request.header("Content-Type", "application/json");
		request.body(employeeJson);
		return request.post("/employees");
	}
	
	/* Added Muliple Employees in Employee Payroll Json */
	@Test
	public void givenEmployeeListWhenAdded_shouldMatch201andCount() throws PayrollServiceException {
		EmployeePayrollData[] arrayOfEmps = getEmployeeList();
		employeePayrollService = new EmployeePayrollService(Arrays.asList(arrayOfEmps));
		
		EmployeePayrollData[]  arrayOfEmpPayRolls = {
				new EmployeePayrollData(0, "Sundar Pichai", 9000000.00, LocalDate.now(),"M"),
				new EmployeePayrollData(0, "Anil Ambani", 5000000.00, LocalDate.now(),"M"),
				new EmployeePayrollData(0, "Warren Buffet", 10000000.00, LocalDate.now(),"M"),
				new EmployeePayrollData(0, "Tim Cook", 12000000.00, LocalDate.now(),"M")
		};
		for(EmployeePayrollData employeePayrollData : arrayOfEmpPayRolls) {
			Response response = addEmployeeToJsonServer(employeePayrollData);
	    	int statusCode = response.getStatusCode();
	    	Assert.assertEquals(201, statusCode);
	    	employeePayrollData = new Gson().fromJson(response.asString(), EmployeePayrollData.class);
	    	employeePayrollService.addEmployeeToPayroll(employeePayrollData, IOService.REST_IO);
		}
		 long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.REST_IO);
         Assert.assertEquals(7,entries);
		
	}
	

	
}








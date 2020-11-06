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
}








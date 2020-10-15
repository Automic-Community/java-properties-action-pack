package com.uc4.ara.feature.properties;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UpdateCreatePropertyTest {
	UpdateCreateProperty test;

	@Before
	public void setUp() throws Exception {
		test = new UpdateCreateProperty();
		test.initialize();
	}

	@After
	public void tearDown() throws Exception {
	}

	public void testRun() throws Exception {
		String file = "C:/Temp/em-connection.properties";
		String variable = "DbReportServerDir2";
		String value = "\\\\bais-entw-db.services.debeka.de\\bais";
		String[] args = new String[] {
				"-prop", file,
				"-var", variable,
				"-v", value
		};
		int retCode = test.run(args);
		assertEquals(0, retCode);
		
		Properties props = new Properties();
		props.load(new FileInputStream("C:/Temp/em-connection.properties"));
		System.out.println(props.getProperty(variable));
	}

}

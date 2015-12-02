package com.paradigma;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.paradigma.arquitecture.ArquitectureConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ArquitectureConfig.class)
@ActiveProfiles("test")
public class ArquitectureApplicationTests {

	@Test
	public void contextLoads() {
	}

}

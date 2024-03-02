package com.artsemrogovenko.diplom.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestStorageApplication {

	public static void main(String[] args) {
		SpringApplication.from(StorageApplication::main).with(TestStorageApplication.class).run(args);
	}

}

package com.sbaldasso.combobackend;

import org.springframework.boot.SpringApplication;

public class TestCombobackendApplication {

	public static void main(String[] args) {
		SpringApplication.from(CombobackendApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}

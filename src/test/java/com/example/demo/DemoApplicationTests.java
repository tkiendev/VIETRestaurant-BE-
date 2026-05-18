package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.corundumstudio.socketio.SocketIOServer;

@SpringBootTest
class DemoApplicationTests {

	@MockBean
	private SocketIOServer socketIOServer;

	@Test
	void contextLoads() {
	}

}

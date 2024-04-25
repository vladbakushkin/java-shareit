package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.user.controller.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ShareItTests {

	@Autowired
	private UserController userController;

	@Autowired
	private ItemController itemController;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
		assertThat(itemController).isNotNull();
	}

}

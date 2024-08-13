package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepoMock = mock(OrderRepository.class);
    private UserRepository userRepoMock = mock(UserRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        FieldInjector.injectObjects(orderController, "userRepository", userRepoMock);
        FieldInjector.injectObjects(orderController, "orderRepository", orderRepoMock);
    }

    @Test
    public void testGetOrdersByUsername_Success() {
        User testUser = createUser(1L, "testUser");
        when(userRepoMock.findByUsername(testUser.getUsername())).thenReturn(testUser);

        UserOrder testOrder = new UserOrder();
        when(orderRepoMock.findById(any(Long.class))).thenReturn(Optional.of(testOrder));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("testUser");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetOrdersByUsername_UserNotFound() {
        when(userRepoMock.findByUsername("nonexistentUser")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("nonexistentUser");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testSubmitOrder_UserNotFound() {
        when(userRepoMock.findByUsername("nonexistentUser")).thenReturn(null);

        ResponseEntity<?> response = orderController.submit("nonexistentUser");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testSubmitOrder_Success() {
        User testUser = createUser(1L, "testUser");
        when(userRepoMock.findByUsername(testUser.getUsername())).thenReturn(testUser);

        Item testItem = createItem();
        List<Item> items = new ArrayList<>();
        items.add(testItem);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setTotal(new BigDecimal("5.00"));
        cart.setItems(items);
        testUser.setCart(cart);
        when(userRepoMock.findByUsername(testUser.getUsername())).thenReturn(testUser);

        ResponseEntity<?> response = orderController.submit("testUser");

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    private User createUser(long id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setPassword("password");
        user.setCart(new Cart());
        return user;
    }

    private Item createItem() {
        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal("5.00"));
        item.setDescription("Sample Item");
        return item;
    }
}

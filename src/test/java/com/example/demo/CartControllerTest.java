package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private ItemRepository itemRepoMock = mock(ItemRepository.class);
    private UserRepository userRepoMock = mock(UserRepository.class);
    private CartRepository cartRepoMock = mock(CartRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        FieldInjector.injectObjects(cartController, "userRepository", userRepoMock);
        FieldInjector.injectObjects(cartController, "cartRepository", cartRepoMock);
        FieldInjector.injectObjects(cartController, "itemRepository", itemRepoMock);
    }

    @Test
    public void testAddItemToCart_Success() {
        User testUser = createUser(1L, "testUser");
        when(userRepoMock.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(cartRepoMock.save(any(Cart.class))).thenReturn(new Cart());
        Optional<Item> itemOptional = Optional.of(createItem());
        itemOptional.get().setPrice(new BigDecimal("11.00"));
        when(itemRepoMock.findById(any(Long.class))).thenReturn(itemOptional);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(1);
        request.setUsername(testUser.getUsername());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();
        Assertions.assertNotNull(cart);
        Assertions.assertEquals(11, cart.getTotal().intValue());
    }

    @Test
    public void testAddItemToCart_UserNotFound() {
        when(userRepoMock.findByUsername("nonexistentUser")).thenReturn(null);

        Optional<Item> itemOptional = Optional.of(createItem());
        itemOptional.get().setPrice(new BigDecimal("8.00"));
        when(itemRepoMock.findById(any(Long.class))).thenReturn(itemOptional);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(2);
        request.setUsername("nonexistentUser");

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddItemToCart_ItemNotFound() {
        User testUser = createUser(1L, "testUser");
        when(userRepoMock.findByUsername(testUser.getUsername())).thenReturn(testUser);

        when(itemRepoMock.findById(2L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(2);
        request.setQuantity(2);
        request.setUsername(testUser.getUsername());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveItemFromCart_UserNotFound() {
        when(userRepoMock.findByUsername("nonexistentUser")).thenReturn(null);

        Optional<Item> itemOptional = Optional.of(createItem());
        itemOptional.get().setPrice(new BigDecimal("8.00"));
        when(itemRepoMock.findById(any(Long.class))).thenReturn(itemOptional);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(1);
        request.setQuantity(2);
        request.setUsername("nonexistentUser");

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveItemFromCart_ItemNotFound() {
        User testUser = createUser(1L, "testUser");
        when(userRepoMock.findByUsername(testUser.getUsername())).thenReturn(testUser);

        when(itemRepoMock.findById(2L)).thenReturn(Optional.empty());

        ModifyCartRequest request = new ModifyCartRequest();
        request.setItemId(2);
        request.setQuantity(2);
        request.setUsername(testUser.getUsername());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
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
        item.setDescription("Test Item");
        return item;
    }
}

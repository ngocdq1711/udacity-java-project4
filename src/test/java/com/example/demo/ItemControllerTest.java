package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController controllerUnderTest;
    private ItemRepository itemRepoMock = mock(ItemRepository.class);

    @Before
    public void setUp() {
        controllerUnderTest = new ItemController();
        FieldInjector.injectObjects(controllerUnderTest, "itemRepository", itemRepoMock);
    }

    @Test
    public void testGetAllItems() {
        ResponseEntity<List<Item>> response = controllerUnderTest.getItems();
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemsByName_Found() {
        List<Item> items = new ArrayList<>();
        items.add(new Item());
        when(itemRepoMock.findByName("Round Widget")).thenReturn(items);

        ResponseEntity<List<Item>> response = controllerUnderTest.getItemsByName("Round Widget");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
        Assertions.assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetItemsByName_NotFound() {
        List<Item> items = new ArrayList<>();
        when(itemRepoMock.findByName("Nonexistent Widget")).thenReturn(items);

        ResponseEntity<List<Item>> response = controllerUnderTest.getItemsByName("Nonexistent Widget");
        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemById_Found() {
        Item item = new Item();
        item.setId(1L);
        when(itemRepoMock.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = controllerUnderTest.getItemById(1L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testGetItemById_NotFound() {
        when(itemRepoMock.findById(2L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = controllerUnderTest.getItemById(2L);
        Assertions.assertNotNull(response);
        Assertions.assertEquals(404, response.getStatusCodeValue());
    }
}

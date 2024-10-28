package ru.tbank.springapp.service.utils;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import ru.tbank.springapp.client.KudagoClient;
import ru.tbank.springapp.dao.Repository;
import ru.tbank.springapp.integration.AbstractIntegrationTest;
import ru.tbank.springapp.model.Category;
import ru.tbank.springapp.model.City;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test-with-events")
class DataFetcherTest extends AbstractIntegrationTest {

    @MockBean
    private KudagoClient client;

    @MockBean
    private Repository<String, Category> categoryRepository;

    @MockBean
    private Repository<String, City> cityRepository;

    //    @Test
    void testDataFetchingOnApplicationReadyEvent() {
        verify(client, times(1)).getCategories();
        verify(client, times(1)).getCities();
    }

}
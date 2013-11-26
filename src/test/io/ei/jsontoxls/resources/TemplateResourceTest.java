package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.repository.TemplateRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class TemplateResourceTest {
    @Mock
    private TemplateRepository repository;
    private TemplateResource resource;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        resource = new TemplateResource(repository);
    }

    @Test
    public void shouldSaveTemplate() throws Exception {
        byte[] templateData = new byte[]{1};

        Response response = resource.save(templateData);

        assertEquals(201, response.getStatus());
        verify(repository).add(anyString(), eq(templateData));
    }

    @Test
    public void shouldReturnBadRequestWhenTemplateDataIsEmpty() throws Exception {
        byte[] templateData = new byte[]{};

        Response response = resource.save(templateData);

        assertEquals(400, response.getStatus());
        assertEquals("Template cannot be empty.", response.getEntity());
        verifyZeroInteractions(repository);
    }

    @Test
    public void shouldReturnInternalServerErrorWhenThereIsAnException() throws Exception {
        byte[] templateData = new byte[]{1};
        doThrow(new RuntimeException()).when(repository).add(anyString(), eq(templateData));

        Response response = resource.save(templateData);

        assertEquals(500, response.getStatus());
        assertEquals("Unable to save template due to internal error.", response.getEntity());
    }
}

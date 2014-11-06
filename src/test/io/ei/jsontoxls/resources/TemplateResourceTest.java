package io.ei.jsontoxls.resources;

import java.util.Map;

import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.util.ExcelUtils;

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
    @Mock
    private ExcelUtils excelUtil;

    private TemplateResource resource;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        resource = new TemplateResource(repository, excelUtil);
    }

    @Test
    public void shouldSaveTemplate() throws Exception {
        byte[] templateData = new byte[]{1};
        when(excelUtil.isExcel(templateData)).thenReturn(true);

        Response response = resource.save(templateData);

        assertEquals(201, response.getStatus());
        verify(repository).add(anyString(), eq(templateData));
    }

    @Test
    public void shouldReturnInternalServerErrorWhenThereIsAnExceptionWhileSaving() throws Exception {
        byte[] templateData = new byte[]{1};
        when(excelUtil.isExcel(templateData)).thenReturn(true);
        doThrow(new RuntimeException()).when(repository).add(anyString(), eq(templateData));

        Response response = resource.save(templateData);

        assertEquals(500, response.getStatus());
        assertEquals("Unable to save template due to internal error.", response.getEntity());
    }

    @Test
    public void shouldReturnBadRequestWhenTemplateDataIsNotAValidExcel() throws Exception {
        byte[] invalidTemplateData = {1, 2};
        when(excelUtil.isExcel(invalidTemplateData)).thenReturn(false);

        Response response = resource.save(invalidTemplateData);

        assertEquals(400, response.getStatus());
        assertEquals("Template is not a valid Excel.", response.getEntity());
        verifyZeroInteractions(repository);
    }

    @Test
    public void shouldUpdateTemplate() throws Exception {
        byte[] templateData = new byte[]{1};
        byte[] existingTemplateData = new byte[]{1};
        String token = "Token";
        when(excelUtil.isExcel(templateData)).thenReturn(true);
        when(repository.findByToken(token)).thenReturn(existingTemplateData);

        Response response = resource.update(token, templateData);

        assertEquals(200, response.getStatus());
        verify(repository).update(token, templateData);
    }

    @Test
    public void shouldReturnInternalServerErrorWhenThereIsAnExceptionWhileUpdating() throws Exception {
        byte[] templateData = new byte[]{1};
        byte[] existingTemplateData = new byte[]{1};
        String token = "Token";
        when(excelUtil.isExcel(templateData)).thenReturn(true);
        when(repository.findByToken(token)).thenReturn(existingTemplateData);
        doThrow(new RuntimeException()).when(repository).update(token, templateData);

        Response response = resource.update(token, templateData);

        assertEquals(500, response.getStatus());
        assertEquals("Unable to update template due to internal error.", response.getEntity());
    }

    @Test
    public void shouldReturnNotFoundWhenThereIsNoTemplateToUpdateMatchingTheToken() throws Exception {
        byte[] templateData = new byte[]{1};
        String token = "Token";
        when(repository.findByToken("token")).thenReturn(null);
        when(excelUtil.isExcel(templateData)).thenReturn(true);

        Response response = resource.update(token, templateData);

        assertEquals(404, response.getStatus());
        assertEquals("Could not find a valid template for the given token. Token: Token.", response.getEntity());
    }

    @Test
    public void shouldGetTemplateForAGivenToken() {
        byte[] templateData = new byte[]{1};
        String token = "Token";
        when(repository.findByToken(token)).thenReturn(templateData);

        Response response = resource.get(token);

        assertEquals(200, response.getStatus());
        Map metadata = response.getMetadata();
        assertEquals(2, metadata.size());
        verify(repository).findByToken(token);
    }

    @Test
    public void shouldReturnNotFoundWhenThereIsNoTemplateMatchingTheToken() throws Exception {
        String token = "Token";
        when(repository.findByToken(token)).thenReturn(null);

        Response response = resource.get(token);

        assertEquals(404, response.getStatus());
        assertEquals("Could not find a valid template for the given token. Token: Token.", response.getEntity());
    }
}

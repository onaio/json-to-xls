package io.ei.jsontoxls.resources;

import java.util.Map;

import io.ei.jsontoxls.repository.ExcelRepository;
import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.JsonPojoConverter;
import io.ei.jsontoxls.util.ObjectDeserializer;
import io.ei.jsontoxls.util.PackageUtils;

import org.codehaus.jackson.JsonParseException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.ws.rs.core.Response;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class XlsResourceTest {

    private XlsResource xlsResource;

    @Mock
    private ObjectDeserializer objectDeserializer;
    @Mock
    private PackageUtils packageUtil;
    @Mock
    private JsonPojoConverter converter;
    @Mock
    private ExcelUtils excelUtil;
    @Mock
    private TemplateRepository templateRepository;
    @Mock
    private ExcelRepository excelRepository;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        xlsResource = new XlsResource(converter, objectDeserializer, packageUtil, excelUtil,
                templateRepository, excelRepository);
    }

    @Test
    public void shouldGenerateXLSUsingTokenAndJSONData() throws Exception {
        String dataJson = "{\n" +
                "    \"loc\": {\n" +
                "        \"state\": \"Karnataka\",\n" +
                "        \"district\": \"Mysore\"\n" +
                "    },\n" +
                "    \"ind\": {\n" +
                "        \"anc\": \"1\",\n" +
                "        \"anc_12\": \"2\",\n" +
                "        \"anc_jsy\": \"3\"\n" +
                "    }\n" +
                "}";
        byte[] template = new byte[]{};
        byte[] generatedExcel = new byte[]{};
        when(templateRepository.findByToken("template_token")).thenReturn(template);
        when(converter.generateJavaClasses(dataJson)).thenReturn("generated-package-name");
        when(objectDeserializer.makeJsonObject("generated-package-name", dataJson)).thenReturn(new Object());
        when(excelUtil.generateExcel(anyMap(), eq(template)))
                .thenReturn(generatedExcel);

        Response response = xlsResource.generateExcelFromTemplate("template_token", dataJson);

        assertEquals(response.getStatus(), 201);
        verify(excelUtil).generateExcel(anyMap(), eq(template));
        verify(excelRepository).add(anyString(), eq("template_token"), eq(generatedExcel));
        verify(packageUtil).cleanup("generated-package-name");
    }

    @Test
    public void shouldReturnErrorWhenNoTemplateExistsForGivenToken() throws Exception {
        String dataJson = "{\n" +
                "    \"loc\": {\n" +
                "        \"state\": \"Karnataka\",\n" +
                "        \"district\": \"Mysore\"\n" +
                "    },\n" +
                "    \"ind\": {\n" +
                "        \"anc\": \"1\",\n" +
                "        \"anc_12\": \"2\",\n" +
                "        \"anc_jsy\": \"3\"\n" +
                "    }\n" +
                "}";
        when(templateRepository.findByToken("token")).thenReturn(null);

        Response response = xlsResource.generateExcelFromTemplate("token", dataJson);

        assertEquals(response.getStatus(), 404);
        assertEquals(response.getEntity(), "Could not find a valid template for the given token. Token: token.");
        verifyZeroInteractions(excelUtil);
        verifyZeroInteractions(converter);
        verifyZeroInteractions(objectDeserializer);
    }

    @Test
    public void shouldReturnErrorWhenEmptyJSONIsPosted() throws Exception {
        when(templateRepository.findByToken("token")).thenReturn(new byte[]{});

        Response response = xlsResource.generateExcelFromTemplate("token", "");

        assertEquals(400, response.getStatus());
        assertEquals("JSON data cannot be empty.", response.getEntity());
        verifyZeroInteractions(excelUtil);
        verifyZeroInteractions(converter);
        verifyZeroInteractions(objectDeserializer);
    }

    @Test
    public void shouldReturnMalformedJSONErrorWhenInvalidJSONIsPosted() throws Exception {
        String dataJson = "invalid JSON {";
        when(templateRepository.findByToken("token")).thenReturn(new byte[]{1});
        when(converter.generateJavaClasses(dataJson)).thenThrow(JsonParseException.class);

        Response response = xlsResource.generateExcelFromTemplate("token", dataJson);

        assertEquals(400, response.getStatus());
        assertEquals("JSON is not valid.", response.getEntity());
        verify(packageUtil).cleanup("");
        verifyZeroInteractions(excelUtil);
    }

    @Test
    public void shouldReturnXLSForGivenToken() throws Exception {
        byte[] expectedGeneratedExcel = new byte[]{};
        when(excelRepository.findByToken("token")).thenReturn(expectedGeneratedExcel);

        Response response = xlsResource.get("token");

        assertEquals(200, response.getStatus());
        Map metadata = response.getMetadata();
        assertEquals(2, metadata.size());
        verify(excelRepository).findByToken("token");
    }

    @Test
    public void shouldReturn404WhenThereIsNoExcelForGivenToken() throws Exception {
        when(excelRepository.findByToken("token")).thenReturn(null);

        Response response = xlsResource.get("token");

        assertEquals(404, response.getStatus());
        assertEquals("Could not find a valid excel for the given token. Token: token.", response.getEntity());
    }

    @Test
    public void shouldGenerateXLSUsingTokenAndJSONDataWhichHasJsonArrayAsRootNode() throws Exception {
        String dataJson = "[{\n" +
                "    \"loc\": {\n" +
                "        \"state\": \"Karnataka\",\n" +
                "        \"district\": \"Mysore\"\n" +
                "    },\n" +
                "    \"ind\": {\n" +
                "        \"anc\": \"1\",\n" +
                "        \"anc_12\": \"2\",\n" +
                "        \"anc_jsy\": \"3\"\n" +
                "    }\n" +
                "}, {\n" +
                "    \"loc\": {\n" +
                "        \"state\": \"Karnataka1\",\n" +
                "        \"district\": \"Mysore2\"\n" +
                "    },\n" +
                "    \"ind\": {\n" +
                "        \"anc\": \"4\",\n" +
                "        \"anc_12\": \"5\",\n" +
                "        \"anc_jsy\": \"6\"\n" +
                "    }\n" +
                "}]";

        byte[] template = new byte[]{};
        byte[] generatedExcel = new byte[]{};
        when(templateRepository.findByToken("template_token")).thenReturn(template);
        when(converter.generateJavaClasses(dataJson)).thenReturn("generated-package-name");
        when(objectDeserializer.makeJsonList("generated-package-name", dataJson)).thenReturn(new Object());
        when(excelUtil.generateExcel(anyMap(), eq(template)))
                .thenReturn(generatedExcel);

        Response response = xlsResource.generateExcelFromTemplate("template_token", dataJson);

        assertEquals(response.getStatus(), 201);
        verify(excelUtil).generateExcel(anyMap(), eq(template));
        verify(excelRepository).add(anyString(), eq("template_token"), eq(generatedExcel));
    }
}

